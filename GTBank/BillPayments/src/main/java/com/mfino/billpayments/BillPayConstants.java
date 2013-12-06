package com.mfino.billpayments;

/**
 * @author Sasi
 *
 */
public class BillPayConstants {

	public static final String SOURCE_TO_SUSPENSE = "SOURCE_TO_SUSPENSE";
	public static final String SUSPENSE_TO_DESTINATION = "SUSPENSE_TO_DESTINATION";
	public static final String SOURCE_TO_DESTINATION = "SOURCE_TO_DESTINATION";
	
	public static final String TERMINAL_ID_KEY = "TERMINAL_ID";
	public static final String PREFIX = "PREFIX";

	
	//Bill pay events
	/*
	 * Flow completed for below events and notification needs to be sent.
	 */
	public static final String SRC_SUSPENSE_INQ_SUCCESS = "SRC_SUSPENSE_INQ_SUCCESS";
	public static final String SRC_SUSPENSE_INQ_FAILED = "SRC_SUSPENSE_INQ_FAILED"; //notification needs to be sent
	public static final String SRC_SUSPENSE_INQ_TO_BANK = "SRC_SUSPENSE_INQ_TO_BANK";
	
	public static final String SRC_SUSPENSE_CONFIRMATION_SUCCESS = "SRC_SUSPENSE_CONFIRMATION_SUCCESS";
	public static final String SRC_SUSPENSE_CONFIRMATION_FAILED = "SRC_SUSPENSE_CONFIRMATION_FAILED"; //notification needs to be sent
	public static final String SRC_SUSPENSE_CONFIRMATION_TO_BANK = "SRC_SUSPENSE_CONFIRMATION_TO_BANK";
	
	public static final String SUSPENSE_DEST_INQ_SUCCESS = "SUSPENSE_DEST_INQ_SUCCESS";
	public static final String SUSPENSE_DEST_INQ_FAILED = "SUSPENSE_DEST_INQ_FAILED";
	public static final String SUSPENSE_DEST_INQ_TO_BANK = "SUSPENSE_DEST_INQ_TO_BANK";
	
	public static final String SUSPENSE_DEST_CONFIRMATION_SUCCESS = "SUSPENSE_DEST_CONFIRMATION_SUCCESS";
	public static final String SUSPENSE_DEST_CONFIRMATION_FAILED = "SUSPENSE_DEST_CONFIRMATION_FAILED";
	public static final String SUSPENSE_DEST_CONFIRMATION_TO_BANK = "SUSPENSE_DEST_CONFIRMATION_TO_BANK";
	
	public static final String BILLER_INQUIRY_FAILED = "BILLER_INQUIRY_FAILED";
	public static final String BILLER_INQUIRY_PENDING = "BILLER_INQUIRY_PENDING";
	public static final String BILLER_INQUIRY_COMPLETED = "BILLER_INQUIRY_COMPLETED"; // notification needs to be sent
	
	public static final String BILLER_CONFIRMATION_PENDING = "BILLER_CONFIRMATION_PENDING";
	public static final String BILLER_CONFIRMATION_COMPLETED = "BILLER_CONFIRMATION_COMPLETED";
	public static final String BILLER_CONFIRMATION_FAILED = "BILLER_CONFIRMATION_FAILED"; // send notification
	public static final String BILLER_CONFIRMATION_SUCCESSFUL = "BILLER_CONFIRMATION_SUCCESSFUL"; //send notification
}
