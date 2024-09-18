package com.toofifty.easygiantsfoundry;

//import java.util.ArrayList;
//import java.util.List;

/**
 * Solves the heating/cooling action and predicts tick duration (index)
 * the naming convention is focused on the algorithm rather than in-game terminology for the context.
 * <p>
 * the dx_n refers to successive derivatives of an ordinary-differential-equations
 * https://en.wikipedia.org/wiki/Ordinary_differential_equation
 * also known as distance (dx0), speed (dx1), and acceleration (dx2).
 * <p>
 * dx0 - players current heat at tick
 * dx1 - dx0_current - dx0_last_tick, aka the first derivative
 * dx2 - dx1_current - dx1_last_tick, aka the second derivative
 * <p>
 * for context, here's what dx1 extracted directly from in-game dunking looks like.
 * the purpose of the HeatActionSolver.java is to accurately model this data.
 * int[] dx1 = {
 * 27,
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

	/**
	 * @param goal       the desired heat destination
	 * @param init_dx1   initial speed of heating/cooling. currently 7 for heat/cool, 27 for dunk/quench.
	 * @param dx2_offset bonus acceleration. currently, 0 for heat/cool, 2 for dunk/quench.
	 * @return Index here refers to tick. So an index of 10 means the goal can be reached in 10 ticks.
	 */
	public static int findDx0Index(int goal, int init_dx1, int dx2_offset)
	{
		int dx0 = 0;
		int dx1 = init_dx1;
		int count_index = 0;
		for (int dx2 = 1; dx0 <= goal; dx2++)
		{  // Start from 1 up to the count inclusive
			int repetitions;
			if (dx2 == 1)
			{
				repetitions = 2;  // The first number appears twice
			}
			else if (dx2 % 2 == 0)
			{
				repetitions = 6;  // Even numbers appear six times
			}
			else
			{
				repetitions = 4;  // Odd numbers (after 1) appear four times
			}
			for (int j = 0; j < repetitions && dx0 <= goal; j++)
			{
				dx0 += dx1;
				dx1 += dx2 + dx2_offset;  // Sum the current number 'repetitions' times
				count_index += 1;
			}
		}
		return count_index;
	}


	/**
	 * We can use the pattern to get the dx2 at a specific index numerically
	 *
	 * @param index the index/tick we want to calculate dx2 at
	 * @return the acceleration of heating/cooling at index/tick
	 */
	public static int getDx2AtIndex(int index)
	{
		if (index <= 1) return 1;

		index -= 2;
		// 0 1 2 3 4 5 6 7 8 9
		// e,e,e,e,e,e,o,o,o,o

		int block = index / 10;
		int block_idx = index % 10;
		int number = block * 2;
		if (block_idx <= 5)
		{
			return number + 2;
		}
		else
		{
			return number + 3;
		}
	}


	/**
	 * We can use the pattern to get the dx1 at a specific index numerically
	 *
	 * @param index    the index/tick we want to calculate the speed of heating/cooling
	 * @param constant the initial speed of heating/cooling.
	 * @return the speed of heating at index/tick
	 */
	public static int getDx1AtIndex(int index, int constant)
	{
		int _dx1 = constant;
		for (int i = 0; i < index; ++i)
		{
			_dx1 += getDx2AtIndex(i);
		}

		return _dx1;
	}

// Methods below are functional, but only used to for debugging & development

//	public static int getDx0AtIndex(int index, int constant)
//	{
//		int dx0 = 0;
//		int dx1 = getDx1AtIndex(0, constant);
//		for (int i = 0; i < index; i++)
//		{  // Start from 1 up to the count inclusive
//			int dx2 = getDx2AtIndex(i);
//			dx1 += dx2;  // Sum the current number 'repetitions' times
//			dx0 += dx1;
//		}
//		return dx0;
//	}

	// We iteratively generate dx2 into a list
//	public static List<Integer> generateDx2List(int count)
//	{
//		List<Integer> pattern = new ArrayList<>();  // This will hold our pattern
//		for (int n = 1, i = 0; i < count; n++)
//		{  // Start from 1 up to the count inclusive
//			int repetitions;
//			if (n == 1)
//			{
//				repetitions = 2;  // The first number appears twice
//			} else if (n % 2 == 0)
//			{
//				repetitions = 6;  // Even numbers appear six times
//			} else
//			{
//				repetitions = 4;  // Odd numbers (after 1) appear four times
//			}
//			for (int j = 0; j < repetitions && i < count; j++, i++)
//			{
//				pattern.add(n);  // Append the current number 'repetitions' times
//			}
//		}
//		return pattern;
//	}

//	public static int findDx0IndexContinue(int goal, int constant, int init_index)
//	{
//		int dx0 = getDx0AtIndex(init_index, constant);
//		int dx1 = getDx1AtIndex(init_index, constant);
//		int count_index = init_index;
//		for (; dx0 <= goal; count_index++)
//		{  // Start from 1 up to the count inclusive
//			int dx2 = getDx2AtIndex(count_index);
//			dx1 += dx2;  // Sum the current number 'repetitions' times
//			dx0 += dx1;
//		}
//		return count_index - init_index;
//	}
}

