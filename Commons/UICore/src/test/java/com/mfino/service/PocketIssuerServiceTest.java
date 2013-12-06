/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.domain.PocketTemplate;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class PocketIssuerServiceTest {

	@Autowired
	@Qualifier("PocketTemplateServiceImpl")
	private PocketTemplateService pocketTemplateService;
    
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
    public void testCompatible1(){
        PocketTemplate template1 = new PocketTemplate();
        template1.setType(CmFinoFIX.PocketType_SVA);
        template1.setCommodity(CmFinoFIX.Commodity_Airtime);
        template1.setBillingType(CmFinoFIX.BillingType_PostPaid);
        template1.setBankCode(null);
        template1.setBankAccountCardType(null);
        template1.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);

        PocketTemplate template2 = new PocketTemplate();
        template2.setType(CmFinoFIX.PocketType_SVA);
        template2.setCommodity(CmFinoFIX.Commodity_Airtime);
        template2.setBillingType(CmFinoFIX.BillingType_PostPaid);
        template2.setBankCode(null);
        template2.setBankAccountCardType(null);
        template2.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);

       Assert.assertEquals(pocketTemplateService.areCompatible(template1, template2), true);
    }
    
    @Test
    public void testCompatible2(){
        PocketTemplate template1 = new PocketTemplate();
        template1.setType(CmFinoFIX.PocketType_SVA);
        template1.setCommodity(CmFinoFIX.Commodity_Airtime);
        template1.setBillingType(CmFinoFIX.BillingType_PostPaid);
        template1.setBankCode(null);
        template1.setBankAccountCardType(null);
        template1.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);

        PocketTemplate template2 = new PocketTemplate();
        template2.setType(CmFinoFIX.PocketType_BOBAccount);
        template2.setCommodity(CmFinoFIX.Commodity_Airtime);
        template2.setBillingType(CmFinoFIX.BillingType_PostPaid);
        template2.setBankCode(null);
        template2.setBankAccountCardType(null);
        template2.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);

       Assert.assertEquals(pocketTemplateService.areCompatible(template1, template2), false);
    }

    @Test
    public void testCompatible3(){
        PocketTemplate template1 = new PocketTemplate();
        template1.setType(CmFinoFIX.PocketType_BankAccount);
        template1.setCommodity(CmFinoFIX.Commodity_Money);
        template1.setBillingType(null);
        template1.setBankCode(153);    // hard coding Sinarmas bank as 153
        template1.setBankAccountCardType(CmFinoFIX.BankAccountCardType_CreditCard);
        template1.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);

        PocketTemplate template2 = new PocketTemplate();
        template2.setType(CmFinoFIX.PocketType_BankAccount);
        template2.setCommodity(CmFinoFIX.Commodity_Money);
        template2.setBillingType(null);
        template2.setBankCode(153);  // hard coding Sinarmas bank as 153
        template2.setBankAccountCardType(CmFinoFIX.BankAccountCardType_CreditCard);
        template2.setOperatorCode(null);

       Assert.assertEquals(pocketTemplateService.areCompatible(template1, template2), false);
    }

    @Test
    public void testCompatible4(){
        PocketTemplate template1 = new PocketTemplate();
        template1.setType(CmFinoFIX.PocketType_BankAccount);
        template1.setCommodity(CmFinoFIX.Commodity_Money);
        template1.setBillingType(null);
        template1.setBankCode(153);  // hard coding Sinarmas bank as 153
        template1.setBankAccountCardType(CmFinoFIX.BankAccountCardType_CreditCard);
        template1.setOperatorCode(null);

        PocketTemplate template2 = new PocketTemplate();
        template2.setType(CmFinoFIX.PocketType_BankAccount);
        template2.setCommodity(CmFinoFIX.Commodity_Money);
        template2.setBillingType(null);
        template2.setBankCode(153);  // hard coding Sinarmas bank as 153 
        template2.setBankAccountCardType(CmFinoFIX.BankAccountCardType_CreditCard);
        template2.setOperatorCode(null);

       Assert.assertEquals(pocketTemplateService.areCompatible(template1, template2), true);
    }
    
}
