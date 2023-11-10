package tests;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import formulas.*;
import formulas.atemporal.*;
import formulas.LTL.*;
import formulas.propositional.*;
import logicHandlers.SetFormulas;
import logicHandlers.SetSetFormulas;
import logicHandlers.World;
//import logicHandlers.Sat;
import symbols.*;

public class Test {
//	//a big formula with every component
//	final static Formula oneFormulaToRuleThemAll = 
//		new And(
//			new And(
//				new Equivalent(
//					new Always(
//						new Or(
//							new TimeEquals(new TimeSymbol("t1"), new TimeSymbol("t2")),
//							new Until(
//								new LessTrustworthy(new AgentSymbol("a1"), new PropositionalSymbol("p1"), new AgentSymbol("a2")),
//								new MostTrustworthy(
//									new AgentSymbol("a1"),
//									new Claim(new TimeSymbol("t2"), new PropositionalSymbol("p2")).invert()
//								).negate()
//							)
//						)
//					).negate(),
//					new Eventually(
//						new AgentClaim(
//							new AgentSymbol("a1"),
//							new Claim(new TimeSymbol("t1"), new PropositionalSymbol("p1"))
//						)
//					)
//				),
//				new Implies(
//					new Next(
//						new Event(new EventSymbol("e"))
//					),
//					new TimeLessThan(new TimeSymbol("t2"), new TimeSymbol("t3"))
//				)
//			),
//			new Implies(
//				new True(),
//				new Until(
//						new Claim(new TimeSymbol("t3"), new PropositionalSymbol("p2")).invert(),
//					new False()
//				)
//			)
//		);
	public static void main(String[] args) {
		Formula phi1 = new MostTrustworthy(new AgentSymbol("a1"), new Claim(new TimeSymbol("t1"), new PropositionalSymbol("p1")));
		Formula phi = Formula.parse("(G F crime) implies (F(HR : afternoon . SJ_guilty and SJ : afternoon . HR_guilty))");
		System.out.println(phi.display());

	}
}
