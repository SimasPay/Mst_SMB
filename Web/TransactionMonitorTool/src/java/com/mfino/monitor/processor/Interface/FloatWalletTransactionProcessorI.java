package com.mfino.monitor.processor.Interface;

import java.util.List;

import com.mfino.monitor.model.FloatWalletTransaction;

/**
 * @author Srikanth
 * 
 */

public interface FloatWalletTransactionProcessorI   {

	List<FloatWalletTransaction> process(FloatWalletTransaction searchBean);
}
