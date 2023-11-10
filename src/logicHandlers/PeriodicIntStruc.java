package logicHandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data_structures.Graph;
import data_structures.UndirectedGraph;
import formulas.*;
import formulas.atemporal.*;
import symbols.*;
import visitors.GlobalSatVisitor;

public class PeriodicIntStruc {
	/*
	 * Represents a periodic linear time interpretation structure.
	 * I.e. has two lists of LocalIntStruc: prefix and period
	 * such that the ltl int struc is [prefix][period][period]...
	 */
	
	
	Graph<TimeSymbol> timeLessThanRelation = new Graph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The < relation represented as a graph
	
	UndirectedGraph<TimeSymbol> timeEqualsRelation = new UndirectedGraph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The ≅ relation represented as a graph
	
	Map<PropositionalSymbol, Graph<AgentSymbol>> trustRelations = new HashMap<>();
	//For each propositional symbol we get a graph representing the relation
	
	
	private List<LocalIntStruc> prefix = new ArrayList<>();
	private int prefixLength = 0;
	private List<LocalIntStruc> period = new ArrayList<>();
	private int periodLength = 0;
	
	public PeriodicIntStruc() {}
	
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

	public void addPrefixInstant(LocalIntStruc I) {
		prefix.add(I);
		prefixLength++;
	}
	
	public void addPeriodInstant(LocalIntStruc I) {
		period.add(I);
		periodLength++;
	}
	
	public LocalIntStruc getLIS(int instant) {
		if(instant < prefix.size()) {
			return prefix.get(instant);
		} else {
			return period.get(((instant - prefixLength) % periodLength));
		}
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
	
	public boolean sat(int k, Formula phi) {
		GlobalSatVisitor v = new GlobalSatVisitor(this, k);
		phi.accept(v);
		return v.sat;
	}
	
	public boolean sat(Formula phi) {
		return sat(0, phi);
	}
	
	

	@Override
	public String toString() {
		String s;
		s = "Periodic Temporal Interpretation Structure:";
		
		s += "\nTime Order:\n\t";
		List<TimeSymbol> times = new ArrayList<TimeSymbol>(Formula.sigma.getTimeSymbols());
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
			s += this.getLIS(instant);
			s += "\n--------------------------\n";
		}
		s += "\n<<<START OF CYCLE>>>\n\n--------------------------\n";
		for(; instant < prefixLength + periodLength; instant++) {
			s += "Instant: " + instant + "\n";
			s += this.getLIS(instant);
			s += "\n--------------------------\n";
		}
		s += "\n<<<END OF CYCLE>>>\n";
		return s;
	}


	public static void main(String[] args) {
//		AtemporalFormula phi1 = new TimeLessThan(new TimeSymbol("t1"), new TimeSymbol("t2"));
//		AtemporalFormula phi2 = new TimeLessThan(new TimeSymbol("t3"), new TimeSymbol("t4"));
//		PeriodicIntStruc I = new PeriodicIntStruc(Formula.sigma);
//		I.addTimeLessThanRelation(new TimeSymbol("t1"), new TimeSymbol("t2"));
//		I.addTimeEqualsRelation(new TimeSymbol("t3"), new TimeSymbol("t4"));
//		I.addTimeEqualsRelation(new TimeSymbol("t4"), new TimeSymbol("t5"));
//		I.addTrustRelation(new AgentSymbol("a1"), new PropositionalSymbol("p1"), new AgentSymbol("a2"));
//		I.addTrustRelation(new AgentSymbol("a3"), new PropositionalSymbol("p1"), new AgentSymbol("a4"));
//		I.addTrustRelation(new AgentSymbol("a1"), new PropositionalSymbol("p2"), new AgentSymbol("a2"));
//		I.addTrustRelation(new AgentSymbol("a3"), new PropositionalSymbol("p2"), new AgentSymbol("a4"));
//		
//		LocalIntStruc L1 = new LocalIntStruc(Formula.sigma);
//		L1.addNegativeClaim(new AgentSymbol("a1"), new PropositionalSymbol("p1"), new TimeSymbol("t1"));
//		L1.addNegativeClaim(new AgentSymbol("a2"), new PropositionalSymbol("p2"), new TimeSymbol("t2"));
//		L1.setEvent(new EventSymbol("e1"));
//		
//		LocalIntStruc L2 = new LocalIntStruc(Formula.sigma);
//		L2.addPositiveClaim(new AgentSymbol("a1"), new PropositionalSymbol("p1"), new TimeSymbol("t1"));
//		L2.addPositiveClaim(new AgentSymbol("a2"), new PropositionalSymbol("p2"), new TimeSymbol("t2"));
//		L2.setEvent(new EventSymbol("e2"));
//		
//		I.addPrefixInstant(L1);
//		I.addPeriodInstant(L2);
//		
//		System.out.println(I);
		Formula phi1 = Formula.parse("a1: t . p1");
		Formula phi2 = Formula.parse("a2: t . p2");
		PeriodicIntStruc I = new PeriodicIntStruc();
		I.addTrustRelation(new AgentSymbol("a1"), new PropositionalSymbol("p1"), new AgentSymbol("a2"));
		System.out.println(I.lessTrustworthy(new AgentSymbol("a1"), new PropositionalSymbol("p1"), new AgentSymbol("a2")));
		System.out.println(I.lessTrustworthy(new AgentSymbol("a1"), new PropositionalSymbol("p2"), new AgentSymbol("a2")));
	}
}