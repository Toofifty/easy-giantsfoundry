package com.toofifty.easygiantsfoundry;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

public class BonusWidget {
    private static final int BONUS_WIDGET = 49414148;
    private static final int BONUS_COLOR = 0xfcd703;

    static boolean isActive(Client client) {
        Widget bonusWidget = client.getWidget(BONUS_WIDGET);
        return bonusWidget != null
                && bonusWidget.getChildren() != null
                && bonusWidget.getChildren().length != 0
                && bonusWidget.getChild(0).getTextColor() == BONUS_COLOR;
    }
}
