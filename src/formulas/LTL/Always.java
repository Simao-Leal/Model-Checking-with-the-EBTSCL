package formulas.LTL;

import java.util.Objects;

import formulas.Formula;
import visitors.Visitor;

final public class Always extends Formula {
	private Formula inner;

	public Always(Formula inner) {
		this.inner = inner;
	}
	
	public Formula getInner() {
		return inner;
	}
	
	@Override
	public String preToString() {
		return "G" + this.inner;
	}

	@Override
	public Always negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public Always expand() {
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
		Always other = (Always) obj;
		return Objects.equals(inner, other.inner)  && negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitAlways(this);
	}
	
	@Override
	public Always clone() {
		Always phi = new Always(this.inner.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
