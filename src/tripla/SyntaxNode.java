package tripla;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class SyntaxNode {
	/* Variables */
	private Code synCode;
	private ArrayList<SyntaxNode> nodeList = new ArrayList<>();
	private Object value;

	//Knoten f?r 3 Kinder
	public SyntaxNode(Code synCode, Object obj, SyntaxNode...nodes){
		this.synCode=synCode;
		nodeList.addAll(Arrays.asList(nodes));
		this.value=obj;
	}
	
	public String getTab(int numberOfTabs){
		String tabString = "";
		for(int i=1;i<=numberOfTabs;i++){
			tabString+="\t";
		}
		
		return tabString;
	}
	
	public String toXML(int depth){
		/*
		String xml="";
		String tmp1="",tmp2="",tmp3="";
		
		
		//Blatt mit Value
		if (node1 == null && node2==null && node3==null && value != null){
			String code = codeToString(synCode);
			
			if(value instanceof Integer){
				xml += getTab(depth+1)+"<"+code+" value='"+value.toString()+"'/>\n";
			}
			else{
				xml += getTab(depth+1)+"<"+code+" value='"+value.toString()+"'/>\n";
			}
		}
		//Blatt ohne Value
		else if(node1 == null && node2==null && node3==null && value == null){
			System.out.println(synCode);
			String code = codeToString(synCode);
			xml += getTab(depth+1)+"<"+code+"/>\n";
		}
		//Knoten mit 1 Kind
		else if (node2==null && node3==null){
			String code = codeToString(synCode);
			xml += getTab(depth+1)+"<"+code+">\n";
			
			//Erstes Kind und einziges Kind
			tmp1 = node1.toXML(depth+1);
			xml += tmp1;
			
			xml += getTab(depth+1)+"</"+code+">\n";
		}
		//Knoten mit 2 Kindern
		else if (node3==null){
			String code = codeToString(synCode);
			xml += getTab(depth+1)+"<"+code+">\n";
			
			//Erstes Kind
			tmp1 = node1.toXML(depth+1);
			xml += tmp1;
			
			//Zweites Kind
			tmp2 = node2.toXML(depth+1);
			xml += tmp2;

			xml += getTab(depth+1)+"</"+code+">\n";
		}
		//Knoten mit 3 Kindern
		else{
			String code = codeToString(synCode);
			xml += getTab(depth+1)+"<"+code+">\n";
			
			//Erstes Kind
			tmp1 = node1.toXML(depth+1);
			xml += tmp1;
			
			//Zweites Kind
			tmp2 = node2.toXML(depth+1);
			xml += tmp2;
			
			//Drittes Kind
			tmp3 = node3.toXML(depth+1);
			xml += tmp3;

			xml += getTab(depth+1)+"</"+code+">\n";
		}
		
		return xml;
		*/
		return "";
	}
	
	public String codeToString(Code synCode){
		switch(synCode){
			case LET_IN:
				return "let_in";
			case IF_THEN_ELSE:
				return "if_then_else";
			case ID:
				return "id";
			case OP_PLUS:
				return "plus";
			case OP_MINUS:
				return "minus";
			case OP_MUL:
				return "mul";
			case OP_DIV:
				return "div";
			case OP_EQ :
				return "eq";
			case OP_NEQ:
				return "neq";
			case OP_LT :
				return "lt";
			case OP_GT :
				return "gt";
			case OP :
				return "operation";
			case ASSIGN :
				return "assign";
			case PARENTHESES:
				return "paranthesis";
			case CONST:
				return "const";
			case SEQUENCE:
				return "sequence";
			case COMMA:
				return "comma";
			case SEMICOLON:
				return "semicolon";
			case FUNCTION:
				return "function";
			case FUNCTION_DEFINITION:
				return "function_definition";
			default:
				return "expression";
		}
	}
	
	public static void toFile(String xml){
		try{
			PrintWriter writer = new PrintWriter("Tree.xml", "UTF-8");
			writer.println("<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n\n<SyntaxTree>\n"+xml+"</SyntaxTree>");
			writer.close();
		}catch(Exception e){
		
		}
	}
}