/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionLog;
import com.mfino.hibernate.Timestamp;

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
        MfinoServiceProvider msp = mspDao.getById(1L);
        
        TransactionLog tlog = new TransactionLog();

        tlog.setCreatetime(new Timestamp());
        tlog.setCreatedby("sa");
        tlog.setLastupdatetime(new Timestamp());
        tlog.setMfinoServiceProvider(msp);
        tlog.setMessagecode(Integer.MAX_VALUE);
        tlog.setMessagedata("sas");
        tlog.setParenttransactionid(new BigDecimal(Long.MIN_VALUE));
        tlog.setTransactiontime(new Timestamp());
        tlog.setUpdatedby("sas");


        trandao.save(tlog);

    }
}
