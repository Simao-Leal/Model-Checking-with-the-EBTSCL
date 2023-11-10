package data_structures;

import java.util.LinkedList;

public class UndirectedGraph<T> extends Graph<T> {

	@Override
	public void addEdge(T vertex1, T vertex2) {
		if(!lists.containsKey(vertex1)) {
			lists.put(vertex1, new LinkedList<T>());
		}
		if(!lists.containsKey(vertex2)) {
			lists.put(vertex2, new LinkedList<T>());
		}
		if (!lists.get(vertex1).contains(vertex2)) {
			lists.get(vertex1).add(vertex2);
		}
		if (!lists.get(vertex2).contains(vertex1)) {
			lists.get(vertex2).add(vertex1);
		}
	}

}
