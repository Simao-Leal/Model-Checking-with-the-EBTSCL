/*
 * The EventTransitionSystem class used to be called ActionTransitionSystem.
 * This is why there are many references to action_statements, action_state, action_transitions, etc.
 * The classes in the AST are also called ActionStateNode, etc. It would be nice to change these all
 * to EventStateNode, etc. (basically replace action with event in many places) which I shall do when
 * I have time. But for now it seems like it would take a lot of time. As this code is not very substancial
 * within the thesis (it is literally just syntatic sugar), I will handle this later. 
 */

package claimlang.AST;

import java.util.Iterator;

public class ToJavaVisitor implements Visitor {
	
	/*
	 * Type checking would be nice, as it would
	 * prevent some errors at compile time, but it is 
	 * too much work, so type errors (like trying to
	 * assign an EventTS to a variable declared TS
	 * will appear at run time.
	 */
	
	public String file = "";
	private String className;
	private String state_id;
	private String TS_id;
	private int lvl = 0;
	
	
	public ToJavaVisitor(String className) {
		this.className = className;
	}

	private void write(String s) {
		for(int i = 0; i < lvl; i++) file += "\t";
		file += s;
	}
	
	private void writeln(String s) {
		for(int i = 0; i < lvl; i++) file += "\t";
		file += s + "\n";
	}

	@Override
	public void visitActionStateNode(ActionStateNode node) {
		writeln("/*    Begin State    */");
		writeln("{");
		lvl++;
		state_id = node.id;
		writeln("LocalIntStruc " + state_id + " = new LocalIntStruc();");
		for(BasicNode statement : node.claims) {
			statement.accept(this);
		}
		writeln(TS_id + (node.initial ? ".addInitialState(\"" : ".addState(\"") + state_id + "\", " + state_id + ");");
		lvl--;
		writeln("}");
		writeln("/*    End State    */");

	}

	@Override
	public void visitActionTransitionNode(ActionTransitionNode node) {
		Iterator<String> it = node.transitions.states.iterator();
		String source = it.next();
		while(it.hasNext()) {
			 String target = it.next();
			 if(node.event != null) {
				 writeln(TS_id + ".addTransition(\"" + source + "\", \"" + target + "\", new EventSymbol(\"" + node.event + "\"));");
			 } else {
				 writeln(TS_id + ".addTransition(\"" + source + "\", \"" + target + "\", null);");
			 }
			 source = target;
		}
	}

	@Override
	public void visitActionTransitionSystemNode(ActionTransitionSystemNode node) {
		writeln("/*    Begin Event Transition System    */");
		TS_id = node.id;
		lvl++;
		writeln("EventTransitionSystem " + TS_id + " = new EventTransitionSystem();");
		for(BasicNode statement : node.statements) {
			statement.accept(this);
		}
		lvl--;
		writeln("/*    End Event Transition System    */");
	}

	@Override
	public void visitClaimNode(ClaimNode node) {
		writeln(state_id + (node.isMinus ? ".addNegativeClaim" : ".addPositiveClaim")+"(new AgentSymbol(\"" +
				node.idAgent + "\"), new TimeSymbol(\"" + node.idTime +
				"\"), new PropositionalSymbol(\"" + node.idProp +
				"\"));");
	}

	@Override
	public void visitEventNode(EventNode node) {
		writeln(state_id + ".setEvent(new EventSymbol(\"" + node.id + "\"));");
	}
	
	@Override
	public void visitFileNode(FileNode node) {
		writeln("//The following file was automatically generated by the ClaimLang translator");
		writeln("");
		writeln("import model_checking.TransitionSystem;");
		writeln("import model_checking.EventTransitionSystem;");
		writeln("import formulas.Formula;");
		writeln("import logicHandlers.Sat;");
		writeln("import formulas.atemporal.AgentClaim;");
		writeln("import symbols.*;");
		writeln("import logicHandlers.LocalIntStruc;");
		
		writeln("public class " + className + "{");
		lvl++;
		writeln("public static void main(String[] args) {");
		lvl++;
		for(BasicNode declaration : node.declarations) {
			declaration.accept(this);
		}
		lvl--;
		writeln("}");//close main
		lvl--;
		writeln("}");//close class
	}

	@Override
	public void visitStateNode(StateNode node) {
		writeln("/*    Begin State    */");
		writeln("{");
		lvl++;
		state_id = node.id;
		writeln("LocalIntStruc " + state_id + " = new LocalIntStruc();");
		for(BasicNode statement : node.statements) {
			statement.accept(this);
		}
		writeln(TS_id + (node.initial ? ".addInitialState(\"" : ".addState(\"") + state_id + "\", " + state_id + ");");
		lvl--;
		writeln("}");
		writeln("/*    End State    */");
	}

	@Override
	public void visitTimeEqualsNode(TimeEqualsNode node) {
		writeln(TS_id + ".addTimeEqualsRelation(new TimeSymbol(\"" + node.idLeft + "\"), new TimeSymbol(\"" + node.idRight + "\"));");
	}

	@Override
	public void visitTimeLessThanNode(TimeLessThanNode node) {
		writeln(TS_id + ".addTimeLessThanRelation(new TimeSymbol(\"" + node.idLeft + "\"), new TimeSymbol(\"" + node.idRight + "\"));");
	}

	@Override
	public void visitTimeOrderNode(TimeOrderNode node) {
		writeln("/*    Begin Time Order    */");
		lvl++;
		for(TimeComparisonNode relation : node.relations) {
			relation.accept(this);
		}
		lvl--;
		writeln("/*    End Time Order    */");
	}

	@Override
	public void visitTransitionNode(TransitionNode node) {
		Iterator<String> it = node.states.iterator();
		String source = it.next();
		while(it.hasNext()) {
			 String target = it.next();
			 writeln(TS_id + ".addTransition(\"" + source + "\", \"" + target + "\");");
			 source = target;
		}
	}

	@Override
	public void visitTransitionSystemNode(TransitionSystemNode node) {
		writeln("/*    Begin Transition System    */");
		TS_id = node.id;
		lvl++;
		writeln("TransitionSystem " + TS_id + " = new TransitionSystem();");
		for(BasicNode statement : node.statements) {
			statement.accept(this);
		}
		lvl--;
		writeln("/*    End Transition System    */");
	}

	@Override
	public void visitTrustLessThanNode(TrustLessThanNode node) {
		writeln(TS_id + ".addTrustRelation(new AgentSymbol(\"" + node.idLeft + "\"), new PropositionalSymbol(\"" 
					+ node.idProp + "\"), new AgentSymbol(\"" + node.idRight + "\"));");
	}

	@Override
	public void visitTrustOrderNode(TrustOrderNode node) {
		writeln("/*    Begin Trust Order    */");
		lvl++;
		for(TrustLessThanNode relation : node.relations) {
			relation.accept(this);
		}
		lvl--;
		writeln("/*    End Trust Order    */");
	}

	@Override
	public void visitATSAssignmentNode(ATSAssignmentNode node) {
		write("EventTransitionSystem " + node.id + " = ");
		node.ats.accept(this);
		file += ";\n";
	}

	@Override
	public void visitCheckSatNode(CheckSatNode node) {
		write("Sat.displaySat(");
		node.formula.accept(this); 
		file += ");\n";
	}

	@Override
	public void visitCheckTSSatFormulaNode(CheckTSSatFormulaNode node) {
		System.out.println();
		write("System.out.println(\"Checking whether the transition system " + node.ts.print() + " satisfies the formula \" + ");
		node.formula.accept(this);
		file += ");\n";
		write(""); //because of indentation
		node.ts.accept(this);
		file += ".displaySat(";
		node.formula.accept(this);
		file += ");\n";
	}

	@Override
	public void visitCheckValNode(CheckValNode node) {
		write("Sat.displayVal(");
		node.formula.accept(this); 
		file += ");\n";
	}

	@Override
	public void visitFormulaAssignmentNode(FormulaAssignmentNode node) {
		write("Formula " + node.id + " = ");
		node.formula.accept(this);
		file += ";\n";
	}

	@Override
	public void visitFormulaLitaralNode(FormulaLiteralNode node) {
		file += "Formula.parse(\"" + node.formula + "\")";
	}

	@Override
	public void visitProductNode(ProductNode node) {
		node.left.accept(this); 
		file += ".product(";
		node.right.accept(this); 
		file += ")";
	}

	@Override
	public void visitRValueNode(RValueNode node) {
		file += node.id;
	}

	@Override
	public void visitTSAssignmentNode(TSAssignmentNode node) {
		write("TransitionSystem " + node.id + " = ");
		node.ts.accept(this);
		file += ";\n";
	}
}
