package com.mfino.zenith.dstv;

/**
 * DSTV Response encapsulation
 * @author POCHADRI
 *
 */
public class DSTVResponseUtil 
{
	// *FindbugsChange*
	// Previous -- public static String SUCCESS ="00";
	//    		   public static String FAILURE ="01";
	public static final String SUCCESS ="00";
	public static final String FAILURE ="01";
	public static String DONT_KNOW ="02";
	
	public static boolean isSuccess(String result)
	{
		return (result!=null&&result.equals(SUCCESS)); 
	}
	
	public static boolean isFailure(String result)
	{
		return (result!=null&&result.equals(FAILURE));
	}
	
	/**
	 * map the response code to what we understand
	 * @param result
	 * @return
	 */
	public static String getResponseCode(Object result)
	{
		if(result instanceof String)
		{
			/**
			 * anything which is not 00 is failure response for us.
			 * Currently we understand only String as a result from web service
			 * if later is is modified to return say List then need to change this check
			 */
			if(((String)result).equals("00"))
			{
				return SUCCESS;
			}
			else
			{
				return FAILURE;
			}
				
		}
		return FAILURE;
	}
}
