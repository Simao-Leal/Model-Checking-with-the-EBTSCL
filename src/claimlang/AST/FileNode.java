package claimlang.AST;

import java.util.List;

public class FileNode implements BasicNode {
	
	List<BasicNode> declarations;

	
	public FileNode(List<BasicNode> declarations) {
		this.declarations = declarations;
	}

	@Override
	public void accept(Visitor v) {
		v.visitFileNode(this);
	}
}
