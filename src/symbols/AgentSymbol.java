package symbols;

import formulas.Formula;

public class AgentSymbol extends Symbol {
	
	public AgentSymbol(String representation) {
		this.representation = representation;
		Formula.sigma.getAgentSymbols().add(this);
	}
	
}
