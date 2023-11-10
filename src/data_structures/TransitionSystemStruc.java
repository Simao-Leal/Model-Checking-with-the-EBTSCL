package data_structures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TransitionSystemStruc<S, A, L> {
	/*
	 * S - type of the states
	 * A - type of the actions
	 * L - type of the label
	 * 
	 * A transition system is a weighted (each weight is an action) directed graph
	 * the set of propositional symbols are formulas of the type P
	 * Thus the label of each state is a Set<P>
	 */
	private WeightedGraph<S, A> graph;
	private List<S> initialStates; //initial states, we keep the same references as the graph
	private Map<S, L> labelingFunction;
	
	protected WeightedGraph<S, A> getGraph() {
		return graph;
	}

	public TransitionSystemStruc() {
		graph = new WeightedGraph<>();
		initialStates = new LinkedList<>();
		labelingFunction = new HashMap<>();
	}
	
	public void addState(S state, L label) {
		graph.addVertex(state);
		labelingFunction.put(state, label);
	}
	
	public void addInitialState(S state, L label) {
		graph.addVertex(state);
		initialStates.add(state);
		labelingFunction.put(state, label);
	}
	
	public List<S> getInitialStates() {
		return initialStates;
	}
	
	public boolean initialQ(S state) {
		return initialStates.contains(state);
	}
	
	public L label(S state) {
		return labelingFunction.get(state);
	}

	public void addTransition(S source, S target, A action) {
		graph.addEdge(source, target, action);
	}
	
	public void addTransition(S source, S target) {
		// if an action is not specified, then any action might be used
		graph.addEdge(source, target, null);
	}
	
	public boolean transitionQ(S source, S target, A action) {
		//true iff one can transition from source to target with action
		if(graph.edgeQ(source, target)){
			A weight = graph.getWeight(source, target);
			if(weight == null || weight.equals(action)) {
				return true;
			}
		}
		return false;
	}
	
	public List<S> suc(S source, A action){
		List<S> res = new LinkedList<>();
		for(S target : graph.offspring(source)) {
			A weight = graph.getWeight(source, target);
			if(weight == null || weight.equals(action)) {
				res.add(target);
			}
		}
		return res;
	}
	
	public List<S> suc(S source){
		return graph.offspring(source);
	}
	
	public List<S> pred(S target, A action){
		Graph<S> transpose = graph.transpose();
		List<S> res = new LinkedList<>();
		for(S source : transpose.offspring(target)) {
			A weight = graph.getWeight(source, target);
			if(weight == null || weight.equals(action)) {
				res.add(target);
			}
		}
		return res;
	}
	
	public List<S> pred(S target){
		Graph<S> transpose = graph.transpose();
		return transpose.offspring(target);
	}
	
	@Override
	public String toString() {
		String res = "";
		for(S state : graph.vertices()) {
			res += (initialQ(state) ? "Initial " : "") + "State " + state + ":\n";
			res += label(state);
			res += "\n\n";
		}
		
		res += "Transitions:\n";
		for(List<S> edge : graph.edges()) {
			S source = edge.get(0);
			S target = edge.get(1);
			A weight = graph.getWeight(source, target);
			res += "" + (weight == null ? "" :"[" + weight + "] ") + edge.get(0) + " -> " + edge.get(1) + "\n";
		}
		return res;
	}
}
