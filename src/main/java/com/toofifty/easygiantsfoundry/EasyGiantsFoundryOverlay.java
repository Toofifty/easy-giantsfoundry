package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Singleton
public class EasyGiantsFoundryOverlay extends OverlayPanel
{
	@Inject
	private EasyGiantsFoundryState state;

	@Inject
	private EasyGiantsFoundryHelper helper;

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

		panelComponent.getChildren().add(TitleComponent.builder().text("Easy Giant's Foundry").build());
		panelComponent.getChildren().add(
			LineComponent.builder().left("Heat").right(heat.getName() + " (" + state.getHeatAmount() / 10 + "%)").rightColor(heat.getColor()).build()
		);
		panelComponent.getChildren().add(
			LineComponent.builder().left("Stage").right(stage.getName() + " (" + state.getProgressAmount() / 10 + "%)").rightColor(stage.getColor()).build()
		);

		int actionsLeft = helper.getActionsLeftInStage();
		int heatLeft = helper.getActionsForHeatLevel();

		panelComponent.getChildren().add(
			LineComponent.builder().left("Actions left").right(actionsLeft + "").build()
		);
		panelComponent.getChildren().add(
			LineComponent.builder().left("Heat left").right(heatLeft + "").rightColor(getHeatColor(actionsLeft, heatLeft)).build()
		);

		return super.render(graphics);
	}
}
