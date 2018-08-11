package Dataflow;

import org.jgrapht.ext.IntegerNameProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

public class CFGDotExport extends AbstractCFGExportStrategy {

    private ArrayList<CFG> drawnGraphs = new ArrayList<>();
    private ArrayList<CFGVertex> drawnVertexes = new ArrayList<>();
    private ArrayList<LabeledCFGEdge> drawnEdges = new ArrayList<>();

    IntegerNameProvider<CFGVertex> vertexIDProvider = new IntegerNameProvider<>();

    @Override
    public void export(Writer writer, CFG cfg) throws IOException {
    writer.write("digraph G {\n  splines=ortho;\n");

        printGraph(writer, cfg);

        writer.write("}\n");
        writer.flush();

    }

    private Random random = new Random();

    private void printGraph(Writer writer, CFG cfg) throws IOException {
        writer.write("  subgraph cluster_" + cfg.getName() + " {\n");
        writer.write("    label=\"" + cfg.getName() + "\";\n     style=\"dashed\"");

        String color = CFGColors.values()[random.nextInt(CFGColors.values().length)].name();

        writer.write("    color=\"" + color + "\";\n");

        for (CFG c : cfg.getSubCFGs().values()) {
            if (!drawnGraphs.contains(c)) {
                drawnGraphs.add(c);
                printGraph(writer, c);
            }
        }

        for (CFGVertex v : cfg.vertexSet()) {
            if (!drawnVertexes.contains(v)) {
                drawnVertexes.add(v);
                writer.write(this.vertexIDProvider.getVertexName(v));
                writer.write(" [label = \"" + v.getLabel() + "\", shape = \"" + v.getType().name() + "\", style = \"" + v.getStyle() + "\", color=\"" + color + "\"];\n");
            }
        }

        for (LabeledCFGEdge e : cfg.edgeSet()) {
            if (!drawnEdges.contains(e)) {
                drawnEdges.add(e);
                writer.write("  " + (this.vertexIDProvider.getVertexName(cfg.getEdgeSource(e)) + " -> " + this.vertexIDProvider.getVertexName(cfg.getEdgeTarget(e))));
                writer.write(" [xlabel = \"" + e.getLabel() + "\", constraint = \"" + e.isConstraint() + "\", color=\"" + color + "\"]\n");
            }
        }



        writer.write("\n}\n");
    }
}
