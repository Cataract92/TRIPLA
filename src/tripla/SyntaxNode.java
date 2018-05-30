package tripla;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class SyntaxNode {
	/* Variables */
	private Code synCode;
	private ArrayList<SyntaxNode> nodes = new ArrayList<>();
	private Object value;

	//Knoten f?r 3 Kinder
	public SyntaxNode(Code synCode, Object obj, SyntaxNode...nodeList){
		this.synCode=synCode;
		this.nodes.addAll(Arrays.asList(nodeList));
		this.value=obj;
	}
	
	public String getTab(int numberOfTabs){
		String tabString = "";
		for(int i=1;i<=numberOfTabs;i++){
			tabString+="\t";
		}
		
		return tabString;
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

    /*
    public String toJson()
    {
        return toJson(0);
    }

    private String toJson(int depth)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < depth; i++)
            output.append("\t");

        output.append(synCode);

        if (value != null) {
            output.append(": ").append(value);
        }

        if (!nodes.isEmpty()) {
            output.append("\n");

            for (int i = 0; i < depth; i++)
                output.append("\t");

            output.append("{\n");

            for (SyntaxNode node : nodes) {
                output.append(node.toJson(depth + 1));
            }

            for (int i = 0; i < depth; i++)
                output.append("\t");

            output.append("}");
        }
        output.append("\n");

        return output.toString();

    }
    */
}