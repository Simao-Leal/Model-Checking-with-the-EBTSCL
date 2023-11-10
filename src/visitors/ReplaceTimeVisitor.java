package visitors;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;
import symbols.*;

public class ReplaceTimeVisitor implements Visitor {
	
	/*
	 * Visitor for replacing every instance of time1 by time2 in a formula.
	 * We actually only replace on subformulas of the type TimeLessThan and Claim as per the paper (p.6)
	 * i.e. it doesn't make much sense to replace on formulas of type TimeEquals
	 * because we would be introducing formulas of the form t = t
	 * TODO actually on the paper it says to replace only on AgentClaim and not Claim,
	 * but I think it makes sense to also replace on Claim
	 */
	
	private TimeSymbol time1;
	private TimeSymbol time2;
	public Formula phi;


	public ReplaceTimeVisitor(TimeSymbol time1, TimeSymbol time2) {
		this.time1 = time1;
		this.time2 = time2;
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
		agentClaim.getClaim().accept(this);
		phi = new AgentClaim(agentClaim.getAgent(), (Claim) phi);
		global(agentClaim);
	}

	@Override
	public void visitClaim(Claim claim) {
		if(claim.getTime().equals(time1)) {
			phi = new Claim(time2, claim.getProp());
			global(claim);
			if(claim.isMinus()) {
				((Claim) phi).invert();
			}
		} else {
			phi = claim.clone();
		}	
	}

	@Override
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy) {
		phi = lessTrustworthy.clone();
	}

	@Override
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy) {
		mostTrustworthy.getClaim().accept(this);
		phi = new AgentClaim(mostTrustworthy.getAgent(), (Claim) phi);
		global(mostTrustworthy);
	}

	@Override
	public void visitTimeEquals(TimeEquals timeEquals) {
		phi = timeEquals.clone();
	}

	@Override
	public void visitTimeLessThan(TimeLessThan timeLessThan) {
		phi = new TimeLessThan(
				( timeLessThan.getTime1().equals(time1) ? time2 : timeLessThan.getTime1() ),
				( timeLessThan.getTime2().equals(time1) ? time2 : timeLessThan.getTime2() )
				);
		global(timeLessThan);
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
