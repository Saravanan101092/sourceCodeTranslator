package com.sourcecode.translator.googleclient;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.sourcecode.translator.utils.Constants;

@Component
public class GoogleAPIClient {

	public String API;
	
	@Autowired
	Environment env;
	
	@PostConstruct
	public void init() {
		API = env.getProperty(Constants.API_PROP);
	}
	public Map<String,String> translate(String[] words, String sourceLan, String targetLan) throws Exception {
		String originalTextStr = StringUtils.arrayToDelimitedString(words, System.getProperty(Constants.LINE_SEPERATOR));
		return callAPI(originalTextStr,sourceLan,targetLan);	
	}
	
	
	public String simpleTranslate(String text,String source, String target) throws Exception{
		Thread.sleep(1000);
		RestTemplate restTemplate = new RestTemplate();
		URIBuilder uriBuilder = new URIBuilder(API);
		uriBuilder.addParameter("client", "gtx");
		uriBuilder.addParameter("sl", source/*"es"*/);
		uriBuilder.addParameter("tl", target/*"en"*/);
		uriBuilder.addParameter("dt", "t");
		uriBuilder.addParameter("q", text);
		 HttpHeaders headers = new HttpHeaders();
         headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
         headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
         HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		ResponseEntity<String> response = restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, String.class);
		JSONArray jsonObj = new JSONArray(response.getBody());
		
		return getStringOfResponse(jsonObj);
	}
	public Map<String,String> callAPI(String text,String source, String target) throws URISyntaxException, JSONException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		RestTemplate restTemplate = new RestTemplate();
		URIBuilder uriBuilder = new URIBuilder(API);
		uriBuilder.addParameter("client", "gtx");
		uriBuilder.addParameter("sl", source/*"es"*/);
		uriBuilder.addParameter("tl", target/*"en"*/);
		uriBuilder.addParameter("dt", "t");
		uriBuilder.addParameter("q", text);
		 HttpHeaders headers = new HttpHeaders();
         headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
         headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
         HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		ResponseEntity<String> response = restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, String.class);
		JSONArray jsonObj = new JSONArray(response.getBody());
		return getMap(jsonObj);
	}
	
	public static String getStringOfResponse(JSONArray jsonarr) throws JSONException {
		JSONArray jsonarr2 = (JSONArray)jsonarr.get(0);
			JSONArray json = (JSONArray)jsonarr2.get(0);
			return json.getString(0);
	}
	
	public static Map<String,String> getMap(JSONArray jsonarr) throws JSONException{
		Map<String,String> map = new HashMap<>();
			JSONArray jsonarr2 = (JSONArray)jsonarr.get(0);
			for(int index=0;index<jsonarr2.length();index++) {
				JSONArray json = (JSONArray)jsonarr2.get(index);
				String orgText = json.getString(1);
				orgText = orgText.replaceAll(System.getProperty(Constants.LINE_SEPERATOR), "");
				String trnsltdText = json.getString(0);
				trnsltdText = trnsltdText.replaceAll(System.getProperty(Constants.LINE_SEPERATOR), "");
				if(trnsltdText.contains(",")) {
					trnsltdText = getCamelCaseFromCommaDelimitedString(trnsltdText);
				}
				if(orgText.contains(",")) {
					orgText = getCamelCaseFromCommaDelimitedString(orgText);
				}
				map.put(orgText, trnsltdText);
			}
		return map;
	}
	
	public static String getCamelCaseFromCommaDelimitedString(String commaDelimited) {
		String[] splt = commaDelimited.split(",",-1);
		StringBuilder result = new StringBuilder();
		for(int i=0;i<splt.length;i++) {
			result.append(splt[i].trim());
		}
		return result.toString();
	}
}
