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

public class EnlargeTextRecommendation extends LowVisionRecommendation{
	public EnlargeTextRecommendation( LowVisionProblem _prob ) throws LowVisionProblemException{
		super( ENLARGE_TEXT_RECOMMENDATION, _prob, Messages.getString("EnlargeTextRecommendation.Enlarge_the_text._1") );
	}
}
