package visitors;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;

public interface Visitor {
	//Event
	public void visitEvent(Event event);
	
	//Atemporal formulas
	public void visitAgentClaim(AgentClaim agentClaim);
	public void visitClaim(Claim claim);
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy);
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy);
	public void visitTimeEquals(TimeEquals timeEquals);
	public void visitTimeLessThan(TimeLessThan timeLessThan);
	
	//LTL formulas
	public void visitAlways(Always always);
	public void visitEventually(Eventually eventually);
	public void visitNext(Next next);
	public void visitUntil(Until until);
	
	//Propositional formulas
	public void visitAnd(And and);
	public void visitEquivalent(Equivalent equivalent);
	public void visitFalse(False _false);
	public void visitImplies(Implies implies);
	public void visitOr(Or or);
	public void visitTrue(True _true);
}
