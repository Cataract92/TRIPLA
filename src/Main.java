/*
 * Nico Feld - 1169233
 */

import cup.Parser;
import flex.Lexer;
import tripla.SyntaxNode;

import java.io.*;

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

            result.toFile(argv[1]);

            System.out.println("Output: " + argv[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

