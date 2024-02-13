package com.java.POSSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Common {
	public static String getUserDatabaseConnectionString() {
		return  new File(new File("").getAbsolutePath(), "\\Database\\user.xml").getPath();
	}
	
	// 리소스로 포함된 파일에 접근하는 방법 주석으로 남겨두기
	//  public static String getUserDatabaseConnectionString() {
	//      ClassLoader classLoader = Common.class.getClassLoader();
	//      URL resourceURL = classLoader.getResource("user.xml");
	//      if (resourceURL != null) {
	//         return resourceURL.getPath();
	//      } else {
	//         System.err.println("user.xml resource not found.");
	//         return null;
	//      }
	//   }

	// 리소스로 포함된 파일에 접근하는 방법 주석으로 남겨두기
	//   public static InputStream getUserDatabaseStream() {
	//      try {
	//         ClassLoader classLoader = Common.class.getClassLoader();
	//         return classLoader.getResourceAsStream("user.xml");
	//
	//      } catch (Exception e) {
	//         // TODO Auto-generated catch block
	//         e.printStackTrace();
	//      }
	//      return null;
	//   }

	public static String getMenuDatabaseConnectionString() {
		return new File(new File("").getAbsolutePath(), "\\Database\\menu.xml").getPath();
	}

	public static String getOrderDatabaseConnectionString() {
		return new File(new File("").getAbsolutePath(), "\\Database\\order.xml").getPath();
	}

	public static String XMLTagValue(String original, String tagName) {
		int startTagIndex = original.indexOf("<" + tagName + ">") + ("<" + tagName + ">").length();
		int endTagIndex = original.indexOf("</" + tagName + ">");
		return  original.substring(startTagIndex, endTagIndex);
	}

	public static Document getXMLDocument(String path) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(path);
	}     

	public static Document getXMLDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(stream);
	}
}