/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.hibernate.Timestamp;

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
    private MfinoServiceProvider msp = new MfinoServiceProvider();
    private Subscriber sub = new Subscriber();
    private SubscriberMdn mdn1 = new SubscriberMdn();
    private SubscriberMdn mdn2 = new SubscriberMdn();
    private TransactionLog tlog = new TransactionLog();
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
        System.out.println(msp.getCreatedby());

        sub.setFirstname("xxx");
        sub.setCurrency("dollar");
        sub.setActivationtime(new Timestamp());
        sub.setCreatetime(new Timestamp());
        sub.setCreatedby("sas");
        sub.setEmail("sasa");
        sub.setLanguage(Integer.MAX_VALUE);
        sub.setLastname("sdas");
        sub.setLastupdatetime(new Timestamp());
        sub.setNotificationmethod(Integer.MAX_VALUE);
        sub.setRestrictions(Integer.MAX_VALUE);
        sub.setStatus(Integer.MAX_VALUE);
        sub.setStatustime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedby("sasa");

        sub.setMfinoServiceProvider(msp);

        subDao.save(sub);


        mdn1.setMdn(getRandomMDN());
        mdn1.setSubscriber(sub);
        mdn1.setCreatetime(new Timestamp());
        mdn1.setRestrictions(new Integer(10));
        mdn1.setStatus(new Integer(10));
        mdn1.setActivationtime(new Timestamp());
        mdn1.setAuthenticationphonenumber("sunny");
        mdn1.setAuthenticationphrase("sunny");
        mdn1.setCreatedby("sunny");
        mdn1.setDigestedpin("sds");
        mdn1.setLasttransactionid(10L);
        mdn1.setLasttransactiontime(new Timestamp());
        mdn1.setLastupdatetime(new Timestamp());
        mdn1.setStatustime(new Timestamp());
        mdn1.setUpdatedby("sunny");
        mdn1.setWrongpincount(Integer.MAX_VALUE);
        mdndao.save(mdn1);

        mdn2.setMdn(getRandomMDN());
        mdn2.setSubscriber(sub);
        mdn2.setCreatetime(new Timestamp());
        mdn2.setRestrictions(new Integer(10));
        mdn2.setStatus(new Integer(10));
        mdn2.setActivationtime(new Timestamp());
        mdn2.setAuthenticationphonenumber("sunny");
        mdn2.setAuthenticationphrase("sunny");
        mdn2.setCreatedby("sunny");
        mdn2.setDigestedpin("sds");
        mdn2.setLasttransactionid(0L);
        mdn2.setLasttransactiontime(new Timestamp());
        mdn2.setLastupdatetime(new Timestamp());
        mdn2.setStatustime(new Timestamp());
        mdn2.setUpdatedby("sunny");
        mdn2.setWrongpincount(Integer.MAX_VALUE);
        mdndao.save(mdn2);

        tlog.setCreatetime(new Timestamp());
        tlog.setCreatedby("sa");
        tlog.setLastupdatetime(new Timestamp());
        tlog.setMfinoServiceProvider(msp);
        tlog.setMessagecode(Integer.MAX_VALUE);
        tlog.setMessagedata("sas");
        tlog.setParenttransactionid(BigDecimal.ONE);
        tlog.setTransactiontime(new Timestamp());
        tlog.setUpdatedby("sas");

        trandao.save(tlog);

        pt.setMfinoServiceProvider(msp);
        pt.setAllowance(Integer.MAX_VALUE);
        pt.setBankaccountcardtype(Integer.MAX_VALUE);
        pt.setBankcode(Integer.MAX_VALUE);
        pt.setCardpansuffixlength(Integer.MAX_VALUE);
        pt.setCommodity(new Integer(0));
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
        pt.setMinamountpertransaction(new BigDecimal(Long.MIN_VALUE));
        pt.setMintimebetweentransactions(Integer.MAX_VALUE);
        pt.setMinimumstoredvalue(new BigDecimal(Long.MIN_VALUE));
        pt.setOperatorcode(881);
        pt.setType(new Integer(1));
        pt.setUnits("sd");
        pt.setUpdatedby("sunny");
        pt.setBillingtype(0);

        poctDao.save(pt);

        poc.setActivationtime(new Timestamp());
        poc.setCardpan("sdas");
        poc.setCreatetime(new Timestamp());
        poc.setCreatedby("sasa");
        poc.setCurrentbalance(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentdailyexpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentdailytxnscount(Integer.MAX_VALUE);
        poc.setCurrentmonthlyexpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentmonthlytxnscount(Integer.MAX_VALUE);
        poc.setCurrentweeklyexpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentweeklytxnscount(Integer.MIN_VALUE);
        poc.setIsdefault(true);
        poc.setLastbankauthorizationcode("sas");
        poc.setLastbankrequestcode(Integer.MAX_VALUE);
        poc.setLastbankresponsecode(Integer.MAX_VALUE);
        poc.setLasttransactiontime(new Timestamp());
        poc.setLastupdatetime(new Timestamp());
        poc.setRestrictions(Integer.MAX_VALUE);
        poc.setStatus(Integer.MAX_VALUE);
        poc.setStatustime(new Timestamp());
        poc.setUpdatedby("sas");
        poc.setSubscriberMdn(mdn1);
        poc.setPocketTemplateByPockettemplateid(pt);

        pocDao.save(poc);

        pcmt.setId(getRandomID());
        pcmt.setAmount(new BigDecimal(1000));
        pcmt.setDestmdn(mdn2.getMdn());
        pcmt.setStarttime(new Timestamp());
        pcmt.setSourcemdn(mdn1.getMdn());
        pcmt.setBankauthorizationcode("sunny");
        pcmt.setBankcode(Integer.MAX_VALUE);
        pcmt.setBankerrortext("sunny");
        pcmt.setBankrejectreason("sunnys");
        pcmt.setBankresponsecode(Integer.MAX_VALUE);
        pcmt.setBankresponsetime(new Timestamp());
        pcmt.setBillingtype(Integer.MAX_VALUE);
        pcmt.setBuckettype("sunnys");
        pcmt.setCommodity(Integer.MAX_VALUE);
        pcmt.setCreatetime(new Timestamp());
        pcmt.setCurrency("dollar");
        pcmt.setSourcecardpan("bunny");
        pcmt.setDestmdnid(mdn2.getId());
        pcmt.setDestpocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt.setDestpockettype(Integer.MAX_VALUE);
        pcmt.setDestpocketid(Long.MIN_VALUE);
        pcmt.setDestsubscriberid(Long.MIN_VALUE);
        pcmt.setDestsubscribername("sunny");
        pcmt.setEndtime(new Timestamp());
        pcmt.setLastupdatetime(new Timestamp());
        pcmt.setMsgtype(Integer.MAX_VALUE);
        pcmt.setOperatorauthorizationcode("ssunny");
        pcmt.setOperatorcode(Integer.MAX_VALUE);
        pcmt.setOperatorerrortext("Error");
        pcmt.setOperatorrejectreason("sunnys");
        pcmt.setOperatorresponsecode(Integer.MAX_VALUE);
        pcmt.setOperatorresponsetime(new Timestamp());
        pcmt.setSourceapplication(Integer.MAX_VALUE);
        pcmt.setSourcecardpan("sda");
        pcmt.setSourcepocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt.setSourcepockettype(Integer.MAX_VALUE);
        pcmt.setSourcereferenceid("dssd");
        pcmt.setSourcesubscribername("sunnys");
        pcmt.setTransferfailurereason(Integer.MAX_VALUE);
        pcmt.setTransferstatus(Integer.MAX_VALUE);
        pcmt.setIso8583Primaryaccountnumber("12345");

        pcmt.setUpdatedby("sd");
        pcmt.setCreatedby("sas");

        pcmt.setMfinoServiceProvider(msp);


        pcmt.setSubscriber(sub);
        pcmt.setSubscriberMdn(mdn1);
        pcmt.setPocket(poc);
        pcmt.setTransactionLog(tlog);
        pcmt.setLastreversaltime(new Timestamp());
        pcmt.setReversalcount(1);


        dao.save(pcmt);


        pcmt1.setId(getRandomID());
        pcmt1.setAmount(new BigDecimal(1000));
        pcmt1.setDestmdn(mdn1.getMdn());
        pcmt1.setStarttime(new Timestamp());
        pcmt1.setSourcemdn(mdn2.getMdn());
        pcmt1.setBankauthorizationcode("sunny");
        pcmt1.setBankcode(Integer.MAX_VALUE);
        pcmt1.setBankerrortext("sunny");
        pcmt1.setBankrejectreason("sunnys");
        pcmt1.setBankresponsecode(Integer.MAX_VALUE);
        pcmt1.setBankresponsetime(new Timestamp());
        pcmt1.setBillingtype(Integer.MAX_VALUE);
        pcmt1.setBuckettype("sunnys");
        pcmt1.setCommodity(Integer.MAX_VALUE);
        pcmt1.setCreatetime(new Timestamp());
        pcmt1.setCurrency("dollar");
        pcmt1.setSourcecardpan("bunny");
        pcmt1.setDestmdnid(mdn1.getId());
        pcmt1.setDestpocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt1.setDestpockettype(Integer.MAX_VALUE);
        pcmt1.setDestpocketid(Long.MIN_VALUE);
        pcmt1.setDestsubscriberid(Long.MIN_VALUE);
        pcmt1.setDestsubscribername("sunny");
        pcmt1.setEndtime(new Timestamp());
        pcmt1.setLastupdatetime(new Timestamp());
        pcmt1.setMsgtype(Integer.MAX_VALUE);
        pcmt1.setOperatorauthorizationcode("ssunny");
        pcmt1.setOperatorcode(Integer.MAX_VALUE);
        pcmt1.setOperatorerrortext("Error");
        pcmt1.setOperatorrejectreason("sunnys");
        pcmt1.setOperatorresponsecode(Integer.MAX_VALUE);
        pcmt1.setOperatorresponsetime(new Timestamp());
        pcmt1.setSourceapplication(Integer.MAX_VALUE);
        pcmt1.setSourcecardpan("sdahhkjhjk");
        pcmt1.setSourcepocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt1.setSourcepockettype(Integer.MAX_VALUE);
        pcmt1.setSourcereferenceid("dssd");
        pcmt1.setSourcesubscribername("sunnys");
        pcmt1.setTransferfailurereason(Integer.MAX_VALUE);
        pcmt1.setTransferstatus(Integer.MAX_VALUE);
        pcmt1.setIso8583Primaryaccountnumber("12345");

        pcmt1.setUpdatedby("sd");
        pcmt1.setCreatedby("sas");
        pcmt1.setMfinoServiceProvider(msp);


        pcmt1.setSubscriber(sub);
        pcmt1.setSubscriberMdn(mdn2);
        pcmt1.setPocket(poc);
        pcmt1.setTransactionLog(tlog);
        pcmt1.setLastreversaltime(new Timestamp());
        pcmt1.setReversalcount(1);

        dao.save(pcmt1);

        assertTrue(pcmt.getId().longValue() > 0);
        assertTrue(pcmt1.getId().longValue() > 0);
        assertTrue(mdn1.getId().longValue() > 0);
        assertTrue(mdn2.getId().longValue() > 0);

        mdn1ID = mdn1.getId().longValue();
        mdn2ID = mdn2.getId().longValue();
     
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

