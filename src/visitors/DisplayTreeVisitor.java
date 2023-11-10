package visitors;

import java.util.Stack;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;

public class DisplayTreeVisitor implements Visitor {
	
	String s = "";
	int lvl = 0;
	Stack<Integer> connecting = new Stack<>();
	
	private void connect() {
		connecting.push(lvl);
	}
	
	private void disconnect() {
		connecting.pop();
	}
	
	private void addLvl() {
		for(int i = 0; i < lvl; i++) {
			if(connecting.contains(i)) s += "│   ";
			else s += "    ";
		}
	}
	
	private void addGlobalBefore(Formula phi) {
		if(phi.isNegation()) {

			s += "Not (¬)\n";
			addLvl();
			s += "└──";
			lvl++;
		}
	}
	
	private void addGlobalAfter(Formula phi) {
		if(phi.isNegation()) lvl--;
	}
	
	@Override
	public void visitEvent(Event event) {
		addGlobalBefore(event);
		s += "Event: " + event + "\n";
		addGlobalAfter(event);
	}

	@Override
	public void visitAgentClaim(AgentClaim agentClaim) {
		addGlobalBefore(agentClaim);
		s += "Agent Claim\n";
		addLvl();
		s += "├──Agent: " + agentClaim.getAgent() + "\n";
		addLvl();
		s += "└──";
		lvl++;
		agentClaim.getClaim().accept(this);
		lvl--;
		addGlobalAfter(agentClaim);
	}

	@Override
	public void visitClaim(Claim claim) {
		addGlobalBefore(claim);
		s += "Claim\n";
		if(claim.isMinus()) {
			addLvl();
			s += "└──Minus (-)\n";
			lvl++;
		}
		addLvl();
		s += "├──Time: " + claim.getTime() + "\n";
		addLvl();
		s += "└──Proposition: " + claim.getProp() + "\n"; 
		if(claim.isMinus()) lvl--;
		addGlobalAfter(claim);
	}

	@Override
	public void visitLessTrustworthy(LessTrustworthy lessTrustworthy) {
		addGlobalBefore(lessTrustworthy);
		s += "Less trustworthy than [" + lessTrustworthy.getProp()+"]\n";
		addLvl();
		s += "├──Agent: " + lessTrustworthy.getAgent1() + "\n";
		addLvl();
		s += "└──Agent: " + lessTrustworthy.getAgent2() + "\n";
		addGlobalAfter(lessTrustworthy);
	}

	@Override
	public void visitMostTrustworthy(MostTrustworthy mostTrustworthy) {
		addGlobalBefore(mostTrustworthy);
		s += "Most trustworthy (⊡)\n";
		addLvl();
		s += "├──Agent: " + mostTrustworthy.getAgent() + "\n";
		addLvl();
		s += "└──";
		lvl++;
		mostTrustworthy.getClaim().accept(this);
		lvl--;
		addGlobalAfter(mostTrustworthy);
	}

	@Override
	public void visitTimeEquals(TimeEquals timeEquals) {
		addGlobalBefore(timeEquals);
		s += "Time equals (≅)\n";
		addLvl();
		s += "├──Time: " + timeEquals.getTime1() + "\n";
		addLvl();
		s += "└──Time: " + timeEquals.getTime2() + "\n";
		addGlobalAfter(timeEquals);
	}

	@Override
	public void visitTimeLessThan(TimeLessThan timeLessThan) {
		addGlobalBefore(timeLessThan);
		s += "Time Less Than (<)\n";
		addLvl();
		s += "├──Time: " + timeLessThan.getTime1() + "\n";
		addLvl();
		s += "└──Time: " + timeLessThan.getTime2() + "\n";
		addGlobalAfter(timeLessThan);

	}

	@Override
	public void visitAlways(Always always) {
		addGlobalBefore(always);
		s += "Always (G)\n";
		addLvl();
		s += "└──";
		lvl++;
		always.getInner().accept(this);
		lvl--;
		addGlobalAfter(always);
	}

	@Override
	public void visitEventually(Eventually eventually) {
		addGlobalBefore(eventually);
		s += "Eventually (F)\n";
		addLvl();
		s += "└──";
		lvl++;
		eventually.getInner().accept(this);
		lvl--;
		addGlobalAfter(eventually);
	}

	@Override
	public void visitNext(Next next) {
		addGlobalBefore(next);
		s += "Next (X)\n";
		addLvl();
		s += "└──";
		lvl++;
		next.getInner().accept(this);
		lvl--;
		addGlobalAfter(next);
	}

	@Override
	public void visitUntil(Until until) {
		addGlobalBefore(until);
		s += "Until (U)\n";
		addLvl();
		s += "├──";
		connect();
		lvl++;
		until.getInner1().accept(this);
		lvl--;
		addLvl();
		s += "└──";
		disconnect();
		lvl++;
		until.getInner2().accept(this);
		lvl--;
		addGlobalAfter(until);
	}

	@Override
	public void visitAnd(And and) {
		addGlobalBefore(and);
		s += "And (⋀)\n";
		addLvl();
		s += "├──";
		connect();
		lvl++;
		and.getInner1().accept(this);
		lvl--;
		addLvl();
		s += "└──";
		disconnect();
		lvl++;
		and.getInner2().accept(this);
		lvl--;
		addGlobalAfter(and);
	}

	@Override
	public void visitEquivalent(Equivalent equivalent) {
		addGlobalBefore(equivalent);
		s += "Equivalent (<=>)\n";
		addLvl();
		s += "├──";
		connect();
		lvl++;
		equivalent.getInner1().accept(this);
		lvl--;
		addLvl();
		s += "└──";
		disconnect();
		lvl++;
		equivalent.getInner2().accept(this);
		lvl--;
		addGlobalAfter(equivalent);
	}

	@Override
	public void visitFalse(False _false) {
		s += "False (⊥)\n";
	}

	@Override
	public void visitImplies(Implies implies) {
		addGlobalBefore(implies);
		s += "Implies (=>)\n";
		addLvl();
		s += "├──";
		connect();
		lvl++;
		implies.getInner1().accept(this);
		lvl--;
		addLvl();
		s += "└──";
		disconnect();
		lvl++;
		implies.getInner2().accept(this);
		lvl--;
		addGlobalAfter(implies);
	}

	@Override
	public void visitOr(Or or) {
		addGlobalBefore(or);
		s += "Or (⋁)\n";
		addLvl();
		s += "├──";
		connect();
		lvl++;
		or.getInner1().accept(this);
		lvl--;
		addLvl();
		s += "└──";
		disconnect();
		lvl++;
		or.getInner2().accept(this);
		lvl--;
		addGlobalAfter(or);
	}

	@Override
	public void visitTrue(True _true) {
		s += "True (⊤)\n";
	}
	
	public static String DisplayTree(Formula phi) {
		DisplayTreeVisitor v = new DisplayTreeVisitor();
		phi.accept(v);
		return v.s;
	}
}
