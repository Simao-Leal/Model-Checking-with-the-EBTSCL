package logicHandlers;

import java.util.HashSet;
import java.util.Set;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.Next;
import symbols.EventSymbol;
import symbols.TimeSymbol;

@SuppressWarnings("serial")
public class SetFormulas extends HashSet<Formula> {
	
	public SetFormulas() {
		super();
	}
	
	private boolean monolithQ(Formula phi) { //for testing purposes only
		return 	phi instanceof TimeLessThan ||
				phi instanceof TimeEquals ||
				phi instanceof LessTrustworthy ||
				phi instanceof AgentClaim ||
				phi instanceof Event || //not really a monolith but ok
				phi instanceof Next;
	}
	
	@Override
	public String toString() {
		String s = "{";
		for(Formula phi : this) {
			//if(!monolithQ(phi)) continue; //FIXME for testing purposes only
			s += phi + ", ";
		}
		if(s.length() > 1) s = s.substring(0, s.length()-2);
		s += "}";
		return s;
	}

	public void print() {
		//FIXME do not use except for testing purposes
		String s = "{";
		for(Formula phi : this) {
			s += phi + ", ";
		}
		if(s.length() > 1) s = s.substring(0, s.length()-2);
		s += "}";
		System.err.println(s);
	}
	
	public String toLaTeX() {
		String s = "\\{";
		for(Formula f : this) {
			s += f.toLaTeX();
			s += ", ";
		}
		s = s.substring(0, s.length() - 2);
		s += "\\}";
		return s;
	}
	
	public SetSetFormulas star() {
		SetFormulas aux = (SetFormulas) this.clone();
		aux.add(World.tau(aux));
		SetSetFormulas world = World.worldClosure(aux);
		SetSetFormulas result = new SetSetFormulas();
		for(SetFormulas delta : world) {
			result.add(World.operatorClosure(delta));
		}
		return result;
	}

	public static void main(String[] args) {
		AtemporalFormula phi1 = new TimeLessThan(new TimeSymbol("t1"), new TimeSymbol("t2"));
		AtemporalFormula phi2 = new TimeEquals(new TimeSymbol("t2"), new TimeSymbol("t3"));
		AtemporalFormula phi3 = new TimeLessThan(new TimeSymbol("t3"), new TimeSymbol("t1")); //false
	
		SetFormulas s = new SetFormulas();
		s.add(phi1);
		s.add(phi2);
		s.add(phi3);
		System.out.println(s.star());
	}
}
