package com.mfino.fidelity.iso8583.processor.fixtoiso;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CmFinoFIX.CMBase;

public class FixToISOService {

	private Map<String, String> transactionDescriptionMap;

	private IntegrationSummaryDao integrationSummaryDao;

	public Map<String, String> getTransactionDescriptionMap() {
		return transactionDescriptionMap;
	}

	public void setTransactionDescriptionMap(Map<String, String> transactionDescriptionMap) {
		this.transactionDescriptionMap = transactionDescriptionMap;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getTransactionDiscription(CMBase msg,boolean setReconcilationID) {
		String description = null;
		if (msg.getUICategory() != null)
			description = transactionDescriptionMap.get(msg.toString());
		if (StringUtils.isBlank(description))
			description = transactionDescriptionMap.get("0");
		if (setReconcilationID) {
			integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
			IntegrationSummary integrationSummary = integrationSummaryDao.getByScltId(msg.getServiceChargeTransactionLogID(),null);//
			if (integrationSummary != null
					&& StringUtils.isNotBlank(integrationSummary.getReconcilationID1()))
				description = integrationSummary.getReconcilationID1() + ","+ description;
		}
		return description;
	}
}
