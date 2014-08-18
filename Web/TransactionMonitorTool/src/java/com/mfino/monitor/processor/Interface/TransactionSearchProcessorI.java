package com.mfino.monitor.processor.Interface;

import java.util.List;

import com.mfino.monitor.model.Transaction;


/**
 * @author Srikanth
 * 
 * 
 */

public interface TransactionSearchProcessorI {

	List<Transaction> process(Transaction searchBean); }
