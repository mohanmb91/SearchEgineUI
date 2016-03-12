package com.ag.web.SearchUserInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.json.JSONObject;
import com.ag.web.Dal.MongoDal;
import com.ag.web.Model.keyValue;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class Search {
	static MongoDal dal = new MongoDal();
	//static Indexing objIndexing = new Indexing();
	static DBCollection wordIndexTable =  dal.getTableFromDatabase("searchengine", "wordindextable");
	static DBCollection linkAnalysisTable =  dal.getTableFromDatabase("searchengine", "linkAnalysis");



	public List<keyValue> performSearch(String searchString){
		List<String> urls = new ArrayList<String>();
		//String searchString = getInputFromUser().toLowerCase();
		searchString = searchString.trim();
		String words[] = searchString.split(" ");
		if(words.length == 1)
		{
			if(isWordExsit(searchString)){
				Map<String,Double> urlScoreMap = getDocumentPositionList(searchString);
				urls = getURLAfterRanking(urlScoreMap);
			}
			else
			{
				System.out.println("word not found");
			}
		}
		else
		{
			System.out.println("2 or more words");
			Map<String,Double> urlScoreMap = new HashMap<String, Double>();
			for (int i = 0; i < words.length; i++) {
				if(urlScoreMap.isEmpty()){
					urlScoreMap = getDocumentPositionList(words[i]);
					System.out.println("List of URLS for word 1 " + words[i]);
					Set<Entry<String,Double>> urlScoreSet = urlScoreMap.entrySet();
					for (Entry<String, Double> entry : urlScoreSet) {
						System.out.println(entry.getKey()+"  "+entry.getValue());
					}
				}
				else
				{
					Map<String,Double> newUrlScoreMap = new HashMap<String, Double>();
					newUrlScoreMap = getDocumentPositionList(words[i]);
					
					System.out.println("List of URLS for word 22 " + words[i]);
					Set<Entry<String,Double>> newUrlScoreSet = newUrlScoreMap.entrySet();
					for (Entry<String, Double> entry : newUrlScoreSet) {
						System.out.println(entry.getKey()+"  "+entry.getValue());
					}
					
					urlScoreMap = mergeBothMaps(urlScoreMap,newUrlScoreMap);
				}
			}
			
			urls = getURLAfterRanking(urlScoreMap);			
		}
		List<keyValue> urlWithTitle = new ArrayList<keyValue>();
		urlWithTitle = getUrlWithTitle(urls);
		return urlWithTitle;
	}
	


	private List<keyValue> getUrlWithTitle(List<String> urls) {
		DBCollection urlTable =  dal.getTableFromDatabase("searchengine", "urltable");
		List<keyValue> urlWithTitle = new ArrayList<keyValue>();
		for (String eachUrl : urls) {
			keyValue eachKeyValue = new keyValue();
			eachKeyValue.key = "DNS";
			eachKeyValue.value = eachUrl;
			String title = " ";
			DBCursor cursor =  dal.findInTableAsPerQuery(urlTable, eachKeyValue);
			while(cursor.hasNext()){
				DBObject eachRow = cursor.next();
				String titleFromTable = (String) eachRow.get("title");
				String titleItems[] = titleFromTable.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
				System.out.println(titleFromTable);
				for (String eachString : titleItems) {
					System.out.println(eachString);
					title = title.concat(eachString).concat(" ");
				}
			}
			eachKeyValue.key = eachUrl;
			eachKeyValue.value = title;
			urlWithTitle.add(eachKeyValue);
		}
		return urlWithTitle;
	}



	private Map<String, Double> mergeBothMaps(Map<String, Double> urlScoreMap, Map<String, Double> newUrlScoreMap) {
		
		//urlScoreMap.putAll(newUrlScoreMap);
		
		
		Map<String, Double> finalUrlScoreMap = new HashMap<String, Double>();
		//Map<String, Double> finalUrlScoreMap = new HashMap<String, Double>();

		finalUrlScoreMap.putAll(urlScoreMap);
		finalUrlScoreMap.putAll(newUrlScoreMap);
		Set<Entry<String,Double>> finalUrlScoreMapSet = finalUrlScoreMap.entrySet();
		for (Entry<String, Double> finalUrlScoreMapSetEntry : finalUrlScoreMapSet) {
			finalUrlScoreMapSetEntry.setValue(0.0);
		}
		
		for (Entry<String, Double> eachEntry : finalUrlScoreMapSet) {
			if((urlScoreMap.containsKey(eachEntry.getKey())) && (newUrlScoreMap.containsKey(eachEntry.getKey()))){
				double score = urlScoreMap.get(eachEntry.getKey()) + newUrlScoreMap.get(eachEntry.getKey());
				score = score + eachEntry.getValue();
				eachEntry.setValue(score);
			}
			else{
				if(urlScoreMap.containsKey(eachEntry.getKey())){
					double score = urlScoreMap.get(eachEntry.getKey());
					score = score + eachEntry.getValue();
					eachEntry.setValue(score);
				}
				else{
					if(newUrlScoreMap.containsKey(eachEntry.getKey())){
						double score = newUrlScoreMap.get(eachEntry.getKey());
						score = score + eachEntry.getValue();
						eachEntry.setValue(score);
					}
					}
			}
		}
		return finalUrlScoreMap;
	}



	private List<String> getURLAfterRanking(Map<String, Double> urlScoreMap) {
		System.out.println("Before Ranking");
		List<String> urls = new ArrayList<String>();
		Set<Entry<String, Double>> urlScoreSet = urlScoreMap.entrySet();
		for (Entry<String, Double> entry : urlScoreSet) {
			System.out.println(entry.getKey()+" ==== "+entry.getValue());

		}
		System.out.println("After Ranking");

		List<Entry<String, Double>> urlScoreList = new ArrayList<Entry<String, Double>>(urlScoreSet);
		Collections.sort( urlScoreList, new Comparator<Map.Entry<String, Double>>()
		{
			public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
			{
				return (o2.getValue()).compareTo( o1.getValue() );
			}
		} );
		for(Map.Entry<String, Double> eachURLScoreEntry:urlScoreList){
			System.out.println(eachURLScoreEntry.getKey()+" ==== "+eachURLScoreEntry.getValue());
			urls.add(eachURLScoreEntry.getKey());
		}
		return urls;
		
	}


	private boolean isWordExsit(String word) {
		DBCursor cursor =  dal.findInTableAsPerQuery(wordIndexTable, new keyValue("word",word));
		if(cursor.hasNext()){
			return true;
		}
		return false;
	}


	private Map<String,Double> getDocumentPositionList(String searchString) {

		DBCursor cursor =  dal.findInTableAsPerQuery(wordIndexTable, new keyValue("word",searchString));
		//Map<String,List<PositionTFweightageModel>> frequencyMapFromTable = new HashMap<String, List<PositionTFweightageModel>>(); 
		Map<String,Double> urlScoreMap = new HashMap<String, Double>();
		if(cursor.hasNext()){
			DBObject eachRow = cursor.next();
			JSONObject jsonObject = new JSONObject( (String) eachRow.get("frequency")); // HashMap
			Iterator<?> urlKeySet = jsonObject.keys(); // HM
			double weightage = 0.0;
			double score = 0.0;
			double titleScore = 0.5;
			String title[] = null;
			while (urlKeySet.hasNext()) {
				String eachurlKey=  (String) urlKeySet.next();

				//get the score from link analysis table
				DBCursor cursorOfLinkAnalysisTable =  dal.findInTableAsPerQuery(linkAnalysisTable, new keyValue("url",eachurlKey));
				double linkAnalysisScore = 0.0;
				if(cursorOfLinkAnalysisTable.hasNext()){
					DBObject eachRowOfLinkAnalysisTable = cursorOfLinkAnalysisTable.next();
					linkAnalysisScore = Double.parseDouble(eachRowOfLinkAnalysisTable.get("rank").toString());

				}
				
				//getting json for evey url
				JSONObject innerJsonObject = new JSONObject((jsonObject.get(eachurlKey)).toString()); // HashMap
				weightage = Double.parseDouble((innerJsonObject.get("weightage").toString()));
				title = (innerJsonObject.get("title")).toString().split(" ");
				for (int i = 1; i < title.length; i++) {
					if(title[i].equalsIgnoreCase(searchString)){
						titleScore = titleScore+( 5.0 /(double)(i*10));
						break;
					}
					score = weightage + titleScore + linkAnalysisScore;
					urlScoreMap.put(eachurlKey,score);
				}
			}
		}
		return urlScoreMap;
	}

}
