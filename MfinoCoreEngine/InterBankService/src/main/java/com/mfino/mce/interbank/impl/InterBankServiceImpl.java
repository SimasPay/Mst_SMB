package com.mfino.mce.interbank.impl;


import static com.mfino.constants.SystemParameterKeys.INTERBANK_PARTNER_MDN_KEY;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.InterbankCodesDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SystemParametersDao;
import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterBankCode;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.mce.interbank.InterBankService;
import com.mfino.service.SubscriberService;
import com.mfino.service.impl.SubscriberServiceImpl;

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
	public InterbankTransfer createInterBankTransfer(CMInterBankFundsTransferInquiry ibtInquiry, Pocket sourcePocket){
		InterbankTransfer ibt = new InterbankTransfer();

		ibt.setTerminalID(ibtInquiry.getChannelCode());
		ibt.setDestBankCode(ibtInquiry.getDestBankCode());
		ibt.setSourceAccountName(sourcePocket.getCardPAN());
		ibt.setDestAccountName(ibtInquiry.getDestAccountNumber());
		ibt.setSourceAccountNumber(sourcePocket.getCardPAN());
		ibt.setDestAccountNumber(ibtInquiry.getDestAccountNumber());
		//ibt.setNarration(ibtInquiry);
		ibt.setAmount(ibtInquiry.getAmount());
		ibt.setCharges(ibtInquiry.getCharges());
		
		ibt.setIBTStatus(CmFinoFIX.IBTStatus_INQUIRY);
		ibt.setSctlId(ibtInquiry.getServiceChargeTransactionLogID());

		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		interBankTransferDao.save(ibt);
		
		return ibt;
	}
	
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterBankCode getBankCode(String bankCode){
		
		InterBankCode nbCode = null;
		InterbankCodesDao nbDao = DAOFactory.getInstance().getInterbankCodesDao();
		InterBankCodesQuery query = new InterBankCodesQuery();
		query.setBankCode(bankCode);
		List<InterBankCode> nbCodeList = nbDao.get(query);
		if(nbCodeList.size() >0){
			nbCode = nbCodeList.get(0);
		}
		
		return nbCode;
	}

	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public boolean isIBTRestricted(String bankCode){
		InterBankCode interBankCode = getBankCode(bankCode);
		boolean isIBTAllowed = ((interBankCode != null) && (interBankCode.getibAllowed())) ? true : false;
		
		return isIBTAllowed;
	}
	
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterbankTransfer getIBT(Long sctlId){
		
		if(sctlId == null) return null;
		
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		InterBankTransfersQuery query = new InterBankTransfersQuery();
		query.setSctlId(sctlId);
		List<InterbankTransfer> ibtList = interBankTransferDao.get(query);
		
		if(ibtList!=null && !ibtList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return ibtList.get(0);
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public InterbankTransfer updateIBT(InterbankTransfer ibt){
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		interBankTransferDao.save(ibt);
		
		return ibt;
	}

	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public Pocket getIBDestinationPocket(){
		Pocket pocket = null;
		
		SystemParametersDao systemParameterDao = DAOFactory.getInstance().getSystemParameterDao();
		String interbankPartnerMdn = systemParameterDao.getSystemParameterByName(INTERBANK_PARTNER_MDN_KEY).getParameterValue();
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMdn = subscriberMdnDao.getByMDN(interbankPartnerMdn);
		
		pocket = subscriberService.getDefaultPocket(subscriberMdn.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		
		return pocket;
	}
}
