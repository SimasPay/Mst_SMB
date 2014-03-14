package com.mfino.billpayments.service.impl;

import static com.mfino.billpayments.BillPayConstants.SOURCE_TO_DESTINATION;
import static com.mfino.billpayments.BillPayConstants.SOURCE_TO_SUSPENSE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public class BillPaymentsServiceImpl extends BillPaymentsBaseServiceImpl implements BillPaymentsService{

	public Log log = LogFactory.getLog(this.getClass());
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BillPayments createBillPayments(CMBillPayInquiry billPayInquiry) {
		
		log.info("BillPaymentsServiceImpl createBillPayments billPayInquiry="+billPayInquiry);
		
		BillPayments billPayments = new BillPayments();
		billPayments.setAmount(billPayInquiry.getAmount());
		billPayments.setBillerCode(billPayInquiry.getBillerCode());
		billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_INITIALIZED);
		billPayments.setCharges(billPayInquiry.getCharges());
		billPayments.setIntegrationCode(billPayInquiry.getIntegrationCode());
		billPayments.setChargesIncluded(billPayInquiry.getChargesIncluded());
		billPayments.setInvoiceNumber(billPayInquiry.getInvoiceNumber());
		billPayments.setOriginalINTxnId(billPayInquiry.getTransactionID().toString());
		billPayments.setINTxnId(billPayInquiry.getTransactionID().toString());
		billPayments.setPartnerBillerCode(billPayInquiry.getPartnerBillerCode());
		billPayments.setPartnerBillerCode(billPayInquiry.getPartnerBillerCode());
		billPayments.setSctlId(billPayInquiry.getServiceChargeTransactionLogID());
		billPayments.setSourceMDN(billPayInquiry.getSourceMDN());
		
		if(StringUtils.isNotBlank(billPayInquiry.getPaymentInquiryDetails())) {
			billPayments.setInfo1(billPayInquiry.getPaymentInquiryDetails());
		}
		else if(StringUtils.isNotBlank(billPayInquiry.getBenOpCode())) {
			billPayments.setInfo1(billPayInquiry.getBenOpCode());
		}
		else if (StringUtils.isNotBlank(billPayInquiry.getOnBeHalfOfMDN())) {
			billPayments.setInfo1(billPayInquiry.getOnBeHalfOfMDN());
		}
		// Merchant Data is for Flashiz QR Payment .Store merchant data in Info1 of billPayments table
		else if (StringUtils.isNotBlank(billPayInquiry.getMerchantData())) {
			billPayments.setInfo1(billPayInquiry.getMerchantData());
		}
		billPayments.setInfo2(billPayInquiry.getNarration());
		BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
		billPayDAO.save(billPayments);
		
		return billPayments;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BillPayments getBillPaymentsRecord(Long sctlID) {
		log.info("BillPaymentsServiceImpl getBillPaymentsRecord sctlID="+sctlID);
		
		BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctlID);
		List<BillPayments> billPaymentsList = billPayDAO.get(query);
		
		if(billPaymentsList!=null && !billPaymentsList.isEmpty())
		{
			return billPaymentsList.get(0);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BillPayments getBillPaymentsRecordWrapInTxn(Long sctlID) {
		log.info("BillPaymentsServiceImpl getBillPaymentsRecord sctlID="+sctlID);
		
		BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctlID);
		List<BillPayments> billPaymentsList = billPayDAO.get(query);
		
		if(billPaymentsList!=null && !billPaymentsList.isEmpty())
		{
			return billPaymentsList.get(0);
		}
		
		return null;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BillPayments saveBillPayment(BillPayments billPayments) {
		log.info("BillPaymentsServiceImpl saveBillPayment billPayments="+billPayments);
		

		BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
		billPayDAO.save(billPayments);
		
		return billPayments;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateBillPayStatus(Long sctlId, Integer billPayStatus){
		log.info("BillPaymentsServiceImpl :: updateBillPayStatus sctlId="+sctlId+", billPayStatus="+billPayStatus);
		
		BillPayments billPayments = getBillPaymentsRecord(sctlId);
		billPayments.setBillPayStatus(billPayStatus);
		saveBillPayment(billPayments);
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateBillPayStatus(MCEMessage mceMessage, String transferType) {
		log.info("BillPaymentsServiceImpl updateBillPayStatus transferType="+transferType+", mceMessage="+mceMessage);
		
		CFIXMsg request = mceMessage.getRequest();
		CFIXMsg response = mceMessage.getResponse();
		
		Long sctlId = ((CMBase)request).getServiceChargeTransactionLogID();
		BillPayments billPayments = getBillPaymentsRecord(sctlId);
		
		if(request instanceof CMBillPayInquiry){
			if(response instanceof CMTransferInquiryToBank){
				if(SOURCE_TO_DESTINATION.equals(transferType)){
					billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_INQ_PENDING);
				}
				else if(SOURCE_TO_SUSPENSE.equals(transferType)){
					billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_INQ_PENDING);
				}
			}
		}
		
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Collection<BillPayments> getBillPaymentsWithStatus(Collection<Integer> billPayStatus) {
		log.info("BillPaymentsServiceImpl :> getBillPaymentsWithStatus billPayStatus="+billPayStatus);
		List<BillPayments> billPayments = new ArrayList<BillPayments>();
		
		BillPaymentsDAO billPaymentsDao = DAOFactory.getInstance().getBillPaymentDAO();
		BillPaymentsQuery billPayQuery = new BillPaymentsQuery();
		billPayQuery.setBillPayStatuses(billPayStatus);
		billPayments = billPaymentsDao.get(billPayQuery);
		
		return billPayments;
	}
}
