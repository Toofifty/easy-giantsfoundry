package com.toofifty.easygiantsfoundry;

public class HeatActionCalculator
{
	// Temperature increments taken from heating a preform from 0.
	// These do include the temperature drop every 2 ticks - but
	// as that'll happen anyway, this will work for getting the
	// approximate ticks required.
	// Doing it this way instead of reverse engineering the
	// temperature function may not be totally correct - but
	// as the range is small this will do just fine.
	private final static int[] SMALL_CHANGES = new int[]{
		0, 7, 14, 33, 46, 60, 77, 95, 116, 139, 166, 195,
		228, 264, 305, 349, 398, 450, 507, 568, 635, 706,
		783, 865, 954, 1000
	};

	// same as above, taken from dunking a preform
	private final static int[] LARGE_CHANGES = new int[]{
		0, 27, 57, 89, 126, 166, 211, 259, 312, 368, 430,
		496, 568, 644, 727, 815, 910
	};

	private static int getActionsRequired(int delta, int currentProgress, int[] changes)
	{
		// start counting from the current progress
		int actions = currentProgress;

		// if, for example, current progress puts us at +166
		// temperature, then the next change will not be +211 but
		// +55 (211 - 166)
		while ((changes[actions] - changes[currentProgress]) < delta) {
			actions++;
		}

		// Remove the current progress to get the actions that
		// are still required.
		// The final action in the loop will take us over the
		// target, so we decrement one too.
		return actions - currentProgress - 1;
	}

	public static int getLargeActionsRequired(int delta, int currentProgress)
	{
		return getActionsRequired(delta, currentProgress, LARGE_CHANGES);
	}

	public static int getSmallActionsRequired(int delta, int currentProgress)
	{
		return getActionsRequired(delta, currentProgress, SMALL_CHANGES);
	}
}
