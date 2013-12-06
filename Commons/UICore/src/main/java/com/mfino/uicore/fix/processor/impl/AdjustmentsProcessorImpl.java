package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AdjustmentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.AdjustmentsQuery;
import com.mfino.domain.Adjustments;
import com.mfino.domain.Pocket;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAdjustments;
import com.mfino.fix.CmFinoFIX.CMTransactionAdjustments;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.AdjustmentsProcessor;

/**
 * @author Srikanth
 *
 */
@Service("AdjustmentsProcessorImpl")
public class AdjustmentsProcessorImpl extends FIXMessageHandler implements AdjustmentsProcessor {
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	private ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	private static Logger log = LoggerFactory.getLogger(AdjustmentsProcessorImpl.class);
	
	private void updateMessage(Adjustments adj, CMJSAdjustments.CGEntries e) {
		e.setID(adj.getID());
		if(adj.getServiceChargeTransactionLogBySctlId()!=  null) {
			e.setSctlId(adj.getServiceChargeTransactionLogBySctlId().getID());
		}
		if(adj.getPocketBySourcePocketID() != null) {
			Pocket pocket = adj.getPocketBySourcePocketID();
			e.setSourcePocketID(pocket.getID());
			e.setSourcePocketTemplateDescription(pocket.getPocketTemplate().getDescription() + "(ID:" + pocket.getID() + ")");
		}
		if(adj.getPocketByDestPocketID() != null) {
			Pocket pocket = adj.getPocketByDestPocketID();
			e.setDestPocketID(pocket.getID());
			e.setDestPocketTemplateDescription(pocket.getPocketTemplate().getDescription() + "(ID:" + pocket.getID() + ")");
		}
		if(adj.getAmount() != null) {
			e.setAmount(adj.getAmount());
		}
		if(adj.getAdjustmentType() != null) {
			e.setAdjustmentType(adj.getAdjustmentType());
		}
		if(adj.getDescription() != null) {
			e.setDescription(adj.getDescription());
		}
		if(adj.getAdjustmentStatus() != null) {
			e.setAdjustmentStatus(adj.getAdjustmentStatus());
			e.setAdjustmentStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_AdjustmentStatus, CmFinoFIX.Language_English, adj.getAdjustmentStatus()));
		}
		if(adj.getAppliedBy() != null) {
			e.setAppliedBy(adj.getAppliedBy());
		}
		if(adj.getAppliedTime() != null) {
			e.setAppliedTime(adj.getAppliedTime());
		}
		if(adj.getApprovedOrRejectedBy() != null) {
			e.setApprovedOrRejectedBy(adj.getApprovedOrRejectedBy());
		}
		if(adj.getApproveOrRejectComment() != null) {
			e.setApproveOrRejectComment(adj.getApproveOrRejectComment());
		}
		if(adj.getApproveOrRejectTime() != null) {
			e.setApproveOrRejectTime(adj.getApproveOrRejectTime());
		}
		e.setRecordVersion(adj.getVersion());
		e.setCreatedBy(adj.getCreatedBy());
		e.setCreateTime(adj.getCreateTime());
		e.setUpdatedBy(adj.getUpdatedBy());
		e.setLastUpdateTime(adj.getLastUpdateTime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
		CMJSAdjustments realMsg = (CMJSAdjustments) msg;
		AdjustmentsDAO dao = DAOFactory.getInstance().getAdjustmentsDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			AdjustmentsQuery query = new AdjustmentsQuery();
			int i=0;
			
			if (realMsg.getSctlId() != null) {
				query.setSctlID(realMsg.getSctlId());
			}
			if (realMsg.getStartDateSearch() != null) {
				query.setStartDate(realMsg.getStartDateSearch());
			}
			if (realMsg.getEndDateSearch() != null) {
				query.setEndDate(realMsg.getEndDateSearch());
			}
			if (realMsg.getSctlId() != null) {
				query.setSctlID(realMsg.getSctlId());
			}
			if (realMsg.getAdjustmentStatus() != null) {
				query.setAdjustmentStatus(realMsg.getAdjustmentStatus());
			}
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}			
			List<Adjustments> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (Adjustments adj: lst){
					CMJSAdjustments.CGEntries e = new CMJSAdjustments.CGEntries();
					updateMessage(adj, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			// Insert implementation is done in different way compared to other processors
			// Instead of saving using form store in UI, an ajax request [like  mFino.util.fix.send(msg, params)] is used to save values
			// hence the regular logic cannot be used 
			Adjustments adj = new Adjustments();
			if(realMsg.getSctlId() != null) { 
				adj.setServiceChargeTransactionLogBySctlId(sctlDao.getById(realMsg.getSctlId()));
			}
			if(realMsg.getSourcePocketID() != null) {
				adj.setPocketBySourcePocketID(pocketDao.getById(realMsg.getSourcePocketID()));
			}
			if(realMsg.getDestPocketID() != null) {
				adj.setPocketByDestPocketID(pocketDao.getById(realMsg.getDestPocketID()));
			}
			if(realMsg.getAmount() != null) {
				adj.setAmount(realMsg.getAmount());
			}
			if(realMsg.getAdjustmentType() != null) {
				adj.setAdjustmentType(realMsg.getAdjustmentType());
			}
			if(realMsg.getDescription() != null) {
				adj.setDescription(realMsg.getDescription());
			}
			adj.setAdjustmentStatus(CmFinoFIX.AdjustmentStatus_Requested);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        String userName = (auth != null) ? auth.getName() : " ";
			adj.setAppliedBy(userName);
			adj.setAppliedTime(new Timestamp());
			try {
				dao.save(adj);
			} catch (ConstraintViolationException ce) {
				return generateError(ce);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {	
			// Update implementation is done in different way compared to other processors
			// Instead of updating using form store in UI, an ajax request [like  mFino.util.fix.send(msg, params)] is used to save values
			// hence the regular logic cannot be used
			CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
			if(realMsg.getID() != null) {
				Adjustments adj = dao.getById(realMsg.getID());
				if(realMsg.getAdjustmentStatus() != null) {
					adj.setAdjustmentStatus(realMsg.getAdjustmentStatus());
				}
				if(realMsg.getApproveOrRejectComment() != null) {
					adj.setApproveOrRejectComment(realMsg.getApproveOrRejectComment());
				}
				adj.setApproveOrRejectTime(new Timestamp());
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        String userName = (auth != null) ? auth.getName() : " ";
				adj.setApprovedOrRejectedBy(userName);
				if(CmFinoFIX.AdjustmentStatus_Approved.equals(realMsg.getAdjustmentStatus())){
					adj.setAdjustmentStatus(CmFinoFIX.AdjustmentStatus_Processing);
					CMTransactionAdjustments txnAdjustments = new CMTransactionAdjustments();
					txnAdjustments.setSourcePocketID(adj.getPocketBySourcePocketID().getID());
					txnAdjustments.setDestPocketID(adj.getPocketByDestPocketID().getID());
					txnAdjustments.setSctlId(adj.getServiceChargeTransactionLogBySctlId().getID());
					txnAdjustments.setAdjustmentType(adj.getAdjustmentType());
					txnAdjustments.setAmount(adj.getAmount());
					CFIXMsg response = super.process(txnAdjustments);
	
					TransactionResponse transactionResponse = checkBackEndResponse(response);
					
					if(StringUtils.isNotBlank(transactionResponse.getMessage())){
						errorMsg.setErrorDescription(transactionResponse.getMessage());
					}
					else{
						log.error("Could not obtain a notification message");
						errorMsg.setErrorDescription("Sorry your transaction has failed.");
					}
					if(!transactionResponse.isResult()){
						//failure
						log.info("Got the transaction response as failed");
						adj.setAdjustmentStatus(CmFinoFIX.AdjustmentStatus_Failed);
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					}else{
						log.info("Got the transaction response as success");
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
						adj.setAdjustmentStatus(CmFinoFIX.AdjustmentStatus_Completed);
					}
						
				}
				else{
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					errorMsg.setErrorDescription("Adjustment request for transaction is rejected");
				}
				try {
					dao.save(adj);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
			}
			return errorMsg;
		} 
		
		return realMsg;
	}
	
	private CFIXMsg generateError(ConstraintViolationException cvError) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		String message = MessageText._("Error occured while creating the adjustment entry");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);		
		log.warn(message, cvError);
		return errorMsg;
	}
}
