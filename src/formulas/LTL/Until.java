package formulas.LTL;

import java.util.Objects;

import formulas.Formula;
import visitors.Visitor;

final public class Until extends Formula {
	private Formula inner1;
	private Formula inner2;

	
	public Formula getInner1() {
		return inner1;
	}


	public Formula getInner2() {
		return inner2;
	}


	public Until(Formula inner1, Formula inner2) {
		this.inner1 = inner1;
		this.inner2 = inner2;
	}


	@Override
	public String preToString() {
		return "(" + this.inner1 + " U " + this.inner2 + ")";
	}

	@Override
	public Until negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public Until expand() {
		expanded = true;
		return this;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(inner1, inner2, negation);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Until other = (Until) obj;
		return Objects.equals(inner1, other.inner1) && Objects.equals(inner2, other.inner2) 
				&& negation == other.negation;
	}


	public void accept(Visitor visitor) {
		visitor.visitUntil(this);
	}
	
	@Override
	public Until clone() {
		Until phi = new Until(this.inner1.clone(), this.inner2.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
