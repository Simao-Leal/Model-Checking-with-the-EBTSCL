package visitors;

import java.util.Iterator;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;
import logicHandlers.PeriodicIntStruc;
import symbols.AgentSymbol;
import symbols.PropositionalSymbol;

public class GlobalSatVisitor implements Visitor {

	private PeriodicIntStruc M;
	private int instant;
	public boolean sat;

	public GlobalSatVisitor(PeriodicIntStruc M, int instant) {
		this.M = M;
		this.instant = instant;
	}

	public void global(Formula phi) {
		//only needed for non AtemporalFormulas, as negations of
		//AtemporalFormulas are treated as AtemporalFormulas
		if(phi.isNegation()) {
			sat = !sat;
		}
	}
	@Override
	public void visitEvent(Event event) {
		sat = M.getLIS(instant).getEvent().equals(event.getSymbol());
		global(event);
	}

	@Override
	public void visitAgentClaim(AgentClaim agentClaim) {
		int claim = M.getLIS(instant).agentClaim(agentClaim.getAgent(),
				 agentClaim.getClaim().getTime(),
				 agentClaim.getClaim().getProp());
		if(claim == 0) {
		sat = false;
		} else {
		sat = agentClaim.getClaim().isMinus() ^ (claim == 1); // ^ = XOR
		}
		global(agentClaim);
	}

	@Override
	public void visitClaim(Claim claim) {
		//TODO This maybe can be optimized by choosing a graph structure?
		boolean foundPositive = false;
		boolean foundNegative = false;
		Claim oppositeClaim = new Claim(claim.getTime(), claim.getProp()).invert();
		AgentSymbol agent;
		for(Iterator<AgentSymbol> iter = Formula.sigma.getAgentSymbols().iterator(); iter.hasNext() && !foundNegative;) {
			agent = iter.next();
			int c = M.getLIS(instant).agentClaim(agent, claim);
			if(!foundPositive && c==1) {
				if(M.sat(new MostTrustworthy(agent, claim))){
					foundPositive = true;
				}
			} else if(c == -1) {
				if(M.sat(new MostTrustworthy(agent, oppositeClaim))) {
					foundNegative = true;
				}
			}
		}
		sat = foundPositive && (! foundNegative);	
		global(claim);
	}

	@Override
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy) {
		sat = M.lessTrustworthy(
				lessTrustworthy.getAgent1(),lessTrustworthy.getProp(), lessTrustworthy.getAgent2());
		global(lessTrustworthy);
	}

	@Override
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy) {
		//TODO This maybe can be optimized by choosing a graph structure?
		Claim claim = mostTrustworthy.getClaim();
		boolean found = false;
		AgentSymbol agent = mostTrustworthy.getAgent();
		PropositionalSymbol propositionalSymbol = mostTrustworthy.getClaim().getProp();
		AgentSymbol otherAgent;
		for(Iterator<AgentSymbol> iter = Formula.sigma.getAgentSymbols().iterator(); iter.hasNext() && !found;) {
			otherAgent = iter.next();
			if(M.lessTrustworthy(agent, propositionalSymbol, otherAgent)) {
				found = M.getLIS(instant).agentClaim(agent, claim) * M.getLIS(instant).agentClaim(otherAgent, claim) == -1;
			}
		}
		sat = !found;
		global(mostTrustworthy);
	}

	@Override
	public void visitTimeEquals(TimeEquals timeEquals) {
		sat = M.timeEquals(timeEquals.getTime1(), timeEquals.getTime2());
		global(timeEquals);
	}

	@Override
	public void visitTimeLessThan(TimeLessThan timeLessThan) {
		sat = M.timeLessThan(timeLessThan.getTime1(), timeLessThan.getTime2());
		global(timeLessThan);
	}

	@Override
	public void visitAlways(Always always) {
		//if initial instant is inside the periodic block then we 
		//just need to check in a period of size periodLength after instant
		int originalInstant = instant;
		boolean found = false; //found an instant where the inner formula is false
		int end = (instant < M.getPrefixLength() ? M.getPrefixLength() + M.getPeriodLength() : instant + M.getPeriodLength());
		for(; instant <  end && !found; instant++) {
			always.getInner().accept(this);
			found = !sat;
		}
		sat = !found;
		instant = originalInstant;
	}

	@Override
	public void visitEventually(Eventually eventually) {
		//if initial instant is inside the periodic block then we 
		//just need to check in a period of size periodLength after instant
		int originalInstant = instant;
		boolean found = false; //found an instant where the inner formula is true
		int end = (instant < M.getPrefixLength() ? M.getPrefixLength() + M.getPeriodLength() : instant + M.getPeriodLength());
		for(; instant <  end && !found; instant++) {
			eventually.getInner().accept(this);
			found = sat;
		}
		sat = found;
		instant = originalInstant;
	}

	@Override
	public void visitNext(Next next) {
		instant++;
		next.getInner().accept(this);
		instant--;
		global(next);
	}


	@Override
	public void visitUntil(Until until) {
		//if initial instant is inside the periodic block then we 
		//just need to check in a period of size periodLength after instant
		int originalInstant = instant;
		boolean foundFalse = false; //found an instant where phi1 does not hold
		boolean foundTrue = false; //found an instant where phi2 holds
		int end = (instant < M.getPrefixLength() ? M.getPrefixLength() + M.getPeriodLength() : instant + M.getPeriodLength());
		
		for(; instant < end && !foundTrue && !foundFalse ; instant++) {
			until.getInner2().accept(this);
			foundTrue = sat;
			if(!foundTrue) {
				until.getInner1().accept(this);
				foundFalse = !sat;
			}
		}
		sat = foundTrue;
		instant = originalInstant;
	}

	@Override
	public void visitAnd(And and) {
		and.getInner1().accept(this);
		if(sat) { 
			//if the sat value of the left formula is true, evaluate the second one
			//else the and is false and so we don't need to do anything
			and.getInner2().accept(this);
		}
		global(and);
	}

	@Override
	public void visitEquivalent(Equivalent equivalent) {
		equivalent.getInner1().accept(this);
		boolean first = sat;
		equivalent.getInner2().accept(this);
		sat = (first == sat);
		global(equivalent);
	}

	@Override
	public void visitFalse(False _false) {
		sat = false;
		global(_false);
	}

	@Override
	public void visitImplies(Implies implies) {
		implies.getInner1().accept(this);
		if(!sat) { 
			//if the sat value of the left formula is true, then the implies is true
			sat = true;
		} else {
			//if the antecedent is true, the implies has the same value as the consequent
			implies.getInner2().accept(this);
		}
		global(implies);
	}

	@Override
	public void visitOr(Or or) {
		or.getInner1().accept(this);
		if(!sat) { 
			//if the sat value of the left formula is false, evaluate the second one
			//else the or is true and so we don't need to do anything
			or.getInner2().accept(this);
		}
		global(or);
	}

	@Override
	public void visitTrue(True _true) {
		sat = true;
		global(_true);
	}

}
