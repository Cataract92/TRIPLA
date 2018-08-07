package Dataflow;

import tripla.SyntaxNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CFGVertex {

    private static HashMap<String,ArrayList<CFGVertex>> graphList = new HashMap<>();

    private SyntaxNode syntaxNode;
    private CFGVertexType type;
    private String label;
    private String subGraph;
    private String style = "";

    public CFGVertex(SyntaxNode node, CFGVertexType type, String label, String subGraph) {
        this.syntaxNode = node;
        this.type = type;
        this.label = label;
        this.subGraph = subGraph;
        if (!graphList.containsKey(subGraph))
            graphList.put(subGraph,new ArrayList<>());

        graphList.get(subGraph).add(this);
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

    public String getSubGraph() {
        return subGraph;
    }

    public static HashMap<String, ArrayList<CFGVertex>> getGraphList() {
        return graphList;
    }
}
