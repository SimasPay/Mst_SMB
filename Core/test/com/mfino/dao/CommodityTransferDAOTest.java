/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sunil
 */
public class CommodityTransferDAOTest {

    private CommodityTransferDAO dao = new CommodityTransferDAO();
    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private SubscriberMDNDAO mdndao = new SubscriberMDNDAO();
    private SubscriberDAO subDao = new SubscriberDAO();
    private TransactionsLogDAO trandao = new TransactionsLogDAO();
    private PocketDAO pocDao = new PocketDAO();
    private PocketTemplateDAO poctDao = new PocketTemplateDAO();
    private mFinoServiceProvider msp = new mFinoServiceProvider();
    private Subscriber sub = new Subscriber();
    private SubscriberMDN mdn1 = new SubscriberMDN();
    private SubscriberMDN mdn2 = new SubscriberMDN();
    private TransactionsLog tlog = new TransactionsLog();
    private PocketTemplate pt = new PocketTemplate();
    private Pocket poc = new Pocket();
    private CommodityTransfer pcmt = new CommodityTransfer();
    private CommodityTransfer pcmt1 = new CommodityTransfer();
    private static Long mdn1ID;
    private static Long mdn2ID;
    private static Random random = new Random();



    public CommodityTransferDAOTest() {
        random.setSeed(System.currentTimeMillis());
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


    @Ignore //this fails in a strange way
    @Test
    public void insertTestData(){
        msp = mspDao.getById(1L);
        System.out.println(msp.getCreatedBy());

        sub.setFirstName("xxx");
        sub.setCurrency("dollar");
        sub.setActivationTime(new Timestamp());
        sub.setCreateTime(new Timestamp());
        sub.setCreatedBy("sas");
        sub.setEmail("sasa");
        sub.setLanguage(Integer.MAX_VALUE);
        sub.setLastName("sdas");
        sub.setLastUpdateTime(new Timestamp());
        sub.setNotificationMethod(Integer.MAX_VALUE);
        sub.setRestrictions(Integer.MAX_VALUE);
        sub.setStatus(Integer.MAX_VALUE);
        sub.setStatusTime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedBy("sasa");

        sub.setmFinoServiceProviderByMSPID(msp);

        subDao.save(sub);


        mdn1.setMDN(getRandomMDN());
        mdn1.setSubscriber(sub);
        mdn1.setCreateTime(new Timestamp());
        mdn1.setRestrictions(new Integer(10));
        mdn1.setStatus(new Integer(10));
        mdn1.setActivationTime(new Timestamp());
        mdn1.setAuthenticationPhoneNumber("sunny");
        mdn1.setAuthenticationPhrase("sunny");
        mdn1.setCreatedBy("sunny");
        mdn1.setDigestedPIN("sds");
        mdn1.setLastTransactionID(Long.MIN_VALUE);
        mdn1.setLastTransactionTime(new Timestamp());
        mdn1.setLastUpdateTime(new Timestamp());
        mdn1.setStatusTime(new Timestamp());
        mdn1.setUpdatedBy("sunny");
        mdn1.setWrongPINCount(Integer.MAX_VALUE);
        mdndao.save(mdn1);

        mdn2.setMDN(getRandomMDN());
        mdn2.setSubscriber(sub);
        mdn2.setCreateTime(new Timestamp());
        mdn2.setRestrictions(new Integer(10));
        mdn2.setStatus(new Integer(10));
        mdn2.setActivationTime(new Timestamp());
        mdn2.setAuthenticationPhoneNumber("sunny");
        mdn2.setAuthenticationPhrase("sunny");
        mdn2.setCreatedBy("sunny");
        mdn2.setDigestedPIN("sds");
        mdn2.setLastTransactionID(Long.MIN_VALUE);
        mdn2.setLastTransactionTime(new Timestamp());
        mdn2.setLastUpdateTime(new Timestamp());
        mdn2.setStatusTime(new Timestamp());
        mdn2.setUpdatedBy("sunny");
        mdn2.setWrongPINCount(Integer.MAX_VALUE);
        mdndao.save(mdn2);

        tlog.setCreateTime(new Timestamp());
        tlog.setCreatedBy("sa");
        tlog.setLastUpdateTime(new Timestamp());
        tlog.setmFinoServiceProviderByMSPID(msp);
        tlog.setMessageCode(Integer.MAX_VALUE);
        tlog.setMessageData("sas");
        tlog.setParentTransactionID(Long.MIN_VALUE);
        tlog.setTransactionTime(new Timestamp());
        tlog.setUpdatedBy("sas");

        trandao.save(tlog);

        pt.setmFinoServiceProviderByMSPID(msp);
        pt.setAllowance(Integer.MAX_VALUE);
        pt.setBankAccountCardType(Integer.MAX_VALUE);
        pt.setBankCode(Integer.MAX_VALUE);
        pt.setCardPANSuffixLength(Integer.MAX_VALUE);
        pt.setCommodity(new Integer(0));
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
        pt.setOperatorCode(new Integer(881));
        pt.setType(new Integer(1));
        pt.setUnits("sd");
        pt.setUpdatedBy("sunny");
        pt.setBillingType(new Integer(0));

        poctDao.save(pt);

        poc.setActivationTime(new Timestamp());
        poc.setCardPAN("sdas");
        poc.setCreateTime(new Timestamp());
        poc.setCreatedBy("sasa");
        poc.setCurrentBalance(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentDailyExpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentDailyTxnsCount(Integer.MAX_VALUE);
        poc.setCurrentMonthlyExpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentMonthlyTxnsCount(Integer.MAX_VALUE);
        poc.setCurrentWeeklyExpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentWeeklyTxnsCount(Integer.MIN_VALUE);
        poc.setIsDefault(Boolean.TRUE);
        poc.setLastBankAuthorizationCode("sas");
        poc.setLastBankRequestCode(Integer.MAX_VALUE);
        poc.setLastBankResponseCode(Integer.MAX_VALUE);
        poc.setLastTransactionTime(new Timestamp());
        poc.setLastUpdateTime(new Timestamp());
        poc.setRestrictions(Integer.MAX_VALUE);
        poc.setStatus(Integer.MAX_VALUE);
        poc.setStatusTime(new Timestamp());
        poc.setUpdatedBy("sas");
        poc.setSubscriberMDNByMDNID(mdn1);
        poc.setPocketTemplate(pt);

        pocDao.save(poc);

        pcmt.setID(getRandomID());
        pcmt.setAmount(new BigDecimal(1000));
        pcmt.setDestMDN(mdn2.getMDN());
        pcmt.setStartTime(new Timestamp());
        pcmt.setSourceMDN(mdn1.getMDN());
        pcmt.setBankAuthorizationCode("sunny");
        pcmt.setBankCode(Integer.MAX_VALUE);
        pcmt.setBankErrorText("sunny");
        pcmt.setBankRejectReason("sunnys");
        pcmt.setBankResponseCode(Integer.MAX_VALUE);
        pcmt.setBankResponseTime(new Timestamp());
        pcmt.setBillingType(Integer.MAX_VALUE);
        pcmt.setBucketType("sunnys");
        pcmt.setCommodity(Integer.MAX_VALUE);
        pcmt.setCreateTime(new Timestamp());
        pcmt.setCurrency("dollar");
        pcmt.setDestCardPAN("bunny");
        pcmt.setDestMDNID(mdn2.getID());
        pcmt.setDestPocketBalance(new BigDecimal(Long.MIN_VALUE));
        pcmt.setDestPocketType(Integer.MAX_VALUE);
        pcmt.setDestPocketID(Long.MIN_VALUE);
        pcmt.setDestSubscriberID(Long.MIN_VALUE);
        pcmt.setDestSubscriberName("sunny");
        pcmt.setEndTime(new Timestamp());
        pcmt.setLastUpdateTime(new Timestamp());
        pcmt.setMsgType(Integer.MAX_VALUE);
        pcmt.setOperatorAuthorizationCode("ssunny");
        pcmt.setOperatorCode(Integer.MAX_VALUE);
        pcmt.setOperatorErrorText("Error");
        pcmt.setOperatorRejectReason("sunnys");
        pcmt.setOperatorResponseCode(Integer.MAX_VALUE);
        pcmt.setOperatorResponseTime(new Timestamp());
        pcmt.setSourceApplication(Integer.MAX_VALUE);
        pcmt.setSourceCardPAN("sda");
        pcmt.setSourcePocketBalance(new BigDecimal(Long.MIN_VALUE));
        pcmt.setSourcePocketType(Integer.MAX_VALUE);
        pcmt.setSourceReferenceID("dssd");
        pcmt.setSourceSubscriberName("sunnys");
        pcmt.setTransferFailureReason(Integer.MAX_VALUE);
        pcmt.setTransferStatus(Integer.MAX_VALUE);
        pcmt.setISO8583_PrimaryAccountNumber("12345");

        pcmt.setUpdatedBy("sd");
        pcmt.setCreatedBy("sas");

        pcmt.setmFinoServiceProviderByMSPID(msp);


        pcmt.setSubscriberBySourceSubscriberID(sub);
        pcmt.setSubscriberMDNBySourceMDNID(mdn1);
        pcmt.setPocketBySourcePocketID(poc);
        pcmt.setTransactionsLogByTransactionID(tlog);
        pcmt.setLastReversalTime(new Timestamp());
        pcmt.setReversalCount(1);


        dao.save(pcmt);


        pcmt1.setID(getRandomID());
        pcmt1.setAmount(new BigDecimal(1000));
        pcmt1.setDestMDN(mdn1.getMDN());
        pcmt1.setStartTime(new Timestamp());
        pcmt1.setSourceMDN(mdn2.getMDN());
        pcmt1.setBankAuthorizationCode("sunny");
        pcmt1.setBankCode(Integer.MAX_VALUE);
        pcmt1.setBankErrorText("sunny");
        pcmt1.setBankRejectReason("sunnys");
        pcmt1.setBankResponseCode(Integer.MAX_VALUE);
        pcmt1.setBankResponseTime(new Timestamp());
        pcmt1.setBillingType(Integer.MAX_VALUE);
        pcmt1.setBucketType("sunnys");
        pcmt1.setCommodity(Integer.MAX_VALUE);
        pcmt1.setCreateTime(new Timestamp());
        pcmt1.setCurrency("dollar");
        pcmt1.setDestCardPAN("bunny");
        pcmt1.setDestMDNID(mdn1.getID());
        pcmt1.setDestPocketBalance(new BigDecimal(Long.MIN_VALUE));
        pcmt1.setDestPocketType(Integer.MAX_VALUE);
        pcmt1.setDestPocketID(Long.MIN_VALUE);
        pcmt1.setDestSubscriberID(Long.MIN_VALUE);
        pcmt1.setDestSubscriberName("sunny");
        pcmt1.setEndTime(new Timestamp());
        pcmt1.setLastUpdateTime(new Timestamp());
        pcmt1.setMsgType(Integer.MAX_VALUE);
        pcmt1.setOperatorAuthorizationCode("ssunny");
        pcmt1.setOperatorCode(Integer.MAX_VALUE);
        pcmt1.setOperatorErrorText("Error");
        pcmt1.setOperatorRejectReason("sunnys");
        pcmt1.setOperatorResponseCode(Integer.MAX_VALUE);
        pcmt1.setOperatorResponseTime(new Timestamp());
        pcmt1.setSourceApplication(Integer.MAX_VALUE);
        pcmt1.setSourceCardPAN("sdahhkjhjk");
        pcmt1.setSourcePocketBalance(new BigDecimal(Long.MIN_VALUE));
        pcmt1.setSourcePocketType(Integer.MAX_VALUE);
        pcmt1.setSourceReferenceID("dssd");
        pcmt1.setSourceSubscriberName("sunnys");
        pcmt1.setTransferFailureReason(Integer.MAX_VALUE);
        pcmt1.setTransferStatus(Integer.MAX_VALUE);
        pcmt1.setISO8583_PrimaryAccountNumber("12345");

        pcmt1.setUpdatedBy("sd");
        pcmt1.setCreatedBy("sas");
        pcmt1.setmFinoServiceProviderByMSPID(msp);


        pcmt1.setSubscriberBySourceSubscriberID(sub);
        pcmt1.setSubscriberMDNBySourceMDNID(mdn2);
        pcmt1.setPocketBySourcePocketID(poc);
        pcmt1.setTransactionsLogByTransactionID(tlog);
        pcmt1.setLastReversalTime(new Timestamp());
        pcmt1.setReversalCount(1);

        dao.save(pcmt1);

        assertTrue(pcmt.getID() > 0);
        assertTrue(pcmt1.getID() > 0);
        assertTrue(mdn1.getID() > 0);
        assertTrue(mdn2.getID() > 0);

        mdn1ID = mdn1.getID();
        mdn2ID = mdn2.getID();
     
    }

    @Ignore
    @Test
    public void testSearchAll() throws Exception {

        CommodityTransferQuery query = new CommodityTransferQuery();

        query.setStart(0);
        query.setLimit(10);

        List results = dao.get(query);

        assertTrue(results.size() > 0);
    }

    @Ignore
    @Test
    public void testSearch() throws Exception {

        CommodityTransferQuery query = new CommodityTransferQuery();

//        query.setDestinationMDN("111");
//        query.setEndDate(new Timestamp());
        query.setSourceMDN("111");

        query.setStart(0);
        query.setLimit(10);

        List results = dao.get(query);
        assertTrue(results.size() > 0);
    }

    @Ignore
    @Test
    public void testSearchBySubscriberMDNID1() throws Exception {

        CommodityTransferQuery query = new CommodityTransferQuery();

     //   query.setSubscriberMDNID(mdn1ID);
        query.setStart(0);
        query.setLimit(10);

        query.setSortString(null);

        List results = dao.get(query);

        assertTrue(results.size() == 1);

    }

    @Ignore
    @Test
    public void testSearchBySubscriberMDNID2() throws Exception {

        CommodityTransferQuery query = new CommodityTransferQuery();

    //    query.setSubscriberMDNID(mdn2ID);
        query.setStart(0);
        query.setLimit(10);

        query.setSortString(null);

        List results = dao.get(query);

        assertTrue(results.size() == 1);

    }


    private static String getRandomMDN()
    {
        String mdn= "1111";
        return mdn+(long)(random.nextDouble()*10000L);
    }

    private static Long getRandomID()
    {
        return (long)(random.nextDouble()*10000L);
    }


}

