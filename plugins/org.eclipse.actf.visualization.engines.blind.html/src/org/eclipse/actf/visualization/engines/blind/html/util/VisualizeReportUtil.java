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
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.actf.visualization.engines.blind.eval.PageEvaluation;
import org.eclipse.actf.visualization.eval.guideline.GuidelineHolder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



public class VisualizeReportUtil {

    // move from VisualizeEngine

    public static void createReport(File targetFile, PageEvaluation pageEval) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation domImpl = builder.getDOMImplementation();
            Document document = domImpl.createDocument("", "html", null);

            Node rootN = document.getDocumentElement();

            Element head = document.createElement("head");
            Element meta = document.createElement("meta");
            meta.setAttribute("http-equiv", "Content-type");
            meta.setAttribute("content", "text/html; charset=UTF-8");
            head.appendChild(meta);
            Element title = document.createElement("title");
            title.appendChild(document.createTextNode("Overall rating"));
            head.appendChild(title);
            rootN.appendChild(head);

            Element body = document.createElement("body");
            rootN.appendChild(body);

            Element div = document.createElement("div");
            Element starImg = document.createElement("img");
            starImg.setAttribute("src", "img/" + pageEval.getRatingIcon());
            starImg.setAttribute("alt", "");
            div.appendChild(starImg);

            //for not svg util
            Element b = document.createElement("b");
            b.appendChild(document.createTextNode(//"Page Rating: " +
                    " " + pageEval.getOverallRating()));
            div.appendChild(b);
            //p.appendChild(document.createElement("br"));
            body.appendChild(div);

            //TODO temp
            int count = 0;
            boolean enabledMetrics[] = GuidelineHolder.getInstance().getMatchedMetrics();
            for (int i = 0; i < enabledMetrics.length; i++) {
                if (enabledMetrics[i]) {
                    count++;
                }
            }
            if (count > 2) {
                Element img = document.createElement("img");
                img.setAttribute("src", "./pagerating.png");
                img.setAttribute("alt", "");
                body.appendChild(img);
            }

            body.appendChild(document.createElement("hr"));

            appendRatingTable(pageEval.getAllResult(), document, body);

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream os = new FileOutputStream(targetFile);
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            StreamResult result = new StreamResult(osw);
            transformer.transform(source, result);
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void appendRatingTableAndTitle(PageEvaluation pageEval, String imageBriefDir, Document document,
            Node target) {

        String[] ratingStr = pageEval.getAllResult();

        Element p = document.createElement("div");
        Element img = document.createElement("img");
        img.setAttribute("src", imageBriefDir + pageEval.getRatingIcon());
        img.setAttribute("alt", "");
        p.appendChild(img);

        Element b = document.createElement("b");
        b.appendChild(document.createTextNode(ratingStr[0] + ": " + ratingStr[1]));
        p.appendChild(b);
        target.appendChild(p);

        appendRatingTable(ratingStr, document, target);

    }

    private static void appendRatingTable(String[] ratingStr, Document document, Node target) {
        Element table = document.createElement("table");
        table.setAttribute("border", "1");
        Element tr = document.createElement("tr");
        Element th = document.createElement("th");
        th.appendChild(document.createTextNode("evaluation"));
        tr.appendChild(th);
        th = document.createElement("th");
        th.appendChild(document.createTextNode("score"));
        tr.appendChild(th);
        table.appendChild(tr);

        int size = ratingStr.length / 2;

        for (int i = 1; i < size; i++) {
            tr = document.createElement("tr");
            Element td = document.createElement("td");
            td.appendChild(document.createTextNode(ratingStr[i * 2]));
            tr.appendChild(td);
            td = document.createElement("td");
            td.appendChild(document.createTextNode(ratingStr[i * 2 + 1]));
            tr.appendChild(td);
            table.appendChild(tr);
        }
        target.appendChild(table);
    }

}
