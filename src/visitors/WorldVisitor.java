package visitors;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;
import logicHandlers.SetFormulas;
import logicHandlers.SetSetFormulas;
import symbols.*;

public class WorldVisitor implements Visitor {
	
	public SetSetFormulas setSet = new SetSetFormulas();
	
	public boolean notExpanded(Formula phi) {
		if(phi.isExpanded()) {
			monolith(phi);
			return false;
		} else {
			return true;
		}
	}
	
	public void monolith(Formula phi) {
		//an expanded formula is a monolith, so there is
		//no need to check for that
		SetFormulas set = new SetFormulas();
		set.add(phi.clone());
		setSet.add(set);
	}
	
	public void propagate(Formula phi) {
		//world for relations
		if(notExpanded(phi)) {
			SetFormulas set = new SetFormulas();
			set.add(phi.clone());
			set.add(new Next(phi.clone()));
			setSet.add(set);
		}
	}
	
	public void monolith() {
		//for true and not false
		SetFormulas set = new SetFormulas();
		setSet.add(set);
	}
	
	@Override
	public void visitEvent(Event event) {
		if(notExpanded(event)) {
			if(event.isNegation()) {
				SetFormulas set;
				for(EventSymbol otherEvent : Formula.sigma.getEventSymbols() ) {
					if(!event.getSymbol().equals(otherEvent)) {
						set = new SetFormulas();
						set.add(new Event(otherEvent));
						setSet.add(set);
					}
				}
				if(setSet.size() == 0) {
					/*
					 * then there is only one event, e, in the signature and so this
					 * event must happen in every instant. Thus world(not e) = {{false}}
					 */
					set = new SetFormulas();
					set.add(new False());
					setSet.add(set);
				}
			} else {
				monolith(event);
			}
		}
	}

	@Override
	public void visitAgentClaim(AgentClaim agentClaim) {
		monolith(agentClaim);
	}

	@Override
	public void visitClaim(Claim claim) {
		if(notExpanded(claim)) {
			if(claim.isNegation()) {
				SetFormulas set;
				Claim inner_claim = claim.clone().negate(); //claim is not phi, so we must get phi, here lies a bug that evaded me for 8 months
				for(AgentSymbol a : Formula.sigma.getAgentSymbols()) {
					set = new SetFormulas();
					set.add(new AgentClaim(a, inner_claim.clone().invert()));
					set.add(new MostTrustworthy(a, inner_claim.clone().invert()));
					
					setSet.add(set);
				}
				set = new SetFormulas();
				for(AgentSymbol b : Formula.sigma.getAgentSymbols()) { 
					//I could have used "a" again, but I'm following the notation on the paper
					set.add(new Implies(
							new AgentClaim(b, inner_claim.clone()),
							new MostTrustworthy(b, inner_claim.clone()).negate()
							));
				}
				setSet.add(set);
				
			} else {
				SetFormulas set;
				for(AgentSymbol a : Formula.sigma.getAgentSymbols()) {
					set = new SetFormulas();
					for(AgentSymbol b : Formula.sigma.getAgentSymbols()) {
						set.add(new Implies(
								new AgentClaim(b, claim.clone().invert()),
								new MostTrustworthy(b, claim.clone().invert()).negate()
								));
					}
					set.add(new AgentClaim(a, claim.clone()));
					set.add(new MostTrustworthy(a, claim.clone()));
					
					setSet.add(set);
				}
			}
		}

	}

	@Override
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy) {
		propagate(lessTrustworthy);
	}

	@Override
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy) {
		if(notExpanded(mostTrustworthy)) {
			AgentSymbol a = mostTrustworthy.getAgent();
			Claim claim = mostTrustworthy.getClaim();
			if(mostTrustworthy.isNegation()) {
				SetFormulas set;
				for(AgentSymbol b : Formula.sigma.getAgentSymbols()) {
					set = new SetFormulas();
					set.add(new LessTrustworthy(a, claim.getProp(), b));
					set.add(new AgentClaim(b, claim.clone().invert()));
					
					setSet.add(set);
				}
				
			} else {
				SetFormulas set = new SetFormulas();
				for(AgentSymbol b : Formula.sigma.getAgentSymbols()) {
					set.add(new Implies(
								new LessTrustworthy(a, claim.getProp(), b),
								new AgentClaim(b, claim.clone().invert()).negate()
								));
				}
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitTimeEquals(TimeEquals timeEquals) {
		propagate(timeEquals);
	}

	@Override
	public void visitTimeLessThan(TimeLessThan timeLessThan) {
		propagate(timeLessThan);
	}

	@Override
	public void visitAlways(Always always) {
		if(notExpanded(always)) {
			if(always.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(always.getInner().clone().negate());
				setSet.add(set);
				set = new SetFormulas();
				set.add(always.getInner().clone());
				set.add(new Next(new Always(always.getInner().clone()).negate()));
				//Here lies a bug that evaded me for 7 months
				//we cannot just clone always because that would give an expanded formula
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(always.getInner().clone());
				set.add(new Next(new Always(always.getInner().clone())));
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitEventually(Eventually eventually) {
		if(notExpanded(eventually)) {
			if(eventually.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(eventually.getInner().clone().negate());
				set.add(new Next(new Eventually(eventually.getInner().clone()).negate()));
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(eventually.getInner().clone());
				setSet.add(set);
				set = new SetFormulas();
				set.add(eventually.getInner().clone().negate());
				set.add(new Next(new Eventually(eventually.getInner().clone())));
				//we cannot just clone eventually because that would give an expanded formula
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitNext(Next next) {
		if(notExpanded(next)) {
			if(next.isNegation()) {
				monolith(new Next(next.getInner().clone().negate()));
			} else {
				monolith(next);
			}
		}
	}

	@Override
	public void visitUntil(Until until) {
		if(notExpanded(until)) {
			if(until.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(until.getInner2().clone().negate());
				set.add(until.getInner1().clone().negate());
				setSet.add(set);
				set = new SetFormulas();
				set.add(until.getInner1().clone());
				set.add(until.getInner2().clone().negate());
				set.add(new Next(new Until(until.getInner1().clone(), until.getInner2().clone()).negate()));
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(until.getInner2().clone());
				setSet.add(set);
				set = new SetFormulas();
				set.add(until.getInner1().clone());
				set.add(until.getInner2().clone().negate());
				set.add(new Next(new Until(until.getInner1().clone(), until.getInner2().clone())));
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitAnd(And and) {
		if(notExpanded(and)) {
			if(and.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(and.getInner1().clone().negate());
				setSet.add(set);
				set = new SetFormulas();
				set.add(and.getInner2().clone().negate());
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(and.getInner1().clone());
				set.add(and.getInner2().clone());
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitEquivalent(Equivalent equivalent) {
		if(notExpanded(equivalent)) {
			if(equivalent.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(equivalent.getInner1().clone());
				set.add(equivalent.getInner2().clone().negate());
				setSet.add(set);
				set = new SetFormulas();
				set.add(equivalent.getInner1().clone().negate());
				set.add(equivalent.getInner2().clone());
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(equivalent.getInner1().clone());
				set.add(equivalent.getInner2().clone());
				setSet.add(set);
				set = new SetFormulas();
				set.add(equivalent.getInner1().clone().negate());
				set.add(equivalent.getInner2().clone().negate());
				setSet.add(set);
			}
		}

	}

	@Override
	public void visitFalse(False _false) {
		if(notExpanded(_false)) {
			if(_false.isNegation()) {
				monolith();
			} else {
				monolith(_false);
			}
		}
	}

	@Override
	public void visitImplies(Implies implies) {
		if(notExpanded(implies)) {
			if(implies.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(implies.getInner1().clone());
				set.add(implies.getInner2().clone().negate());
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(implies.getInner1().clone().negate());
				setSet.add(set);
				set = new SetFormulas();
				set.add(implies.getInner2().clone());
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitOr(Or or) {
		if(notExpanded(or)) {
			if(or.isNegation()) {
				SetFormulas set = new SetFormulas();
				set.add(or.getInner1().clone().negate());
				set.add(or.getInner2().clone().negate());
				setSet.add(set);
			} else {
				SetFormulas set = new SetFormulas();
				set.add(or.getInner1().clone());
				setSet.add(set);
				set = new SetFormulas();
				set.add(or.getInner2().clone());
				setSet.add(set);
			}
		}
	}

	@Override
	public void visitTrue(True _true) {
		if(notExpanded(_true)) {
			if(_true.isNegation()) {
				monolith(new False());
			} else {
				monolith();
			}
		}
	}
}
