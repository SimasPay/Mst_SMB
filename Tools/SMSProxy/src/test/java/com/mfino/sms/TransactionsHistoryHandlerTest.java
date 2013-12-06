/**
 * 
 */
package com.mfino.sms;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactions;
import com.mfino.sms.handlers.TransactionsHistoryHandler;
import com.mfino.util.HibernateUtil;

/**
 * @author Deva
 *
 */
public class TransactionsHistoryHandlerTest {

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
	 * Test method for {@link com.mfino.sms.handlers.TransactionsHistoryHandler#handle()}.
	 */
	@Test
	public void testHandle() {
		
		CMGetTransactions transactionsRequest = new CMGetTransactions();
		transactionsRequest.setMaxCount(4);
		transactionsRequest.setPin("123456");
		transactionsRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		transactionsRequest.setSourceMDN("628811001298");
		transactionsRequest.setGetTransactionType(CmFinoFIX.GetTransactionType_Transfer);
		TransactionsHistoryHandler handler = new TransactionsHistoryHandler(transactionsRequest,"CEKISI.123456");
		handler.handle();
	}

}
