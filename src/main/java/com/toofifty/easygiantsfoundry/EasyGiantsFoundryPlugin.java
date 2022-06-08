package com.toofifty.easygiantsfoundry;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Easy Giant's Foundry",
	description = "Helpful overlays for the Giant's Foundry minigame"
)
public class EasyGiantsFoundryPlugin extends Plugin
{
	private static final int POLISHING_WHEEL = 44621;
	private static final int PREFORM = 27010;

	@Inject
	private EasyGiantsFoundryState state;

	@Inject
	private EasyGiantsFoundryHelper helper;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private EasyGiantsFoundryOverlay overlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (event.getGameObject().getId() == POLISHING_WHEEL)
		{
			state.setEnabled(true);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (event.getGameObject().getId() == POLISHING_WHEEL)
		{
			state.setEnabled(false);
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
}
