package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.MetalBarType;
import lombok.Value;

@Value
public class FoundryRecommendation
{
	MetalBarType primary;
	int primaryCount;
	MetalBarType secondary;
	int secondaryCount;
	int swords;
	int metalScore;

	public String getRecipeText()
	{
		if (secondary == null)
		{
			return primaryCount + " " + displayName(primary);
		}

		return primaryCount + " " + displayName(primary) + " + " + secondaryCount + " " + displayName(secondary);
	}

	public String getSwordsText()
	{
		return swords + " sword" + (swords == 1 ? "" : "s");
	}

	private static String displayName(MetalBarType type)
	{
		switch (type)
		{
			case BRONZE:
				return "Bronze";
			case IRON:
				return "Iron";
			case STEEL:
				return "Steel";
			case MITHRIL:
				return "Mithril";
			case ADAMANT:
				return "Adamant";
			case RUNITE:
				return "Rune";
			default:
				throw new IllegalArgumentException("Unknown metal type: " + type);
		}
	}
}
