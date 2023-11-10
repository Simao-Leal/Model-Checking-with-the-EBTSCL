package symbols;

import java.util.HashSet;
import java.util.Set;

public class Signature {
	private Set<PropositionalSymbol> propositionalSymbols = new HashSet<>();
	private Set<EventSymbol> eventSymbols = new HashSet<>();
	private Set<TimeSymbol> timeSymbols = new HashSet<>();
	private Set<AgentSymbol> agentSymbols = new HashSet<>();
	
	public Set<PropositionalSymbol> getPropositionalSymbols() {
		return propositionalSymbols;
	}

	public Set<EventSymbol> getEventSymbols() {
		return eventSymbols;
	}

	public Set<TimeSymbol> getTimeSymbols() {
		return timeSymbols;
	}

	public Set<AgentSymbol> getAgentSymbols() {
		return agentSymbols;
	}
}
