import java.util.ArrayList;

// this is a tree data structure that is used when looking for the best move to take
public class treeNode {
	int eval;
	ArrayList<treeNode> children;
	ArrayList<int[]> board;

	public treeNode(ArrayList<int[]> board, int eval) {
		this.eval = eval;
		this.board = board;
		children = new ArrayList<treeNode>();
	}

	public treeNode(ArrayList<int[]> board) {
		this.board = board;
		children = new ArrayList<treeNode>();
	}

	public void setChildren(ArrayList<int[]> children) {
		children.addAll(children);
	}
}
