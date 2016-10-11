/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.domain.Region;
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
public class RegionDaoTest{

    public RegionDaoTest() {
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
        RegionDAO dao = new RegionDAO();
        Region reg = new Region();
        reg.setCreatetime(new Timestamp());
        reg.setLastupdatetime(new Timestamp());
        reg.setCreatedby("Raju");
        reg.setUpdatedby("mfino");
        reg.setRegionname("Hyderabad");
        reg.setRegioncode("1001");
        reg.setDescription("TestCase");
        CompanyDAO comp = new CompanyDAO();
        reg.setCompany(comp.getById(1L));
        dao.save(reg);

    }
}
