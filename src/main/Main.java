package main;

import graph.Graph;
import graph.Node;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;

import javax.swing.text.html.HTMLEditorKit.Parser;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import pagerank.PageRank;

public class Main {
	
	private static String path = "tintuc/thegioi/";
	
	public static void main(String[] args) throws IOException {
		// Configure and open database using builder pattern.
		// All options are available with code auto-completion.
		File dbFile = File.createTempFile("mapdb", "db");
		DB db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown()
				.encryptionEnable("datasection").make();
		// open an collection, TreeMap has better performance than HashMap
		ConcurrentNavigableMap<String, ArrayList<String>> dTable = db
				.getTreeMap("DocDatabase");
		ConcurrentNavigableMap<String, ArrayList<String>> wTable = db
				.getTreeMap("WordDatabase");
		ConcurrentNavigableMap<String, Map<Double, Double>> verticesTable = db
				.getTreeMap("PageRankDatabase");
		ConcurrentNavigableMap<String, Node> nodeTable = db
				.getTreeMap("GraphDatabase");
		
		// parse all the files in folder
		parser.Parser parser = new parser.Parser(path);
		parser.keywordParser(db, dTable, wTable);
		
		//get keyword
		System.out.println("Input keyword: ");
		Scanner input = new Scanner(System.in);
		String keyword = input.nextLine();
		if(!wTable.containsKey(keyword)) {
			System.out.println("Keyword doesn't exist!");
			return;
		}
		//print word list to file
		PrintWriter pw = new PrintWriter(new File("wordlist.txt"));
		for(Map.Entry entry : wTable.entrySet()) {
			pw.println(entry.getKey());
			pw.flush();
		}
		pw.close();
		
		
		//create graph
		Graph g = new Graph();
		g.init(db, dTable, wTable, nodeTable);
		g.getMinDistanceSubgraph(db, dTable, wTable, nodeTable, keyword);
		ArrayList<String> subgraph = new ArrayList<String>();
		for(Map.Entry entry : wTable.entrySet()) {
			
			Node node = nodeTable.get(entry.getKey());

			if(!subgraph.contains(node.getPreviousNodeName())) {
				subgraph.add(node.getPreviousNodeName());
			}
		}
		
		ArrayList<String> removeDoc = new ArrayList<>();
		
		for(Map.Entry entry : dTable.entrySet()) {
			
			if(!subgraph.contains(entry.getKey())) {
				dTable.remove(entry.getKey());
				removeDoc.add(entry.getKey().toString());
			}
		}
		
		for(Map.Entry entry : wTable.entrySet()) {
			ArrayList<String> adjacent = wTable.get(entry.getKey());
			for(int i=0;i<removeDoc.size();i++) {
				adjacent.remove(removeDoc.get(i));
			}
			wTable.replace(entry.getKey().toString(), adjacent);
		}

		PageRank pr = new PageRank();
		pr.run(db, dTable, wTable, verticesTable, keyword);
		
		WordSimilarity ws = new WordSimilarity();
		Map<String, Double> sorted = ws.run(db, verticesTable, wTable, keyword);
		
		int count = 0;
		System.out.println("\nDach sach cac tu co do tuong dong cao nhat:\n");
		for(Map.Entry entry : sorted.entrySet()) {
			
			System.out.println(entry.getKey());
			count++;
			if(count == 20) break;
			
		}

		db.close();
	}
}
