/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCashFlow;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRPendingCommodityTransfer;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CashFlowProcessor;

/**
 *
 * @author sunil
 */
@Service("CashFlowProcessorImpl")
public class CashFlowProcessorImpl extends BaseFixProcessor implements CashFlowProcessor{

    
    private String state = null;
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    protected void updateMessage(CommodityTransfer c,
            PendingCommodityTransfer c2,
            CMJSCashFlow.CGEntries entry, Long pocketId) {
        entry.setID(c.getId().longValue());
        entry.setTransactionID(c.getTransactionLog().getId().longValue());
        entry.setJSMsgType(((Long)c.getMsgtype()).intValue());
        entry.setTransferStatus(((Long)c.getTransferstatus()).intValue());
        entry.setTransferStateText(state);
        if (c.getSourcereferenceid() != null) {
            entry.setSourceReferenceID(c.getSourcereferenceid());
        }
        if (c.getSourcemdn() != null) {
            entry.setSourceMDN(c.getSourcemdn());
        }
        entry.setSourceMDNID(c.getSubscriberMdn().getId().longValue());
        entry.setSourceSubscriberID(c.getSubscriber().getId().longValue());
        if (c.getSourcesubscribername() != null) {
            entry.setSourceSubscriberName(c.getSourcesubscribername());
        }
        if (c.getSubscriber().getMfinoUserBySubscriberuserid() != null) {
            entry.setSourceUserName(c.getSubscriber().getMfinoUserBySubscriberuserid().getUsername());
        }
        entry.setSourcePocketType(((Long)c.getSourcepockettype()).intValue());
        entry.setSourcePocketID(c.getPocket().getId().longValue());
        if (c.getSourcepocketbalance() != null) {
            entry.setSourcePocketBalance(new BigDecimal(c.getSourcepocketbalance()));
        }
        if (c.getDestmdn() != null) {
            entry.setDestMDN(c.getDestmdn());
        }
        if (c.getUicategory() != null) {
            entry.setTransactionUICategory(c.getUicategory().intValue());
            entry.setTransactionUICategoryText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, c.getUicategory()));
        }
        if (c.getDestmdnid() != null) {
            entry.setDestMDNID(c.getDestmdnid().longValue());
        }
        if (c.getDestsubscriberid() != null) {
            entry.setDestSubscriberID(c.getDestsubscriberid().longValue());
            SubscriberDAO destSubDao = DAOFactory.getInstance().getSubscriberDAO();
            Subscriber sub = destSubDao.getById(c.getDestsubscriberid().longValue());
            if (sub.getMfinoUserBySubscriberuserid() != null) {
                entry.setDestnUserName(sub.getMfinoUserBySubscriberuserid().getUsername());
            }
        }
        if (c.getDestsubscribername() != null) {
            entry.setDestSubscriberName(c.getDestsubscribername());
        }
        if (c.getDestpockettype() != null) {
            entry.setDestPocketType(c.getDestpockettype().intValue());
        }
        if (c.getDestpocketid() != null) {
            entry.setDestPocketID(c.getDestpocketid().longValue());
        }
        if (c.getDestpocketbalance() != null) {
            entry.setDestPocketBalance(new BigDecimal(c.getDestpocketbalance()));
        }
        if (c.getBillingtype() != null) {
            entry.setBillingType(c.getBillingtype().intValue());
        }
        entry.setAmount(c.getAmount());
        entry.setCommodity(((Long)c.getCommodity()).intValue());
        if (c.getBuckettype() != null) {
            entry.setBucketType(c.getBuckettype());
        }
        entry.setSourceApplication(((Long)c.getSourceapplication()).intValue());
        if (c.getCurrency() != null) {
            entry.setCurrency(c.getCurrency());
        }
        if (c.getLastupdatetime() != null) {
            entry.setLastUpdateTime(c.getLastupdatetime());
        }
        if (c.getUpdatedby() != null) {
            entry.setUpdatedBy(c.getUpdatedby());
        }
        if (c.getLetterOfPurchase() != null) {
            if (c.getLetterOfPurchase().getActualamountpaid() != null) {
                entry.setPaidAmount(c.getLetterOfPurchase().getActualamountpaid());
            }
        }
        if (c.getSourceterminalid() != null) {
            entry.setSourceTerminalID(c.getSourceterminalid());
        }

        if (pocketId != null) {
            entry.setSourceDestnPocketID(pocketId);
        }
        if (c2 != null) {
            if ((Short)c2.getOperatoractionrequired() != null) {
                entry.setOperatorActionRequired(c2.getOperatoractionrequired() != 0);
            }
            if ((Short)c2.getLocalrevertrequired() != null) {
                entry.setLocalRevertRequired(c2.getLocalrevertrequired() != 0);
            }
            if ((Short)c2.getBankreversalrequired() != null) {
                entry.setBankReversalRequired(c2.getBankreversalrequired() != 0);
            }
        }
        if(c.getCsraction() != null) {
            entry.setCSRAction(c.getCsraction().intValue());
        }
        if (c.getBankcode() != null) {
            entry.setBankCode(c.getBankcode().intValue());
        }
        entry.setStartTime(c.getStarttime());
        entry.setAmountText(c.getAmount() + GeneralConstants.SINGLE_SPACE + c.getCurrency());
        entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, c.getTransferstatus()));
        entry.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, c.getCommodity()));
        entry.setAccessMethodText(channelCodeService.getChannelNameBySourceApplication(((Long)c.getSourceapplication()).intValue()));
        entry.setTransferFailureReasonText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, c.getTransferfailurereason()));
        entry.setSourcePocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SourcePocketType, null, c.getSourcepockettype()));
        entry.setDestPocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_DestPocketType, null, c.getDestpockettype()));
        entry.setBillingTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BillingType, null, c.getBillingtype()));
        entry.setBucketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType, null, c.getBuckettype()));
        entry.setBankResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankResponseCode, null, c.getBankresponsecode()));
        entry.setOperatorResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorResponseCode, null, c.getOperatorresponsecode()));
        entry.setOperatorCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorCodeForRouting, null, c.getOperatorcode()));
//        entry.setBankCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting, null, c.getBankCode()));
        entry.setRecordVersion(((Long)c.getVersion()).intValue());
    }
    /*
     *  This Method is to be used when we are doing a Group By Opearation
     * @ c is returned record from the group by clause
     * @ entry is the entry record to be updated for a message
     * @ SubtotalBy accepts a parameter on which you are trying to do a group by operation
     * @ isSource is used to identify if the MDN is to be used as Source or as Destination ( Specific to Transaction Type)
     * @ idCounter is a variable to distinguish each record in the UI so as each record will be treated individually
     */

    protected void groupByUpdateMessage(Object[] c, CMJSCashFlow.CGEntries entry, Integer SubtotalBy, boolean isSource, int idCounter, Long pocketId) {
        if (CmFinoFIX.SubtotalBy_Access_Method.equals(SubtotalBy)) {
            entry.setSourceApplication((Integer) c[0]);
            entry.setAccessMethodText(channelCodeService.getChannelNameBySourceApplication((Integer) c[0]));
        } else if (CmFinoFIX.SubtotalBy_Commodity_Type.equals(SubtotalBy)) {
            entry.setCommodity((Integer) c[0]);
            entry.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, (Integer) c[0]));
        } else if (CmFinoFIX.SubtotalBy_Transaction_Status.equals(SubtotalBy)) {
            entry.setTransferStatus((Integer) c[0]);
            entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, (Integer) c[0]));
        } else if (CmFinoFIX.SubtotalBy_Transaction_Type.equals(SubtotalBy)) {
            entry.setTransactionUICategory((Integer) c[0]);
            entry.setTransactionUICategoryText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, (Integer) c[0]));
        } else if (CmFinoFIX.SubtotalBy_Buy_Sell.equals(SubtotalBy)) {
            if (isSource) {
                entry.setSourcePocketID(((Pocket) c[0]).getId().longValue());
            } else {
                entry.setDestPocketID((Long) c[0]);
            }
        }
        entry.setAmount((BigDecimal) c[1]);
        entry.setTransferStateText(state);
        if (pocketId != null) {
            entry.setSourceDestnPocketID(pocketId);
        }
        //This needs to be set so as differentiate records from one another
        entry.setID((long) (idCounter));

    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSCashFlow realMsg = (CMJSCashFlow) msg;

        CommodityTransferDAO dao = DAOFactory.getInstance().getCommodityTransferDAO();
        PendingCommodityTransferDAO pendingDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
        CompanyDAO companyDAO = DAOFactory.getInstance().getCompanyDAO();
        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            CommodityTransferQuery query = new CommodityTransferQuery();
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            //this is used only for H2HMerchantsReports Generations
            if(realMsg.getCompanyID()!=null){
                query.setCompany(companyDAO.getById(realMsg.getCompanyID()));
            }
            query.setSourceDestnMDN(realMsg.getSourceDestnMDN());
            query.setId(realMsg.getIDSearch());
            SubscriberMDNDAO subsriberMdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
            SubscriberMdn subscriberMdn = subsriberMdnDAO.getByMDN(realMsg.getSourceDestnMDN());
            Long subcriberId = 0L;
            if (subscriberMdn != null) {
                subcriberId = subscriberMdn.getId().longValue();
            }

            PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();

            PocketQuery pocketQuery = new PocketQuery();
            pocketQuery.setMdnIDSearch(subcriberId);
            pocketQuery.setIsDefault(Boolean.TRUE);
            pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
            pocketQuery.setCommodity(CmFinoFIX.Commodity_Airtime);

            //We should not get more than one pocket returned that is default and also of Type SVA

            List<Pocket> pocketResults = pocketDAO.get(pocketQuery);
            Long pocketId = 0L;
            Pocket sourcePocket = null;
            if (pocketResults.size() > 0) {
                sourcePocket = pocketResults.get(0);
                pocketId = sourcePocket.getId().longValue();
            }

            if (pocketId != null) {
                query.setSourceDestnPocket(sourcePocket);
                query.setSourceDestnMDN(null);
                realMsg.setSourceDestnPocketID(pocketId);
            }

            query.setTransferStatus(realMsg.getTransferStatus());
            //This Specific code is to make the Subset TransactionsTransferStatus Independent of TransferStatus
            if (realMsg.getTransactionsTransferStatus() == CmFinoFIX.TransactionsTransferStatus_Completed) {
                query.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
            } else if (realMsg.getTransactionsTransferStatus() == CmFinoFIX.TransactionsTransferStatus_Failed) {
                query.setTransferStatus(CmFinoFIX.TransferStatus_Failed);
            }
            if (realMsg.getTransactionType() != null) {
                if (realMsg.getTransactionType().equals(CmFinoFIX.TransactionType_Buy)) {
                    query.setSourceDestnPocket(null);
                    query.setDestinationPocketID(pocketId);
                    query.setSourcePocket(null);
                } else if (realMsg.getTransactionType().equals(CmFinoFIX.TransactionType_Sell)) {
                    query.setSourceDestnPocket(null);
                    query.setDestinationPocketID(null);
                    query.setSourcePocket(sourcePocket);
                }
            }
            query.setMsgType(realMsg.getTransactionUICategory());
            query.setSourceMDN(realMsg.getSourceMDN());
            query.setDestinationMDN(realMsg.getDestMDN());
            query.setSourceApplicationSearch(realMsg.getSourceApplicationSearch());
            query.setSubTotalBy(realMsg.getSubtotalBy());
           

            // Here we are previously setting the start date and end date for the query.
            // The problem is for few records the endtime can be null.
            // To avoid this we need to set the StartTime and EndTime as starttime>= starttime and
            // starttime < endtime.
            //query.setStartDate(realMsg.getStartTime());
            //query.setEndDate(realMsg.getEndTime());
            if(realMsg.getStartTime() != null) {
                query.setStartTimeGE(realMsg.getStartTime());
            }
            if(realMsg.getEndTime() != null) {
                query.setStartTimeLT(realMsg.getEndTime());
            }
            
            query.setStart(realMsg.getstart());
            if(realMsg.getlimit() != null)
                query.setLimit(realMsg.getlimit());

            List<CommodityTransfer> results = null;
            List<Object[]> groupByResults = null;
            List<Object[]> sourceResults = null;
            List<Object[]> destnResults = null;
            List<PendingCommodityTransfer> pendingResults = null;

            // Conditional check to find if the table is a Commodity Transfer Table or the Pending Commodity Transfer Table

            if (CmFinoFIX.TransferState_Complete.equals(realMsg.getTransferState())) {
                //We are to Search on Commodity Transfer Table
                //Check to see if we need to do a Group By
                //state = enumService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Complete);
              state = CmFinoFIX.TransferStateValue_Complete;
                
                if (query.getSubTotalBy() == null) {
                    results = dao.get(query);
                } else {
                    //If you are here you are doing a group by operation
                    //Check if we are doing a group by on TransactionType
                    // Transaction Type is a special case we need to handle the SourceMDN and Destination MDN Case Seperately
                    //and the below code does the same
                    if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Buy_Sell) {
                        if (StringUtils.isEmpty(realMsg.getTransactionType())) {
                            query.setSourcePocket(sourcePocket);
                            query.setDestinationPocketID(null);
                            query.setSourceDestnPocket(null);
                            // list storing results where MDN is the source
                            sourceResults = dao.groupBy(query);
                            query.setSourcePocket(null);
                            query.setDestinationPocketID(pocketId);
                            query.setSourceDestnPocket(null);
                            // list storing results where MDN is the Destination
                            destnResults = dao.groupBy(query);
                        } else {
                            if (realMsg.getTransactionType().equals(CmFinoFIX.TransactionType_Buy)) {
                                query.setSourceDestnPocket(null);
                                query.setDestinationPocketID(pocketId);
                                query.setSourcePocket(null);
                                destnResults = dao.groupBy(query);
                            } else if (realMsg.getTransactionType().equals(CmFinoFIX.TransactionType_Sell)) {
                                query.setSourceDestnPocket(null);
                                query.setDestinationPocketID(null);
                                query.setSourcePocket(sourcePocket);
                                sourceResults = dao.groupBy(query);
                            }
                        }
                    } else {
                        groupByResults = dao.groupBy(query);
                    }
                }
            } else if (CmFinoFIX.TransferState_Pending.equals(realMsg.getTransferState())) {
                // This will return all the Transactions for Pending Commodity Transfer
                //This is specific to Transactions Page
                //state = enumService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Pending);
                state = CmFinoFIX.TransferStateValue_Pending;
                boolean setValue = true;
                query.setBankReversalRequired(setValue);
                query.setOperatorActionRequired(setValue);

                if (query.getSubTotalBy() == null) {
                    pendingResults = pendingDAO.get(query);
                } else {
                    //If you are here you are doing a group by operation
                    //Check if we are doing a group by on TransactionType
                    // Transaction Type is a special case we need to handle the SourceMDN and Destination MDN Case Seperately
                    //and the below code does the same
                    if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Buy_Sell) {
                        if (StringUtils.isEmpty(realMsg.getTransactionType())) {
                        	query.setSourcePocket(sourcePocket);
                            query.setDestinationPocketID(null);
                            query.setSourceDestnPocket(null);
                            // list storing results where MDN is the source
                            sourceResults = pendingDAO.groupBy(query);
                            query.setSourcePocket(null);
                            query.setDestinationPocketID(pocketId);
                            query.setSourceDestnPocket(null);
                            // list storing results where MDN is the Destination
                            destnResults = pendingDAO.groupBy(query);
                        } else {
                            if (realMsg.getTransactionType().equals(CmFinoFIX.TransactionType_Buy)) {
                                query.setSourceDestnPocket(null);
                                query.setDestinationPocketID(pocketId);
                                query.setSourcePocket(null);
                                destnResults = pendingDAO.groupBy(query);
                            } else if (realMsg.getTransactionType().equals(CmFinoFIX.TransactionType_Sell)) {
                                query.setSourceDestnPocket(null);
                                query.setDestinationPocketID(null);
                                query.setSourcePocket(sourcePocket);
                                sourceResults = pendingDAO.groupBy(query);
                            }
                        }
                    } else {
                        groupByResults = pendingDAO.groupBy(query);
                    }
                }
            }

            if (results != null) {
                realMsg.allocateEntries(results.size());
            } else if (pendingResults != null) {
                realMsg.allocateEntries(pendingResults.size());
            } else if (groupByResults != null) {
                realMsg.allocateEntries(groupByResults.size());
            } else if (sourceResults != null || destnResults != null) {
                int size = 0;
                if (sourceResults != null) {
                    size = sourceResults.size();
                }
                if (destnResults != null) {
                    size = size + destnResults.size();
                }
                realMsg.allocateEntries(size);
            }
            // Check to see if we are querying for GroupBY
            // If not we will be doing a normal processing for earch record returned
            if (query.getSubTotalBy() == null) {
                int size = 0;
                if (results != null) {
                    size = results.size();
                    for (int i = 0; i < size; i++) {
                        CommodityTransfer c = results.get(i);
                        CMJSCashFlow.CGEntries entry =
                                new CMJSCashFlow.CGEntries();
                        //i-1 to handel when i=0 and all generated refereces should be negative
                        updateMessage(c, null, entry, pocketId);
                        realMsg.getEntries()[i] = entry;
                    }
                } else if (pendingResults != null) {
                    size = pendingResults.size();
                    for (int i = 0; i < size; i++) {
                        PendingCommodityTransfer c = pendingResults.get(i);
                        CMJSCashFlow.CGEntries entry =
                                new CMJSCashFlow.CGEntries();
                        //i-1 to handel when i=0 and all generated refereces should be negative
                        updateMessage(c, c, entry, pocketId);
                        realMsg.getEntries()[i] = entry;
                    }
                }
            } else {
                //If we are are not doing a group by using Transactions_type we get into this
                if (query.getSubTotalBy() != CmFinoFIX.SubtotalBy_Buy_Sell) {
                    for (int i = 0; i < groupByResults.size(); i++) {
                        Object[] c = groupByResults.get(i);
                        CMJSCashFlow.CGEntries entry =
                                new CMJSCashFlow.CGEntries();
                        //i-1 to handel when i=0 and all generated refereces should be negative
                        int t = -2 - i;
                        groupByUpdateMessage(c, entry, query.getSubTotalBy(), false, t, pocketId);
                        realMsg.getEntries()[i] = entry;
                    }
                } else {
                    //This is code specific to handle Group By Transactions_Type Messages
                    int sourceSize = 0;
                    int destnSize = 0;
                    if (sourceResults != null) {
                        sourceSize = sourceResults.size();
                    }
                    if (destnResults != null) {
                        destnSize = destnResults.size();
                    }
                    if (sourceSize > 0) {
                        for (int i = 0; i < sourceSize; i++) {
                            Object[] c = sourceResults.get(i);
                            CMJSCashFlow.CGEntries entry =
                                    new CMJSCashFlow.CGEntries();
                            //i-1 to handel when i=0 and all generated refereces should be negative
                            int t = -2 - i;
                            groupByUpdateMessage(c, entry, query.getSubTotalBy(), true, t, pocketId);
                            realMsg.getEntries()[i] = entry;
                        }
                    }
                    if (destnSize > 0) {
                        for (int i = sourceSize; i < (destnSize + sourceSize); i++) {
                            Object[] c = destnResults.get(i - sourceSize);
                            CMJSCashFlow.CGEntries entry =
                                    new CMJSCashFlow.CGEntries();
                            //i-1 to handel when i=0 and all generated refereces should be negative
                            int t = -2 - i;
                            groupByUpdateMessage(c, entry, query.getSubTotalBy(), false, t, pocketId);
                            realMsg.getEntries()[i] = entry;
                        }
                    }
                }
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        }
        return realMsg;
    }
}

