package com.toofifty.easygiantsfoundry;

import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryClientIDs.VARBIT_GAME_STAGE;
import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryClientIDs.WIDGET_PROGRESS_PARENT;
import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryHelper.getHeatColor;
import static com.toofifty.easygiantsfoundry.MouldHelper.SWORD_TYPE_1_VARBIT;
import static com.toofifty.easygiantsfoundry.MouldHelper.SWORD_TYPE_2_VARBIT;
import com.toofifty.easygiantsfoundry.enums.CommissionType;
import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.StringUtils;

public class FoundryOverlay3D extends Overlay
{

	private static final int HAND_IN_WIDGET = 49414221;
	private final ModelOutlineRenderer modelOutlineRenderer;

	GameObject tripHammer;
	GameObject grindstone;
	GameObject polishingWheel;
	GameObject lavaPool;
	GameObject waterfall;
	GameObject mouldJig;
	GameObject crucible;
	NPC kovac;

	private final Client client;
	private final EasyGiantsFoundryState state;
	private final EasyGiantsFoundryConfig config;

	@Inject
	private FoundryOverlay3D(
		Client client,
		EasyGiantsFoundryState state,
		EasyGiantsFoundryConfig config,
		ModelOutlineRenderer modelOutlineRenderer)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.state = state;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
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

		int actionsLeft = state.getActionsLeftInStage();
		int heatLeft = state.getActionsForHeatLevel();
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
			if (config.drawMouldInfoOverlay())
			{
				drawMouldScoreIfMouldSet(graphics);
				drawPreformScoreIfPoured(graphics);
			}


			return null;
		}

		Stage stage = state.getCurrentStage();
		GameObject stageObject = getStageObject(stage);
		if (stageObject == null)
		{
			return null;
		}

		drawActionOverlay(graphics, stageObject);

		Heat heat = state.getCurrentHeat();
		Color color = getObjectColor(stage, heat);
		// TODO Config
		if (config.highlightStyle() == HighlightStyle.HIGHLIGHT_CLICKBOX)
		{
			drawObjectClickbox(graphics, stageObject, color);
		}
		else
		{
			drawObjectOutline(graphics, stageObject, color);
		}

		if ((stage.getHeat() != heat || !state.heatingCoolingState.isIdle()) && config.highlightWaterAndLava())
		{
			drawHeatChangers(graphics);
		}

		if (state.heatingCoolingState.isCooling())
		{
			drawHeatingCoolingOverlay(graphics, waterfall);
		}
		if (state.heatingCoolingState.isHeating())
		{
			drawHeatingCoolingOverlay(graphics, lavaPool);
		}


		return null;
	}

	private void drawObjectClickbox(Graphics2D graphics, GameObject stageObject, Color color)
	{
		Shape objectClickbox = stageObject.getClickbox();
		if (objectClickbox != null && config.highlightTools())
		{
			Point mousePosition = client.getMouseCanvasPosition();
			if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY()))
			{
				graphics.setColor(color.darker());
			}
			else
			{
				graphics.setColor(color);
			}
			graphics.draw(objectClickbox);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(objectClickbox);
		}
	}

	private void drawObjectOutline(Graphics2D graphics, GameObject stageObject, Color color)
	{
		Color _color = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.borderAlpha());
		modelOutlineRenderer.drawOutline(stageObject, config.borderThickness(), _color, config.borderFeather());
	}

	private void drawHeatingCoolingOverlay(
		Graphics2D graphics,
		GameObject stageObject
	)
	{
		if (!config.drawLavaWaterInfoOverlay())
		{
			return;
		}

		if (state.heatingCoolingState.isIdle())
		{
			return;
		}

		String text;
		text = String.format("%d %s",
			state.heatingCoolingState.getRemainingDuration(),
			state.heatingCoolingState.getActionName()
		);

		LocalPoint stageLoc = stageObject.getLocalLocation();
		stageLoc = new LocalPoint(stageLoc.getX(), stageLoc.getY());

		Point pos = Perspective.getCanvasTextLocation(client, graphics, stageLoc, text, 50);
		Color color = config.lavaWaterfallColour();

		OverlayUtil.renderTextLocation(graphics, pos, text, color);
	}

	private void drawHeatChangers(Graphics2D graphics)
	{
		int change = state.getHeatChangeNeeded();
		Shape shape = null;

		if (change < 0 || state.heatingCoolingState.isCooling())
		{
			shape = waterfall.getClickbox();
		}
		else if (change > 0 || state.heatingCoolingState.isHeating())
		{
			shape = lavaPool.getClickbox();
		}
		if (shape != null)
		{
			Point mousePosition = client.getMouseCanvasPosition();
			Color color = config.lavaWaterfallColour();
			if (shape.contains(mousePosition.getX(), mousePosition.getY()))
			{
				graphics.setColor(color.darker());
			}
			else
			{
				graphics.setColor(color);
			}
			graphics.draw(shape);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(shape);
		}
	}

	static final int CRUCIBLE_CAPACITY = 28;

	private void drawCrucibleContent(Graphics2D graphics)
	{
		if (!config.drawCrucibleInfoOverlay())
		{
			return;
		}
		String text = String.format("%d/%d score: %d", state.getCrucibleCount(), CRUCIBLE_CAPACITY, (int)state.getCrucibleScore());

		LocalPoint crucibleLoc = crucible.getLocalLocation();
		crucibleLoc = new LocalPoint(crucibleLoc.getX() - 100, crucibleLoc.getY());

		Point pos = Perspective.getCanvasTextLocation(client, graphics, crucibleLoc, text, 200);
		Color color;
		if (state.getCrucibleCount() == CRUCIBLE_CAPACITY)
		{
			color = config.toolGood();
		}
		else
		{
			color = config.generalHighlight();
		}
		OverlayUtil.renderTextLocation(graphics, pos, text, color);
	}

	private void drawMouldScoreIfMouldSet(Graphics2D graphics) {
		if (client.getVarbitValue(SWORD_TYPE_1_VARBIT) == 0)
		{
			return;
		}
		if (client.getVarbitValue(VARBIT_GAME_STAGE) != 1)
		{
			return;
		}

		if (state.getMouldScore() < 0)
		{
			return;
		}

		String text = String.format("score: %d", state.getMouldScore());
		LocalPoint mouldLoc = mouldJig.getLocalLocation();
		Point pos = Perspective.getCanvasTextLocation(client, graphics, mouldLoc, text, 115);
		Color color = config.generalHighlight();

		OverlayUtil.renderTextLocation(graphics, pos, text, color);
	}
	private void drawPreformScoreIfPoured(Graphics2D graphics) {
		if (client.getVarbitValue(VARBIT_GAME_STAGE) != 2)
		{
			return;
		}

		if (state.getMouldScore() < 0 || state.getLastKnownCrucibleScore() < 0)
		{
			return;
		}

		int preformScore = state.getLastKnownCrucibleScore() + state.getMouldScore();
		String text = String.format("score: %d", preformScore);
		LocalPoint mouldLoc = mouldJig.getLocalLocation();
		Point pos = Perspective.getCanvasTextLocation(client, graphics, mouldLoc, text, 115);

		Color color = config.generalHighlight();

		OverlayUtil.renderTextLocation(graphics, pos, text, color);
	}

	private void drawCrucibleIfMouldSet(Graphics2D graphics)
	{
		if (client.getVarbitValue(SWORD_TYPE_1_VARBIT) == 0)
		{
			return;
		}
		if (client.getVarbitValue(VARBIT_GAME_STAGE) != 1)
		{
			return;
		}

		drawCrucibleContent(graphics);

		if (config.highlightStyle() == HighlightStyle.HIGHLIGHT_CLICKBOX)
		{
			Shape shape = crucible.getConvexHull();
			if (shape != null)
			{
				Color color = config.generalHighlight();
				if (state.getCrucibleCount() == CRUCIBLE_CAPACITY)
				{
					graphics.setColor(config.toolGood());
				}
				else
				{
					graphics.setColor(config.generalHighlight());
				}
				graphics.draw(shape);
				graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
				graphics.fill(shape);
			}
		}
		else if (config.highlightStyle() == HighlightStyle.HIGHLIGHT_BORDER)
		{
			Color color;
			if (state.getCrucibleCount() == CRUCIBLE_CAPACITY)
			{
				color = config.toolGood();
			}
			else
			{
				color = config.generalHighlight();
			}
			drawObjectOutline(graphics, crucible, color);
		}
	}

	private void drawMouldIfNotSet(Graphics2D graphics)
	{
		if (client.getWidget(WIDGET_PROGRESS_PARENT) != null
			|| client.getVarbitValue(SWORD_TYPE_1_VARBIT) == 0
			|| (client.getVarbitValue(VARBIT_GAME_STAGE) != 0
			&& client.getVarbitValue(VARBIT_GAME_STAGE) != 2))
		{
			return;
		}
		if (config.highlightStyle() == HighlightStyle.HIGHLIGHT_CLICKBOX)
		{
			Shape shape = mouldJig.getConvexHull();
			if (shape != null)
			{
				Color color = config.generalHighlight();
				graphics.setColor(color);
				graphics.draw(shape);
				graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
				graphics.fill(shape);
			}
		}
		else if (config.highlightStyle() == HighlightStyle.HIGHLIGHT_BORDER)
		{
			drawObjectOutline(graphics, mouldJig, config.generalHighlight());
		}

		if (config.drawMouldInfoOverlay())
		{
			CommissionType type1 = CommissionType.forVarbit(client.getVarbitValue(SWORD_TYPE_1_VARBIT));
			CommissionType type2 = CommissionType.forVarbit(client.getVarbitValue(SWORD_TYPE_2_VARBIT));
			String text = StringUtils.capitalize(type1.toString().toLowerCase()) + " " + StringUtils.capitalize(type2.toString().toLowerCase());
			LocalPoint textLocation = mouldJig.getLocalLocation();
			textLocation = new LocalPoint(textLocation.getX(), textLocation.getY());
			Point canvasLocation = Perspective.getCanvasTextLocation(client, graphics, textLocation, text, 100);
			canvasLocation = new Point(canvasLocation.getX(), canvasLocation.getY() + 10);
			OverlayUtil.renderTextLocation(graphics, canvasLocation, text, config.generalHighlight());
		}
	}

	private void drawKovacIfHandIn(Graphics2D graphics)
	{
		Widget handInWidget = client.getWidget(HAND_IN_WIDGET);
		if (handInWidget != null && !handInWidget.isHidden())
		{
			Shape shape = kovac.getConvexHull();
			if (shape != null)
			{
				Color color = config.generalHighlight();
				graphics.setColor(color);
				graphics.draw(shape);
				graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
				graphics.fill(shape);
			}
		}
	}

	private void drawActionOverlay(Graphics2D graphics, GameObject gameObject)
	{
		int actionsLeft = state.getActionsLeftInStage();
		int heatLeft = state.getActionsForHeatLevel();

		// Draw heat left
		if (config.drawHeatLeftOverlay())
		{
			String text = "Heat left: " + heatLeft;
			LocalPoint textLocation = gameObject.getLocalLocation();
			textLocation = new LocalPoint(textLocation.getX(), textLocation.getY());
			Point canvasLocation = Perspective.getCanvasTextLocation(client, graphics, textLocation, text, 250);
			OverlayUtil.renderTextLocation(graphics, canvasLocation, text, getHeatColor(actionsLeft, heatLeft));
		}
		if (config.drawActionLeftOverlay())
		// Draw actions left
		{
			String text = "Actions left: " + actionsLeft;
			LocalPoint textLocation = gameObject.getLocalLocation();
			textLocation = new LocalPoint(textLocation.getX(), textLocation.getY());
			Point canvasLocation = Perspective.getCanvasTextLocation(client, graphics, textLocation, text, 250);
			canvasLocation = new Point(canvasLocation.getX(), canvasLocation.getY() + 10);
			OverlayUtil.renderTextLocation(graphics, canvasLocation, text, getHeatColor(actionsLeft, heatLeft));
		}
	}
}
