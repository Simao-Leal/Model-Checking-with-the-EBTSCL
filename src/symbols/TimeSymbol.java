package symbols;

import formulas.Formula;

public class TimeSymbol extends Symbol {
	
	public TimeSymbol(String representation) {
		this.representation = representation;
		Formula.sigma.getTimeSymbols().add(this);
	}

	
}
