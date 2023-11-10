package formulas.atemporal;

import java.util.Objects;

import symbols.AgentSymbol;
import symbols.PropositionalSymbol;
import symbols.TimeSymbol;
import visitors.Visitor;

public final class AgentClaim extends AtemporalFormula {
	
	private AgentSymbol agent;
	private Claim claim;
	
	
	public AgentSymbol getAgent() {
		return agent;
	}

	public Claim getClaim() {
		return claim;
	}	
	
	public AgentClaim(AgentSymbol agent, Claim claim) {
		this.agent = agent;
		this.claim = claim;
	}
	
	public AgentClaim(AgentSymbol agent, TimeSymbol time, PropositionalSymbol prop) {
		this.agent = agent;
		this.claim = new Claim(time, prop);
	}

	@Override
	public String preToString() {
		return "(" + agent + " : " + claim + ")";
	}

	@Override
	public AgentClaim negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public AgentClaim expand() {
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
		AgentClaim other = (AgentClaim) obj;
		return Objects.equals(agent, other.agent) 
				&& Objects.equals(claim, other.claim)
				&& negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitAgentClaim(this);
	}

	@Override
	public AgentClaim clone() {
		AgentClaim phi = new AgentClaim(this.agent, this.claim.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
