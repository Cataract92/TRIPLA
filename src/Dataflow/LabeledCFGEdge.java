package Dataflow;

public class LabeledCFGEdge {

    private String label;
    private CFGVertex source;
    private CFGVertex target;

    public LabeledCFGEdge() {
        label = "";
    }

    public LabeledCFGEdge(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public CFGVertex getSource() {
        return source;
    }

    public CFGVertex getTarget() {
        return target;
    }
}
