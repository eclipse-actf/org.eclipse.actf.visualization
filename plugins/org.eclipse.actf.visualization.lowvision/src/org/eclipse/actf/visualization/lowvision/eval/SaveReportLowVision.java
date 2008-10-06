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

package org.eclipse.actf.visualization.lowvision.eval;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.eclipse.actf.ui.util.DialogSave;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.actf.visualization.eval.guideline.GuidelineHolder;
import org.eclipse.actf.visualization.eval.guideline.IGuidelineData;
import org.eclipse.actf.visualization.eval.problem.IProblemConst;
import org.eclipse.actf.visualization.eval.problem.IProblemItem;
import org.eclipse.actf.visualization.eval.problem.IProblemItemImage;
import org.eclipse.actf.visualization.lowvision.ui.internal.Messages;
import org.eclipse.actf.visualization.lowvision.util.LowVisionVizResourceUtil;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class SaveReportLowVision {

	private static final String LVIMG_FILE = "lvimg.bmp";

	private static final String RESULT_BMP_FILE = "result.bmp";

	private static final String[] ERROR_IMAGE_ALT = {
			Messages.getString("SaveReportLowVision.Iro21_Error_Alt"),
			Messages.getString("SaveReportLowVision.Boke21_Error_Alt") };

	private Shell shell;

	private String saveFileName;

	private List<IProblemItem> problemList;

	private String currentUrlS;

	private boolean bSave;

	private String saveImgName;

	// private String probStaticsName;
	private String strParamNo;

	private int iColorNum;

	private int iFontNum;

	private int processNo;

	private Document document;

	private File vizImageFile;

	private File reportImageFile;

	private GuidelineHolder guidelineHolder = GuidelineHolder
			.getInstance();

	public SaveReportLowVision(Shell _shell) {
		shell = _shell;
		saveFileName = "";
	}

	// TODO save rating

	public void doSave(String _currentUrlS, List<IProblemItem> targetList,
			File vizImageFile, File reportImageFile) {
		currentUrlS = _currentUrlS;

		saveFileName = DialogSave.open(shell, DialogSave.HTML, currentUrlS,
				"_lowVision.htm");
		saveImgName = LVIMG_FILE;
		// probStaticsName = "";
		problemList = targetList;

		if (saveFileName == null)
			return;
		document = null;
		bSave = true;

		this.vizImageFile = vizImageFile;
		this.reportImageFile = reportImageFile;

		SaveBmpFileThread saveThread = new SaveBmpFileThread();
		saveThread.start();
	}

	public void doSave(int _processNo, int[] paramNo, String paramStr,
			String folder, Document probStatisticsDoc, String _currentUrlS,
			List<IProblemItem> targetList, File vizImageFile,
			File reportImageFile) {
		currentUrlS = _currentUrlS;
		processNo = _processNo;
		this.vizImageFile = vizImageFile;
		this.reportImageFile = reportImageFile;

		// saveFileName = DialogSave.open(shell,DialogSave.HTML, currentUrlS,
		// "_lowVision.htm");
		saveFileName = currentUrlS;
		if (saveFileName != null) {

			int iPos = saveFileName.indexOf("//");
			if (iPos == -1)
				iPos = 0;
			saveFileName = saveFileName.substring(iPos + 2);
			saveFileName = saveFileName.replaceAll("\\p{Punct}", "_");

			if (saveFileName.indexOf(".") > 0) {
				saveFileName = saveFileName.substring(0, saveFileName
						.lastIndexOf("."));
			}

			if (saveFileName.length() > 100) {
				saveFileName = saveFileName.substring(0, 100);
			}
			// saveFileName += "_"+String.valueOf(processNo)+"_lowVision.htm";
		}
		// probStaticsName = folder + probName;
		saveImgName = saveFileName + "_" + String.valueOf(processNo) + "_"
				+ String.valueOf(paramNo[0]) + String.valueOf(paramNo[1])
				+ String.valueOf(paramNo[2]) + "_lvimg.png";
		saveFileName += "_" + String.valueOf(processNo) + "_"
				+ String.valueOf(paramNo[0]) + String.valueOf(paramNo[1])
				+ String.valueOf(paramNo[2]) + "_lowVision.htm";
		folder += "lowvision" + File.separator;
		File testDir = new File(folder);
		if (!testDir.isDirectory())
			testDir.mkdirs();
		saveFileName = folder + saveFileName;
		strParamNo = paramStr;

		problemList = targetList;

		if (saveFileName == null)
			return;
		document = probStatisticsDoc;
		bSave = true;
		SaveBmpFileThread saveThread = new SaveBmpFileThread();
		saveThread.start();
	}

	private class SaveBmpFileThread extends Thread {

		private String getArrayString(int[][] data, int item) {
			StringBuffer tmpSB = new StringBuffer();
			try {
				if (data.length > 0) {
					for (int i = 0; i < data.length; i++) {
						tmpSB.append(data[i][item] + ",");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (tmpSB.toString());
		}

		private void prepareJavaScript(Writer writer,
				IProblemItemImage[] problemArray, String imgDir) {

			int size = problemArray.length;
			int[][] data = new int[size][4]; // left,top,width,height
			for (int i = 0; i < size; i++) {
				data[i][0] = problemArray[i].getX();
				data[i][1] = problemArray[i].getY();
				data[i][2] = problemArray[i].getWidth();
				data[i][3] = problemArray[i].getHeight();
			}
			try {
				writer.write("<SCRIPT Language=\"JavaScript\">"
						+ FileUtils.LINE_SEP);
				writer.write("var divLeft = new Array("
						+ getArrayString(data, 0) + "0);" + FileUtils.LINE_SEP);
				writer.write("var divTop = new Array("
						+ getArrayString(data, 1) + "0);" + FileUtils.LINE_SEP);
				writer.write("var divWidth = new Array("
						+ getArrayString(data, 2) + "0);" + FileUtils.LINE_SEP);
				writer.write("var divHeight = new Array("
						+ getArrayString(data, 3) + "0);" + FileUtils.LINE_SEP);
				writer.write("var divArray = new Array(" + size + ");"
						+ FileUtils.LINE_SEP);
				writer.write("var sLeft = new Array(" + size + ");"
						+ FileUtils.LINE_SEP);
				writer.write("var sTop = new Array(" + size + ");"
						+ FileUtils.LINE_SEP);

				writer.write("</SCRIPT>" + FileUtils.LINE_SEP);
				// writer.write(
				// "<SCRIPT Language=\"JavaScript\"
				// src=\"lvHighlight.js\"></SCRIPT>"
				// + ADesignerConst.LINE_SEP);
				writer.write("<SCRIPT><!--" + FileUtils.LINE_SEP);
				writer
						.write("if(navigator.appName.toLowerCase().indexOf(\"microsoft\")>=0){"
								+ FileUtils.LINE_SEP);
				writer.write("jsFile=\"" + imgDir + "lvHighlight.js\";"
						+ FileUtils.LINE_SEP);
				writer.write("}else{" + FileUtils.LINE_SEP);
				writer.write("jsFile=\"" + imgDir + "lvHighlight_moz.js\";"
						+ FileUtils.LINE_SEP);
				writer.write("}" + FileUtils.LINE_SEP);
				writer
						.write("document.write(\"<script src=\"+jsFile+\"></script>\");"
								+ FileUtils.LINE_SEP);
				writer.write("-->" + FileUtils.LINE_SEP + "</SCRIPT>"
						+ FileUtils.LINE_SEP);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void run() {
			try {

				String saveDir = saveFileName.substring(0, saveFileName
						.lastIndexOf("."));
				;
				File tmpFile = new File(saveDir);
				tmpFile.mkdirs();

				String imgDir = saveDir;
				imgDir = getFileName(imgDir) + "/";

				saveDir += File.separator;
				String saveBmpName = saveDir + RESULT_BMP_FILE;

				FileUtils.copyFile(vizImageFile.getAbsolutePath(), saveBmpName,
						true);

				PrintWriter fileOutput = new PrintWriter(new FileWriter(
						saveFileName));
				// FileWriter fileOutput = new FileWriter(saveFileName);
				// problemLVItemArray =
				// phlv.getProblemTableItemArrayLV();

				fileOutput.write("<html><head><title>Report (LowVision) of "
						+ currentUrlS + "</title>");

				IProblemItemImage[] problemLVArray = new IProblemItemImage[problemList
						.size()];
				problemList.toArray(problemLVArray);

				prepareJavaScript(fileOutput, problemLVArray, imgDir);

				fileOutput.write("</head>");
				if (problemLVArray.length > 0) {
					fileOutput.write("<body onload=\"onloadFunc()\">"
							+ FileUtils.LINE_SEP);
				} else {
					fileOutput.write("<body>" + FileUtils.LINE_SEP);
				}

				// TODO logo
				// fileOutput.write("<DIV align=\"right\"><img src=\"" + imgDir
				// + ImageUtils.LOGO + "\"></DIV><hr>"
				// + LINE_SEP);

				if (reportImageFile != null) {
					fileOutput.write("<p><FONT>Problem Map</FONT><BR>");
					fileOutput
							.write("<img src=\""
									// + LVIMG_FILE //Modified on 2004/02/05
									+ imgDir
									+ saveImgName
									+ "\" height=\"200\" style=\"{border:2 solid black}\">");
					fileOutput.write("</p><hr>" + FileUtils.LINE_SEP);
				}

				fileOutput.write("<DIV id=\"mother\">");
				fileOutput.write("<img id=\"simImg\" src=\"" + imgDir
						+ RESULT_BMP_FILE + "\"></DIV>" + FileUtils.LINE_SEP);
				fileOutput.write("<hr>" + FileUtils.LINE_SEP);

				iColorNum = 0;
				iFontNum = 0;

				if (problemLVArray.length > 0) {

					fileOutput.write("<table border=\"1\">\r\n");

					fileOutput.write("<tr>");
					fileOutput
							.write("<th>" + IProblemConst.TITLE_ICON + "</th>");

					IGuidelineData[] guidelineDataArray = guidelineHolder
							.getGuidelineData();
					for (int i = 0; i < guidelineDataArray.length; i++) {
						fileOutput.write("<th>"
								+ guidelineDataArray[i].getGuidelineName()
								+ "</th>");
					}

					fileOutput.write("<th>" + IProblemConst.TITLE_DESCRIPTION
							+ "</th>");

					fileOutput.write("<th>" + IProblemConst.TITLE_SEVERITY
							+ "</th>");
					fileOutput.write("<th>" + IProblemConst.TITLE_FORECOLOR
							+ "</th>");

					fileOutput.write("<th>" + IProblemConst.TITLE_BACKCOLOR
							+ "</th>");

					fileOutput.write("<th>" + IProblemConst.TITLE_X + "</th>");

					fileOutput.write("<th>" + IProblemConst.TITLE_Y + "</th>");

					fileOutput
							.write("<th>" + IProblemConst.TITLE_AREA + "</th>");

					fileOutput.write("</tr>" + FileUtils.LINE_SEP);

					LowVisionVizResourceUtil.saveErrorIcons(saveDir);
					LowVisionVizResourceUtil.saveImages(saveDir);
					LowVisionVizResourceUtil.saveScripts(saveDir);

					for (int i = 0; i < problemLVArray.length; i++) {
						fileOutput.write("<tr id=\"" + i
								+ "\" onclick=\"TROnclickFunc(this)\">");

						int iconId = problemLVArray[i].getIconId();
						int currentIcon = 0;
						String strIconName = "";// TODO
						switch (iconId) {
						case IProblemItemImage.ICON_IRO:
							iColorNum++;
							currentIcon = 0;
							if (problemLVArray[i].isCanHighlight()) {
								strIconName = "HiIro21.gif";
							} else {
								strIconName = "ErrIro21.gif";
							}
							break;
						case IProblemItemImage.ICON_BOKE:
							iFontNum++;
							currentIcon = 1;
							if (problemLVArray[i].isCanHighlight()) {
								strIconName = "HiBoke21.gif";
							} else {
								strIconName = "ErrBoke21.gif";
							}
							break;
						}
						;

						fileOutput.write("<td><img src=" + imgDir + strIconName
								+ " alt=\"" + ERROR_IMAGE_ALT[currentIcon]
								+ "\"></td>");

						String[] guidelineItems = problemLVArray[i]
								.getEvaluationItem().getTableDataGuideline();

						for (int j = 0; j < guidelineItems.length; j++) {
							fileOutput.write("<td>" + guidelineItems[j]
									+ "&nbsp;</td>");
						}

						fileOutput
								.write("<td  STYLE=\"COLOR:blue;TEXT-DECORATION:underline\">"
										+ problemLVArray[i].getDescription()
										+ "&nbsp;</td>");

						fileOutput.write("<td>"
								+ String.valueOf(problemLVArray[i]
										.getSeverityLV()) + "&nbsp;</td>");

						fileOutput.write("<td>"
								+ problemLVArray[i].getForegroundS()
								+ "&nbsp;</td>");

						fileOutput.write("<td>"
								+ problemLVArray[i].getBackgroundS()
								+ "&nbsp;</td>");

						fileOutput.write("<td>"
								+ String.valueOf(problemLVArray[i].getX())
								+ "&nbsp;</td>");

						fileOutput.write("<td>"
								+ String.valueOf(problemLVArray[i].getY())
								+ "&nbsp;</td>");

						fileOutput.write("<td>"
								+ String.valueOf(problemLVArray[i].getArea())
								+ "&nbsp;</td>");

						fileOutput.write("</tr>" + FileUtils.LINE_SEP);
					}

					fileOutput.write("</table>" + FileUtils.LINE_SEP);
				}

				if (reportImageFile != null) {
					FileUtils.copyFile(reportImageFile.getAbsolutePath(),
							saveDir + saveImgName, true);
				}

				fileOutput.write("</body>" + FileUtils.LINE_SEP);
				fileOutput.write("</html>");

				fileOutput.flush();
				fileOutput.close();

				// problem statics
				if (document != null) {
					int iPos = saveFileName.lastIndexOf("lowvision"
							+ File.separator);
					// String relFile = saveFileName.substring(iPos);
					String relFile = "lowvision/"
							+ saveFileName.substring(iPos + 10);

					// HtmlParserUtil hpu = new HtmlParserUtil();
					// hpu.parse(new FileInputStream(probStaticsName +
					// ".html"));
					// SHDocument document = hpu.getSHDocument();

					NodeList tdList = document.getElementsByTagName("td");
					Element urlEle = (Element) tdList.item(0);
					Node oldNode = urlEle.getChildNodes().item(0);
					urlEle.replaceChild(document.createTextNode("URL("
							+ String.valueOf(processNo) + ")"), oldNode);

					NodeList tableList = document.getElementsByTagName("tbody");
					Element tableEle = (Element) tableList.item(0);
					Element trElement = document.createElement("tr");
					tableEle.appendChild(trElement);
					Element tdElement = document.createElement("td");
					tdElement.setAttribute("width", "600");
					trElement.appendChild(tdElement);
					Element aElement = document.createElement("a");
					aElement.setAttribute("href", relFile);
					aElement.appendChild(document.createTextNode(currentUrlS));
					tdElement.appendChild(aElement);
					tdElement = document.createElement("td");
					tdElement.appendChild(document.createTextNode(strParamNo));
					trElement.appendChild(tdElement);
					tdElement = document.createElement("td");
					tdElement.appendChild(document.createTextNode(String
							.valueOf(iColorNum)));
					trElement.appendChild(tdElement);
					tdElement = document.createElement("td");
					tdElement.appendChild(document.createTextNode(String
							.valueOf(iFontNum)));
					trElement.appendChild(tdElement);
					// tdElement = document.createElement("td");
					// tdElement.appendChild(
					// document.createTextNode(String.valueOf(iBobbyNum)));
					// trElement.appendChild(tdElement);
					// PrintWriter pw =
					// new PrintWriter(
					// new FileWriter(probStaticsName + ".html"));
					// document.printAsSGML(pw, true);
					// pw.flush();
					// pw.close();
				}

				bSave = false;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public boolean isInSaveReport() {
		return bSave;
	}

	/**
	 * @return
	 */
	public int getNumColorProblem() {
		return iColorNum;
	}

	/**
	 * @return
	 */
	public int getNumFontProblem() {
		return iFontNum;
	}

	private String getFileName(String filePath) {
		int index = filePath.lastIndexOf("/");
		int index2 = filePath.lastIndexOf("\\");
		if (index < index2) {
			index = index2;
		}
		if (index > -1) {
			return filePath.substring(index + 1);
		}
		return filePath;
	}

}
