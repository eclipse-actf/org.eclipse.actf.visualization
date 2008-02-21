/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Junji MAEDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.visualization.engines.lowvision.image;

import org.eclipse.actf.visualization.engines.lowvision.character.CharacterSS;
import org.eclipse.actf.visualization.engines.lowvision.color.ColorUtil;
import org.eclipse.actf.visualization.engines.lowvision.internal.util.DebugUtil;

public class PageImageDebugUtil {
	private PageImage pageImage;
	
	PageImageDebugUtil(PageImage pageImage){
		this.pageImage = pageImage;
	}

	public void paintOnePageComponent(PageComponent _com, int[][] _image,
			int _color) {
		int startX = _com.cc.left;
		int startY = _com.cc.top;
		int paintW = _com.cc.shape.width;
		int paintH = _com.cc.shape.height;

		for (int j = 0; j < paintH; j++) {
			for (int i = 0; i < paintW; i++) {
				if (_com.cc.shape.data[j][i] != 0) {
					_image[j + startY][i + startX] = _color;
				}
			}
		}
	}

	public int[][] paintContainers(int[][] _image, int _color) {
		if (pageImage.numContainers == 0) {
			DebugUtil.errMsg(this, "There are no containers.");
			return (null);
		}
		DebugUtil.debugMsg(null, "# of containers = " + pageImage.numContainers,
				"CONTAINER");
		for (int i = 0; i < pageImage.numContainers; i++) {
			Container curContainer = pageImage.containers[i];
			paintOnePageComponent(curContainer, _image, _color);
		}

		return (_image);
	}

	public Int2D showRepaintedContainerMap() {
		Int2D i2d = new Int2D(pageImage.pixel.width, pageImage.pixel.height);
		for (int j = 0; j < pageImage.pixel.height; j++) {
			for (int i = 0; i < pageImage.pixel.width; i++) {
				i2d.data[j][i] = ColorUtil
						.distinguishableColor(pageImage.containerMap[j][i]);
			}
		}
		return (i2d);
	}

	public Int2D showAllSSCharacters() {
		return (showAllSSCharacters(0x00ff0000));
	}

	public Int2D showAllSSCharacters(int _color) {
		Int2D i2d = new Int2D(pageImage.pixel.width, pageImage.pixel.height);
		for (int k = 0; k < pageImage.containerVector.size(); k++) {
			i2d = showSSCharactersInOneContainer(i2d, k, _color);
		}

		return (i2d);
	}

	public Int2D showSSCharactersInOneContainer(Int2D _i2d, int _contIndex,
			int _color) {
		Container curCont = (Container) (pageImage.containerVector.elementAt(_contIndex));
		for (int l = 0; l < curCont.ssCharacterVector.size(); l++) {
			CharacterSS ssc = (CharacterSS) (curCont.ssCharacterVector
					.elementAt(l));
			ConnectedComponent cc = ssc.cc;
			for (int j = 0; j < cc.shape.height; j++) {
				for (int i = 0; i < cc.shape.width; i++) {
					if (cc.shape.data[j][i] != 0) {
						_i2d.data[j + cc.top][i + cc.left] = _color;
					}
				}
			}
		}
		return (_i2d);
	}

	public Int2D showAllCharacters() {
		return (showAllCharacters(0x00ff0000, 0x0000ff00, 0x000000ff,
				0x00ffff00));
	}

	public Int2D showAllCharacters(int _ssColor, int _msColor, int _smColor1,
			int _smColor2) {
		Int2D i2d = new Int2D(pageImage.pixel.width, pageImage.pixel.height);
		DebugUtil.debugMsg(this, "numContainers = " + pageImage.numContainers,
				"CONTAINER");
		for (int k = 0; k < pageImage.numContainers; k++) {
			Container cont = pageImage.containers[k];
			i2d = showCharactersInOneContainer(i2d, cont, _ssColor, _msColor,
					_smColor1);
		}

		for (int k = 0; k < pageImage.numNonContainedCharacters; k++) {
			i2d = pageImage.nonContainedCharacters[k].cc.drawShape(i2d, _smColor2);
		}

		return (i2d);
	}

	public Int2D showCharactersInOneContainer(Int2D _i2d, Container _cont,
			int _ssColor, int _msColor, int _smColor) {
		for (int l = 0; l < _cont.numSSCharacters; l++) {
			_i2d = _cont.ssCharacters[l].cc.drawShape(_i2d, _ssColor);
		}
		for (int l = 0; l < _cont.numMSCharacters; l++) {
			_i2d = _cont.msCharacters[l].cc.drawShape(_i2d, _msColor);
		}
		for (int l = 0; l < _cont.numSMCharacters; l++) {
			_i2d = _cont.smCharacters[l].cc.drawShape(_i2d, _smColor);
		}
		return (_i2d);
	}

	public Int2D showAllThinedCharacters() throws ImageException {
		return (showAllThinedCharacters(0x00ff0000, 0x0000ff00, 0x000000ff,
				0x00ffff00));
	}

	public Int2D showAllThinedCharacters(int _ssColor, int _msColor,
			int _smColor1, int _smColor2) throws ImageException {
		Int2D i2d = new Int2D(pageImage.pixel.width, pageImage.pixel.height);
		DebugUtil.debugMsg(this, "numContainers = " + pageImage.numContainers,
				"CONTAINER");
		for (int k = 0; k < pageImage.numContainers; k++) {
			Container cont = pageImage.containers[k];
			i2d = showThinedCharactersInOneContainer(i2d, cont, _ssColor,
					_msColor, _smColor1);
		}

		for (int k = 0; k < pageImage.numNonContainedCharacters; k++) {
			i2d = pageImage.nonContainedCharacters[k].cc.thinning().drawShape(i2d,
					_smColor2);
		}

		return (i2d);
	}

	public Int2D showThinedCharactersInOneContainer(Int2D _i2d,
			Container _cont, int _ssColor, int _msColor, int _smColor)
			throws ImageException {
		for (int l = 0; l < _cont.numSSCharacters; l++) {
			_i2d = _cont.ssCharacters[l].cc.thinning()
					.drawShape(_i2d, _ssColor);
		}
		for (int l = 0; l < _cont.numMSCharacters; l++) {
			_i2d = _cont.msCharacters[l].cc.thinning()
					.drawShape(_i2d, _msColor);
		}
		for (int l = 0; l < _cont.numSMCharacters; l++) {
			_i2d = _cont.smCharacters[l].cc.thinning()
					.drawShape(_i2d, _smColor);
		}
		return (_i2d);
	}
}
