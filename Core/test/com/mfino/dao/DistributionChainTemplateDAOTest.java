/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.mFinoServiceProvider;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sandeepjs
 */
public class DistributionChainTemplateDAOTest {

    private DistributionChainTemplateDAO dctDao = new DistributionChainTemplateDAO();
    private DistributionChainTemp dctTemplate = new DistributionChainTemp();
    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private DistributionChainTemplateQuery query = new DistributionChainTemplateQuery();

    public DistributionChainTemplateDAOTest() {
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

    private void insertData() {

        mFinoServiceProvider msp = new mFinoServiceProvider();

        msp = mspDao.getById(1L);
        System.out.println(msp.getCreatedBy());

        dctTemplate.setmFinoServiceProviderByMSPID(msp);
        dctTemplate.setDescription("description text ");
        dctTemplate.setName(" Temaplate Name 1");
        dctTemplate.setDistributionChainLevelFromTemplateID(null);

        dctDao.save(dctTemplate);
    }

    @Test
    public void testGetDCTByID() {
        insertData();
        query.setId(dctTemplate.getID());
        List results = dctDao.get(query);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testGetDCTByName() {
        insertData();
        query.setDistributionChainTemplateName(dctTemplate.getName());
        List results = dctDao.get(query);
        assertTrue(results.size() == 1);
    }
}
