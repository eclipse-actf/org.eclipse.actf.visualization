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

import org.eclipse.actf.visualization.engines.lowvision.internal.Messages;

public class DontRelyOnColorRecommendation extends LowVisionRecommendation {

	// TBD more detailed recommendation (border, hatchung, line type, etc.)

	public DontRelyOnColorRecommendation(LowVisionProblem _prob)
			throws LowVisionProblemException {
		super(
				DONT_RELY_ON_COLOR_RECOMMENDATION,
				_prob,
				Messages
						.getString("DontRelyOnColorRecommendation.Don__t_rely_only_on_color._1"));
	}
}
