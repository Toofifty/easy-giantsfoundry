package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class EasyGiantsFoundryState
{
	// heat and progress are from 0-1000
	private static final int VARBIT_HEAT = 13948;
	private static final int VARBIT_PROGRESS = 13949;

	private static final int VARBIT_ORE_COUNT = 13934;
	private static final int VARBIT_FORTE_SELECTED = 13910;
	private static final int VARBIT_BLADE_SELECTED = 13911;
	private static final int VARBIT_TIP_SELECTED = 13912;

	// 0 - load bars
	// 1 - set mould
	// 2 - collect preform
	// 3 -
	static final int VARBIT_GAME_STAGE = 13914;

	private static final int WIDGET_HEAT_PARENT = 49414153;
	private static final int WIDGET_LOW_HEAT_PARENT = 49414163;
	private static final int WIDGET_MED_HEAT_PARENT = 49414164;
	private static final int WIDGET_HIGH_HEAT_PARENT = 49414165;

	static final int WIDGET_PROGRESS_PARENT = 49414219;
	// children with type 3 are stage boxes
	// every 11th child is a sprite

	private static final int SPRITE_ID_TRIP_HAMMER = 4442;
	private static final int SPRITE_ID_GRINDSTONE = 4443;
	private static final int SPRITE_ID_POLISHING_WHEEL = 4444;

	@Inject
	private Client client;

	@Setter
	@Getter
	private boolean enabled;

	@Getter
	private int bonusActionsReceived = 0;

	private final List<Stage> stages = new ArrayList<>();
	private double heatRangeRatio = 0;

	public void reset()
	{
		stages.clear();
		heatRangeRatio = 0;
		bonusActionsReceived = 0;
	}

	public int getHeatAmount()
	{
		return client.getVarbitValue(VARBIT_HEAT);
	}

	public int getProgressAmount()
	{
		return client.getVarbitValue(VARBIT_PROGRESS);
	}

	public double getHeatRangeRatio()
	{
		if (heatRangeRatio == 0)
		{
			Widget heatWidget = client.getWidget(WIDGET_HEAT_PARENT);
			Widget medHeat = client.getWidget(WIDGET_MED_HEAT_PARENT);
			if (medHeat == null || heatWidget == null)
			{
				return 0;
			}

			heatRangeRatio = medHeat.getWidth() /(double) heatWidget.getWidth();
		}

		return heatRangeRatio;
	}

	public int[] getLowHeatRange()
	{
		return new int[]{
			(int) ((1 / 6d - getHeatRangeRatio() / 2) * 1000),
			(int) ((1 / 6d + getHeatRangeRatio() / 2) * 1000),
		};
	}

	public int[] getMedHeatRange()
	{
		return new int[]{
			(int) ((3 / 6d - getHeatRangeRatio() / 2) * 1000),
			(int) ((3 / 6d + getHeatRangeRatio() / 2) * 1000),
		};
	}

	public int[] getHighHeatRange()
	{
		return new int[]{
			(int) ((5 / 6d - getHeatRangeRatio() / 2) * 1000),
			(int) ((5 / 6d + getHeatRangeRatio() / 2) * 1000),
		};
	}

	public List<Stage> getStages()
	{
		if (stages.isEmpty())
		{
			Widget progressParent = client.getWidget(WIDGET_PROGRESS_PARENT);
			if (progressParent == null || progressParent.getChildren() == null)
			{
				return new ArrayList<>();
			}

			for (Widget child : progressParent.getChildren())
			{
				switch (child.getSpriteId())
				{
					case SPRITE_ID_TRIP_HAMMER:
						stages.add(Stage.TRIP_HAMMER);
						break;
					case SPRITE_ID_GRINDSTONE:
						stages.add(Stage.GRINDSTONE);
						break;
					case SPRITE_ID_POLISHING_WHEEL:
						stages.add(Stage.POLISHING_WHEEL);
						break;
				}
			}
		}

		return stages;
	}

	public Stage getCurrentStage()
	{
		int index = (int) (getProgressAmount() / 1000d * getStages().size());
		if (index < 0 || index > getStages().size() - 1)
		{
			return null;
		}

		return getStages().get(index);
	}

	public Heat getCurrentHeat()
	{
		int heat = getHeatAmount();

		int[] low = getLowHeatRange();
		if (heat > low[0] && heat < low[1])
		{
			return Heat.LOW;
		}

		int[] med = getMedHeatRange();
		if (heat > med[0] && heat < med[1])
		{
			return Heat.MED;
		}

		int[] high = getHighHeatRange();
		if (heat > high[0] && heat < high[1])
		{
			return Heat.HIGH;
		}

		return Heat.NONE;
	}

	public int getHeatChangeNeeded()
	{
		Heat requiredHeat = getCurrentStage().getHeat();
		int heat = getHeatAmount();

		int[] range;
		switch (requiredHeat)
		{
			case LOW:
				range = getLowHeatRange();
				break;
			case MED:
				range = getMedHeatRange();
				break;
			case HIGH:
				range = getHighHeatRange();
				break;
			default:
				return 0;
		}

		if (heat < range[0])
			return range[0] - heat;
		else if (heat > range[1])
			return range[1] - heat;
		else
			return 0;
	}

	public int getBonusActionsExpected()
	{
		if (getStages().size() >= 6) {
			return 3;
		}

		return 2;
	}

	public void addBonusActionReceived()
	{
		++bonusActionsReceived;
	}
}
