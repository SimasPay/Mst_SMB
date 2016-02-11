package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AutoReversalsDao;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.service.SCTLService;

/**
 * @author Shashank
 *
 */

/**
 * This class should contain all the functionality related to service charge transaction log.
 */
@Service("SCTLServiceImpl")
public class SCTLServiceImpl implements SCTLService{

	
	/**
	 * finds out sctl with given integrationId and returns the value
	 * @param transactionID and Info1
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<ServiceChargeTransactionLog> getBySCTLIntegrationTxnID(String TXNID, String Info1){
 	
		ServiceChargeTransactionLogDAO sctldao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		
	 	sctlQuery.setIntegrationTxnID(Long.parseLong(TXNID));
		sctlQuery.setInfo1(Info1);
		
		List<ServiceChargeTransactionLog> sctlList = sctldao.get(sctlQuery);
		
		return sctlList;
		
	}
	
	/**
	 *  Updates sctl with the status provided.
	 * @param status , sctl
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateSCTLStatus(Integer status, ServiceChargeTransactionLog sctl){
		
		ServiceChargeTransactionLogDAO sctldao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		sctl.setStatus(status);
		sctldao.save(sctl);
	}
	
	/**
	 *  get sctl from id.
	 * @param parentTxnId
	 * @return sctl
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public ServiceChargeTransactionLog getBySCTLID(long id){
		
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDAO.getById(id);
		return sctl;
				
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public ServiceChargeTransactionLog getByTransactionLogId(Long TxnLogID){
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctlForMFA = sctlDAO.getByTransactionLogId(TxnLogID);
		return sctlForMFA;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<ServiceChargeTransactionLog> getByQuery(ServiceChargeTransactionsLogQuery sctlQuery){
		ServiceChargeTransactionLogDAO sctldao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		List<ServiceChargeTransactionLog> sctlList = sctldao.get(sctlQuery);
		return sctlList;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CommodityTransfer getCTfromSCTL(ServiceChargeTransactionLog sctl){
		Long ctId = sctl.getCommodityTransferID();
		CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
		return  ctDao.getById(ctId);
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public AutoReversals getAutoReversalsFromSCTL(ServiceChargeTransactionLog sctl){
		AutoReversalsDao arDao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals ar = arDao.getBySctlId(sctl.getID());
		return ar;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public void saveSCTL(ServiceChargeTransactionLog sctl){
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		sctlDAO.save(sctl);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public List<ServiceChargeTransactionLog> getSubscriberPendingTransactions(ServiceChargeTransactionsLogQuery query) {
		
		ServiceChargeTransactionLogDAO sctldao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		List<ServiceChargeTransactionLog> sctlList = sctldao.getSubscriberPendingTransactions(query);
		return sctlList;
	}
}

