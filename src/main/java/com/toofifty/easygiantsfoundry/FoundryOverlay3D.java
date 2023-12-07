package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class FoundryOverlay3D extends Overlay {

    private static final int HAND_IN_WIDGET = 49414221;

    GameObject tripHammer;
    GameObject grindstone;
    GameObject polishingWheel;
    GameObject lavaPool;
    GameObject waterfall;
    GameObject mouldJig;
    GameObject crucible;
    NPC kovac;

	@Inject
    private Client client;

	@Inject
    private EasyGiantsFoundryState state;

	@Inject
    private EasyGiantsFoundryHelper helper;

	@Inject
    private EasyGiantsFoundryConfig config;

	@Inject
	private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private FoundryOverlay3D()
    {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!state.isEnabled())
        {
            return null;
        }

        if (config.highlightKovac())
        {
            drawKovacIfHandIn(graphics);
        }

        if (state.getCurrentStage() == null)
        {
            if (config.highlightMould())
            {
                drawMouldIfNotSet(graphics);
            }
            if (config.highlightCrucible())
            {
                drawCrucibleIfMouldSet(graphics);
            }
            return null;
        }

        Stage stage = state.getCurrentStage();
        GameObject stageObject = getStageObject(stage);
        if (stageObject == null)
        {
            return null;
        }

        Heat heat = state.getCurrentHeat();

		if (config.highlightTools())
		{
			drawHighlight(graphics, stageObject, getObjectColor(stage, heat), true);
		}

		if (config.highlightWaterAndLava())
		{
			int cools = helper.getCoolsToTarget();
			if (cools > -1)
			{
				int quenches = helper.getQuenchesToTarget();
				drawOverlayText(
					graphics, waterfall, "Cool: " + cools + " Quench: " + quenches,
					cools > 0 ? config.toolGood() : config.toolBad()
				);
			}

			int heats = helper.getHeatsToTarget();
			if (heats > -1)
			{
				int dunks = helper.getDunksToTarget();
				drawOverlayText(
					graphics, lavaPool, "Heat: " + heats + " Dunk: " + dunks,
					heats > 0 ? config.toolGood() : config.toolBad()
				);
			}
		}

        if (stage.getHeat() != heat && config.highlightWaterAndLava())
        {
            drawHeatChangers(graphics);
        }

        return null;
    }

    private void drawHeatChangers(Graphics2D graphics)
    {
        int change = state.getHeatChangeNeeded();
		if (change == 0)
		{
			return;
		}

		drawHighlight(
			graphics,
			change < 0 ? waterfall : lavaPool,
			config.lavaWaterfallColour(),
			true
		);
    }

    private void drawCrucibleIfMouldSet(Graphics2D graphics)
    {
        if (client.getVarbitValue(MouldHelper.SWORD_TYPE_1_VARBIT) == 0)
        {
            return;
        }
        if (client.getVarbitValue(EasyGiantsFoundryState.VARBIT_GAME_STAGE) != 1)
        {
            return;
        }

		drawHighlight(graphics, crucible, config.generalHighlight(), false);

		int metal1 = state.getMetal1Amount();
		int metal2 = state.getMetal2Amount();
		Color textColor = metal1 + metal2 == 28 ? config.toolGood() : config.generalHighlight();
		drawOverlayText(graphics, crucible, metal1 + " / " + metal2, textColor);
    }

    private void drawMouldIfNotSet(Graphics2D graphics)
    {
        if (client.getWidget(EasyGiantsFoundryState.WIDGET_PROGRESS_PARENT) != null
            || client.getVarbitValue(MouldHelper.SWORD_TYPE_1_VARBIT) == 0
            || (client.getVarbitValue(EasyGiantsFoundryState.VARBIT_GAME_STAGE) != 0
                && client.getVarbitValue(EasyGiantsFoundryState.VARBIT_GAME_STAGE) != 2))
        {
            return;
        }

		drawHighlight(graphics, mouldJig, config.generalHighlight(), false);
    }

    private void drawKovacIfHandIn(Graphics2D graphics)
    {
        Widget handInWidget = client.getWidget(HAND_IN_WIDGET);
        if (handInWidget != null && !handInWidget.isHidden())
        {
			drawHighlight(graphics, kovac, config.generalHighlight(), false);
        }
    }

	private Color getObjectColor(Stage stage, Heat heat)
	{
		if (stage.getHeat() != heat)
		{
			return config.toolBad();
		}

		if (BonusWidget.isActive(client))
		{
			return config.toolBonus();
		}

		int actionsLeft = helper.getActionsLeftInStage();
		float heatLeft = helper.getActionsForHeatLevel();
		if (actionsLeft <= 1 || heatLeft <= 1)
		{
			return config.toolCaution();
		}

		return config.toolGood();
	}

	private GameObject getStageObject(Stage stage)
	{
		switch (stage)
		{
			case TRIP_HAMMER:
				return tripHammer;
			case GRINDSTONE:
				return grindstone;
			case POLISHING_WHEEL:
				return polishingWheel;
		}
		return null;
	}

	private void drawHighlight(Graphics2D graphics, Shape shape, Color color, boolean dimOnHover)
	{
		if (shape != null)
		{
			graphics.setColor(color);

			if (dimOnHover)
			{
				Point mousePosition = client.getMouseCanvasPosition();
				if (shape.contains(mousePosition.getX(), mousePosition.getY()))
				{
					graphics.setColor(color.darker());
				}
			}

			graphics.setColor(color);
			graphics.draw(shape);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(shape);
		}
	}

	private void drawHighlight(Graphics2D graphics, GameObject object, Color color, boolean dimOnHover)
	{
		switch (config.highlightMode())
		{
			case CLICKBOX:
				drawHighlight(graphics, object.getClickbox(), color, dimOnHover);
				return;
			case HULL:
				drawHighlight(graphics, object.getConvexHull(), color, dimOnHover);
				return;
			case OUTLINE:
				modelOutlineRenderer.drawOutline(object, 2, color, 0);
		}
	}

	private void drawHighlight(Graphics2D graphics, NPC npc, Color color, boolean dimOnHover)
	{
		switch (config.highlightMode())
		{
			case CLICKBOX:
			case HULL:
				drawHighlight(graphics, npc.getConvexHull(), color, dimOnHover);
				return;
			case OUTLINE:
				modelOutlineRenderer.drawOutline(npc, 2, color, 0);
		}
	}

	/**
	 * Draw overlay text in the centre of the object's bounds
	 */
	private void drawOverlayText(Graphics2D graphics, GameObject object, String text, Color color)
	{
		Shape clickbox = object.getClickbox();
		if (clickbox == null) return;

		Rectangle bounds = clickbox.getBounds();
		if (bounds == null) return;

		graphics.setFont(FontManager.getRunescapeBoldFont());

		FontMetrics metrics = graphics.getFontMetrics();
		int textHeight = metrics.getHeight();
		int textWidth = metrics.stringWidth(text);

		int x = bounds.x + bounds.width / 2 - textWidth / 2;
		int y = bounds.y + bounds.height / 2 - textHeight / 2;

		// shadow
		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, y + 1);

		// text
		graphics.setColor(color);
		graphics.drawString(text, x, y);
	}
}
