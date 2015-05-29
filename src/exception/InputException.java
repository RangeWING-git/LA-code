package exception;

public class InputException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8733750908525405891L;
	
	public InputException(String msg){
		super("InputError: " + msg);
	}
}
