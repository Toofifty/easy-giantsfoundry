package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.CommissionType;
import com.toofifty.easygiantsfoundry.enums.Mould;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class MouldHelper
{
    static final int MOULD_LIST_PARENT = 47054857;
    static final int DRAW_MOULD_LIST_SCRIPT = 6093;
    static final int REDRAW_MOULD_LIST_SCRIPT = 6095;
    static final int RESET_MOULD_SCRIPT = 6108;
    public static final int SELECT_MOULD_SCRIPT = 6098;
    static final int SWORD_TYPE_1_VARBIT = 13907; // 4=Broad
    static final int SWORD_TYPE_2_VARBIT = 13908; // 3=Flat
    private static final int DISABLED_TEXT_COLOR = 0x9f9f9f;

    @Inject
    private Client client;
    @Inject
    private EasyGiantsFoundryConfig config;

    @Inject
    private ClientThread clientThread;

    public void selectBest(int scriptId)
    {
        Widget parent = client.getWidget(MOULD_LIST_PARENT);
        if (parent == null || parent.getChildren() == null)
        {
            return;
        }

        Map<Mould, Widget> mouldToChild = getOptions(parent.getChildren());

        int bestScore = -1;
        Widget bestWidget = null;
        CommissionType type1 = CommissionType.forVarbit(client.getVarbitValue(SWORD_TYPE_1_VARBIT));
        CommissionType type2 = CommissionType.forVarbit(client.getVarbitValue(SWORD_TYPE_2_VARBIT));
        for (Map.Entry<Mould, Widget> entry : mouldToChild.entrySet()) {
            Mould mould = entry.getKey();
            int score = mould.getScore(type1, type2);
            if (score > bestScore) {
                bestScore = score;
                bestWidget = entry.getValue();
            }
        }
        if (bestWidget != null) {
            bestWidget.setTextColor(config.mouldHighlightColor().getRGB());
        }

        if (scriptId == DRAW_MOULD_LIST_SCRIPT || scriptId == REDRAW_MOULD_LIST_SCRIPT)
        {
            Widget scrollBar = client.getWidget(718, 11);
            Widget scrollList = client.getWidget(718, 9);
            if (scrollBar != null && scrollList != null)
            {
                int height = scrollList.getHeight();
                int scrollMax = scrollList.getScrollHeight();
                Widget finalBestWidget = bestWidget;
                clientThread.invokeLater(() -> {
                    if (finalBestWidget != null) {
                        client.runScript(
                                ScriptID.UPDATE_SCROLLBAR,
                                scrollBar.getId(),
                                scrollList.getId(),
                                Math.min(finalBestWidget.getOriginalY() - 2, scrollMax - height));
                    }
                });
            }
        }
    }

    private Map<Mould, Widget> getOptions(Widget[] children) {
        Map<Mould, Widget> mouldToChild = new LinkedHashMap<>();
        for (int i = 2; i < children.length; i += 17)
        {
            Widget child = children[i];
            Mould mould = Mould.forName(child.getText());
            if (mould != null && child.getTextColor() != DISABLED_TEXT_COLOR) {
                mouldToChild.put(mould, child);
            }
        }
        return mouldToChild;
    }
}
