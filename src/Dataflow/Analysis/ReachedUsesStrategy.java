/*
 * Nico Feld - 1169233
 */

package Dataflow.Analysis;

import Dataflow.CFG;
import Dataflow.Data.Edge;
import Dataflow.Data.Vertex;
import tripla.Code;
import tripla.SyntaxNode;

import java.util.*;

public class ReachedUsesStrategy {

    private class Pair {
        private Vertex vertex;
        private String id;

        public Pair(Vertex vertex, String id) {
            this.vertex = vertex;
            this.id = id;
        }

        public Vertex getVertex() {
            return vertex;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Pair) {
                return this.id.equals(((Pair) o).id) && this.vertex == ((Pair) o).vertex;
            }
            return super.equals(o);
        }
    }

    private HashMap<Vertex, HashSet<Pair>> IN = new HashMap<>();
    private HashMap<Vertex, HashSet<Pair>> OUT = new HashMap<>();
    private HashMap<Vertex, HashSet<Pair>> GEN = new HashMap<>();
    private HashMap<Vertex, List<String>> KILL = new HashMap<>();


    public void compute(CFG cfg) {
        IN.clear();
        OUT.clear();
        GEN.clear();
        KILL.clear();

        for (Vertex v : cfg.vertexSet()) {

            if (v.getSyntaxNode() == null) continue;

            IN.put(v,new HashSet<>());
            OUT.put(v,new HashSet<>());
            GEN.put(v,new HashSet<>());
            KILL.put(v,new LinkedList<>());


            switch (v.getSyntaxNode().getSynCode()) {
                case ID: {
                    Pair p = new Pair(v, (String) v.getSyntaxNode().getValue());
                    IN.get(v).add(p);
                    GEN.get(v).add(p);
                    break;
                }

                case ASSIGN: {
                    KILL.get(v).add((String) v.getSyntaxNode().getNodes().get(0).getValue());
                    break;
                }
                case FUNCTION_DEFINITION: {
                    if (v.getSyntaxNode().getNodes().get(1).getSynCode() == Code.COMMA) {
                        for (SyntaxNode n : v.getSyntaxNode().getNodes().get(1).getNodes()) {
                            KILL.get(v).add((String) n.getValue());
                        }
                    } else {
                        KILL.get(v).add((String) v.getSyntaxNode().getNodes().get(1).getValue());
                    }
                }
            }
        }

        for (int i = 0; i < 1000; i++) {
            for (Vertex v : cfg.vertexSet()) {

                if (v.getSyntaxNode() == null) continue;

                HashSet<Pair> allIN = new HashSet<>();

                for (Vertex suc : getValidSuccessors(v,cfg)) {
                        allIN.addAll(IN.get(suc));
                }

                OUT.put(v, allIN);

                HashSet<Pair> tmp = new HashSet<>(allIN);

                allIN.stream().filter(pair -> KILL.get(v).contains(pair.id)).forEach(tmp::remove);

                tmp.addAll(GEN.get(v));

                IN.put(v, tmp);
            }
        }

        for (Vertex v : cfg.vertexSet())
        {
            if (v.getSyntaxNode() == null)
                continue;

            if ( !((v.getSyntaxNode().getSynCode() == Code.FUNCTION_DEFINITION && v.getLabel().startsWith("Start")) || v.getSyntaxNode().getSynCode() == Code.ASSIGN))
                continue;


            for (Pair pair : OUT.get(v))
            {
                if (KILL.get(v).contains(pair.id))
                {
                    Edge e = new Edge();
                    e.setStyle("dashed");
                    e.setConstraint(false);
                    e.setColor("green");
                    cfg.addEdge(v,pair.vertex,e);
                }
            }

        }

    }

    private ArrayList<Vertex> getValidSuccessors(Vertex root, CFG cfg)
    {
        ArrayList<Vertex> list = new ArrayList<>();
        for (Edge e : cfg.outgoingEdgesOf(root))
        {
            Vertex target = cfg.getEdgeTarget(e);
            if (target.getSyntaxNode() == null){
                list.addAll(getValidSuccessors(target,cfg));
            } else
            {
                list.add(target);
            }
        }
        return list;
    }

}
