package logicHandlers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import data_structures.*;
import formulas.*;
import formulas.atemporal.*;
import formulas.propositional.*;
import symbols.*;
import visitors.WorldVisitor;

public class World {
	public static SetSetFormulas world(Formula phi) {
		WorldVisitor v = new WorldVisitor();
		phi.accept(v);
		return v.setSet;
	}

	
	private static boolean nextCombination(int p, int[] combination, SetFormulas[][] worlds) {
		if(combination[p] < worlds[p].length - 1) {
			combination[p]++;
			return true;
		} else {
			combination[p] = 0;
			if(p < worlds.length - 1) {
				return nextCombination(p + 1, combination, worlds);
			} else {
				return false;
			}
		}
	}
	
	public static SetSetFormulas world(SetFormulas omega) {
		int n = omega.size();
		SetFormulas [][] worlds = new SetFormulas[n][];
		Formula[] formulas = new Formula[n];
		int i = 0;
		for(Formula phi : omega) {
			formulas[i] = phi;
			SetSetFormulas world = world(phi);
			worlds[i] =  new SetFormulas[world.size()]; //SetFormulas[world.size()] is an array of SetFormulas
			int j = 0;
			for(SetFormulas om : world) {
				worlds[i][j] = om;
				j++;
			}
			i++;
		}
		
		SetSetFormulas setSet = new SetSetFormulas();
		int[] combination = new int[n];
		do {
			SetFormulas set = new SetFormulas();
			/*Important note:
			 * The documentation for set.add specifies that
			 * "If this set already contains the element, the call leaves the set unchanged"
			 * We consider expanded formulas to be equal to their non expanded counterparts
			 * but if we want them expanded in the set, we must insert them first
			 */
			for(i = 0; i < n; i++) {
				set.add(formulas[i].clone().expand());
			}
			for(i = 0; i < n; i++) {
				set.addAll(worlds[i][combination[i]]);
			}
			setSet.add(set);
		} while(nextCombination(0, combination, worlds));
		return setSet;
	}
	
	public static SetSetFormulas world(SetSetFormulas psi) {
		SetSetFormulas setSet = new SetSetFormulas();
		for(SetFormulas set : psi) {
			setSet.addAll(world(set));
		}
		return setSet;
	}
	
	public static SetSetFormulas worldClosure(Formula phi) {
		return worldClosure(world(phi));
	}
	
	public static SetSetFormulas worldClosure(SetFormulas omega) {
		return worldClosure(world(omega));
	}
	
	public static SetSetFormulas worldClosure(SetSetFormulas psi) {
		SetSetFormulas before;
		SetSetFormulas after = psi;
		do {
			before = after;
			after = world(after);
		}while(!before.equals(after));
		return after;
	}
	
	public static Formula tau(SetFormulas omega) {
		Set<EventSymbol> evs = Formula.sigma.getEventSymbols();
		if(evs.isEmpty()) return new True();
		
		List<Formula> events = new LinkedList<>();
		for(EventSymbol ev : evs) {
			events.add(new Event(ev));
		}
		return Formula.bigOr(events);
	}
	
	private static List<List<TimeSymbol>> deepCopy(List<List<TimeSymbol>> partition){
		//probably not the best way to do this
		List<List<TimeSymbol>> res = new LinkedList<>();
		for(List<TimeSymbol> set : partition) {
			List<TimeSymbol> newSet = new LinkedList<>(set);
			res.add(newSet);
		}
		return res;
	}
	
	private static List<List<List<TimeSymbol>>> generatePartitions(List<TimeSymbol> timeList){
		/*
		 * Returns all the ordered partitions of the set timeList
		 * The order of the sets of the partition is relevant but
		 * the order within each set is not (i.e. are considered the same)
		 */
		if(timeList.size() == 1) {
			List<TimeSymbol> L1 = new LinkedList<>();
			L1.add(timeList.get(0));
			List<List<TimeSymbol>> L2 = new LinkedList<>();
			L2.add(L1);
			List<List<List<TimeSymbol>>> L3 = new LinkedList<>();
			L3.add(L2);
			return L3;
		} else {
			List<List<List<TimeSymbol>>> res = new LinkedList<>();
			List<TimeSymbol> previous = new LinkedList<>(timeList);
			TimeSymbol addNow = previous.remove(previous.size() - 1);
			for(List<List<TimeSymbol>> partition : generatePartitions(previous)) { //recursion!
				
				//adding addNow to each partion not changing the number of sets
				for(int i = 0 ; i < partition.size(); i++) {
					List<List<TimeSymbol>> newPartition = deepCopy(partition);
					newPartition.get(i).add(addNow);
					res.add(newPartition);
				}
						
				//adding another set to the partition (it must be inserted in every index possible)
				for(int index = 0 ; index < partition.size() + 1; index++) {
					List<List<TimeSymbol>> newPartition = deepCopy(partition);
					List<TimeSymbol> newSet = new LinkedList<>();
					newSet.add(addNow);
					newPartition.add(index, newSet);
					res.add(newPartition);
				}
			}
			return res;
		}
	}
	
	public static Formula timeChains(Set<TimeSymbol> timeSet) {
		/* A time chain is a formula of the kind 
		 * t1 < t2 = t3 = t4 < t5 = t6 < t7
		 * The output of this function is a formula: the disjunction of
		 * all possible time chains.
		 */
		if(timeSet.isEmpty()) return new True();
			
		List<List<List<TimeSymbol>>> partitions = generatePartitions(new LinkedList<>(timeSet));
		List<Formula> chains = new LinkedList<>();
		for(List<List<TimeSymbol>> partition : partitions) {
			List<Formula> clauses = new LinkedList<>();
			TimeSymbol t1 = partition.get(0).get(0);
			TimeSymbol t2;
			for(int i = 0; i < partition.size(); i++) {
				for(int j = (i == 0 ? 1 : 0); j < partition.get(i).size(); j++) {
					t2 = partition.get(i).get(j);
					if(j == 0) {
						clauses.add(new TimeLessThan(t1, t2));
					} else {
						clauses.add(new TimeEquals(t1, t2));
					}
					t1 = t2;
				}
			}
			chains.add(Formula.bigAnd(clauses));
		}
		return Formula.bigOr(chains);
	}
	
	private static SetFormulas nonCongruentClosure(SetFormulas omega, 
			Graph<TimeSymbol> gTimelessThan, UndirectedGraph<TimeSymbol> gTimeEquals,
			Map<PropositionalSymbol, Graph<AgentSymbol>> gLessTrustworthyMap) {
		
		SetFormulas result = new SetFormulas();
		result.addAll(omega);
		
		//TIME LESS THAN
		//TRANSITIVE
		for(TimeSymbol origin : gTimelessThan.vertices()) {
			List<TimeSymbol> reachable = gTimelessThan.strictReach(origin);
			for(TimeSymbol target : reachable) {
				result.add(new TimeLessThan(origin, target));
			}
		}
		
		
		//TIME EQUALS
		for(TimeSymbol origin : gTimeEquals.vertices()) {
			List<TimeSymbol> reachable = gTimeEquals.reach(origin);
			for(TimeSymbol target : reachable) {
				//TRANSITIVE, REFLEXIVE & SYMMETRIC
				result.add(new TimeEquals(origin, target));
			}
		}
		
		//LESS TRUSTWORTHY
		for(PropositionalSymbol prop : gLessTrustworthyMap.keySet()) {
			Graph<AgentSymbol> gLessTrustworthy = gLessTrustworthyMap.get(prop);
			//TRANSITIVE & REFLEXIVE
			for(AgentSymbol origin : gLessTrustworthy.vertices()) {
				List<AgentSymbol> reachable = gLessTrustworthy.reach(origin);
				for(AgentSymbol target : reachable) {
					result.add(new LessTrustworthy(origin, prop, target));
				}
			}
			
		}
		return result;
	}
	
	private static SetFormulas CongruentClosure(SetFormulas omega, 
			UndirectedGraph<TimeSymbol> gTimeEquals,
			Map<PropositionalSymbol, List<List<AgentSymbol>>> SCCsMap) {
		
		SetFormulas result = new SetFormulas();
		result.addAll(omega);
		
		//TIME EQUALS
		for(TimeSymbol origin : gTimeEquals.vertices()) {
			List<TimeSymbol> reachable = gTimeEquals.reach(origin);
			for(TimeSymbol target : reachable) {
				//CONGRUENT
				for(Formula phi : omega) {
					result.add(phi.replace(origin, target));
				}
			}
		}
		
		//LESS TRUSTWORTHY
		for(PropositionalSymbol prop : SCCsMap.keySet()) {
			List<List<AgentSymbol>> SCCs = SCCsMap.get(prop);
			//CONGRUENT
			for(List<AgentSymbol> SCC : SCCs) {
				for(AgentSymbol agent1 : SCC) {
					for(AgentSymbol agent2 : SCC) {
						if(!agent1.equals(agent2)) {
							for(Formula phi : omega) {
								result.add(phi.replace(prop, agent1, agent2));
							}
						}
					}
				}
			}
		}
		return result;	
	}
	
	public static SetFormulas operatorClosure(SetFormulas omega) {
		//Building relation graphs
		Graph<TimeSymbol> gTimelessThan = new Graph<TimeSymbol>();
		UndirectedGraph<TimeSymbol> gTimeEquals = new UndirectedGraph<TimeSymbol>();
		Map<PropositionalSymbol, Graph<AgentSymbol>> gLessTrustworthyMap = new HashMap<>();
		Map<PropositionalSymbol, List<List<AgentSymbol>>> SCCsMap = new HashMap<>();
		for(Formula phi : omega) {
			if(!phi.isNegation()) {
				if(phi instanceof TimeLessThan) {
					TimeLessThan form = (TimeLessThan) phi;
					gTimelessThan.addEdge(form.getTime1(), form.getTime2());
				}
				else if(phi instanceof TimeEquals) {
					TimeEquals form = (TimeEquals) phi;
					gTimeEquals.addEdge(form.getTime1(), form.getTime2());
				}
				else if(phi instanceof LessTrustworthy) {
					LessTrustworthy form = (LessTrustworthy) phi;
					PropositionalSymbol prop = form.getProp();
					if(!gLessTrustworthyMap.containsKey(prop)) gLessTrustworthyMap.put(prop, new Graph<>());
					gLessTrustworthyMap.get(form.getProp()).addEdge(form.getAgent1(), form.getAgent2());
				}
			}
		}
		for(PropositionalSymbol prop : gLessTrustworthyMap.keySet()) {
			Graph<AgentSymbol> gLessTrustworthy = gLessTrustworthyMap.get(prop);
			SCCsMap.put(prop, gLessTrustworthy.StronglyConnectedComponents());
		}
		
		SetFormulas before;
		SetFormulas after = nonCongruentClosure(omega, gTimelessThan, gTimeEquals, gLessTrustworthyMap);
		
		do {
			before = after;
			after = CongruentClosure(after,gTimeEquals,SCCsMap);
		}while(!before.equals(after));
		return after;
	}
	
	public static void main(String[] args) {
		Formula[] forms = new Formula[6];
		forms[0] = Formula.parse("t1 < t2");
		forms[1] = Formula.parse("not t1 < t2");
		forms[2] = Formula.parse("t1 = t2");
		forms[3] = Formula.parse("not t1 = t2");
		forms[4] = Formula.parse("a1 <[p] a2");
		forms[5] = Formula.parse("not a1 <[p] a2");
		
		
		for(Formula phi : forms) {
			System.out.println(phi);
			System.out.println(world(phi));
		}
		
		System.out.println(timeChains(Formula.sigma.getTimeSymbols()));
	}
}
