package com.withparadox2.whut;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.withparadox2.whut.dao.WhutGlobal;

public class GetWebData {
	
	private HttpClient httpclient;
	private String line = "";
	private String[][] result = new String[4][5] ;
	 
	public GetWebData() {
		// TODO Auto-generated constructor stub
		
	}
	
	
	public void getHtml(){
		 HttpParams params = new BasicHttpParams();
	     params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	      httpclient = new DefaultHttpClient(params);
	     HttpGet httpget1 = new HttpGet(WhutGlobal.URL_HEADER_STR + "tjkbcx.aspx?xh="+ WhutGlobal.USER_ID +"&xm="+ WhutGlobal.USER_NAME +"&gnmkdm=N121601");
	         httpget1.setHeader("Referer", WhutGlobal.URL_HEADER_STR +"xs_main.aspx?xh="+ WhutGlobal.USER_ID);
	         HttpResponse response3;
				try {
					  System.out.println("start--get");
					response3 = httpclient.execute(httpget1);
				     System.out.println("end--get");
			            HttpEntity entity = response3.getEntity();
			            InputStream is = entity.getContent();
			           
			            BufferedInputStream bis = new BufferedInputStream(is);
			            byte  bytearray[] = new  byte[80000];
			             int current= -1;
			             int i=0;  
			             while((current=bis.read())!=-1) {
			                 bytearray[i] =(byte) current;
			                  i++;
			           }
			          line = new String (bytearray,"GB2312");
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	    
	}
	
	public String[][] getWebData(){
		getHtml();
	     Document document = Jsoup.parse(line);
	     Element table = document.select("Table").get(1);
	     Elements trs =  table.select("tr");
	     String fuck;
	    		 
	     for(int i=1; i<5; i++){
	     	Element tr = trs.get(2*i);
	     	Elements tds = tr.select("td");
	     	if(tds.size()==9) tds.remove(0); 
	     	for(int j=1; j<6; j++){
	     		fuck = tds.get(j).html().toString().replaceAll("<br />", "\n").replaceAll("&nbsp;", "").replaceAll("\n\n\n", "\n\n");
	     		result[i-1][j-1] = fuck.endsWith("\n") ? fuck.substring(0, fuck.length()-1) : fuck;
	     		System.out.println(i+"++"+j+"+++"+result[i-1][j-1]);
	     	}
	     }
		return result;
		
}
	

}
