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
        return null;
    }

    @Override
    public AbstractCFGVertex getOut() {
        return null;
    }

    @Override
    public void setIn(AbstractCFGVertex in) {

    }

    @Override
    public void setOut(AbstractCFGVertex out) {

    }

    @Override
    public void export(AbstractCFGExportStrategy exportStrategy) {

    }
}
