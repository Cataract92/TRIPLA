/*
 * Nico Feld - 1169233
 */

import CodeGenerator.Instruction;
import CodeGenerator.Label;
import Dataflow.*;
import Dataflow.Analysis.ReachedUsesStrategy;
import Dataflow.Export.DotExportStrategy;
import cup.Parser;
import flex.Lexer;
import tripla.SyntaxNode;
import tripla.SyntaxTreeManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String argv[]) {
        if (argv.length != 2) {
            System.out.println("Usage: TRIPLA.jar path/to/sample.tripla path/to/output");
            return;
        }

        File source = new File(argv[0]);

        String output = argv[1] + "/" + source.getName().substring(0, source.getName().lastIndexOf("."));

        SyntaxNode result = buildAndPrintTree(source, output);

        doDataflowAnalysis(result, output);

        printInstructions(result, output);
    }

    private static SyntaxNode buildAndPrintTree(File sample, String output) {

        SyntaxNode result = null;
        try {
            Reader input = new FileReader(sample);

            Parser p = new Parser(new Lexer(input));
            result = ((SyntaxNode) p.parse().value);

            SyntaxTreeManager stm = SyntaxTreeManager.getInstance();

            stm.optimizeTree(result);
            stm.toFile(output + ".json", result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void doDataflowAnalysis(SyntaxNode root, String output) {
        CFG cfg = new CFG(root, "in", "out");

        new ReachedUsesStrategy().compute(cfg);

        cfg.export(new DotExportStrategy(), output + ".dot");
    }

    private static void printInstructions(SyntaxNode root, String output) {
        ArrayList<Instruction> code = root.code(new HashMap<>(), 0);

        Label.replaceLabels(code);

        try {
            FileWriter fileWriter = new FileWriter(output + ".tram");
            for (Instruction instruction : code)
                fileWriter.write(instruction.toString() + "\n");

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

