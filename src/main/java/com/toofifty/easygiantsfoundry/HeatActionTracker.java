package com.toofifty.easygiantsfoundry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

/**
 * A super round-about way of determining how many ticks
 * have been spent using the waterfall or lava pool.
 *
 * This will wait until the player clicks on of the options
 * on the objects, and when they are close enough to the object
 * it will start counting the ticks.
 *
 * If the player uses the same action again, nothing will happen.
 * But this will track changing from quenching to cooling, for example.
 */
@Singleton
public class HeatActionTracker
{
	@Getter
	private Action currentAction = Action.NONE;

	// start at -2 as it takes 2 ticks to start performing
	// actions when pathing to an object
	private int ticksInAction = -2;

	private final static List<WorldPoint> COOLING_TILES = List.of(
		new WorldPoint(3360, 11489, 0)
	);
	private final static List<WorldPoint> HEATING_TILES = List.of(
		new WorldPoint(3371, 11498, 0),
		new WorldPoint(3371, 11497, 0)
	);

	@Inject
	private Client client;

	@Inject
	public HeatActionTracker(EventBus eventBus)
	{
		eventBus.register(this);
	}

	private void reset()
	{
		currentAction = Action.NONE;
		ticksInAction = -2;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// todo: only reset when the menu option cancels the
		// current action (e.g. interfaces won't cancel)
		if (!event.getMenuOption().contains("-preform"))
		{
			reset();
			return;
		}

		Action newAction = Action.fromMenuOption(event.getMenuOption());

		if (currentAction != newAction)
		{
			currentAction = newAction;

			// 1 tick delay if switching actions on same object
			// 2 tick delay if walking to object
			ticksInAction = isOnCorrectTile(currentAction) ? -1 : -2;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (currentAction == Action.NONE)
		{
			return;
		}

		if (isOnCorrectTile(currentAction))
		{
			ticksInAction++;
		}
	}

	private boolean isOnCorrectTile(Action action)
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		return currentAction.nearTiles.contains(playerLocation);
	}

	public int getTicksInAction()
	{
		return Math.max(ticksInAction, 0);
	}

	@AllArgsConstructor
	public enum Action {
		COOL("Cool", COOLING_TILES, false, false),
		QUENCH("Quench", COOLING_TILES, false, true),
		HEAT("Heat", HEATING_TILES, true, false),
		DUNK("Dunk", HEATING_TILES, true, true),
		NONE(null, null, false, false);

		private final String name;
		private final List<WorldPoint> nearTiles;

		@Getter
		@Accessors(fluent = true)
		private final boolean addsHeat;
		@Getter
		private final boolean isLargeAction;

		public String getName()
		{
			return name == null ? "null" : name;
		}

		public static Action fromMenuOption(String menuOption)
		{
			return Arrays.stream(values())
				.filter(action -> menuOption.contains(action.name))
				.findFirst().orElse(Action.NONE);
		}
	}
}
