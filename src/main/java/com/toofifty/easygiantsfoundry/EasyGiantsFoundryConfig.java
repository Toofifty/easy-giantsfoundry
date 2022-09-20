package com.toofifty.easygiantsfoundry;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup(EasyGiantsFoundryConfig.GROUP)
public interface EasyGiantsFoundryConfig extends Config {
    String GROUP = "easygiantsfoundry";
    String SOUND_ID = "soundID";
    String POINTS_KEY = "easygiantsfoundrypoints";

    @ConfigSection(
            name = "Notifications",
            description = "Notifications",
            position = 0
    )
    String notificationList = "notificationList";

    @ConfigItem(
            keyName = "giantsFoundryStageNotification",
            name = "Notify stage changes",
            description = "Notifies just before completing a stage",
            position = 0,
            section = notificationList
    )
    default boolean showGiantsFoundryStageNotifications() {
        return true;
    }

    @ConfigItem(
            keyName = "giantsFoundryHeatNotification",
            name = "Notify heat changes",
            description = "Notifies just before overheating/cooling when using tools",
            position = 1,
            section = notificationList
    )
    default boolean showGiantsFoundryHeatNotifications() {
        return true;
    }

    @ConfigItem(
            keyName = "giantsFoundryStageThreshold",
            name = "Stage threshold notification",
            description = "The number of actions left required for the notification.",
            position = 2,
            section = notificationList
    )
    default int StageNotificationsThreshold() {
        return 1;
    }

    @ConfigItem(
            keyName = "giantsFoundryHeatThreshold",
            name = "Heat threshold notification",
            description = "The heat level left required for the notification.",
            position = 3,
            section = notificationList
    )
    default int HeatNotificationsThreshold() {
        return 1;
    }

    @ConfigItem(
            keyName = "bonusNotification",
            name = "Notify bonus",
            description = "Notifies when bonus appears",
            position = 4,
            section = notificationList
    )
    default boolean bonusNotification() {
        return false;
    }

    @ConfigItem(
            keyName = "bonusSound",
            name = "Bonus sound",
            description = "Plays a sound when bonus appears",
            position = 5,
            section = notificationList
    )
    default boolean bonusSoundNotify() {
        return true;
    }

    @ConfigItem(
            keyName = SOUND_ID,
            name = "Bonus sound ID",
            description = "Sound Effect ID to play when bonus appears",
            position = 6,
            section = notificationList
    )
    default int soundId() {
        return 4212;
    }


    @ConfigSection(
            name = "Highlights",
            description = "3D npc/object highlights",
            position = 1
    )
    String highlightList = "highlightList";

    @ConfigItem(
            keyName = "toolsHighlight",
            name = "Highlight Tools",
            description = "Highlights current tool with symbolic colors",
            position = 0,
            section = highlightList
    )
    default boolean highlightTools() {
        return true;
    }

    @ConfigItem(
            keyName = "waterLavaHighlight",
            name = "Highlight Waterfall/Lava Pool",
            description = "Highlight Lava Pool / Waterfall when heat change required",
            position = 1,
            section = highlightList
    )
    default boolean highlightWaterAndLava() {
        return true;
    }

    @ConfigItem(
            keyName = "mouldHighlight",
            name = "Highlight Mould",
            description = "Highlight Mould when it should be clicked",
            position = 2,
            section = highlightList
    )
    default boolean highlightMould() {
        return true;
    }

    @ConfigItem(
            keyName = "mouldHighlightColor",
            name = "Highlighted Mould Color",
            description = "Highlight color of the mould",
            position = 3,
            section = highlightList
    )
    default Color mouldHighlightColor() {
        return new Color(13, 193, 13);
    }


    @ConfigItem(
            keyName = "crucibleHighlight",
            name = "Highlight Crucible",
            description = "Highlight Crucible when it should be filled/poured",
            position = 4,
            section = highlightList
    )
    default boolean highlightCrucible() {
        return true;
    }

    @ConfigItem(
            keyName = "kovacHighlight",
            name = "Highlight Kovac for hand in",
            description = "Highlight Kovac when sword can be handed in",
            position = 5,
            section = highlightList
    )
    default boolean highlightKovac() {
        return true;
    }


    @ConfigSection(
            name = "Info Panel",
            description = "Settings for the Info Panel overlay",
            position = 1
    )
    String infoPanelList = "infoPanelList";

    @ConfigItem(
            keyName = "infoTitle",
            name = "Title",
            description = "Toggle for \"Easy Giant's Foundry\" text",
            position = 0,
            section = infoPanelList
    )
    default boolean drawTitle() {
        return true;
    }

    @ConfigItem(
            keyName = "heatInfo",
            name = "Heat",
            description = "Toggle for Heat text",
            position = 1,
            section = infoPanelList
    )
    default boolean drawHeatInfo() {
        return true;
    }

    @ConfigItem(
            keyName = "stageInfo",
            name = "Stage",
            description = "Toggle for Stage text",
            position = 2,
            section = infoPanelList
    )
    default boolean drawStageInfo() {
        return true;
    }

    @ConfigItem(
            keyName = "actionsLeft",
            name = "Actions Left",
            description = "Toggle for Actions left text",
            position = 3,
            section = infoPanelList
    )
    default boolean drawActionsLeft() {
        return true;
    }

    @ConfigItem(
            keyName = "heatLeft",
            name = "Heat Left",
            description = "Toggle for Heat left text",
            position = 4,
            section = infoPanelList
    )
    default boolean drawHeatLeft() {
        return true;
    }

    @ConfigItem(
            keyName = "shopPoints",
            name = "Reputation",
            description = "Toggle for reputation text",
            position = 5,
            section = infoPanelList
    )
    default boolean drawShopPoints()
    {
        return false;
    }
}
