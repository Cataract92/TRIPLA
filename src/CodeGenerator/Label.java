package CodeGenerator;

import tripla.SyntaxNode;

import java.util.ArrayList;

public class Label {

    private static ArrayList<Label> allLabels = new ArrayList<>();

    private Instruction labeledInstruction;
    private ArrayList<Instruction> instructions = new ArrayList<>();

    public Label() {
        Label.allLabels.add(this);
    }

    public Instruction getLabeledInstruction() {
        return labeledInstruction;
    }

    public void setLabeledInstruction(Instruction labeledInstruction) {
        this.labeledInstruction = labeledInstruction;
    }

    public void addInstruction(Instruction instruction)
    {
        instructions.add(instruction);
    }

    public ArrayList<Instruction> getInstructions()
    {
        return instructions;
    }

    public static void replaceLabels(ArrayList<Instruction> instructions)
    {
        for (Label label : allLabels)
        {
            int address = instructions.indexOf(label.labeledInstruction);

            for (Instruction inst: label.getInstructions())
            {
                switch (inst.getOpcode())
                {
                    case Instruction.GOTO :
                    {
                        inst.setArg1(address);
                        break;
                    }

                    case Instruction.INVOKE :
                    {
                        inst.setArg2(address);
                        break;
                    }

                    case Instruction.IFZERO :
                    {
                        inst.setArg1(address);
                        break;
                    }
                }
            }
        }
    }
}
