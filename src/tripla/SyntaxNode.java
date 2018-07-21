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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SyntaxNode {

    private Code synCode;
    private ArrayList<SyntaxNode> nodes = new ArrayList<>();
    private Object value;

    private SyntaxTreeManager syntaxTreeManager;

    public SyntaxNode(Code synCode, Object obj, SyntaxNode... nodeList) {
        this.synCode = synCode;
        this.nodes.addAll(Arrays.asList(nodeList));
        this.value = obj;
        syntaxTreeManager = SyntaxTreeManager.getInstance();
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
                for (SyntaxNode node: getNodes())
                {
                    node.elab_def(rho, nl);
                }
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
                for (SyntaxNode node: nodes)
                {
                    instructions.addAll(node.code(rho,nl));
                }
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
                Label l1 = new Label();
                Label l2 = new Label();

                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));

                instructions.add(new Instruction(Instruction.ADD));

                Instruction ifzero = new Instruction(Instruction.IFZERO,-1);
                l1.addInstruction(ifzero);
                instructions.add(ifzero);

                instructions.add(new Instruction(Instruction.CONST,1));

                Instruction gTo = new Instruction(Instruction.GOTO,-1);
                l2.addInstruction(gTo);
                instructions.add(gTo);

                Instruction const0 = new Instruction(Instruction.CONST,0);
                l1.setLabeledInstruction(const0);
                instructions.add(const0);

                Instruction nop = new Instruction(Instruction.NOP);
                l2.setLabeledInstruction(nop);
                instructions.add(nop);

                break;
            }

            case IF_THEN_ELSE: {
                Label l1 = new Label();
                Label l2 = new Label();

                instructions.addAll(nodes.get(0).code(rho, nl));

                Instruction ifzero = new Instruction(Instruction.IFZERO,-1);
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

                instructions.add(new Instruction(Instruction.POP));

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
                instructions.add(new Instruction(Instruction.MULT));
                break;
            }

            case PARENTHESES: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                break;
            }

            case SEQUENCE: {
                for (SyntaxNode node : nodes)
                    instructions.addAll(node.code(rho,nl));
                break;
            }

            case SEMICOLON: {

                for (SyntaxNode node : nodes) {
                    instructions.addAll(node.code(rho, nl));
                    instructions.add(new Instruction(Instruction.POP));
                }
                instructions.remove(instructions.size()-1); // Remove last pop
                break;
            }

            case OP_SUB: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.SUB));
                break;
            }

            case OP_MULT: {
                instructions.addAll(nodes.get(0).code(rho,nl));
                instructions.addAll(nodes.get(1).code(rho,nl));
                instructions.add(new Instruction(Instruction.MULT));
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

                Instruction invoke = new Instruction(Instruction.INVOKE,syntaxTreeManager.countComma(nodes.get(1))+1,-1,nl-pair.getNl());
                ((Label) pair.getLoc()).addInstruction(invoke);
                instructions.add(invoke);
                break;
            }

            case FUNCTION_DEFINITION: {

                    HashMap<String ,AddressPair> map = new HashMap<>(rho);

                    ArrayList<String> allIds = syntaxTreeManager.getAllIDs(nodes.get(1));

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

    public Code getSynCode() {
        return synCode;
    }

    public ArrayList<SyntaxNode> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<SyntaxNode> nodes) {
        this.nodes = nodes;
    }

    public Object getValue() {
        return value;
    }
}