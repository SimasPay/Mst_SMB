package com.mfino.interbank.uangku;

import static com.mfino.billpayments.BillPayConstants.SOURCE_TO_DESTINATION;


import static com.mfino.billpayments.BillPayConstants.SOURCE_TO_SUSPENSE;
import static com.mfino.billpayments.BillPayConstants.SUSPENSE_TO_DESTINATION;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPayMoneyTransferService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.service.impl.BillPayMoneyTransferServiceImpl;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.MFSBillerQuery;
import com.mfino.dao.query.PartnerServicesQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.PocketService;
import com.mfino.service.impl.BillerServiceImpl;
/**
 * 
 * @author HemanthKumar
 *
 */
public class InterBankNewMoneyTransferServiceImpl extends BillPayMoneyTransferServiceImpl {
	
	public String getReversalResponseQueue(){
		return "jms:ibtSuspenseAndChargesRRQueue?disableReplyTo=true";
	}
	public String getSourceToSuspenseBankResponseQueue(){
		return "jms:ibtSourceToSuspenseBRQueue?disableReplyTo=true";
	}
	public String getSuspenseToDestBankResposeQueue(){
		return "jms:ibtSuspenseToDestBRQueue?disableReplyTo=true";
	}
	
	
}
