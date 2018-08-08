package Dataflow;

public class LabeledCFGEdge {

    private String label;
    private boolean constraint = true;
    private String color = "black";

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
}
