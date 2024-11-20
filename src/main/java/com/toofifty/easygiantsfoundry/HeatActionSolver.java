package com.toofifty.easygiantsfoundry;

//import java.util.ArrayList;
//import java.util.List;

import com.toofifty.easygiantsfoundry.enums.Stage;
import lombok.Value;

/**
 * Solves the heating/cooling action and predicts tick duration (index)
 * the naming convention is focused on the algorithm rather than in-game terminology for the context.
 * <p>
 * the dx_n refers to successive derivatives of an ordinary-differential-equations
 * https://en.wikipedia.org/wiki/Ordinary_differential_equation
 * also known as position (dx0), speed (dx1), and acceleration (dx2).
 * <p>
 * dx0 - players current heat at tick
 * dx1 - dx0_current - dx0_last_tick, aka the first derivative
 * dx2 - dx1_current - dx1_last_tick, aka the second derivative
 * <p>
 * for context, here's what dx1 extracted directly from in-game dunking looks like.
 * the purpose of the HeatActionSolver.java is to accurately model this data.
 * int[] dx1 = {
 * 7,
 * 8,
 * 9,
 * 11,
 * 13,
 * 15,
 * 17,
 * 19,
 * 21,
 * 24,
 * 27, -- dunk/quench starts here
 * 30,
 * 33,
 * 37,
 * 41,
 * 45,
 * 49,
 * 53,
 * 57,
 * 62,
 * 67,
 * 72,
 * 77,
 * 83,
 * 89,
 * 95,
 * 91,
 * };
 */

/* The following code-snippet can be copy-pasted into runelite developer-tools "Shell" to extract dx1

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

import java.util.concurrent.atomic.AtomicInteger;

int HEAT_ID = 13948;

AtomicInteger tickCounter = new AtomicInteger(-1);
AtomicInteger prevHeat = new AtomicInteger(client.getVarbitValue(HEAT_ID));


Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

String output = "";

subscribe(VarbitChanged.class, ev ->
{
	if (ev.getVarbitId() == HEAT_ID)
	{
		int deltaHeat = ev.getValue() - prevHeat.getAndSet(ev.getValue());

		if (deltaHeat == -1) return; // ignore passive drain

		String str = "[" + tickCounter.incrementAndGet()
			+ "] deltaHeat: " + deltaHeat;
		log.info(str);
		output = output + deltaHeat + "\n";
		StringSelection selection = new StringSelection(output);
		clipboard.setContents(selection, selection);
	}
});
*/

public class HeatActionSolver
{

	public static final int[] DX_1 = new int[]{
		7,
		8,
		9,
		11,
		13,
		15,
		17,
		19,
		21,
		24,
		27, // -- dunk/quench starts here
		30,
		33,
		37,
		41,
		45,
		49,
		53,
		57,
		62,
		67,
		72,
		77,
		83,
		89,
		95,
		91, // last one will always overshoot 1000
	};
	public static final int MAX_INDEX = DX_1.length;
	public static final int FAST_INDEX = 10;

	@Value(staticConstructor = "of")
	public static class SolveResult
	{
		int index;
		int dx0;
		int dx1;
		int dx2;
	}

	private static SolveResult heatingSolve(int start, int goal, boolean overshoot, int max, boolean isFast)
	{
		return relativeSolve(goal - start, overshoot, max - start, isFast, -1);
	}

	private static SolveResult coolingSolve(int start, int goal, boolean overshoot, int min, boolean isFast)
	{
		return relativeSolve(start - goal, overshoot, start - min, isFast, 1);
	}

	private static SolveResult relativeSolve(int goal, boolean overshoot, int max, boolean isFast, int decayValue)
	{

		int index = isFast ? FAST_INDEX : 0;
		int dx0 = 0;

		boolean decay = false;

		while (true) {

			if (index > MAX_INDEX)
			{
				break;
			}

			if (!overshoot && dx0 + DX_1[index] > goal)
			{
				break;
			}
			else if (overshoot && dx0 >= goal)
			{
				break;
			}

			if (dx0 + DX_1[index] >= max)
			{
				break;
			}

			if (decay)
			{
				dx0 -= decayValue;
			}


			dx0 += DX_1[index];
			++index;
			decay = !decay;
		}

		if (isFast)
		{
			index -= FAST_INDEX;
		}

		return SolveResult.of(index, dx0, DX_1[index], -1);
	}


	@Value(staticConstructor = "of")
	public static class DurationResult
	{
		int duration;
		boolean goalInRange;
		boolean overshooting;
		int predictedHeat;
	}

	public static DurationResult solve(
		Stage stage,
		int[] range,
		int actionLeftInStage,
		int start,
		boolean isFast,
		boolean isActionHeating,
		int padding)
	{

		final boolean isStageHeating = stage.isHeating();

		// adding 2.4s/8ticks worth of padding so preform doesn't decay out of range
		// average distance from lava+waterfall around 8 ticks
		// preform decays 1 heat every 2 ticks
		final int min = Math.max(0, Math.min(1000, range[0] + padding));
		final int max = Math.max(0, Math.min(1000, range[1] + padding));

		final int actionsLeft_DeltaHeat = actionLeftInStage * stage.getHeatChange();

		int estimatedDuration = 0;

		final boolean goalInRange;
		boolean overshoot = false;

		SolveResult result = null;

		// case actions are all cooling, heating is mirrored version

		// goal: in-range // stage: heating
		// overshoot goal
		//  <----------|stop|<---------------- heat
		// ------|min|----------goal-------|max|
		//                      stage ---------------->

		// goal: out-range // stage: heating
		// undershoot min
		// ...----------|stop|<--------------------- heat
		// -goal---|min|---------------------|max|
		//                      stage ----------------->

		// goal: in-range // stage: cooling
		// undershoot goal
		//   <-------------------------|stop|<--------------- heat
		// ------|min|----------goal-------|max|
		//    <---------------- stage

		// goal: out-range // stage: cooling
		// overshoot max
		//    <--------------------|stop|<--------------- heat
		// --------|min|---------------------|max|----goal
		//    <---------------- stage

		if (isActionHeating)
		{
			int goal = min - actionsLeft_DeltaHeat;
			goalInRange = goal >= min && goal <= max;

			if (isStageHeating)
			{

				if (start <= max)
				{
					overshoot = !goalInRange;

					if (!goalInRange)
					{
						goal = min;
					}

					result = heatingSolve(start, goal, overshoot, max, isFast);

					estimatedDuration = result.index;
				}
			}
			else // cooling stage
			{
				// actionsLeft_DeltaHeat is negative here
				if (start <= max)
				{
					overshoot = goalInRange;

					if (!goalInRange)
					{
						goal = max;
					}

					result = heatingSolve(start, goal, overshoot, max, isFast);

					estimatedDuration = result.index;
				}
			}
		}
		else // cooling action
		{
			int goal = max - actionsLeft_DeltaHeat;
			goalInRange = goal >= min && goal <= max;

			if (isStageHeating)
			{
				if (start >= min)
				{
					overshoot = goalInRange;

					if (!goalInRange)
					{
						goal = min;
					}

					result = coolingSolve(start, goal, overshoot, min, isFast);

					estimatedDuration = result.index;
				}
			}
			else // cooling stage cooling action
			{
				if (start >= min)
				{
					overshoot = !goalInRange;
					if (!goalInRange)
					{
						goal = max;
					}

					result = coolingSolve(start, goal, overshoot, min, isFast);

					estimatedDuration = result.index;
				}
			}
		}

		int dx0 = result == null ? 0 : result.dx0;
		if (!isActionHeating)
		{
			dx0 *= -1;
		}


		return DurationResult.of(estimatedDuration, goalInRange, overshoot, start + dx0);
	}

}

