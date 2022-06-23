package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class FoundryOverlay3D extends Overlay {

    private static final int BONUS_COLOR = 0xfcd703;
    private static final int BONUS_WIDGET = 49414148;

    private static final int HAND_IN_WIDGET = 49414221;
    private static final int FINISH_ANIM = 9457;

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
    private final EasyGiantsFoundryHelper helper;
    private final EasyGiantsFoundryConfig config;

    @Inject
    private FoundryOverlay3D(Client client, EasyGiantsFoundryState state, EasyGiantsFoundryHelper helper,
                             EasyGiantsFoundryConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.state = state;
        this.helper = helper;
        this.config = config;
    }

    private Color getObjectColor(Stage stage, Heat heat)
    {
        if (stage.getHeat() != heat)
        {
            return ColorScheme.PROGRESS_ERROR_COLOR;
        }

        Widget bonusWidget = client.getWidget(BONUS_WIDGET);
        if (bonusWidget != null
                && bonusWidget.getChildren() != null
                && bonusWidget.getChildren().length != 0
                && bonusWidget.getChild(0).getTextColor() == BONUS_COLOR) {
            return Color.CYAN;
        }


        int actionsLeft = helper.getActionsLeftInStage();
        int heatLeft = helper.getActionsForHeatLevel();
        if (actionsLeft <= 1 || heatLeft <= 1)
        {
            return ColorScheme.PROGRESS_INPROGRESS_COLOR;
        }

        return ColorScheme.PROGRESS_COMPLETE_COLOR;
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

        drawKovacIfHandIn(graphics);

        if (state.getCurrentStage() == null)
        {
            drawMouldIfNotSet(graphics);
            drawCrucibleIfMouldSet(graphics);
            return null;
        }

        Heat heat = state.getCurrentHeat();
        Stage stage = state.getCurrentStage();

        GameObject stageObject = getStageObject(stage);
        if (stageObject == null)
        {
            return null;
        }

        Color color = getObjectColor(stage, heat);
        Shape objectClickbox = stageObject.getClickbox();
        if (objectClickbox != null)
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

        if (color.equals(ColorScheme.PROGRESS_ERROR_COLOR))
        {
            drawHeatChangers(graphics);
        }

        return null;
    }

    private void drawHeatChangers(Graphics2D graphics)
    {
        int change = state.getHeatChangeNeeded();
        Shape shape = null;
        if (change < 0)
        {
            shape = waterfall.getClickbox();
        } else if (change > 0)
        {
            shape = lavaPool.getClickbox();
        }
        if (shape != null)
        {
            Point mousePosition = client.getMouseCanvasPosition();
            Color color = ColorScheme.PROGRESS_COMPLETE_COLOR;
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
        Shape shape = crucible.getConvexHull();
        if (shape != null)
        {
            Color color = Color.CYAN;
            graphics.setColor(color);
            graphics.draw(shape);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(shape);
        }
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
        Shape shape = mouldJig.getConvexHull();
        if (shape != null)
        {
            Color color = Color.CYAN;
            graphics.setColor(color);
            graphics.draw(shape);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(shape);
        }
    }

    private void drawKovacIfHandIn(Graphics2D graphics)
    {
        Widget handInWidget = client.getWidget(HAND_IN_WIDGET);
        if (handInWidget != null && !handInWidget.isHidden()
            && client.getLocalPlayer().getAnimation() != FINISH_ANIM)
        {
            Shape shape = kovac.getConvexHull();
            if (shape != null)
            {
                Color color = Color.CYAN;
                graphics.setColor(color);
                graphics.draw(shape);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                graphics.fill(shape);
            }
        }
    }
}
