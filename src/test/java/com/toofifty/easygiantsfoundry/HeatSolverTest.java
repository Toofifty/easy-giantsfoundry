package com.toofifty.easygiantsfoundry;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

// playground to test HeatSolver
public class HeatSolverTest
{
//	@Test
//	public void TestHeatSolver_dx2_Iterative()
//	{
//		final int[] answer =
//			{1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6};
//		List<Integer> produced = HeatActionSolver.generateDx2List(answer.length);
//
//		System.err.println("Expected Length: " + answer.length + " Length: " + produced.size());
//		// print produced
//		for (Integer integer : produced)
//		{
//			System.err.print(integer + ",");
//		}
//		System.err.println();
//		// compare
//		for (int i = 0; i < answer.length; i++)
//		{
//			assertEquals("Asserting dx2 n=" + i, answer[i], produced.get(i).intValue());
//		}
//	}

	@Test
	public void TestHeatSolver_dx2_Numerical()
	{
		final int[] answer =
			{1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6};
		// test getDx2AtIndex
		for (int i = 0; i < answer.length; i++)
		{
			assertEquals("Asserting dx2 n=" + i, answer[i], HeatActionSolver.getDx2AtIndex(i));
		}
	}

	@Test
	public void TestHeatSolver_dx1()
	{
		final int c = 7;
		final int[] answer =
//          {1,1,2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6};
			{7, 8, 9, 11, 13, 15, 17, 19, 21, 24, 27, 30, 33, 37, 41, 45, 49, 53, 57, 62, 67, 72, 77, 83, 89};
		for (int i = 0; i < answer.length; i++)
		{
			assertEquals("Asserting dx1 n=" + i + " c=" + c + " answer=" + answer[i], answer[i], HeatActionSolver.getDx1AtIndex(i, c));
		}
	}

	@Test
	public void TestHeatSolver_dx2_from_groundtruth()
	{
		// runelite-shell script for retrieving heating/cooling delta.
		// copy-paste into developer-tools -> Shell


		// ground-truth answer from game
		final int[] answer =
			{7, 8, 9, 11, 13, 15, 17, 19, 21, 24, 27, 30, 33, 37, 41, 45, 49, 53, 57, 62, 67, 72, 77, 83, 89};
		for (int i = 0; i < answer.length - 1; i++)
		{
			System.err.print(answer[i + 1] - answer[i] + ",");
		}
	}

	@Test
	public void TestHeatSolver_Dx0()
	{
		final int[] answer_dx1 =
			{7, 8, 9, 11, 13, 15, 17, 19, 21, 24, 27, 30, 33, 37, 41, 45, 49, 53, 57, 62, 67, 72, 77, 83, 89};
		List<Integer> answer_dx0 = new ArrayList<>();


		int sum = 0;
		for (int i = 0; i < answer_dx1.length; i++)
		{
			sum += answer_dx1[i];
			answer_dx0.add(sum);
		}

		System.err.println(answer_dx0);

		for (int i = 0; i < answer_dx1.length; i++)
		{
			TestHeatSolver_Dx0_Helper(answer_dx0.get(i), answer_dx0.get(0), i);
		}
	}

	@Test
	public void TestHeatSolver_Dx0_Manual()
	{
		for (int i = 0; i < 50; i++)
		{
			System.err.println("[" + (350 + i) + "]" + HeatActionSolver.findDuration(350 + i, 7, 0));
		}
	}

	@Test
	public void TestHeatSolver_Dx0_2()
	{
// 7->1,15->2,24->3,35->4,48->5,63->6,80->7,99->8,120->9,144->10,171->11,201->12,234->13,271->14,312->15,357->16,406->17,459->18,516->19,578->20,645->21,717->22,794->23,877->24,966->25
		final int[] answer_dx1 =
			{7, 8, 9, 11, 13, 15, 17, 19, 21, 24, 27, 30, 33, 37, 41, 45, 49, 53, 57, 62, 67, 72, 77, 83, 89};
		List<Integer> answer_dx0 = new ArrayList<>();


		int sum = 0;
		for (int i = 0; i < answer_dx1.length; i++)
		{
			sum += answer_dx1[i];
			answer_dx0.add(sum);
		}

		System.err.println(answer_dx0);

//		System.err.println(
//			HeatSolver.findDx0IndexContinue(406, 7, 0));
//		System.err.println(
//			HeatSolver.findDx0IndexContinue(406, 7, 10));
//		System.err.println(
//			HeatSolver.findDx0IndexContinue(406, 7, 17));
//
//		System.err.println(
//			HeatSolver.findDx0IndexContinue(1000, 7, 0));
		System.err.println(
			HeatActionSolver.findDuration(957, 27, 2));
//		System.err.println(
//			HeatActionSolver.findDuration(1000, 7, 1));
	}

	public void TestHeatSolver_Dx0_Helper(int dx0, int constant, int answer_index)
	{
		System.err.print(dx0 + "->" + HeatActionSolver.findDuration(dx0, constant, 0) + ",");

		// test calcDx0Index
		assertEquals("Asserting dx0 for index " + answer_index,
			answer_index, HeatActionSolver.findDuration(dx0, constant, 0));
	}

	@Test
	public void Calc()
	{
		int[] dx1 = {
			27,
			30,
			33,
			37,
			41,
			45,
			49,
			53,
			57,
			62,
			67,
			72,
			77,
			83,
			89,
			95,
			91,
		};

		List<Integer> dx2 = new ArrayList<>();
		for (int i = 0; i < dx1.length - 1; i++)
		{
			dx2.add(dx1[i+1] - dx1[i]);
		}

		System.err.println(dx2);
	}

}