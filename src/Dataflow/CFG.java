/*
 * Nico Feld - 1169233
 */

package Dataflow;

import com.sun.xml.internal.bind.v2.model.core.ID;
import org.jgrapht.graph.DefaultDirectedGraph;
import tripla.SyntaxNode;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CFG extends DefaultDirectedGraph<CFGVertex,LabeledCFGEdge>{

    private class IDSet
    {
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

    private HashMap<String,IDSet> idMap = new HashMap<>();
    private CFGVertex in, out;

    public CFG() {
        super(LabeledCFGEdge.class);
    }

    public void build(SyntaxNode root){

        in = new CFGVertex(null,CFGVertexType.doublecircle,"in","");

        this.addVertex(in);

        out = new CFGVertex(null,CFGVertexType.doublecircle,"out","");
        this.addVertex(out);

        CFGVertex main = buildSubGraph(root,in,"","");
        this.addEdge(main,out);


        for (String graph : CFGVertex.getGraphList().keySet()) {
            for (CFGVertex v : new ArrayList<>(CFGVertex.getGraphList().get(graph))) {
                if (v.getLabel().equals("")) {

                    for (LabeledCFGEdge e1 : this.incomingEdgesOf(v)) {
                        for (LabeledCFGEdge e2 : this.outgoingEdgesOf(v)) {
                            this.addEdge(this.getEdgeSource(e1), this.getEdgeTarget(e2), new LabeledCFGEdge(e1.getLabel(), e1.isConstraint()));
                        }
                    }

                    this.removeVertex(v);
                    CFGVertex.getGraphList().get(graph).remove(v);
                }
            }
        }

    }

    private CFGVertex buildSubGraph(SyntaxNode node, CFGVertex in, String edgeLabel, String subGraph)
    {
        switch (node.getSynCode())
        {
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
            case OP_LTE:
            {
                CFGVertex v1 = buildSubGraph(node.getNodes().get(0),in,edgeLabel,subGraph);
                CFGVertex v2 = buildSubGraph(node.getNodes().get(1),v1,"",subGraph);
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle, v1.getLabel()+" "+node.getSynCode().name() + " " + v2.getLabel(),subGraph);
                this.addVertex(out);
                this.addEdge(v2,out);
                return out;
            }
            case LET_IN:
            {
                CFGVertex def = buildSubGraph(node.getNodes().get(0),in,"",subGraph);
                CFGVertex call = buildSubGraph(node.getNodes().get(1),in,edgeLabel,subGraph);

                return call;
            }
            case IF_THEN_ELSE:
            {
                CFGVertex pre = buildSubGraph(node.getNodes().get(0),in,edgeLabel,subGraph);

                CFGVertex e = new CFGVertex(node,CFGVertexType.diamond,"?",subGraph);

                this.addVertex(e);

                addEdge(pre,e);

                CFGVertex t = buildSubGraph(node.getNodes().get(1),e,"true",subGraph);
                CFGVertex f = buildSubGraph(node.getNodes().get(2),e,"false",subGraph);

                CFGVertex out = new CFGVertex(node,CFGVertexType.circle,"",subGraph);

                this.addVertex(out);

                addEdge(t,out);
                addEdge(f,out);

                return out;
            }
            case ASSIGN:
            {

                CFGVertex v1 = buildSubGraph(node.getNodes().get(1),in,edgeLabel,subGraph);

                CFGVertex v2 = new CFGVertex(node,CFGVertexType.rectangle, (String) node.getNodes().get(0).getValue() + " = " +  v1.getLabel(),subGraph);

                this.addVertex(v2);

                addEdge(v1,v2);

                return v2;
            }
            case CONST:
            {
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,((Integer) node.getValue()).toString(),subGraph);
                this.addVertex(out);
                this.addEdge(in,out,new LabeledCFGEdge(edgeLabel,true));
                return out;
            }
            case ID:
            {
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,(String) node.getValue(),subGraph);
                this.addVertex(out);
                this.addEdge(in,out,new LabeledCFGEdge(edgeLabel,true));
                return out;
            }
            case COMMA:
            {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel,subGraph);
                    edgeLabel = "";
                    this.addEdge(tmp,v);
                    tmp = v;
                }

                return tmp;
            }
            case BOOL:
            {
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,((Boolean) node.getValue())? "true" : "false",subGraph);
                this.addVertex(out);
                this.addEdge(in,out,new LabeledCFGEdge(edgeLabel,true));
                return out;
            }
            case DO_WHILE:
            {

                CFGVertex start = new CFGVertex(node,CFGVertexType.rectangle,"",subGraph);
                this.addVertex(start);
                this.addEdge(in,start,new LabeledCFGEdge(edgeLabel,true));

                CFGVertex v1 = buildSubGraph(node.getNodes().get(0),start,"",subGraph);
                CFGVertex v2 = buildSubGraph(node.getNodes().get(1),v1,"",subGraph);

                CFGVertex end = new CFGVertex(node,CFGVertexType.diamond,"?",subGraph);
                this.addVertex(end);

                CFGVertex out = new CFGVertex(node,CFGVertexType.circle,"",subGraph);
                this.addVertex(out);

                this.addEdge(v2,end);
                this.addEdge(end,start,new LabeledCFGEdge("true",true));
                this.addEdge(end,out,new LabeledCFGEdge("false",true));

                return out;
            }
            case SEQUENCE:
            {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel,subGraph);
                    //this.addEdge(tmp,v);
                    tmp = v;
                }

                return tmp;

            }
            case SEMICOLON:
            {

                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel,subGraph);
                    tmp = v;
                }

                return tmp;
            }
            case PARENTHESES:
            {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel,subGraph);
                    this.addEdge(tmp,v,new LabeledCFGEdge(edgeLabel,true));
                    tmp = v;
                }

                return tmp;
            }
            case FUNCTION_CALL:
            {
                CFGVertex params = buildSubGraph(node.getNodes().get(1),in,edgeLabel,subGraph);

                CFGVertex call = new CFGVertex(node,CFGVertexType.rectangle,"Call " +(String) node.getNodes().get(0).getValue(),subGraph);

                this.addVertex(call);

                CFGVertex ret = new CFGVertex(node,CFGVertexType.rectangle,"return " +(String) node.getNodes().get(0).getValue(),subGraph);

                this.addVertex(ret);

                this.addEdge(params,call);
                this.addEdge(call,ret);

                IDSet set = idMap.get((String) node.getNodes().get(0).getValue());

                this.addEdge(call,set.getIn(),new LabeledCFGEdge("",false));

                this.addEdge(set.getOut(),ret,new LabeledCFGEdge("",false));

                return ret;
            }
            case FUNCTION_DEFINITION:
            {
                CFGVertex start = new CFGVertex(node,CFGVertexType.rectangle,"Start "+ (String) node.getNodes().get(0).getValue(),(String) node.getNodes().get(0).getValue());

                this.addVertex(start);

                CFGVertex end = new CFGVertex(node,CFGVertexType.rectangle,"End "+ (String) node.getNodes().get(0).getValue(),(String) node.getNodes().get(0).getValue());

                idMap.put((String)node.getNodes().get(0).getValue(),new IDSet(start,end));

                this.addVertex(end);

                CFGVertex body = buildSubGraph(node.getNodes().get(2),start,edgeLabel,(String) node.getNodes().get(0).getValue());

                this.addEdge(body,end);

                return end;
            }
        }
        return null;
    }

    public void export(AbstractCFGExportStrategy exportStrategy) {
        exportStrategy.export(new PrintWriter(System.out),this);
    }
}
