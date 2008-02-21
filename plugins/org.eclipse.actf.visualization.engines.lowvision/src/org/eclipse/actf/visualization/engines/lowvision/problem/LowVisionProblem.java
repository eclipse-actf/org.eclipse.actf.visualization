/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Junji MAEDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.visualization.engines.lowvision.problem;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.actf.visualization.engines.lowvision.LowVisionType;
import org.eclipse.actf.visualization.engines.lowvision.PageElement;
import org.eclipse.actf.visualization.engines.lowvision.image.ConnectedComponent;
import org.eclipse.actf.visualization.engines.lowvision.image.Int2D;
import org.eclipse.actf.visualization.engines.lowvision.image.PageComponent;
import org.eclipse.actf.visualization.engines.lowvision.image.PageImage;
import org.eclipse.actf.visualization.eval.problem.ILowvisionProblemSubtype;


public abstract class LowVisionProblem implements ILowvisionProblemSubtype{
	public static final int UNSET_POSITION = -1;
	public static final int DEFAULT_PRIORITY = 0;
	
    //061024
    public static final short LOWVISION_PROBLEM = 0;
    
	PageImage pageImage = null; 
	LowVisionType lowVisionType = null;
	short problemType; 
	short componentType = PageComponent.UNDEFINED_TYPE; 
	PageComponent pageComponent = null; 
	PageElement pageElement = null; 
	String description; 
	int left = UNSET_POSITION;
	int top = UNSET_POSITION;
	int width = 0;
	int height = 0;
	int priority; 

	double probability = 0.0; // 
	double characterScore = 0.0; //
	int numRecommendations = 0; // recommendations.length;
	LowVisionRecommendation[] recommendations = null;
	boolean isGroupFlag = false; // is LowVisionProblemGroup?
	
	protected LowVisionProblem(){
	}
	
	public LowVisionProblem( short _type, LowVisionType _lvType, String _description, PageComponent _com, double _proba ) throws LowVisionProblemException{
		problemType = _type;
		lowVisionType = _lvType;
		description = _description;
		pageComponent = _com;
		componentType = pageComponent.getType();
		pageImage = pageComponent.getPageImage();
		ConnectedComponent cc = pageComponent.getConnectedComponent();
		if( cc != null ){
			left = cc.getLeft();
			top = cc.getTop();
			width = cc.getWidth();
			height = cc.getHeight();
		}
		setPriority();
		probability = _proba;
		characterScore = probability * (double)width * (double)height; 
	}

	public LowVisionProblem( short _type, LowVisionType _lvType, String _description, PageElement _pe, double _proba ){
		problemType = _type;
		lowVisionType = _lvType;
		description = _description;
		pageElement = _pe;
		if( pageElement != null ){
			left = pageElement.getX();
			top = pageElement.getY();
			width = pageElement.getWidth();
			height = pageElement.getHeight();
		}
		setPriority();
		probability = _proba;
	}
	
	private void setPriority(){
		if( left == UNSET_POSITION || top == UNSET_POSITION ){
			priority = DEFAULT_PRIORITY;
		}else{
			priority = 0x7fffffff - top * 0xffff - left;
		}
		/*
		PageImage pi = component.getPageImage();
		if( pi != null ){
			int pageWidth = pi.getWidth();
			int pageHeight = pi.getHeight();
			priority = pageWidth*pageHeight - top*pageWidth - left;
		}
		else{
			priority = DEFAULT_PRIORITY;
		}
		*/
	}

	protected abstract void setRecommendations() throws LowVisionProblemException;
		
	public short getType() {
		return( LOWVISION_PROBLEM );
	}

	public LowVisionType getLowVisionType(){
		return( lowVisionType );
	}

	// LowVision Error type (Color, Blur, etc.)
	public short getLowVisionProblemType(){
		return( problemType );
	}
	public String getDescription() throws LowVisionProblemException{
		return( description );
	}
	public PageImage getPageImage(){
		return( pageImage );
	}
	public int getX(){
		return( left );
	}
	public int getY(){
		return( top );
	}
	public int getWidth(){
		return( width );
	}
	public int getHeight(){
		return( height );
	}
	public int getPriority(){
		return( priority );
	}
	public double getProbability(){
		return( probability );
	}
	public int getIntProbability(){
		return( (int)(Math.rint(probability*100.0)) );
	}
	public double getCharacterScore(){
		return( characterScore );
	}
	public LowVisionRecommendation[] getRecommendations(){
		return( recommendations );
	}
	public boolean isGroup(){
		return( isGroupFlag );
	}
	public short getComponentType() throws LowVisionProblemException{
		if( !isGroupFlag ){
			return( componentType );
		}
		else{
			throw new LowVisionProblemException( "componentType cannot be gotten from a ProblemGroup." );
		}
	}
	public PageComponent getPageComponent() throws LowVisionProblemException{
		if( !isGroupFlag ){
			return( pageComponent );
		}
		else{
			throw new LowVisionProblemException( "component cannot be gotten from a ProblemGroup." );
		}
	}

	public PageElement getPageElement(){
		return( pageElement );
	}

	public String toString(){
		String compTypeString = null;
		if( componentType == PageComponent.SS_CHARACTER_TYPE ){
			compTypeString = "(SS)";
		}
		else if( componentType == PageComponent.MS_CHARACTER_TYPE ){
			compTypeString = "(MS)";
		}
		else if( componentType == PageComponent.SM_CHARACTER_TYPE ){
			compTypeString = "(SM)";
		}
		else{
			compTypeString = ""+componentType;
		}
		StringBuffer sb = new StringBuffer();
		sb.append( "Description=" + description );
		sb.append( compTypeString );
		sb.append( ", " );
		sb.append( "(x,y)=(" + left + "," + top + ")" );
		sb.append( ", " );
		sb.append( "[WIDTH x HEIGHT]=[" + width + " x " + height + "]" );
		sb.append( ", " );
		sb.append( "Probability=" + (int)(Math.rint(probability*100.0)) );
		sb.append( ", " );
		sb.append( "#Recommendations=" + numRecommendations );
		return( sb.toString() );
	}
	
	public void dump( PrintStream _ps, boolean _doRecommendations ) throws LowVisionProblemException{
		PrintWriter pw = new PrintWriter( _ps, true );
		dump( pw, _doRecommendations );
	}
	public void dump( PrintWriter _pw, boolean _doRecommendations ) throws LowVisionProblemException{
		_pw.println( "----------" );
		_pw.println( "dumping a problem");
		_pw.println( "problemType = " + problemType );
		_pw.println( "componentType = " + componentType );
		_pw.println( "description = " + getDescription() );
		_pw.println( "(x,y) = ( " + getX() + ", " + getY() + ")" );
		_pw.println( "width, height = " + getWidth() + ", " + getHeight() );
		_pw.println( "priority = " + getPriority() );
		LowVisionRecommendation[] recs = getRecommendations();
		if( recs != null ){
			_pw.println( "# of Recommendations = " + recs.length );
		}
		else{
			_pw.println( "Recommendations are null." );
		}
		if( _doRecommendations && recs != null ){
			for( int i=0; i<recs.length; i++ ){
				_pw.println( "Recommendation #" + i );
				recs[i].dump(_pw);
			}
		}
		_pw.println( "----------" );
	}

	private static final int[] PROBLEM_COLORS = {
		0x00ffffff, 0x00ff0000, 0x0000ff00
	};
	
	public void drawSurroundingBox( Int2D _img ){
		int x0 = getX();
		int y0 = getY();
		int x1 = x0+getWidth();
		int y1 = y0+getHeight();
		int color = PROBLEM_COLORS[problemType];
		for( int i=x0; i<x1; i++ ){
			_img.data[y0][i] = color;
			_img.data[y1-1][i] = color;
		}
		for( int j=y0; j<y1; j++ ){
			_img.data[j][x0] = color;
			_img.data[j][x1-1] = color;
		}
	}
	
	public static void drawAllSurroundingBoxes( LowVisionProblem[] _problems, Int2D _img ){
		for( int k=0; k<_problems.length; k++ ){
			_problems[k].drawSurroundingBox( _img );
		}
	}
}
