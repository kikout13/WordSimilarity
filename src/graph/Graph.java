package graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Graph {
	
	public Graph() {
		// TODO Auto-generated constructor stub
	}
	
	//create bi-graph with every word and document are nodes
	//node table is a list of all node in graph
	public void init(DB db, ConcurrentNavigableMap<String, ArrayList<String>> dTable,
			ConcurrentNavigableMap<String, ArrayList<String>> wTable, 
			ConcurrentNavigableMap<String, Node> nodeTable) {
		System.out.println("Initiate graph...");
		for(Map.Entry entry : wTable.entrySet()) {
			Node node = new Node(0, null);
			nodeTable.put(entry.getKey().toString(), node);
		}
		
		for(Map.Entry entry : dTable.entrySet()) {
			Node node = new Node(0, null);
			nodeTable.put(entry.getKey().toString(), node);
		}

		
		for(Map.Entry entry : nodeTable.entrySet()) {
			Node node = (Node) entry.getValue();
			node.setminDistance(nodeTable.size());
			nodeTable.put(entry.getKey().toString(), node);
		}
		System.out.println("Initiate graph: done!");
	}
	
	//get min distance subgraph of a graph from a source node
	//use Dijktras algorithm
	public void getMinDistanceSubgraph(DB db, ConcurrentNavigableMap<String, ArrayList<String>> dTable,
			ConcurrentNavigableMap<String, ArrayList<String>> wTable, 
			ConcurrentNavigableMap<String, Node> nodeTable, String keyword) {
		
		System.out.println("Get subgraph...");
		ArrayList<String> processingNode = new ArrayList<String>();
		
		//set keyword node's color = 2 
		Node node = nodeTable.get(keyword);
		node.setColor(1);
		node.setminDistance(0);
		node.setPreviousNodeName(keyword);
		nodeTable.put(keyword, node);
		processingNode.add(keyword);
		
		//travel throw all node from source node
		while(processingNode.size() > 0) {
			node = nodeTable.get(processingNode.get(0));
			node.setColor(2);
			nodeTable.put(processingNode.get(0), node);
			
			ArrayList<String> adjacent = new ArrayList<String>();
			if(wTable.containsKey(processingNode.get(0))) {
				adjacent = wTable.get(processingNode.get(0)); 
			}
			else if(dTable.containsKey(processingNode.get(0))) {
				adjacent = dTable.get(processingNode.get(0));
			}
			
			for(int i=0; i<adjacent.size();i++) {
				Node n = nodeTable.get(adjacent.get(i));
				if(n.getColor() == 0) {
					n.setColor(1);
					
					if(node.getMinDistance()+1 < n.getMinDistance()) {
						n.setminDistance(node.getMinDistance()+1);
						n.setPreviousNodeName(processingNode.get(0));
						//System.out.println(processingNode.get(0) + "  " + adjacent.get(i));
					}
					nodeTable.put(adjacent.get(i), n);
					processingNode.add(adjacent.get(i));
				}
			}
			
			processingNode.remove(processingNode.get(0));
		}
		
		System.out.println("Get subgraph: done!");
	}
	
}
