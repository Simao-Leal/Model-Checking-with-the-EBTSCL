package model_checking;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import data_structures.BoolModelPair;
import data_structures.Graph;
import data_structures.TransitionSystemStruc;
import data_structures.UndirectedGraph;
import formulas.Formula;
import formulas.LTL.Always;
import formulas.LTL.Eventually;
import formulas.LTL.Until;
import formulas.atemporal.*;
import logicHandlers.LocalIntStruc;
import logicHandlers.Sat;
import logicHandlers.SetFormulas;
import logicHandlers.World;
import symbols.AgentSymbol;
import symbols.EventSymbol;
import symbols.PropositionalSymbol;
import symbols.TimeSymbol;

public class TransitionSystem extends TransitionSystemStruc<String, Void, LocalIntStruc>{
	/*
	 * A TransitionSystem for the EBTSL over a signature Sigma consists of
	 * - A time-stamp < and = orders, which may be partial.
	 * It always corresponds to a total order, but we might be given partial information.
	 * Partial information may or may not be enough to verify that a model satisfies a formula
	 * - A trust order for each of the propositional symbols in Sigma.
	 * This order may be partial and may correspond to a partial order.
	 * - A transition system with
	 * 	- No actions
	 * 	- States are irrelevant
	 * 	- Each state gets a LocalIntStruc as a label, that is, a pair (Event, Set of AgentClaims)
	 */
	
	private Graph<TimeSymbol> timeLessThanRelation = new Graph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The < relation represented as a graph
	
	private UndirectedGraph<TimeSymbol> timeEqualsRelation = new UndirectedGraph<>(); //Replace with time stamp chain? - No, way too much work and for nothing
	//The ≅ relation represented as a graph
	
	private Map<PropositionalSymbol, Graph<AgentSymbol>> trustRelations = new HashMap<>();
	//For each propositional symbol we get a graph representing the relation
	
	
	//getters for these last 3 for convenience reasons
	void setTimeLessThanRelation(Graph<TimeSymbol> timeLessThanRelation) {
		this.timeLessThanRelation = timeLessThanRelation;
	}


	void setTimeEqualsRelation(UndirectedGraph<TimeSymbol> timeEqualsRelation) {
		this.timeEqualsRelation = timeEqualsRelation;
	}


	void setTrustRelations(Map<PropositionalSymbol, Graph<AgentSymbol>> trustRelations) {
		this.trustRelations = trustRelations;
	}
	//end
	
	
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
		String s = "Transition System\n";
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
	
	public TransitionSystem product(TransitionSystem other) {
		TransitionSystem res = new TransitionSystem();
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
				//events in the labels must coincide
				EventSymbol event1 = this.label(state1).getEvent();
				EventSymbol event2 = other.label(state2).getEvent();
				if(event1 == null || event2 == null || event1.equals(event2)) {
					//in case one of the events is null, we pick the more restrictive event, this is handled in union
					if(this.initialQ(state1) && other.initialQ(state2)) {
						res.addInitialState("<" + state1 + ", " + state2 + ">", this.label(state1).union(other.label(state2))); 
					} else {
						res.addState("<" + state1 + ", " + state2 + ">", this.label(state1).union(other.label(state2)));
					}
				}
			}
		}
		//transitions
		for(List<String> edge1 : this.getGraph().edges()) {
			for(List<String> edge2 : other.getGraph().edges()) {
				String source = "<" + edge1.get(0) + ", " + edge2.get(0) + ">";
				String target = "<" + edge1.get(1) + ", " + edge2.get(1) + ">";
				// the source and target states might not exist
				if(res.getGraph().vertices().contains(source) && res.getGraph().vertices().contains(target)) {
					res.addTransition(source, target);
				}
			}
		}
		return res;
	}
	
	class Pair{
		String state;
		SetFormulas delta;
		public Pair(String state, SetFormulas delta) {
			this.state = state;
			this.delta = delta;
		}
		@Override
		public int hashCode() {
			return Objects.hash(delta, state);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			return Objects.equals(delta, other.delta) && Objects.equals(state, other.state);
		}
	}
	
	private boolean compatiblePathQ(List<Pair> path, int periodStart) {
		/*
		 * This function assumes that path is such that each delta of
		 * path is locally satisfiable by its respective state and that
		 * we can transition from path[-1] to path[periodStart]. It
		 * only checks that the liveness conditions are met. Liveness conditions
		 * are introduced by: Until, Eventually and Not Always. 
		 */
		//Checking for liveness conditions
		/*
		 * Important note:
		 * The liveness conditions are important because World(F phi) = {{phi}, {XF phi}}
		 * and so we can have a chain F phi -> F phi -> F phi -> ...
		 * which would be considered sat, but it would never satisfy phi. However, if such a
		 * chain exists, then F phi must be present in every delta of the period. That is,
		 * to extract the liveness conditions, it is enough to look at any delta in the period
		 * (in periodStart for example). The condition must be met at some point in the period.
		 */
		SetFormulas requirements = new SetFormulas();
		//adding requirements for period
		for(Until until : Sat.untilFormulas(path.get(periodStart).delta)) {
			requirements.add(until.getInner2());
		}
		for(Eventually eventually : Sat.eventuallyFormulas(path.get(periodStart).delta)) {
			requirements.add(eventually.getInner());
		}
		for(Always always : Sat.notAlwaysFormulas(path.get(periodStart).delta)) {
			requirements.add(always.getInner().clone().negate());
		}

		//removing requirements in period (including those added in prefix)
		SetFormulas toRemove = new SetFormulas();
		for(Formula beta : requirements) {
			for(int i = periodStart; i < path.size(); i++) {
				if(path.get(i).delta.contains(beta)) toRemove.add(beta);
			}
		}
		requirements.removeAll(toRemove);
		
		//IF requirements is empty then we've found a compatible path 
		return requirements.isEmpty();
	}
	
	private String printPath(List<Pair> path) {
		//for debug purposes
		String res = "";
		res += "[";
		for(Pair pair : path) {
			res += pair.state + ", ";
		}
		res += "]";
		return res;
	}
	
	
	public BoolModelPair<Path> pathSat(Formula phi){
		boolean DFS = false;
		System.out.println(this);
		/*
		 * Tries to find a path in the transition system that satisfies phi
		 * by following the tree-like structure of fig 3 (p.20) and the
		 * transition system at the same time in
		 * a BFS style algorithm. From a pair (delta_1, state_1) we can transition
		 * to (delta_2, state_2) if the label of state_2 locally satisfies delta_2
		 */
		
		// ============[ INITIALIZATION ]============
		Deque<List<Pair>> queue = new LinkedList<>(); //rename this to stack if you want DFS
		//each element of the stack is a path. This is done because we always need the whole path
		SetFormulas omega = new SetFormulas();
		omega.add(phi);
		
		//adding given relations
		//TIME: there is no need to apply the transitive (and etc.) properties,
		//because we will then complete the relation
		for(List<TimeSymbol> edge : timeLessThanRelation.edges()) {
			omega.add(new TimeLessThan(edge.get(0), edge.get(1)));
		}
		for(List<TimeSymbol> edge : timeEqualsRelation.edges()) {
			omega.add(new TimeEquals(edge.get(0), edge.get(1)));
		}
		
		//TRUST: we need to calculate the closure of the relation
		//and add not a1 <[p] a2 for every relation not present
		for(PropositionalSymbol p : trustRelations.keySet()) {
			Graph<AgentSymbol> relation = trustRelations.get(p);
			for(AgentSymbol origin : relation.vertices()) {
				List<AgentSymbol> reachable = relation.reach(origin);
				for(AgentSymbol target : Formula.sigma.getAgentSymbols()) {
					if(reachable.contains(target)) {
						omega.add(new LessTrustworthy(origin, p, target));
					} else {
						omega.add(new LessTrustworthy(origin, p, target).negate());
					}
				}
			}
		}

		
		//adding time chains for time completeness
		omega.add(World.timeChains(Formula.sigma.getTimeSymbols()));
		
		//initializing the queue
		for(SetFormulas delta : omega.star()) { //for each initial delta
			if(!Sat.locallyIncompatibleQ(delta)) { //if delta is satisfiable
				System.out.println(getInitialStates());
				for(String state : getInitialStates()) { //for each initial state
					if(label(state).locallySat(delta)){ //if the state satisfies delta
						//create a new path with (state, delta)
						List<Pair> p = new LinkedList<>();
						p.add(new Pair(state, delta));
						if(DFS) queue.addFirst(p); else queue.addLast(p); //replace by addFirst if you want DFS
					}
				}
			}
		}
		
		
		// ============[ SEARCH ]============
		boolean found = false;
		List<Pair> path = null;
		int periodStart = 0;
		while(!found && !queue.isEmpty()) {
			path = queue.removeFirst();
			Pair last = path.get(path.size() - 1);
			String lastState = last.state;
			SetFormulas lastDelta = last.delta;
			for(SetFormulas delta : Sat.nextStep(lastDelta).star()) { //for each next delta
				if(!Sat.locallyIncompatibleQ(delta)) { //if delta is satisfiable
					for(String state : this.suc(lastState)) { //for each next state
						if(label(state).locallySat(delta)){ // if state locally satisfies delta
							//then we found a possible transition
							//check if it forms a cycle
							Pair pair = new Pair(state, delta);
							if(path.contains(pair)) {
								//if so, check if the deltas in path form a compatible path
								periodStart = path.indexOf(pair);
								found = compatiblePathQ(path, periodStart);
							} else {
								//if it doesn't make a cycle we add (state, delta) to path and add path to the queue
								List<Pair> newPath = new LinkedList<>(path);
								newPath.add(new Pair(state, delta));
								if(DFS) queue.addFirst(newPath); else queue.addLast(newPath); //replace by addFirst if you want DFS
							} 
						}
					}
				}
			}
		}	
		//At this point, IF we found a satisfying path, it is stored in "path" and "periodStart"
		BoolModelPair<Path> output;
		if(found) {
			/*Building time and trust orders:
			 *They are fixed along time, but we might only infer them at the end
			 *So we pick the last delta in the path to infer the orders
			 */
			Path p = new Path(this);
			SetFormulas delta = path.get(path.size() - 1).delta;
			for(Formula psi : delta) {
				if(!psi.isNegation()) {
					if(psi instanceof LessTrustworthy) {
						LessTrustworthy lessTrustworthy = (LessTrustworthy) psi;
						p.addTrustRelation(lessTrustworthy.getAgent1(), lessTrustworthy.getProp(), lessTrustworthy.getAgent2());
					} else if(psi instanceof TimeLessThan) {
						TimeLessThan timeLessThan = (TimeLessThan) psi;
						p.addTimeLessThanRelation(timeLessThan.getTime1(), timeLessThan.getTime2());
					} else if(psi instanceof TimeEquals) {
						TimeEquals timeEquals = (TimeEquals) psi;
						p.addTimeEqualsRelation(timeEquals.getTime1(), timeEquals.getTime2());
					}
				}
			}
			
			//Building the prefix and period
			for(int i = 0; i < periodStart; i++) {
				p.addPrefixInstant(path.get(i).state);
			}
			for(int i = periodStart; i < path.size(); i++) {
				p.addPeriodInstant(path.get(i).state);
			}
			output = new BoolModelPair<Path>(true, p);
		} else {
			output = new BoolModelPair<Path>(false, null);
		}
		return output;	
	};
	
	public BoolModelPair<Path> sat(Formula phi) {
		/*
		 * Satisfaction in the transition system sense,
		 * i.e., T satisfies phi if every path in T satisfies
		 * phi. Similar to validity.
		 */
		BoolModelPair<Path> out = pathSat(phi.clone().negate());
		return new BoolModelPair<Path>(!out.bool, out.model);
	}
	
	public void displaySat(Formula phi) {
		BoolModelPair<Path> o = this.sat(phi);
		if(o.bool) {
			System.out.println("\u001b[32;1mSatisfies!\u001b[0m");
		} else {
			System.out.println("\u001b[31;1mDoes not satisfy!\u001b[0m");
			System.out.println("Counter example:");
			System.out.println(o.model);
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		//TESTING
		//Exercise 6.3
		TransitionSystem T1 = new TransitionSystem();
		LocalIntStruc s0 = new LocalIntStruc();
		s0.addClaim( (AgentClaim) Formula.parse("a1 : t1 . p1"));
		s0.addClaim( (AgentClaim) Formula.parse("a2 : t2 . p2"));
		T1.addInitialState("s0", s0);
		
		LocalIntStruc s1 = new LocalIntStruc();
		T1.addState("s1", s1);
		
		LocalIntStruc s = new LocalIntStruc();
		s.addClaim( (AgentClaim) Formula.parse("a1 : t1 . p1"));
		T1.addState("s2", s);
		T1.addState("s3", s);
		
		T1.addTransition("s0", "s0");
		T1.addTransition("s0", "s1");
		T1.addTransition("s0", "s2");
		
		T1.addTransition("s1", "s3");
		
		T1.addTransition("s2", "s1");
		
		T1.addTransition("s3", "s3");
		T1.addTransition("s3", "s2");
		
		Formula phi1 = Formula.parse("(GF(a1 : t1 . p1)) => (GF(a2 : t2 . p2))");
		Formula phi2 = Formula.parse("X((a1 : t1 . p1) and (X(a1 : t1 . p1)))");
		System.out.println(phi1.display());
		
		BoolModelPair<Path> out;
		out = T1.sat(phi1);
		System.out.println(out.bool);
		System.out.println(out.model);
		
		out = T1.sat(phi2);
		System.out.println(out.bool);
		System.out.println(out.model);
		
		//Exercise 6.4
		TransitionSystem T2 = new TransitionSystem();
		s0 = new LocalIntStruc();
		s0.addClaim( (AgentClaim) Formula.parse("a1 : t1 . p1"));
		T2.addInitialState("s0", s0);
		
		s1 = new LocalIntStruc();
		T2.addState("s1", s1);
		
		LocalIntStruc s2 = new LocalIntStruc();
		s2.addClaim( (AgentClaim) Formula.parse("a2 : t2 . p2"));
		T2.addState("s2", s2);
		
		T2.addTransition("s0", "s1");
		T2.addTransition("s1", "s0");
		T2.addTransition("s1", "s2");
		
		T2.addTransition("s2", "s2");
		
		Formula phi = Formula.parse("G( a1:t1.p1 => (F a2:t2.p2))");
		out = T2.sat(phi);
		System.out.println(out.bool);
		System.out.println(out.model);
		
		
		//EXAM NORMAL SEASON AND APPEAL SEASON, GROUP II
		TransitionSystem T3 = new TransitionSystem();
		s0 = new LocalIntStruc();
		s0.addClaim( (AgentClaim) Formula.parse("a1 : t1 . p1"));
		T3.addInitialState("s0", s0);
		
		s1 = new LocalIntStruc();
		s1.addClaim( (AgentClaim) Formula.parse("a2 : t2 . p2"));
		T3.addState("s1", s1);
		
		s2 = new LocalIntStruc();
		s2.addClaim( (AgentClaim) Formula.parse("a3 : t3 . p3"));
		T3.addState("s2", s2);
		
		T3.addTransition("s0", "s1");
		T3.addTransition("s1", "s0");
		T3.addTransition("s1", "s2");
		T3.addTransition("s2", "s1");
		T3.addTransition("s2", "s1");
		T3.addTransition("s2", "s2");
		T3.addTransition("s0", "s2");
		
		phi1 = Formula.parse("G((a1:t1.p1 or a2:t2.p2) => (F a3:t3.p3))");
		out = T3.sat(phi1);
		System.out.println(out.bool);
		System.out.println(out.model);
		
		phi2 = Formula.parse("G((a1:t1.p1 or a2:t2.p2) => (X a3:t3.p3))");
		out = T3.sat(phi1);
		System.out.println(out.bool);
		System.out.println(out.model);
	}

}
