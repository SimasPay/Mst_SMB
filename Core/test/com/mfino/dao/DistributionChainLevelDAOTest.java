/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.dao.query.DistributionChainLevelQuery;
import com.mfino.domain.DistributionChainLvl;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.MfinoServiceProvider;

/**
 *
 * @author sandeepjs
 */
public class DistributionChainLevelDAOTest {

    public DistributionChainLevelDAOTest() {
    }

    private DistributionChainTemplateDAO dctDao = new DistributionChainTemplateDAO();
    private DistributionChainTemp dctTemplate = new DistributionChainTemp();
    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private DistributionChainLevelQuery query = new DistributionChainLevelQuery();
    private DistributionChainLevelDAO dclDao = new DistributionChainLevelDAO();
    private DistributionChainLvl dcl = new DistributionChainLvl();




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

    private void insertData() {

        MfinoServiceProvider msp = new MfinoServiceProvider();

        msp = mspDao.getById(1L);
        System.out.println(msp.getCreatedby());

        dctTemplate.setMfinoServiceProvider(msp);
        dctTemplate.setDescription("description text ");
        dctTemplate.setName(" Temaplate Name 1");
        
        dctDao.save(dctTemplate);


        dcl.setCommission(new BigDecimal(1000));
        dcl.setDistributionChainTemp(dctTemplate);
        dcl.setDistributionlevel(new Integer(1));
        dcl.setPermissions(new Integer(1));

        dclDao.save(dcl);
        
    }

    @Test
    public void testGetDCLByID() {
        insertData();
        query.setId(dcl.getId().longValue());
        List results = dclDao.get(query);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testGetDCLByDCT() {
        insertData();
        query.setDistributionChainTemplateID(dctTemplate.getId().longValue());
        List results = dclDao.get(query);
        assertTrue(results.size() == 1);
    }

}