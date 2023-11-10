package formulas.propositional;

import formulas.Formula;
import visitors.Visitor;

final public class True extends Formula{

	@Override
	public String preToString() {
		return "⊤";
	}
	
	@Override
	public True negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public True expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		True other = (True) obj;
		return negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitTrue(this);
	}
	
	@Override
	public True clone() {
		True phi = new True();
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
