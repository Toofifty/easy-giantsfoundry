package com.toofifty.easygiantsfoundry;

import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryClientIDs.*;
import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryHelper.getHeatColor;
import static com.toofifty.easygiantsfoundry.MouldHelper.SWORD_TYPE_1_VARBIT;
import static com.toofifty.easygiantsfoundry.MouldHelper.SWORD_TYPE_2_VARBIT;
import com.toofifty.easygiantsfoundry.enums.CommissionType;
import com.toofifty.easygiantsfoundry.enums.Stage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
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
	private static final int CRUCIBLE_CAPACITY = 28;
	private final ModelOutlineRenderer modelOutlineRenderer;

	GameObject tripHammer;
	GameObject grindstone;
	GameObject polishingWheel;
	GameObject lavaPool;
	GameObject waterfall;
	GameObject mouldJig;
	GameObject crucible;
	GameObject storage;
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

	private Color getToolColor()
	{
		if (state.getCurrentStage().getHeat() != state.getCurrentHeat())
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

	private GameObject getStageGameObject(Stage stage)
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

		if (client.getVarbitValue(VARBIT_PREFORM_STORED) == 1)
		{
			if (config.highlightStorage())
			{
				drawStorage(graphics);
			}
			return null;
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

		if (config.drawHeatLeftOverlay())
		{
			drawHeatLeftInfo(graphics);
		}

		if (config.drawActionLeftOverlay())
		{
			drawActionLeftInfo(graphics);
		}

		if (config.highlightTools())
		{
			drawToolHighlight(graphics);
		}

		drawHeatChangersIfEnabled(graphics);


		return null;
	}

	private void drawToolHighlight(Graphics2D graphics)
	{
		Color color = getToolColor();
		GameObject stageObject = getStageGameObject(state.getCurrentStage());
		if (config.highlightStyle() == HighlightStyle.HIGHLIGHT_CLICKBOX)
		{
			drawObjectClickbox(graphics, stageObject, color);
		}
		else
		{
			drawObjectOutline(stageObject, color);
		}
	}

	private void drawHeatChangersIfEnabled(Graphics2D graphics)
	{

		if (state.heatChangerStateMachine.isCooling())
		{
			_drawHeatChangerStateMachineIfEnabled(graphics, waterfall);
			return;
		}

		if (state.heatChangerStateMachine.isHeating())
		{
			_drawHeatChangerStateMachineIfEnabled(graphics, lavaPool);
			return;
		}

		if (state.heatChangerStateMachine.isPending())
		{
			final boolean isLava = state.heatChangerStateMachine.actionHeating;
			final GameObject gameObject = isLava ? lavaPool : waterfall;
			_drawHeatChangerPreviewIfEnabled(graphics, isLava);
			return;
		}

		final boolean needLava = state.getHeatChangeNeeded() > 0;
		final boolean needWaterfall = state.getHeatChangeNeeded() < 0;

		if (needWaterfall)
		{
			_drawHeatChangerPreviewIfEnabled(graphics, false);
			return;
		}
		else if (needLava)
		{
			_drawHeatChangerPreviewIfEnabled(graphics, true);
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();
		if (menuEntries.length != 0)
		{
			MenuEntry hoveredMenu = menuEntries[menuEntries.length - 1];

			if (hoveredMenu.getIdentifier() == lavaPool.getId())
			{
				_drawHeatChangerPreviewIfEnabled(graphics, true);
				return;
			}

			if (hoveredMenu.getIdentifier() == waterfall.getId())
			{
				_drawHeatChangerPreviewIfEnabled(graphics, false);
				return;
			}
		}

	}

	/**
	 * Private helper for drawHeatChangersIfEnabled
	 * An live updating solve for preview before the statemachine has started. Shows both fast and slow.
	 * Draws both clickbox & info, depending on plugin config.
	 */
	private void _drawHeatChangerPreviewIfEnabled(Graphics2D graphics, boolean isLava)
	{

		// early exit if we're not rendering anything
		if (!config.highlightWaterAndLava() && !config.drawLavaWaterInfoOverlay())
		{
			return;
		}

		GameObject gameObject = isLava ? lavaPool : waterfall;

		HeatActionSolver.DurationResult fastResult =
			HeatActionSolver.solve(
				state.getCurrentStage(),
				state.getCurrentHeatRange(),
				state.getActionsLeftInStage(),
				state.getHeatAmount(),
				true,
				isLava,
				config.heatActionPadTicks(),
				state.isPlayerRunning()
			);

		final int fastDuration = fastResult.getDuration();
		HeatActionSolver.DurationResult slowResult =
			HeatActionSolver.solve(
				state.getCurrentStage(),
				state.getCurrentHeatRange(),
				state.getActionsLeftInStage(),
				state.getHeatAmount(),
				false,
				isLava,
				config.heatActionPadTicks(),
				state.isPlayerRunning()
			);
		final int slowDuration = slowResult.getDuration();

		if (fastDuration == 0 && slowDuration == 0)
		{
			return;
		}

		if (config.highlightWaterAndLava())
		{
			drawObjectClickbox(graphics, gameObject, config.lavaWaterfallColour());
		}

		if (config.drawLavaWaterInfoOverlay())
		{
			final String fastName = isLava ? "Dunks" : "Quenches";
			final String slowName = isLava ? "Heats" : "Cools";

			String text;
			if (config.debugging())
			{
				text = String.format("%d %s (predicted: %d) or %d %s (predicted: %d) (overshoot: %s goal-in-range: %s)",
					fastDuration, fastName, fastResult.getPredictedHeat(), slowDuration, slowName, slowResult.getPredictedHeat(), slowResult.isOvershooting(), fastResult.isGoalInRange());
			}
			else
			{
				text = String.format("%d %s or %d %s ",
					fastDuration, fastName, slowDuration, slowName);
			}

			LocalPoint stageLoc = gameObject.getLocalLocation();
			stageLoc = new LocalPoint(stageLoc.getX(), stageLoc.getY());

			Point pos = Perspective.getCanvasTextLocation(client, graphics, stageLoc, text, 50);
			if (pos == null)
			{
				return;
			}

			OverlayUtil.renderTextLocation(graphics, pos, text, config.lavaWaterfallColour());
		}

	}

	/**
	 * Private helper for drawHeatChangersIfEnabled.
	 * State Machine solves once and tracks the progress over time. Only draws the ongoing action for clarity.
	 * Draws both clickbox & info, depending on plugin config.
	 * @param gameObject Lava/Waterfall gameObject
	 */
	private void _drawHeatChangerStateMachineIfEnabled(Graphics2D graphics, GameObject gameObject)
	{
		if (config.highlightWaterAndLava())
		{
			drawObjectClickbox(graphics, gameObject, config.lavaWaterfallColour());
		}

		if (config.drawLavaWaterInfoOverlay())
		{
			String text;
			if (config.debugging())
			{
				text = String.format("%d %s (overshoot: %s) [goal-in-range: %s]",
					state.heatChangerStateMachine.getRemainingDuration(),
					state.heatChangerStateMachine.getActionName(),
					state.heatChangerStateMachine.isOverShooting(),
					state.heatChangerStateMachine.isGoalInRange()
				);
			}
			else
			{
				text = String.format("%d %s",
					state.heatChangerStateMachine.getRemainingDuration(),
					state.heatChangerStateMachine.getActionName()
				);
			}

			LocalPoint stageLoc = gameObject.getLocalLocation();
			stageLoc = new LocalPoint(stageLoc.getX(), stageLoc.getY());

			Point pos = Perspective.getCanvasTextLocation(client, graphics, stageLoc, text, 50);

			OverlayUtil.renderTextLocation(graphics, pos, text, config.lavaWaterfallColour());
		}
	}



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
			drawObjectOutline(crucible, color);
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
			drawObjectOutline(mouldJig, config.generalHighlight());
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

	private void drawStorage(Graphics2D graphics)
	{
		Shape shape = storage.getConvexHull();
		if (shape != null)
		{
			Color color = config.generalHighlight();
			graphics.setColor(color);
			graphics.draw(shape);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(shape);
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

	private void drawActionLeftInfo(Graphics2D graphics)
	{
		Stage stage = state.getCurrentStage();
		GameObject stageObject = getStageGameObject(stage);

		int actionsLeft = state.getActionsLeftInStage();
		int heatLeft = state.getActionsForHeatLevel();
		String text = "Actions left: " + actionsLeft;
		LocalPoint textLocation = stageObject.getLocalLocation();
		textLocation = new LocalPoint(textLocation.getX(), textLocation.getY());
		Point canvasLocation = Perspective.getCanvasTextLocation(client, graphics, textLocation, text, 250);
		if (canvasLocation == null)
		{
			return;
		}
		canvasLocation = new Point(canvasLocation.getX(), canvasLocation.getY() + 10);
		OverlayUtil.renderTextLocation(graphics, canvasLocation, text, getHeatColor(actionsLeft, heatLeft));
	}

	private void drawHeatLeftInfo(Graphics2D graphics)
	{
		Stage stage = state.getCurrentStage();
		GameObject stageObject = getStageGameObject(stage);

		int actionsLeft = state.getActionsLeftInStage();
		int heatLeft = state.getActionsForHeatLevel();
		String text = "Heat left: " + heatLeft;
		LocalPoint textLocation = stageObject.getLocalLocation();
		textLocation = new LocalPoint(textLocation.getX(), textLocation.getY());
		Point canvasLocation = Perspective.getCanvasTextLocation(client, graphics, textLocation, text, 250);
		if (canvasLocation == null)
		{
			return;
		}
		OverlayUtil.renderTextLocation(graphics, canvasLocation, text, getHeatColor(actionsLeft, heatLeft));
	}

	private void drawObjectClickbox(Graphics2D graphics, GameObject stageObject, Color color)
	{
		if (stageObject == null)
		{
			return;
		}

		Shape objectClickbox = stageObject.getClickbox();
		if (objectClickbox == null)
		{
			return;
		}

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

	private void drawObjectOutline(GameObject stageObject, Color color)
	{
		Color _color = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.borderAlpha());
		modelOutlineRenderer.drawOutline(stageObject, config.borderThickness(), _color, config.borderFeather());
	}

}
