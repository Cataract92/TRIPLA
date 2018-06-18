/*
 * Nico Feld - 1169233
 */

package tripla;

import CodeGenerator.AddressPair;
import CodeGenerator.Instruction;
import CodeGenerator.Label;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.util.ArrayUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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


    private void elab_def(HashMap<String, AddressPair> rho, int nl) {


        switch (synCode){

            case FUNCTION_DEFINITION:
            {
                Label l = new Label();
                rho.put((String) nodes.get(0).value,new AddressPair(l,nl));
                break;
            }

            case SEQUENCE:
            {
                getNodes().get(1).elab_def(rho, nl);
                getNodes().get(0).elab_def(rho,nl);
                break;
            }

        }

    }


    public ArrayList<Instruction> code(HashMap<String, AddressPair> rho, int nl) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        switch (synCode) {

            case ID: {
                AddressPair pair = rho.get(value);

                instructions.add(new Instruction(Instruction.LOAD,(Integer) pair.getLoc(),nl - pair.getNl()));
                break;
            }

            case BOOL: {
                if ((Boolean) value)
                    instructions.add(new Instruction(Instruction.CONST, 1));
                else
                    instructions.add(new Instruction(Instruction.CONST, 0));
                break;
            }

            case COMMA: {
                instructions.addAll(nodes.get(0).code(rho, nl));
                instructions.addAll(nodes.get(1).code(rho, nl));
                break;
            }

            case CONST: {
                instructions.add(new Instruction(Instruction.CONST, (Integer) value));
                break;
            }

            case OP_EQ: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.EQ));
                break;
            }

            case OP_GT: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.GT));
                break;
            }

            case OP_LT: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.LT));
                break;
            }

            case OP_OR: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.OR));
                break;
            }

            case IF_THEN_ELSE: {
                Label l1 = new Label();
                Label l2 = new Label();

                instructions.addAll(nodes.get(0).code(rho, nl));

                Instruction ifzero = new Instruction(Instruction.IFZERO);
                l1.addInstruction(ifzero);
                instructions.add(ifzero);

                instructions.addAll(nodes.get(1).code(rho, nl));

                Instruction gTo = new Instruction(Instruction.GOTO,-1);
                l2.addInstruction(gTo);
                instructions.add(gTo);

                ArrayList<Instruction> code_e3 = nodes.get(2).code(rho, nl);
                l1.setLabeledInstruction(code_e3.get(0));
                instructions.addAll(code_e3);

                Instruction nop = new Instruction(Instruction.NOP);
                l2.setLabeledInstruction(nop);
                instructions.add(nop);

                break;
            }

            case DO_WHILE: {

                Label l1 = new Label();
                Label l2 = new Label();

                ArrayList<Instruction> code_e1 = nodes.get(0).code(rho, nl);
                l1.setLabeledInstruction(code_e1.get(0));
                instructions.addAll(code_e1);

                instructions.addAll(nodes.get(1).code(rho, nl));

                Instruction ifzero = new Instruction(Instruction.IFZERO);
                l2.addInstruction(ifzero);
                instructions.add(ifzero);

                Instruction gTo = new Instruction(Instruction.GOTO,-1);
                l1.addInstruction(gTo);
                instructions.add(gTo);

                Instruction nop = new Instruction(Instruction.NOP);
                l2.setLabeledInstruction(nop);
                instructions.add(nop);

                break;
            }

            case ASSIGN: {
                AddressPair pair = rho.get(nodes.get(0).value);

                instructions.addAll(nodes.get(1).code(rho, nl));
                instructions.add(new Instruction(Instruction.STORE,(Integer) pair.getLoc(),nl - pair.getNl()));
                instructions.add(new Instruction(Instruction.LOAD,(Integer) pair.getLoc(),nl - pair.getNl()));
                break;
            }

            case LET_IN: {
                Label label = new Label();

                Instruction gTo = new Instruction(Instruction.GOTO,-1);
                label.addInstruction(gTo);
                instructions.add(gTo);

                nodes.get(0).elab_def(rho,nl);
                instructions.addAll(nodes.get(0).code(rho,nl));

                ArrayList<Instruction> code_e = nodes.get(1).code(rho,nl);
                label.setLabeledInstruction(code_e.get(0));
                instructions.addAll(code_e);

                break;
            }

            case OP_ADD: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.ADD));
                break;
            }

            case OP_AND: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.AND));
                break;
            }

            case PARENTHESES: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                break;
            }

            case SEQUENCE: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                break;
            }

            case SEMICOLON: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.add(new Instruction(Instruction.POP));
                instructions.addAll(nodes.get(1).code(rho,nl));
                break;
            }

            case OP_SUB: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.SUB));
                break;
            }

            case OP_MUL: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.MUL));
                break;
            }

            case OP_DIV: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.DIV));
                break;
            }

            case OP_NEQ: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.NEQ));
                break;
            }

            case OP_GTE: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.GTE));
                break;
            }

            case OP_LTE: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.LTE));
                break;
            }

            case FUNCTION_CALL: {
                instructions.addAll(nodes.get(1).code(rho, nl));

                AddressPair pair = rho.get(nodes.get(0).value);

                Instruction invoke = new Instruction(Instruction.INVOKE,nodes.get(1).countComma()+1,-1,nl-pair.getNl());
                ((Label) pair.getLoc()).addInstruction(invoke);
                instructions.add(invoke);
                break;
            }

            case FUNCTION_DEFINITION: {
                HashMap<String ,AddressPair> map = new HashMap<>(rho);

                ArrayList<String> allIds = nodes.get(1).getAllIDs();

                for (int i = 0; i < allIds.size(); i++)
                {
                    map.put(allIds.get(i),new AddressPair(i,nl+1));
                }

                ArrayList<Instruction> code_e = nodes.get(2).code(map,nl+1);
                Label l = (Label) map.get(nodes.get(0).value).getLoc();
                l.setLabeledInstruction(code_e.get(0));
                instructions.addAll(code_e);

                instructions.add(new Instruction(Instruction.RETURN));

                break;
            }
        }

        return instructions;
    }

    private ArrayList<String> getAllIDs()
    {
        ArrayList<String> list = new ArrayList<>();

        if (synCode == Code.ID)
        {
            list.add((String)value);
        } else if (synCode == Code.COMMA)
        {
            for (SyntaxNode n : nodes)
            {
                list.addAll(n.getAllIDs());
            }
        }
        return list;
    }

    private int countComma()
    {
        int count = 0;
        if (synCode == Code.COMMA)
        {
            count++;
            for (SyntaxNode n : nodes)
            {
                count += n.countComma();
            }
        }
        return count;
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