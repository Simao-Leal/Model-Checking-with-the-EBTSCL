package formulas.atemporal;

import java.util.Objects;

import symbols.*;
import visitors.Visitor;

final public class Claim extends AtemporalFormula {
	private boolean minus; //True if a negative claim
	private TimeSymbol time;
	private PropositionalSymbol prop;

	public boolean isMinus() {
		return minus;
	}

	public TimeSymbol getTime() {
		return time;
	}

	public PropositionalSymbol getProp() {
		return prop;
	}

	public Claim(TimeSymbol time, PropositionalSymbol prop) {
		this.time = time;
		this.prop = prop;
		this.minus = false; //all claims are initialized as positive claims
	}
	
	public Claim invert() { //changes sign of claim 
		minus = !minus;
		return this;
	}

	@Override
	public String preToString() {
		return (minus ? "-" : "") + "(" + time + " ∙ " + prop + ")";
	}

	@Override
	public Claim negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public Claim expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(minus, prop, time, negation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Claim other = (Claim) obj;
		return minus == other.minus && Objects.equals(prop, other.prop)
				&& Objects.equals(time, other.time) && negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitClaim(this);
	}

	@Override
	public Claim clone() {
		Claim phi = new Claim(this.time, this.prop);
		phi.minus = this.minus;
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}

}
