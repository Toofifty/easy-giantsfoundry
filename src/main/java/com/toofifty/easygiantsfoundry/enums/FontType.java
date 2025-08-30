package com.toofifty.easygiantsfoundry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

import java.awt.Font;

@Getter
@AllArgsConstructor
public enum FontType
{
	DEFAULT("Default", null),
	REGULAR("Regular", FontManager.getRunescapeFont()),
	BOLD("Bold", FontManager.getRunescapeBoldFont()),
	SMALL("Small", FontManager.getRunescapeSmallFont()),
	;

	private final String name;
	private final Font font;

	@Override
	public String toString()
	{
		return name;
	}
}
