package logicHandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import data_structures.*;
import formulas.*;
import formulas.atemporal.*;
import symbols.*;

public class LocalIntStruc {
	/*
	T is the type of the elements of D_T
	i.e. the type of the interpretation of the time points
	might be int, double, string, etc.
	we restrict this to types which implement comparable
	so that we may use the embedded order, but we can opt out of it
	*/
	
	EventSymbol event;
	Set<AgentClaim> claims = new HashSet<>();
	
	
	//methods for building the interpretation structure
	public LocalIntStruc() {}
	
	public void addPositiveClaim(AgentSymbol agent, TimeSymbol time, PropositionalSymbol prop) {
		claims.add(new AgentClaim(agent, new Claim(time, prop)));
	}
	
	public void addNegativeClaim(AgentSymbol agent, TimeSymbol time, PropositionalSymbol prop) {
		claims.add(new AgentClaim(agent, new Claim(time, prop).invert()));
	}
	
	public void addClaim(AgentClaim claim) {
		claims.add(claim);
	}

	public void setEvent(EventSymbol event) {
		this.event = event;
	}
	
	public Set<AgentClaim> getClaims() {
		return claims;
	}

	public EventSymbol getEvent() {
		return event;
	}
	
	public LocalIntStruc union(LocalIntStruc other) {
		LocalIntStruc res = new LocalIntStruc();
		assert this.getEvent() == null || other.getEvent() == null || this.getEvent() == other.getEvent();
		//in case one of the events is null, we pick the more restrictive event
		res.setEvent(this.getEvent() == null ? other.getEvent() : this.getEvent());
		for(AgentClaim claim : this.getClaims()) {
			res.addClaim(claim);
		}
		for(AgentClaim claim : other.getClaims()) {
			res.addClaim(claim);
		}
		return res;
	}
	
	public int agentClaim(AgentSymbol agent, TimeSymbol time, PropositionalSymbol prop) {
		//returns 1 if a:(t.p) and -1 if a:-(t.p). Returns 0 if neither
		if(claims.contains(new AgentClaim(agent, new Claim(time, prop)))) {
			return 1;
		} else if(claims.contains(new AgentClaim(agent, new Claim(time, prop).invert()))) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public int agentClaim(AgentSymbol agent, Claim claim) {
		//returns 1 if a:(t.p) and -1 if a:-(t.p). Returns 0 if neither
		return agentClaim(agent, claim.getTime(), claim.getProp());
	}
	
	
	public boolean locallySat(SetFormulas delta) {
		/*
		 * The purpose of this function is to check if a state's label, I, locally satisfies some delta.
		 * This function is supposed to be used only when delta is in C(Omega).
		 * That is, we assume that delta enjoys operator closure.
		 * The function works by creating a auxiliary set C, importing all claims of I to C,
		 * importing all the relations of Delta to C. Then applying OperatorClosure to C and
		 * finally checking if the agent claims in C are compatible with the claims of delta
		 * that is, if (a : phi) in delta then (a : phi) must be in C 
		 * and if (not a : phi) in delta then (a : phi) must not be in C
		 */

		SetFormulas C = new SetFormulas();
		C.addAll(this.claims);
		for(Formula phi : delta) {
			if(!phi.isNegation()) {
				if(phi instanceof Event) {
					if(this.getEvent() != null && !this.getEvent().equals(((Event) phi).getSymbol())) {
						return false;
					}
				} else if(phi instanceof TimeLessThan || phi instanceof TimeEquals || phi instanceof LessTrustworthy) {
					C.add(phi);
				}
			}
		}
		C = World.operatorClosure(C);
		
		//checking that the state label is consistent with itself
		if(Sat.locallyIncompatibleQ(C)) return false;
		
		for(Formula phi : delta) {
			if(phi instanceof AgentClaim) {
				if(phi.isNegation()) {
					if(C.contains(phi.clone().negate())) {
						return false;
					}
				} else {
					if(!C.contains(phi)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		String s;
		s = "Local Interpretation Structure:\n";
	
		s += "Event: " + this.getEvent() + "\n";
		s += "Claims: ";
		for(AgentClaim claim : claims) {
			s += claim + ", ";
		}
		return s;
	}

	public static void main(String[] args) {
		AgentClaim a = (AgentClaim) Formula.parse("a: - t . p");
		LocalIntStruc lis = new LocalIntStruc();
		lis.addClaim(a);
		
		System.out.println(lis);
	}
}
