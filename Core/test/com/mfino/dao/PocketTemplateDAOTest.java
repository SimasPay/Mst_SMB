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

import com.mfino.domain.PocketTemplate;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author sandeepjs
 */
public class PocketTemplateDAOTest {

    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private PocketTemplateDAO poctDao = new PocketTemplateDAO();

    public PocketTemplateDAOTest() {
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
        
        PocketTemplate pt = new PocketTemplate();

        pt.setmFinoServiceProviderByMSPID(msp);
        pt.setAllowance(Integer.MAX_VALUE);
        pt.setBankAccountCardType(Integer.MAX_VALUE);
        pt.setBankCode(Integer.MAX_VALUE);
        pt.setCardPANSuffixLength(Integer.MAX_VALUE);
        pt.setCommodity(Integer.MAX_VALUE);
        pt.setCreateTime(new Timestamp());
        pt.setCreatedBy("sd");
        pt.setDescription("sd");
        pt.setLastUpdateTime(new Timestamp());
        pt.setMaxAmountPerDay(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxAmountPerMonth(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxAmountPerTransaction(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxAmountPerWeek(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxTransactionsPerDay(Integer.MAX_VALUE);
        pt.setMaxTransactionsPerMonth(Integer.MAX_VALUE);
        pt.setMaxTransactionsPerWeek(Integer.MAX_VALUE);
        pt.setMaximumStoredValue(new BigDecimal(Long.MIN_VALUE));
        pt.setOperatorCode(Integer.MAX_VALUE);
        pt.setMinAmountPerTransaction(new BigDecimal(Long.MIN_VALUE));
        pt.setMinTimeBetweenTransactions(Integer.MAX_VALUE);
        pt.setMinimumStoredValue(new BigDecimal(Long.MIN_VALUE));
        pt.setOperatorCode(Integer.MAX_VALUE);
        pt.setType(Integer.MAX_VALUE);
        pt.setUnits("sd");
        pt.setUpdatedBy("dsd");

        poctDao.save(pt);

    }
}
