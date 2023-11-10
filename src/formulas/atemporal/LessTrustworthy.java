package formulas.atemporal;

import java.util.Objects;

import symbols.*;
import visitors.Visitor;

final public class LessTrustworthy extends AtemporalFormula{
	private AgentSymbol agent1;
	private PropositionalSymbol prop;
	private AgentSymbol agent2;
	
	public LessTrustworthy(AgentSymbol agent1, PropositionalSymbol prop, AgentSymbol agent2) {
		this.agent1 = agent1;
		this.prop = prop;
		this.agent2 = agent2;
	}

	
	public AgentSymbol getAgent1() {
		return agent1;
	}


	public PropositionalSymbol getProp() {
		return prop;
	}


	public AgentSymbol getAgent2() {
		return agent2;
	}


	@Override
	public String preToString() {
		return "(" + agent1 + " ⊴[" + prop + "] " + agent2 + ")";
	}
	
	@Override
	public LessTrustworthy negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public LessTrustworthy expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(agent1, agent2, prop, negation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LessTrustworthy other = (LessTrustworthy) obj;
		return Objects.equals(agent1, other.agent1) && Objects.equals(agent2, other.agent2)
				&& Objects.equals(prop, other.prop) && negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitLessTrustworthy(this);
	}


	@Override
	public LessTrustworthy clone() {
		LessTrustworthy phi = new LessTrustworthy(this.agent1, this.prop, this.agent2);
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
