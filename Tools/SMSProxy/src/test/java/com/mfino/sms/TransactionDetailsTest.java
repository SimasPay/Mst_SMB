/**
 * 
 */
package com.mfino.sms;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactionDetails;
import com.mfino.fix.CmFinoFIX.CMGetTransactions;
import com.mfino.sms.handlers.ShareLoadHistoryHandler;
import com.mfino.sms.handlers.TransactionDetailsHandler;
import com.mfino.util.HibernateUtil;

/**
 * @author admin
 *
 */
public class TransactionDetailsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		HibernateUtil.getCurrentSession().beginTransaction();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		HibernateUtil.getCurrentSession().getTransaction().rollback();
	}

	/**
	 * Test method for {@link com.mfino.sms.handlers.ShareLoadHistoryHandler#handle()}.
	 */
	@Test
	public void testHandle() {
		CMGetTransactionDetails transactionsRequest = new CMGetTransactionDetails();
		transactionsRequest.setPin("123456");
		transactionsRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		transactionsRequest.setSourceMDN("628811001317");
		Long transID = 19L;
		transactionsRequest.setTransID(transID);
		TransactionDetailsHandler handler = new TransactionDetailsHandler(transactionsRequest,"check.123456.19");
		handler.handle();
	}

}
