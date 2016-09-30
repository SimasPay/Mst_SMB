package com.mfino.mce.backend.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.mce.backend.AutoReversalFailuresMonitoringService;
import com.mfino.mce.backend.AutoReversalService;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public class AutoReversalFailuresMonitoringServiceImpl extends BaseServiceImpl implements AutoReversalFailuresMonitoringService {
	
	private Map<String,String> integrationCodeToQueueMap;
	
	private AutoReversalService autoReversalService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<MCEMessage> handleAutoReversalFailures(){
		log.info("AutoReversalFailuresMonitoringServiceImpl :: handleAutoReversalFailures() BEGIN");
		
		Collection<Integer> failedAutoRevStatuses = new HashSet<Integer>();
		failedAutoRevStatuses.add(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_FAILED);
		failedAutoRevStatuses.add(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_FAILED);
		failedAutoRevStatuses.add(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED);
		failedAutoRevStatuses.add(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED);
		failedAutoRevStatuses.add(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_FAILED);
		failedAutoRevStatuses.add(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED);
		
		Collection<AutoReversals> autoReversals = autoReversalService.getAutoReversalsWithStatus(failedAutoRevStatuses);
		
		//This messages will go to autoReversalFailureRoutingQueue via a splitter, which routes them based on the destination queue.
		//if charges -> transit failed, we need to do the entire reversal again. (autoReversalQueue)
		//if dest -> transit failed, let this message go to destination to transit inquiry queue and continue from their.
		//if transit -> source fails, let this message go to transit to source inquiry queue and continue from their.
		
		List<MCEMessage> mceMessages = new ArrayList<MCEMessage>();
		
		for(AutoReversals autoReversal : autoReversals){
			Long sctlId = autoReversal.getSctlid().longValue();
			ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
			
			MCEMessage mceMessage = new MCEMessage();
			//If integration queue is not set in the config file, message will not go back to approriate service, instead, notification will be sent out to 
			//customer from auto reversal module itself.
			
			String integrationQueue = integrationCodeToQueueMap.get(sctl.getIntegrationcode());
			
			if((null != integrationQueue) && !("".equals(integrationQueue))){
				mceMessage.setDestinationQueue(integrationQueue);
			}
			
			CMAutoReversal cmAutoReversal = new CMAutoReversal();
			cmAutoReversal.setServiceChargeTransactionLogID(sctlId);
			
			mceMessage.setRequest(cmAutoReversal);
			
			if((CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_FAILED.intValue() == (int)autoReversal.getAutorevstatus()) ||
					(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_FAILED.intValue() == (int)autoReversal.getAutorevstatus())){
				mceMessage.setDestinationQueue("jms:autoReversalQueue?disableReplyTo=true");
			}
			else if((CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED.intValue() == (int)autoReversal.getAutorevstatus()) ||
					(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED.intValue() == (int)autoReversal.getAutorevstatus())){
				mceMessage.setDestinationQueue("jms:destinationToTransitInquiry?disableReplyTo=true");
			}
			else if((CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_FAILED.intValue() == (int)autoReversal.getAutorevstatus()) ||
					(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED.intValue() == (int)autoReversal.getAutorevstatus())){
				mceMessage.setDestinationQueue("jms:transitToSourceInquiry?disableReplyTo=true");
			}			
			
			mceMessages.add(mceMessage);
		}
		
		log.info("AutoReversalFailuresMonitoringServiceImpl :: handleAutoReversalFailures() END");
		return mceMessages;
	}
	
	public AutoReversalService getAutoReversalService() {
		return autoReversalService;
	}
	
	public void setAutoReversalService(AutoReversalService autoReversalService) {
		this.autoReversalService = autoReversalService;
	}

	public Map<String, String> getIntegrationCodeToQueueMap() {
		return integrationCodeToQueueMap;
	}

	public void setIntegrationCodeToQueueMap(
			Map<String, String> integrationCodeToQueueMap) {
		this.integrationCodeToQueueMap = integrationCodeToQueueMap;
	}
}
