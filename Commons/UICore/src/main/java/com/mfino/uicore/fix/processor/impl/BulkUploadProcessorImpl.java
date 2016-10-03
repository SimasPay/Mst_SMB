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
import com.mfino.domain.SubscriberMdn;
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
                SubscriberMdn subMdn = (SubscriberMdn)sub.getSubscriberMdns().toArray()[0];
                query.setMdnID(subMdn.getId().longValue());
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
        entry.setID(bu.getId().longValue());

        if (bu.getDescription() != null) {
            entry.setDescription(bu.getDescription());
        }
        if (bu.getInfilename() != null) {
            entry.setFileName(bu.getInfilename());
        }
        if ((Long)bu.getDeliverystatus() != null) {
            entry.setBulkUploadDeliveryStatus(((Long)bu.getDeliverystatus()).intValue());
        }
        if ((Long) bu.getFiletype() != null) {
            entry.setBulkUploadFileType(((Long)bu.getFiletype()).intValue());
            entry.setBulkUploadFileTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadFileType, 
            		null, bu.getFiletype()));
        }
        if (bu.getDeliverydate() != null) {
            entry.setBulkUploadDeliveryDate(bu.getDeliverydate());
        }
        if (bu.getCreatedby() != null) {
            entry.setUploadedBy(bu.getCreatedby());
        }
        if (bu.getMdnid() != null) {
            entry.setMDNID(bu.getMdnid().longValue());
        }
        if (bu.getTotalamount() != null) {
            entry.setTotalAmount(bu.getTotalamount());
        }
        if (bu.getSuccessamount() != null) {
            entry.setSuccessAmount(bu.getSuccessamount());
        }
        if (bu.getFailedtransactionscount() != null) {
            entry.setFailedTransactionsCount(bu.getFailedtransactionscount().intValue());
        }
        if (bu.getUsername() != null) {
            entry.setUserName(bu.getUsername());
        }
        if (bu.getMdn() != null) {
            entry.setMDN(bu.getMdn());
        }
        if ((Long)bu.getTransactionscount() != null) {
            entry.setTransactionsCount(((Long)bu.getTransactionscount()).intValue());
        }
        if (bu.getVerificationchecksum() != null) {
            entry.setVerificationChecksum(bu.getVerificationchecksum().longValue());
        }
        if (bu.getCreatetime() != null) {
            entry.setCreateTime(bu.getCreatetime());
        }
        if (bu.getCreatedby() != null) {
        	entry.setCreatedBy(bu.getCreatedby());
        }
        if (bu.getLastupdatetime() != null) {
        	entry.setLastUpdateTime(bu.getLastupdatetime());
        }
        if (bu.getUpdatedby() != null) {
        	entry.setUpdatedBy(bu.getUpdatedby());
        }
        if (bu.getName() != null) {
        	entry.setName(bu.getName());
        }
        if (((Long)bu.getFiletype()).equals(CmFinoFIX.BulkUploadFileType_BankAccountTransfer)) {
            CommodityTransferDAO dao = DAOFactory.getInstance().getCommodityTransferDAO();
            PendingCommodityTransferDAO pdao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
            CommodityTransferQuery query = new CommodityTransferQuery();
            query.setBulkuploadID(bu.getId().longValue());
            query.setBulkUploadLineNumber(1);
            try {
                List<CommodityTransfer> results = dao.get(query);
                if (results.size() > 0) {
                    entry.setCurrency(results.get(0).getCurrency());
                    entry.setProcessTime(results.get(0).getStarttime());
                } else {
                    List<PendingCommodityTransfer> presults = pdao.get(query);
                    if (presults.size() > 0) {
                        entry.setCurrency(presults.get(0).getCurrency());
                        entry.setProcessTime(presults.get(0).getStarttime());
                    }
                }
            } catch (Exception exp) {
               log.error("Updating message", exp);
            }


        }
        entry.setBulkUploadDeliveryStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadDeliveryStatus, null, entry.getBulkUploadDeliveryStatus()));
        if (((Long)bu.getDeliverystatus()).equals(CmFinoFIX.BulkUploadDeliveryStatus_Processed) || ((Long)bu.getDeliverystatus()).equals(CmFinoFIX.BulkUploadDeliveryStatus_Complete)) {
            if (bu.getFailedtransactionscount() != null && ((Long)bu.getTransactionscount()) != null) {
            	Long trxSuccessCount = bu.getTransactionscount() - bu.getFailedtransactionscount();
                entry.setSuccessfulTransactionsCount(trxSuccessCount.intValue());
            }
        } else {
            entry.setSuccessfulTransactionsCount(null);
        }
        if (bu.getPaymentdate() != null) {
        	entry.setPaymentDate(bu.getPaymentdate());
        }
        if (bu.getPocket() != null) {
        	entry.setSourcePocket(bu.getPocket().getId().longValue());
			if (StringUtils.isNotBlank(bu.getPocket().getCardpan()) && bu.getPocket().getPocketTemplate() != null) {
	        	String cPan = bu.getPocket().getCardpan();
	        	if (cPan.length() > 6) {
	        		cPan = cPan.substring(cPan.length()-6);
	        	}
	        	entry.setSourcePocketDispText(bu.getPocket().getPocketTemplate().getDescription() + " - " + cPan);
			} else if (bu.getPocket().getPocketTemplate() != null) {
				entry.setSourcePocketDispText(bu.getPocket().getPocketTemplate().getDescription());
			}
        }
        if (bu.getServicechargetransactionlogid() != null) {
        	entry.setServiceChargeTransactionLogID(bu.getServicechargetransactionlogid().longValue());
        }
        if (bu.getReversesctlid() != null) {
        	entry.setReverseSCTLID(bu.getReversesctlid().longValue());
        }
        if (StringUtils.isNotBlank(bu.getFailurereason())) {
        	entry.setFailureReason(bu.getFailurereason());
        }
    }
}
