/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author xchen
 */
public class EnumTextDAOTest extends TestCase {

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    public void testGetWithTagId() {
        EnumTextQuery query = new EnumTextQuery();
        query.setTagId(5049);
//        query.setLanguage(CmFinoFIX.Language_English);
        
        EnumTextDAO dao = new EnumTextDAO();
        List<EnumText> results = dao.get(query);
        assertTrue(results.size() > 0);

        System.out.println("Result count: " + results.size());
    }
}
