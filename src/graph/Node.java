package graph;

import java.io.Serializable;

public class Node implements Serializable{
	
	private int color;
	private String prev;
	private int minDistance;
	
	public Node(int color, String prev) {
		this.color = color;
		this.prev = prev;
	}
	
	public int getColor() {
		return color;
	}
	
	public String getPreviousNodeName() {
		return prev;
	}
	
	public int getMinDistance() {
		return minDistance;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public void setPreviousNodeName(String nodeName) {
		this.prev = nodeName;
	}
	
	public void setminDistance(int minDistance) {
		this.minDistance = minDistance;
	}
	
}
