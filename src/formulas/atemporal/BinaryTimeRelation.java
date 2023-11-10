package formulas.atemporal;

import symbols.TimeSymbol;

public interface BinaryTimeRelation {
	/*
	 * This is only used so we can refer to TimeEquals and TimeLessThan as the same type
	 */	
	public TimeSymbol getTime1();
	public TimeSymbol getTime2();
}
