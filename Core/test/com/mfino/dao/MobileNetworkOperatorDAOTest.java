/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.domain.MobileNetworkOperator;
import com.mfino.hibernate.Timestamp;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Raju
 */
public class MobileNetworkOperatorDAOTest {

    private MobileNetworkOperatorDAO mnoDao = new MobileNetworkOperatorDAO();

    public MobileNetworkOperatorDAOTest() {
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


        MobileNetworkOperator mno = new MobileNetworkOperator();
        mno.setCreatetime(new Timestamp());
        mno.setDescription("sas1");
        mno.setLastupdatetime(new Timestamp());
        mno.setName("Sass1");
        mno.setStatus(Long.MAX_VALUE);
        mno.setStatustime(new Timestamp());
        mnoDao.save(mno);
    }
}
