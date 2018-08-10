package Dataflow;

import org.jgrapht.ext.IntegerNameProvider;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Random;

public class CFGDotExport extends AbstractCFGExportStrategy {

    IntegerNameProvider<CFGVertex> vertexIDProvider = new IntegerNameProvider<>();

    @Override
    public void export(Writer writer, CFG cfg) {
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.println("digraph G {\n  splines=ortho;");

        printGraph(printWriter,cfg);

        printWriter.println("}");
        printWriter.flush();

    }

    private void printGraph(PrintWriter printWriter, CFG cfg)
    {
        printWriter.println("  subgraph cluster_"+cfg.getName()+" {");
        printWriter.println("    label=\"" +cfg.getName()+"\";");

        String color = CFGColors.values()[new Random().nextInt(CFGColors.values().length)].name();

        printWriter.println("    color=\"" + color + "\";");
        printWriter.print("    ");


        for (CFG c : cfg.getSubCFGs().values())
        {
            printGraph(printWriter,c);
        }

        for (CFGVertex v :cfg.vertexSet()) {
            printWriter.print(this.vertexIDProvider.getVertexName(v));
            printWriter.print(" [label = \"" + v.getLabel() + "\", shape = \"" + v.getType().name() + "\", style = \"" + v.getStyle() + "\", color=\"" + color + "\"]; ");
        }

        for (LabeledCFGEdge e : cfg.edgeSet()) {
            printWriter.print("  " + this.cfg.getEdgeSource(e) + " -> " + cfg.getEdgeTarget(e));
            printWriter.println(" [label = \"" + e.getLabel() + "\", constraint = \"" + e.isConstraint() + "\", color=\""+e.getColor()+"\"]");
        }
        printWriter.println("\n  }");
    }
}
