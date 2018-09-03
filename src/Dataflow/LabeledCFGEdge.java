package Dataflow;

public class LabeledCFGEdge {

    private String label;
    private boolean constraint = true;
    private String color = "";
    private String style = "";

    public LabeledCFGEdge() {
        label = "";
    }

    public LabeledCFGEdge(String label, boolean constraint) {
        this.label = label;
        this.constraint = constraint;
    }

    public String getLabel() {
        return label;
    }

    public boolean isConstraint() {
        return constraint;
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
}
