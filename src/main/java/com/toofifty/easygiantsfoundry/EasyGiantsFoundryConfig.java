package com.toofifty.easygiantsfoundry;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(EasyGiantsFoundryConfig.GROUP)
public interface EasyGiantsFoundryConfig extends Config {
    String GROUP = "easygiantsfoundry";

    @ConfigItem(
            keyName = "giantsFoundryStageNotification",
            name = "Notify stage changes",
            description = "Notifies just before completing a stage",
            position = 0
    )
    default boolean showGiantsFoundryStageNotifications()
    {
        return false;
    }

    @ConfigItem(
            keyName = "giantsFoundryHeatNotification",
            name = "Notify heat changes",
            description = "Notifies just before overheating/cooling when using tools",
            position = 1
    )
    default boolean showGiantsFoundryHeatNotifications()
    {
        return false;
    }

}
