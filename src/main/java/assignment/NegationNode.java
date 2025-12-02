package assignment;

public class NegationNode extends GeneralNode {
    private GeneralNode childNode;

    public NegationNode(GeneralNode node) {
        super("!");
        this.childNode = node;
    }

    public GeneralNode getChildNode() {
        return childNode;
    }
}
