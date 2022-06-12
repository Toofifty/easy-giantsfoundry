package com.toofifty.easygiantsfoundry;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Easy Giant's Foundry",
	description = "Helpful overlays for the Giant's Foundry minigame"
)
public class EasyGiantsFoundryPlugin extends Plugin
{
	private static final int TRIP_HAMMER = 44619;
	private static final int GRINDSTONE = 44620;
	private static final int POLISHING_WHEEL = 44621;

	private static final int LAVA_POOL = 44631;
	private static final int WATERFALL = 44632;

	private static final int CRUCIBLE = 44776;
	private static final int MOULD_JIG = 44777;

	private static final int KOVAC_NPC = 11472;

	private static final int PREFORM = 27010;

	@Inject
	private EasyGiantsFoundryState state;

	@Inject
	private EasyGiantsFoundryHelper helper;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private FoundryOverlay2D overlay2d;

	@Inject
	private FoundryOverlay3D overlay3d;

	@Inject
	private MouldHelper mouldHelper;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay2d);
		overlayManager.add(overlay3d);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay2d);
		overlayManager.remove(overlay3d);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		switch (gameObject.getId())
		{
			case POLISHING_WHEEL:
				state.setEnabled(true);
				overlay3d.polishingWheel = gameObject;
				break;
			case GRINDSTONE:
				overlay3d.grindstone = gameObject;
				break;
			case LAVA_POOL:
				overlay3d.lavaPool = gameObject;
				break;
			case WATERFALL:
				overlay3d.waterfall = gameObject;
				break;
			case TRIP_HAMMER:
				overlay3d.tripHammer = gameObject;
				break;
			case MOULD_JIG:
				overlay3d.mouldJig = gameObject;
				break;
			case CRUCIBLE:
				overlay3d.crucible = gameObject;
				break;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState().equals(GameState.LOADING))
		{
			state.setEnabled(false);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject gameObject = event.getGameObject();
		switch (gameObject.getId())
		{
			case POLISHING_WHEEL:
				state.setEnabled(false);
				overlay3d.polishingWheel = null;
				break;
			case GRINDSTONE:
				overlay3d.grindstone = null;
				break;
			case LAVA_POOL:
				overlay3d.lavaPool = null;
				break;
			case WATERFALL:
				overlay3d.waterfall = null;
				break;
			case TRIP_HAMMER:
				overlay3d.tripHammer = null;
				break;
			case MOULD_JIG:
				overlay3d.mouldJig = null;
				break;
			case CRUCIBLE:
				overlay3d.crucible = null;
				break;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == KOVAC_NPC)
		{
			overlay3d.kovac = event.getNpc();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().getId() == KOVAC_NPC)
		{
			overlay3d.kovac = null;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId()
		 	&& event.getItemContainer().count(PREFORM) == 0)
		{
			state.reset();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == MouldHelper.DRAW_MOULD_LIST_SCRIPT
			|| event.getScriptId() == MouldHelper.REDRAW_MOULD_LIST_SCRIPT
			|| event.getScriptId() == MouldHelper.SELECT_MOULD_SCRIPT
			|| event.getScriptId() == MouldHelper.RESET_MOULD_SCRIPT)
		{
			mouldHelper.selectBest(event.getScriptId());
		}
	}
}
