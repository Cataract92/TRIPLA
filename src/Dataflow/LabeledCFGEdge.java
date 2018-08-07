package Dataflow;

public class LabeledCFGEdge {

    private String label;
    private boolean constraint = true;

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
}
