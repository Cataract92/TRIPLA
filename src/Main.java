/*
 * Nico Feld - 1169233
 */

import CodeGenerator.AddressPair;
import CodeGenerator.Instruction;
import CodeGenerator.Label;
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
            System.out.println("Usage: TRIPLA.jar path/to/sample.txt path/to/output.json");
            return;
        }


        try {
            Reader input = new FileReader(argv[0]);
            Parser p = new Parser(new Lexer(input));

            SyntaxNode result = ((SyntaxNode) p.parse().value);

            SyntaxTreeManager stm = SyntaxTreeManager.getInstance();

            stm.optimizeTree(result);
            stm.toFile(argv[1],result);

            ArrayList<Instruction> code = result.code(new HashMap<>(),0);

            Label.replaceLabels(code);

            for (Instruction instruction : code)
            {
                System.out.println(instruction.toString());
            }

            System.out.println("Output: " + argv[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

