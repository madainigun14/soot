/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;

import soot.tagkit.*;
import soot.util.*;
import soot.xml.*;
import java.util.*;
import java.io.*;

public class XMLAttributesPrinter {

	
	private String inFilename;
	private String useFilename;
	private SootClass sootClass;
	private String outputDir;
    private ArrayList attributes;
    
	private void setOutputDir(String dir) {
		outputDir = dir;
	}

	private String getOutputDir() {
		return outputDir;
	}
	
	public XMLAttributesPrinter(String filename, String outputDir) {
		setInFilename(filename);
		setOutputDir(outputDir);
		initAttributesDir();
		createUseFilename();
		initFile();
	}

	private void initFile() {
        attributes = new ArrayList();
		try {
		  streamOut = new FileOutputStream(getUseFilename());
		  writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
		  writerOut.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		  writerOut.println("<attributes>");
		}
		catch(IOException e1) {
		  G.v().out.println(e1.getMessage());
		}
									
	}

	private void finishFile() {
		  writerOut.println("</attributes>");
		  writerOut.close();
	}
    
	public void printAttrs(SootClass c) {
	
        soot.xml.TagCollector tc = new soot.xml.TagCollector();
        tc.collectTags(c);
        tc.printTags(writerOut);
        
		/*int java_ln = 0;
		int jimple_ln = 0;
	
        // tag fields
        Iterator fit = c.getFields().iterator();
		while (fit.hasNext()){
            SootField sf = (SootField)fit.next();
            Iterator fTags = sf.getTags().iterator();
            startPrintAttribute();
            soot.xml.Attribute attr = new soot.xml.Attribute();
            while (fTags.hasNext()){
                Tag t = (Tag)fTags.next();
                printAttributeTag(t, attr);
            }
            attributes.add(attr);
            endPrintAttribute();
        }
        
        // tag methods
        Iterator it = c.getMethods().iterator();
		while (it.hasNext()) {
			SootMethod sm = (SootMethod)it.next();
			if (!sm.hasActiveBody()) {
				continue;
			}
			if (!sm.getTags().isEmpty()){
				Iterator mTags = sm.getTags().iterator();
				startPrintAttribute();
                soot.xml.Attribute attr = new soot.xml.Attribute();
				while (mTags.hasNext()){
					Tag t = (Tag)mTags.next();
					printAttributeTag(t, attr);
				}
                attributes.add(attr);
				endPrintAttribute();
			}
			
			Body b = sm.getActiveBody();
			Iterator itUnits = b.getUnits().iterator();
			while (itUnits.hasNext()) {
				Unit u = (Unit)itUnits.next();
				Iterator itTags = u.getTags().iterator();
				startPrintAttribute();
                soot.xml.Attribute attr = new soot.xml.Attribute();
				while (itTags.hasNext()) {
			   		Tag t = (Tag)itTags.next();
                    System.out.println("tag class: "+t.getClass());
					printAttributeTag(t, attr);
				}
				Iterator valBoxIt = u.getUseAndDefBoxes().iterator();
				while (valBoxIt.hasNext()){
					ValueBox vb = (ValueBox)valBoxIt.next();
					if (!vb.getTags().isEmpty()){
						startPrintValBoxAttr();
                        soot.xml.PosColorAttribute vbAttr = new soot.xml.PosColorAttribute();
						Iterator tagsIt = vb.getTags().iterator(); 
						while (tagsIt.hasNext()) {
							Tag t = (Tag)tagsIt.next();
							printAttributeTag(t, vbAttr);
						}
                        attr.addVBAttr(vbAttr);
						endPrintValBoxAttr();
					}
				}
                attributes.add(attr);
				endPrintAttribute();	
			}
		}*/
		finishFile();
	}

	
	FileOutputStream streamOut = null;
	PrintWriter writerOut = null;
	
	/*private void printAttributeTag(Tag t, PosColorAttribute attr) {
		if (t instanceof LineNumberTag) {
            int lnNum = (new Integer(((LineNumberTag)t).toString())).intValue();
			printJavaLnAttr(lnNum);
            ((soot.xml.Attribute)attr).javaStartLn(lnNum);
            ((soot.xml.Attribute)attr).javaEndLn(lnNum);
		}
		else if (t instanceof JimpleLineNumberTag) {
            JimpleLineNumberTag jlnTag = (JimpleLineNumberTag)t;
			printJimpleLnAttr(jlnTag.getStartLineNumber(), jlnTag.getEndLineNumber());
            ((soot.xml.Attribute)attr).jimpleStartLn(jlnTag.getStartLineNumber());
            ((soot.xml.Attribute)attr).jimpleEndLn(jlnTag.getEndLineNumber());
		}
		else if (t instanceof SourceLineNumberTag) {
            SourceLineNumberTag jlnTag = (SourceLineNumberTag)t; 
			printJavaLnAttr(jlnTag.getStartLineNumber(), jlnTag.getEndLineNumber());
            ((soot.xml.Attribute)attr).javaStartLn(jlnTag.getStartLineNumber());
            ((soot.xml.Attribute)attr).javaEndLn(jlnTag.getEndLineNumber());
		}
		else if (t instanceof LinkTag) {
			LinkTag lt = (LinkTag)t;
			Host h = lt.getLink();
			printLinkAttr(formatForXML(lt.toString()), getJimpleLnOfHost(h), getJavaLnOfHost(h), lt.getClassName());
            LinkAttribute link = new LinkAttribute(lt.toString(), getJimpleLnOfHost(h), getJavaLnOfHost(h), lt.getClassName());
            ((soot.xml.Attribute)attr).addLink(link);

		}
		else if (t instanceof StringTag) {
			printTextAttr(formatForXML(((StringTag)t).toString()));
            ((soot.xml.Attribute)attr).addText(formatForXML(((StringTag)t).toString()));
		}
		else if (t instanceof SourcePositionTag){
            System.out.println("printing source position tag");
			SourcePositionTag pt = (SourcePositionTag)t;
			printSourcePositionAttr(pt.getStartOffset(), pt.getEndOffset());
            attr.javaStartPos(pt.getStartOffset());
            attr.javaEndPos(pt.getEndOffset());
		}
        else if (t instanceof PositionTag){
			PositionTag pt = (PositionTag)t;
			printPositionAttr(pt.getStartOffset(), pt.getEndOffset());
            attr.jimpleStartPos(pt.getStartOffset());
            attr.jimpleEndPos(pt.getEndOffset());
		}
		else if (t instanceof ColorTag){
			ColorTag ct = (ColorTag)t;
			printColorAttr(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground());
            ColorAttribute ca = new ColorAttribute(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground());
            attr.color(ca);
		}
		else {
			printTextAttr(t.toString());
            ((soot.xml.Attribute)attr).addText(t.toString());
		}
								
	}*/
	
	private int getJavaLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			//G.v().out.println(t.getClass().toString());
			if (t instanceof SourceLineNumberTag) {
				//G.v().out.println("t is LineNumberTag");
				return ((SourceLineNumberTag)t).getStartLineNumber();
			}
            else if (t instanceof LineNumberTag){
                return (new Integer(((LineNumberTag)t).toString())).intValue();
            }
		}
		return 0;
	}
	
	private int getJimpleLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			if (t instanceof JimpleLineNumberTag) {
				return ((JimpleLineNumberTag)t).getStartLineNumber();
			}
		}
		return 0;
	}

    /*private void organizeAttrs(){
        Iterator it = attributes.iterator();
        soot.xml.Attributes organizedAttrs = new soot.xml.Attributes();
        while (it.hasNext()){
            Object next = it.next();
            if (next instanceof soot.xml.Attribute){
                soot.xml.Attribute attr = (soot.xml.Attribute)next;
                JavaAttribute ja;
                if (!organizedAttrs.hasJavaAttr(attr.javaStartLn())){
                    ja = new JavaAttribute();
                }
                else {
                    ja = organizedAttrs.getJavaAttr(attr.javaStartLn());
                }
                ja.startLn(attr.javaStartLn());
                ja.endLn(attr.javaEndLn());
                JimpleAttribute jpa;
                if (!ja.hasJimpleAttribute(attr.jimpleStartLn())){
                    jpa = new JimpleAttribute();
                }
                else {
                    jpa = 
                }
            }
        }
    }*/
    /*private ArrayList getTextsOfHost(Host h){
        Iterator it = h.getTags().iterator();
        while (it.hasNext()){
            Tag t = (Tag)it.next();
            if (t instanceof StringTag){
            
            }
        }
    }*/
	
	/*private void startPrintAttribute(){
		writerOut.println("<attribute>");
	}

	private void printJavaLnAttr(int start_ln, int end_ln){
		writerOut.println("<java_start_ln>"+start_ln+"</java_start_ln>");
		writerOut.println("<java_end_ln>"+end_ln+"</java_end_ln>");
	}

	private void printJavaLnAttr(int java_ln){
		writerOut.println("<java_ln>"+java_ln+"</java_ln>");
	}

	private void printJimpleLnAttr(int jimple_ln){
		writerOut.println("<jimple_ln>"+jimple_ln+"</jimple_ln>");
	}

	private void printJimpleLnAttr(int start_ln, int end_ln){
		writerOut.println("<jimple_start_ln>"+start_ln+"</jimple_start_ln>");
		writerOut.println("<jimple_end_ln>"+end_ln+"</jimple_end_ln>");
	}

	private void printTextAttr(String text){
		writerOut.println("<text>"+text+"</text>");
	}
	
	private void printLinkAttr(String label, int jimpleLink, int javaLink, String className){
		writerOut.println("<link_attribute>");
		writerOut.println("<link_label>"+label+"</link_label>");
		writerOut.println("<jimple_link>"+jimpleLink+"</jimple_link>");
		writerOut.println("<java_link>"+javaLink+"</java_link>");
		writerOut.println("<className>"+className+"</className>");
		writerOut.println("</link_attribute>");
	}

	/*private void startPrintSourceValBoxAttr(){
		writerOut.println("<source_value_box_attribute>");
	}*/
    
	/*private void startPrintValBoxAttr(){
		writerOut.println("<value_box_attribute>");
	}

	private void printSourcePositionAttr(int start, int end){
		writerOut.println("<sourceStartOffset>"+start+"</sourceStartOffset>");
		writerOut.println("<sourceEndOffset>"+end+"</sourceEndOffset>");
	}
    
	private void printPositionAttr(int start, int end){
		writerOut.println("<startOffset>"+start+"</startOffset>");
		writerOut.println("<endOffset>"+end+"</endOffset>");
	}
	
	private void printColorAttr(int r, int g, int b, boolean fg){
		writerOut.println("<red>"+r+"</red>");
		writerOut.println("<green>"+g+"</green>");
		writerOut.println("<blue>"+b+"</blue>");
        if (fg) {
		    writerOut.println("<fg>1</fg>");
        }
        else {
		    writerOut.println("<fg>0</fg>");    
        }
	}
	
	/*private void endPrintSourceValBoxAttr(){
		writerOut.println("</source_value_box_attribute>");
	}*/
    
	/*private void endPrintValBoxAttr(){
		writerOut.println("</value_box_attribute>");
	}
	
	private void endPrintAttribute(){
		writerOut.println("</attribute>");
	}*/
	

	private void initAttributesDir() {
	
		StringBuffer sb = new StringBuffer();
		String attrDir = "attributes";
		
		sb.append(getOutputDir());
		sb.append(System.getProperty("file.separator"));
		sb.append(attrDir);
		
		File dir = new File(sb.toString());

		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} 
			catch (SecurityException se) {
			        G.v().out.println("Unable to create " + attrDir);
		                //System.exit(0);
		        }
		}
				
	}

	private String  formatForXML(String in) {
		in = StringTools.replaceAll(in, "<", "&lt;");
		in = StringTools.replaceAll(in, ">", "&gt;");
		in = StringTools.replaceAll(in, "&", "&amp;");
		return in;
	}

	private void createUseFilename() {
		String tmp = getInFilename();
		//G.v().out.println("attribute file name: "+tmp);
		tmp = tmp.substring(0, tmp.lastIndexOf('.'));
		int slash = tmp.lastIndexOf(System.getProperty("file.separator"));
		if (slash != -1) {
			tmp = tmp.substring((slash+1), tmp.length()); 
		}
	
		StringBuffer sb = new StringBuffer();
		String attrDir = "attributes";
		sb.append(getOutputDir());
		sb.append(System.getProperty("file.separator"));
		sb.append(attrDir);
		sb.append(System.getProperty("file.separator"));  
		sb.append(tmp);
		sb.append(".xml");
		//tmp = sb.toString()+tmp+".xml";
		setUseFilename(sb.toString());
	}

	private void setInFilename(String file) {
		useFilename = file;
	}

	private String getInFilename() {
		return useFilename;
	}

	private void setUseFilename(String file) {
		useFilename = file;
	}

	private String getUseFilename() {
		return useFilename;
	}
		
	
}
