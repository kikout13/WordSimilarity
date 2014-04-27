package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

import org.jsoup.Jsoup;
import org.mapdb.DB;

import article.Article;

import com.google.gson.Gson;

public class Parser {
	
	private String path; 
	
	public Parser(String path) {
		this.path = path;
	}
	
	public void keywordParser(DB db, ConcurrentNavigableMap<String, ArrayList<String>> dTable,
			ConcurrentNavigableMap<String, ArrayList<String>> wTable) throws IOException {
		
		//get all files in folder 
		File dir = new File(path);
		File[] files = dir.listFiles();
		int file_number = files.length;
		
		//get keyword
		System.out.println("Parse file...");
		for(int i=0; i<file_number;i++) {
			String filename = path + String.format("%06d", i+1) + ".json";
			//System.out.println("Parsing content from file: " + filename);
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String currentLine;
			StringBuffer strBuff = new StringBuffer();
			
			while ((currentLine = br.readLine()) !=null) {
				strBuff.append(currentLine);	
			}
			
			Article art = new Article();
			Gson gson = new Gson();
			art = gson.fromJson(strBuff.toString(), Article.class);
			String[] keyword = art.getKeyword();
			ArrayList keywordsList = new ArrayList<>();
			
			//create word table and document table
			for(int j=0;j<keyword.length;j++) {
				keywordsList.add(keyword[j]);
				ArrayList<String> documentsList;
				
				if(wTable.containsKey(keyword[j])) 
					documentsList = wTable.get(keyword[j]);		
				else documentsList = new ArrayList<>();
		
				documentsList.add(filename);
				
				wTable.put(keyword[j], documentsList);
			}
			
			dTable.put(filename, keywordsList);		
		}
		System.out.println("Parse file: done!");
	}
	
}
