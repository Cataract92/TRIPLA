/*
 * Nico Feld - 1169233
 */

package Dataflow;

import com.sun.xml.internal.bind.v2.model.core.ID;
import org.jgrapht.graph.DefaultDirectedGraph;
import tripla.SyntaxNode;

import java.util.HashMap;

public class CFG extends DefaultDirectedGraph<CFGVertex,LabeledCFGEdge> implements ICFG {

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

        in = new CFGVertex(null,CFGVertexType.cirlce,"in");

        this.addVertex(in);

        out = new CFGVertex(null,CFGVertexType.cirlce,"out");
        this.addVertex(out);

        CFGVertex v = buildSubGraph(root,in,"");
        this.addEdge(v,out);
    }

    private CFGVertex buildSubGraph(SyntaxNode node, CFGVertex in, String edgeLabel)
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
                CFGVertex v1 = buildSubGraph(node.getNodes().get(0),in,edgeLabel);
                CFGVertex v2 = buildSubGraph(node.getNodes().get(1),v1,"");
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle, v1.label+" "+node.getSynCode().name() + " " + v2.label);
                this.addVertex(out);
                this.addEdge(v2,out);
                return out;
            }
            case LET_IN:
            {
                CFGVertex def = buildSubGraph(node.getNodes().get(0),in,edgeLabel);
                CFGVertex call = buildSubGraph(node.getNodes().get(1),in,edgeLabel);

                return call;
            }
            case IF_THEN_ELSE:
            {
                CFGVertex pre = buildSubGraph(node.getNodes().get(0),in,edgeLabel);

                CFGVertex e = new CFGVertex(node,CFGVertexType.rectangle,"?");

                this.addVertex(e);

                addEdge(pre,e);

                CFGVertex t = buildSubGraph(node.getNodes().get(1),e,"true");
                CFGVertex f = buildSubGraph(node.getNodes().get(2),e,"false");

                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,"");

                this.addVertex(out);

                addEdge(t,out);
                addEdge(f,out);

                return out;
            }
            case ASSIGN:
            {

                CFGVertex v1 = buildSubGraph(node.getNodes().get(1),in,edgeLabel);

                CFGVertex v2 = new CFGVertex(node,CFGVertexType.rectangle, (String) node.getNodes().get(0).getValue() + " = " +  v1.label);

                this.addVertex(v2);

                addEdge(v1,v2);

                return v2;
            }
            case CONST:
            {
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,((Integer) node.getValue()).toString());
                this.addVertex(out);
                this.addEdge(in,out,new LabeledCFGEdge(edgeLabel));
                return out;
            }
            case ID:
            {
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,(String) node.getValue());
                this.addVertex(out);
                this.addEdge(in,out,new LabeledCFGEdge(edgeLabel));
                return out;
            }
            case COMMA:
            {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel);
                    edgeLabel = "";
                    this.addEdge(tmp,v);
                    tmp = v;
                }

                return tmp;
            }
            case BOOL:
            {
                CFGVertex out = new CFGVertex(node,CFGVertexType.rectangle,((Boolean) node.getValue())? "true" : "false");
                this.addVertex(out);
                this.addEdge(in,out,new LabeledCFGEdge(edgeLabel));
                return out;
            }
            case DO_WHILE:
            {
                break;
            }
            case SEQUENCE:
            {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel);
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
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel);
                    tmp = v;
                }

                return tmp;
            }
            case PARENTHESES:
            {
                CFGVertex tmp = in;
                for (SyntaxNode n : node.getNodes())
                {
                    CFGVertex v = buildSubGraph(n,tmp,edgeLabel);
                    this.addEdge(tmp,v,new LabeledCFGEdge(edgeLabel));
                    tmp = v;
                }

                return tmp;
            }
            case FUNCTION_CALL:
            {
                CFGVertex params = buildSubGraph(node.getNodes().get(1),in,edgeLabel);

                CFGVertex call = new CFGVertex(node,CFGVertexType.rectangle,"Call " +(String) node.getNodes().get(0).getValue());

                this.addVertex(call);

                CFGVertex ret = new CFGVertex(node,CFGVertexType.rectangle,"return " +(String) node.getNodes().get(0).getValue());

                this.addVertex(ret);

                this.addEdge(params,call);
                this.addEdge(call,ret);

                IDSet set = idMap.get((String) node.getNodes().get(0).getValue());

                this.addEdge(call,set.getIn());

                this.addEdge(set.getOut(),ret);

                return ret;
            }
            case FUNCTION_DEFINITION:
            {
                CFGVertex start = new CFGVertex(node,CFGVertexType.rectangle,"Start "+ (String) node.getNodes().get(0).getValue());

                this.addVertex(start);

                CFGVertex end = new CFGVertex(node,CFGVertexType.rectangle,"End "+ (String) node.getNodes().get(0).getValue());

                idMap.put((String)node.getNodes().get(0).getValue(),new IDSet(start,end));

                this.addVertex(end);

                CFGVertex body = buildSubGraph(node.getNodes().get(2),start,edgeLabel);

                this.addEdge(body,end);



                return end;
            }
        }
        return null;
    }

    @Override
    public CFGVertex getIn() {
        return in;
    }

    @Override
    public CFGVertex getOut() {
        return out;
    }

    @Override
    public void setIn(CFGVertex in) {
        this.in = in;
    }

    @Override
    public void setOut(CFGVertex out) {
        this.out = out;
    }

    @Override
    public void export(AbstractCFGExportStrategy exportStrategy) {

    }
}
