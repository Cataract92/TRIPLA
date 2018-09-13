/*
 * Nico Feld - 1169233
 */

package Dataflow.Data;

import tripla.SyntaxNode;

import java.util.ArrayList;

public class Vertex {

    private SyntaxNode syntaxNode;
    private VertexType type;
    private String label = "";
    private String style = "";
    private String color = "black";

    public Vertex(SyntaxNode node, VertexType type, String label) {
        this.syntaxNode = node;
        this.type = type;
        this.label = label;
    }

    public SyntaxNode getSyntaxNode() {
        return syntaxNode;
    }

    public VertexType getType() {
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSyntaxNode(SyntaxNode syntaxNode) {
        this.syntaxNode = syntaxNode;
    }

    @Override
    public String toString() {
        ArrayList<String> attributes = new ArrayList<>();

        if (!getLabel().equals(""))
            attributes.add("label = \"" + getLabel() + "\"");

        attributes.add("shape = \"" + getType() + "\"");

        if (!getColor().equals("black"))
            attributes.add("color = \"" + getColor() + "\"");

        if (!getStyle().equals(""))
            attributes.add("style = \"" + getStyle() + "\"");

        return "[" + String.join(",", attributes) + "]";
    }
}
