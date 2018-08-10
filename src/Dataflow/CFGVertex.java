package Dataflow;

import tripla.SyntaxNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CFGVertex {

    private SyntaxNode syntaxNode;
    private CFGVertexType type;
    private String label;
    private String style = "";

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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

}
