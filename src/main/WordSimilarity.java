package main;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;

public class WordSimilarity {
	
	public WordSimilarity() {
		// TODO Auto-generated constructor stub
	}
	
	public Map<String, Double> run (DB db, ConcurrentNavigableMap<String, Map<Double, Double>> verticesTable, 
			ConcurrentNavigableMap<String, ArrayList<String>> wTable, String keyword) {
		
		Map<String, Double> wordMap = new HashMap<String, Double>();
		Double keywordPageRank = 0.00;
		Map<Double, Double> kw = verticesTable.get(keyword);
		double kwoldPagerank = 0.00, kwnewPagerank = 0.00;
			
			for(Map.Entry entry2 : kw.entrySet()) {
				kwoldPagerank = (Double) entry2.getKey();
				kwnewPagerank = (Double) entry2.getValue();
			}
			
			keywordPageRank = kwnewPagerank;
		
		for(Map.Entry entry : verticesTable.entrySet()) {
			
			if(wTable.containsKey(entry.getKey())) {
				double oldPagerank = 0.00, newPagerank = 0.00;
				Map<Double, Double> pagerank = verticesTable.get(entry.getKey());
				
				for(Map.Entry entry2 : pagerank.entrySet()) {
					
					oldPagerank = (Double) entry2.getKey();
					newPagerank = (Double) entry2.getValue();
					
				}
				
				wordMap.put(entry.getKey().toString(), Math.abs(keywordPageRank- newPagerank));
			}
		}
		
		Map<String, Double> sorted = sortByValues(wordMap);
		
		return sorted;
	}
	
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		// LinkedHashMap will keep the keys in the order they are inserted
		// which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
