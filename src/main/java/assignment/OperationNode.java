package assignment;

public class OperationNode extends GeneralNode {
    private GeneralNode left;
    private GeneralNode right;

    public OperationNode(GeneralNode left, GeneralNode right) {
        this.left = left;
        this.right = right;
    }

    public GeneralNode getLeft() {
        return left;
    }

    public GeneralNode getRight() {
        return right;
    }
}

