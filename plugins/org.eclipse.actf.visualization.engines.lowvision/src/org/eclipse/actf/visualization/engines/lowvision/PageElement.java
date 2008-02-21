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

package org.eclipse.actf.visualization.engines.lowvision;

import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.actf.model.ui.editor.browser.CurrentStyles;
import org.eclipse.actf.visualization.engines.lowvision.checker.W3CColorChecker;
import org.eclipse.actf.visualization.engines.lowvision.color.ColorCSS;
import org.eclipse.actf.visualization.engines.lowvision.color.ColorException;
import org.eclipse.actf.visualization.engines.lowvision.color.ColorIRGB;
import org.eclipse.actf.visualization.engines.lowvision.image.ImageException;
import org.eclipse.actf.visualization.engines.lowvision.internal.util.DebugUtil;
import org.eclipse.actf.visualization.engines.lowvision.problem.ColorProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.FixedSizeFontProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.FixedSmallFontProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.LowVisionProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.LowVisionProblemException;
import org.eclipse.actf.visualization.engines.lowvision.problem.ProhibitedBackgroundColorProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.ProhibitedBothColorsProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.ProhibitedForegroundColorProblem;
import org.eclipse.actf.visualization.engines.lowvision.problem.SmallFontProblem;
import org.eclipse.actf.visualization.engines.lowvision.util.LengthUtil;

/*
 * informations from HTML DOM
 */
public class PageElement {

	private static final String DELIM = "/";

	// text check
	private static final String[] nonTextTagNames = { "area", "base",
			"basefont", "br", "col", "colgroup", "frame", "frameset", "head",
			"html", "hr", "img", "input", "isindex", "link", "meta",
			"optgroup", "option", "param", "script", "select", "style",
			"textarea", "title" };

	// tags that change font size when that succeeded pt from <body>
	private static final String[] fontSizeChangeTags = { "big", "code", "h1",
			"h2", "h3", "h5", "h6", "kbd", "pre", "samp", "small", "sub",
			"sup", "tt" };

	/*
	 * tags that usually uses same font size (can change by using %,em,ex)
	 */
	private static final String[] alwaysFixedFontSizeTags = { "button",
			"option", "textarea" };

	public static final int UNSET_POSITION = -1;

	public static final int UNSET_COLOR = -1;

	private String id = null; // eclipse-actf-id

	private CurrentStyles style = null;

	private String tagName = null;

	// position in the image
	private int x = UNSET_POSITION;

	private int y = UNSET_POSITION;

	private int width = UNSET_POSITION;

	private int height = UNSET_POSITION;

	private int foregroundColor = UNSET_COLOR;

	private int backgroundColor = UNSET_COLOR;

	public PageElement(String _key, CurrentStyles _cs) throws ImageException {
		id = _key;
		style = _cs;

		tagName = style.getTagName().toLowerCase();
		setDimension();
		setColors();
	}

	private void setDimension() {
		x = Integer.parseInt(style.getOffsetLeft());
		y = Integer.parseInt(style.getOffsetTop());
		width = Integer.parseInt(style.getOffsetWidth());
		height = Integer.parseInt(style.getOffsetHeight());
	}

	private void setColors() throws ImageException {
		if (!isTextTag()) {
			return;
		}

		String fgStr = style.getColor();
		String bgStr = style.getBackgroundColor();
		try {
			foregroundColor = (new ColorCSS(fgStr)).toInt();
			backgroundColor = (new ColorCSS(bgStr)).toInt();
		} catch (ColorException e) {
			e.printStackTrace();
			throw new ImageException("Could not interpret colors.");
		}
	}

	public int getX() {
		return (x);
	}

	public int getY() {
		return (y);
	}

	public int getWidth() {
		return (width);
	}

	public int getHeight() {
		return (height);
	}

	public String getTagName() {
		return (tagName);
	}

	public int getForegroundColor() {
		return (foregroundColor);
	}

	public int getBackgroundColor() {
		return (backgroundColor);
	}

	// _lvType: for LowVision error check
	// _targetPage: for guideline check
	public LowVisionProblem[] check(LowVisionType _lvType,
			TargetPage _targetPage) {
		Vector<LowVisionProblem> problemVec = new Vector<LowVisionProblem>();

		// ignore elements not in the rendered area
		if (x < 0 || y < 0) {
			return (new LowVisionProblem[0]);
		}

		ColorProblem cp = null;
		try {
			cp = checkColors(_lvType);
		} catch (LowVisionException e) {
			DebugUtil.errMsg(this, "Error occurred in checking colors: id = "
					+ this.id);
			e.printStackTrace();
		}
		if (cp != null) {
			problemVec.addElement(cp);
		}

		FixedSizeFontProblem fsfp = null;
		try {
			fsfp = checkFixedSizeFont(_lvType);
		} catch (LowVisionException e) {
			DebugUtil.errMsg(this,
					"Error occurred in checking fixed-size font: id = "
							+ this.id);
			e.printStackTrace();
		}

		SmallFontProblem sfp = null;
		try {
			sfp = checkSmallFont(_lvType);
		} catch (LowVisionException e) {
			DebugUtil.errMsg(this,
					"Error occurred in checking small font: id = " + this.id);
			e.printStackTrace();
		}

		if (fsfp != null && sfp != null) {
			// // calc severity
			// double proba = Math.max( fsfp.getProbability(),
			// sfp.getProbability() );

			// use fixed severity
			double proba = LowVisionCommon.SEVERITY_FIXED_SMALL_FONT;
			FixedSmallFontProblem newProblem = null;
			try {
				newProblem = new FixedSmallFontProblem(this, _lvType, proba);
				problemVec.addElement(newProblem);
			} catch (LowVisionProblemException e) {
				e.printStackTrace();
			}
		} else if (fsfp != null) {
			problemVec.addElement(fsfp);
		} else if (sfp != null) {
			problemVec.addElement(sfp);
		}

		String[] allowedForegroundColors = _targetPage
				.getAllowedForegroundColors();
		String[] allowedBackgroundColors = _targetPage
				.getAllowedBackgroundColors();
		ProhibitedForegroundColorProblem pfcp = null;
		ProhibitedBackgroundColorProblem pbcp = null;

		if (allowedForegroundColors != null
				&& allowedForegroundColors.length > 0) {
			try {
				pfcp = checkAllowedForegroundColors(_lvType,
						allowedForegroundColors);
			} catch (LowVisionException lve) {
				lve.printStackTrace();
			}
		}
		if (allowedBackgroundColors != null
				&& allowedBackgroundColors.length > 0) {
			try {
				pbcp = checkAllowedBackgroundColors(_lvType,
						allowedBackgroundColors);
			} catch (LowVisionException lve) {
				lve.printStackTrace();
			}
		}
		if ((pfcp != null) && (pbcp != null)) {// fg/bg
			try {
				problemVec.addElement(new ProhibitedBothColorsProblem(this,
						_lvType,
						LowVisionCommon.SEVERITY_PROHIBITED_BOTH_COLORS));
			} catch (LowVisionProblemException lvpe) {
				lvpe.printStackTrace();
			}
			pfcp = null;
			pbcp = null;
		} else if (pfcp != null) { // fg
			problemVec.addElement(pfcp);
		} else if (pbcp != null) { // bg
			problemVec.addElement(pbcp);
		}

		int size = problemVec.size();
		LowVisionProblem[] problemArray = new LowVisionProblem[size];
		for (int i = 0; i < size; i++) {
			problemArray[i] = (LowVisionProblem) (problemVec.elementAt(i));
		}
		return (problemArray);
	}

	public ColorProblem checkColors(LowVisionType _lvType)
			throws LowVisionException {
		if (!(_lvType.doChangeColors())) {
			return (null);
		}
		if (!isTextTag()) {
			return (null);
		}

		ColorIRGB fgSim = null;
		ColorIRGB bgSim = null;
		try {
			fgSim = new ColorIRGB(_lvType.convertColor(foregroundColor));
			bgSim = new ColorIRGB(_lvType.convertColor(backgroundColor));
		} catch (LowVisionException e) {
			e.printStackTrace();
			throw new LowVisionException("Could not convert colors.");
		}

		W3CColorChecker w3c = new W3CColorChecker(fgSim, bgSim);
		double severity = w3c.calcSeverity();
		if (severity <= 0.0) {
			return (null);
		} else {
			try {
				return (new ColorProblem(this, _lvType, severity));
			} catch (LowVisionProblemException e) {
				e.printStackTrace();
				return (null);
			}
		}
	}

	/*
	 * fixed size font check experimental result (in IE6) (1)mm,cm,in,pt,pc,px ->
	 * fixed (2)larger,smaller, xx-small to xx-large -> variable (3)em,ex,% ->
	 * same with parent (if not specified in ancestor -> variable)
	 * 
	 * if <BODY> uses "pt" -> consider IE added this configuration
	 * 
	 * private static final Pattern regexFontSequence =
	 * Pattern.compile("^(([^\\/]+\\/)*([^\\/]+))$" );
	 * 
	 */
	private static final short FONT_SIZE_UNKNOWN = 0;

	private static final short FONT_SIZE_FIXED = 1; // in, cm, mm, pc, px

	private static final short FONT_SIZE_PT = 2; // pt

	private static final short FONT_SIZE_RELATIVE = 3; // smaller, larger

	// xx-small,..., xx-large
	private static final short FONT_SIZE_ABSOLUTE = 4;

	private static final short FONT_SIZE_PERCENT = 5; // %

	private static final short FONT_SIZE_EM = 6; // em, ex

	public FixedSizeFontProblem checkFixedSizeFont(LowVisionType _lvType)
			throws LowVisionException {
		if (!(_lvType.doBlur())) {
			return (null);
		}

		if (!isTextTag()) {
			return (null);
		}

		// difficult to change font size
		if (isAlwaysFixedSizeFontTag(tagName)) {
			return (null);
		}

		String fontStr = style.getFontSize().toLowerCase();

		// directly under the <BODY>
		if (fontStr.indexOf(DELIM) == -1) {
			fontStr = digitToFontSetting(fontStr);
			short type = fontSizeType(fontStr);
			if (type == FONT_SIZE_FIXED) { // not include "pt"
				try {
					return (new FixedSizeFontProblem(this, _lvType,
							LowVisionCommon.SEVERITY_FIXED_SIZE_FONT));
				} catch (LowVisionProblemException e) {
					e.printStackTrace();
					return (null);
				}
			} else {
				return (null);
			}
		}

		boolean fixedFlag = false;
		StringTokenizer st = new StringTokenizer(fontStr, DELIM);
		int tokenCount = st.countTokens();
		String myFont = digitToFontSetting(st.nextToken());
		short myType = fontSizeType(myFont);
		if (myType == FONT_SIZE_FIXED) {
			fixedFlag = true;
		} else if (myType == FONT_SIZE_RELATIVE || myType == FONT_SIZE_ABSOLUTE) {
			// fixedFlag = false;
		} else { // "pt", "em", "ex", "%"
			String[] fontSequence = new String[tokenCount];
			fontSequence[tokenCount - 1] = myFont;
			for (int i = tokenCount - 2; i >= 0; i--) {
				fontSequence[i] = digitToFontSetting(st.nextToken());
			}
			StringTokenizer stTag = new StringTokenizer(tagName, DELIM);
			if (stTag.countTokens() != tokenCount) {
				throw new LowVisionException(
						"# of tagNames and fontSizes did not match.");
			}
			String[] tagNameSequence = new String[tokenCount];
			for (int i = tokenCount - 1; i >= 0; i--) {
				tagNameSequence[i] = stTag.nextToken();
			}

			// fixedFlag = false;
			String curFont = fontSequence[0]; // <BODY>
			short curType = fontSizeType(curFont);
			boolean firstPtFlag = true;

			// if( curType == FONT_SIZE_PARENT ){
			// firstPtFlag = false;
			// }else if( curType == FONT_SIZE_FIXED ){
			// fixedFlag = true;
			// }
			if (curType != FONT_SIZE_PT) {
				firstPtFlag = false;
			}
			if (curType == FONT_SIZE_FIXED) {
				fixedFlag = true;
			}

			for (int i = 1; i < tokenCount; i++) {
				String tmpFont = fontSequence[i];
				String tmpTag = tagNameSequence[i];
				// <TD>,<TH> -> same initialization at <BODY>
				if (tmpTag.equals("td") || tmpTag.equals("th")) {
					firstPtFlag = true;
					if (curType != FONT_SIZE_PT) {
						firstPtFlag = false;
					}
					if (curType == FONT_SIZE_FIXED) {
						fixedFlag = true;
					}
				} else {
					if (curFont.equals(tmpFont)) { // not defined by user
						continue;
					} else {
						short tmpType = fontSizeType(tmpFont);
						if (tmpType == FONT_SIZE_FIXED) {
							fixedFlag = true;
							firstPtFlag = true;
						} else if (tmpType == FONT_SIZE_RELATIVE
								|| tmpType == FONT_SIZE_ABSOLUTE) {
							fixedFlag = false;
							firstPtFlag = true;
						} else if (tmpType == FONT_SIZE_PT) {
							if (!firstPtFlag) {
								firstPtFlag = true;
								fixedFlag = false; // need check
							} else if (curType != FONT_SIZE_PT
									|| fixedFlag == true
									|| !isFontSizeChangeTag(tmpTag)) {
								fixedFlag = true;
							}
							// else{
							// // "pt" & parent "pt" & variable & <PRE> etc. ->
							// variable
							// }
						}
						// else{
						// // "em", "ex", "%"-> same as parent
						// }
						curFont = tmpFont;
						curType = tmpType;
					}
				}
			}
		}

		if (fixedFlag) {
			try {
				return (new FixedSizeFontProblem(this, _lvType,
						LowVisionCommon.SEVERITY_FIXED_SIZE_FONT));
			} catch (LowVisionProblemException e) {
				e.printStackTrace();
				return (null);
			}
		} else {
			return (null);
		}
	}

	private short fontSizeType(String _fontSize) {
		String s = _fontSize.toLowerCase();

		if (s.endsWith("mm") || s.endsWith("cm") || s.endsWith("in") ||
		// s.endsWith("pt") || // pt is special
				s.endsWith("pc") || s.endsWith("px")) {
			return (FONT_SIZE_FIXED);
		} else if (s.endsWith("pt")) {
			return (FONT_SIZE_PT);
		} else if (s.endsWith("%")) {
			return (FONT_SIZE_PERCENT);
		} else if (s.endsWith("em") || s.endsWith("ex")) {
			return (FONT_SIZE_EM);
		} else if (s.equals("smaller") || s.equals("larger")) {
			return (FONT_SIZE_RELATIVE);
		} else {
			return (FONT_SIZE_ABSOLUTE);
		}
	}

	private String digitToFontSetting(String _fontStr)
			throws LowVisionException {

		if (_fontStr.length() == 1) {
			if (_fontStr.equals("1")) {
				return ("xx-small");
			} else if (_fontStr.equals("2")) {
				return ("x-small");
			} else if (_fontStr.equals("3")) {
				return ("small");
			} else if (_fontStr.equals("4")) {
				return ("medium");
			} else if (_fontStr.equals("5")) {
				return ("large");
			} else if (_fontStr.equals("6")) {
				return ("x-large");
			} else if (_fontStr.equals("7")) {
				return ("xx-large");
			} else {
				throw new LowVisionException("Invalid font size setting: "
						+ _fontStr);
			}
		} else if (_fontStr.startsWith("+")) {
			if (_fontStr.equals("+1")) {
				return ("120%");
			} else if (_fontStr.equals("+2")) {
				return ("144%");
			} else if (_fontStr.equals("+3")) {
				return ("173%");
			} else if (_fontStr.equals("+4")) {
				return ("207%");
			} else if (_fontStr.equals("+5")) {
				return ("249%");
			} else if (_fontStr.equals("+6")) {
				return ("299%");
			} else if (_fontStr.equals("+0")) {
				// used in some pages
				return ("100%");
			} else {
				throw new LowVisionException("Invalid font size setting: "
						+ _fontStr);
			}
		} else if (_fontStr.startsWith("-")) {
			if (_fontStr.equals("-1")) {
				return ("83%");
			} else if (_fontStr.equals("-2")) {
				return ("69%");
			} else if (_fontStr.equals("-3")) {
				return ("58%");
			} else if (_fontStr.equals("-4")) {
				return ("48%");
			} else if (_fontStr.equals("-5")) {
				return ("40%");
			} else if (_fontStr.equals("-6")) {
				return ("33%");
			} else if (_fontStr.equals("-0")) {
				return ("100%");
			} else {
				throw new LowVisionException("Invalid font size setting: "
						+ _fontStr);
			}
		} else {
			return (_fontStr);
		}
	}

	private boolean isFontSizeChangeTag(String _st) {
		int len = fontSizeChangeTags.length;
		String s = _st.toLowerCase();
		for (int i = 0; i < len; i++) {
			if (s.equals(fontSizeChangeTags[i])) {
				return (true);
			}
		}
		return (false);
	}

	private boolean isAlwaysFixedSizeFontTag(String _st) {
		String s = _st.toLowerCase();
		int index = s.indexOf(DELIM);
		if (index > -1) {
			s = s.substring(0, index);
		}
		int len = alwaysFixedFontSizeTags.length;
		for (int i = 0; i < len; i++) {
			if (s.equals(alwaysFixedFontSizeTags[i])) {
				return (true);
			}
		}
		return (false);
	}

	/*
	 * note: reset at td/th is experimental behaviour in IE6
	 */
	public SmallFontProblem checkSmallFont(LowVisionType _lvType)
			throws LowVisionException {
		if (!(_lvType.doBlur())) {
			return (null);
		}

		double eyesightLength = _lvType.getEyesightLength();

		if (!isTextTag()) {
			return (null);
		}

		/*
		 * TODO <OPTION>: offsetWidth, offsetHeight = 0 -> can't highlight
		 * target
		 * 
		 * need to select parent <SELECT>
		 */
		if (tagName.startsWith("option")) {
			return (null);
		}

		String fontStr = style.getFontSize().toLowerCase();

		// reset at TD/TH
		StringTokenizer fontSt = new StringTokenizer(fontStr, DELIM);
		Vector<String> fontSequence = new Vector<String>();
		StringTokenizer tagSt = new StringTokenizer(tagName, DELIM);
		String curFont = fontSt.nextToken();
		String curTag = tagSt.nextToken();
		fontSequence.addElement(curFont);
		while (fontSt.hasMoreTokens()) {
			String tmpFont = fontSt.nextToken();
			curTag = tagSt.nextToken();
			if (curTag.equals("td") || curTag.equals("th")) {
				if (!(tmpFont.equals(curFont))) {
					fontSequence.addElement(tmpFont);
				}
				break;
			} else {
				if (!(tmpFont.equals(curFont))) {
					fontSequence.addElement(tmpFont);
				}
			}
			curFont = tmpFont;
		}

		int numFontSizeSettings = fontSequence.size();
		String[] fontSizeSettings = new String[numFontSizeSettings];
		for (int i = 0; i < numFontSizeSettings; i++) {
			String tmpSetting = (String) (fontSequence.elementAt(i));
			fontSizeSettings[i] = digitToFontSetting(tmpSetting);
		}
		fontSequence = null;

		/*
		 * if last value is "pt"-> consider the automatic setting by IE
		 * 
		 * define LARGEST as default to check the small size font in the LARGEST
		 * setting
		 * 
		 */
		String curFontSize = fontSizeSettings[numFontSizeSettings - 1];
		if (fontSizeType(curFontSize) == FONT_SIZE_PT) {
			fontSizeSettings[numFontSizeSettings - 1] = LowVisionCommon.IE_LARGEST_FONT;
			for (int i = numFontSizeSettings - 2; i >= 0; i--) {
				if (fontSizeSettings[i].equals(curFontSize)) {
					fontSizeSettings[i] = LowVisionCommon.IE_LARGEST_FONT;
				} else {
					break;
				}
			}
		}

		float scaling = 1.0f; // smaller, larger, em, ex, %
		short curType = FONT_SIZE_UNKNOWN;
		for (int i = 0; i < numFontSizeSettings; i++) {
			curFontSize = fontSizeSettings[i];
			curType = fontSizeType(curFontSize);
			if (curType == FONT_SIZE_FIXED || curType == FONT_SIZE_PT) {
				break;
			} else if (curType == FONT_SIZE_ABSOLUTE) {
				if (curFontSize.equals("xx-large")) {
					curFontSize = "48pt";
				} else if (curFontSize.equals("x-large")) {
					curFontSize = "32pt";
				} else if (curFontSize.equals("large")) {
					curFontSize = "24pt";
				} else if (curFontSize.equals("medium")) {
					curFontSize = "18pt";
				} else if (curFontSize.equals("small")) {
					curFontSize = "16pt";
				} else if (curFontSize.equals("x-small")) {
					curFontSize = "14pt";
				} else if (curFontSize.equals("xx-small")) {
					curFontSize = "12pt";
				}
				break;
			} else if (curType == FONT_SIZE_PERCENT) {
				double value = Double.parseDouble(curFontSize.substring(0,
						curFontSize.length() - 1));
				scaling *= (value / 100.0);
			} else if (curType == FONT_SIZE_EM) {
				double value = 0.0;
				value = Double.parseDouble(curFontSize.substring(0, curFontSize
						.length() - 2));
				if (curFontSize.endsWith("ex")) {
					value /= 2.0;
				}
				scaling *= (value * LowVisionCommon.IE_EM_SCALING);
			} else if (curFontSize.equals("larger")) {
				scaling *= LowVisionCommon.IE_LARGER_SCALING;
			} else if (curFontSize.equals("smaller")) {
				scaling *= LowVisionCommon.IE_SMALLER_SCALING;
			} else {
				throw new LowVisionException("unknown font size setting: "
						+ curFontSize);
			}
		}
		if (curType != FONT_SIZE_FIXED && curType != FONT_SIZE_PT
				&& curType != FONT_SIZE_ABSOLUTE) {
			curFontSize = LowVisionCommon.IE_LARGEST_FONT;
		}

		float value = Float.parseFloat(curFontSize.substring(0, curFontSize
				.length() - 2));
		float sizeInMm = 0.0f;
		if (curFontSize.endsWith("in")) {
			sizeInMm = LengthUtil.in2mm(value);
		} else if (curFontSize.endsWith("cm")) {
			sizeInMm = LengthUtil.cm2mm(value);
		} else if (curFontSize.endsWith("mm")) {
			sizeInMm = value;
		} else if (curFontSize.endsWith("pt")) {
			sizeInMm = LengthUtil.pt2mm(value);
		} else if (curFontSize.endsWith("pc")) {
			sizeInMm = LengthUtil.pc2mm(value);
		} else if (curFontSize.endsWith("px")) {
			sizeInMm = LengthUtil.px2mm(value);
		} else {
			throw new LowVisionException("unknown font size unit: "
					+ curFontSize);
		}
		sizeInMm *= scaling;

		// can distinguish "c" and "o"?
		// size of "c" is about half of char size
		// disconnected part of "c" is about 1/5 of "c"
		double severity = 2.0 - sizeInMm / (10.0 * eyesightLength);
		if (severity > 1.0)
			severity = 1.0;
		else if (severity < 0.0)
			severity = 0.0;

		if (severity > 0.0) {
			try {
				// fixed severity
				return (new SmallFontProblem(this, _lvType,
						LowVisionCommon.SEVERITY_SMALL_FONT));
			} catch (LowVisionProblemException e) {
				e.printStackTrace();
				return (null);
			}
		} else {
			return (null);
		}
	}

	public ProhibitedForegroundColorProblem checkAllowedForegroundColors(
			LowVisionType _lvType, String[] _allowedColors)
			throws LowVisionException {
		if (_allowedColors == null) {
			return (null);
		}
		int len = _allowedColors.length;
		if (len == 0) {
			return (null);
		}

		if (!isTextTag()) {
			return (null);
		}

		// TODO check link color?
		if (tagName.startsWith("a/")) {
			return (null);
		}

		// use "black" as default
		// TODO use system color
		if (foregroundColor == ColorCSS.DEFAULT_COLOR_INT) {
			return (null);
		}

		for (int i = 0; i < len; i++) {
			String curColorString = _allowedColors[i];
			ColorIRGB templateColor = null;
			try {
				templateColor = new ColorIRGB(curColorString);
			} catch (ColorException ce) {
				ce.printStackTrace();
				throw new LowVisionException(
						"ColorException occurs while converting String \""
								+ curColorString + "\" to a color.");
			}
			if (templateColor.equals(foregroundColor)) {
				return (null);
			}
		}

		try {
			return (new ProhibitedForegroundColorProblem(this, _lvType,
					LowVisionCommon.SEVERITY_PROHIBITED_FOREGROUND_COLOR));
		} catch (LowVisionProblemException lvpe) {
			lvpe.printStackTrace();
			return (null);
		}
	}

	public ProhibitedBackgroundColorProblem checkAllowedBackgroundColors(
			LowVisionType _lvType, String[] _allowedColors)
			throws LowVisionException {
		if (_allowedColors == null) {
			return (null);
		}
		int len = _allowedColors.length;
		if (len == 0) {
			return (null);
		}

		if (!isTextTag()) {
			return (null);
		}

		// use transparent as defaul background-color
		if (backgroundColor == ColorCSS.DEFAULT_BACKGROUND_COLOR_INT) {
			return (null);
		}

		for (int i = 0; i < len; i++) {
			String curColorString = _allowedColors[i];
			ColorIRGB templateColor = null;
			try {
				templateColor = new ColorIRGB(curColorString);
			} catch (ColorException ce) {
				ce.printStackTrace();
				throw new LowVisionException(
						"ColorException occurs while converting String \""
								+ curColorString + "\" to a color.");
			}
			if (templateColor.equals(backgroundColor)) {
				return (null);
			}
		}
		try {
			return (new ProhibitedBackgroundColorProblem(this, _lvType,
					LowVisionCommon.SEVERITY_PROHIBITED_BACKGROUND_COLOR));
		} catch (LowVisionProblemException lvpe) {
			lvpe.printStackTrace();
			return (null);
		}
	}

	private boolean isTextTag() {
		String tagName = style.getTagName().toLowerCase();
		int len = nonTextTagNames.length;
		for (int i = 0; i < len; i++) {
			if (tagName.startsWith(nonTextTagNames[i] /* +"/" */)) {
				return (false);
			}
			// if( tagName.equals( nonTextTagNames[i] ) ){
			// return( false );
			// }
		}
		return (true);
	}

}
