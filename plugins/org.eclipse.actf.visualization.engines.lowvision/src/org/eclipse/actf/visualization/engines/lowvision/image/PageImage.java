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
package org.eclipse.actf.visualization.engines.lowvision.image;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.actf.model.ui.ImagePositionInfo;
import org.eclipse.actf.visualization.engines.lowvision.LowVisionCommon;
import org.eclipse.actf.visualization.engines.lowvision.LowVisionType;
import org.eclipse.actf.visualization.engines.lowvision.character.CandidateCharacter;
import org.eclipse.actf.visualization.engines.lowvision.character.CandidateUnderlinedCharacter;
import org.eclipse.actf.visualization.engines.lowvision.character.CharacterMS;
import org.eclipse.actf.visualization.engines.lowvision.character.CharacterSM;
import org.eclipse.actf.visualization.engines.lowvision.character.CharacterSS;
import org.eclipse.actf.visualization.engines.lowvision.checker.CharacterChecker;
import org.eclipse.actf.visualization.engines.lowvision.internal.util.DebugUtil;
import org.eclipse.actf.visualization.engines.lowvision.io.ImageReader;
import org.eclipse.actf.visualization.engines.lowvision.io.LowVisionIOException;
import org.eclipse.actf.visualization.engines.lowvision.problem.LowVisionProblemException;
import org.eclipse.actf.visualization.engines.lowvision.problem.LowVisionProblemGroup;
import org.eclipse.actf.visualization.engines.lowvision.util.DecisionMaker;

/*
 * Rednered image of Web page
 * 
 */
public class PageImage implements LowVisionCommon {
	private static final boolean DO_CHECK_CHARACTERS = false;

	private static final boolean DO_CHECK_IMAGES = true;

	// private static final boolean DO_CHAR_TEST = false;

	Int2D pixel = null;

	int numContainers; // containers.length;

	Container[] containers;

	int numNonContainedCharacters; // nonContainedCharacters.length

	CharacterSM[] nonContainedCharacters; // SM Char

	boolean extractedFlag = false; //

	ImagePositionInfo[] imagePositions = null;

	boolean useImagePositions = false;

	InteriorImage[] interiorImageArray = null;

	int currentContainerID = 1;

	int[][] containerMap = null;

	// for debug (TBD move back into extractCharacters())
	Vector<Container> containerVector = new Vector<Container>();

	Vector<CandidateCharacter> candidateCharacterVector = new Vector<CandidateCharacter>();

	Vector<CandidateUnderlinedCharacter> candidateUnderlinedCharacterVector = new Vector<CandidateUnderlinedCharacter>();

	PrintWriter writer = null;

	public PageImage() {
	}

	public PageImage(Int2D _i2d) {
		this(_i2d, true);
	}

	public PageImage(Int2D _i2d, boolean _removeScrollBar) {
		Int2D i2d = null;
		if (_removeScrollBar) {
			if (LowVisionCommon.REMOVE_SURROUNDINGS) {
				try {
					i2d = _i2d.cutMargin(LowVisionCommon.SURROUNDINGS_WIDTH);
				} catch (ImageException ie) {
					// ie.printStackTrace();
					i2d = _i2d;
				}
			} else {
				i2d = _i2d;
			}
			if (LowVisionCommon.REMOVE_SCROLL_BAR_AT_RIGHT) {
				Int2D tmpI2d = new Int2D(i2d.width
						- LowVisionCommon.SCROLL_BAR_WIDTH, i2d.height);
				for (int j = 0; j < tmpI2d.height; j++) {
					for (int i = 0; i < tmpI2d.width; i++) {
						tmpI2d.data[j][i] = i2d.data[j][i];
					}
				}
				i2d = tmpI2d;
			}
			if (LowVisionCommon.REMOVE_SCROLL_BAR_AT_BOTTOM) {
				Int2D tmpI2d = new Int2D(i2d.width, i2d.height
						- LowVisionCommon.SCROLL_BAR_WIDTH);
				for (int j = 0; j < tmpI2d.height; j++) {
					for (int i = 0; i < tmpI2d.width; i++) {
						tmpI2d.data[j][i] = i2d.data[j][i];
					}
				}
				i2d = tmpI2d;
			}
		} else {
			i2d = _i2d;
		}

		pixel = i2d.deepCopy();
		i2d = null;
	}

	public void init(BufferedImage _bi) throws ImageException {
		pixel = ImageUtil.bufferedImageToInt2D(_bi);
	}

	// for debug
	public static PageImage readFromFile(String _fileName)
			throws LowVisionIOException {
		BufferedImage bi = ImageReader.readBufferedImage(_fileName);
		// PageImage pi = new PageImage( bi );
		Int2D i2d = new Int2D(bi);
		PageImage pi = new PageImage(i2d);
		return (pi);
	}

	public int getWidth() {
		return (pixel.width);
	}

	public int getHeight() {
		return (pixel.height);
	}

	public int[][] getPixelData() {
		return (pixel.data);
	}

	public BufferedImage getBufferedImage() {
		return (ImageUtil.int2DToBufferedImage(pixel));
	}

	public Int2D getInt2D() {
		return (pixel);
	}

	public void disposeInt2D() {
		pixel = new Int2D(0, 0);
	}

	public int getNumContainers() {
		return (numContainers);
	}

	public Container[] getContainers() {
		return (containers);
	}

	public int getNumSMCharacters() {
		return (numNonContainedCharacters);
	}

	public CharacterSM[] getSMCharacters() {
		return (nonContainedCharacters);
	}

	public int getNumNonContainedCharacters() {
		return numNonContainedCharacters;
	}

	public CharacterSM[] getNonContainedCharacters() {
		return nonContainedCharacters;
	}

	public void setWriter(PrintWriter _pw) {
		writer = _pw;
	}

	public ImagePositionInfo[] getInteriorImagePosition() {
		return (imagePositions);
	}

	public void setInteriorImagePosition(ImagePositionInfo[] infoArray) {
		if (infoArray != null) {
			imagePositions = infoArray;
		}
	}

	public boolean isInteriorImageArraySet() {
		if (interiorImageArray == null || interiorImageArray.length == 0) {
			return (false);
		} else {
			return (true);
		}
	}

	/*
	 * Estimate character/image positions in the PageImage.
	 */
	public void extractCharacters() throws ImageException {
		if (DO_CHECK_CHARACTERS) {
			extractAllCharacters();
		}

		if (DO_CHECK_IMAGES && imagePositions != null
				&& imagePositions.length > 0) {
			useImagePositions = true;

			// for memory (create InteriorImages for each time (degrade
			// performance))

			// extractInteriorImages();
		}
	}

	/*
	 * Extract all images in the page by using position info from DOM.
	 * 
	 */
	private void extractInteriorImages() {
		if (imagePositions == null)
			return;
		int numImages = imagePositions.length;
		Vector<InteriorImage> imageVector = new Vector<InteriorImage>();

		if (LowVisionCommon.REMOVE_SURROUNDINGS) {
			for (int k = 0; k < numImages; k++) {
				ImagePositionInfo pos = imagePositions[k];
				pos.setX(pos.getX() - LowVisionCommon.SURROUNDINGS_WIDTH);
				if (pos.getX() < 0) {
					pos.setX(0);
				}
				pos.setY(pos.getY() - LowVisionCommon.SURROUNDINGS_WIDTH);
				if (pos.getY() < 0) {
					pos.setY(0);
				}
			}
		}

		for (int k = 0; k < numImages; k++) {
			ImagePositionInfo curPos = imagePositions[k];

			// InteriorImagePosition contains all image info in the Web page.
			// However, PageImage only contains a part of Web page in the case
			// of partial image dump. So, select images within the dumped image.
			if (!isFullyContained(curPos)) {
				continue;
			}

			InteriorImage curIm = new InteriorImage(this, curPos);
			imageVector.addElement(curIm);
		}
		int size = imageVector.size();
		if (size > 0) {
			interiorImageArray = new InteriorImage[size];
			for (int k = 0; k < size; k++) {
				interiorImageArray[k] = (InteriorImage) (imageVector
						.elementAt(k));
			}
		}
	}

	private boolean isFullyContained(ImagePositionInfo _pos) {
		if (_pos.getX() + _pos.getWidth() > this.getWidth()) {
			return (false);
		}
		if (_pos.getY() + _pos.getHeight() > this.getHeight()) {
			return (false);
		}
		return (true);
	}

	private void extractAllCharacters() throws ImageException {
		if (extractedFlag) {
			return;
		}

		// TODO more improvement

		int numProcessedColors = 0;

		containerMap = new int[pixel.height][pixel.width];

		ColorHistogram histogram = ColorHistogram.makeColorHistogram(pixel);

		/*
		 * (1) estimate content type (container, char, etc.) from image by using
		 * connected components (each major color)
		 */
		int len = histogram.getSize();
		numProcessedColors = len;
		ColorHistogramBin[] histoArray = histogram.getSortedArrayByOccurrence();
		for (int i = 0; i < len; i++) {
			if (histoArray[i].occurrence < THRESHOLD_MIN_OCCURRENCES) {
				numProcessedColors = i;
				break;
			}
		}

		for (int i = 0; i < numProcessedColors; i++) {
			int curColor = histoArray[i].color;
			BinaryImage binaryByColor = new BinaryImage(pixel,
					BinaryImage.METHOD_SPECIFY_FOREGROUND, curColor);

			// connected components
			LabeledImage curLabeledImage = new LabeledImage(binaryByColor,
					LabeledImage.METHOD_8_CONNECTIVITY);

			int numComponents = curLabeledImage.numComponents;
			ConnectedComponent[] components = curLabeledImage.components;

			for (int j = 0; j < numComponents; j++) {
				ConnectedComponent cc = components[j];

				short type = DecisionMaker.judgeComponentType(cc, this, true);

				if (type == PageComponent.CONTAINER_TYPE) {
					Container tmpContainer = new Container(this,
							currentContainerID, cc, curColor);
					containerVector.addElement(tmpContainer);
					paintContainerMap(currentContainerID, cc);
					currentContainerID++;
				} else if (type == PageComponent.CANDIDATE_CHARACTER_TYPE) {
					CandidateCharacter tmpC = new CandidateCharacter(this, cc,
							curColor);
					candidateCharacterVector.addElement(tmpC);
				} else if (type == PageComponent.CANDIDATE_UNDERLINED_CHARACTER_TYPE) {
					CandidateUnderlinedCharacter tmpU = new CandidateUnderlinedCharacter(
							this, cc, curColor);
					candidateUnderlinedCharacterVector.addElement(tmpU);
				} else if (type == PageComponent.OTHER_TYPE) {
					;
				} else {
					throw new ImageException("Unexpected type = " + type);
				}
			}
		}

		// end (1)

		// mark out containerMap
		fillContainerMap(currentContainerID);

		/*
		 * (2) Assign CharacterCandidates to Container
		 */
		int numCandChar = candidateCharacterVector.size();
		for (int k = numCandChar - 1; k >= 0; k--) {
			CandidateCharacter cChar = (CandidateCharacter) (candidateCharacterVector
					.elementAt(k));
			int w = cChar.cc.shape.width;
			int i = 0;
			for (; i < w; i++) {
				if (cChar.cc.shape.data[0][i] != 0) {
					break;
				}
			}
			int id = containerMap[cChar.cc.top][cChar.cc.left + i];
			if (id > 0) {// id of Container starts from 1

				Container parentCont = containerVector.elementAt(id - 1);
				cChar.setContainer(parentCont);
				parentCont.candidateCharacterVector.addElement(cChar);
				candidateCharacterVector.removeElementAt(k);
			} // (id = 0) = does not belong to Container (= SM Char)
		}
		int numCandUnderChar = candidateUnderlinedCharacterVector.size();
		for (int k = numCandUnderChar - 1; k >= 0; k--) {
			CandidateUnderlinedCharacter cuChar = candidateUnderlinedCharacterVector
					.elementAt(k);
			int w = cuChar.cc.shape.width;
			int i = 0;
			for (; i < w; i++) {
				if (cuChar.cc.shape.data[0][i] != 0) {
					break;
				}
			}
			int id = containerMap[cuChar.cc.top][cuChar.cc.left + i];
			if (id > 0) {// id of Container starts from 1
				Container parentCont = containerVector.elementAt(id - 1);
				cuChar.setContainer(parentCont);
				parentCont.candidateUnderlinedCharacterVector
						.addElement(cuChar);
				candidateUnderlinedCharacterVector.removeElementAt(k);
			}// (id = 0) = does not belong to Container (= SM Char)
		}

		/*
		 * Other character candidates (do not belong to Container)
		 */
		Vector tmpSMCharacterVector = makeSMCharacterVector(
				candidateCharacterVector, candidateUnderlinedCharacterVector);
		this.candidateCharacterVector.removeAllElements();
		this.candidateUnderlinedCharacterVector.removeAllElements();
		int tmpTmpSMVecSize = tmpSMCharacterVector.size();
		for (int k = tmpTmpSMVecSize - 1; k >= 0; k--) {
			CharacterSM tmpSM = (CharacterSM) (tmpSMCharacterVector
					.elementAt(k));
			if (!(DecisionMaker.isSMCharacter(tmpSM))) {
				tmpSMCharacterVector.removeElementAt(k);
			}
		}
		this.numNonContainedCharacters = tmpSMCharacterVector.size();
		this.nonContainedCharacters = new CharacterSM[numNonContainedCharacters];
		for (int k = 0; k < numNonContainedCharacters; k++) {
			nonContainedCharacters[k] = (CharacterSM) (tmpSMCharacterVector
					.elementAt(k));
		}
		tmpSMCharacterVector.removeAllElements();
		tmpSMCharacterVector = null;

		/*
		 * (3) Container
		 */
		int numContainer = containerVector.size();
		for (int k = 0; k < numContainer; k++) {
			Container curCont = containerVector.elementAt(k);
			int contW = curCont.cc.shape.width;
			int contH = curCont.cc.shape.height;
			int contX = curCont.cc.left;
			int contY = curCont.cc.top;
			int contColor = curCont.getColor();
			BinaryImage contBin = new BinaryImage(contW, contH);

			// Container (filled hole)
			BinaryImage filledContBin = new BinaryImage(contW, contH);

			// HashMap contMap = new HashMap();
			// HashMap nonContMap = new HashMap();
			// Object dummy = new Object();
			for (int j = 0; j < contH; j++) {
				for (int i = 0; i < contW; i++) {
					int curPixel = pixel.data[j + contY][i + contX];
					// distinguishable from container color?
					try {
						if (curPixel == contColor) {// same
							contBin.data[j][i] = 1;
						}
						/*
						 * //TODO recover this? else{ Integer curInt = new
						 * Integer( curPixel ); if( contMap.get(curInt) != null ){ //
						 * similar contBin.data[j][i] = 1; continue; } if(
						 * nonContMap.get(curInt) != null ){ // differ continue; }
						 * //first time if(DecisionMaker.distinguishableColors(
						 * curPixel, contColor ) ){ // differ nonContMap.put(
						 * curInt, dummy ); } else{ // similar
						 * 
						 * contBin.data[j][i] = 1; contMap.put( curInt, dummy ); } }
						 */
					} catch (Exception e) {
						// e.printStackTrace();
						throw new ImageException(
								"An error occurred while making contBin.");
					}

					if (containerMap[j + contY][i + contX] == k + 1) {
						filledContBin.data[j][i] = 1;
					}
				}
			}
			BinaryImage fgBin = BinaryImage.subtract(filledContBin, contBin);

			// Find connected component
			LabeledImage curLabImg = new LabeledImage(fgBin,
					LabeledImage.METHOD_8_CONNECTIVITY);
			int numComponents = curLabImg.numComponents;
			if (numComponents == 0) {
				continue;
			}
			ConnectedComponent[] components = curLabImg.components;

			for (int l = numComponents - 1; l >= 0; l--) {
				ConnectedComponent cc2 = components[l];
				// convert to relative coordinates (Page)
				cc2.setLeft(cc2.getLeft() + contX);
				cc2.setTop(cc2.getTop() + contY);
				if (collateCandidates(curCont, cc2)) {
					// SS Char (or with underline)
					continue;
					// nothing to do here (already done in collateCandidates)
				} else if (DecisionMaker.isMSCharacter(cc2)) {
					int fg = -1;
					if ((fg = getForegroundColor(cc2)) == -1) {
						CharacterMS msc = new CharacterMS(this, cc2, curCont,
								pixel);
						curCont.msCharacterVector.addElement(msc);
					} else {
						/*
						 * to handle character written by using minor color in
						 * histogram
						 */
						short ssType = DecisionMaker.judgeComponentType(cc2,
								this);
						if (ssType == PageComponent.CANDIDATE_CHARACTER_TYPE) {
							CharacterSS ssc = new CharacterSS(this, cc2,
									curCont, fg);
							curCont.ssCharacterVector.addElement(ssc);
						} else if (ssType == PageComponent.CANDIDATE_UNDERLINED_CHARACTER_TYPE) {
							CandidateUnderlinedCharacter cuc = new CandidateUnderlinedCharacter(
									this, cc2, fg);
							cuc.setContainer(curCont);
							Vector sscVec = removeUnderlineAndGenerateSS(cuc);
							for (int m = 0; m < sscVec.size(); m++) {
								curCont.ssCharacterVector
										.addElement((CharacterSS) (sscVec
												.elementAt(m)));
							}
							sscVec.removeAllElements();
						}
					}
				}
			}

			// remaining candidates -> SM Character
			// TODO check more
			Vector tmpVec = makeSMCharacterVector(
					curCont.candidateCharacterVector,
					curCont.candidateUnderlinedCharacterVector);
			curCont.candidateCharacterVector.removeAllElements();
			curCont.candidateUnderlinedCharacterVector.removeAllElements();
			int tmpVecSize = tmpVec.size();
			for (int l = tmpVecSize - 1; l >= 0; l--) {
				CharacterSM smc = (CharacterSM) (tmpVec.elementAt(l));
				if (smc.getForegroundColor() == curCont.getColor()) {
					// remove elements (same color with Container)
					// (e.g., hole of 'A','R' etc.)
					tmpVec.removeElementAt(l);
				} else if (includingMSCharacter(smc, curCont) != null) {
					// remove elements contained in MS Char (*)
					tmpVec.removeElementAt(l);
				} else {
					// check SS Character which has similar fg/bg color
					// ((1)->OK but (3)->NG)
					if (getBackgroundColor(smc.cc) > -1) {
						CharacterSS ssc = new CharacterSS(this, smc.cc,
								smc.container, smc.getForegroundColor());
						curCont.ssCharacterVector.addElement(ssc);
						tmpVec.removeElementAt(l);
					}
				}
			}

			/*
			 * Check very small MS Char. Need to do it after (*)
			 */
			int msVecSize = curCont.msCharacterVector.size();
			for (int l = msVecSize - 1; l >= 0; l--) {
				CharacterMS curMS = (CharacterMS) (curCont.msCharacterVector
						.elementAt(l));
				if (DecisionMaker.isTooSmallThinedMSCharacter(curMS)) {
					curCont.msCharacterVector.removeElementAt(l);
				}
			}

			curCont.ssVector2Array(); // ssCharacterVector->ssCharacters

			curCont.msVector2Array(); // msCharacterVector->msCharacters

			int tmptmpVecSize = tmpVec.size();
			for (int l = tmptmpVecSize - 1; l >= 0; l--) {
				CharacterSM tmpSM = (CharacterSM) (tmpVec.elementAt(l));
				if (!(DecisionMaker.isSMCharacter(tmpSM))) {
					tmpVec.removeElementAt(l);
				}
			}

			curCont.numSMCharacters = tmpVec.size();
			curCont.smCharacters = new CharacterSM[curCont.numSMCharacters];
			for (int l = 0; l < curCont.numSMCharacters; l++) {
				curCont.smCharacters[l] = (CharacterSM) (tmpVec.elementAt(l));
			}
			tmpVec.removeAllElements();
			tmpVec = null;
		}
		// end (3)

		// remove Containers without Character
		for (int k = numContainer - 1; k >= 0; k--) {
			Container curCont = containerVector.elementAt(k);
			if (curCont.numSSCharacters == 0 && curCont.numMSCharacters == 0
					&& curCont.numSMCharacters == 0) {
				containerVector.removeElementAt(k);
			}
		}
		numContainers = containerVector.size();
		containers = new Container[numContainers];
		for (int k = 0; k < numContainers; k++) {
			containers[k] = containerVector.elementAt(k);
		}
		containerVector.removeAllElements();

		extractedFlag = true;

	}

	// paint Container into ContainerMap
	private void paintContainerMap(int _id, ConnectedComponent _cc) {
		int w = _cc.shape.width;
		int h = _cc.shape.height;
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				if (_cc.shape.data[j][i] != 0) {
					containerMap[_cc.top + j][_cc.left + i] = _id;
				}
			}
		}
	}

	/*
	 * fill hole of ContainerMap
	 */
	private void fillContainerMap(int _lastID) throws ImageException {
		// ID=0 -> not in Container
		for (int i = 1; i < _lastID; i++) {// Container ID starts from 1
			fillOneContainer(i);
		}
	}

	private void fillOneContainer(int _id) throws ImageException {
		Container curCont = (Container) (containerVector.elementAt(_id - 1));
		int curX0 = curCont.cc.left;
		int curY0 = curCont.cc.top;
		int curX1 = curX0 + curCont.cc.shape.width;
		int curY1 = curY0 + curCont.cc.shape.height;

		// left boundary=1,
		// right boundary=2,
		// both (1 pixel line)=3,
		// others=0
		int[][] workMap = new int[pixel.height][pixel.width];

		for (int j = curY0; j < curY1; j++) {
			// left boundary
			boolean mostLeftFound = false;
			boolean otherContainerLeft = false;
			for (int i = curX0; i < curX1; i++) {
				if (containerMap[j][i] == _id) {
					if (!mostLeftFound) {
						workMap[j][i] = 1;
						mostLeftFound = true;
						otherContainerLeft = false;
					} else if (otherContainerLeft) {
						workMap[j][i] = 1;
						otherContainerLeft = false;
					}
				} else if (containerMap[j][i] > 0) {
					otherContainerLeft = true;
				}
			}
			// right boundary
			boolean mostRightFound = false;
			boolean otherContainerRight = false;
			for (int i = curX1 - 1; i >= curX0; i--) {
				if (containerMap[j][i] == _id) {
					if (!mostRightFound) {
						if (workMap[j][i] != 1) {
							workMap[j][i] = 2;
						} else {
							workMap[j][i] = 3;
						}
						mostRightFound = true;
						otherContainerRight = false;
					} else if (otherContainerRight) {
						if (workMap[j][i] != 1) {
							workMap[j][i] = 2;
						} else {
							workMap[j][i] = 3;
						}
						otherContainerRight = false;
					}
				} else if (containerMap[j][i] > 0) {
					otherContainerRight = true;
				}
			}

			// fill between left/right boundary
			boolean inTheContainer = false;
			for (int i = curX0; i < curX1; i++) {
				if (workMap[j][i] == 0 && inTheContainer) {
					// debug (TBD remove this if sentence)
					if (containerMap[j][i] != 0 && containerMap[j][i] != _id) {
						DebugUtil.outMsg(this, "i = " + i + ", j = " + j);
						DebugUtil.outMsg(this, "Dumping containerMap");
						for (int k = 0; k < pixel.width; k++) {
							System.err.print("" + containerMap[j][k]);
						}
						System.err.println("");
						DebugUtil.outMsg(this, "Dumping workMap");
						for (int k = 0; k < pixel.width; k++) {
							System.err.print("" + workMap[j][k]);
						}
						System.err.println("");
						throw new ImageException("filling error 0: id = " + _id);
					}
					containerMap[j][i] = _id;
				} else if (workMap[j][i] == 1) {
					// debug (TBD remove this if sentence)
					if (inTheContainer) {
						throw new ImageException("filling error 1: id = " + _id);
					}
					inTheContainer = true;
				} else if (workMap[j][i] == 2) {
					inTheContainer = false;
				}
			}
		}
	}

	/*
	 * Collate candidates from (1) and (3) (-> SS Character)
	 */
	private boolean collateCandidates(Container _cont, ConnectedComponent _cc)
			throws ImageException {
		int numCand = _cont.candidateCharacterVector.size();
		for (int k = numCand - 1; k >= 0; k--) {
			CandidateCharacter cChar = _cont.candidateCharacterVector
					.elementAt(k);
			if (_cc.equals(cChar.cc)) {
				// (confirmed) SS Character
				CharacterSS ssc = new CharacterSS(cChar);
				_cont.ssCharacterVector.addElement(ssc);
				_cont.candidateCharacterVector.removeElementAt(k);
				return (true);
			}
		}
		int numUCand = _cont.candidateUnderlinedCharacterVector.size();
		for (int k = numUCand - 1; k >= 0; k--) {
			CandidateUnderlinedCharacter cuChar = _cont.candidateUnderlinedCharacterVector
					.elementAt(k);
			if (_cc.equals(cuChar.cc)) {
				// (confirmed) Underlined SS Character
				Vector sscVec = removeUnderlineAndGenerateSS(cuChar);
				for (int l = 0; l < sscVec.size(); l++) {
					_cont.ssCharacterVector.addElement((CharacterSS) (sscVec
							.elementAt(l)));
				}
				_cont.candidateUnderlinedCharacterVector.removeElementAt(k);
				return (true);
			}
		}
		return (false);
	}

	/*
	 * Returns fg color of connected component (int) color: single fg color -1:
	 * multiple fg color/no fg color
	 */
	private int getForegroundColor(ConnectedComponent _cc) {
		int fg = -1;
		for (int j = 0; j < _cc.shape.height; j++) {
			for (int i = 0; i < _cc.shape.width; i++) {
				if (_cc.shape.data[j][i] != 0) {
					if (fg == -1) {
						fg = pixel.data[j + _cc.top][i + _cc.left];
					} else if (fg != pixel.data[j + _cc.top][i + _cc.left]) {
						return (-1);
					}
				}
			}
		}
		return (fg);
	}

	/*
	 * Returns bg color of connected component (int) color: single bg color -1:
	 * multiple bg color/no bg color
	 */
	private int getBackgroundColor(ConnectedComponent _cc) {
		int bg = -1;
		for (int j = 0; j < _cc.shape.height; j++) {
			for (int i = 0; i < _cc.shape.width; i++) {
				if (_cc.shape.data[j][i] == 0) {
					if (bg == -1) {
						bg = pixel.data[j + _cc.top][i + _cc.left];
					} else if (bg != pixel.data[j + _cc.top][i + _cc.left]) {
						return (-1);
					}
				}
			}
		}
		return (bg);
	}

	// Create SM Characters from candidateCharacter/candidateUnderlinedCharacter
	private Vector makeSMCharacterVector(Vector<CandidateCharacter> _cVec,
			Vector<CandidateUnderlinedCharacter> _uVec) throws ImageException {
		Vector<CharacterSM> tmpVec = new Vector<CharacterSM>();
		int numRemainingChar = _cVec.size();
		for (int k = 0; k < numRemainingChar; k++) {
			CandidateCharacter cChar = _cVec.elementAt(k);
			CharacterSM smc = new CharacterSM(cChar, pixel);
			tmpVec.addElement(smc);
		}

		int numRemainingUnderlinedChar = _uVec.size();
		for (int k = 0; k < numRemainingUnderlinedChar; k++) {
			CandidateUnderlinedCharacter cuChar = _uVec.elementAt(k);
			Vector smcVec = removeUnderlineAndGenerateSM(cuChar);
			for (int l = 0; l < smcVec.size(); l++) {
				tmpVec.addElement((CharacterSM) (smcVec.elementAt(l)));
			}
		}

		return (tmpVec);
	}

	// target SM Char is contained within MS Char in the Container?
	private CharacterMS includingMSCharacter(CharacterSM _smc, Container _cont) {
		for (int k = 0; k < _cont.msCharacterVector.size(); k++) {
			CharacterMS curMS = (CharacterMS) (_cont.msCharacterVector
					.elementAt(k));
			if (_smc.cc.isIncludedBy(curMS.cc)) {
				return (curMS);
			}
		}
		return (null);
	}

	public LowVisionProblemGroup[] checkCharacters(LowVisionType _lvType)
			throws ImageException, LowVisionProblemException {
		LowVisionProblemGroup[] charProblemGroupArray = null;
		LowVisionProblemGroup[] imgProblemGroupArray = null;
		LowVisionProblemGroup[] answerArray = null;

		if (DO_CHECK_CHARACTERS) {
			CharacterChecker charChecker = new CharacterChecker(this);
			charProblemGroupArray = charChecker.checkAllCharacters(_lvType);
		}

		if (DO_CHECK_IMAGES && useImagePositions) {

			// for memory (create InteriorImages for each time (degrade
			// performance))
			extractInteriorImages();

			imgProblemGroupArray = checkInteriorImages(_lvType);

			// for memory
			interiorImageArray = null;
		}

		if (charProblemGroupArray == null) {
			if (imgProblemGroupArray == null) {
				answerArray = new LowVisionProblemGroup[0];
			} else {
				answerArray = imgProblemGroupArray;
			}
		} else {
			if (imgProblemGroupArray == null) {
				answerArray = charProblemGroupArray;
			} else {
				int charLen = charProblemGroupArray.length;
				int imgLen = imgProblemGroupArray.length;
				int allLen = charLen + imgLen;
				LowVisionProblemGroup[] allProblemGroupArray = new LowVisionProblemGroup[allLen];
				for (int i = 0; i < charLen; i++) {
					allProblemGroupArray[i] = charProblemGroupArray[i];
				}
				for (int i = 0; i < imgLen; i++) {
					allProblemGroupArray[charLen + i] = imgProblemGroupArray[i];
				}
				answerArray = allProblemGroupArray;
			}
		}

		return (answerArray);
	}

	private LowVisionProblemGroup[] checkInteriorImages(LowVisionType _lvType)
			throws ImageException {
		if (!useImagePositions) {
			return (new LowVisionProblemGroup[0]);
		}

		Vector<LowVisionProblemGroup> problemVector = new Vector<LowVisionProblemGroup>();

		int numInteriorImages = 0;
		if (interiorImageArray != null && interiorImageArray.length > 0) {
			numInteriorImages = interiorImageArray.length;
		}
		for (int k = 0; k < numInteriorImages; k++) {
			InteriorImage curIm = interiorImageArray[k];

			LowVisionProblemGroup[] probArray = curIm.checkColors(_lvType);
			if (probArray != null) {
				int numProb = probArray.length;
				for (int l = 0; l < numProb; l++) {
					problemVector.addElement(probArray[l]);
				}
			}
		}

		int size = problemVector.size();
		if (size > 0) {
			LowVisionProblemGroup[] allProbArray = new LowVisionProblemGroup[size];
			for (int k = 0; k < size; k++) {
				allProbArray[k] = problemVector.elementAt(k);
			}
			problemVector = null;
			return (allProbArray);
		} else {
			problemVector = null;
			return (new LowVisionProblemGroup[0]);
		}
	}

	private LabeledImage removeUnderlineAndCCL(
			CandidateUnderlinedCharacter _cuChar) throws ImageException {
		BinaryImage origImage = _cuChar.cc.shape;
		BinaryImage lineImage = origImage.drawUnderline();

		BinaryImage removedImage = origImage.subtract(lineImage);
		LabeledImage li = new LabeledImage(removedImage,
				LabeledImage.METHOD_8_CONNECTIVITY);

		return (li);
	}

	private Vector removeUnderlineAndGenerateSS(
			CandidateUnderlinedCharacter _cuChar) throws ImageException {
		Vector<CharacterSS> ssVec = new Vector<CharacterSS>();
		int offsetX = _cuChar.cc.left;
		int offsetY = _cuChar.cc.top;
		short conn = _cuChar.cc.connectivity;
		LabeledImage li = removeUnderlineAndCCL(_cuChar);
		int numCC = li.numComponents;
		for (int k = 0; k < numCC; k++) {
			ConnectedComponent cc = li.components[k];
			cc.left += offsetX;
			cc.top += offsetY;
			cc.connectivity = conn;
			if (DecisionMaker.judgeComponentType(cc, this) == PageComponent.CANDIDATE_CHARACTER_TYPE) {
				CharacterSS ssc = new CharacterSS(this, cc, _cuChar.container,
						_cuChar.getForegroundColor());
				ssVec.addElement(ssc);
			}
		}
		return (ssVec);
	}

	private Vector removeUnderlineAndGenerateSM(
			CandidateUnderlinedCharacter _cuChar) throws ImageException {
		Vector<CharacterSM> smVec = new Vector<CharacterSM>();
		int offsetX = _cuChar.cc.left;
		int offsetY = _cuChar.cc.top;
		short conn = _cuChar.cc.connectivity;
		LabeledImage li = removeUnderlineAndCCL(_cuChar);
		int numCC = li.numComponents;
		for (int k = 0; k < numCC; k++) {
			ConnectedComponent cc = li.components[k];
			cc.left += offsetX;
			cc.top += offsetY;
			cc.connectivity = conn;
			if (DecisionMaker.judgeComponentType(cc, this) == PageComponent.CANDIDATE_CHARACTER_TYPE) {
				CharacterSM smc = new CharacterSM(this, cc, _cuChar.container,
						_cuChar.getForegroundColor(), pixel);
				smVec.addElement(smc);
			}
		}
		return (smVec);
	}
}
