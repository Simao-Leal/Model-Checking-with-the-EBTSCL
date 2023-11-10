package symbols;

import formulas.Formula;

public class EventSymbol extends Symbol {
	
	public EventSymbol(String representation) {
		this.representation = representation;
		Formula.sigma.getEventSymbols().add(this);
	}
}