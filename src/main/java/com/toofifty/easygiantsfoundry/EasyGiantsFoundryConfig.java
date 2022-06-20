package com.toofifty.easygiantsfoundry;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(EasyGiantsFoundryConfig.GROUP)
public interface EasyGiantsFoundryConfig extends Config {
    String GROUP = "easygiantsfoundry";

    @ConfigItem(
            keyName = "giantsFoundryStageNotification",
            name = "Enable stage notifications",
            description = "Configures whether to notify you when you are about to finish a stage.",
            position = 0
    )
    default boolean showGiantsFoundryNotifications()
    {
        return true;
    }

}
