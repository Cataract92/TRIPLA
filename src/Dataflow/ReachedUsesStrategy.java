package Dataflow;

import tripla.Code;
import tripla.SyntaxNode;

import java.util.*;

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
    private HashMap<CFGVertex, List<String>> KILL = new HashMap<>();


    public void compute(CFG cfg) {
        IN.clear();
        OUT.clear();
        GEN.clear();
        KILL.clear();

        for (CFGVertex v : cfg.vertexSet()) {

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
            for (CFGVertex v : cfg.vertexSet()) {

                if (v.getSyntaxNode() == null) continue;

                HashSet<Pair> allIN = new HashSet<>();

                for (CFGVertex suc : getValidSuccessors(v,cfg)) {
                        allIN.addAll(IN.get(suc));
                }

                OUT.put(v, allIN);

                HashSet<Pair> tmp = new HashSet<>(allIN);

                allIN.stream().filter(pair -> KILL.get(v).contains(pair.id)).forEach(tmp::remove);

                tmp.addAll(GEN.get(v));

                IN.put(v, tmp);
            }
        }

        for (CFGVertex v : cfg.vertexSet())
        {
            if (v.getSyntaxNode() == null)
                continue;

            if ( !((v.getSyntaxNode().getSynCode() == Code.FUNCTION_DEFINITION && v.getLabel().startsWith("Start")) || v.getSyntaxNode().getSynCode() == Code.ASSIGN))
                continue;


            for (Pair pair : OUT.get(v))
            {
                if (KILL.get(v).contains(pair.id))
                {
                    LabeledCFGEdge e = new LabeledCFGEdge();
                    e.setStyle("dashed");
                    e.setColor("black");
                    cfg.addEdge(v,pair.vertex,e);
                }
            }

            /*
            OUT.get(v).stream().filter(pair -> {
                return KILL.get(v).stream().anyMatch(pair1 -> pair1.id.equals(pair.id));
            }).forEach(pair -> {
                System.out.println(v);
                System.out.println(pair);
                cfg.addEdge(v,pair.vertex,new LabeledCFGEdge("___",true));
            });
*/
        }

        //IN.keySet().stream().forEach(cfgVertex -> System.out.println(cfgVertex.getLabel()));

        System.out.println("Done");

    }

    private ArrayList<CFGVertex> getValidSuccessors(CFGVertex root,CFG cfg)
    {
        ArrayList<CFGVertex> list = new ArrayList<>();
        for (LabeledCFGEdge e : cfg.outgoingEdgesOf(root))
        {
            CFGVertex target = cfg.getEdgeTarget(e);
            if (target.getSyntaxNode() == null){
                list.addAll(getValidSuccessors(target,cfg));
            } else
            {
                list.add(target);
            }
        }
        return list;
    }

    private ArrayList<CFGVertex> getValidPredecessor(CFGVertex root,CFG cfg)
    {
        ArrayList<CFGVertex> list = new ArrayList<>();
        for (LabeledCFGEdge e : cfg.incomingEdgesOf(root))
        {
            CFGVertex target = cfg.getEdgeSource(e);
            if (target.getSyntaxNode() == null){
                list.addAll(getValidPredecessor(target,cfg));
            } else
            {
                list.add(target);
            }
        }
        return list;
    }

    /*
    private ArrayList<CFGVertex> getValidPredecessor(CFGVertex root,CFG cfg)
    {
        ArrayList<CFGVertex> list = new ArrayList<>();
        for (LabeledCFGEdge e : cfg.incomingEdgesOf(root))
        {
            CFGVertex target = cfg.getEdgeSource(e);
            if (target.getSyntaxNode() == null){
                list.addAll(getValidPredecessor(target,cfg));
            } else
            {
                switch (target.getSyntaxNode().getSynCode())
                {
                    case FUNCTION_DEFINITION:
                    case ASSIGN:
                    case ID:
                    {
                        list.add(target);
                        break;
                    }
                    default:
                    {
                        list.addAll(getValidPredecessor(target,cfg));
                    }
                }
            }
        }
        return list;
    }
*/
}
