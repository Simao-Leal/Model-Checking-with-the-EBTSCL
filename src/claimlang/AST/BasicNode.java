package claimlang.AST;

public interface BasicNode {
	/*super class for every node*/
	public void accept(Visitor v);
}
