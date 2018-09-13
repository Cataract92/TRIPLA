/*
 * Nico Feld - 1169233
 */

package Dataflow.Data;

import java.util.ArrayList;

public class Edge {

    private String label;
    private boolean constraint = true;
    private String color = "black";
    private String style = "";

    public Edge() {
        label = "";
    }

    public Edge(String label, boolean constraint) {
        this.label = label;
        this.constraint = constraint;
    }

    public String getLabel() {
        return label;
    }

    public boolean isConstraint() {
        return constraint;
    }

    public void setConstraint(boolean constraint) {
        this.constraint = constraint;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String toString() {
        ArrayList<String> attributes = new ArrayList<>();

        if (!getLabel().equals(""))
            attributes.add("label = \"" + getLabel() + "\"");

        if (!isConstraint())
            attributes.add("constraint = \"false\"");

        if (!getColor().equals("black"))
            attributes.add("color = \"" + getColor() + "\"");

        if (!getStyle().equals(""))
            attributes.add("style = \"" + getStyle() + "\"");

        if (attributes.isEmpty())
            return "";

        return "[" + String.join(",", attributes) + "]";
    }
}
