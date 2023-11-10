package formulas.atemporal;

import java.util.Objects;

import symbols.AgentSymbol;
import visitors.Visitor;

final public class MostTrustworthy extends AtemporalFormula{
	private AgentSymbol agent;
	private Claim claim;

	public MostTrustworthy(AgentSymbol agent, Claim claim) {
		this.agent = agent;
		this.claim = claim;
	}

	public AgentSymbol getAgent() {
		return agent;
	}

	public Claim getClaim() {
		return claim;
	}

	@Override
	public String preToString() {
		return "(" + agent + " : ⊡" + claim + ")";
	}
	
	@Override
	public MostTrustworthy negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public MostTrustworthy expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(agent, claim, negation);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MostTrustworthy other = (MostTrustworthy) obj;
		return Objects.equals(agent, other.agent) && Objects.equals(claim, other.claim) && negation == other.negation;
	}



	public void accept(Visitor visitor) {
		visitor.visitMostTrustworthy(this);
	}
	
	@Override
	public MostTrustworthy clone() {
		MostTrustworthy phi = new MostTrustworthy(this.agent, this.claim.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}