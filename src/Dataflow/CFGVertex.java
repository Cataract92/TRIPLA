package Dataflow;

import tripla.SyntaxNode;

public class CFGVertex {

    protected SyntaxNode syntaxNode;
    protected CFGVertexType type;
    protected String label;

    public CFGVertex(SyntaxNode node, CFGVertexType type, String label) {
        this.syntaxNode = node;
        this.type = type;
        this.label = label;
    }

    public SyntaxNode getSyntaxNode() {
        return syntaxNode;
    }

    public CFGVertexType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }
}
