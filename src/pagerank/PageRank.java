package pagerank;

import graph.Graph;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

import main.WordSimilarity;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class PageRank {
	
	public PageRank() {
		// TODO Auto-generated constructor stub
	}
	
	//Calculate personalize pagerank on a bi-graph
	public void run(DB db, ConcurrentNavigableMap<String, ArrayList<String>> dTable,
			ConcurrentNavigableMap<String, ArrayList<String>> wTable, 
			ConcurrentNavigableMap<String, Map<Double, Double>> verticesTable,
			String keyword) {
		
		System.out.println("Calculate pagerank...");
		double epsilon = 0.000001;
		int N = 0;
		
		System.out.println("\tCreate vertices table");
		for(Map.Entry entry : wTable.entrySet()) {
			Map<Double, Double> pagerank = new HashMap<>();
			pagerank.put(0.00, 0.00);
			verticesTable.put(entry.getKey().toString(), pagerank);
			N++;
		}
		
		for(Map.Entry entry : dTable.entrySet()) {
			Map<Double, Double> pagerank = new HashMap<>();
			pagerank.put(0.00, 0.00);
			verticesTable.put(entry.getKey().toString(), pagerank);
			N++;
		}
		System.out.println("\tCreate vertices table: done");
		
		//init pagerank of keyword = 1
		System.out.println("\tInitiate source vertice's pagarank...");
		Map<Double, Double> pagerank = verticesTable.get(keyword);
		pagerank.put(0.00, 1.00);
		verticesTable.put(keyword, pagerank);
		System.out.println("\tInitiate source vertice's pagarank: done!");
		
		//recalculate pagerank of all vertices
		System.out.println("\tRecalculate pagerank...");
		while(Convergence(db, verticesTable)>epsilon) {

			for(Map.Entry entry : verticesTable.entrySet()) {
			
				double oldPagerank = 0.00, newPagerank = 0.00;
				Map<Double, Double> tmp_pagerank = verticesTable.get(entry.getKey());
			
				for(Map.Entry entries : tmp_pagerank.entrySet()) {
					
					oldPagerank = (Double) entries.getKey();
					newPagerank = (Double) entries.getValue();
					
				}
			
				tmp_pagerank.remove(oldPagerank);
				oldPagerank = newPagerank;
				newPagerank = 0.00;
				tmp_pagerank.put(oldPagerank, newPagerank);
				verticesTable.put(entry.getKey().toString(), tmp_pagerank);
			}
		
			for(Map.Entry entry : verticesTable.entrySet()) {
			
				if(wTable.containsKey(entry.getKey())) {
					ArrayList<String> adjacent = wTable.get(entry.getKey());
					double temp = 0.00;
				
					for(int i=0; i<adjacent.size(); i++) {
						temp += getOldPagerank(db, verticesTable, adjacent.get(i)) / dTable.get(adjacent.get(i)).size();
					}
				
					temp = 0.15/N + 0.85*temp;
					setNewPagerank(db, verticesTable, entry.getKey().toString(), temp);
				}
			
				if(dTable.containsKey(entry.getKey())) {
					ArrayList<String> adjacent = dTable.get(entry.getKey());
					double temp = 0.00;

					for(int i=0; i<adjacent.size(); i++) {
						temp += getOldPagerank(db,verticesTable, adjacent.get(i)) / wTable.get(adjacent.get(i)).size();
					}

					temp = 0.15/N + 0.85*temp;
					setNewPagerank(db, verticesTable, entry.getKey().toString(), temp);
				}

			}
		}
		
		System.out.println("\tRecalculate pagerank: done!");
		System.out.println("Calculate pagerank: done!");
	}
	
	private double getOldPagerank(DB db, ConcurrentNavigableMap<String, 
			Map<Double, Double>> verticesTable,String keyword) {
		
		double oldPagerank = 0.00;
		Map<Double, Double> pagerank = verticesTable.get(keyword);
		
		for(Map.Entry entry : pagerank.entrySet()) {
			oldPagerank = (Double) entry.getKey();
		}
		
		return oldPagerank;
	}
	
	private double getNewPagerank(DB db, ConcurrentNavigableMap<String, 
			Map<Double, Double>> verticesTable, String keyword) {
		
		double newPagerank = 0.00;
		Map<Double, Double> pagerank1 = verticesTable.get(keyword);
		
		for(Map.Entry entry : pagerank1.entrySet()) {
			newPagerank = (Double) entry.getValue();
		}
		
		return newPagerank;
	}
	
	private void setNewPagerank(DB db, ConcurrentNavigableMap<String, Map<Double, Double>> verticesTable,
			String keyword, double newPR) {
		
		double oldPagerank = 0.00, newPagerank = 0.00;
		Map<Double, Double> pagerank = verticesTable.get(keyword);
		
		for(Map.Entry entry : pagerank.entrySet()) {
			oldPagerank = (Double) entry.getKey();
			newPagerank = (Double) entry.getValue();
		}
		
		pagerank = new HashMap<>();
		pagerank.put(oldPagerank, newPR);
		verticesTable.put(keyword, pagerank);
	}
	
	private double Convergence(DB db, ConcurrentNavigableMap<String, Map<Double, Double>> verticesTable) {
		
		double oldPagerank = 0.00, newPagerank = 0.00;
		double sum = 0.00;
		
		for(Map.Entry entry : verticesTable.entrySet()) {
			
			Map<Double, Double> pagerank = verticesTable.get(entry.getKey());
		
			for(Map.Entry entries : pagerank.entrySet()) {
				oldPagerank = (Double) entries.getKey();
				newPagerank = (Double) entries.getValue();
				sum += Math.pow(oldPagerank - newPagerank, 2);
			}
		}
		
		return sum;
	}
	
}
