/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.visualization.engines.blind.html.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.actf.mediator.IACTFReport;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.actf.util.dom.DomPrintUtil;
import org.eclipse.actf.visualization.engines.blind.BlindVizEnginePlugin;
import org.eclipse.actf.visualization.engines.blind.eval.PageEvaluation;
import org.eclipse.actf.visualization.engines.blind.util.BlindVizResourceUtil;
import org.eclipse.actf.visualization.eval.EvaluationPreferencesUtil;
import org.eclipse.actf.visualization.eval.IEvaluationItem;
import org.eclipse.actf.visualization.eval.IEvaluationResult;
import org.eclipse.actf.visualization.eval.guideline.GuidelineData;
import org.eclipse.actf.visualization.eval.guideline.GuidelineHolder;
import org.eclipse.actf.visualization.eval.problem.HighlightTargetId;
import org.eclipse.actf.visualization.eval.problem.IProblemItem;
import org.eclipse.actf.visualization.eval.problem.ProblemConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SaveReportBlind {

	private static final String[] ICON_NAMES = { "Err.png", "Warn.png",
			"Info.png" };

	// moved from PartRightBlind
	// TODO renew

	// TODO include only selected guideline/metrics

	public static void saveReport(Document result, IACTFReport checkResult,
			String sFileName, String imageBriefDir, String maxTime,
			PageEvaluation pageEval,
			// String strUrl,
			boolean bAccessory) {
		if (null != checkResult && checkResult instanceof IEvaluationResult) {

			IEvaluationResult evalResult = (IEvaluationResult) checkResult;

			String saveDir = sFileName.substring(0, sFileName
					.lastIndexOf(File.separator) + 1);
			String imageDir = saveDir + imageBriefDir;
			File fDir = new File(imageDir);
			if ((!fDir.isDirectory() || !fDir.canWrite()) && !fDir.mkdirs()) {
				System.err.println("can't create image dir:" + imageDir);
			}

			String preName = sFileName.substring(sFileName
					.lastIndexOf(File.separator) + 1, sFileName
					.lastIndexOf("."));
			String scriptVariantName = preName + "_variant.js";
			String reportImgSaveName = preName + ".png";

			String variantFileS = "variant.js";

			if (bAccessory) {
				BlindVizResourceUtil.saveImages(imageDir);
				BlindVizResourceUtil.saveScripts(imageDir);

				File[] assFiles = evalResult.getAssociateFiles();

				for (File target : assFiles) {
					if (target.getName().startsWith("variant")
							&& target.getName().endsWith(".js")) {
						FileUtils.copyFile(target,
								imageDir + scriptVariantName, true);
						variantFileS = target.getName();
					} else {
						FileUtils.copyFile(target, imageDir + target.getName(),
								true);

					}
				}

				PrintWriter pw;
				try {
					pw = new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(imageDir + scriptVariantName,
									true), "UTF-8"));
					pw.write("var acc_imageDir = '" + imageBriefDir + "'; ");
					pw.flush();
					pw.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}

				FileUtils.copyFile(new File(BlindVizEnginePlugin
						.getTempDirectory(), "pagerating.png"), imageDir
						+ reportImgSaveName, true);

			}

			IProblemItem[] problemTableBlindDisplayItemArray = new IProblemItem[0];
			if (checkResult != null) {
				List<IProblemItem> tmpList = evalResult.getProblemList();
				problemTableBlindDisplayItemArray = new IProblemItem[tmpList
						.size()];
				try {
					tmpList.toArray(problemTableBlindDisplayItemArray);
				} catch (Exception e) {
				}

			}

			// img/highlight.js -> <imageBriefDir>highlight.js
			String strAtt;
			NodeList nl = result.getElementsByTagName("script");
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				strAtt = el.getAttribute("src");
				if (strAtt.indexOf("img/") == 0) {
					el.setAttribute("src", imageBriefDir + strAtt.substring(4));
					el.getParentNode().removeChild(el);
				} else if (strAtt.indexOf(variantFileS) >= 0) {
					el.setAttribute("src", imageBriefDir + scriptVariantName);
				}
			}

			// set variable acc_imageDir
			nl = result.getElementsByTagName("head");
			if (nl.getLength() > 0) {
				Element el = (Element) nl.item(0);
				// Element script = result.createElement("script");
				// el.appendChild(script);
				// script.appendChild(result.createTextNode("var acc_imageDir =
				// '"
				// + imageBriefDir + "'; "));

				Element newEle = result.createElement("script");
				StringBuffer buffer = new StringBuffer();
				buffer
						.append(FileUtils.LINE_SEP
								+ "if(navigator.appName.toLowerCase().indexOf(\"microsoft\")>=0){"
								+ FileUtils.LINE_SEP);
				buffer.append("jsFile=\"highlight.js\";" + FileUtils.LINE_SEP);
				buffer.append("}else{" + FileUtils.LINE_SEP);
				buffer.append("jsFile=\"highlight_moz.js\";"
						+ FileUtils.LINE_SEP);
				buffer.append("}" + FileUtils.LINE_SEP);
				buffer
						.append("document.write(\"<script src=\"+acc_imageDir+jsFile+\"></script>\");"
								+ FileUtils.LINE_SEP);
				newEle.appendChild(result.createComment(buffer.toString()));
				el.appendChild(newEle);
			}

			// img/<*.gif> -> <imageBriefDir><*.gif>
			nl = result.getElementsByTagName("img");
			for (int i = 0; i < nl.getLength(); i++) {
				Element img = (Element) nl.item(i);
				strAtt = img.getAttribute("src");
				if (strAtt.indexOf("img/") == 0)
					img
							.setAttribute("src", imageBriefDir
									+ strAtt.substring(4));
			}

			// img/<*.gif> -> <imageBriefDir><*.gif>
			nl = result.getElementsByTagName("input");
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				strAtt = el.getAttribute("src");
				if (strAtt.indexOf("img/") == 0)
					el.setAttribute("src", imageBriefDir + strAtt.substring(4));
			}

			// create problem table
			NodeList body = result.getElementsByTagName("body");
			if (body.getLength() > 0) {
				// Element lastBody = (Element) body.item(body.getLength() - 1);
				Element lastBody = (Element) body.item(0);

				// report image
				Element tmpElement = result.createElement("hr");
				lastBody.insertBefore(tmpElement, lastBody.getFirstChild());

				Element tmpDiv = result.createElement("div");
				tmpDiv.setAttribute("style",
						"width: 100%; background-color:white;");

				tmpElement = result.createElement("div");
				// tmpElement.setAttribute("style", "float: left; width: 45%;");

				// TODO
				int count = 0;
				boolean enabledMetrics[] = GuidelineHolder.getInstance()
						.getMatchedMetrics();
				for (int i = 0; i < enabledMetrics.length; i++) {
					if (enabledMetrics[i]) {
						count++;
					}
				}
				if (count > 2) {
					Element tmpImg = result.createElement("img");
					tmpImg.setAttribute("src", imageBriefDir
							+ reportImgSaveName);
					tmpImg.setAttribute("alt", "");
					tmpElement.appendChild(tmpImg);
				}
				tmpDiv.appendChild(tmpElement);

				tmpElement = result.createElement("div");
				// tmpElement.setAttribute("style", "float: right; width:
				// 50%;");
				VisualizeReportUtil.appendRatingTableAndTitle(pageEval,
						imageBriefDir, result, tmpElement);
				tmpDiv.appendChild(tmpElement);

				lastBody.insertBefore(tmpDiv, lastBody.getFirstChild());

				tmpElement = result.createElement("hr");
				lastBody.insertBefore(tmpElement, lastBody.getFirstChild());

				// logo image
				tmpElement = result.createElement("div");
				tmpElement.setAttribute("style", "background-color:white;");
				tmpElement.setAttribute("align", "right");
				Element tmpImg = result.createElement("img");
				tmpImg.setAttribute("src", imageBriefDir + "logo.gif");
				// tmpImg.setAttribute("style", "float:right;");
				tmpImg.setAttribute("alt", "");
				tmpElement.appendChild(tmpImg);
				lastBody.insertBefore(tmpElement, lastBody.getFirstChild());

				Element pElement = result.createElement("p");
				// pElement.appendChild(result.createTextNode("&nbsp"));
				lastBody.appendChild(pElement);
				Element tableElement = result.createElement("table");
				tableElement.setAttribute("border", "1");
				tableElement.setAttribute("style", "background-color:white;");
				lastBody.appendChild(tableElement);
				Element trElement = result.createElement("tr");
				tableElement.appendChild(trElement);
				Element tdElement = result.createElement("th");
				tdElement.appendChild(result
						.createTextNode(ProblemConst.TITLE_ICON));
				trElement.appendChild(tdElement);

				if (EvaluationPreferencesUtil.isOriginalDOM()) {
					tdElement = result.createElement("th");
					tdElement.appendChild(result
							.createTextNode(ProblemConst.TITLE_LINE));
					trElement.appendChild(tdElement);
				}

				GuidelineData[] guidelineDataArray = GuidelineHolder
						.getInstance().getGuidelineData();
				for (int i = 0; i < guidelineDataArray.length; i++) {
					GuidelineData data = guidelineDataArray[i];
					if (data.isMatched()) {
						tdElement = result.createElement("th");
						tdElement.appendChild(result.createTextNode(data
								.getGuidelineName()));
						trElement.appendChild(tdElement);
					}
				}

				tdElement = result.createElement("th");
				tdElement.appendChild(result
						.createTextNode(ProblemConst.TITLE_DESCRIPTION));
				trElement.appendChild(tdElement);

				// int iconId; //TODO
				IProblemItem current;
				for (int i = 0; i < problemTableBlindDisplayItemArray.length; i++) {

					current = problemTableBlindDisplayItemArray[i];

					// iconId = current.getIconId();

					// iError[iconId]++;

					trElement = result.createElement("tr");

					// for highlight(kentarou:031113)
					trElement.setAttribute("onClick",
							"clearTrHighlight();clearHighlight()");
					if (current.isCanHighlight()) {

						HighlightTargetId[] targets = current
								.getHighlightTargetIds();
						int length = targets.length;

						String strSet = "";
						if (length == 1) {
							strSet = "setHighlight(" + targets[0].getStartId()
									+ "," + targets[0].getEndId() + ");";
						} else if (length > 1) {
							StringBuffer strStart = new StringBuffer(2048);
							StringBuffer strEnd = new StringBuffer(2048);
							strStart.append(targets[0].getStartId());
							strEnd.append(targets[0].getEndId());
							for (int j = 1; j < length; j++) {
								strStart.append("," + targets[j].getStartId());
								strEnd.append("," + targets[j].getEndId());
							}
							strSet = "setHighlight2(new Array("
									+ strStart.toString() + "), new Array("
									+ strEnd.toString() + "));";
						}

						trElement.setAttribute("onclick",
								"highlightTr(this);clearHighlight();" + strSet);
						trElement.setAttribute("STYLE",
								"COLOR:blue;TEXT-DECORATION:underline");
						// (
					}

					tableElement.appendChild(trElement);
					tdElement = result.createElement("td");
					trElement.appendChild(tdElement);

					if (current.isCanHighlight()) {
						Element imgElement = result.createElement("img");

						// TODO
						String imgName = "star.gif";

						imgElement.setAttribute("src", imageBriefDir + imgName);
						imgElement.setAttribute("alt", "");// TODO
						tdElement.appendChild(imgElement);
					}

					String altS = current.getSeverityStr();
					String imgName = getIconName(current.getSeverity());

					if (imgName.length() > 0) {
						Element imgElement = result.createElement("img");
						imgElement.setAttribute("src", imageBriefDir + imgName);
						imgElement.setAttribute("alt", altS);
						tdElement.appendChild(imgElement);
					}

					if (EvaluationPreferencesUtil.isOriginalDOM()) {
						tdElement = result.createElement("td");
						String sTmp = current.getLineStrMulti();
						if (sTmp != null && !sTmp.equals("")) {
							tdElement.appendChild(result.createTextNode(sTmp));
						} else {
							tdElement.appendChild(result.createTextNode("-"));
						}
						trElement.appendChild(tdElement);
					}

					String[] guidelines = current.getTableDataGuideline();

					for (int j = 0; j < guidelines.length; j++) {
						if (guidelineDataArray[j].isMatched()) {
							tdElement = result.createElement("td");
							String sTmp = guidelines[j];
							if (sTmp != null && !sTmp.equals(""))
								tdElement.appendChild(result
										.createTextNode(sTmp));
							else
								tdElement.appendChild(result
										.createTextNode("-"));

							trElement.appendChild(tdElement);
						}
					}

					tdElement = result.createElement("td");
					String desc = current.getDescription();
					desc = desc.replaceAll("<", "&lt;");
					desc = desc.replaceAll(">", "&gt;");

					tdElement.appendChild(result.createTextNode(desc));

					trElement.appendChild(tdElement);

				}

				trElement = result.createElement("tr");
				body.item(body.getLength() - 1).appendChild(trElement);
				tdElement = result.createElement("td");
				tdElement.appendChild(result.createTextNode(maxTime));
				trElement.appendChild(tdElement);
			}

			// HtmlParserUtil.saveSGMLDocumentAsUTF8((IHTMLDocumentACTF) result,
			// BlindVizEnginePlugin.getTempDirectoryString()
			// + "saveResultTmp.html", sFileName);

			DomPrintUtil dpu = new DomPrintUtil(result);
			try {
				dpu.writeToFile(sFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private static String getIconName(int id) {
		switch (id) {
		case IEvaluationItem.SEV_ERROR:
			return ICON_NAMES[0];
		case IEvaluationItem.SEV_WARNING:
			return ICON_NAMES[1];
		case IEvaluationItem.SEV_INFO:
			return ICON_NAMES[2];
		default:
			return "";
		}
	}

}
