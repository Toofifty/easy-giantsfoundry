package com.toofifty.easygiantsfoundry;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReputationTracker
{
	private static final int SHOP_WIDGET = 753;
	private static final int CHAT_WIDGET = 229;
	private static final int SHOP_POINTS_TEXT = 13;
	private static final int CHAT_POINTS_TEXT = 1;
	private static final Pattern pattern = Pattern.compile("quality: (?<points>\\d+) Best");

	@Getter
	private int shopPoints;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	public void load()
	{
		Integer points = configManager.getRSProfileConfiguration(EasyGiantsFoundryConfig.GROUP, EasyGiantsFoundryConfig.POINTS_KEY, int.class);
		if (points != null)
		{
			shopPoints = points;
		}
	}

	private void save()
	{
		configManager.setRSProfileConfiguration(EasyGiantsFoundryConfig.GROUP, EasyGiantsFoundryConfig.POINTS_KEY, shopPoints);
	}

	public void onWidgetLoaded(int groupId)
	{
		if (groupId == SHOP_WIDGET)
		{
			shopOpened();
		}
		else if (groupId == CHAT_WIDGET)
		{
			chatBox();
		}
	}

	private void chatBox()
	{
		Widget chat = client.getWidget(CHAT_WIDGET, CHAT_POINTS_TEXT);
		if (chat == null)
		{
			return;
		}

		String chatText = Text.sanitizeMultilineText(chat.getText());
		final Matcher matcher = pattern.matcher(chatText);
		if (matcher.find())
		{
			shopPoints += Integer.parseInt(matcher.group("points"));
			save();
		}
	}

	private void shopOpened()
	{
		Widget shop = client.getWidget(SHOP_WIDGET, SHOP_POINTS_TEXT);
		if (shop != null && shop.getText() != null && Integer.parseInt(shop.getText()) != shopPoints)
		{
			shopPoints = Integer.parseInt(shop.getText());
			save();
		}
	}
}
