package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCancelBulkTranfer;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CancelBulkTransferProcessor;

/**
 * 
 * @author Bala Sunku
 */
@Service("CancelBulkTransferProcessorImpl")
public class CancelBulkTransferProcessorImpl extends BaseFixProcessor implements CancelBulkTransferProcessor{


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

  @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
  public CFIXMsg process(CFIXMsg msg) {

	CMJSCancelBulkTranfer realMsg = (CMJSCancelBulkTranfer) msg;
    CMJSError err = new CMJSError();
    BulkUploadDAO buDAO = DAOFactory.getInstance().getBulkUploadDAO();
    User loggedInUser = userService.getCurrentUser();
    
    if (realMsg.getBulkUploadID() != null) {
    	log.info("Cancelling the Bulk transfer request " + realMsg.getBulkUploadID() + " by user " + loggedInUser.getUsername());
    	BulkUpload bulkUpload = buDAO.getById(realMsg.getBulkUploadID());
    	
    	if (bulkUpload != null && bulkUpload.getMfinoUser().getId().equals(loggedInUser.getId())) {
    		if ( (CmFinoFIX.BulkUploadDeliveryStatus_Uploaded.equals(bulkUpload.getDeliverystatus())) || 
    				(CmFinoFIX.BulkUploadDeliveryStatus_Approved.equals(bulkUpload.getDeliverystatus())) ) {
    			
    			bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Terminated);
    			bulkUpload.setDeliverydate(new Timestamp());
    			buDAO.save(bulkUpload);
    	        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
    	        err.setErrorDescription("Bulk transfer request " + realMsg.getBulkUploadID() + " is cancelled");
    	        log.info("Bulk transfer request " + realMsg.getBulkUploadID() + " cancelled");
    		}
        	else {
      	      err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
      	      err.setErrorDescription(MessageText._("Bulk Transfer Request can't be cancelled as it is already scheduled for payment"));
      	      log.info("Failed to cancel the Bulk transfer request " + realMsg.getBulkUploadID() + " as it is already scheduled");
        	}
    	}
    	else {
    	      err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
    	      err.setErrorDescription(MessageText._("You are not authorized to Cancel the Bulk transfer request"));
    	      log.info("Failed to cancel the Bulk transfer request " + realMsg.getBulkUploadID() + " as authorization issue");
    	}
    }
    return err;
  }
}
