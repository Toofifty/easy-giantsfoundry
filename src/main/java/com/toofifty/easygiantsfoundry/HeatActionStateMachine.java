package com.toofifty.easygiantsfoundry;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A state-machine that keeps track of heating/cooling actions.
 */
@Slf4j
@Data
public class HeatActionStateMachine
{
	/**
	 * Tick counter for heating, -1 means not currently heating.
	 */
	int heatingTicks = -1;

	/**
	 * Tick counter for cooling, -1 means not currently cooling.
	 */
	int coolingTicks = -1;

	boolean actionFast;

	boolean actionHeating;

	/**
	 * The starting heat amount of the heating/cooling action.
	 */
	int startingHeat;

	/**
	 * The estimated tick duration of the heating/cooling action.
	 */
	int estimatedDuration;

	/**
	 * The goal heat amount of the heating/cooling action.
	 */
	int goalHeat = 0;

	// debug
	boolean goalInRange;
	boolean isOverShooting;
	int predictedHeat;

	/**
	 * The last action the player clicked on. Used for ui overlay to display.
	 * When null, the state-machine will stop() and reset.
	 */
	String actionname = null;

	private EasyGiantsFoundryState state;
	private EasyGiantsFoundryConfig config;

	/**
	 * Start the state-machine with the given parameters.
	 * <p>
	 * These parameters have to be set-up manually before start().
	 * Velocity, AccelerationBonus, ActionName
	 *
	 * @param state        the current state of the foundry
	 * @param config       the current configuration of the plugin
	 * @param startingHeat the starting heat amount
	 * @see HeatActionStateMachine#setup(int, int, String)
	 */
	public void start(EasyGiantsFoundryState state, EasyGiantsFoundryConfig config, int startingHeat)
	{
		// use Velocity to determine if heating or cooling
		if (actionHeating)
		{
			heatingTicks = 0;
			coolingTicks = -1;
		}
		else
		{
			heatingTicks = -1;
			coolingTicks = 0;
		}

		this.startingHeat = startingHeat;
		this.state = state;
		this.config = config;

		updateEstimates();
	}

	/**
	 * Get the estimated remaining duration of the heating/cooling action.
	 *
	 * @return the estimated remaining duration in ticks
	 */
	public int getRemainingDuration()
	{
		if (isHeating())
		{
			return Math.max(0, (estimatedDuration - heatingTicks));
		}
		else if (isCooling())
		{
			return Math.max(0, (estimatedDuration - coolingTicks));
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Core logic. Runs once on {@link HeatActionStateMachine#start} and assumes synchronization with the game.
	 * Calculate the estimated duration and goal heat amount of the heating/cooling action.
	 */
	public void updateEstimates()
	{

		HeatActionSolver.DurationResult result =
			HeatActionSolver.solve(
				getState().getCurrentStage(),
				getState().getCurrentHeatRange(),
				getState().getActionsLeftInStage(),
				getStartingHeat(),
				actionFast,
				isHeating(),
				config.heatActionPadTicks() * 2
			);

		goalInRange = result.isGoalInRange();
		isOverShooting = result.isOvershooting();

		predictedHeat = result.getPredictedHeat();

		estimatedDuration = result.getDuration();
	}

	/**
	 * Helper to remind the neccessary parameters to start the state-machine.
	 *
	 * @param velocity          the velocity of the heating/cooling action, 7 for slow, 27 for fast.
	 * @param accelerationBonus the acceleration bonus of the heating/cooling action. Usually 0 for slow, 2 for fast.
	 * @param actionName        the name of the action to display in the ui overlay
	 */
	public void setup(boolean isFast, boolean isHeating, String actionName)
	{
		actionFast = isFast;
		actionHeating = isHeating;
		actionname = actionName;
	}

	/**
	 * Stop the state-machine.
	 */
	public void stop()
	{
		heatingTicks = -1;
		coolingTicks = -1;
		actionname = null;
	}

	/**
	 * Check if the state is currently heating.
	 *
	 * @return true if heating, false otherwise
	 */
	public boolean isHeating()
	{
		return heatingTicks >= 0;
	}

	/**
	 * Check if the state is currently cooling.
	 *
	 * @return true if cooling, false otherwise
	 */
	public boolean isCooling()
	{
		return coolingTicks >= 0;
	}

	/**
	 * Check if the heating/cooling state is currently idle. Neither heating nor cooling.
	 *
	 * @return
	 */
	public boolean isIdle()
	{
		return !(isHeating() || isCooling());
	}

	/**
	 * Tick the state-machine. This has to be called onVarbitChanged in order to sync with the game.
	 */
	public void onTick()
	{
		if (isIdle()) return;

		if (isHeating())
		{
			if (heatingTicks >= estimatedDuration)
			{
				stop();
			}
			else
			{
				heatingTicks++;
			}
		}
		else if (isCooling())
		{
			if (coolingTicks >= estimatedDuration)
			{
				stop();
			}
			else
			{
				coolingTicks++;
			}
		}
	}

}
