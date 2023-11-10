package formulas.LTL;

import java.util.Objects;

import formulas.Formula;
import visitors.Visitor;

final public class Next extends Formula {
	private Formula inner;
	
	public Next(Formula inner) {
		this.inner = inner;
	}
	
	public Formula getInner() {
		return inner;
	}

	@Override
	public String preToString() {
		return "X" + this.inner;
	}
	
	@Override
	public Next negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public Next expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(inner, negation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Next other = (Next) obj;
		return Objects.equals(inner, other.inner) && negation == other.negation;
	}
	
	public void accept(Visitor visitor) {
		visitor.visitNext(this);
	}
	
	@Override
	public Next clone() {
		Next phi = new Next(this.inner.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}

}
