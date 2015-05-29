package exception;

public class VariableException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4301883636225583141L;

	public VariableException(String command, String msg){
		super("VariableError: " + msg + "\n at [ " + command + " ]");
	}
}
