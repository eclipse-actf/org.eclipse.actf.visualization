/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.visualization.internal.engines.blind.html.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.actf.visualization.engines.blind.ParamBlind;
import org.eclipse.actf.visualization.engines.blind.TextChecker;
import org.eclipse.actf.visualization.engines.blind.html.eval.BlindProblem;
import org.eclipse.actf.visualization.engines.voicebrowser.Packet;
import org.eclipse.actf.visualization.engines.voicebrowser.PacketCollection;
import org.eclipse.actf.visualization.eval.html.HtmlTagUtil;
import org.eclipse.actf.visualization.eval.problem.IProblemItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class NodeInfoCreator {

	private static final String HEADING_TAGS = "h1|h2|h3|h4|h5|h6";

	private static final String LIST_TAGS = "ul|ol|dl";

	private static final Set<String> BLOCK_TAG_SET = HtmlTagUtil.getBlockElementSet();

	private VisualizeMapDataImpl mapData;

	private TextChecker textChecker;

	private List<IProblemItem> problems;

	private Set<String> invisibleIdSet;

	private TextCounter textCounter;

	/**
	 * 
	 */
	public NodeInfoCreator(VisualizeMapDataImpl mapData, TextChecker textChecker,
			List<IProblemItem> problems, Set<String> invisibleIdSet,
			ParamBlind paramBlind) {
		this.mapData = mapData;
		this.textChecker = textChecker;
		this.problems = problems;
		this.invisibleIdSet = invisibleIdSet;
		textCounter = new TextCounter(paramBlind.iLanguage);
	}

	private String removePeriod(String targetS, Node targetNode) {
		if (targetNode != null) {
			String nodeS = targetNode.getNodeName();
			if (nodeS.equals("img") || nodeS.equals("applet")) {// AREA?
				if (targetS.endsWith(".]")) {
					targetS = targetS.substring(0, targetS.lastIndexOf(".]"))
							+ "]";
				}
			}
		}
		return targetS;
	}

	private boolean isIdRequiredInput(Element el) {
		String tagName = el.getNodeName();
		if (tagName.equals("select")) {
			return true;
		} else if (tagName.equals("textarea")) {
			return true;
		} else if (tagName.equals("input")) {
			String type = el.getAttribute("type");
			if ((type.length() == 0) | type.equals("text")
					| type.equals("textarea") | type.equals("radio")
					| type.equals("checkbox")) {
				return true;
			}
		}
		return false;
	}

	public void prepareNodeInfo(PacketCollection pc) {
		// node - nodeInfo
		int size = 0;

		if (pc == null) {
			return;
		} else {
			size = pc.size();
		}

		int totalWords = 0;
		int lineNumber = 0;
		int previousLineNumber = 0;

		String prevText = null;
		Packet prevPacket = null;

		for (int it = 0; it < size; it++) {
			Packet p = (Packet) pc.get(it);

			Node node = p.getNode();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = node.getNodeName();
				if (nodeName.equals("option")) {
					Element el = (Element) node;
					Node attrNode = el.getAttributeNode("selected");
					if (attrNode == null)
						continue; // !!!!
				} else if (nodeName.equals("area")) {
					continue; // !!!!
				} else if (nodeName.equals("map")) {
					continue; // !!!!
				}
			}

			String curText = p.getText();
			if ((curText != null) && (curText.length() > 0)) {

				// TODO consider ALT text ('['+alt+']')
				// check inappropritate text
				if (textChecker.isSeparatedJapaneseChars(curText)) {
					BlindProblem prob = new BlindProblem(
							BlindProblem.WRONG_TEXT, curText);
					prob.setNode(p.getNode());
					prob.setTargetNode(mapData.getOrigNode(p.getNode()));
					problems.add(prob);
				}

				// check redundant texts
				if ((prevPacket != null)
						&& (p.getContext().isInsideAnchor() == prevPacket
								.getContext().isInsideAnchor())) {
					Node curNode = p.getNode();
					Node prevNode = prevPacket.getNode();

					if (!curNode.getNodeName().equals("input")) {
						// TODO temporal solution to avoid label problem of
						// JWAT.
						// JWAT count same text twice, if an input has a label.

						if (textChecker.isRedundantText(prevText, curText)) {
							if (!HtmlTagUtil.hasAncestor(curNode, "noscript")
									&& !HtmlTagUtil.hasAncestor(prevNode,
											"noscript")) {
								// remove "." from error (comment from JIM)
								prevText = removePeriod(prevText, prevNode);
								curText = removePeriod(curText, curNode);

								BlindProblem prob = new BlindProblem(
										BlindProblem.REDUNDANT_ALT, "\""
												+ prevText + "\" & \""
												+ curText + "\"");
								prob.setNode(prevNode);
								prob.addNode(curNode);

								// TODO insideAnchor -> check same target?
								if (prevNode.getNodeName().equals("img")) {
									prob.setTargetNode(mapData
											.getOrigNode(prevNode));
									prob.setTargetStringForHPB(prevText);
									problems.add(prob);
								} else if (curNode.getNodeName().equals("img")) {
									prob.setTargetNode(mapData
											.getOrigNode(curNode));
									prob.setTargetStringForHPB(curText);
									problems.add(prob);
								} else {
									// TODO ALERT_REDUNDANT_TEXT
								}
							}
						}
					}
				}

				prevText = curText;
				prevPacket = p;
			}

			VisualizationNodeInfo already = mapData.getNodeInfo(p.getNode());

			if (already != null) { /* end tag? */
				// TODO ??
			} else {
				VisualizationNodeInfo info = new VisualizationNodeInfo();
				info.setPacket(p);
				// info.setNode(p.getNode());
				int words = textCounter.getWordCount(p.getText());

				// take into acount structurization
				boolean isVisible = true;

				Node curNode = p.getNode();
				while (curNode != null) {
					String nodeName = curNode.getNodeName();
					if (curNode.getNodeType() == Node.ELEMENT_NODE) {
						Element tmpE = (Element) curNode;
						if (tmpE.hasAttribute("accesskey")) {
							info.setAccesskey(true);
						}
						if (invisibleIdSet.contains(tmpE.getAttribute("id"))) {
							info.setInvisible(true);
							isVisible = false;
							// System.out.println(tmpE.getAttribute("id"));
						}
					}

					if (nodeName.matches(HEADING_TAGS)) {
						info.setHeading(true);
						info.appendComment("Heading: " + nodeName + ".");
					} else if (nodeName.equals("th")) {
						info.setTableHeader(true);
						info.appendComment("Table header.");
					} else if (nodeName.equals("label")) {
						info.setLabel(true);
						info.appendComment("Label for '"
								+ ((Element) curNode).getAttribute("for")
								+ "'. ");
					}

					if (nodeName.equals("body")) {
						break;
					}
					curNode = curNode.getParentNode();
				}

				if (isVisible) {
					info.setWords(words);
					info.setTotalWords(totalWords);
					info.setOrgTotalWords(totalWords);
					totalWords += words;
					if (words > 0) {
						lineNumber++;
					}
					info.setTotalLines(previousLineNumber);
					info.setOrgTotalLines(previousLineNumber);
					info.setLines(lineNumber - previousLineNumber);

					previousLineNumber = lineNumber;
				} else {
					// invisible
					info.setWords(0);
					info.setTotalWords(totalWords);
					info.setOrgTotalWords(totalWords);
					info.setTotalLines(previousLineNumber);
					info.setOrgTotalLines(previousLineNumber);
					info.setLines(0);
				}
				info.setPacketId(it);

				mapData.addNodeInfoMapping(p.getNode(), info);
			}
		}
	}

	public void createAdditionalNodeInfo(Document doc) {
		// create elementList
		// set node info ID
		// List nodeList = new ArrayList(1024);
		int origTotalWords = 0;
		int origTotalLines = 0;

		Map<String, String> mapTextMap = VisualizeMapUtil.createMapTextMap(doc);

		NodeList bodyNl = doc.getElementsByTagName("body");
		if (bodyNl.getLength() > 0) {
			if (bodyNl.getLength() > 1) {
				System.out.println("multiple body");
			}
			Element bodyEl = (Element) bodyNl.item(0);

			// TODO traversel

			if (bodyEl.hasChildNodes()) {

				Stack<Node> stack = new Stack<Node>();
				stack.push(bodyEl);
				Node curNode = bodyEl.getFirstChild();
				VisualizationNodeInfo lastInfo = null;
				int counter = 0;
				// int tableCount = 0;
				int listCount = 0;

				while ((curNode != null) && (stack.size() > 0)) {
					String curNodeName = curNode.getNodeName();
					VisualizationNodeInfo curInfo = mapData
							.getNodeInfo(curNode);

					if (curNode.getNodeType() == Node.TEXT_NODE) {
						// add text nodes involved in the PacketCollection.
						if (curInfo != null) {
							curInfo.setId(counter);
							mapData.addNodeInfoIntoList(curInfo);
							counter++;
							lastInfo = curInfo;
						}
					} else if (curNode.getNodeType() == Node.ELEMENT_NODE) {
						if (curInfo != null) {
							curInfo.setId(counter);
							mapData.addNodeInfoIntoList(curInfo);
							counter++;
							lastInfo = curInfo;
						} else {
							// create NodeInfo for nodes not in packet.
							// ("b", "img" without alt, etc.)
							try {
								curInfo = new VisualizationNodeInfo(lastInfo);
								curInfo.setWords(0);
								curInfo.setLines(0);
								curInfo.setId(counter);
								curInfo.setNode(curNode);
								counter++;
								mapData.addNodeInfoIntoList(curInfo);

								// TODO check invisible

								mapData.addNodeInfoMapping(curNode, curInfo);

							} catch (NullPointerException npe) {
								// no previous info error
								npe.printStackTrace();
							}
						}

						if (curNodeName.equals("img")) {
							// TODO handle invisible map
							String map = ((Element) curNode)
									.getAttribute("usemap");
							if ((map != null) && (map.length() > 0)) {
								int words = curInfo.getWords();
								String curText = (String) mapTextMap.get(map
										.toLowerCase().substring(1));
								int add = textCounter.getWordCount(curText);
								curInfo.setWords(words + add);
								curInfo.setLines(curInfo.getLines() + 1);
							}
						}
					}

					if (curInfo != null) {
						curInfo.setTotalWords(origTotalWords);
						curInfo.setOrgTotalWords(origTotalWords);
						curInfo.setTotalLines(origTotalLines);
						curInfo.setOrgTotalLines(origTotalLines);
						origTotalWords = origTotalWords + curInfo.getWords();
						origTotalLines = origTotalLines + curInfo.getLines();

						if (listCount > 0) {
							curInfo.setSequence(true);
						}
						if (BLOCK_TAG_SET.contains(curNodeName)) {
							curInfo.setBlockElement(true);
						}

						if (curNode.getNodeType() == Node.ELEMENT_NODE) {
							if (isIdRequiredInput((Element) curNode)) {
								curInfo.setIdRequiredInput(true);
								curInfo.appendComment("Input with id, '"
										+ ((Element) curNode)
												.getAttribute("id") + "'. ");
							}
						}

						mapData.addNodeIdMapping(curInfo.getNode(),
								new Integer(curInfo.getId()));

					}

					boolean isListTag = curNodeName.matches(LIST_TAGS);
					if (curNode.hasChildNodes()) {
						// if (curNodeName.equals("table")) {
						// tableCount++;
						// }
						if (isListTag) {
							listCount++;
						}

						stack.push(curNode);
						curNode = curNode.getFirstChild();
					} else if (curNode.getNextSibling() != null) {
						// if (curNodeName.equals("table")) {
						// tableCount--;
						// }
						if (isListTag) {
							listCount--;
						}
						curNode = curNode.getNextSibling();
					} else {
						// if (curNodeName.equals("table")) {
						// tableCount--;
						// }
						if (isListTag) {
							listCount--;
						}

						curNode = null;
						while ((curNode == null) && (stack.size() > 0)) {
							curNode = (Node) stack.pop();
							curNodeName = curNode.getNodeName();
							// if (curNodeName.equals("table")) {
							// tableCount--;
							// }
							if (isListTag) {
								listCount--;
							}
							curNode = curNode.getNextSibling();
						}
					}
				}
				// System.out.println(tableCount);
			}
		}
	}
}