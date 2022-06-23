package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Singleton
public class FoundryOverlay2D extends OverlayPanel
{
	private EasyGiantsFoundryState state;
	private EasyGiantsFoundryHelper helper;
	private EasyGiantsFoundryConfig config;

	@Inject
	private FoundryOverlay2D(EasyGiantsFoundryState state, EasyGiantsFoundryHelper helper, EasyGiantsFoundryConfig config)
	{
		this.state = state;
		this.helper = helper;
		this.config = config;
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
		if (!state.isEnabled() || state.getCurrentStage() == null || !config.drawInfoPanel())
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

		return super.render(graphics);
	}
}
