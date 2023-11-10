package formulas.atemporal;

import java.util.Objects;

import symbols.TimeSymbol;
import visitors.Visitor;

public final class TimeEquals extends AtemporalFormula implements BinaryTimeRelation{
	
	private TimeSymbol time1;
	private TimeSymbol time2;
	
	public TimeEquals(TimeSymbol time1, TimeSymbol time2) {
		this.time1 = time1;
		this.time2 = time2;
	}

	

	public TimeSymbol getTime1() {
		return time1;
	}

	public TimeSymbol getTime2() {
		return time2;
	}



	@Override
	public String preToString() {
		return "(" + time1 + " ≅ " + time2 + ")";
	}

	@Override
	public TimeEquals negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public TimeEquals expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(time1, time2, negation);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeEquals other = (TimeEquals) obj;
		return Objects.equals(time1, other.time1) && Objects.equals(time2, other.time2) 
				&& negation == other.negation;
	}



	public void accept(Visitor visitor) {
		visitor.visitTimeEquals(this);
	}
	
	@Override
	public TimeEquals clone() {
		TimeEquals phi = new TimeEquals(this.time1, this.time2);
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
