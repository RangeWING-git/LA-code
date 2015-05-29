import java.util.Scanner;

import exception.*;

public class LinearAlgebra {
	public static Scanner sc = new Scanner(System.in);
	public static void main(String[] args){
		Interpreter ipt = new Interpreter();
		
		System.out.println("Welcome to LA-Code!");
		System.out.println(Interpreter.version + " Build: " + Interpreter.build);
		System.out.println();
		
		while(true){
			try {
				System.out.print(">> ");
				ipt.execute(sc.nextLine());
			} catch (SyntaxException se) {
				System.out.println(se.getMessage());
			} catch (VariableException ve) {
				System.out.println(ve.getMessage());
				// TODO: handle exception
			}
		}
	}

	
	

}
