package model_checking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.List;

import data_structures.BoolModelPair;
import data_structures.Graph;
import data_structures.TransitionSystemStruc;
import data_structures.UndirectedGraph;
import formulas.*;
import formulas.atemporal.*;
import logicHandlers.LocalIntStruc;
import symbols.AgentSymbol;
import symbols.EventSymbol;
import symbols.PropositionalSymbol;
import symbols.TimeSymbol;

public class EventTransitionSystem extends TransitionSystemStruc<String, EventSymbol, LocalIntStruc>{
	/*
	 * An EventTransitionSystem for the EBTSL over a signature Sigma consists of
	 * - A time-stamp < and = orders, which may be partial.
	 * It always corresponds to a total order, but we might be given partial information.
	 * Partial information may or may not be enough to verify that a model satisfies a formula
	 * - A trust order for each of the propositional symbols in Sigma.
	 * This order may be partial and may correspond to a partial order.
	 * - A transition system with
	 * 	- An event for each transition (if no event (null) is specified, we assume we can use that transition
	 * 	   using any event.
	 * 	- States are irrelevant
	 * 	- Each state gets a LocalIntStruc as a label. It should be just a set of agent claims, but for convenience
	 *    we just use a LocalIntStruc and ignore the event.
	 * 
	 * An EventTransitionSystem is then converted into a TransitionSystem (with the events in the states)
	 * by using an algorithm akin to the one which converts from a NFA to a DFA.
	 * 
	 * Note: Any initial state in an EventTransitionSystem will then get labeled with the special event "START".
	 */
	
	
	private Graph<TimeSymbol> timeLessThanRelation = new Graph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The < relation represented as a graph
	
	private UndirectedGraph<TimeSymbol> timeEqualsRelation = new UndirectedGraph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The ≅ relation represented as a graph
	
	private Map<PropositionalSymbol, Graph<AgentSymbol>> trustRelations = new HashMap<>();
	//For each propositional symbol we get a graph representing the relation
	
	
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
		String s = "Event Transition System\n";
		s += "\nTime Order:\n\t";
		for(List<TimeSymbol> edge : timeEqualsRelation.edges()) {
			s+= edge.get(0) + " ≅ " + edge.get(1) + ", ";
		}
		s += "\n\t";
		for(List<TimeSymbol> edge : timeLessThanRelation.edges()) {
			s+= edge.get(0) + " < " + edge.get(1) + ", ";
		}
		s += "\n\n";
		s += "\nTrust Relations:";
		for(PropositionalSymbol prop : trustRelations.keySet()) {
			s+="\n\t";
			Graph<AgentSymbol> graph = trustRelations.get(prop);
			for(List<AgentSymbol> edge : graph.edges()) {
				s+= edge.get(0) + " ⊴[" + prop + "] " + edge.get(1) + ", ";
			}
		}
		s += "\n\n";
		s += super.toString();
		return s;
	}
	
	public TransitionSystem convert() {
		TransitionSystem res = new TransitionSystem();
		//TODO this should probably be deep copied, but it might be overkill
		res.setTimeEqualsRelation(timeEqualsRelation);
		res.setTimeLessThanRelation(timeLessThanRelation);
		res.setTrustRelations(trustRelations);
		
		Map<String, List<EventSymbol>> newStates = new HashMap<>();
		
		//adding initial states with event START
		for(String state : this.getGraph().vertices()) {
			newStates.put(state, new LinkedList<>());
			if(initialQ(state)) { 
				newStates.get(state).add(new EventSymbol("START")); //START is a special event that denotes an initial state
			}
		}
		//making the rest of the states
		for(List<String> edge : this.getGraph().edges()) {
			String source = edge.get(0);
			String target = edge.get(1);
			newStates.get(target).add(this.getGraph().getWeight(source, target));
		}
		
		for(String oldState : newStates.keySet()) {
			for(EventSymbol newStateEvent : newStates.get(oldState)) {
				String newStateId = "<" + oldState + ", " + newStateEvent + ">";
				LocalIntStruc newStateIntStruc = new LocalIntStruc();
				newStateIntStruc.setEvent(newStateEvent);
				for(AgentClaim claim : this.label(oldState).getClaims()) {
					newStateIntStruc.addClaim(claim);
				}
				if(newStateEvent != null && newStateEvent.equals(new EventSymbol("START"))){
					res.addInitialState(newStateId, newStateIntStruc);
				} else {
					res.addState(newStateId, newStateIntStruc);
				}
			}
			
		}
		//transitions
		for(List<String> edge : this.getGraph().edges()) {
			String source = edge.get(0);
			String target = edge.get(1);
			for(EventSymbol event : newStates.get(source)) {
				String newSource = "<" + source + ", " + event + ">";
				String newTarget = "<" + target + ", " + this.getGraph().getWeight(source, target) + ">";
				res.addTransition(newSource, newTarget);
			}
		}
		return res;
	}
	
	public EventTransitionSystem product(EventTransitionSystem other) {
		EventTransitionSystem res = new EventTransitionSystem();
		//add relations
		for(List<TimeSymbol> edge : this.timeLessThanRelation.edges()) {
			res.addTimeLessThanRelation(edge.get(0), edge.get(1));
		}
		for(List<TimeSymbol> edge : other.timeLessThanRelation.edges()) {
			res.addTimeLessThanRelation(edge.get(0), edge.get(1));
		}
		for(List<TimeSymbol> edge : this.timeEqualsRelation.edges()) {
			res.addTimeEqualsRelation(edge.get(0), edge.get(1));
		}
		for(List<TimeSymbol> edge : other.timeEqualsRelation.edges()) {
			res.addTimeEqualsRelation(edge.get(0), edge.get(1));
		}
		for(PropositionalSymbol prop : this.trustRelations.keySet()) {
			for(List<AgentSymbol> edge : this.trustRelations.get(prop).edges()) {
				res.addTrustRelation(edge.get(0), prop, edge.get(1));
			}
		}
		for(PropositionalSymbol prop : other.trustRelations.keySet()) {
			for(List<AgentSymbol> edge : other.trustRelations.get(prop).edges()) {
				res.addTrustRelation(edge.get(0), prop, edge.get(1));
			}
		}
		
		//make states
		for(String state1 : this.getGraph().vertices()) {
			for(String state2 : other.getGraph().vertices()) {
				if(this.initialQ(state1) && other.initialQ(state2)) {
					res.addInitialState("<" + state1 + ", " + state2 + ">", this.label(state1).union(other.label(state2))); 
				} else {
					res.addState("<" + state1 + ", " + state2 + ">", this.label(state1).union(other.label(state2)));
				}
			}
		}
		//transitions
		for(List<String> edge1 : this.getGraph().edges()) {
			String source1 = edge1.get(0);
			String target1 = edge1.get(1);
			for(List<String> edge2 : other.getGraph().edges()) {
				String source2 = edge2.get(0);
				String target2 = edge2.get(1);
				//the events must coincide or one of them must be null
				EventSymbol event1 = this.getGraph().getWeight(source1, target1);
				EventSymbol event2 = other.getGraph().getWeight(source2, target2);
				if(event1 == null || event2 == null) {
					//in case one of the events is null, we pick the more restrictive case
					res.addTransition("<" + source1 + ", " + source2 + ">", 
							          "<" + target1 + ", " + target2 + ">",
							          (event1 == null ? event2 : event1));
				} else if(event1.equals(event2)) {
					res.addTransition("<" + source1 + ", " + source2 + ">", 
					          "<" + target1 + ", " + target2 + ">",
					          event1);
				}
			}
		}
		return res;
	}
	
	public BoolModelPair<Path> sat(Formula phi) {
		/*
		 * Satisfaction in the transition system sense,
		 * i.e., T satisfies phi if every path in T satisfies
		 * phi. Similar to validity.
		 * The EventTransitionSystem gets converted into a
		 * regular transition system
		 */
		TransitionSystem convert = this.convert();
		return convert.sat(phi);
	}
	
	public void displaySat(Formula phi) {
		TransitionSystem convert = this.convert();
		convert.displaySat(phi);
	}
	
	public static void main(String[] args) {
		/*    Begin Event Transition System    */
		EventTransitionSystem T = new EventTransitionSystem();
		/*    Begin State    */
		{
			LocalIntStruc s0 = new LocalIntStruc();
			T.addInitialState("s0", s0);
		}
		/*    End State    */
		/*    Begin State    */
		{
			LocalIntStruc s1 = new LocalIntStruc();
			T.addState("s1", s1);
		}
		/*    End State    */
		/*    Begin State    */
		{
			LocalIntStruc s2 = new LocalIntStruc();
			T.addState("s2", s2);
		}
		/*    End State    */
		/*    Begin State    */
		{
			LocalIntStruc s3 = new LocalIntStruc();
			T.addState("s3", s3);
		}
		/*    End State    */
		/*    Begin State    */
		{
			LocalIntStruc s4 = new LocalIntStruc();
			T.addState("s4", s4);
		}
		/*    End State    */
		/*    Begin State    */
		{
			LocalIntStruc s5 = new LocalIntStruc();
			T.addState("s5", s5);
		}
		/*    End State    */
		T.addTransition("s0", "s1", null);
		T.addTransition("s0", "s2", null);
		T.addTransition("s1", "s3", new EventSymbol("a"));
		T.addTransition("s2", "s3", new EventSymbol("b"));
		T.addTransition("s3", "s4", null);
		T.addTransition("s3", "s5", null);
	/*    End Event Transition System    */

		TransitionSystem tconvert = T.convert();
		
		System.out.println(tconvert);
	}
}
