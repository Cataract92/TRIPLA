package Dataflow;

import tripla.Code;
import tripla.SyntaxNode;

import java.util.HashMap;
import java.util.HashSet;

public class ReachedUsesStrategy {

    private class Pair {
        private CFGVertex vertex;
        private String id;

        public Pair(CFGVertex vertex, String id) {
            this.vertex = vertex;
            this.id = id;
        }

        public CFGVertex getVertex() {
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

    private HashMap<CFGVertex, HashSet<Pair>> IN = new HashMap<>();
    private HashMap<CFGVertex, HashSet<Pair>> OUT = new HashMap<>();
    private HashMap<CFGVertex, HashSet<Pair>> GEN = new HashMap<>();
    private HashMap<CFGVertex, HashSet<Pair>> KILL = new HashMap<>();


    public void compute(CFG cfg) {
        IN.clear();
        OUT.clear();
        GEN.clear();
        KILL.clear();

        for (CFGVertex v : cfg.vertexSet()) {



            IN.put(v, new HashSet<>());
            OUT.put(v, new HashSet<>());
            GEN.put(v, new HashSet<>());
            KILL.put(v, new HashSet<>());

            if (v.getSyntaxNode() == null) continue;

            switch (v.getSyntaxNode().getSynCode()) {
                case ID: {
                    Pair p = new Pair(v, (String) v.getSyntaxNode().getValue());
                    IN.get(v).add(p);
                    GEN.get(v).add(p);
                    break;
                }

                case ASSIGN: {
                    KILL.get(v).add(new Pair(v, (String) v.getSyntaxNode().getValue()));
                    break;
                }
                case FUNCTION_DEFINITION: {
                    if (v.getSyntaxNode().getNodes().get(1).getSynCode() == Code.COMMA) {
                        for (SyntaxNode n : v.getSyntaxNode().getNodes().get(1).getNodes()) {
                            KILL.get(v).add(new Pair(v, (String) n.getValue()));
                        }
                    } else {
                        KILL.get(v).add(new Pair(v, (String) v.getSyntaxNode().getNodes().get(0).getValue()));
                    }
                }
            }
        }

        for (int i = 0; i < 1; i++) {
            for (CFGVertex v : cfg.vertexSet()) {
                if (v.getSyntaxNode() == null) continue;

                HashSet<Pair> allIN = new HashSet<>();

                for (LabeledCFGEdge e : cfg.outgoingEdgesOf(v)) {
                    if (cfg.getEdgeTarget(e).getSyntaxNode() == null)
                    {
                        for (LabeledCFGEdge e2 :cfg.outgoingEdgesOf(cfg.getEdgeTarget(e)))
                        {
                            allIN.addAll(IN.get(cfg.getEdgeTarget(e2)));
                        }
                    } else
                    {
                        allIN.addAll(IN.get(cfg.getEdgeTarget(e)));
                    }

                }

                OUT.put(v, allIN);

                HashSet<Pair> tmp = new HashSet<>(allIN);

                tmp.removeAll(KILL.get(v));

                tmp.addAll(GEN.get(v));

                IN.put(v, tmp);
            }
        }

        System.out.println("Done");
    }

}
