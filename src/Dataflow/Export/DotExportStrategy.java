/*
 * Nico Feld - 1169233
 */

package Dataflow.Export;

import Dataflow.CFG;
import Dataflow.Data.Edge;
import Dataflow.Data.Vertex;
import org.jgrapht.ext.IntegerNameProvider;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class DotExportStrategy extends AbstractExportStrategy {

    private ArrayList<Vertex> drawnVertexes = new ArrayList<>();
    private ArrayList<Edge> drawnEdges = new ArrayList<>();

    IntegerNameProvider<Vertex> vertexIDProvider = new IntegerNameProvider<>();

    @Override
    public void export(Writer writer, CFG cfg) throws IOException {
        writer.write("digraph G {\n");

        for (Vertex v : cfg.vertexSet()) {
            if (!drawnVertexes.contains(v)) {
                drawnVertexes.add(v);
                writer.write("\t" + this.vertexIDProvider.getVertexName(v));
                writer.write(" " + v.toString() + "\n");
            }
        }

        writer.write("\n");

        for (Edge e : cfg.edgeSet()) {
            if (!drawnEdges.contains(e)) {
                drawnEdges.add(e);
                writer.write("\t" + this.vertexIDProvider.getVertexName(cfg.getEdgeSource(e)) + " -> " + this.vertexIDProvider.getVertexName(cfg.getEdgeTarget(e)));
                writer.write(" " + e.toString() + "\n");
            }
        }

        writer.write("}\n");
        writer.flush();

    }

}
