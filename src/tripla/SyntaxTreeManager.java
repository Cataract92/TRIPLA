package tripla;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SyntaxTreeManager {

    private static SyntaxTreeManager instance;

    public static SyntaxTreeManager getInstance() {
        if (instance == null)
            instance = new SyntaxTreeManager();

        return instance;
    }

    public void optimizeTree(SyntaxNode root) {
        removeCodeStack(root, Code.COMMA);
        removeCodeStack(root, Code.SEQUENCE);
        removeCodeStack(root, Code.SEMICOLON);
    }

    private void removeCodeStack(SyntaxNode root, Code synCode) {
        if (root.getSynCode() == synCode) {
            boolean removedNode;
            do {
                removedNode = false;

                ArrayList<SyntaxNode> tmp = new ArrayList<>(root.getNodes());

                for (SyntaxNode node : root.getNodes()) {
                    if (node.getSynCode() == synCode) {
                        int index = tmp.indexOf(node);

                        tmp.addAll(index, node.getNodes());

                        tmp.remove(node);

                        removedNode = true;
                    }
                }

                root.setNodes(tmp);

            } while (removedNode);
        }

        for (SyntaxNode node : root.getNodes()) {
            removeCodeStack(node, synCode);
        }

    }

    public ArrayList<String> getAllIDs(SyntaxNode node) {
        ArrayList<String> list = new ArrayList<>();

        if (node.getSynCode() == Code.ID) {
            list.add((String) node.getValue());
        } else if (node.getSynCode() == Code.COMMA) {
            for (SyntaxNode n : node.getNodes()) {
                list.addAll(getAllIDs(n));
            }
        }
        return list;
    }

    public int countComma(SyntaxNode node) {
        int count = 0;
        if (node.getSynCode() == Code.COMMA) {
            count++;
            for (SyntaxNode n : node.getNodes()) {
                count += countComma(n);
            }
        }
        return count;
    }


    public void toFile(String json, SyntaxNode root) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        DefaultPrettyPrinter.Indenter indenter =
                new DefaultIndenter("   ", DefaultIndenter.SYS_LF);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(indenter);
        printer.indentArraysWith(indenter);

        mapper.setDefaultPrettyPrinter(printer);

        Files.write(Paths.get(json), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root).getBytes());
    }

}
