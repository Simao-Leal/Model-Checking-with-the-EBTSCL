package logicHandlers;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import data_structures.BoolModelPair;
import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;
import symbols.*;

public class Sat {
	
	public static LocalIntStruc localIntStrucBuilder(SetFormulas delta){
		//Builds an AtemporalIntStruc that satisfies delta, using the "monolith formulas" 
		//of delta. It shall produce a valid IntStruc provided that delta is locally compatible
		LocalIntStruc I = new LocalIntStruc();
		
		for(Formula phi : delta) {
			if(!phi.isNegation()) {
				if(phi instanceof AgentClaim) {
					AgentClaim agentClaim = (AgentClaim) phi;
					I.addClaim(agentClaim);
				} else if (phi instanceof Event) {
					Event event = (Event) phi;
					I.setEvent(event.getSymbol());
				}
			}
		}
		return I;
	}
	
	public static boolean locallyIncompatibleQ(SetFormulas delta) {
		boolean found = false;
		int eventsFound = 0;
		for(Iterator<Formula> it = delta.iterator(); it.hasNext() && !found;) {
			Formula phi1 = it.next();
			if(!phi1.isNegation()) {
				if(phi1 instanceof AgentClaim) {
					Formula phi2 = new AgentClaim(((AgentClaim)phi1).getAgent(), ((AgentClaim)phi1).getClaim().clone().invert());
					found = delta.contains(phi2);
				} else if(phi1 instanceof TimeLessThan) {
					if(   ((TimeLessThan)phi1).getTime1().equals(  ((TimeLessThan)phi1).getTime2()  )   ){
						found = true;
					} else {
						Formula phi2 = phi1.clone().negate();
						found = delta.contains(phi2);
					}
				} else if(phi1 instanceof TimeEquals || phi1 instanceof LessTrustworthy) {
					Formula phi2 = phi1.clone().negate();
					found = delta.contains(phi2);
				} else if(phi1 instanceof Event) {
					eventsFound++;
					if(eventsFound > 1) found = true;
				} else if(phi1 instanceof False) {
					found = true;
				}
				if( delta.contains(phi1.clone().negate()) ) {
					found = true;
				}
			}
		}
		return found;
	}
	
	public static BoolModelPair<LocalIntStruc> locallySat(Formula phi) {
		SetFormulas omega = new SetFormulas();
		omega.add(phi);
		boolean found = false;
		SetFormulas delta = null;
		for(Iterator<SetFormulas> it = omega.star().iterator(); it.hasNext() && !found;) {
			delta = it.next();
			found = !locallyIncompatibleQ(delta);	
		}

		if(found) {
			return new BoolModelPair<LocalIntStruc>(true, localIntStrucBuilder(delta));
		} else {
			return new BoolModelPair<LocalIntStruc>(false, null);
		}
	}
	
	public static BoolModelPair<LocalIntStruc> locallyValid(Formula phi) {
		BoolModelPair<LocalIntStruc> out = locallySat(phi.clone().negate());
		return new BoolModelPair<LocalIntStruc>(!out.bool, out.model);
	}
	
	public static SetFormulas nextStep(SetFormulas delta) {
		//Corresponds to the X^-1 function of p.18
		SetFormulas res = new SetFormulas();
		for(Formula phi : delta) {
			if(!phi.isNegation() && phi instanceof Next) {
				res.add( ((Next) phi).getInner().clone() );
			}
		}
		return res;
	}
	
	public static  List<Until> untilFormulas(SetFormulas delta) {
		//returns a list of all the Until formulas in delta
		List<Until> res = new LinkedList<>();
		for(Formula phi : delta) {
			if(phi instanceof Until && !phi.isNegation()) {
				res.add((Until) phi);
			}
		}
		return res;
	}
	
	public static List<Eventually> eventuallyFormulas(SetFormulas delta) {
		//returns a list of all the Eventually formulas in delta
		List<Eventually> res = new LinkedList<>();
		for(Formula phi : delta) {
			if(phi instanceof Eventually && !phi.isNegation()) {
				res.add((Eventually) phi);
			}
		}
		return res;
	}
	
	public static List<Always> notAlwaysFormulas(SetFormulas delta) {
		//returns a list of all the not Always formulas in delta
		List<Always> res = new LinkedList<>();
		for(Formula phi : delta) {
			if(phi instanceof Always && phi.isNegation()) {
				res.add((Always) phi);
			}
		}
		return res;
	}
	
	public static BoolModelPair<PeriodicIntStruc> sat(Formula phi) {
		/*
		 * Tries to find a periodic int struc that satisfies phi
		 * by following the tree-like structure of fig 3 (p.20) in
		 * a BFS style algorithm until we find a cycle of compatible deltas.
		 * From these we extract a periodic int struc that satisfies phi.
		 */
		Deque<List<SetFormulas>> queue = new LinkedList<>(); //rename this to stack if you want DFS
		//each element of the stack is a path. This is done because we always need the whole path
		SetFormulas omega = new SetFormulas();
		omega.add(phi);
		omega.add(World.timeChains(Formula.sigma.getTimeSymbols()));
		
		boolean found = false;
		for(SetFormulas delta : omega.star()) {
			if(!locallyIncompatibleQ(delta)) {
				List<SetFormulas> l = new LinkedList<SetFormulas>();
				l.add(delta);
				queue.addLast(l); //replace by addFirst if you want DFS
			}
		}
		
		List<SetFormulas> path = null;
		int periodStart = 0;
		while(!found && !queue.isEmpty()) {
			path = queue.removeFirst();
			SetFormulas last = path.get(path.size() - 1);
			for(SetFormulas delta : nextStep(last).star()) {
				if(!locallyIncompatibleQ(delta)) {
					if(path.contains(delta)) {
						//found potential cycle
						periodStart = path.indexOf(delta);
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
						for(Until until : untilFormulas(path.get(periodStart))) {
							requirements.add(until.getInner2());
						}
						for(Eventually eventually : eventuallyFormulas(path.get(periodStart))) {
							requirements.add(eventually.getInner());
						}
						for(Always always : notAlwaysFormulas(path.get(periodStart))) {
							requirements.add(always.getInner().clone().negate());
						}

						//removing requirements in period (including those added in prefix)
						SetFormulas toRemove = new SetFormulas();
						for(Formula beta : requirements) {
							for(int i = periodStart; i < path.size(); i++) {
								if(path.get(i).contains(beta)) toRemove.add(beta);
							}
						}
						requirements.removeAll(toRemove);
						
						//IF requirements is empty then we've found a satisfying path 
						found = requirements.isEmpty();
					} else {
						//we only keep expanding the path if the next set is not already in the path
						List<SetFormulas> l = new LinkedList<>(path);
						l.add(delta);
						queue.addLast(l); //replace by addFirst if you want DFS
					}
				}
			}
		}
		//At this point, IF we found a satisfying path, it is stored in "path" and "periodStart"
		BoolModelPair<PeriodicIntStruc> o;
		if(found) {
			o = new BoolModelPair<PeriodicIntStruc>(true, new PeriodicIntStruc());
			/*Building time and trust orders:
			 *They are fixed along time, but we might only infer them at the end
			 *So we pick the last delta in the path to infer the orders
			 */
			SetFormulas delta = path.get(path.size() - 1);
			for(Formula psi : delta) {
				if(!psi.isNegation()) {
					if(psi instanceof LessTrustworthy) {
						LessTrustworthy lessTrustworthy = (LessTrustworthy) psi;
						o.model.addTrustRelation(lessTrustworthy.getAgent1(), lessTrustworthy.getProp(), lessTrustworthy.getAgent2());
					} else if(psi instanceof TimeLessThan) {
						TimeLessThan timeLessThan = (TimeLessThan) psi;
						o.model.addTimeLessThanRelation(timeLessThan.getTime1(), timeLessThan.getTime2());
					} else if(psi instanceof TimeEquals) {
						TimeEquals timeEquals = (TimeEquals) psi;
						o.model.addTimeEqualsRelation(timeEquals.getTime1(), timeEquals.getTime2());
					}
				}
			}
			
			//Building the prefix and period
			for(int i = 0; i < periodStart; i++) {
				LocalIntStruc I = localIntStrucBuilder(path.get(i));
				o.model.addPrefixInstant(I);
			}
			for(int i = periodStart; i < path.size(); i++) {
				LocalIntStruc I = localIntStrucBuilder(path.get(i));
				o.model.addPeriodInstant(I);
			}
		} else {
			o = new BoolModelPair<PeriodicIntStruc>(false, null);
		}
		return o;		
	}
	
	public static void displaySat(Formula phi) {
		System.out.println("Checking whether " + phi + " is satisfiable:");
		BoolModelPair<PeriodicIntStruc> o = sat(phi);
		if(!o.bool) {
			System.out.println("\u001b[31;1mNot Satisfiable!\u001b[0m");
		} else {
			System.out.println("\u001b[32;1mSatisfiable!\u001b[0m");
			System.out.println("Witness:");
			System.out.println(o.model);
		}
		System.out.println();
	}
	
	public static BoolModelPair<PeriodicIntStruc> valid(Formula phi) {
		BoolModelPair<PeriodicIntStruc> out = sat(phi.clone().negate());
		return new BoolModelPair<PeriodicIntStruc>(!out.bool, out.model);
	}
	
	public static void displayVal(Formula phi) {
		System.out.println("Checking whether " + phi + " is valid:");
		BoolModelPair<PeriodicIntStruc> o = valid(phi);
		if(o.bool) {
			System.out.println("\u001b[32;1mValid!\u001b[0m");
		} else {
			System.out.println("\u001b[31;1mNot Valid!\u001b[0m");
			System.out.println("Counter example:");
			System.out.println(o.model);
		}
		System.out.println();
	}


	public static void main(String[] args) {
		Formula p = new AgentClaim(new AgentSymbol("a1"), new Claim(new TimeSymbol("t1"), new PropositionalSymbol("p1")));
		Formula q = new AgentClaim(new AgentSymbol("a2"), new Claim(new TimeSymbol("t2"), new PropositionalSymbol("p2")));
		
		
		Formula[] forms = new Formula[1];
		
		//LVM Module02 p.4
//		forms[0] = new Equivalent(new Next(p.clone().negate()), new Next(p).negate() ); //valid
//		forms[1] = new Implies(new Eventually(new And(p, q)),new And(new Eventually(p), new Eventually(q)));//valid
//		forms[2] = new Equivalent(new Eventually(new Or(p, q)),new Or(new Eventually(p), new Eventually(q)));//valid
//		forms[3] = new Equivalent(new Until(p, q),new Or(q,new And(p, new Next(new Until(p,q)))));//valid
//		forms[4] = new Implies(new And(new Eventually(p), new Eventually(q)),new Eventually(new And(p, q)));//not valid
//				
//		//LVM exercises02 p.1
//		forms[5] = new Implies(new Next(new Always(p)), new Always(new Next(p))); //valid
//		forms[6] = new Implies(new Next(new Eventually(p)), new Eventually(new Next(p))); //valid
//		forms[7] = new Implies(new Always(p), new Eventually(p)); //valid
//		forms[8] = new Implies(new Eventually(p), new Always(p)); //not valid
//		forms[9] = new Always(new Implies(new Next(new Next(p)), new Next(p))); //not valid
//		forms[10] = new Implies(new Eventually(p), new Always(new Eventually(p))); //not valid
//		forms[11] = new Implies(new Always(p), new Eventually(new Eventually(p))); //valid
//		forms[12] = new Implies(new Always(new And(p, q)), new And(new Always(p), new Always(q)) ); //valid //this is the one from the paper
//		forms[13] = new Implies(new Always(new Or(p, q)), new Or(new Always(p), new Always(q)) ); //not valid
//		forms[14] = new Implies(new And(new Always(p), new Always(q)), new Always(new And(p, q)) ); //valid
//		forms[15] = new Implies(new Or(new Always(p), new Always(q)), new Always(new Or(p, q))); // valid
//		forms[16] = new Implies(new Eventually(new And(p, q)), new And(new Eventually(p), new Eventually(q)) ); //valid
//		forms[17] = new Implies(new Eventually(new Or(p, q)), new Or(new Eventually(p), new Eventually(q)) ); //valid
		forms[0] = new And(new Implies(new And(new Eventually(p), new Eventually(q)), new Eventually(new And(p, q))), new Event(new EventSymbol("e"))); //not valid
//		forms[19] = new Implies(new Or(new Eventually(p), new Eventually(q)), new Eventually(new Or(p, q))); // valid
//		forms[20] = new Implies(new And(p, new Always(new Implies(p, new Next(p)))), new Always(p)); //valid
		
		
		for(Formula psi : forms) {
			System.out.println(psi);
			BoolModelPair<PeriodicIntStruc> o = valid(psi);
			if(o.bool) {
				System.out.println("\u001b[32;1mValid!\u001b[0m");
			} else {
				System.out.println("\u001b[31;1mNot Valid!\u001b[0m");
				System.out.println(o.model);
			}
			System.out.println();
		}

	}
}
