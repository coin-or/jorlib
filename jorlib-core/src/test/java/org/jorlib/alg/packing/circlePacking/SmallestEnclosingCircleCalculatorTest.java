/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * SmallestEnclosingCircleCalculatorTest.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.alg.packing.circlePacking;

import java.awt.geom.Point2D;
import java.util.Arrays;

import org.junit.Assert;

import junit.framework.TestCase;

/**
 * Unit tests for SmallestEnclosingCircleCalculator.java
 * @author Joris Kinable
 * @since April 9, 2015
 *
 */
public final class SmallestEnclosingCircleCalculatorTest extends TestCase{
	
	public static final double PRECISION=0.000001;

	/**
	 * Test 1 - Single circle: obviously the container must be identical to the circle
	 */
	public void testSingleCircle(){
		//Define the circle.
		double[] xCors={1};
		double[] yCors={2};
		double[] radii={3};
		
		//Calculate the smallest enclosing circle
		SmallestEnclosingCircleCalculator cecc=new SmallestEnclosingCircleCalculator();
		cecc.calcExactContainer(xCors, yCors, radii);
		assertEquals(3.0, cecc.getRadius(), PRECISION);
		Assert.assertArrayEquals(new double[]{1.0,2.0}, cecc.getContainerPosition(), PRECISION);
		assertEquals(new Point2D.Double(1.0, 2.0), cecc.getContainerPositionAsPoint());
	}
	
	/**
	 * Test 2 - Two circles next to each other
	 */
	public void testDoubleCircle(){
		//Define two circles
		double[] xCors={0,2};
		double[] yCors={0,0};
		double[] radii={1,1};
		
		//Calculate the smallest enclosing circle
		SmallestEnclosingCircleCalculator cecc=new SmallestEnclosingCircleCalculator();
		cecc.calcExactContainer(xCors, yCors, radii);
		assertEquals(2.0, cecc.getRadius(), PRECISION);
		Assert.assertArrayEquals(new double[]{1.0,0.0}, cecc.getContainerPosition(), PRECISION);
		assertEquals(new Point2D.Double(1.0, 0.0), cecc.getContainerPositionAsPoint());
	}
	
	/**
	 * 19 Uniform circles test using instance cci19.txt.
	 * Instance available here: http://www.packomania.com/, see circles in a circle, uniform
	 */
	public void testPackomania1(){
		//Define circles
		double[] xCors={-0.2056046467595680,0.2056046467595680,-0.5617223412193920,0.5617223412193920,-0.2056046467595680,0.2056046467595680,-0.7673269879789610,0.7673269879789610,-0.4112092935191370,0.0000000000000000,0.4112092935191370,-0.7673269879789610,0.7673269879789610,-0.2056046467595680,0.2056046467595680,-0.5617223412193920,0.5617223412193920,-0.2056046467595680,0.2056046467595680};
		double[] yCors={-0.7673269879789610,-0.7673269879789610,-0.5617223412193920,-0.5617223412193920,-0.3561176944598240,-0.3561176944598240,-0.2056046467595680,-0.2056046467595680,0.0000000000000000,0.0000000000000000,0.0000000000000000,0.2056046467595680,0.2056046467595680,0.3561176944598240,0.3561176944598240,0.5617223412193920,0.5617223412193920,0.7673269879789610,0.7673269879789610};
		double[] radii=new double[xCors.length];
		Arrays.fill(radii, 0.205604646759568224693193969093);
		
		//Calculate exact
		SmallestEnclosingCircleCalculator cecc=new SmallestEnclosingCircleCalculator();
		cecc.calcExactContainer(xCors, yCors, radii);
		assertEquals(1.0, cecc.getRadius(), PRECISION);
		Assert.assertArrayEquals(new double[]{0.0,0.0}, cecc.getContainerPosition(), PRECISION);
		
		//Calculate approximate
		cecc.calculateApproximateContainer(xCors, yCors, radii);
		assertEquals(1.0, cecc.getRadius(), PRECISION);
		Assert.assertArrayEquals(new double[]{0.0,0.0}, cecc.getContainerPosition(), PRECISION);
	}
	
	
	/**
	 * 80 Uniform circles test using instance cci80.txt.
	 * Instance available here: http://www.packomania.com/, see circles in a circle, uniform
	 */
	public void testPackomania2(){
		//Define circles
		double[] xCors={0.0426898733356200, -0.1575348824571830, 0.2466242540317290, -0.3499247858044820, 0.4322415195567560, -0.5249115018637350, 0.1168198108729920, -0.0833515812606830, 0.6312175988438610, -0.3105925184646160, -0.6737922273010820, 0.4368888434081820, 0.2393467928840140, -0.4839853878772240, 0.0398137107992380, -0.1590823590722910, 0.7800044721374330, 0.5793687930633650, -0.7891625162085000, 0.3685293224720440, 0.1623406928102600, -0.0365177105241222, -0.4348872464080880, -0.2354137803956520, -0.6331564840235330, 0.6806861831005380, -0.8652845334088780, 0.4837637446928120, 0.8730886340211190, 0.2848676748212820, 0.0860092714868999, -0.1128491318474820, -0.5116871571522240, -0.3117452017190120, -0.7104490741512450, 0.6118858844208940, 0.4071689574820750, -0.8983724204096930, 0.2083105541476930, 0.8994958529415820, 0.0094521508133108, -0.1894439190582190, -0.5883760798986490, -0.3883399889297480, 0.7289603451820650, 0.5294702401428680, 0.3306118368084860, -0.8867805815921130, 0.1317534334741040, -0.0671426363974255, -0.2660387062689550, -0.6713845976025570, 0.8696436103608770, -0.4713485066336560, 0.6754142460814130, 0.4531388188195080, 0.2542804154851260, -0.8310855263951340, 0.0551586462633676, -0.1437374236081620, -0.3490472239728630, -0.5505253729913470, 0.5744927311201710, 0.7754770887609930, 0.3779717663846090, 0.1776856282743900, -0.7340571971490270, -0.0192534995282382, -0.2193303826311500, -0.4182679481378070, 0.6231913957984930, -0.6005212085419170, 0.2881293308055260, -0.1062943826472560, 0.0943442743101961, 0.4638871772501750, -0.4371188501128690, -0.2519767878267180, 0.2363124207674440, -0.0062958189877233};
		double[] yCors={-0.8986671118080090, -0.8857808780618150, -0.8652175913919300, -0.8288411472715660, -0.7890451646781860, -0.7306797645630530, -0.7122247340792460, -0.6967088043207380, -0.6410750622441110, -0.6320951673930580, -0.5961786959959290, -0.5884599951529470, -0.5533438147348530, -0.5311450773024050, -0.5269517775964390, -0.5005631201446350, -0.4483503391058860, -0.4471961576052380, -0.4320272284731920, -0.3998254708743010, -0.3680708582520460, -0.3413998388536590, -0.3366061856721560, -0.3150111814018540, -0.3058601734455350, -0.2740176533718770, -0.2463892842112650, -0.2355785963594570, -0.2171203359289390, -0.2091899389076520, -0.1825189195092650, -0.1558479001108780, -0.1461691890980140, -0.1294592426590730, -0.1187883891950980, -0.0786237265093770, -0.0501352184119257, -0.0484973955794410, -0.0234641990135383, -0.0182267295929486, 0.0032068203848490, 0.0295954778366537, 0.0404416077484886, 0.0559841352884584, 0.0874787501403773, 0.1089195020838010, 0.1355905214821890, 0.1518064664034880, 0.1622615408805760, 0.1886501983323810, 0.2150388557841850, 0.2231041212564580, 0.2305319806214720, 0.2386466487964280, 0.2808406469131180, 0.2944714408265820, 0.3211424602249690, 0.3445603734431490, 0.3477049188281070, 0.3740935762799120, 0.3977013692921540, 0.4257634661222120, 0.4542501497391650, 0.4559530462453580, 0.4946916911520860, 0.5065858381725010, 0.5201778873082490, 0.5449392968534230, 0.5599475845729750, 0.5860215731963820, 0.6488894261224210, 0.6699248325163910, 0.6740916482916220, 0.7257151041903300, 0.7260854914997460, 0.7708655459375670, 0.7863536825161830, 0.8636739555684700, 0.8680587391821340, 0.8996584717513820};
		double[] radii=new double[xCors.length];
		Arrays.fill(radii, 0.100319499416176579706634470175);
		
		//Calculate exact
		SmallestEnclosingCircleCalculator cecc=new SmallestEnclosingCircleCalculator();
		cecc.calcExactContainer(xCors, yCors, radii);
		assertEquals(1.0, cecc.getRadius(), PRECISION);
		Assert.assertArrayEquals(new double[]{0.0,0.0}, cecc.getContainerPosition(), PRECISION);
		
		//Calculate approximate
		cecc.calculateApproximateContainer(xCors, yCors, radii);
		assertEquals(1.0, cecc.getRadius(), 0.003);
		Assert.assertArrayEquals(new double[]{0.0,0.0}, cecc.getContainerPosition(), 0.003);
	}
}
