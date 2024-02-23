package com.toofifty.easygiantsfoundry;

import static com.toofifty.easygiantsfoundry.MathUtil.max1;
import static com.toofifty.easygiantsfoundry.EasyGiantsFoundryClientIDs.*;

import com.toofifty.easygiantsfoundry.enums.Heat;
import com.toofifty.easygiantsfoundry.enums.Stage;
import static com.toofifty.easygiantsfoundry.enums.Stage.GRINDSTONE;
import static com.toofifty.easygiantsfoundry.enums.Stage.POLISHING_WHEEL;
import static com.toofifty.easygiantsfoundry.enums.Stage.TRIP_HAMMER;
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

	@Inject
	private Client client;

	@Setter
	@Getter
	private boolean enabled;

	private final List<Stage> stages = new ArrayList<>();
	private double heatRangeRatio = 0;

	public void reset()
	{
		stages.clear();
		heatRangeRatio = 0;
	}

	public int getBarCount()
	{
		return client.getVarbitValue(VARBIT_STEEL_COUNT);
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

			heatRangeRatio = medHeat.getWidth() / (double) heatWidget.getWidth();
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
						stages.add(TRIP_HAMMER);
						break;
					case SPRITE_ID_GRINDSTONE:
						stages.add(GRINDSTONE);
						break;
					case SPRITE_ID_POLISHING_WHEEL:
						stages.add(POLISHING_WHEEL);
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


//		boolean valid = false;
//
//		int bronze = 0;
//		int iron = 0;
//		int steel = 0;
//		int mithril = 0;
//		int adamant = 0;
//		int rune = 0;
//		// Currently 28, will prob always be 28, but I want to future-proof this
//		int capacity = 28;
//
//		public boolean parseCrucibleText(String text)
//		{
//			if (!text.startsWith("The crucible currently contains"))
//			{
//				return false;
//			}
//			String[] parts = text.split("<br>");
//			capacity = Integer.parseInt(parts[0].split(" / ")[1].split(" ")[0]);
//
//			String[] counts = (parts[1] + parts[2]).split(", ");
//			bronze = Integer.parseInt(counts[0].split(" x ")[0]);
//			iron = Integer.parseInt(counts[1].split(" x ")[0]);
//			steel = Integer.parseInt(counts[2].split(" x ")[0]);
//			mithril = Integer.parseInt(counts[3].split(" x ")[0]);
//			adamant = Integer.parseInt(counts[4].split(" x ")[0]);
//			rune = Integer.parseInt(counts[5].split(" x ")[0]);
//
//			valid = true;
//			return true;
//		}

//		public String toString()
//		{
//			return String.format("[Capacity %d/%d. Total Value %d] Bronze: %d, Iron: %d, Steel: %d, Mithril: %d, Adamant: %d, Rune: %d.",
//				bronze, iron, steel, mithril, adamant, rune, count(), capacity, value());
//		}


	public int getCrucibleCount()
	{
		int bronze = client.getVarbitValue(VARBIT_BRONZE_COUNT);
		int iron = client.getVarbitValue(VARBIT_IRON_COUNT);
		int steel = client.getVarbitValue(VARBIT_STEEL_COUNT);
		int mithril = client.getVarbitValue(VARBIT_MITHRIL_COUNT);
		int adamant = client.getVarbitValue(VARBIT_ADAMANT_COUNT);
		int rune = client.getVarbitValue(VARBIT_RUNE_COUNT);

		return bronze + iron + steel + mithril + adamant + rune;
	}

	public double getCrucibleQuality()
	{
		if (getCrucibleCount() == 0) return 0;

		int bronze = client.getVarbitValue(VARBIT_BRONZE_COUNT);
		int iron = client.getVarbitValue(VARBIT_IRON_COUNT);
		int steel = client.getVarbitValue(VARBIT_STEEL_COUNT);
		int mithril = client.getVarbitValue(VARBIT_MITHRIL_COUNT);
		int adamant = client.getVarbitValue(VARBIT_ADAMANT_COUNT);
		int rune = client.getVarbitValue(VARBIT_RUNE_COUNT);

		final int BRONZE_VALUE = 1;
		final int IRON_VALUE = 2;
		final int STEEL_VALUE = 3;
		final int MITHRIL_VALUE = 4;
		final int ADAMANT_VALUE = 5;
		final int RUNE_VALUE = 6;

		final double vB = (10 * BRONZE_VALUE * bronze) / 28.0;
		final double vI = (10 * IRON_VALUE * iron) / 28.0;
		final double vS = (10 * STEEL_VALUE * steel) / 28.0;
		final double vM = (10 * MITHRIL_VALUE * mithril) / 28.0;
		final double vA = (10 * ADAMANT_VALUE * adamant) / 28.0;
		final double vR = (10 * RUNE_VALUE * rune) / 28.0;

		return
			(10 * (vB + vI + vS + vM + vA + vR)
				+ (max1(vB) * max1(vI) * max1(vS) * max1(vM) * max1(vA) * max1(vR))) / 10.0;
	}

	/**
	 * Get the amount of progress each stage needs
	 */
	public double getProgressPerStage()
	{
		return 1000d / getStages().size();
	}

	public int getActionsLeftInStage()
	{
		int progress = getProgressAmount();
		double progressPerStage = getProgressPerStage();
		double progressTillNext = progressPerStage - progress % progressPerStage;

		Stage current = getCurrentStage();
		return (int) Math.ceil(progressTillNext / current.getProgressPerAction());
	}

	public int[] getCurrentHeatRange()
	{
		switch (getCurrentStage())
		{
			case POLISHING_WHEEL:
				return getLowHeatRange();
			case GRINDSTONE:
				return getMedHeatRange();
			case TRIP_HAMMER:
				return getHighHeatRange();
			default:
				return new int[]{0, 0};
		}
	}

	/**
	 * Get the amount of current stage actions that can be
	 * performed before the heat drops too high or too low to
	 * continue
	 */
	public int getActionsForHeatLevel()
	{
		Heat heatStage = getCurrentHeat();
		Stage stage = getCurrentStage();
		if (heatStage != stage.getHeat())
		{
			// not the right heat to start with
			return 0;
		}

		int[] range = getCurrentHeatRange();
		int actions = 0;
		int heat = getHeatAmount();
		while (heat > range[0] && heat < range[1])
		{
			actions++;
			heat += stage.getHeatChange();
		}

		return actions;
	}

	public HeatActionStateMachine heatingCoolingState = new HeatActionStateMachine();

}
