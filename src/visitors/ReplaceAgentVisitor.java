package visitors;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;
import symbols.*;

public class ReplaceAgentVisitor implements Visitor {
	
	/*
	 * Visitor for replacing every instance of agent1 by agent2 in a formula.
	 * We actually only replace on subformulas of the type AgentClaim and MostTrustworthy as per the paper (p.6)
	 * i.e. it doesn't make much sense to replace on formulas of type LessTrustworthy
	 * because we would be introducing formulas of the form a <= a
	 * TODO actually on the paper it says to replace only on AgentClaim and not MostTrustworthy,
	 * but I think it makes sense to also replace on MostTrustworthy
	 */
	
	private PropositionalSymbol prop;
	private AgentSymbol agent1;
	private AgentSymbol agent2;
	public Formula phi;


	public ReplaceAgentVisitor(PropositionalSymbol prop, AgentSymbol agent1, AgentSymbol agent2) {
		this.prop = prop;
		this.agent1 = agent1;
		this.agent2 = agent2;
	}
	
	public void global(Formula form) {
		if(form.isNegation()) {
			phi.negate();
		}
	}

	@Override
	public void visitEvent(Event event) {
		phi = event.clone();
	}

	@Override
	public void visitAgentClaim(AgentClaim agentClaim) {
		if(agentClaim.getClaim().getProp().equals(prop)) {
			phi = new AgentClaim(agentClaim.getAgent().equals(agent1) ?
					agent2 : agentClaim.getAgent()
					,agentClaim.getClaim().clone());
			global(agentClaim);
		} else {
			phi = agentClaim.clone();
		}
	}

	@Override
	public void visitClaim(Claim claim) {
		phi = claim.clone();
	}

	@Override
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy) {
		phi = lessTrustworthy.clone();
	}

	@Override
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy) {
		if(mostTrustworthy.getClaim().getProp().equals(prop)) {
			phi = new MostTrustworthy(mostTrustworthy.getAgent().equals(agent1) ?
					agent2 : mostTrustworthy.getAgent()
					,mostTrustworthy.getClaim().clone());
			global(mostTrustworthy);
		} else {
			phi = mostTrustworthy.clone();
		}
	}

	@Override
	public void visitTimeEquals(TimeEquals timeEquals) {
		phi = timeEquals.clone();
	}

	@Override
	public void visitTimeLessThan(TimeLessThan timeLessThan) {
		phi = timeLessThan.clone();
	}

	@Override
	public void visitAlways(Always always) {
		always.getInner().accept(this);
		phi = new Always(phi);
		global(always);
	}

	@Override
	public void visitEventually(Eventually eventually) {
		eventually.getInner().accept(this);
		phi = new Eventually(phi);
		global(eventually);
	}

	@Override
	public void visitNext(Next next) {
		next.getInner().accept(this);
		phi = new Next(phi);
		global(next);
	}

	@Override
	public void visitUntil(Until until) {
		until.getInner1().accept(this);
		Formula phi1 = phi;
		until.getInner2().accept(this);
		Formula phi2 = phi;
		phi = new Until(phi1, phi2);
		global(until);
	}

	@Override
	public void visitAnd(And and) {
		and.getInner1().accept(this);
		Formula phi1 = phi;
		and.getInner2().accept(this);
		Formula phi2 = phi;
		phi = new And(phi1, phi2);
		global(and);
	}

	@Override
	public void visitEquivalent(Equivalent equivalent) {
		equivalent.getInner1().accept(this);
		Formula phi1 = phi;
		equivalent.getInner2().accept(this);
		Formula phi2 = phi;
		phi = new Equivalent(phi1, phi2);
		global(equivalent);
	}

	@Override
	public void visitFalse(False _false) {
		phi = _false.clone();
	}

	@Override
	public void visitImplies(Implies implies) {
		implies.getInner1().accept(this);
		Formula phi1 = phi;
		implies.getInner2().accept(this);
		Formula phi2 = phi;
		phi = new Implies(phi1, phi2);
		global(implies);
	}

	@Override
	public void visitOr(Or or) {
		or.getInner1().accept(this);
		Formula phi1 = phi;
		or.getInner2().accept(this);
		Formula phi2 = phi;
		phi = new Or(phi1, phi2);
		global(or);
	}

	@Override
	public void visitTrue(True _true) {
		phi = _true.clone();
	}
}
