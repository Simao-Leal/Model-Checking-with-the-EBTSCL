package formulas;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import formulas.propositional.And;
import formulas.propositional.False;
import formulas.propositional.Or;
import formulas.propositional.True;
import java.io.StringReader;

import java_cup.runtime.Symbol;
import parser.Scanner;
import parser.parser;
import symbols.*;

import visitors.DisplayTreeVisitor;
import visitors.LaTeXVisitor;
import visitors.ReplaceAgentVisitor;
import visitors.ReplaceTimeVisitor;
import visitors.Visitor;

public abstract class Formula implements Cloneable{
	
	public final static Signature sigma = new Signature();
	//The signature is common to all formulas, hence it is static. One may not
	//change the reference of the signature by hand so it is final
	protected boolean negation = false; //true if it is a negation
	protected boolean expanded = false; //true if already expanded (useful in world construction)

	
	public boolean isNegation() {
		return negation;
	}
	
	public boolean isExpanded() {
		return expanded;
	}

	abstract public Formula negate();
	
	abstract public Formula expand(); //sets expanded to true (one way)
	
	abstract protected String preToString();
		
	public Formula replace(TimeSymbol time1, TimeSymbol time2) {
		ReplaceTimeVisitor v = new ReplaceTimeVisitor(time1, time2);
		this.accept(v);
		return v.phi;
	}
	
	public Formula replace(PropositionalSymbol prop, AgentSymbol agent1, AgentSymbol agent2) {
		ReplaceAgentVisitor v = new ReplaceAgentVisitor(prop, agent1, agent2);
		this.accept(v);
		return v.phi;
	}
	
	public String display() {
		return DisplayTreeVisitor.DisplayTree(this);
	}
	@Override
	abstract public int hashCode();
	//We consider two formulas equal if they only differ on "expanded"
	
	@Override
	abstract public boolean equals(Object obj);
	//We consider two formulas equal if they only differ on "expanded"
	
	abstract public void accept(Visitor visitor);
	
	@Override
	abstract public Formula clone();

	@Override
	public String toString() {
		String s = "";
		if(expanded) s += "("; 
		if(negation) s += "¬";
		s += preToString();
		if(expanded) s += ")✓"; 
		return s;
	}
	
	public String toLaTeX() {
		return LaTeXVisitor.toLaTeX(this);
	}
	
	
	//Some useful functions
	
	public static Formula bigOr (Collection<Formula> col) {
		Iterator<Formula> it = col.iterator();
		if(!it.hasNext()) {
			return new False();
		}
		Formula a = it.next();
		if(!it.hasNext()) {
			return a;
		}
		Formula res = new Or(a, it.next());
		for(;it.hasNext();) {
			res = new Or(it.next(), res); 
		}
		return res;
	}
	
	public static Formula bigAnd (Collection<Formula> col) {
		Iterator<Formula> it = col.iterator();
		if(!it.hasNext()) {
			return new True();
		}
		Formula a = it.next();
		if(!it.hasNext()) {
			return a;
		}
		Formula res = new And(a, it.next());
		for(;it.hasNext();) {
			res = new And(it.next(), res); 
		}
		return res;
	}
	
	//Parser
	
	@SuppressWarnings("deprecation") //we are going for something that just works
	public static Formula parse(String input) {
		Scanner scanner = new Scanner(new StringReader(input));
        parser parser = new parser(scanner);
        try {
			Symbol result = parser.parse();
			return (Formula) result.value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
