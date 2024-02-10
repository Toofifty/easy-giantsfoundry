package com.toofifty.easygiantsfoundry;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.ui.ColorScheme;

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


    @ConfigSection(
            name = "Info Panel",
            description = "Settings for the Info Panel overlay",
            position = 2
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
            keyName = "bonusActions",
            name = "Bonus Actions",
            description = "Toggle for Bonus actions text",
            position = 5,
            section = infoPanelList
    )
    default boolean drawBonusActions() {
        return true;
    }

    @ConfigItem(
            keyName = "shopPoints",
            name = "Reputation",
            description = "Toggle for reputation text",
            position = 6,
            section = infoPanelList
    )
    default boolean drawShopPoints()
    {
        return false;
    }


	@ConfigSection(
		name = "Colour",
		description = "Colours",
		position = 3
	)
	String colourList = "colourList";

	@ConfigItem(
		keyName = "mouldText",
		name = "Mould Text",
		description = "Colour for optimal mould text",
		position = 0,
		section = colourList
	)
	default Color mouldTextColour()
	{
		return new Color(0xdc10d);
	}


	@ConfigItem(
		keyName = "generalColour",
		name = "General",
		description = "Colour for highlighting objects/npcs in general",
		position = 1,
		section = colourList
	)
	default Color generalHighlight()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "lavaWaterColour",
		name = "Lava/Waterfall",
		description = "Colour for highlighting lava/waterfall",
		position = 2,
		section = colourList
	)
	default Color lavaWaterfallColour()
	{
		return ColorScheme.PROGRESS_COMPLETE_COLOR;
	}

	@ConfigItem(
		keyName = "toolGood",
		name = "Tool Good",
		description = "Colour for highlighting current tool when they are usable",
		position = 3,
		section = colourList
	)
	default Color toolGood()
	{
		return ColorScheme.PROGRESS_COMPLETE_COLOR;
	}

	@ConfigItem(
		keyName = "toolBad",
		name = "Tool Bad",
		description = "Colour for highlighting current tool when they are not usable",
		position = 4,
		section = colourList
	)
	default Color toolBad()
	{
		return ColorScheme.PROGRESS_ERROR_COLOR;
	}

	@ConfigItem(
		keyName = "toolCaution",
		name = "Tool Caution",
		description = "Colour for highlighting current tool when they are about to be not usable",
		position = 5,
		section = colourList
	)
	default Color toolCaution()
	{
		return ColorScheme.PROGRESS_INPROGRESS_COLOR;
	}

	@ConfigItem(
		keyName = "toolBonus",
		name = "Tool Bonus",
		description = "Colour for highlighting current tool when they have a bonus to click on",
		position = 6,
		section = colourList
	)
	default Color toolBonus()
	{
		return Color.CYAN;
	}
}
