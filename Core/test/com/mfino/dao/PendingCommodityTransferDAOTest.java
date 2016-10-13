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
import org.junit.Ignore;
import org.junit.Test;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author sunil
 */
@Ignore // the pending commodity table now does not have an auto increment id. we need to provide that facility before continue this test.
public class PendingCommodityTransferDAOTest {

    private PendingCommodityTransferDAO dao = new PendingCommodityTransferDAO();
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
    private PendingCommodityTransfer pcmt = new PendingCommodityTransfer();
    private PendingCommodityTransfer pcmt1 = new PendingCommodityTransfer();
    private static Long mdn1ID;
    private static Long mdn2ID;

    public PendingCommodityTransferDAOTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        insertTestData();
    }

    @After
    public void tearDown() {
    }

    private void insertTestData(){
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
        sub.setNotificationmethod(Long.MAX_VALUE);
      //  sub.setParentID(Long.MIN_VALUE);
        sub.setRestrictions(Integer.MAX_VALUE);
        sub.setStatus(Integer.MAX_VALUE);
        sub.setStatustime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedby("sasa");

        sub.setMfinoServiceProvider(msp);

        subDao.save(sub);

//Change this if the MDN Already exists in the DB
        mdn1.setMdn("9346110000");
        mdn1.setSubscriber(sub);
        mdn1.setCreatetime(new Timestamp());
        mdn1.setRestrictions(new Integer(10));
        mdn1.setStatus(new Integer(10));
        mdn1.setActivationtime(new Timestamp());
        mdn1.setAuthenticationphonenumber("sunny");
        mdn1.setAuthenticationphrase("sunny");
        mdn1.setCreatedby("sunny");
        mdn1.setDigestedpin("sds");
        mdn1.setLasttransactionid(new BigDecimal(Long.MIN_VALUE));
        mdn1.setLasttransactiontime(new Timestamp());
        mdn1.setLastupdatetime(new Timestamp());
        mdn1.setStatustime(new Timestamp());
        mdn1.setUpdatedby("sunny");
        mdn1.setWrongpincount(Integer.MAX_VALUE);
        mdndao.save(mdn1);
//Change this when ever you run
        mdn2.setMdn("9346220000");
        mdn2.setSubscriber(sub);
        mdn2.setCreatetime(new Timestamp());
        mdn2.setRestrictions(new Integer(10));
        mdn2.setStatus(new Integer(10));
        mdn2.setActivationtime(new Timestamp());
        mdn2.setAuthenticationphonenumber("sunny");
        mdn2.setAuthenticationphrase("sunny");
        mdn2.setCreatedby("sunny");
        mdn2.setDigestedpin("sds");
        mdn2.setLasttransactionid(new BigDecimal(Long.MIN_VALUE));
        mdn2.setLasttransactiontime(new Timestamp());
        mdn2.setLastupdatetime(new Timestamp());
        mdn2.setStatustime(new Timestamp());
//        mdn2.setSubscriberID(1L);
        mdn2.setUpdatedby("sunny");
        mdn2.setWrongpincount(Integer.MAX_VALUE);
        mdndao.save(mdn2);

        tlog.setCreatetime(new Timestamp());
        tlog.setCreatedby("sa");
        tlog.setLastupdatetime(new Timestamp());
        tlog.setMfinoServiceProvider(msp);
        tlog.setMessagecode(Integer.MAX_VALUE);
        tlog.setMessagedata("sas");
        tlog.setParenttransactionid(new BigDecimal(Long.MIN_VALUE));
        tlog.setTransactiontime(new Timestamp());
        tlog.setUpdatedby("sas");

        trandao.save(tlog);

        pt.setMfinoServiceProvider(msp);
        pt.setAllowance(Integer.MAX_VALUE);
        pt.setBankaccountcardtype(Long.MAX_VALUE);
        pt.setBankcode(Long.MAX_VALUE);
        pt.setCardpansuffixlength(Long.MAX_VALUE);
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
        pt.setOperatorcode(Long.MAX_VALUE);
        pt.setMinamountpertransaction(new BigDecimal(Long.MIN_VALUE));
        pt.setMintimebetweentransactions(Integer.MAX_VALUE);
        pt.setMinimumstoredvalue(new BigDecimal(Long.MIN_VALUE));
        pt.setOperatorcode(881L);
        pt.setType(new Integer(1));
        pt.setUnits("sd");
        pt.setUpdatedby("sunny");
        pt.setBillingtype(0L);

        poctDao.save(pt);

        poc.setActivationtime(new Timestamp());
        poc.setCardpan("sdas");
        poc.setCreatetime(new Timestamp());
        poc.setCreatedby("sasa");
        poc.setCurrentbalance(new BigDecimal(Long.MIN_VALUE)+"");
        poc.setCurrentdailyexpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentdailytxnscount(Integer.MAX_VALUE);
        poc.setCurrentmonthlyexpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentmonthlytxnscount(Integer.MAX_VALUE);
        poc.setCurrentweeklyexpenditure(new BigDecimal(Long.MIN_VALUE));
        poc.setCurrentweeklytxnscount(Integer.MIN_VALUE);
        poc.setIsdefault((short)1);
        poc.setLastbankauthorizationcode("sas");
        poc.setLastbankrequestcode(Long.MAX_VALUE);
        poc.setLastbankresponsecode(Long.MAX_VALUE);
        poc.setLasttransactiontime(new Timestamp());
        poc.setLastupdatetime(new Timestamp());
        poc.setRestrictions(Integer.MAX_VALUE);
        poc.setStatus(Integer.MAX_VALUE);
        poc.setStatustime(new Timestamp());
        poc.setUpdatedby("sas");
        poc.setSubscriberMdn(mdn1);
        poc.setPocketTemplate(pt);

        pocDao.save(poc);

        pcmt.setAmount(new BigDecimal(1000));
        pcmt.setDestmdn("9346233462");
        pcmt.setStarttime(new Timestamp());
        pcmt.setSourcemdn("9346233462");
        pcmt.setBankauthorizationcode("sunny");
        pcmt.setBankcode(Long.MAX_VALUE);
        pcmt.setBankerrortext("sunny");
        pcmt.setBankrejectreason("sunnys");
        pcmt.setBankresponsecode(Long.MAX_VALUE);
        pcmt.setBankresponsetime(new Timestamp());
        pcmt.setBillingtype(Long.MAX_VALUE);
        pcmt.setBuckettype("sunnys");
        pcmt.setCommodity(Integer.MAX_VALUE);
        pcmt.setCreatetime(new Timestamp());
        pcmt.setCurrency("dollar");
        pcmt.setDestcardpan("bunny");
        pcmt.setDestmdnid(mdn2.getId());
        pcmt.setDestpocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt.setDestpockettype(Long.MAX_VALUE);
        pcmt.setDestpocketid(new BigDecimal(Long.MIN_VALUE));
        pcmt.setDestsubscriberid(new BigDecimal(Long.MIN_VALUE));
        pcmt.setDestsubscribername("sunny");
        pcmt.setEndtime(new Timestamp());
        pcmt.setLastupdatetime(new Timestamp());
        pcmt.setMsgtype(Integer.MAX_VALUE);
        pcmt.setOperatorauthorizationcode("ssunny");
        pcmt.setOperatorcode(Long.MAX_VALUE);
        pcmt.setOperatorerrortext("Error");
        pcmt.setOperatorrejectreason("sunnys");
        pcmt.setOperatorresponsecode(Long.MAX_VALUE);
        pcmt.setOperatorresponsetime(new Timestamp());
        pcmt.setSourceapplication(Integer.MAX_VALUE);
        pcmt.setSourcecardpan("sda");
        pcmt.setSourcepocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt.setSourcepockettype(Integer.MAX_VALUE);
        pcmt.setSourcereferenceid("dssd");
        pcmt.setSourcesubscribername("sunnys");
        pcmt.setTransferfailurereason(Long.MAX_VALUE);
        pcmt.setTransferstatus(Integer.MAX_VALUE);

        pcmt.setUpdatedby("sd");
        pcmt.setCreatedby("sas");

        pcmt.setMfinoServiceProvider(msp);


        pcmt.setSubscriber(sub);
        pcmt.setSubscriberMdn(mdn1);
        pcmt.setPocket(poc);
        pcmt.setTransactionLog(tlog);
        pcmt.setLastreversaltime(new Timestamp());
        pcmt.setReversalcount(1L);


        dao.save(pcmt);


        pcmt1.setAmount(new BigDecimal(1000));
        pcmt1.setDestmdn("9346233560");
        pcmt1.setStarttime(new Timestamp());
        pcmt1.setSourcemdn("9346233560");
        pcmt1.setBankauthorizationcode("sunny");
        pcmt1.setBankcode(Long.MAX_VALUE);
        pcmt1.setBankerrortext("sunny");
        pcmt1.setBankrejectreason("sunnys");
        pcmt1.setBankresponsecode(Long.MAX_VALUE);
        pcmt1.setBankresponsetime(new Timestamp());
        pcmt1.setBillingtype(Long.MAX_VALUE);
        pcmt1.setBuckettype("sunnys");
        pcmt1.setCommodity(Integer.MAX_VALUE);
        pcmt1.setCreatetime(new Timestamp());
        pcmt1.setCurrency("dollar");
        pcmt1.setDestcardpan("bunny");
        pcmt1.setDestmdnid(mdn1.getId());
        pcmt1.setDestpocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt1.setDestpockettype(Long.MAX_VALUE);
        pcmt1.setDestpocketid(new BigDecimal(Long.MIN_VALUE));
        pcmt1.setDestsubscriberid(new BigDecimal(Long.MIN_VALUE));
        pcmt1.setDestsubscribername("sunny");
        pcmt1.setEndtime(new Timestamp());
        pcmt1.setLastupdatetime(new Timestamp());
        pcmt1.setMsgtype(Integer.MAX_VALUE);
        pcmt1.setOperatorauthorizationcode("ssunny");
        pcmt1.setOperatorcode(Long.MAX_VALUE);
        pcmt1.setOperatorerrortext("Error");
        pcmt1.setOperatorrejectreason("sunnys");
        pcmt1.setOperatorresponsecode(Long.MAX_VALUE);
        pcmt1.setOperatorresponsetime(new Timestamp());
        pcmt1.setSourceapplication(Integer.MAX_VALUE);
        pcmt1.setSourcecardpan("sdahhkjhjk");
        pcmt1.setSourcepocketbalance(new BigDecimal(Long.MIN_VALUE)+"");
        pcmt1.setSourcepockettype(Integer.MAX_VALUE);
        pcmt1.setSourcereferenceid("dssd");
        pcmt1.setSourcesubscribername("sunnys");
        pcmt1.setTransferfailurereason(Long.MAX_VALUE);
        pcmt1.setTransferstatus(Integer.MAX_VALUE);

        pcmt1.setUpdatedby("sd");
        pcmt1.setCreatedby("sas");
        pcmt1.setMfinoServiceProvider(msp);


        pcmt1.setSubscriber(sub);
        pcmt1.setSubscriberMdn(mdn2);
        pcmt1.setPocket(poc);
        pcmt1.setTransactionLog(tlog);
        pcmt1.setLastreversaltime(new Timestamp());
        pcmt1.setReversalcount(1L);

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

    @Ignore //FIXME: this test fails
    @Test
    public void testSearch() throws Exception {

        CommodityTransferQuery query = new CommodityTransferQuery();

        query.setDestinationMDN("934");
        query.setEndDate(new Timestamp());
        query.setSourceMDN("934");
        //query.setTransactionAmount(new Long(1000));
        //query.setTransactionID(new Long(1000));

        query.setStart(0);
        query.setLimit(10);

        List results = dao.get(query);

        assertTrue(results.size() > 0);
    }

    /*
     *
     * This is a unit test for checking the Order by using query object
     *
     */
    @Ignore //FIXME: this test fails
    @Test
    public void testOrderByDesc() throws Exception{

        CommodityTransferQuery query = new CommodityTransferQuery();

        query.setDestinationMDN("934");
        query.setEndDate(new Timestamp());
        query.setSourceMDN("934");
        query.setTransactionAmount(new Long(1000));
        // query.setTransactionID(new Long(1000));

        query.setStart(0);
        query.setLimit(10);

        // "id:desc"
        String sortString = "id" + QueryConstants.COLUMN_ORDER_DELIMITER + QueryConstants.DESC_STRING;

        query.setSortString(sortString);

        List results = dao.get(query);

        BigDecimal idOne = BigDecimal.valueOf(((PendingCommodityTransfer) results.get(0)).getId());
        BigDecimal idTwo = BigDecimal.valueOf(((PendingCommodityTransfer) results.get(1)).getId());

        assertTrue(idOne.longValue() > idTwo.longValue());

    }

    @Ignore //FIXME: this unit test fails
    @Test
    public void testOrderByMdnAsc() throws Exception{
        CommodityTransferQuery query = new CommodityTransferQuery();

        query.setDestinationMDN("934");
        query.setEndDate(new Timestamp());
        query.setSourceMDN("934");
        query.setTransactionAmount(new Long(1000));
        //  query.setTransactionID(new Long(1000));

        query.setStart(0);
        query.setLimit(10);


        //"sourceMdn:asc"
        String sortString = CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceMDN + QueryConstants.COLUMN_ORDER_DELIMITER + QueryConstants.ASC_STRING;
        query.setSortString(sortString);

        List results = dao.get(query);

        Long idOne = new Long(((PendingCommodityTransfer) results.get(0)).getDestmdn());
        Long idTwo = new Long(((PendingCommodityTransfer) results.get(1)).getDestmdn());

        assertTrue(idOne <= idTwo);
    }

    @Ignore //FIXME: This test fails
    @Test
    public void testOrderByMultipleColumns1() throws Exception{

        CommodityTransferQuery query = new CommodityTransferQuery();

        query.setDestinationMDN("934%");
        query.setEndDate(new Timestamp());
        query.setSourceMDN("934%");
        query.setTransactionAmount(new Long(1000));
        //  query.setTransactionID(new Long(1000));

        query.setStart(0);
        query.setLimit(10);

        //"sourceMdn:asc,id:desc"
        String sortString = CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceMDN + QueryConstants.COLUMN_ORDER_DELIMITER + QueryConstants.ASC_STRING +
                QueryConstants.COLUMN_UNIT_DELIMITER + "ID" + QueryConstants.COLUMN_ORDER_DELIMITER + QueryConstants.DESC_STRING;

        query.setSortString(sortString);

        List results = dao.get(query);

        BigDecimal idOne =BigDecimal.valueOf( ((PendingCommodityTransfer) results.get(0)).getId());
        BigDecimal idTwo =BigDecimal.valueOf( ((PendingCommodityTransfer) results.get(1)).getId());


        assertTrue(idOne.longValue() > idTwo.longValue());


        Long idOne1 = new Long(((PendingCommodityTransfer) results.get(0)).getDestmdn());
        Long idTwo1 = new Long(((PendingCommodityTransfer) results.get(1)).getDestmdn());

        assertTrue(idOne1 <= idTwo1);



    }

    @Ignore //FIXME: This test fails
    @Test
    public void testOrderByMultipleColumns2() throws Exception{

        CommodityTransferQuery query = new CommodityTransferQuery();

        query.setDestinationMDN("934");
        query.setEndDate(new Timestamp());
        query.setSourceMDN("934");
        query.setTransactionAmount(new Long(1000));
        //   query.setTransactionID(new Long(1000));

        query.setStart(0);
        query.setLimit(10);

        // id:desc,sourceMdn:asc
        String sortString = "ID" + QueryConstants.COLUMN_ORDER_DELIMITER + QueryConstants.DESC_STRING +
                QueryConstants.COLUMN_UNIT_DELIMITER + CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceMDN + QueryConstants.COLUMN_ORDER_DELIMITER + QueryConstants.ASC_STRING;

        query.setSortString(sortString);

        List results = dao.get(query);

        Long idOne = new Long(((PendingCommodityTransfer) results.get(0)).getDestmdn());
        Long idTwo = new Long(((PendingCommodityTransfer) results.get(1)).getDestmdn());

        assertTrue(idOne <= idTwo);


        BigDecimal idOne1 =BigDecimal.valueOf( ((PendingCommodityTransfer) results.get(0)).getId());
        BigDecimal idTwo1 = BigDecimal.valueOf(((PendingCommodityTransfer) results.get(1)).getId());

        assertTrue(idOne1.longValue() > idTwo1.longValue());
    }

    @Ignore //FIXME: This test fails
    @Test
    public void testSearchBySubscriberMDNID1() throws Exception{

        CommodityTransferQuery query = new CommodityTransferQuery();

    //    query.setSubscriberMDNID(mdn1ID);
        query.setStart(0);
        query.setLimit(10);

        query.setSortString(null);

        List results = dao.get(query);

        assertTrue(results.size() == 1);

    }

    @Ignore //FIXME: This test fails
    @Test
    public void testSearchBySubscriberMDNID2() throws Exception{

        CommodityTransferQuery query = new CommodityTransferQuery();

   //     query.setSubscriberMDNID(mdn2ID);
        query.setStart(0);
        query.setLimit(10);

        query.setSortString(null);

        List results = dao.get(query);

        assertTrue(results.size() == 1);

    }
}

