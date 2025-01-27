package com.toofifty.easygiantsfoundry;

import com.google.inject.Provides;

import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryClientIDs.VARBIT_GAME_STAGE;
import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryClientIDs.VARBIT_HEAT;
import com.toofifty.easygiantsfoundry.enums.Stage;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Objects;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Easy Giants' Foundry",
	description = "Helpful overlays for the Giants' Foundry minigame"
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
	private static final int STORAGE = 44778;

	private static final int KOVAC_NPC = 11472;

	private static final int PREFORM = 27010;

	private static final int REPUTATION_VARBIT = 3436;

	// 5 total items, includes Smiths gloves (i);
	private static final Set<Integer> SMITHS_OUTFIT_IDS = Set.of(27023, 27025, 27027, 27029, 27031);

	private Stage oldStage;

	private int lastBoost;

	private boolean bonusNotified = false;

	@Getter
	private int reputation;

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

	@Inject
	private EasyGiantsFoundryConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay2d);
		overlayManager.add(overlay3d);
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			reputation = client.getVarpValue(REPUTATION_VARBIT);
		}
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
			case STORAGE:
				overlay3d.storage = gameObject;
				break;
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
			case STORAGE:
				overlay3d.storage = null;
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

		if (event.getGameState().equals(GameState.LOGGED_IN))
		{
			reputation = client.getVarpValue(REPUTATION_VARBIT);
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		final int curBoost = statChanged.getBoostedLevel();
		// if the difference between current and last boost is != 0 then a stat boost (or drop) change occurred
		if (!statChanged.getSkill().equals(Skill.SMITHING) ||
			curBoost != lastBoost ||
			!state.isEnabled() ||
			state.getCurrentStage() == null)
		{
			lastBoost = curBoost;
			return;
		}

		if (config.showGiantsFoundryStageNotifications() &&
			state.getActionsLeftInStage() == config.StageNotificationsThreshold() &&
			(oldStage == null || oldStage != state.getCurrentStage()))
		{
			notifier.notify("About to finish the current stage!");
			oldStage = state.getCurrentStage();
		}
		else if (config.showGiantsFoundryHeatNotifications() &&
			state.getActionsForHeatLevel() == config.HeatNotificationsThreshold())
		{
			notifier.notify("About to run out of heat!");
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
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			if (event.getItemContainer().count(PREFORM) == 0)
			{
				state.reset();
				oldStage = null;
			}
			else
			{
				updateSmithsOutfitPieces();
			}
		}
	}

	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getOption().startsWith("Heat-preform") || event.getOption().startsWith("Dunk-preform"))
		{
		}
		else if (event.getOption().startsWith("Cool-preform") || event.getOption().startsWith("Quench-preform")) {
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		clientThread.invokeAtTickEnd(() ->
		{
			if (!(event.getMenuAction() == MenuAction.GAME_OBJECT_FIRST_OPTION
				|| event.getMenuAction() == MenuAction.GAME_OBJECT_SECOND_OPTION
				|| event.getMenuAction() == MenuAction.GAME_OBJECT_THIRD_OPTION
				|| event.getMenuAction() == MenuAction.GAME_OBJECT_FOURTH_OPTION
				|| event.getMenuAction() == MenuAction.GAME_OBJECT_FIFTH_OPTION
				|| event.getMenuAction() == MenuAction.WIDGET_TARGET_ON_GAME_OBJECT
				|| event.getMenuAction() == MenuAction.WALK))
			{
				return;
			}

			if (!state.isEnabled()) return;

			if (event.getMenuTarget().contains("Crucible "))
			{
				if (event.getMenuOption().equals("Pour"))
				{
					if (client.getVarbitValue(VARBIT_GAME_STAGE) == 1)
					{
						state.setLastKnownCrucibleScore((int) state.getCrucibleScore());
					}
					// add persistent game message of the alloy value so user can reference later.
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The score of the preform is <col=00FFFF>" + ((int) state.getCrucibleScore() + state.getMouldScore()), null);
				}
			}

			// Could not find a varbit to capture, so capture the menu-option directly.
			// start the HeatActionStateMachine when varbit begins to update in onVarbitChanged()
			if (event.getMenuOption().startsWith("Heat-preform"))
			{
				state.heatActionStateMachine.stop();
				state.heatActionStateMachine.setup(false, true, "heats");
			}
			else if (event.getMenuOption().startsWith("Dunk-preform"))
			{
				state.heatActionStateMachine.stop();
				state.heatActionStateMachine.setup(true, true, "dunks");
			}
			else if (event.getMenuOption().startsWith("Cool-preform"))
			{
				state.heatActionStateMachine.stop();
				state.heatActionStateMachine.setup(false, false, "cools");
			}
			else if (event.getMenuOption().startsWith("Quench-preform"))
			{
				state.heatActionStateMachine.stop();
				state.heatActionStateMachine.setup(true, false, "quenches");
			}
			else if (!state.heatActionStateMachine.isIdle()) // canceled heating/cooling, stop the heating state-machine
			{
				state.heatActionStateMachine.stop();
			}

		});
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
			updateMouldScore();
		}
	}

	private void updateMouldScore() {

		state.setMouldScore(mouldHelper.getTotalScore());

		// show mould score on Mould UI Title
		Widget mouldParent = client.getWidget(47054850);
		int mouldScore = state.getMouldScore();
		if (mouldParent != null && mouldScore >= 0)
		{
			Widget title = Objects.requireNonNull(mouldParent.getChild(1));

			// not sure why, the ":" character turns into ":      ," when rendered; omitting it.
			title.setText("Giants' Foundry Mould Setup <col=FFFFFF>(Score " + mouldScore + ")");
		}
	}

	// previous heat varbit value, used to filter out passive heat decay.
	private int previousHeat = 0;

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() == REPUTATION_VARBIT)
		{
			reputation = client.getVarpValue(REPUTATION_VARBIT);
		}

		// STAGE becomes 0 again after player picks up the preform
		if (event.getVarbitId() == VARBIT_GAME_STAGE && event.getValue() == 0)
		{
			// clear out the current and soon to be previous scores.
			state.setLastKnownCrucibleScore(-1);
			state.setMouldScore(-1);
		}


		// start the heating state-machine when the varbit updates
		// if heat varbit updated and the user clicked, start the state-machine
		if (event.getVarbitId() == VARBIT_HEAT)
		{
			// ignore passive heat decay, one heat per two ticks
			int delta = event.getValue() - previousHeat;
			// sign check: num * num > 0 == same sign
			if (delta != -1)
			{
				if (state.heatActionStateMachine.getActionname() != null)
				{
					// if the state-machine is idle, start it
					if (state.heatActionStateMachine.isIdle())
					{
						state.heatActionStateMachine.start(state, config, previousHeat);
					}
					state.heatActionStateMachine.onTick();
				}

				if (config.debugging())
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
						"Heat: <col=FF0000>" + event.getValue() + "</col>" +
							"Delta: <col=00FFFF>" + delta + "</col> " +
							"Heating Ticks: <col=00FFFF>" + state.heatActionStateMachine.heatingTicks + "</col>" +
							" Cooling Ticks: <col=00FFFF>" + state.heatActionStateMachine.coolingTicks + "</col>" +
							" Remaining Ticks: <col=00FFFF>" + state.heatActionStateMachine.getRemainingDuration(), "");
				}
			}

//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Delta: <col=00FFFF>" + delta + "</col> ", "");
			previousHeat = event.getValue();
		}
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!EasyGiantsFoundryConfig.GROUP.equals(configChanged.getGroup()))
		{
			return;
		}

		if (EasyGiantsFoundryConfig.SOUND_ID.equals(configChanged.getKey()))
		{
			clientThread.invoke(() -> client.playSoundEffect(config.soundId()));
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		checkBonus();
	}

	private void checkBonus()
	{
		if (!state.isEnabled() || state.getCurrentStage() == null
			|| state.getCurrentStage().getHeat() != state.getCurrentHeat()
			|| !BonusWidget.isActive(client))
		{
			bonusNotified = false;
			return;
		}

		if (bonusNotified)
		{
			return;
		}

		state.addBonusActionReceived();

		if (config.bonusNotification())
		{
			notifier.notify("Bonus - Click tool");
		}
		if (config.bonusSoundNotify())
		{
			client.playSoundEffect(config.soundId());
		}

		bonusNotified = true;
	}

	private void updateSmithsOutfitPieces()
	{
		int pieces = 0;

		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment != null)
		{
			for (Item item : equipment.getItems())
			{
				if (item != null && isSmithsOutfitPiece(item.getId()))
				{
					pieces++;
				}
			}
		}
		state.setSmithsOutfitPieces(pieces);
	}

	private boolean isSmithsOutfitPiece(int itemId)
	{
		return SMITHS_OUTFIT_IDS.contains(itemId);
	}

	@Provides
	EasyGiantsFoundryConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyGiantsFoundryConfig.class);
	}
}
