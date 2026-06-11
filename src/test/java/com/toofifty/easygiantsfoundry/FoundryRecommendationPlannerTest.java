package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.MetalBarType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FoundryRecommendationPlannerTest
{
	@Test
	public void calculatesKnownAdjacentAlloyScores()
	{
		assertEquals(65, FoundryRecommendationPlanner.getMetalScore(MetalBarType.STEEL, 14, MetalBarType.MITHRIL, 14));
		assertEquals(95, FoundryRecommendationPlanner.getMetalScore(MetalBarType.MITHRIL, 14, MetalBarType.ADAMANT, 14));
		assertEquals(130, FoundryRecommendationPlanner.getMetalScore(MetalBarType.ADAMANT, 14, MetalBarType.RUNITE, 14));
	}

	@Test
	public void calculatesEfficientSplitScores()
	{
		assertEquals(89, FoundryRecommendationPlanner.getMetalScore(MetalBarType.MITHRIL, 18, MetalBarType.ADAMANT, 10));
		assertEquals(118, FoundryRecommendationPlanner.getMetalScore(MetalBarType.ADAMANT, 19, MetalBarType.RUNITE, 9));
	}
}
