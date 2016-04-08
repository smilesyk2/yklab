package com.wisenut.worker;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wisenut.common.WNConstants;
import com.wisenut.common.WNProperties;
import com.wisenut.model.WNResultData;
import com.wisenut.util.StringUtil;

public class DaumWorker {
	private String daumOpenAPIURL = "https://apis.daum.net/search/web";
	
	public DaumWorker(){
		
	}
	
	public void search(String query, int startPos, int pageNo, String sort, WNResultData data){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		WNProperties wnprop = WNProperties.getInstance(WNConstants.DAUM_ID);
		String strUrl = daumOpenAPIURL + "?"
				+ "apikey=" + wnprop.getPropValues().getClient_id() + "&"
				+ "q=" + query + "&"
				+ "result=" + pageNo + "&"
				+ "pageno=" + startPos + "&"
				+ "sort=" + sort + "&"
				+ "output=xml";
		
		try {
			//API 요청 및 반환
			URL url = new URL(strUrl);
			URLConnection conn = url.openConnection();
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());

			data.setTotalCount(Integer.parseInt(doc.getElementsByTagName("totalCount").item(0).getTextContent()));
			data.setCurrentCount(Integer.parseInt(doc.getElementsByTagName("result").item(0).getTextContent()));
			data.setStartPos(startPos);
			
			NodeList itemList = doc.getElementsByTagName("item");
			
			for(int i=0; i<itemList.getLength(); i++){
				Node item = itemList.item(i);
				
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) item;
					data.addItem(eElement.getElementsByTagName("title").item(0).getTextContent(),
							StringUtil.removeSpecialCharacter(eElement.getElementsByTagName("description").item(0).getTextContent()),
							eElement.getElementsByTagName("pubDate").item(0).getTextContent(),
							"",
							eElement.getElementsByTagName("link").item(0).getTextContent(),
							"");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
