/*
 * Nico Feld - 1169233
 */

package Dataflow;

import org.jgrapht.graph.DefaultDirectedGraph;

public class CFG extends DefaultDirectedGraph<AbstractCFGVertex,LabeledCFGEdge> implements ICFG {


    private AbstractCFGVertex in, out;

    public CFG(Class<? extends LabeledCFGEdge> aClass) {
        super(aClass);
    }

    @Override
    public AbstractCFGVertex getIn() {
        return in;
    }

    @Override
    public AbstractCFGVertex getOut() {
        return out;
    }

    @Override
    public void setIn(AbstractCFGVertex in) {
        this.in = in;
    }

    @Override
    public void setOut(AbstractCFGVertex out) {
        this.out = out;
    }

    @Override
    public void export(AbstractCFGExportStrategy exportStrategy) {

    }
}
