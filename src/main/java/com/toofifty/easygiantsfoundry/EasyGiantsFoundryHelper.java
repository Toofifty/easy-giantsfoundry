package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EasyGiantsFoundryHelper
{
	// heat lowers every 2 ticks

	// seems to be between 7-11 per
	private static final int HEAT_LAVA_HEAT = 8;
	private static final int COOL_WATERFALL_HEAT = -8;

	// 27-37
	private static final int DUNK_LAVA_HEAT = 32;
	private static final int QUENCH_WATERFALL_HEAT = -32;

	@Inject
	private EasyGiantsFoundryState state;

	@Inject
	private HeatActionTracker heatActionTracker;

	/**
	 * Get the amount of progress each stage needs
	 */
	public double getProgressPerStage()
	{
		return 1000d / state.getStages().size();
	}

	public int getActionsLeftInStage()
	{
		int progress = state.getProgressAmount();
		double progressPerStage = getProgressPerStage();
		double progressTillNext = progressPerStage - progress % progressPerStage;

		Stage current = state.getCurrentStage();
		return (int) Math.ceil(progressTillNext / current.getProgressPerAction());
	}

	public int[] getCurrentHeatRange()
	{
		switch (state.getCurrentStage())
		{
			case POLISHING_WHEEL:
				return state.getLowHeatRange();
			case GRINDSTONE:
				return state.getMedHeatRange();
			case TRIP_HAMMER:
				return state.getHighHeatRange();
			default:
				return new int[]{0, 0};
		}
	}

	/**
	 * Get the amount of current stage actions that can be
	 * performed before the heat drops too high or too low to
	 * continue
	 */
	public float getActionsForHeatLevel()
	{
		Heat heatStage = state.getCurrentHeat();
		Stage stage = state.getCurrentStage();
		if (heatStage != stage.getHeat())
		{
			// not the right heat to start with
			return 0;
		}

		int[] range = getCurrentHeatRange();

		int currentHeat = state.getHeatAmount();
		boolean isLosingHeat = stage.getHeatChange() < 0;
		int lowerBound = isLosingHeat ? range[0] : currentHeat;
		int upperBound = isLosingHeat ? currentHeat : range[1];
		int delta = upperBound - lowerBound;

		return ((float) delta) / Math.abs(stage.getHeatChange());
	}

	public int getTargetTemperature()
	{
		switch (state.getCurrentStage())
		{
			case POLISHING_WHEEL:
				return state.getLowHeatRange()[1];
			case GRINDSTONE:
				return state.getMedHeatRange()[0];
			case TRIP_HAMMER:
				return state.getHighHeatRange()[1];
			default:
				return 0;
		}
	}

	private int getHeatActionsToTarget(HeatActionTracker.Action action)
	{
		int target = getTargetTemperature();
		int heat = state.getHeatAmount();

		int diff = target - heat;

		// no need to calculate in the wrong direction
		if (action.addsHeat() && diff < 0)
		{
			return -1;
		}
		if (!action.addsHeat() && diff > 0)
		{
			return -1;
		}

		int absDiff = Math.abs(diff);
		int currentProgress = heatActionTracker.getCurrentAction() == action
			? heatActionTracker.getTicksInAction()
			: 0;

		return action.isLargeAction()
			? HeatActionCalculator.getLargeActionsRequired(absDiff, currentProgress)
			: HeatActionCalculator.getSmallActionsRequired(absDiff, currentProgress);
	}

	public int getCoolsToTarget()
	{
		return getHeatActionsToTarget(HeatActionTracker.Action.COOL);
	}

	public int getQuenchesToTarget()
	{
		return getHeatActionsToTarget(HeatActionTracker.Action.QUENCH);
	}

	public int getHeatsToTarget()
	{
		return getHeatActionsToTarget(HeatActionTracker.Action.HEAT);
	}

	public int getDunksToTarget()
	{
		return getHeatActionsToTarget(HeatActionTracker.Action.DUNK);
	}
}
