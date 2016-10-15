package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.BankAdmin;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.service.AuthorizationService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerCashOutInquiryProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChargeTransactionsViewProcessor;
import com.mfino.uicore.fix.processor.CommodityTransferProcessor;
import com.mfino.uicore.fix.processor.CommodityTransferUpdateMessage;
import com.mfino.uicore.fix.processor.TransactionsViewProcessor;
import com.mfino.util.DateUtil;
@Service("CommodityTransferProcessorImpl")
public class CommodityTransferProcessorImpl extends BaseFixProcessor implements CommodityTransferProcessor{

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@Autowired
	@Qualifier("BankTellerCashOutInquiryProcessorImpl")
    private BankTellerCashOutInquiryProcessor bankTellerCashOutInquiryProcessor;
    
    @Autowired
    @Qualifier("ChargeTransactionsViewProcessorImpl")
    private ChargeTransactionsViewProcessor chargeTransactionsViewProcessor;
    
    @Autowired
    @Qualifier("TransactionsViewProcessorImpl")
    private TransactionsViewProcessor transactionsViewProcessor;
    
    @Autowired
    @Qualifier("CommodityTransferUpdateMessageImpl")
    private CommodityTransferUpdateMessage commodityTransferUpdateMessage;

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
    	String state=null;
        CMJSCommodityTransfer realMsg = (CMJSCommodityTransfer) msg;
        
        CommodityTransferDAO dao = DAOFactory.getInstance().getCommodityTransferDAO();
        PendingCommodityTransferDAO pendingDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();

        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            CommodityTransferQuery query = new CommodityTransferQuery();
            //handle pockets transaction view
            if ((realMsg.getIsMiniStatementRequest()!=null &&realMsg.getIsMiniStatementRequest()) && 
            		realMsg.getSourceDestnPocketID() != null) {
            	CFIXMsg result =transactionsViewProcessor.process(realMsg);
            	if(result instanceof CMJSError){
            		//not a pocket transaction view so continue with normal processing
            	}else{
            		return result;
            	}
            }
            
            if(realMsg.getJSMsgType()!=null
            		&&CmFinoFIX.MsgType_JSBankTellerCashOutInquiry.equals(realMsg.getJSMsgType())){
            	return bankTellerCashOutInquiryProcessor.process(realMsg);
            }
            
            if(realMsg.getJSMsgType()!=null
            		&&CmFinoFIX.MsgType_JSChargeTransactions.equals(realMsg.getJSMsgType())){
            	return chargeTransactionsViewProcessor.process(realMsg);
            }
            
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            query.setId(realMsg.getIDSearch());
            query.setTransferStatus(realMsg.getTransferStatus());
            //This Specific code is to make the Subset TransactionsTransferStatus Independent of TransferStatus
            if (realMsg.getTransactionsTransferStatus() == CmFinoFIX.TransactionsTransferStatus_Completed) {
                query.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
            } else if (realMsg.getTransactionsTransferStatus() == CmFinoFIX.TransactionsTransferStatus_Failed) {
                query.setTransferStatus(CmFinoFIX.TransferStatus_Failed);
            }
            if(realMsg.getSourceDestMDNAndID() == null){
            	query.setSourceMDN(realMsg.getSourceMDN());
            }
            query.setDestinationMDN(realMsg.getDestMDN());
            query.setSourceDestnMDN(realMsg.getSourceDestnMDN());
            if (realMsg.getCreateTimeSearch() != null) {
                query.setCreateTimeSearchGT(realMsg.getCreateTimeSearch());
                Date createDatePlus1 = DateUtil.addDays(query.getCreateTimeSearchGT(), 1);
                query.setCreateTimeSearchLE(createDatePlus1);
            }
            if (realMsg.getSubscriberMDNID() != null) {
                SubscriberMDNDAO subscMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
                query.setSubscriberMDN(subscMDNDAO.getById(realMsg.getSubscriberMDNID()));
//                SubscriberMDNDAO subdao = new SubscriberMDNDAO();
//                SubscriberMdnQuery subquery = new SubscriberMdnQuery();
//                subquery.setId(realMsg.getSubscriberMDNID());
//                List<SubscriberMDN> subresults = subdao.get(subquery);
//                if (subresults.size() > 0) {
//                    SubscriberMDN submdn = subresults.get(0);
//                    String mdn = submdn.getMDN();
//                    query.setMDN(mdn);
//                }
            }
            if (realMsg.getSourceApplicationSearch() != null) {
                query.setSourceApplicationSearch(realMsg.getSourceApplicationSearch());
            }
            query.setSubTotalBy(realMsg.getSubtotalBy());

            // Here we are setting the StartTime and EndTime search to StartDate and EndDate in
            // the query. Due to this the records which doesn't have the END TIME in the 
            // records will not be returned especially when the date range is provided.
            // Changing the criteria to StartTime >= StartTime and StartTime < EndTime.
            // This will result in showing the correct data for the date range search also.
            //query.setStartDate(realMsg.getStartTime());
            //query.setEndDate(realMsg.getEndTime());
            if (realMsg.getStartTime() != null) {
                query.setStartTimeGE(realMsg.getStartTime());
            }
            if (realMsg.getEndTime() != null) {
                query.setStartTimeLT(realMsg.getEndTime());
            }

            query.setSourceReferenceID(realMsg.getSourceReferenceID());
            query.setDestinationRefID(realMsg.getOperatorAuthorizationCode());
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            query.setMsgType(realMsg.getTransactionUICategory());
            query.setBulkuploadID(realMsg.getBulkUploadIDSearch());
            if(realMsg.getSourceDestnPocketID()!=null){
            	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
            	query.setSourceDestnPocket(pocketDao.getById(realMsg.getSourceDestnPocketID()));
            }

            // Here set the E-Money Only Transactions or not
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Transaction_OnlyEMoney_View)) {
                // If we reach here then this role can only see the EMONEY transactions.
                query.setOnlyEmoneyTxns(true);
            }

            // Here set the Bank Only Transactions or not
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Transaction_OnlyBank_View)) {
                // If we reach here then this role can only see the Bank transactions.
                query.setOnlyBankTxns(true);

                MfinoUser user = userService.getCurrentUser();
                Set<BankAdmin> admins = user.getBankAdmins();

                if (admins != null && admins.size() > 0) {
                    BankAdmin admin = (BankAdmin) admins.toArray()[0];
                    if (admin != null && admin.getBank() != null) {
                        query.setBankRoutingCode(admin.getBank().getBankcode().intValue());
                         // Setting company is null for bank roles..
                        query.setCompany(null);
                    }
                }
            }

            if (realMsg.getSourceDestMDNAndID() != null) {
                String[] mdnAndID = realMsg.getSourceDestMDNAndID().split(",");
                if (mdnAndID.length == 2 && StringUtils.isNumeric(mdnAndID[1])) {
                    query.setSourceDestMDNAndID(new Object[]{mdnAndID[0], NumberUtils.toLong(mdnAndID[1])});
                }
            }
                  List<CommodityTransfer> results = null;
            List<PendingCommodityTransfer> pendingResults = null;
            // Conditional check to find if the table is a Commodity Transfer Table or the Pending Commodity Transfer Table
            if (CmFinoFIX.TransferState_Complete.equals(realMsg.getTransferState())) {
                //We are to Search on Commodity Transfer Table
                //state = enumService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Complete);
                state = CmFinoFIX.TransferStateValue_Complete;
                query.setSortString(CommodityTransfer.FieldName_EndTime+":desc,"+CommodityTransfer.FieldName_RecordID+":desc");
                results = dao.get(query);
                
            } else if (CmFinoFIX.TransferState_Pending.equals(realMsg.getTransferState())) {
                // This will return all the Transactions for Pending Commodity Transfer
                //This is specific to Transactions Page
                boolean setValue = true;
                //state = enumService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Pending);
                state = CmFinoFIX.TransferStateValue_Pending;
                query.setBankReversalRequired(setValue);
                query.setOperatorActionRequired(setValue);
                pendingResults = pendingDAO.get(query);
            }
            if (results != null) {
            	if(realMsg.getServiceID() != null){
            		filterCTBasedOnServiceType(results,realMsg.getServiceID());
            	}
                realMsg.allocateEntries(results.size());
            } else if (pendingResults != null) {
                realMsg.allocateEntries(pendingResults.size());
            }

            // Check to see if we are querying for GroupBY
            // If not we will be doing a normal processing for earch record returned
            if (query.getSubTotalBy() == null) {
                if (results != null || pendingResults != null) {

                    int size = 0;
                    int pendingSize = 0;
                    if (results != null) {
                        size = results.size();
                    }
                    if (pendingResults != null) {
                        pendingSize = pendingResults.size();
                    }
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            CommodityTransfer c = results.get(i);
                            CMJSCommodityTransfer.CGEntries entry =
                                    new CMJSCommodityTransfer.CGEntries();
                            entry.setTransferStateText(state);
                            //i-1 to handel when i=0 and all generated refereces should be negative
                            commodityTransferUpdateMessage.updateMessage(c, null, entry,realMsg);
                            realMsg.getEntries()[i] = entry;
                        }
                    }
                    if (pendingSize > 0) {

                        for (int i = size; i < (pendingSize + size); i++) {
                            PendingCommodityTransfer c = pendingResults.get(i);
                            CMJSCommodityTransfer.CGEntries entry =
                                    new CMJSCommodityTransfer.CGEntries();
                            entry.setTransferStateText(state);
                            commodityTransferUpdateMessage.updateMessage(new CommodityTransfer(), c, entry, realMsg);
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
	
	
	/**
	 * Filters list of CommodityTransfer objects based on the service type
	 * <p>
	 * @param results :list of CommodityTransfer objects
	 * @param serviceId :id of the service which is used for filtering
	 */
	private void filterCTBasedOnServiceType(List<CommodityTransfer> results,Long serviceId){
		ServiceChargeTransactionLogDAO sctlDAO= DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = null;
		ChargeTxnCommodityTransferMapDAO ctMapDao = DAOFactory.getInstance().getTxnTransferMap();
		Long sctlID = null;
		for(int i=0;i<results.size();i++){
			CommodityTransfer ct = results.get(i);
			sctlID = ctMapDao.getSCTLIdByCommodityTransferId(ct.getId().longValue());
			if(sctlID != null){
				sctl = sctlDAO.getById(sctlID);
			}
			if(sctl != null){
				if(!sctl.getServiceid().equals(serviceId)){
					results.remove(i);
					i--;
				}
			}
		}
	}

}
