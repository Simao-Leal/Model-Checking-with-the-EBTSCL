package visitors;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;

public class LaTeXVisitor implements Visitor {
	
	private String s = "";
	
	//auxiliary function:
	private void addGlobalBefore(Formula phi) {
		if(phi.isExpanded()) s += " \\left( ";
		if(phi.isNegation()) s += " \\neg ";
	}
	
	private void addGlobalAfter(Formula phi) {
		if(phi.isExpanded()) s += " \\right) ^ \\checkmark ";
	}
	
	

	@Override
	public void visitEvent(Event event) {
		addGlobalBefore(event);
		s += event.getSymbol();
		addGlobalAfter(event);
	}

	@Override
	public void visitAgentClaim(AgentClaim agentClaim) {
		addGlobalBefore(agentClaim);
		s +=  " \\left( " + agentClaim.getAgent() + " : ";
		agentClaim.getClaim().accept(this); //this = current visitor
		s += " \\right) ";
		addGlobalAfter(agentClaim);
	}

	@Override
	public void visitClaim(Claim claim) {
		addGlobalBefore(claim);
		s += " \\left( " + 
				(claim.isMinus() ? " - " : "") + 
				claim.getTime() +
				" \\cdot " + 
				claim.getProp() + 
				" \\right) ";
		addGlobalAfter(claim);
	}

	@Override
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy) {
		addGlobalBefore(lessTrustworthy);
		s +=  lessTrustworthy.getAgent1() + 
				" \\trianglelefteq_{ " +
				lessTrustworthy.getProp() +
				" } " +
				lessTrustworthy.getAgent2();
		addGlobalAfter(lessTrustworthy);
	}

	@Override
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy) {
		addGlobalBefore(mostTrustworthy);
		s += " \\left( " + 
				mostTrustworthy.getAgent() + 
				" : \\boxdot ";
		mostTrustworthy.getClaim().accept(this);
		s += " \\right) ";
		addGlobalAfter(mostTrustworthy);
	}

	@Override
	public void visitTimeEquals(TimeEquals timeEquals) {
		addGlobalBefore(timeEquals);
		s += " \\left( " +
				timeEquals.getTime1() + 
				" \\cong " + 
				timeEquals.getTime2() + 
				" \\right) ";
		addGlobalAfter(timeEquals);
	}

	@Override
	public void visitTimeLessThan(TimeLessThan timeLessThan) {
		addGlobalBefore(timeLessThan);
		s += " \\left( " +
				timeLessThan.getTime1() + 
				" < " + 
				timeLessThan.getTime2() + 
				" \\right) ";
		addGlobalAfter(timeLessThan);
	}

	@Override
	public void visitAlways(Always always) {
		addGlobalBefore(always);
		s += " \\mathsf{G} ";
					always.getInner().accept(this);
		addGlobalAfter(always);
	}

	@Override
	public void visitEventually(Eventually eventually) {
		addGlobalBefore(eventually);
		s += " \\mathsf{F} ";
					eventually.getInner().accept(this);
		addGlobalAfter(eventually);

	}

	@Override
	public void visitNext(Next next) {
		addGlobalBefore(next);
		s += " \\mathsf{X} ";
					next.getInner().accept(this);
		addGlobalAfter(next);
	}

	@Override
	public void visitUntil(Until until) {
		addGlobalBefore(until);
		s += " \\left( ";
		until.getInner1().accept(this);
		s += " \\mathsf{U} ";
		until.getInner2().accept(this);
		s += " \\right) ";
		addGlobalAfter(until);
	}

	@Override
	public void visitAnd(And and) {
		addGlobalBefore(and);
		s += " \\left( ";
		and.getInner1().accept(this);
		s += " \\land ";
		and.getInner2().accept(this);
		s += " \\right) ";
		addGlobalAfter(and);
	}

	@Override
	public void visitEquivalent(Equivalent equivalent) {
		addGlobalBefore(equivalent);
		s += " \\left( ";
		equivalent.getInner1().accept(this);
		s += " \\iff ";
		equivalent.getInner2().accept(this);
		s += " \\right) ";
		addGlobalAfter(equivalent);
	}

	@Override
	public void visitFalse(False _false) {
		addGlobalBefore(_false);
		s += " \\bot ";
		addGlobalAfter(_false);
	}

	@Override
	public void visitImplies(Implies implies) {
		addGlobalBefore(implies);
		s += " \\left( ";
		implies.getInner1().accept(this);
		s += " \\implies ";
		implies.getInner2().accept(this);
		s += " \\right) ";
		addGlobalAfter(implies);

	}

	@Override
	public void visitOr(Or or) {
		addGlobalBefore(or);
		s += " \\left( ";
		or.getInner1().accept(this);
		s += " \\lor ";
		or.getInner2().accept(this);
		s += " \\right) ";
		addGlobalAfter(or);

	}

	@Override
	public void visitTrue(True _true) {
		addGlobalBefore(_true);
		s += " \\top ";
		addGlobalAfter(_true);
	}
	
	public static String toLaTeX(Formula phi) {
		//Requires \\usepackage{amsmath}
		LaTeXVisitor v = new LaTeXVisitor();
		phi.accept(v);
		return v.s;
	}

}
