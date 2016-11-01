package com.mfino.mce.interbank.impl;


import static com.mfino.constants.SystemParameterKeys.INTERBANK_PARTNER_MDN_KEY;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.InterbankCodesDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SystemParametersDao;
import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankCodes;
import com.mfino.domain.InterbankTransfers;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.mce.interbank.InterBankService;
import com.mfino.service.SubscriberService;

/**
 * @author Sasi
 *
 */
public class InterBankServiceImpl implements InterBankService{
	

	private SubscriberService subscriberService;

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public InterbankTransfers createInterBankTransfer(CMInterBankFundsTransferInquiry ibtInquiry, InterbankCodes interBankCode){
		InterbankTransfers ibt = new InterbankTransfers();

		ibt.setTerminalid(ibtInquiry.getChannelCode());
		ibt.setDestbankcode(ibtInquiry.getDestBankCode());
		ibt.setDestbankname(interBankCode.getBankname());
		ibt.setSourceaccountname(ibtInquiry.getSourceBankAccountNo());
		ibt.setDestaccountname(ibtInquiry.getDestAccountNumber());
		ibt.setSourceaccountnumber(ibtInquiry.getSourceBankAccountNo());
		ibt.setDestaccountnumber(ibtInquiry.getDestAccountNumber());
		//ibt.setNarration(ibtInquiry);
		ibt.setAmount(ibtInquiry.getAmount());
		ibt.setCharges(ibtInquiry.getCharges());
		
		ibt.setIbtstatus(Long.valueOf(CmFinoFIX.IBTStatus_INQUIRY));
		ibt.setSctlid(ibtInquiry.getServiceChargeTransactionLogID());

		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		interBankTransferDao.save(ibt);
		
		return ibt;
	}
	
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterbankCodes getBankCode(String bankCode){
		
		InterbankCodes nbCode = null;
		InterbankCodesDao nbDao = DAOFactory.getInstance().getInterbankCodesDao();
		InterBankCodesQuery query = new InterBankCodesQuery();
		query.setBankCode(bankCode);
		List<InterbankCodes> nbCodeList = nbDao.get(query);
		if(nbCodeList.size() >0){
			nbCode = nbCodeList.get(0);
		}
		
		return nbCode;
	}

	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public boolean isIBTRestricted(String bankCode){
		InterbankCodes interBankCode = getBankCode(bankCode);
		boolean isIBTAllowed = ((interBankCode != null) && (interBankCode.getIballowed() != 0)) ? true : false;
		
		return isIBTAllowed;
	}
	
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterbankTransfers getIBT(Long sctlId){
		
		if(sctlId == null) return null;
		
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		InterBankTransfersQuery query = new InterBankTransfersQuery();
		query.setSctlId(sctlId);
		List<InterbankTransfers> ibtList = interBankTransferDao.get(query);
		
		if(ibtList!=null && !ibtList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return ibtList.get(0);
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public InterbankTransfers updateIBT(InterbankTransfers ibt){
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		interBankTransferDao.save(ibt);
		
		return ibt;
	}

	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public Pocket getIBDestinationPocket(){
		Pocket pocket = null;
		
		SystemParametersDao systemParameterDao = DAOFactory.getInstance().getSystemParameterDao();
		String interbankPartnerMdn = systemParameterDao.getSystemParameterByName(INTERBANK_PARTNER_MDN_KEY).getParametervalue();
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMdn = subscriberMdnDao.getByMDN(interbankPartnerMdn);
		
		pocket = subscriberService.getDefaultPocket(subscriberMdn.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		
		return pocket;
	}
}
