/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.domain.Bank;
import com.mfino.hibernate.Timestamp;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author sunil
 */
public class BankDAOTest extends TestCase {

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    @Test
    public void testInsert() {
        BankDAO service = new BankDAO();

        Bank u = new Bank();
        u.setCreatetime(new Timestamp());
        u.setCreatedby("xchen");
        u.setName("xchen");
        service.save(u);

        assertTrue(u.getId().longValue() > 0);
        Bank results = service.getById(u.getId().longValue());
        assertNotNull(results);
        assertEquals(results.getName(), "xchen");

//        for (int i = 0; i < results.size(); i++) {
//                Bank s = results.get(i);
//                System.out.println("ID::"+s.getId()+"Bank Name::"+s.getName()+
//                        "Description ::"+s.getDescription());
//            }
    }
}
