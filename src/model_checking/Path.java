package model_checking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data_structures.Graph;
import data_structures.UndirectedGraph;
import formulas.Formula;
import logicHandlers.LocalIntStruc;
import symbols.AgentSymbol;
import symbols.PropositionalSymbol;
import symbols.TimeSymbol;

public class Path {
	/*
	 * This is the type which we will use to provide a counter example
	 * when dealing with transition systems. A path consists of two lists (prefix and period)
	 * of states of a given transition system, along with a time and trust orders
	 * Note: the time and trust orders of the path and transition system are not necessarily equal,
	 * but are always coherent with one another. That is, the orders of the path are an extension of the orders
	 * of the transition system (obtained by transitivity for example). The time order of the path must be total.
	 * It is different from a periodic interpretation structure in the sense that it is bound to some transition
	 * system. That is, for each instant it not only contains a local interpretation structure but also the
	 * state in the transition system from which said local interpretation structure was extracted. Notice that
	 * two different states might have identical local interpretation structures.
	 */
	
	Graph<TimeSymbol> timeLessThanRelation = new Graph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The < relation represented as a graph
	
	UndirectedGraph<TimeSymbol> timeEqualsRelation = new UndirectedGraph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The ≅ relation represented as a graph
	
	Map<PropositionalSymbol, Graph<AgentSymbol>> trustRelations = new HashMap<>();
	//For each propositional symbol we get a graph representing the relation
	
	
	private List<String> prefix = new ArrayList<>();
	private int prefixLength = 0;
	private List<String> period = new ArrayList<>();
	private int periodLength = 0;
	
	TransitionSystem transitionSystem;
	
	public Path(TransitionSystem ts) {
		transitionSystem = ts;
	}
	
	public void addTimeLessThanRelation(TimeSymbol time1, TimeSymbol time2) {
		//establishes that time1 < time2
		timeLessThanRelation.addEdge(time1, time2);
	}
	
	public void addTimeEqualsRelation(TimeSymbol time1, TimeSymbol time2) {
		//establishes that time1 ≅ time2
		timeEqualsRelation.addEdge(time1, time2);
	}
	
	public void addTrustRelation(AgentSymbol agent1, PropositionalSymbol prop, AgentSymbol agent2) {
		//establishes that agent1 <=[propositionalSymbol] agent2
		if(!trustRelations.containsKey(prop)) trustRelations.put(prop, new Graph<AgentSymbol>());
		trustRelations.get(prop).addEdge(agent1, agent2);
	}
	
	
	public int getPrefixLength() {
		return prefixLength;
	}

	public int getPeriodLength() {
		return periodLength;
	}

	public void addPrefixInstant(String state) {
		prefix.add(state);
		prefixLength++;
	}
	
	public void addPeriodInstant(String state) {
		period.add(state);
		periodLength++;
	}
	
	public String getState(int instant) {
		if(instant < prefix.size()) {
			return prefix.get(instant);
		} else {
			return period.get(((instant - prefixLength) % periodLength));
		}
	}
	
	public LocalIntStruc getLIS(int instant) {
		return transitionSystem.label(getState(instant));
	}
	
	//relations as functions
	public boolean timeLessThan(TimeSymbol time1, TimeSymbol time2) {
		return timeLessThanRelation.edgeQ(time1, time2);
	}
	
	
	public boolean timeEquals(TimeSymbol time1, TimeSymbol time2) {
		return timeEqualsRelation.edgeQ(time1, time2);
	}
	
	public boolean lessTrustworthy(AgentSymbol agent1, PropositionalSymbol prop, AgentSymbol agent2) {
		return trustRelations.containsKey(prop) && trustRelations.get(prop).edgeQ(agent1, agent2); //lazy evaluation
	}
	
	@Override
	public String toString() {
		String s;
		s = "Path:";
		
		s += "\nTime Order:\n\t";
		List<TimeSymbol> times = new ArrayList<TimeSymbol>(Formula.sigma.getTimeSymbols()/*this is ok because the relation is total*/);
		if(!times.isEmpty()) {
			Collections.sort(times, new Comparator<TimeSymbol>() {
				public int compare(TimeSymbol time1, TimeSymbol time2) {
					if(timeLessThan(time1, time2)) {
						return -1;
					} else if(timeEquals(time1, time2)) {
						return 0;
					} else {
						return 1;
					}
				}
			});
			s += times.get(0);
			for(int i = 1; i < times.size(); i++) {
				s += (timeEquals(times.get(i - 1), times.get(i)) ? " ≅ " : " < ") + times.get(i);
			}
		}
		s += "\nTrust Relations:";
		for(PropositionalSymbol prop : trustRelations.keySet()) {
			s+="\n\t";
			Graph<AgentSymbol> graph = trustRelations.get(prop);
			for(List<AgentSymbol> edge : graph.edges()) {
				s+= edge.get(0) + " ⊴[" + prop + "] " + edge.get(1) + ", ";
			}
		}
		s += "\n--------------------------\n";
		int instant = 0;
		for(; instant < prefixLength; instant++) {
			s += "Instant: " + instant + "\n";
			s += "State: " + this.getState(instant) + "\n";
			s += this.getLIS(instant);
			s += "\n--------------------------\n";
		}
		s += "\n<<<START OF CYCLE>>>\n\n--------------------------\n";
		for(; instant < prefixLength + periodLength; instant++) {
			s += "Instant: " + instant + "\n";
			s += "State: " + this.getState(instant) + "\n";
			s += this.getLIS(instant);
			s += "\n--------------------------\n";
		}
		s += "\n<<<END OF CYCLE>>>\n";
		return s;
	}

}
