package claimlang.AST;

public class ProductNode implements PrintableNode {

	PrintableNode left;
	PrintableNode right;
	
	public ProductNode(PrintableNode left, PrintableNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public void accept(Visitor v) {
		v.visitProductNode(this);
	}

	@Override
	public String print() {
		return left.print() + " ⨂ " + right.print();
	}

}
