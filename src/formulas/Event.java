package formulas;

import java.util.Objects;

import symbols.EventSymbol;
import visitors.Visitor;

final public class Event extends Formula{
	
	private EventSymbol event;
	
	public EventSymbol getSymbol() {
		return event;
	}

	public Event(EventSymbol event) {
		this.event = event;
	}

	@Override
	public String preToString() {
		return event.toString();
	}

	@Override
	public Event negate() {
		negation = !negation;
		return this;
	}

	@Override
	public Event expand() {
		expanded = true;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(event, negation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return Objects.equals(event, other.event) && negation == other.negation;
	}

	public void accept(Visitor visitor) {
		visitor.visitEvent(this);
	}

	@Override
	public Event clone() {
		Event phi = new Event(this.event);
		phi.negation = this.negation;
		phi.expanded = this.expanded;
		return phi;
	}
}
