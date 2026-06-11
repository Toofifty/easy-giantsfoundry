package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.MetalBarType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static com.toofifty.easygiantsfoundry.MathUtil.max1;

@Singleton
public class FoundryRecommendationPlanner
{
	private static final int SWORD_METAL_COUNT = 28;

	@Inject
	private MetalBarCounter metalBarCounter;

	public Optional<FoundryRecommendation> recommend(int smithingLevel)
	{
		Optional<FoundryRecommendation> best = Optional.empty();

		for (MetalBarType type : MetalBarType.values())
		{
			best = chooseBest(best, evaluate(smithingLevel, type, SWORD_METAL_COUNT, null, 0));
		}

		MetalBarType[] types = MetalBarType.values();
		for (int i = 0; i < types.length - 1; i++)
		{
			MetalBarType lower = types[i];
			MetalBarType higher = types[i + 1];
			for (int lowerCount = 1; lowerCount < SWORD_METAL_COUNT; lowerCount++)
			{
				best = chooseBest(best, evaluate(smithingLevel, lower, lowerCount, higher, SWORD_METAL_COUNT - lowerCount));
			}
		}

		return best;
	}

	private Optional<FoundryRecommendation> chooseBest(
		Optional<FoundryRecommendation> current,
		Optional<FoundryRecommendation> candidate)
	{
		if (candidate.isEmpty())
		{
			return current;
		}

		if (current.isEmpty())
		{
			return candidate;
		}

		return compare(candidate.get(), current.get()) > 0 ? candidate : current;
	}

	private int compare(FoundryRecommendation left, FoundryRecommendation right)
	{
		return Comparator
			.comparingInt((FoundryRecommendation recommendation) -> recommendation.getMetalScore() * recommendation.getSwords())
			.thenComparingInt(FoundryRecommendation::getMetalScore)
			.thenComparingInt(FoundryRecommendation::getSwords)
			.compare(left, right);
	}

	private Optional<FoundryRecommendation> evaluate(
		int smithingLevel,
		MetalBarType primary,
		int primaryCount,
		MetalBarType secondary,
		int secondaryCount)
	{
		if (smithingLevel < levelRequirement(primary) || (secondary != null && smithingLevel < levelRequirement(secondary)))
		{
			return Optional.empty();
		}

		int swords = metalBarCounter.getFoundryInput(primary) / primaryCount;
		if (secondary != null)
		{
			swords = Math.min(swords, metalBarCounter.getFoundryInput(secondary) / secondaryCount);
		}

		if (swords == 0)
		{
			return Optional.empty();
		}

		int metalScore = getMetalScore(primary, primaryCount, secondary, secondaryCount);

		return Optional.of(new FoundryRecommendation(
			primary,
			primaryCount,
			secondary,
			secondaryCount,
			swords,
			metalScore));
	}

	static int getMetalScore(MetalBarType primary, int primaryCount, MetalBarType secondary, int secondaryCount)
	{
		Double[] metals = new Double[] {
			getWeightedValue(primary, primaryCount),
			secondary == null ? 0d : getWeightedValue(secondary, secondaryCount)
		};

		Arrays.sort(metals, Collections.reverseOrder());

		return (int) ((10 * metals[0] + 10 * metals[1] + max1(metals[0]) * max1(metals[1])) / 10.0);
	}

	private static double getWeightedValue(MetalBarType type, int count)
	{
		return (10 * metalTier(type) * count) / (double) SWORD_METAL_COUNT;
	}

	private static int metalTier(MetalBarType type)
	{
		switch (type)
		{
			case BRONZE:
				return 1;
			case IRON:
				return 2;
			case STEEL:
				return 3;
			case MITHRIL:
				return 4;
			case ADAMANT:
				return 5;
			case RUNITE:
				return 6;
			default:
				throw new IllegalArgumentException("Unknown metal type: " + type);
		}
	}

	private static int levelRequirement(MetalBarType type)
	{
		switch (type)
		{
			case BRONZE:
			case IRON:
				return 15;
			case STEEL:
				return 30;
			case MITHRIL:
				return 50;
			case ADAMANT:
				return 70;
			case RUNITE:
				return 85;
			default:
				throw new IllegalArgumentException("Unknown metal type: " + type);
		}
	}
}
