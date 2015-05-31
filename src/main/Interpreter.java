package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import object.LAObject;
import object.Matrix;
import exception.SyntaxException;
import exception.VariableException;

/**
 * 
 * @author RangeWING
 * @email rangewing@kaist.ac.kr
 * @since 2015.05.27.
 * This Class Interprets a command in a line.
 * Linear Algebric Code
 * [Supports]
 * a0.1
 * var NAME				//변수 할당
 * NAME = new Matrix	//변수에 행렬 할당
 * elim MATRIX			//Row Echelon Form
 * rref MATRIX			//Reduced Row Echelon Form
 * elim MATRIX > MATRIX'//MATRIX' = REF
 * rref MATRIX > MATRIX'//MATRIX' = RREF
 * a0.2
 * A + B
 * A * B
 * A * k
 * t A	//transpose A
 */
public class Interpreter {
	public final static String version = "Alpha 0.2.2";
	public final static String build = "F5290";
	
	private final static int VARIABLE = 0xFA;
	private final static int COMMAND = 0xFB;
	private final static int OPERATOR = 0xFC;
	private final static int METHOD = 0xFD;
	
	private HashMap<String, LAObject> varTable; //name, object
	private HashMap<String, String> cmdTable; //name, info
	private HashMap<String, Integer> opTable; //name, # of terms
	private HashMap<String, String> methodTable; //name, returnType
	
	public Interpreter(){
		varTable = new HashMap<String, LAObject>();
		initCmdTable();
	}
	
	public LAObject execute(String command) throws SyntaxException, VariableException{
		if(command.compareTo("") == 0) return null;
		LAObject result = null;
		
		StringTokenizer st = new StringTokenizer(command, " ");
		ArrayList<String> cmd = new ArrayList<String>();
		
		while(st.hasMoreTokens()){
			cmd.add(st.nextToken());
		}
		
		//첫 단어: 명령어, 메소드, 변수 가능
		//메소드: ()포함
		//변수: 변수 table에 있음
		//명령어: 기타

		
		String first = cmd.get(0);
		if(command.contains("=")){
			result = executeEqual(command, cmd);
		}
		//Method
		else if(first.contains("(") && first.contains(")")){
			if(cmd.size() > 1) throw new SyntaxException(command, "Unexpected sth after calling a method");
			String str = cmd.get(0);
			if(str.indexOf(")") != str.length()-1) throw new SyntaxException(command, "Unexpected sth after closing method"); 
			int pIndex = str.indexOf("(");
			String name = str.substring(0, pIndex);
			if(findType(name) != METHOD){
				throw new SyntaxException(name, "There is no method named " + name);
			}
			ArrayList<String> params = null;
			//If parameters exists
			if(pIndex < str.length() - 2){
				String param = str.substring(pIndex+1, str.length());
				StringTokenizer stParam = new StringTokenizer(param, ",");
				params = new ArrayList<String>();
				while(stParam.hasMoreTokens()){
					params.add(stParam.nextToken());
				}
			}
			
			executeMethod(name, params);
			//TODO
		}
		//Variable
		else if(varTable.containsKey(first)){
			result = executeVariable(command, cmd);
		}
		//Command
		else if(cmdTable.containsKey(first)){
			result = executeCommand(command, cmd);
			 
		}
		//Error
		else{
			throw new SyntaxException(first, "Unknown Command");
		}
		
		return result;
		
	}
	public LAObject executeEqual(String command, ArrayList<String> cmd) throws SyntaxException, VariableException{
		LAObject result = null;		
		int index = cmd.indexOf("=");
		if(index > 0){
			String name = cmd.get(index-1);

			String subcmd = subCommand(cmd, index+1, cmd.size());
			if(subcmd.isEmpty()){
				throw new SyntaxException(command, "Expected sth after '='");
			}
			
			if(!varTable.containsKey(name)){
				if(cmd.get(0).compareTo("var") == 0){
					execute(subCommand(cmd, 0, index));
				}else{
					throw new VariableException(command, "There is no variable named '" + name+"'");
				}
			}
			result = execute(subcmd);
			addVar(name, result);
		}else{
			throw new SyntaxException(command, "Unexpected '='");
		}
		
		return result;
	}
	
	public LAObject executeVariable(String command, ArrayList<String> cmd) throws SyntaxException, VariableException{
		LAObject result = null;
		String first = cmd.get(0);
		LAObject variable = varTable.get(first);
		if(variable == null){
			throw new VariableException(command, "The variable '" + first + "' has never been initalized");
		}
		//출력
		if(cmd.size() == 1){
			result = variable;
			System.out.println("# " + first + " : " + findType(result) + " =");
			result.print();
		}
		return result;
	}
	
	public LAObject executeCommand(String command, ArrayList<String> cmd) throws SyntaxException, VariableException{
		LAObject result = null;
		String first = cmd.get(0);
		
		//var
		if(first.compareTo("var") == 0){
			if(cmd.size() < 2){
				throw new SyntaxException(command, "Expected name of new variable after 'var'");
			}
			String name = cmd.get(1);
			if(cmd.size() > 2){
				throw new SyntaxException(command, "Unexpected commands after 'var " + name + "'");
			}
			varTable.put(name, null);
			return null;
		 }
		//new
		else if(first.compareTo("new") == 0){
			if(cmd.size() < 2){
				throw new SyntaxException(command, "Expected type of new object after 'new'");
			}
			String type = cmd.get(1);
			if(cmd.size() > 2){
				throw new SyntaxException(command, "Unexpected commands after 'new " + type + "'");
			}
			
			if(type.compareTo("Matrix") == 0){
				return Matrix.inputMatrix();
			}
		}
		
		//info
		else if(first.compareTo("info") == 0){
			switch(cmd.size()){
			case 1:
				System.out.println("LinearAlebric Code. by RangeWING");
				System.out.println(Interpreter.version + " Build: " + Interpreter.build);
				System.out.println("Contact: rangewing@kaist.ac.kr");
				System.out.println();
				break;
				
			case 2:
				String name = cmd.get(1);
				switch(findType(name)){
				case VARIABLE:
					System.out.println(name + "(Variable) : ");
					varTable.get(name).info();
					break;
				case COMMAND:
					System.out.println(name + "(Command): " + cmdTable.get(name));
					break;
				case OPERATOR:
					//TODO
					break;
				case METHOD:
					break;
				default:
					System.out.println("There is nothing named " + name);
				}
				
			default:
				throw new SyntaxException(command, "Too many arguments for 'info'");	
			}
		}
		
		return result;
	}
	
	private LAObject executeMethod(String name, ArrayList<String> params){
		//TODO
		return null;
	}
	
	
	public String findType(LAObject lobj){
		if(lobj instanceof Matrix){
			return "Matrix";
		}
		return "Undefined";
	}
	
	private int findType(String name){
		//Find from variable
		if(varTable.containsKey(name)) return VARIABLE;
		else if(cmdTable.containsKey(name)) return COMMAND;
		else if(opTable.containsKey(name)) return OPERATOR;
		else if(methodTable.containsKey(name)) return METHOD;
		else return -1;
	}
	
	private void addVar(String name, LAObject value){
		if(varTable.containsKey(name)){
			varTable.remove(name);
		}
		varTable.put(name, value);
	}
	
	private String subCommand(ArrayList<String> cmd, int start, int end){
		String sub = "";
		if(end > cmd.size()) end = cmd.size();
		
		for(int i=start; i<end; i++){
			sub += cmd.get(i);
			sub += " ";
		}
		
		return sub;
	}
	
	@SuppressWarnings("unused")
	private void log(String log){
		System.out.println("[LOG] " + log);
	}
	
	private void initCmdTable(){
		cmdTable = new HashMap<String, String>();
		cmdTable.put("var", "Make a Variable. \nUSAGE: var NAME");
		cmdTable.put("info", "See the information of sth. \nUSAGE: info");
		cmdTable.put("new", "Make sth new. [ex. Matrix] \nUSAGE: new TYPE");
	}
	
	private void initOpTable(){
		opTable = new HashMap<String, Integer>();
		opTable.put("+", 2);
		opTable.put("-", 2);
		opTable.put("*", 2);
	}
	
	private void initMethodTable(){
		methodTable = new HashMap<String, String>();
		methodTable.put("elim", "Matrix");
		methodTable.put("rref", "Matrix");
	}
	
}
