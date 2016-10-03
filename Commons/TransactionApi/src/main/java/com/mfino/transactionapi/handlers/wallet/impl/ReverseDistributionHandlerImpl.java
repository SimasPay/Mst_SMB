package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.query.FundDistributionInfoQuery;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.FundStorageService;
import com.mfino.service.SCTLService;
import com.mfino.transactionapi.handlers.wallet.ReverseDistributionHandler;

@Service("ReverseDistributionHandlerImpl")
public class ReverseDistributionHandlerImpl implements ReverseDistributionHandler  {
	public static final Long INQUIRY_TIMEOUT = 120000L;
	private Logger log = LoggerFactory.getLogger(ReverseDistributionHandlerImpl.class);	
	private static final String TIMEOUT = "Time Out";
	
	@Autowired
	@Qualifier("FundStorageServiceImpl")
	private FundStorageService fundStorageService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void handle() {
		FundDistributionInfoQuery fundDistributionInfoQuery = new FundDistributionInfoQuery();
		fundDistributionInfoQuery.setDistributionStatus(CmFinoFIX.DistributionStatus_INITIALIZED);
		fundDistributionInfoQuery.setDistributionType(CmFinoFIX.DistributionType_Withdrawal);
		List<FundDistributionInfo> lstFundDistributionInfos = fundStorageService.getFundDistributionInfosByQuery(fundDistributionInfoQuery);
		
		if(CollectionUtils.isNotEmpty(lstFundDistributionInfos)){
			for(int iter=0;iter<lstFundDistributionInfos.size();iter++){
				FundDistributionInfo fundDistributionInfo = lstFundDistributionInfos.get(iter);
				
				boolean isTrxnTimeOut=checkTimeOut(fundDistributionInfo);
				if(isTrxnTimeOut){
						processReversal(fundDistributionInfo);

				}
			}
		}
	}


	private void processReversal(FundDistributionInfo fundDistributionInfo) {
			log.info("The Fund Distribution inquiry request has timed out.Reversing Fund Distribution with id:"+fundDistributionInfo.getId()+"Starting....");
			UnregisteredTxnInfo unRegisteredTxnInfo = fundDistributionInfo.getUnRegisteredTxnInfoByFundAllocationId();
			fundDistributionInfo.setFailurereason("Confirmation not received.failed by scheduler");
			fundDistributionInfo.setDistributionstatus((long)CmFinoFIX.DistributionStatus_TRANSFER_FAILED);
			BigDecimal availableAmount = unRegisteredTxnInfo.getAvailableamount().add(fundDistributionInfo.getDistributedamount());
			unRegisteredTxnInfo.setAvailableamount(availableAmount);
			Integer status = unRegisteredTxnInfo.getUnregisteredtxnstatus().intValue();
			if(CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN.equals(status) || 
					CmFinoFIX.UnRegisteredTxnStatus_FUND_COMPLETELY_WITHDRAWN.equals(status)){
				if(availableAmount.equals(unRegisteredTxnInfo.getAmount())){
					unRegisteredTxnInfo.setUnregisteredtxnstatus((long)CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE);
				}
				else{
					unRegisteredTxnInfo.setUnregisteredtxnstatus((long)CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN);
				}
			}else if(!CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE.equals(unRegisteredTxnInfo.getUnregisteredtxnstatus())){
				unRegisteredTxnInfo.setUnregisteredtxnstatus((long)CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED);
			}
			fundStorageService.allocateFunds(unRegisteredTxnInfo);
			fundStorageService.withdrawFunds(fundDistributionInfo);
			log.info("Reversal Completed successfully for Fund Distribution id: "+fundDistributionInfo.getId());
			
	}

	private boolean checkTimeOut(FundDistributionInfo fundDistributionInfo) {
		ServiceChargeTransactionLog sctl = sctlService.getBySCTLID(fundDistributionInfo.getTransfersctlid().longValue());
		if(CmFinoFIX.SCTLStatus_Failed.equals(sctl.getStatus()) && TIMEOUT.equals(sctl.getFailureReason())){
			return true;
		}
		return false;
	}
}
