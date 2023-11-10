package symbols;

import java.util.Objects;

abstract class Symbol { //may not be refered to
	
	String representation;

	@Override
	public String toString() {
		return representation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(representation); //FIXME this is not coherent with equals if you have different type symbols with the same representation (edge case)
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symbol other = (Symbol) obj;
		return Objects.equals(representation, other.representation);
	}
}
