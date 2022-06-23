package com.toofifty.easygiantsfoundry;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(EasyGiantsFoundryConfig.GROUP)
public interface EasyGiantsFoundryConfig extends Config {
    String GROUP = "easygiantsfoundry";

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
            keyName = "crucibleHighlight",
            name = "Highlight Crucible",
            description = "Highlight Crucible when it should be filled/poured",
            position = 3,
            section = highlightList
    )
    default boolean highlightCrucible() {
        return true;
    }

    @ConfigItem(
            keyName = "kovacHighlight",
            name = "Highlight Kovac for hand in",
            description = "Highlight Kovac when sword can be handed in",
            position = 4,
            section = highlightList
    )
    default boolean highlightKovac() {
        return true;
    }
}
