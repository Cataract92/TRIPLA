package Dataflow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.IntegerNameProvider;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Random;

public class CFGDotExport extends AbstractCFGExportStrategy {

    IntegerNameProvider<CFGVertex> vertexIDProvider = new IntegerNameProvider<>();

    @Override
    public void export(Writer writer, CFG cfg) {
        PrintWriter printWriter = new PrintWriter(writer);
        String arrow = " -> ";

        printWriter.println("digraph G {\n  splines=ortho;");

        int count=0;
        for (String subgraph : CFGVertex.getGraphList().keySet())
        {
            printWriter.println("  subgraph cluster_"+count+" {");
            printWriter.println("    label=\"" +subgraph+"\";");

            String color = null;
            if (subgraph.equals(""))
            {
                color = "black";
            } else
            {
                color = CFGColors.values()[new Random().nextInt(CFGColors.values().length)].name();
            }

            printWriter.println("    color=\"" + color + "\";");
            printWriter.print("    ");
            for (CFGVertex v : CFGVertex.getGraphList().get(subgraph))
            {
                if (!subgraph.equals(""))
                {
                    for (LabeledCFGEdge e : cfg.edgesOf(v))
                    {
                        e.setColor(color);
                    }
                }
                printWriter.print(this.vertexIDProvider.getVertexName(v));
                printWriter.print(" [label = \"" + v.getLabel() + "\", shape = \"" + v.getType().name() + "\", style = \"" + v.getStyle() + "\", color=\""+color+"\"]; ");
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
            printWriter.print(" [label = \"" + e.getLabel() + "\", constraint = \"" + e.isConstraint() + "\", color=\""+e.getColor()+"\"]");
        }

        printWriter.println("}");
        printWriter.flush();
    }
}
