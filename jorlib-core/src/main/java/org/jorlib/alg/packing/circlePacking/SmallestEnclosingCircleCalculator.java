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
 * AllAlgTests.java
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jorlib.alg.packing.circlePacking.util.MathUtil;

/**
 * For a given set of circles C, this class calculates the smallest enclosing circle which encloses all the circles in set C. The 
 * circles in C can have varying radii. To emphasize, this class does *NOT* perform circle packing. It merely computes the smallest enclosing
 * circle around a set of circles which have already been fixed.<p>
 * 
 * The implementation of this class is based on:<br>
 * "A randomized incremental algorithm" in:<br>
 * Xu, S. Freund, R.M. Sun, J. Solution methodologies for the Smallest Enclosing Circle Problem.<br>
 * Computational Optimization and Applications, volumne 25, issue 1-3, pp283-292, 2003<p>
 * 
 * Calculating the exact size of the smallest enclosing circle can be computationally expensive. This class also offers an approximation of
 * this value. The approximated radius is guaranteed to be larger or equal to the exact radius. The tighter the circles are packed, the more
 * accurate the approximation becomes.<p>
 * 
 * Examples for circle packing are given here:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/CirclePacking.html">http://mathworld.wolfram.com/CirclePacking.html</a></li>
 * <li><a href="http://en.wikipedia.org/wiki/Circle_packing_in_a_circle">http://en.wikipedia.org/wiki/Circle_packing_in_a_circle</a></li>
 * <li><a href="http://www.packomania.com/">http://www.packomania.com/</a></li>
 * </ul>
 *
 * Note from the author: In the aforementioned paper by Xu et. al several methods are compared. The authors report to obtain their
 * best results with their Quadratic programming approach, but we obtained better results with their 'Randomized incremental algorithm'.
 * Further experimenting may be required to determine the fastest method.<p>
 * 
 * To limit the impact caused by rounding issues, this class uses BigDecimals for added precision.
 * 
 * @author Joris Kinable
 * @since April 8, 2015
 *
 */
public class SmallestEnclosingCircleCalculator {
	//For debugging purposes, validation can be enabled to verify that intermediate solutions are correct.
	public static final boolean VALIDATORS_ENABLED=false;
	//Enable/disable debugging output
	public static final boolean DEBUG=false;
	
	/** Precision parameter **/
	public static final double PRECISION=0.000001;
		
		
	private int n; //the number of circles
	private BigDecimal[] xCors; //xCors of circles
	private BigDecimal[] yCors; //yCors of circles
	private BigDecimal[] radii; //radii of circles
	
	private BigDecimal R=BigDecimal.ZERO; //Radix of enclosing circle
	private BigDecimal x=BigDecimal.ZERO; //x-cor of the center of the enclosing circle
	private BigDecimal y=BigDecimal.ZERO; //y-cor of the center of the enclosing circle
	
	
	/**
	 * Given a set of circles identified by their x-coordinates, y-coordinates and radii, this method *approximates*
	 * the smallest enclosing circle which encloses all the circles provided. The circles may overlap and can be of
	 * any size. The approximation is calculated as follows. Let N be the number of circles, x_i, y_i, r_i resp the x coordinate,
	 * y coordinate, and radius of circles i.<br><br>
	 * 
	 * {@code xAVG=\frac{\sum_i x_i}{N}}<br>
	 * {@code yAVG=\frac{\sum_i y_i}{N}}<br><br>
	 * 
	 * {@code R=max_i \sqrt((xAVG-x_i)^2+(yAVG-y_i)^2)+r_i}<br><br>
	 * 
	 * The container will have its center at (xAVG,yAVG) and has radius R. The denser the packing of the circles, the more accurate
	 * the approximation of the container will be. This method is computationally cheap, fast and accurate. The approximated radius
	 * is guaranteed to be larger or equal to the exact radius.
	 * 
	 * @param xCors x-coordinates of the circles to be enclosed (can be positive and negative values)
	 * @param yCors y-coordinates of the circles to be enclosed (can be positive and negative values)
	 * @param radii radii of the circles to be enclosed (must be strictly positive, circles can be of any size)
	 */
	public void calculateApproximateContainer(double[] xCors, double[] yCors, double[] radii) {
		double maxR=Double.MIN_VALUE;
		int nrCircles=xCors.length;
		
		double xAVG = 0;
		double yAVG = 0;
		
		for (int i = 0; i < nrCircles; i++) {
			xAVG+=xCors[i];
			yAVG+=yCors[i];
		}
		xAVG/=nrCircles;
		yAVG/=nrCircles;
		
		for (int i = 0; i < nrCircles; i++) {
			double xi=xCors[i];
			double yi=yCors[i];
			double ri=radii[i];
			double dist = Math.sqrt(Math.pow(xAVG-xi, 2) + Math.pow(yAVG-yi, 2))+ri;
			if (dist>maxR) maxR=dist;
		}
		
		this.R=BigDecimal.valueOf(maxR);
		this.x=BigDecimal.valueOf(xAVG);
		this.y=BigDecimal.valueOf(yAVG);
	}
	
	/**
	 * Given a set of circles identified by their x-coordinates, y-coordinates and radii, this method calculates
	 * the smallest enclosing circle which encloses all the circles provided. The circles may overlap and can be of
	 * any size.
	 * @param xCors x-coordinates of the circles to be enclosed (can be positive and negative values)
	 * @param yCors y-coordinates of the circles to be enclosed (can be positive and negative values)
	 * @param radii radii of the circles to be enclosed (must be strictly positive, circles can be of any size)
	 */
	public void calcExactContainer(double[] xCors, double[] yCors, double[] radii){
		this.xCors=MathUtil.doubleToBigDecimalArray(xCors);
		this.yCors=MathUtil.doubleToBigDecimalArray(yCors);
		this.radii=MathUtil.doubleToBigDecimalArray(radii);
		this.n=xCors.length;
		
		List<Integer> C=new ArrayList<>();
		for(int i=0; i<n; i++)
			C.add(i);
		List<Integer> B=Collections.emptyList();
		
		Disk d=this.miniCircle(C, B);
		if(DEBUG) System.out.println("Final disk: "+d);
		if(VALIDATORS_ENABLED) this.validateSolution(d);
		
		this.x=d.x;
		this.y=d.y;
		this.R=d.R;
		
		if(DEBUG) System.out.println("Disk: "+d);
	}
	
	/**
	 * Given are a set of old circles, a circular container which encloses these circles and a new circle. This method tests whether the new
	 * circle fits into the existing container. If yes, the method returns. If not, the method increases the size of the container such that
	 * it encloses both the old circles and the new circle. Calculations are performed in an efficient way, thereby starting from the old container.
	 * It is much cheaper to invoke this method when a new circle is added, than to invoke the calcContainer(...) method.
	 * @param posCircleToAdd: the position of the circle that is being added in the xCors/yCors/radii vectors
	 * @param xCors: xCors of placed circles (including the circle that is being added)
	 * @param yCors: yCors of placed circles (including the circle that is being added)
	 * @param radii: radii of placed circles (including the circle that is being added)
	 * @param xCorContainer: xCor of center of existing container
	 * @param yCorContainer: yCor of center of existing container
	 * @param radiusContainer: : radix of existing container
	 */
	public void incrementalCalcContainer(int posCircleToAdd, double[] xCors, double[] yCors, double[] radii, double xCorContainer, double yCorContainer, double radiusContainer){
		Disk d=new Disk(BigDecimal.valueOf(xCorContainer), BigDecimal.valueOf(yCorContainer), BigDecimal.valueOf(radiusContainer));
		
		if(!d.circleIsContained(BigDecimal.valueOf(xCors[posCircleToAdd]), BigDecimal.valueOf(yCors[posCircleToAdd]), BigDecimal.valueOf(radii[posCircleToAdd]))){ //If the new circle does not fall within the existing container, calculate a new container:
			this.xCors=MathUtil.doubleToBigDecimalArray(xCors);
			this.yCors=MathUtil.doubleToBigDecimalArray(yCors);
			this.radii=MathUtil.doubleToBigDecimalArray(radii);
			this.n=xCors.length;
			
			List<Integer> C=new ArrayList<>();
			for(int i=0; i<n; i++){
				if(i!=posCircleToAdd)
					C.add(i);
			}
			List<Integer> B=new ArrayList<>();
			B.add(posCircleToAdd); //The new circle must be on the edge of the container
			
			Disk dNew=this.miniCircle(C, B);
			if(DEBUG) System.out.println("Final disk: "+dNew);
			this.validateSolution(dNew);
			
			this.x=dNew.x;
			this.y=dNew.y;
			this.R=dNew.R;
		}
	}
	
	/**
	 * Get the radius of the enclosing circle
	 * @return the radius of the enclosing circle
	 */
	public double getRadius(){
		return R.doubleValue();
	}
	/**
	 * Get the x and y coordinates of the center of the enclosing circle
	 * @return x and y coordinates of the center of the enclosing circle, returned as an array a, where a[0]=x-cor and a[1]=y-cor
	 */
	public double[] getContainerPosition(){
		return new double[]{x.doubleValue(),y.doubleValue()};
	}
	/**
	 * Get the x and y coordinates of the center of the enclosing circle
	 * @return x and y coordinates of the center of the enclosing circle as a point
	 */
	public Point2D getContainerPositionAsPoint(){
		return new Point2D.Double(x.doubleValue(),y.doubleValue());
	}
	
	
	/**
	 * Implementation of the MiniCircle function (Algorithm 2.6 in the paper)
	 * @param C Set of circles to be enclosed
	 * @param B Set of boundary circles
	 * @return Enclosing circle
	 */
	private Disk miniCircle(List<Integer> C, List<Integer> B){
		if(DEBUG) System.out.println("Calculating disk size for C: "+C.toString()+" B: "+B.toString());
		Disk D=null;
		if(C.isEmpty()){
			if(B.isEmpty()){ //Calculate an empty circle
				D=new Disk(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			}else if(B.size()==1){ //The container should contain a single circle
				int id1=B.get(0);
				D=new Disk(xCors[id1], yCors[id1], radii[id1]);
			}else if(B.size()==2){ //The container should contain two circles
				D=this.getDiskTwoTangentCircles(B);
			}
			
		}else{
			int c=C.get(0);
			List<Integer> Cnew=new ArrayList<>(C);
			Cnew.remove(0);
			D=this.miniCircle(Cnew, B);
			if(!D.circleIsContained(xCors[c], yCors[c], radii[c])){ //A new corner point has been found
				if(B.size()<= 1){
					Cnew=new ArrayList<>(C);
					List<Integer> Bnew=new ArrayList<>(B);
					Cnew.remove(Cnew.indexOf(c));
					Bnew.add(c);
					D=this.miniCircle(Cnew, Bnew);
				}else{
					List<Integer> Bnew=new ArrayList<>(B);
					Bnew.add(c);
					D=this.getDiskThreeTangentCircles(Bnew);
				}
			}
		}
		if(DEBUG) System.out.println("Disk: "+D);
		return D;
	}
	
	/**
	 * Calculate the smallest enclosing circle for the circles in set B
	 * @param B Set of 2 circles
	 * @return Smallest enclosing circle
	 */
	private Disk getDiskTwoTangentCircles(List<Integer> B){
		if(B.size() != 2)
			throw new RuntimeException("Invalid number of items in D. Current size: "+B.size());
		int id1=B.get(0);
		int id2=B.get(1);
		
		if(DEBUG) System.out.println("Getting disk size for: c1: ("+xCors[id1]+","+yCors[id1]+") r: "+radii[id1]+"\nc2: ("+xCors[id2]+","+yCors[id2]+") r: "+radii[id2]);
		
		//r=r1+r2+sqrt((x1-x2)^2+(y1-y2)^2);
		BigDecimal r=radii[id1].add(radii[id2]).add(MathUtil.sqrt((xCors[id1].subtract(xCors[id2])).pow(2).add((yCors[id1].subtract(yCors[id2])).pow(2)), MathContext.DECIMAL64));
		r=r.divide(BigDecimal.valueOf(2),25,RoundingMode.HALF_UP);
		
		if(DEBUG) System.out.println("R: "+r);
		
		BigDecimal xCor;
		BigDecimal yCor;		
		if(MathUtil.equals(xCors[id1],xCors[id2],PRECISION)){ //x1=x2
			xCor=xCors[id1];
			//yCor=(r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/2(y2-y1)-R(r1-r2)/(y2-y1)
			yCor=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			yCor=MathUtil.divide(yCor, BigDecimal.valueOf(2).multiply(yCors[id2].subtract(yCors[id1])));
			yCor=yCor.subtract(r.multiply(  MathUtil.divide(radii[id1].subtract(radii[id2]), yCors[id2].subtract(yCors[id1])) ));
		}else if(MathUtil.equals(yCors[id1], yCors[id2], PRECISION)){ //y1=y2
			//xCor=(r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/2(x2-x1)-R(r1-r2)/(x2-x1)
			xCor=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			xCor=MathUtil.divide(xCor, BigDecimal.valueOf(2).multiply(xCors[id2].subtract(xCors[id1])));
			xCor=xCor.subtract(r.multiply(  MathUtil.divide(radii[id1].subtract(radii[id2]), xCors[id2].subtract(xCors[id1])) ));
			
			yCor=yCors[id1];
		}else{
			//c1=(y2-y1)/(x2-x1)
			BigDecimal c1=MathUtil.divide(yCors[id2].subtract(yCors[id1]), xCors[id2].subtract(xCors[id1]));
			//c2=(r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/2(x2-x1)-R(r1-r2)/(x2-x1)
			BigDecimal c2=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			c2=MathUtil.divide(c2, BigDecimal.valueOf(2).multiply(xCors[id2].subtract(xCors[id1])));
			c2=c2.subtract(r.multiply(  MathUtil.divide(radii[id1].subtract(radii[id2]), xCors[id2].subtract(xCors[id1])) ));
			
			//c3=c2-x1
			BigDecimal c3=c2.subtract(xCors[id1]);
			
			if(DEBUG) System.out.println("c1: "+c1+" c2: "+c2+" c3: "+c3+" r: "+r);
			
			//a=1+c1^2
			BigDecimal a=BigDecimal.valueOf(1).add(c1.pow(2));
			//b=-2c1c3-2y1
			BigDecimal b=BigDecimal.valueOf(-2).multiply(c1).multiply(c3).subtract(BigDecimal.valueOf(2).multiply(yCors[id1]));
			//c=c3^2+y1^2-(R-r1)^2
			BigDecimal c=c3.pow(2).add(yCors[id1].pow(2)).subtract((r.subtract(radii[id1])).pow(2));
			//double d=Math.pow(b,2)-4*a*c;
			BigDecimal d=b.pow(2).subtract(BigDecimal.valueOf(4).multiply(a).multiply(c));
			
			if(DEBUG) System.out.println("a: "+a+" b: "+b+" c: "+c+" d: "+d);
			
			//|0-d|<EPSILON
			if(d.compareTo(BigDecimal.ZERO)==-1){
				d=BigDecimal.ZERO;
			}
			
						
			//yCor1=(-b+sqrt(d)/2a)
			BigDecimal yCor1=(b.negate().add(MathUtil.sqrt(d, MathContext.DECIMAL128))).divide(BigDecimal.valueOf(2).multiply(a),25, RoundingMode.HALF_UP);
			//yCor2=(-b-sqrt(d)/2a)
			BigDecimal yCor2=(b.negate().subtract(MathUtil.sqrt(d, MathContext.DECIMAL128))).divide(BigDecimal.valueOf(2).multiply(a),25, RoundingMode.HALF_UP);
			
			if(DEBUG) System.out.println("yCor1: "+yCor1+" yCor2: "+yCor2);
			
			if(yCor1.compareTo(BigDecimal.valueOf(0))>0 && yCor2.compareTo(BigDecimal.valueOf(0))>0 && (yCor1.subtract(yCor2)).abs().compareTo(BigDecimal.valueOf(0.1))>0)
				throw new RuntimeException("Both yCor1 and yCor2 are positive! yCor1: "+yCor1+" yCor2: "+yCor2);
			
			
			if(yCor1.compareTo(BigDecimal.valueOf(0))==-1)
				yCor=yCor2;
			else
				yCor=yCor1;
			
			xCor=c1.negate().multiply(yCor).add(c2);
		}
		Disk D=new Disk(xCor, yCor, r);
		if(VALIDATORS_ENABLED) this.validateGetDiskTwoTangentCircles(B,D);
		
		return D;
	}
	
	/**
	 * Calculate the smallest enclosing circle for the circles in set B
	 * @param B Set of 3 circles
	 * @return Smallest enclosing circle
	 */
	private Disk getDiskThreeTangentCircles(List<Integer> B){
						
		if(B.size() != 3)
			throw new RuntimeException("Invalid number of items in D. Current size: "+B.size());
		
		int id1=B.get(0);
		int id2=B.get(1);
		int id3=B.get(2);
		
		if(DEBUG) System.out.println("Getting disk size for: c1: ("+xCors[id1]+","+yCors[id1]+") r: "+radii[id1]+"\nc2: ("+xCors[id2]+","+yCors[id2]+") r: "+radii[id2]+"\nc3: ("+xCors[id3]+","+yCors[id3]+") r: "+radii[id3]);
		
		//Calculate the X coordinate
		BigDecimal c1;
		BigDecimal c2;
		
		if(MathUtil.equals(yCors[id1], yCors[id2], PRECISION)){ //y_1=y_2
			//c1=(r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/2(x2-x1)
			c1=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			c1=MathUtil.divide(c1, BigDecimal.valueOf(2).multiply(xCors[id2].subtract(xCors[id1])));
			//c2=(r1-r2)/(x2-x1)
			c2=MathUtil.divide(radii[id1].subtract(radii[id2]), xCors[id2].subtract(xCors[id1]));
		}else if(MathUtil.equals(yCors[id1], yCors[id3], PRECISION)){ //y_1=y_3
			//c1=(r1^2-x1^2-y1^2-(r3^2-x3^2-y3^2))/2(x3-x1)
			c1=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id3].pow(2).subtract(xCors[id3].pow(2)).subtract(yCors[id3].pow(2)));
			c1=MathUtil.divide(c1, BigDecimal.valueOf(2).multiply(xCors[id3].subtract(xCors[id1])));
			//c2=(r1-r3)/(x3-x1)
			c2=MathUtil.divide(radii[id1].subtract(radii[id3]), xCors[id3].subtract(xCors[id1]));
		}else if(MathUtil.equals(yCors[id2], yCors[id3], PRECISION)){ //y_2=y_3
			//c1=(r3^2-x3^2-y3^2-(r2^2-x2^2-y2^2))/2(x2-x3)
			c1=radii[id3].pow(2).subtract(xCors[id3].pow(2)).subtract(yCors[id3].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			c1=MathUtil.divide(c1, BigDecimal.valueOf(2).multiply(xCors[id2].subtract(xCors[id3])));
			//c2=(r3-r2)/(x2-x3)
			c2=MathUtil.divide(radii[id3].subtract(radii[id2]), xCors[id2].subtract(xCors[id3]));
		}else{
			//c0=(x2-x1)/(y2-y1)-(x3-x1)/(y3-y1)
			BigDecimal c0=MathUtil.divide(xCors[id2].subtract(xCors[id1]), yCors[id2].subtract(yCors[id1])).subtract(MathUtil.divide(xCors[id3].subtract(xCors[id1]), yCors[id3].subtract(yCors[id1])));
			//c1=((r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/2(y2-y1) - (r1^2-x1^2-y1^2-(r3^2-x3^2-y3^2))/2(y3-y1) )/c0
			c1=MathUtil.divide(radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2))), BigDecimal.valueOf(2).multiply(yCors[id2].subtract(yCors[id1])));
			c1=c1.subtract(MathUtil.divide(radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id3].pow(2).subtract(xCors[id3].pow(2)).subtract(yCors[id3].pow(2))), BigDecimal.valueOf(2).multiply(yCors[id3].subtract(yCors[id1]))));
			c1=MathUtil.divide(c1, c0);
			//c2=((r1-r2)/(y2-y1)-(r1-r3)/(y3-y1))/c0;
			c2=MathUtil.divide(radii[id1].subtract(radii[id2]), yCors[id2].subtract(yCors[id1])).subtract(MathUtil.divide(radii[id1].subtract(radii[id3]), yCors[id3].subtract(yCors[id1])));
			c2=MathUtil.divide(c2, c0);
		}
		
		//Calculate the Y coordinate
		BigDecimal c4;
		BigDecimal c5;
		
		if(MathUtil.equals(xCors[id1], xCors[id2], PRECISION)){ //x_1=x_2
			//c4=(r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/(2(y2-y1))
			c4=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			c4=MathUtil.divide(c4, BigDecimal.valueOf(2).multiply(yCors[id2].subtract(yCors[id1])));
			//c5=(r1-r2)/(y2-y1)
			c5=MathUtil.divide(radii[id1].subtract(radii[id2]), yCors[id2].subtract(yCors[id1]));
		}else if(MathUtil.equals(xCors[id1], xCors[id3], PRECISION)){ //x_1=x_3
			//c4=(r1^2-x1^2-y1^2-(r3^2-x3^2-y3^2))/(2(y3-y1))
			c4=radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id3].pow(2).subtract(xCors[id3].pow(2)).subtract(yCors[id3].pow(2)));
			c4=MathUtil.divide(c4, BigDecimal.valueOf(2).multiply(yCors[id3].subtract(yCors[id1])));
			//c5=(r1-r3)/(y3-y1)
			c5=MathUtil.divide(radii[id1].subtract(radii[id3]), yCors[id3].subtract(yCors[id1]));
		}else if(MathUtil.equals(xCors[id2], xCors[id3], PRECISION)){ //x_2=x_3
			//c4=(r3^2-x3^2-y3^2-(r2^2-x2^2-y2^2))/(2(y2-y3))
			c4=radii[id3].pow(2).subtract(xCors[id3].pow(2)).subtract(yCors[id3].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2)));
			c4=MathUtil.divide(c4, BigDecimal.valueOf(2).multiply(yCors[id2].subtract(yCors[id3])));
			//c5=(r3-r2)/(y2-y3)
			c5=MathUtil.divide(radii[id3].subtract(radii[id2]), yCors[id2].subtract(yCors[id3]));
		}else{
			//c3=(y2-y1)/(x2-x1)-(y3-y1)/(x3-x1)
			BigDecimal c3=MathUtil.divide(yCors[id2].subtract(yCors[id1]), xCors[id2].subtract(xCors[id1])).subtract(MathUtil.divide(yCors[id3].subtract(yCors[id1]), xCors[id3].subtract(xCors[id1])));
			//c4=((r1^2-x1^2-y1^2-(r2^2-x2^2-y2^2))/2(x2-x1)-(r1^2-x1^2-y1^2-(r3^2-x3^2-y3^3))/2(x3-x1))/c3
			c4=MathUtil.divide(radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id2].pow(2).subtract(xCors[id2].pow(2)).subtract(yCors[id2].pow(2))), BigDecimal.valueOf(2).multiply(xCors[id2].subtract(xCors[id1])));
			c4=c4.subtract(MathUtil.divide(radii[id1].pow(2).subtract(xCors[id1].pow(2)).subtract(yCors[id1].pow(2)).subtract(radii[id3].pow(2).subtract(xCors[id3].pow(2)).subtract(yCors[id3].pow(2))), BigDecimal.valueOf(2).multiply(xCors[id3].subtract(xCors[id1]))));
			c4=MathUtil.divide(c4, c3);
			
			//c5=((r1-r2)/(x2-x1)-(r1-r3)/(x3-x1))/c3
			c5=MathUtil.divide(radii[id1].subtract(radii[id2]), xCors[id2].subtract(xCors[id1]));
			c5=c5.subtract(MathUtil.divide(radii[id1].subtract(radii[id3]), xCors[id3].subtract(xCors[id1])));
			c5=MathUtil.divide(c5, c3);
		}
		
		
		//double a=Math.pow(c2, 2)+Math.pow(c5, 2)-1;
		BigDecimal a=c2.pow(2).add(c5.pow(2)).subtract(BigDecimal.valueOf(1));
		//double b=-2*(c1-xCors[id1])*c2-2*(c4-yCors[id1])*c5+2*radii[id1];
		BigDecimal b=BigDecimal.valueOf(-2).multiply(c1.subtract(xCors[id1])).multiply(c2).subtract(BigDecimal.valueOf(2).multiply(c4.subtract(yCors[id1])).multiply(c5)).add(BigDecimal.valueOf(2).multiply(radii[id1]));
		//double c=-Math.pow(radii[id1], 2)+Math.pow(c1, 2)+Math.pow(c4, 2)+Math.pow(xCors[id1], 2)+Math.pow(yCors[id1], 2)-2*xCors[id1]*c1-2*yCors[id1]*c4;
		BigDecimal c=radii[id1].pow(2).negate().add(c1.pow(2)).add(c4.pow(2)).add(xCors[id1].pow(2)).add(yCors[id1].pow(2)).subtract(BigDecimal.valueOf(2).multiply(xCors[id1]).multiply(c1)).subtract(BigDecimal.valueOf(2).multiply(yCors[id1]).multiply(c4));
		//double d=Math.pow(b,2)-4*a*c;
		BigDecimal d=b.pow(2).subtract(BigDecimal.valueOf(4).multiply(a).multiply(c));
		
		if(DEBUG) System.out.println("a: "+a+" b: "+b+" c: "+c+" d: "+d);
		
		//|0-d|<EPSILON
		if(d.compareTo(BigDecimal.ZERO)==-1){
			d=BigDecimal.ZERO;
		}
		
		//double r1=(-b+Math.sqrt(d))/(2*a);
		BigDecimal r1=(b.negate().add(MathUtil.sqrt(d, MathContext.DECIMAL128))).divide(BigDecimal.valueOf(2).multiply(a),25, RoundingMode.HALF_UP);
		//double r2=(-b-Math.sqrt(d))/(2*a); //this SHOULD be negative?
		BigDecimal r2=(b.negate().subtract(MathUtil.sqrt(d, MathContext.DECIMAL128))).divide(BigDecimal.valueOf(2).multiply(a),25, RoundingMode.HALF_UP);
		
		if(DEBUG) System.out.println("r1: "+r1+" r2: "+r2);

		BigDecimal xCor=c1.subtract(r2.multiply(c2)); //c1-r*c2;
		BigDecimal yCor=c4.subtract(r2.multiply(c5)); //c4-r*c5;
		
		Disk D=new Disk(xCor, yCor, r2);
		if(DEBUG) System.out.println("Disk: "+D);
		if(VALIDATORS_ENABLED) this.validateGetDiskThreeTangentCircles(B, D);
		return D;
	}
	
	
	/**
	 * Method for debugging purposes. For a given enclosing circle, this method checks whether all circles are contained.
	 * @param d Enclosing circle
	 */
	private void validateSolution(Disk d){
		for(int i=0; i<n; i++){
			if(!d.circleIsContained(xCors[i], yCors[i], radii[i]))
				throw new RuntimeException("Circle outside container. Circle: ("+xCors[i]+","+yCors[i]+") r: "+radii[i]+" container: ("+d.x+","+d.y+") R: "+d.R);
		}
	}
	
	/**
	 * Method for debugging purposes. Checks whether Disk d is indeed the smallest enclosing circle for the 2 circles in set B. The borders
	 * of these circles must touch the enclosing circle.
	 * @param B Set of 2 circles
	 * @param d Smallest enclosing circle
	 */
	private void validateGetDiskTwoTangentCircles(List<Integer> B, Disk d){
		int c1=B.get(0);
		int c2=B.get(1);
		
		//Check disk 1
		if(!MathUtil.equals((d.x.subtract(xCors[c1])).pow(2).add((d.y.subtract(yCors[c1])).pow(2)), (d.R.subtract(radii[c1])).pow(2), PRECISION)){
			System.out.println("Diff1: "+(d.x.subtract(xCors[c1])).pow(2).add((d.y.subtract(yCors[c1])).pow(2)).subtract((d.R.subtract(radii[c1])).pow(2)).abs());
			throw new RuntimeException("Circle c1 is not on the border of the disk. c1: ("+xCors[c1]+","+yCors[c1]+") r: "+radii[c1]+"\nc2: ("+xCors[c2]+","+yCors[c2]+") r: "+radii[c2]+"\ndisk: ("+d.x+","+d.y+") R: "+d.R);
		}
		//Check disk 2
		if(!MathUtil.equals((d.x.subtract(xCors[c2])).pow(2).add((d.y.subtract(yCors[c2])).pow(2)), (d.R.subtract(radii[c2])).pow(2), PRECISION)){
			System.out.println("Diff2: "+(d.x.subtract(xCors[c2])).pow(2).add((d.y.subtract(yCors[c2])).pow(2)).subtract((d.R.subtract(radii[c2])).pow(2)).abs());
			throw new RuntimeException("Circle c2 is not on the border of the disk. c1: ("+xCors[c1]+","+yCors[c1]+") r: "+radii[c1]+"\nc2: ("+xCors[c2]+","+yCors[c2]+") r: "+radii[c2]+"\ndisk: ("+d.x+","+d.y+") R: "+d.R);
		}
	}
	
	/**
	 * Method for debugging purposes. Checks whether Disk d is indeed the smallest enclosing circle for the 3 circles in set B. The borders
	 * of these circles must touch the enclosing circle.
	 * @param B Set of 3 circles
	 * @param d Smallest enclosing circle
	 */
	private void validateGetDiskThreeTangentCircles(List<Integer> B, Disk d){
		int c1=B.get(0);
		int c2=B.get(1);
		int c3=B.get(2);
		
		//Check disk 1
		if(!MathUtil.equals((d.x.subtract(xCors[c1])).pow(2).add((d.y.subtract(yCors[c1])).pow(2)), (d.R.subtract(radii[c1])).pow(2), PRECISION))
			throw new RuntimeException("Circle c1 is not on the border of the disk. c1: ("+xCors[c1]+","+yCors[c1]+") r: "+radii[c1]+"\nc2: ("+xCors[c2]+","+yCors[c2]+") r: "+radii[c2]+"\nc3: ("+xCors[c3]+","+yCors[c3]+") r: "+radii[c3]+"\ndisk: ("+d.x+","+d.y+") R: "+d.R);
		//Check disk 2
		if(!MathUtil.equals((d.x.subtract(xCors[c2])).pow(2).add((d.y.subtract(yCors[c2])).pow(2)), (d.R.subtract(radii[c2])).pow(2), PRECISION))
			throw new RuntimeException("Circle c2 is not on the border of the disk. c1: ("+xCors[c1]+","+yCors[c1]+") r: "+radii[c1]+"\nc2: ("+xCors[c2]+","+yCors[c2]+") r: "+radii[c2]+"\nc3: ("+xCors[c3]+","+yCors[c3]+") r: "+radii[c3]+"\ndisk: ("+d.x+","+d.y+") R: "+d.R);
		//Check disk 3
		if(!MathUtil.equals((d.x.subtract(xCors[c3])).pow(2).add((d.y.subtract(yCors[c3])).pow(2)), (d.R.subtract(radii[c3])).pow(2), PRECISION))
			throw new RuntimeException("Circle c3 is not on the border of the disk. c1: ("+xCors[c1]+","+yCors[c1]+") r: "+radii[c1]+"\nc2: ("+xCors[c2]+","+yCors[c2]+") r: "+radii[c2]+"\nc3: ("+xCors[c3]+","+yCors[c3]+") r: "+radii[c3]+"\ndisk: ("+d.x+","+d.y+") R: "+d.R);
	}
	
	/**
	 * Implementation of a disk/circle
	 *
	 */
	private class Disk{
		public final BigDecimal x; //x-cor of center of the disk
		public final BigDecimal y; //y-cor of center of the disk
		public final BigDecimal R; //radix of the disk
		
		public Disk(BigDecimal x, BigDecimal y, BigDecimal R){
			this.x=x;
			this.y=y;
			this.R=R;
		}
		
		/**
		 * Check whether this circle fully encloses the given circle
		 * @param x_i x-cor of the center of the given circle
		 * @param y_i y-cor of the center of the given circle
		 * @param radix radix of the given circle
		 * @return true if this circle fully encloses the given circle
		 */
		public boolean circleIsContained(BigDecimal x_i, BigDecimal y_i, BigDecimal radix){
			return MathUtil.sqrt((this.x.subtract(x_i)).pow(2).add((this.y.subtract(y_i)).pow(2)), MathContext.DECIMAL128).add(radix).compareTo(this.R.add(BigDecimal.valueOf(PRECISION))) <= 0;
		}
		
		public String toString(){
			return "center: ("+x+";"+y+"), radix: "+R;
		}
	}
	
}
