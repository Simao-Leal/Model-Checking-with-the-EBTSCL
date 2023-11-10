package data_structures;

import java.util.HashMap;
import java.util.Map;

public class WeightedGraph<T, W> extends Graph<T> { //W is the type of the weights
	
	private Map<T, Map<T, W>> weights = new HashMap<>();
	
	@Override
	@Deprecated //might not be the most elegant solution
	public void addEdge(T vertex1, T vertex2) {
		throw new UnsupportedOperationException("Can't add an edge without a weight");
	}
	
	public void addEdge(T vertex1, T vertex2, W weight) {
		super.addEdge(vertex1, vertex2);
		
		if(!weights.containsKey(vertex1)){
			weights.put(vertex1, new HashMap<>());	
		}
		weights.get(vertex1).put(vertex2, weight);
	}
	
	public W getWeight(T vertex1, T vertex2) {
		return weights.get(vertex1).get(vertex2);
	}
}
