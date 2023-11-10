package symbols;

import formulas.Formula;

public class PropositionalSymbol extends Symbol{

	public PropositionalSymbol(String representation) {
		this.representation = representation;
		Formula.sigma.getPropositionalSymbols().add(this);
	}
	
}