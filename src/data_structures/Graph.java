package data_structures;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Arrays;
import java.util.LinkedList;

public class Graph<T>{

	Map<T, List<T>> lists; //adjacency lists

	public Graph(){
		lists = new HashMap<T, List<T>>();
	}
	
	public Set<T> vertices(){
		return lists.keySet();
	}
	
	public List<List<T>> edges(){
		List<List<T>> res = new LinkedList<>();
		for(T v1 : this.vertices()) {
			for(T v2 : lists.get(v1)) {
				List<T> edge = new ArrayList<T>(2);
				edge.add(v1);
				edge.add(v2);
				res.add(edge);
			}
		}
		return res;
	}
	
	
	
	@Override
	public String toString() {
		String s = "{";
		for(T origin : lists.keySet()) {
			for(T target : lists.get(origin)) {
				s += origin + " -> " + target + ", ";
			}
		}
		return s + "}";//s.substring(0, s.length()-2) + "}";
	}

	public void addVertex(T vertex) {
		//adds an isolated vertex
		lists.put(vertex, new LinkedList<T>());
	}
	
	public void addEdge(T vertex1, T vertex2) {
		//one need not add the vertices before adding the edge
		if(!lists.containsKey(vertex1)) {
			lists.put(vertex1, new LinkedList<T>());
		}
		if(!lists.containsKey(vertex2)) {
			lists.put(vertex2, new LinkedList<T>());
		}
		if (!lists.get(vertex1).contains(vertex2)) {
			lists.get(vertex1).add(vertex2);
		}
	}
	
	
	public boolean edgeQ(T vertex1, T vertex2) {
		if(lists.containsKey(vertex1)) {
			return lists.get(vertex1).contains(vertex2);
		} else {
			return false;
		}
	}
	
	public List<T> offspring(T vertex1) {
		return lists.get(vertex1);
	}
	
	public List<T> reach(T origin) {
		List<T> visited = new LinkedList<T>();
		Queue<T> q = new LinkedList<T>();
		q.add(origin);
		visited.add(origin);
		while(q.peek() != null) {
			T node = q.remove();
			List<T> offspring = offspring(node);
			for (T child : offspring) {
				if (! visited.contains(child)) {
					q.add(child);
					visited.add(child);
				}
			}
		}
		return visited;
	}
	
	public List<T> strictReach(T origin) {
		//Same as reach but origin is not reachable if it is only 
		//reachable by the null path (useful for non reflexive relations)
		List<T> visited = new LinkedList<T>();
		Queue<T> q = new LinkedList<T>();
		q.add(origin);
		while(q.peek() != null) {
			T node = q.remove();
			List<T> offspring = offspring(node);
			for (T child : offspring) {
				if (! visited.contains(child)) {
					q.add(child);
					visited.add(child);
				}
			}
		}
		return visited;
	}
	
	public Graph<T> transpose(){
		Graph<T> res = new Graph<>();
		for(List<T> edge : this.edges()) {
			res.addEdge(edge.get(1), edge.get(0));
		}
		return res;
	}
	
	public List<List<T>> StronglyConnectedComponents(){
		List<List<T>> res = new LinkedList<>();
		Graph<T> transposed = this.transpose();
		List<T> picked = new LinkedList<T>();
		for(T origin : this.vertices()) {
			if(!picked.contains(origin)) {
				List<T> CC1 = this.reach(origin);
				List<T> CC2 = transposed.reach(origin);
				CC1.retainAll(CC2); //intersection
				picked.removeAll(CC1);
				res.add(CC1);
			}
		}
		return res;
	}

}