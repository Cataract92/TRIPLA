/*
 * Nico Feld - 1169233
 */

package tripla;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class SyntaxNode {

    private Code synCode;
    private ArrayList<SyntaxNode> nodes = new ArrayList<>();
    private Object value;

    public SyntaxNode(Code synCode, Object obj, SyntaxNode... nodeList) {
        this.synCode = synCode;
        this.nodes.addAll(Arrays.asList(nodeList));
        this.value = obj;
    }

    public void toFile(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        DefaultPrettyPrinter.Indenter indenter =
                new DefaultIndenter("   ", DefaultIndenter.SYS_LF);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(indenter);
        printer.indentArraysWith(indenter);

        mapper.setDefaultPrettyPrinter(printer);

        Files.write(Paths.get(json), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this).getBytes());
    }

    public Code getSynCode() {
        return synCode;
    }

    public ArrayList<SyntaxNode> getNodes() {
        return nodes;
    }

    public Object getValue() {
        return value;
    }
}