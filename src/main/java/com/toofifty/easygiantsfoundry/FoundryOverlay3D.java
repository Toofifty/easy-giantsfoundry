package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
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

    GameObject tripHammer;
    GameObject grindstone;
    GameObject polishingWheel;
    GameObject lavaPool;
    GameObject waterfall;

    private final Client client;
    private final EasyGiantsFoundryState state;
    private final EasyGiantsFoundryHelper helper;

    @Inject
    private FoundryOverlay3D(Client client, EasyGiantsFoundryState state, EasyGiantsFoundryHelper helper)
    {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.state = state;
        this.helper = helper;
    }

    private Color getObjectColor(Stage stage, Heat heat) {
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
    public Dimension render(Graphics2D graphics) {
        if (!state.isEnabled() || state.getCurrentStage() == null)
        {
            return null;
        }

        Heat heat = state.getCurrentHeat();
        Stage stage = state.getCurrentStage();

        GameObject stageObject = getStageObject(stage);
        if (stageObject != null)
        {
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
        }

        return null;
    }
}
