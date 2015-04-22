package com.mfino.monitor.constants;

import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author srikanth
 */
public class MonitorPeriodConstants {

	public static final String LAST_15_MIN = "1";
	public static final String LAST_1_HOUR = "2";
	public static final String LAST_5_HOUR = "3";
	public static final String LAST_24_HOUR = "4";
	public static final String LAST_1_WEEK = "5";
	public static final String LAST_1_MONTH = "6";
	public static final Integer[] SUCCESSFUL_SCTL = {
			CmFinoFIX.SCTLStatus_Confirmed,
			CmFinoFIX.SCTLStatus_Distribution_Started,
			CmFinoFIX.SCTLStatus_Distribution_Completed,
			CmFinoFIX.SCTLStatus_Distribution_Failed
			};
	public static final Integer[] FAILED_SCTL = { CmFinoFIX.SCTLStatus_Failed };
	public static final Integer[] PENDING_SCTL = { 
			CmFinoFIX.SCTLStatus_Pending, 
			CmFinoFIX.SCTLStatus_Pending_Resolved,
			CmFinoFIX.SCTLStatus_Pending_Resolved_Processing
			};
	public static final Integer[] PROCESSING_SCTL = {
			CmFinoFIX.SCTLStatus_Inquiry, 
			CmFinoFIX.SCTLStatus_Processing
			};
	public static final Integer[] REVERSALS_SCTL = {
			CmFinoFIX.SCTLStatus_Reverse_Approved,
			CmFinoFIX.SCTLStatus_Reverse_Success,
			CmFinoFIX.SCTLStatus_Reversed,
			CmFinoFIX.SCTLStatus_Reverse_Rejected,
			CmFinoFIX.SCTLStatus_Reverse_Failed,
			CmFinoFIX.SCTLStatus_Reverse_Start,
			CmFinoFIX.SCTLStatus_Reverse_Requested,
			CmFinoFIX.SCTLStatus_Reverse_Initiated,
			CmFinoFIX.SCTLStatus_Reverse_Processing };
	public static final Integer[] INTERMEDIATE_SCTL = {
			CmFinoFIX.SCTLStatus_Distribution_Failed,
			CmFinoFIX.SCTLStatus_Confirmed,
			CmFinoFIX.SCTLStatus_Distribution_Started,
			CmFinoFIX.SCTLStatus_Pending_Resolved,
			CmFinoFIX.SCTLStatus_Pending_Resolved_Processing };
	public static final Integer[] COUNT_SCTL = {
		CmFinoFIX.SCTLStatus_Confirmed,
		CmFinoFIX.SCTLStatus_Distribution_Started,
		CmFinoFIX.SCTLStatus_Distribution_Completed,
		CmFinoFIX.SCTLStatus_Distribution_Failed,
		CmFinoFIX.SCTLStatus_Failed,
		CmFinoFIX.SCTLStatus_Pending, 
		CmFinoFIX.SCTLStatus_Pending_Resolved,
		CmFinoFIX.SCTLStatus_Pending_Resolved_Processing,
		CmFinoFIX.SCTLStatus_Inquiry, 
		CmFinoFIX.SCTLStatus_Processing
		};
}
