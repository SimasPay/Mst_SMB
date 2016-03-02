/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;

import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CmFinoFIX;


public interface PocketService {

	public void handleCardPanChange(Pocket pocket);

	public Pocket createDefaultPocket(PocketTemplate defaultPocketTemplate, SubscriberMDN subscriberMDN);

	public Pocket createActivePocket(PocketTemplate defaultPocketTemplate, SubscriberMDN subscriberMDN, boolean isDefault); 

	public boolean isDefaultAirTimeSVA(Pocket p);

	public void changeStatusBasedOnMerchantAndSubscriber(Pocket p); 

	public BigDecimal getPocketBalanceAsOf(Pocket pocket, Date end, List<CommodityTransfer> rafTxns);
	
	public BigDecimal getPocketBalanceAsOf2(Pocket pocket, Date end); 
	
	public BigDecimal getPocketBalanceAsOf3(Pocket pocket, Date asOfDate, List<CommodityTransfer> rafTxns);
	
	public Pocket createPocket(PocketTemplate pocketTemplate,
			SubscriberMDN subscriberMDN, Integer pocketstatus,boolean isDefault,String CardPan,String CardAlias);
	
	public List<CommodityTransfer> getResolvedAsFailedTxnsBetween(Date start, Date end, List<CommodityTransfer> allRAFList);
	
	public CmFinoFIX.CRCommodityTransfer getMostRecentTransferBefore(Pocket pocket, Date end);
	
	public CmFinoFIX.CRCommodityTransfer getFirstTransferAfter(Pocket pocket, Date end);
	
	public String generateSVAEMoney16DigitCardPAN(String mdn) throws InvalidMDNException, EmptyStringException;

	public Pocket createDefaultActivePocket(PocketTemplate pocketTemplate, SubscriberMDN subscriberMdn); 

	public Pocket createDefaultActivePocket(Long pocketTemplateID,
			SubscriberMDN subscriberMDN);
	
	public Pocket createDefaultBankPocket(Long pocketTemplateID,SubscriberMDN subscriberMDN,String cardPan);
	
	public Pocket createPocket(Long pocketTemplateID,
			SubscriberMDN subscriberMDN, Integer pocketstatus,boolean isDefault,String CardPan);
	
	public Pocket createPocket(PocketTemplate pocketTemplate,
			SubscriberMDN subscriberMDN, Integer pocketstatus,boolean isDefault,String CardPan);
	
	public PocketTemplate getPocketTemplateFromPocketTemplateConfig(Long kycLevel, Boolean isDefault, Integer pocketType, 
			Integer subscriberType, Integer businessPartnerType, Long groupID);
	
	public PocketTemplate getPocketTemplateFromPocketTemplateConfig(Long kycLevel, Boolean isDefault, Boolean isSuspensePocket, Boolean isCollectorPocket, 
			Integer pocketType, Integer subscriberType, Integer businessPartnerType, Long groupID);
	
	public PocketTemplate getBankPocketTemplateFromPocketTemplateConfig(Integer bankAccountType, boolean isDefault, Integer subscriberType, Integer businessPartnerType, Long groupID);

	public boolean isAllowed(PocketTemplate pocketTemplate,
			SubscriberMDN mdn); 
	
	public Pocket getDefaultPocket(Partner partner, long pocketTemplateId, boolean isSuspencePocket);
	
	public Pocket getDefaultPocket(Partner partner, long pocketTemplateId);
	
	public Pocket getDefaultPocket(SubscriberMDN sMDN, String pocketCode);
	
	public boolean checkCount(PocketTemplate pocketTemplate,
			SubscriberMDN subMdn);
	
	public List<Pocket> get(PocketQuery query) throws MfinoRuntimeException;
	
	public void save(Pocket pocket) throws MfinoRuntimeException;
	
	public Pocket getById(Long pocketId) throws MfinoRuntimeException;
	
	public Pocket getByCardPan(String cardPan) throws MfinoRuntimeException;
	
	public Pocket getByCardAlias(String cardAlias) throws MfinoRuntimeException;
	
	public Pocket getSuspencePocket(User user); 
	
	public Pocket getSuspencePocket(Partner partner);
	
	public Pocket getById(Long pocketId,LockMode lockmode) throws MfinoRuntimeException;
	
	public Pocket getNFCPocket(SubscriberMDN subscriberMDN, String cardPAN);

    public Pocket getPocketAferEvicting(Pocket pocket);
    
	public List<Pocket> getDefaultBankPocketByMdnList(List<Long> mdnlist); 
	
	public List<Long> getLakuPandaiPockets();
}