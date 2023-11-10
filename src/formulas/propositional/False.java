package formulas.propositional;

import formulas.Formula;
import visitors.Visitor;

final public class False extends Formula{

	@Override
	public String preToString() {
		return "⊥";
	}
	
	@Override
	public False negate() {
		negation = !negation;
		return this;
	}
	
	@Override
	public False expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		False other = (False) obj;
		return negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitFalse(this);
	}
	
	@Override
	public False clone() {
		False phi = new False();
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
