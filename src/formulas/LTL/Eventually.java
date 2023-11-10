package formulas.LTL;

import java.util.Objects;

import formulas.Formula;
import visitors.Visitor;

final public class Eventually extends Formula {
	private Formula inner;
	
	public Eventually(Formula inner) {
		this.inner = inner;
	}
	
	public Formula getInner() {
		return inner;
	}

	@Override
	public String preToString() {
		return "F" + this.inner;
	}
	
	@Override
	public Eventually negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public Eventually expand() {
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
		Eventually other = (Eventually) obj;
		return Objects.equals(inner, other.inner)  && negation == other.negation;
	}
	
	public void accept(Visitor visitor) {
		visitor.visitEventually(this);
	}

	@Override
	public Eventually clone() {
		Eventually phi = new Eventually(this.inner.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
