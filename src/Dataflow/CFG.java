/*
 * Nico Feld - 1169233
 */

package Dataflow;

import Dataflow.Data.Edge;
import Dataflow.Data.Vertex;
import Dataflow.Data.VertexType;
import Dataflow.Export.AbstractExportStrategy;
import org.jgrapht.graph.DirectedMultigraph;
import tripla.Code;
import tripla.SyntaxNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CFG extends DirectedMultigraph<Vertex, Edge> {

    private class IDSet {
        private Vertex in;
        private Vertex out;

        public IDSet(Vertex in, Vertex out) {
            this.in = in;
            this.out = out;
        }

        public Vertex getIn() {
            return in;
        }

        public Vertex getOut() {
            return out;
        }

    }

    private HashMap<String, HashMap<String, IDSet>> functions = new HashMap<>();

    public CFG(SyntaxNode root, String LabelIn, String LabelOut) {
        super(Edge.class);

        this.functions.put("", new HashMap<>());

        Vertex in = new Vertex(null, VertexType.doublecircle, LabelIn);
        this.addVertex(in);

        Vertex out = new Vertex(null, VertexType.doublecircle, LabelOut);
        this.addVertex(out);

        Vertex main = buildSubGraph(root, in, "", "");
        this.addEdge(main, out);


        for (Vertex v : new ArrayList<>(this.vertexSet())) {
            if (v.getLabel().equals("")) {
                for (Edge e1 : this.incomingEdgesOf(v)) {
                    for (Edge e2 : this.outgoingEdgesOf(v)) {
                        this.addEdge(this.getEdgeSource(e1), this.getEdgeTarget(e2), new Edge(e1.getLabel(), e1.isConstraint()));
                    }
                }
                this.removeVertex(v);
            }
        }
    }

    private Vertex buildSubGraph(SyntaxNode node, Vertex in, String edgeLabel) {
        return buildSubGraph(node, in, edgeLabel, "");
    }

    private Vertex buildSubGraph(SyntaxNode node, Vertex in, String edgeLabel, String currentFunction) {
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
                Vertex v1 = buildSubGraph(node.getNodes().get(0), in, edgeLabel);
                Vertex v2 = buildSubGraph(node.getNodes().get(1), v1, "");
                Vertex out = new Vertex(node, VertexType.rectangle, v1.getLabel() + " " + node.getSynCode().getName() + " " + v2.getLabel());
                this.addVertex(out);
                this.addEdge(v2, out);
                return out;
            }
            case LET_IN: {
                Vertex def = buildSubGraph(node.getNodes().get(0), null, "");
                Vertex call = buildSubGraph(node.getNodes().get(1), in, edgeLabel);

                return call;
            }
            case IF_THEN_ELSE: {
                Vertex pre = buildSubGraph(node.getNodes().get(0), in, edgeLabel);

                Vertex e = new Vertex(node, VertexType.diamond, "?");

                this.addVertex(e);

                addEdge(pre, e);

                Vertex t = buildSubGraph(node.getNodes().get(1), e, "true");
                Vertex f = buildSubGraph(node.getNodes().get(2), e, "false");

                Vertex out = new Vertex(node, VertexType.circle, "");

                this.addVertex(out);

                addEdge(t, out);
                addEdge(f, out);

                return out;
            }
            case ASSIGN: {

                Vertex v1 = buildSubGraph(node.getNodes().get(1), in, edgeLabel);

                Vertex v2 = new Vertex(node, VertexType.rectangle, (String) node.getNodes().get(0).getValue() + " = " + v1.getLabel());

                this.addVertex(v2);

                addEdge(v1, v2);

                return v2;
            }
            case CONST: {
                Vertex out = new Vertex(node, VertexType.rectangle, ((Integer) node.getValue()).toString());
                this.addVertex(out);
                this.addEdge(in, out, new Edge(edgeLabel, true));
                return out;
            }
            case ID: {
                Vertex out = new Vertex(node, VertexType.rectangle, (String) node.getValue());
                this.addVertex(out);
                this.addEdge(in, out, new Edge(edgeLabel, true));
                return out;
            }
            case COMMA: {
                Vertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    Vertex v = buildSubGraph(n, tmp, edgeLabel);
                    edgeLabel = "";
                    tmp = v;
                }

                return tmp;
            }
            case BOOL: {
                Vertex out = new Vertex(node, VertexType.rectangle, ((Boolean) node.getValue()) ? "true" : "false");
                this.addVertex(out);
                this.addEdge(in, out, new Edge(edgeLabel, true));
                return out;
            }
            case DO_WHILE: {

                Vertex start = new Vertex(node, VertexType.rectangle, "");
                this.addVertex(start);
                this.addEdge(in, start, new Edge(edgeLabel, true));

                Vertex v1 = buildSubGraph(node.getNodes().get(0), start, "");
                Vertex v2 = buildSubGraph(node.getNodes().get(1), v1, "");

                Vertex end = new Vertex(node, VertexType.diamond, "?");
                this.addVertex(end);

                Vertex out = new Vertex(node, VertexType.circle, "");
                this.addVertex(out);

                this.addEdge(v2, end);
                this.addEdge(end, start, new Edge("true", true));
                this.addEdge(end, out, new Edge("false", true));

                return out;
            }
            case SEQUENCE: {
                Vertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    Vertex v = buildSubGraph(n, tmp, edgeLabel);
                    tmp = v;
                }

                return tmp;

            }
            case SEMICOLON: {

                Vertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    Vertex v = buildSubGraph(n, tmp, edgeLabel);
                    edgeLabel = "";
                    tmp = v;
                }

                return tmp;
            }
            case PARENTHESES: {
                Vertex tmp = in;
                for (SyntaxNode n : node.getNodes()) {
                    Vertex v = buildSubGraph(n, tmp, "");
                    this.addEdge(tmp, v, new Edge(edgeLabel, true));
                    tmp = v;
                }

                return tmp;
            }
            case FUNCTION_CALL: {
                Vertex params = buildSubGraph(node.getNodes().get(1), in, edgeLabel);

                Vertex call = new Vertex(node, VertexType.rectangle, "Call " + (String) node.getNodes().get(0).getValue());
                call.setColor("blue");
                call.setStyle("bold");
                this.addVertex(call);

                Vertex ret = new Vertex(node, VertexType.rectangle, "return " + (String) node.getNodes().get(0).getValue());
                ret.setColor("blue");
                ret.setStyle("bold");
                this.addVertex(ret);

                this.addEdge(params, call);
                this.addEdge(call, ret);

                IDSet set = this.functions.get(currentFunction).get((String) node.getNodes().get(0).getValue());

                this.addEdge(call, set.getIn());
                this.addEdge(set.getOut(), ret);


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

                Vertex start = new Vertex(node, VertexType.hexagon, "Start " + (String) node.getNodes().get(0).getValue() + " (" + params + ")");
                start.setColor("red");
                start.setStyle("bold");
                Vertex end = new Vertex(node, VertexType.hexagon, "End " + (String) node.getNodes().get(0).getValue());
                end.setColor("red");
                end.setStyle("bold");
                this.addVertex(start);
                this.addVertex(end);

                this.functions.get(currentFunction).put((String) node.getNodes().get(0).getValue(), new IDSet(start, end));

                this.functions.put((String) node.getNodes().get(0).getValue(), new HashMap<>(this.functions.get(currentFunction)));

                Vertex main = buildSubGraph(node.getNodes().get(2), start, "", (String) node.getNodes().get(0).getValue());

                this.addEdge(main, end);

                return end;
            }
        }
        return null;
    }

    public void export(AbstractExportStrategy exportStrategy, String output) {
        try {
            exportStrategy.export(new FileWriter(output), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
