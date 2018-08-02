/*
 * Nico Feld - 1169233
 */

package Dataflow;

import org.jgrapht.graph.DefaultDirectedGraph;
import tripla.SyntaxNode;

public class CFG extends DefaultDirectedGraph<AbstractCFGVertex,LabeledCFGEdge> implements ICFG {

    private class ResultSet<X,Y> {
        private X x;
        private Y y;

        public ResultSet(X in, Y out) {
            this.x = in;
            this.y = out;
        }

        public X getIn() {
            return x;
        }

        public Y getOut() {
            return y;
        }
    }


    private AbstractCFGVertex in, out;

    public CFG() {
        super(LabeledCFGEdge.class);
    }

    public void build(SyntaxNode root){
        buildSubGraph(root);
    }

    private CFGVertex buildSubGraph(SyntaxNode node)
    {
        return null;
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
