package exception;

public class SyntaxException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7566941302991489163L;
	
	public SyntaxException(String command, String msg){
		super("SyntaxError: " + msg + "\n at [ " + command + " ]");
	}
	
}
