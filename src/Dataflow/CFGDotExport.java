package Dataflow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.IntegerNameProvider;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Random;

public class CFGDotExport extends AbstractCFGExportStrategy {

    IntegerNameProvider<CFGVertex> vertexIDProvider = new IntegerNameProvider<>();

    private enum Colors
    {
        blue,
        red,
        orange,
        purple,
        green,
        yellow,
        brown,
        cyan,
        gold,
        aqua,
        fuchsia,
        navy,
        olive,
        teal
    }

    @Override
    public void export(Writer writer, CFG cfg) {
        PrintWriter printWriter = new PrintWriter(writer);
        String arrow = " -> ";

        printWriter.println("digraph G {\n  splines=ortho;\n  rankdir=\"LR\";");

        int count=0;
        for (String subgraph : CFGVertex.getGraphList().keySet())
        {
            printWriter.println("  subgraph cluster_"+count+" {");
            printWriter.println("    label=\"" +subgraph+"\";");
            printWriter.println("    color=\"" +Colors.values()[new Random().nextInt(Colors.values().length)]+"\";");
            printWriter.print("    ");
            for (CFGVertex v : CFGVertex.getGraphList().get(subgraph))
            {
                printWriter.print(this.vertexIDProvider.getVertexName(v));
                printWriter.print(" [label = \"" + v.getLabel() + "\", shape = \"" + v.getType().name() + "\", style = \"" + v.getStyle() + "\"]; ");
            }
            printWriter.println("\n  }");
            count++;
        }

        Iterator<LabeledCFGEdge> labelIterator;
        LabeledCFGEdge e;
        for(labelIterator = cfg.edgeSet().iterator(); labelIterator.hasNext(); printWriter.println(";")) {
            e = labelIterator.next();
            String var8 = this.vertexIDProvider.getVertexName(cfg.getEdgeSource(e));
            String var9 = this.vertexIDProvider.getVertexName(cfg.getEdgeTarget(e));
            printWriter.print("  " + var8 + arrow + var9);
            printWriter.print(" [label = \"" + e.getLabel() + "\", constraint = \"" + e.isConstraint() + "\"]");
        }

        printWriter.println("}");
        printWriter.flush();
    }
}
