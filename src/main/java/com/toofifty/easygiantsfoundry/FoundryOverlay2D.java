package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

@Singleton
public class FoundryOverlay2D extends OverlayPanel
{
	private final EasyGiantsFoundryPlugin plugin;
	private final EasyGiantsFoundryState state;
	private final EasyGiantsFoundryHelper helper;
	private final EasyGiantsFoundryConfig config;

	@Inject
	private FoundryOverlay2D(EasyGiantsFoundryPlugin plugin, EasyGiantsFoundryState state, EasyGiantsFoundryHelper helper, EasyGiantsFoundryConfig config)
	{
		this.plugin = plugin;
		this.state = state;
		this.helper = helper;
		this.config = config;
		this.setPosition(OverlayPosition.BOTTOM_LEFT);
	}

	private Color getHeatColor(int actions, int heat)
	{
		if (heat >= actions)
		{
			return ColorScheme.PROGRESS_COMPLETE_COLOR;
		}

		if (heat > 0)
		{
			return ColorScheme.PROGRESS_INPROGRESS_COLOR;
		}

		return ColorScheme.PROGRESS_ERROR_COLOR;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!state.isEnabled() || state.getCurrentStage() == null)
		{
			return null;
		}

		Heat heat = state.getCurrentHeat();
		Stage stage = state.getCurrentStage();

		if (config.drawTitle())
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Easy Giant's Foundry").build());
		}
		if (config.drawHeatInfo())
		{
			panelComponent.getChildren().add(
					LineComponent.builder().left("Heat").right(heat.getName() + " (" + state.getHeatAmount() / 10 + "%)").rightColor(heat.getColor()).build()
			);
		}
		if (config.drawStageInfo())
		{
			panelComponent.getChildren().add(
					LineComponent.builder().left("Stage").right(stage.getName() + " (" + state.getProgressAmount() / 10 + "%)").rightColor(stage.getHeat().getColor()).build()
			);
		}

		int actionsLeft = helper.getActionsLeftInStage();
		int heatLeft = helper.getActionsForHeatLevel();

		if (config.drawActionsLeft())
		{
			panelComponent.getChildren().add(
					LineComponent.builder().left("Actions left").right(actionsLeft + "").build()
			);
		}
		if (config.drawHeatLeft())
		{
			panelComponent.getChildren().add(
					LineComponent.builder().left("Heat left").right(heatLeft + "").rightColor(getHeatColor(actionsLeft, heatLeft)).build()
			);
		}

		int points = plugin.getReputationTracker().getShopPoints();
		if (config.drawShopPoints())
		{
			panelComponent.getChildren().add(
					LineComponent.builder().left("Points").right(points + "").build()
			);
		}

		return super.render(graphics);
	}
}
