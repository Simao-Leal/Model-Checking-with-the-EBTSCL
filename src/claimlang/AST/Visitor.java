package claimlang.AST;

public interface Visitor {

	public void visitActionStateNode(ActionStateNode node);
	public void visitActionTransitionNode(ActionTransitionNode node);
	public void visitActionTransitionSystemNode(ActionTransitionSystemNode node);
	public void visitATSAssignmentNode(ATSAssignmentNode node);
	public void visitCheckSatNode(CheckSatNode node);
	public void visitCheckTSSatFormulaNode(CheckTSSatFormulaNode node);
	public void visitCheckValNode(CheckValNode node);
	public void visitClaimNode(ClaimNode node);
	public void visitEventNode(EventNode node);
	public void visitFileNode(FileNode node);
	public void visitFormulaAssignmentNode(FormulaAssignmentNode node);
	public void visitFormulaLitaralNode(FormulaLiteralNode node);
	public void visitProductNode(ProductNode node);
	public void visitRValueNode(RValueNode node);
	public void visitStateNode(StateNode node);
	public void visitTimeEqualsNode(TimeEqualsNode node);
	public void visitTimeLessThanNode(TimeLessThanNode node);
	public void visitTimeOrderNode(TimeOrderNode node);
	public void visitTransitionNode(TransitionNode node);
	public void visitTransitionSystemNode(TransitionSystemNode node);
	public void visitTrustLessThanNode(TrustLessThanNode node);
	public void visitTrustOrderNode(TrustOrderNode node);
	public void visitTSAssignmentNode(TSAssignmentNode node);
}
