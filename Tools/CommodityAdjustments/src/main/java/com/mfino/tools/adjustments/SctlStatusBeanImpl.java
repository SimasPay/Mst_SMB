package com.mfino.tools.adjustments;

import java.util.List;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AutoReversalsDao;
import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.impl.TransactionChargingServiceImpl;

public class SctlStatusBeanImpl implements SctlStatusBean {
	
	private static Logger log = LoggerFactory.getLogger(AdjustmentMessageCreator.class);

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage updateToPending(Exchange ex) {
		return update(ex, CmFinoFIX.SCTLStatus_Pending);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage updateToSuccessful(Exchange ex) {
		return update(ex, CmFinoFIX.SCTLStatus_Confirmed);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage updateToFailed(Exchange ex) {
		return update(ex, CmFinoFIX.SCTLStatus_Failed);
	}

	private MCEMessage update(Exchange ex, Integer status) {

		MCEMessage msg = ex.getIn().getBody(MCEMessage.class);
		BackendResponse be = (BackendResponse) msg.getResponse();
		
		if(be==null || be.getServiceChargeTransactionLogID()==null)
		{
			return msg;
		}

		ServiceChargeTxnLog sctl = DAOFactory.getInstance().getServiceChargeTransactionLogDAO()
		        .getById(be.getServiceChargeTransactionLogID());
		/*if(!CmFinoFIX.SCTLStatus_Failed.equals(sctl.getStatus()))
		{
			sctl.setStatus(status);
		}*/
		
		
		TransactionChargingServiceImpl tcs = new TransactionChargingServiceImpl();
		tcs.saveServiceTransactionLog(sctl);
		if(CmFinoFIX.SCTLStatus_Confirmed.equals(status))
		{
			//Update AutoReversal Status, BillPay Status, TransferToUnregistered Status
			AutoReversalsDao autoReversalDAO = DAOFactory.getInstance().getAutoReversalsDao();
			AutoReversals autoReversal = autoReversalDAO.getBySctlId(sctl.getId().longValue());
			if(autoReversal!=null)
			{
				log.info("ManualAdjustment: autoreversal status is set to complete for sctl: "+sctl.getId()+ " original status is: "+autoReversal.getAutorevstatus());
				autoReversal.setAutorevstatus(CmFinoFIX.AutoRevStatus_COMPLETED);
				autoReversalDAO.save(autoReversal);
			}
			
			BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
			BillPaymentsQuery billpayQuery = new BillPaymentsQuery();
			billpayQuery.setSctlID(sctl.getId().longValue());
			
			List<BillPayments> billPayments = billPayDAO.get(billpayQuery);
			if(!billPayments.isEmpty())
			{
				BillPayments billpayment = billPayments.get(0);
				log.info("ManualAdjustment: BillPayment status is set to complete for sctl: "+sctl.getId()+ " original status is: "+billpayment.getBillpaystatus());
				billpayment.setBillpaystatus(CmFinoFIX.BillPayStatus_COMPLETED);
				billPayDAO.save(billpayment);
			}
			
			UnRegisteredTxnInfoDAO unRegTxnInfoDAO = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
			UnRegisteredTxnInfoQuery infoQuery = new UnRegisteredTxnInfoQuery();
			infoQuery.setTransferSctlId(sctl.getId().longValue());
			List<UnregisteredTxnInfo> txns = unRegTxnInfoDAO.get(infoQuery);
			if(!txns.isEmpty())
			{
				UnregisteredTxnInfo unRegTxnInfo = txns.get(0);
				log.info("ManualAdjustment: UnRegisteredTxnInfo status is set to complete for sctl: "+sctl.getId()+ " original status is: "+unRegTxnInfo.getUnregisteredtxnstatus());
				unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_COMPLETED));
				unRegTxnInfoDAO.save(unRegTxnInfo);
			}
		}
		
		msg.setResponse(be);

		return msg;
	}
}