package formulas.propositional;

import java.util.Objects;

import formulas.Formula;
import visitors.Visitor;

final public class Equivalent extends Formula {
	private Formula inner1;
	private Formula inner2;

	
	public Equivalent(Formula inner1, Formula inner2) {
		this.inner1 = inner1;
		this.inner2 = inner2;
	}


	public Formula getInner1() {
		return inner1;
	}


	public Formula getInner2() {
		return inner2;
	}


	@Override
	public String preToString() {
		return "(" + this.inner1 + " <=> " + this.inner2 + ")";
	}
	
	@Override
	public Equivalent negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public Equivalent expand() {
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
		Equivalent other = (Equivalent) obj;
		return Objects.equals(inner1, other.inner1) && Objects.equals(inner2, other.inner2) 
				&& negation == other.negation;
	}
	
	public void accept(Visitor visitor) {
		visitor.visitEquivalent(this);
	}
	
	
	@Override
	public Equivalent clone() {
		Equivalent phi = new Equivalent(this.inner1.clone(), this.inner2.clone());
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
