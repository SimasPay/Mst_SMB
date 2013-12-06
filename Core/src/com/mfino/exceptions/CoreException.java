/**
 * 
 */
package com.mfino.exceptions;

/**
 * Thrown to indicate that an exception occurred while performing core services. 
 * 
 * 
 * @author Chaitanya
 *
 */
public class CoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462568950331178665L;

	private int notificationCode;
	
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
	public CoreException(String message){
		super(message);
	}
	
	
    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public CoreException(String message, Throwable cause){
		super(message, cause);
	}
	
	public CoreException(String message, int notificationCode){
		super(message);
		this.notificationCode = notificationCode;
	}
	
	public int getNotificationCode(){
		return notificationCode;
	}
}
