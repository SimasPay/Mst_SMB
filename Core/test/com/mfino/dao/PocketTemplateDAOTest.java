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
import com.mfino.domain.PocketTemplate;
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

        MfinoServiceProvider msp = mspDao.getById(1L);
        
        PocketTemplate pt = new PocketTemplate();

        pt.setMfinoServiceProvider(msp);
        pt.setAllowance(Integer.MAX_VALUE);
        pt.setBankaccountcardtype(Integer.MAX_VALUE);
        pt.setBankcode(Integer.MAX_VALUE);
        pt.setCardpansuffixlength(Integer.MAX_VALUE);
        pt.setCommodity(Integer.MAX_VALUE);
        pt.setCreatetime(new Timestamp());
        pt.setCreatedby("sd");
        pt.setDescription("sd");
        pt.setLastupdatetime(new Timestamp());
        pt.setMaxamountperday(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxamountpermonth(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxamountpertransaction(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxamountperweek(new BigDecimal(Long.MIN_VALUE));
        pt.setMaxtransactionsperday(Integer.MAX_VALUE);
        pt.setMaxtransactionspermonth(Integer.MAX_VALUE);
        pt.setMaxtransactionsperweek(Integer.MAX_VALUE);
        pt.setMaximumstoredvalue(new BigDecimal(Long.MIN_VALUE));
        pt.setOperatorcode(Integer.MAX_VALUE);
        pt.setMaxamountpertransaction(new BigDecimal(Long.MIN_VALUE));
        pt.setMintimebetweentransactions(Integer.MAX_VALUE);
        pt.setMinimumstoredvalue(new BigDecimal(Long.MIN_VALUE));
        pt.setOperatorcode(Integer.MAX_VALUE);
        pt.setType(Integer.MAX_VALUE);
        pt.setUnits("sd");
        pt.setUpdatedby("dsd");

        poctDao.save(pt);

    }
}
