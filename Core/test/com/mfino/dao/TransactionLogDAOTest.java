/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sandeepjs
 */
public class TransactionLogDAOTest {

    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private TransactionsLogDAO trandao = new TransactionsLogDAO();

    public TransactionLogDAOTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello() {
        mFinoServiceProvider msp = mspDao.getById(1L);
        
        TransactionsLog tlog = new TransactionsLog();

        tlog.setCreateTime(new Timestamp());
        tlog.setCreatedBy("sa");
        tlog.setLastUpdateTime(new Timestamp());
        tlog.setmFinoServiceProviderByMSPID(msp);
        tlog.setMessageCode(Integer.MAX_VALUE);
        tlog.setMessageData("sas");
        tlog.setParentTransactionID(Long.MIN_VALUE);
        tlog.setTransactionTime(new Timestamp());
        tlog.setUpdatedBy("sas");


        trandao.save(tlog);

    }
}
