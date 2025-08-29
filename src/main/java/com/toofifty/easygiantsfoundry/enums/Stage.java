package com.toofifty.easygiantsfoundry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Stage
{
	TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25, 4, 14, 5),
	GRINDSTONE("Grind", Heat.MED, 10, 15, 7, 19, 2),
	POLISHING_WHEEL("Polish", Heat.LOW, 10, -17, 12, 10, 2);

	private final String name;
	private final Heat heat;
	private final int progressPerAction;
	private final int heatChange;

	private final int distanceToLava;
	private final int distanceToWaterfall;

	// Ticks between action
	private final int toolTickCycle;

	public boolean isHeating()
	{
		return heatChange > 0;
	}

	public boolean isCooling()
	{
		return heatChange < 0;
	}

}
