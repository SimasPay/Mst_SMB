/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkUpload;
import com.mfino.service.EnumTextService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.BulkUploadProcessor;

/**
 *
 * @author Raju
 */
@Service("BulkUploadProcessorImpl")
public class BulkUploadProcessorImpl extends BaseFixProcessor implements BulkUploadProcessor {

	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        BulkUploadDAO dao = DAOFactory.getInstance().getBulkUploadDAO();
        CMJSBulkUpload realMsg = (CMJSBulkUpload) msg;

        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            BulkUploadQuery query = new BulkUploadQuery();
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            User loggedInUser = userService.getCurrentUser();
//            if (CmFinoFIX.Role_Corporate_User.equals(loggedInUser.getRole())) {
//            	query.setUserId(loggedInUser.getID());
//            }
            
            query.setAssociationOrdered(true);
            if (realMsg.getStartDateSearch() != null) {
                query.setStartDate(realMsg.getStartDateSearch());
            }
            if (realMsg.getEndDateSearch() != null) {
                query.setEndDate(realMsg.getEndDateSearch());
            }
            if (realMsg.getFileTypeSearch() != null) {
                query.setFileType(realMsg.getFileTypeSearch());
            }
            if (realMsg.getFileStatusSearch() != null) {
                query.setFileStatus(realMsg.getFileStatusSearch());
            }
            if(realMsg.getMerchantIDSearch()!=null)
            {
                SubscriberDAO subsDao = DAOFactory.getInstance().getSubscriberDAO();
                Subscriber sub = subsDao.getById(realMsg.getMerchantIDSearch());
                SubscriberMDN subMdn = (SubscriberMDN)sub.getSubscriberMDNFromSubscriberID().toArray()[0];
                query.setMdnID(subMdn.getID());
            }
            if (realMsg.getPaymentDateSearch() != null) {
            	query.setPaymentDate(realMsg.getPaymentDateSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getNameSearch())) {
            	query.setNameSearch(realMsg.getNameSearch());
            }
            if (realMsg.getIDSearch() != null) {
            	query.setId(realMsg.getIDSearch());
            }
            
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<BulkUpload> results = dao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                BulkUpload s = results.get(i);
                CMJSBulkUpload.CGEntries entry =
                        new CMJSBulkUpload.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        }
        return realMsg;
    }

    public void updateMessage(BulkUpload bu, CMJSBulkUpload.CGEntries entry) {
        entry.setID(bu.getID());

        if (bu.getDescription() != null) {
            entry.setDescription(bu.getDescription());
        }
        if (bu.getInFileName() != null) {
            entry.setFileName(bu.getInFileName());
        }
        if (bu.getDeliveryStatus() != null) {
            entry.setBulkUploadDeliveryStatus(bu.getDeliveryStatus());
        }
        if (bu.getFileType() != null) {
            entry.setBulkUploadFileType(bu.getFileType());
            entry.setBulkUploadFileTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadFileType, null, bu.getFileType()));
        }
        if (bu.getDeliveryDate() != null) {
            entry.setBulkUploadDeliveryDate(bu.getDeliveryDate());
        }
        if (bu.getCreatedBy() != null) {
            entry.setUploadedBy(bu.getCreatedBy());
        }
        if (bu.getMDNID() != null) {
            entry.setMDNID(bu.getMDNID());
        }
        if (bu.getTotalAmount() != null) {
            entry.setTotalAmount(bu.getTotalAmount());
        }
        if (bu.getSuccessAmount() != null) {
            entry.setSuccessAmount(bu.getSuccessAmount());
        }
        if (bu.getFailedTransactionsCount() != null) {
            entry.setFailedTransactionsCount(bu.getFailedTransactionsCount());
        }
        if (bu.getUserName() != null) {
            entry.setUserName(bu.getUserName());
        }
        if (bu.getMDN() != null) {
            entry.setMDN(bu.getMDN());
        }
        if (bu.getTransactionsCount() != null) {
            entry.setTransactionsCount(bu.getTransactionsCount());
        }
        if (bu.getVerificationChecksum() != null) {
            entry.setVerificationChecksum(bu.getVerificationChecksum());
        }
        if (bu.getCreateTime() != null) {
            entry.setCreateTime(bu.getCreateTime());
        }
        if (bu.getCreatedBy() != null) {
        	entry.setCreatedBy(bu.getCreatedBy());
        }
        if (bu.getLastUpdateTime() != null) {
        	entry.setLastUpdateTime(bu.getLastUpdateTime());
        }
        if (bu.getUpdatedBy() != null) {
        	entry.setUpdatedBy(bu.getUpdatedBy());
        }
        if (bu.getName() != null) {
        	entry.setName(bu.getName());
        }
        if (bu.getFileType().equals(CmFinoFIX.BulkUploadFileType_BankAccountTransfer)) {
            CommodityTransferDAO dao = DAOFactory.getInstance().getCommodityTransferDAO();
            PendingCommodityTransferDAO pdao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
            CommodityTransferQuery query = new CommodityTransferQuery();
            query.setBulkuploadID(bu.getID());
            query.setBulkUploadLineNumber(1);
            try {
                List<CommodityTransfer> results = dao.get(query);
                if (results.size() > 0) {
                    entry.setCurrency(results.get(0).getCurrency());
                    entry.setProcessTime(results.get(0).getStartTime());
                } else {
                    List<PendingCommodityTransfer> presults = pdao.get(query);
                    if (presults.size() > 0) {
                        entry.setCurrency(presults.get(0).getCurrency());
                        entry.setProcessTime(presults.get(0).getStartTime());
                    }
                }
            } catch (Exception exp) {
               log.error("Updating message", exp);
            }


        }
        entry.setBulkUploadDeliveryStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadDeliveryStatus, null, entry.getBulkUploadDeliveryStatus()));
        if (bu.getDeliveryStatus().equals(CmFinoFIX.BulkUploadDeliveryStatus_Processed) || bu.getDeliveryStatus().equals(CmFinoFIX.BulkUploadDeliveryStatus_Complete)) {
            if (bu.getFailedTransactionsCount() != null && bu.getTransactionsCount() != null) {
                entry.setSuccessfulTransactionsCount(bu.getTransactionsCount() - bu.getFailedTransactionsCount());
            }
        } else {
            entry.setSuccessfulTransactionsCount(null);
        }
        if (bu.getPaymentDate() != null) {
        	entry.setPaymentDate(bu.getPaymentDate());
        }
        if (bu.getPocketBySourcePocket() != null) {
        	entry.setSourcePocket(bu.getPocketBySourcePocket().getID());
			if (StringUtils.isNotBlank(bu.getPocketBySourcePocket().getCardPAN()) && bu.getPocketBySourcePocket().getPocketTemplate() != null) {
	        	String cPan = bu.getPocketBySourcePocket().getCardPAN();
	        	if (cPan.length() > 6) {
	        		cPan = cPan.substring(cPan.length()-6);
	        	}
	        	entry.setSourcePocketDispText(bu.getPocketBySourcePocket().getPocketTemplate().getDescription() + " - " + cPan);
			} else if (bu.getPocketBySourcePocket().getPocketTemplate() != null) {
				entry.setSourcePocketDispText(bu.getPocketBySourcePocket().getPocketTemplate().getDescription());
			}
        }
        if (bu.getServiceChargeTransactionLogID() != null) {
        	entry.setServiceChargeTransactionLogID(bu.getServiceChargeTransactionLogID());
        }
        if (bu.getReverseSCTLID() != null) {
        	entry.setReverseSCTLID(bu.getReverseSCTLID());
        }
        if (StringUtils.isNotBlank(bu.getFailureReason())) {
        	entry.setFailureReason(bu.getFailureReason());
        }
    }
}
