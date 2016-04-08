package com.wisenut.worker;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wisenut.common.WNConstants;
import com.wisenut.common.WNProperties;
import com.wisenut.model.WNResultData;
import com.wisenut.util.StringUtil;

public class NaverWorker {
	private String naverOpenAPIURL = "https://openapi.naver.com/v1/search/news.xml";
	
	public NaverWorker(){
		
	}
	
	public void search(String query, int startPos, int pageNo, String sort, WNResultData data){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		WNProperties wnprop = WNProperties.getInstance(WNConstants.NAVER_ID);
		String requestUrl = naverOpenAPIURL + "?"
				+ "query=" + query + "&"
				+ "display=" + pageNo + "&"
				+ "start=" + startPos + "&"
				+ "sort=" + sort;
		
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(requestUrl);
			
			request.addHeader("X-Naver-Client-Id", wnprop.getPropValues().getClient_id()); //발급받은ID
			request.addHeader("X-Naver-Client-Secret", wnprop.getPropValues().getClient_secret()); //발급받은 PW
			
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();
			builder = factory.newDocumentBuilder();
			
			Document doc = builder.parse(is);
			data.setTotalCount(Integer.parseInt(doc.getElementsByTagName("total").item(0).getTextContent()));
			data.setCurrentCount(Integer.parseInt(doc.getElementsByTagName("display").item(0).getTextContent()));
			data.setStartPos(Integer.parseInt(doc.getElementsByTagName("start").item(0).getTextContent()));
			
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
