import cup.Parser;
import flex.Lexer;
import tripla.SyntaxNode;

import java.io.*;

public class Main
{
  public static void main(String argv[])
  {
    try 
    {
      Reader input = new FileReader("sample.txt");
      Parser p = new Parser(new Lexer(input));
	  
      SyntaxNode result = ((SyntaxNode) p.parse().value);
	  
	  SyntaxNode.toFile(result.toXML(0));
    }
    catch (Exception e) 
    {
        e.printStackTrace();
    }
  }
}

