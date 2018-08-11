/*
 * Nico Feld - 1169233
 */

package Dataflow;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import tripla.Code;
import tripla.SyntaxNode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class CFG extends DefaultDirectedGraph<CFGVertex, LabeledCFGEdge> {

    private class IDSet {
        private CFGVertex in;
        private CFGVertex out;

        public IDSet(CFGVertex in, CFGVertex out) {
            this.in = in;
            this.out = out;
        }

        public CFGVertex getIn() {
            return in;
        }

        public void setIn(CFGVertex in) {
            this.in = in;
        }

        public CFGVertex getOut() {
            return out;
        }

        public void setOut(CFGVertex out) {
            this.out = out;
        }
    }

    private HashMap<String, CFG> subCFGs;
    private CFGVertex in, out;
    private String name;

    public CFG(String name, SyntaxNode root, String LabelIn, String LabelOut, HashMap<String, CFG> subCFGs) {
        super(LabeledCFGEdge.class);

        this.name = name;
        this.subCFGs = new HashMap<>(subCFGs);

        in = new CFGVertex(null, CFGVertexType.doublecircle, LabelIn);
        this.addVertex(in);

        out = new CFGVertex(null, CFGVertexType.doublecircle, LabelOut);
        this.addVertex(out);

        CFGVertex main = buildSubGraph(root, in, "");
        this.addEdge(main, out);


        for (CFGVertex v : new ArrayList<>(this.vertexSet())) {
            if (v.getLabel().equals("")) {
                for (LabeledCFGEdge e1 : this.incomingEdgesOf(v)) {
                    for (LabeledCFGEdge e2 : this.outgoingEdgesOf(v)) {
                        this.addEdge(this.getEdgeSource(e1), this.getEdgeTarget(e2), new LabeledCFGEdge(e1.getLabel(), e1.isConstraint()));
                    }
                }
                this.removeVertex(v);
            }
        }
    }


    private CFGVertex buildSubGraph(SyntaxNode node, CFGVertex in, String edgeLabel) {
        switch (node.getSynCode()) {
            case OP_AND:
            case OP_OR:
            case OP_ADD:
            case OP_SUB:
            case OP_MULT:
            case OP_DIV:
            case OP_EQ:
            case OP_NEQ:
            case OP_GT:
            case OP_LT:
            case OP_GTE:
            case OP_LTE: {
                CFGVertex v1 = buildSubGraph(node.getNodes().get(0), in, edgeLabel);
                CFGVertex v2 = buildSubGraph(node.getNodes().get(1), v1, "");
                CFGVertex out = new CFGVertex(node, CFGVertexType.rectangle, v1.getLabel() + " " + node.getSynCode().getName() + " " + v2.getLabel());
                this.addVertex(out);
                this.addEdge(v2, out);
                return out;
            }
            case LET_IN: {
                CFGVertex def = buildSubGraph(node.getNodes().get(0), null, "");
                CFGVertex call = buildSubGraph(node.getNodes().get(1), in, edgeLabel);

                return call;
            }
            case IF_THEN_ELSE: {
                CFGVertex pre = buildSubGraph(node.getNodes().get(0), in, edgeLabel);

                CFGVertex e = new CFGVertex(node, CFGVertexType.diamond, "?");

                this.addVertex(e);

                addEdge(pre, e);

                CFGVertex t = buildSubGraph(node.getNodes().get(1), e, "true");
                CFGVertex f = buildSubGraph(node.getNodes().get(2), e, "false");

                CFGVertex out = new CFGVertex(node, CFGVertexType.circle, "");

                this.addVertex(out);

                addEdge(t, out);
                addEdge(f, out);

                return out;
            }
            case ASSIGN: {

                CFGVertex v1 = buildSubGraph(node.getNodes().get(1), in, edgeLabel);

                CFGVertex v2 = new CFGVertex(node, CFGVertexType.rectangle, (String) node.getNodes().get(0).getValue() + " = " + v1.getLabel());

                this.addVertex(v2);

                addEdge(v1, v2);

                return v2;
            }
            case CONST: {
                CFGVertex out = new CFGVertex(node, CFGVertexType.rectangle, ((Integer) node.getValue()).toString());
                this.addVertex(out);
                this.addEdge(in, out, new LabeledCFGEdge(edgeLabel, true));
                return out;
            }
            case ID: {
                CFGVertex out = new CFGVertex(node, CFGVertexType.rectangle, (String) node.getValue());
                this.addVertex(out);
                this.addEdge(in, out, new LabeledCFGEdge(edgeLabel, true));
                return out;
            }
            case COMMA: {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    CFGVertex v = buildSubGraph(n, tmp, edgeLabel);
                    edgeLabel = "";
                    tmp = v;
                }

                return tmp;
            }
            case BOOL: {
                CFGVertex out = new CFGVertex(node, CFGVertexType.rectangle, ((Boolean) node.getValue()) ? "true" : "false");
                this.addVertex(out);
                this.addEdge(in, out, new LabeledCFGEdge(edgeLabel, true));
                return out;
            }
            case DO_WHILE: {

                CFGVertex start = new CFGVertex(node, CFGVertexType.rectangle, "");
                this.addVertex(start);
                this.addEdge(in, start, new LabeledCFGEdge(edgeLabel, true));

                CFGVertex v1 = buildSubGraph(node.getNodes().get(0), start, "");
                CFGVertex v2 = buildSubGraph(node.getNodes().get(1), v1, "");

                CFGVertex end = new CFGVertex(node, CFGVertexType.diamond, "?");
                this.addVertex(end);

                CFGVertex out = new CFGVertex(node, CFGVertexType.circle, "");
                this.addVertex(out);

                this.addEdge(v2, end);
                this.addEdge(end, start, new LabeledCFGEdge("true", true));
                this.addEdge(end, out, new LabeledCFGEdge("false", true));

                return out;
            }
            case SEQUENCE: {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    CFGVertex v = buildSubGraph(n, tmp, edgeLabel);
                    //this.addEdge(tmp,v);
                    tmp = v;
                }

                return tmp;

            }
            case SEMICOLON: {

                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    CFGVertex v = buildSubGraph(n, tmp, edgeLabel);
                    edgeLabel = "";
                    tmp = v;
                }

                return tmp;
            }
            case PARENTHESES: {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    CFGVertex v = buildSubGraph(n, tmp, "");
                    this.addEdge(tmp, v, new LabeledCFGEdge(edgeLabel, true));
                    tmp = v;
                }

                return tmp;
            }
            case FUNCTION_CALL: {
                CFGVertex params = buildSubGraph(node.getNodes().get(1), in, edgeLabel);

                CFGVertex call = new CFGVertex(node, CFGVertexType.rectangle, "Call " + (String) node.getNodes().get(0).getValue());

                this.addVertex(call);

                CFGVertex ret = new CFGVertex(node, CFGVertexType.rectangle, "return " + (String) node.getNodes().get(0).getValue());

                this.addVertex(ret);

                this.addEdge(params, call);
                this.addEdge(call, ret);

                CFG sub = subCFGs.get((String) node.getNodes().get(0).getValue());

                this.addVertex(sub.getIn());
                this.addVertex(sub.getOut());

                this.addEdge(call, sub.getIn(), new LabeledCFGEdge("", false));

                this.addEdge(sub.getOut(), ret, new LabeledCFGEdge("", false));

                return ret;
            }
            case FUNCTION_DEFINITION: {

                String params = "";
                if (node.getNodes().get(1) != null) {
                    if (node.getNodes().get(1).getSynCode() == Code.COMMA) {
                        ArrayList<String> ids = new ArrayList<>();
                        for (SyntaxNode n : node.getNodes().get(1).getNodes()) {
                            ids.add((String) n.getValue());
                        }
                        params = String.join(",", ids);
                    } else {
                        params = (String) node.getNodes().get(1).getValue();
                    }
                }

                CFG subCFG = new CFG((String) node.getNodes().get(0).getValue(), node.getNodes().get(2), "Start " + (String) node.getNodes().get(0).getValue() + " (" + params + ")", "End " + (String) node.getNodes().get(0).getValue(), this.getSubCFGs());

                subCFG.getIn().setSyntaxNode(node);
                subCFG.getOut().setSyntaxNode(node);

                subCFGs.put((String) node.getNodes().get(0).getValue(), subCFG);

                return null;
            }
        }
        return null;
    }

    public void export(AbstractCFGExportStrategy exportStrategy) throws IOException {
        exportStrategy.export(new FileWriter("res/graph.dot"), this);
    }

    public CFGVertex getIn() {
        return in;
    }

    public CFGVertex getOut() {
        return out;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, CFG> getSubCFGs() {
        return subCFGs;
    }

    public void mergeWithSubGraphs() {
        for (CFG c : subCFGs.values()) {
            c.mergeWithSubGraphs();
            Graphs.addGraph(this, c);
        }
    }
}
