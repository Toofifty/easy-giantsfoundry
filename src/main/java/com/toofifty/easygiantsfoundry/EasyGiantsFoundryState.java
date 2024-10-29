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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
public class EasyGiantsFoundryState
{

	@Inject
	private Client client;

	@Setter
	@Getter
	private boolean enabled;

	@Getter
	private int bonusActionsReceived = 0;

	@Setter
	private int smithsOutfitPieces;

	@Setter
	@Getter
	private int mouldScore = -1; // starts -1 because mould score is unknown

	@Setter
	@Getter
	private int lastKnownCrucibleScore = -1; // will be set when "Pour"ed

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

	public int getBonusActionsExpected()
	{
		if (getStages().size() >= 6)
		{
			return 3;
		}

		return 2;
	}

	public void addBonusActionReceived()
	{
		++bonusActionsReceived;
	}

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

	public double getCrucibleScore()
	{
		// https://oldschool.runescape.wiki/w/Giants%27_Foundry#Metal_score

		if (getCrucibleCount() == 0) return 0;

		final int BRONZE_VALUE = 1;
		final int IRON_VALUE = 2;
		final int STEEL_VALUE = 3;
		final int MITHRIL_VALUE = 4;
		final int ADAMANT_VALUE = 5;
		final int RUNE_VALUE = 6;


		final int bronzeNum = client.getVarbitValue(VARBIT_BRONZE_COUNT);
		final int ironNum = client.getVarbitValue(VARBIT_IRON_COUNT);
		final int steelNum = client.getVarbitValue(VARBIT_STEEL_COUNT);
		final int mithrilNum = client.getVarbitValue(VARBIT_MITHRIL_COUNT);
		final int adamantNum = client.getVarbitValue(VARBIT_ADAMANT_COUNT);
		final int runeNum = client.getVarbitValue(VARBIT_RUNE_COUNT);

		final double bronzeVal = (10 * BRONZE_VALUE * bronzeNum) / 28.0;
		final double ironVal = (10 * IRON_VALUE * ironNum) / 28.0;
		final double steelVal = (10 * STEEL_VALUE * steelNum) / 28.0;
		final double mithrilVal = (10 * MITHRIL_VALUE * mithrilNum) / 28.0;
		final double adamantVal = (10 * ADAMANT_VALUE * adamantNum) / 28.0;
		final double runeVal = (10 * RUNE_VALUE * runeNum) / 28.0;

		Double[] metals = new Double[] {
			bronzeVal,
			ironVal,
			steelVal,
			mithrilVal,
			adamantVal,
			runeVal
		};

		// Descending order
		Arrays.sort(metals, Collections.reverseOrder());

		return
			((10 * metals[0] + 10 * metals[1]) + max1(metals[0]) * max1(metals[1])) / 10.0;
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
		// Each Smith's Outfit piece adds 20% chance to increase action progress by 1, or 100% for all 4 pieces.
		double smithsOutfitBonus = smithsOutfitPieces == 4 ? 1 : 0.2 * smithsOutfitPieces;
		return (int) Math.ceil(progressTillNext / (current.getProgressPerAction() + smithsOutfitBonus));
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
