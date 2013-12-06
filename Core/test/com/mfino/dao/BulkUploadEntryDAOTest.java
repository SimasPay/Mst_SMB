/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.BulkUploadEntryQuery;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.hibernate.Timestamp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Raju
 */
public class BulkUploadEntryDAOTest extends TestCase {

    BulkUploadEntry bue = new BulkUploadEntry();
    BulkUploadEntryDAO dao = new BulkUploadEntryDAO();
    BulkUploadEntryQuery query = new BulkUploadEntryQuery();
    private static Random random = new Random();

    public BulkUploadEntryDAOTest() {
        random.setSeed(System.currentTimeMillis());
    }

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    private void insertData() {
        bue.setAmount(new BigDecimal(Long.MIN_VALUE));
        bue.setCreatedBy(null);
        bue.setDestMDN("12345");
        bue.setLineNumber(Integer.SIZE);
        bue.setUploadID(3l);
        bue.setVersion(Integer.SIZE);
        bue.setStatus(1);
        bue.setLastUpdateTime(new Timestamp());
        bue.setCreateTime(new Timestamp());
        bue.setUpdatedBy("raju");
        dao.save(bue);

    }

    @Test
    public void testGet() {

        insertData();
        assertNotNull(bue.getID());
        BulkUploadEntryDAO service = new BulkUploadEntryDAO();

        List<BulkUploadEntry> results = service.getAll();
        assertTrue(results.size() > 0);
    }
}
