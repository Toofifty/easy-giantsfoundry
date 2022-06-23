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
	public int getActionsForHeatLevel()
	{
		Heat heatStage = state.getCurrentHeat();
		Stage stage = state.getCurrentStage();
		if (heatStage != stage.getHeat())
		{
			// not the right heat to start with
			return 0;
		}

		int[] range = getCurrentHeatRange();
		int actions = 0;
		int heat = state.getHeatAmount();
		while (heat > range[0] && heat < range[1])
		{
			actions++;
			heat += stage.getHeatChange();
		}

		return actions;
	}
}
