/**
 * 
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ProductReferralDAO;
import com.mfino.dao.query.ProductReferralQuery;
import com.mfino.domain.ProductReferral;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSProductReferral;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ProductReferralProcessor;
import com.mfino.uicore.web.WebContextError;

/**
 * @author Admin
 *
 */
@Service("ProductReferralProcessorImpl")
public class ProductReferralProcessorImpl extends BaseFixProcessor implements ProductReferralProcessor{
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ProductReferralDAO productReferralDAO = DAOFactory.getInstance().getProductReferralDAO();
	
	private void updateEntity(ProductReferral productReferral, CMJSProductReferral.CGEntries e) {
		if (StringUtils.isNotBlank(e.getAgentMDN())) {
			productReferral.setAgentMDN(e.getAgentMDN());
		}

		if (StringUtils.isNotBlank(e.getSubscriberMDN())) {		
			productReferral.setSubscriberMDN(e.getSubscriberMDN());
		}

		if (StringUtils.isNotBlank(e.getFullName())) {
			productReferral.setFullName(e.getFullName());
		}
		
		if (StringUtils.isNotBlank(e.getEmail())) {
			productReferral.setEmail(e.getEmail());
		}
		
		if (StringUtils.isNotBlank(e.getProductDesired())) {
			productReferral.setProductDesired(e.getProductDesired());
		}
		
		if (StringUtils.isNotBlank(e.getOthers())) {
			productReferral.setOthers(e.getOthers());
		}

	}

	
	private void updateMessage(ProductReferral productReferral, CMJSProductReferral.CGEntries e) {
		e.setID(productReferral.getID());
		e.setAgentMDN(productReferral.getAgentMDN());
		e.setSubscriberMDN(productReferral.getSubscriberMDN());
		e.setFullName(productReferral.getFullName());
		e.setEmail(productReferral.getEmail());		
		e.setProductDesired(productReferral.getProductDesired());
		e.setOthers(productReferral.getOthers());
		e.setRecordVersion(productReferral.getVersion());
		e.setCreatedBy(productReferral.getCreatedBy());
		e.setCreateTime(productReferral.getCreateTime());
		e.setUpdatedBy(productReferral.getUpdatedBy());
		e.setLastUpdateTime(productReferral.getLastUpdateTime());
		
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor=Throwable.class)		
	public CFIXMsg process(CFIXMsg msg) throws Exception {

		CMJSProductReferral realMsg = (CMJSProductReferral) msg;
		if
		(CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			ProductReferralQuery query = new ProductReferralQuery();

			if (StringUtils.isNotBlank(realMsg.getAgentMDNSearch())) {
				query.setAgentMDN(realMsg.getAgentMDNSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getFullNameSearch())) {
				query.setFullName(realMsg.getFullNameSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getSubscriberMDNSearch())) {
				query.setSubscriberMDN(realMsg.getSubscriberMDNSearch());
			}
			
			if (StringUtils.isNotBlank(realMsg.getProductDesiredSearch())) {
				query.setProductDesired(realMsg.getProductDesiredSearch());
			}
			
			if (realMsg.getStartDateSearch() != null) {
				query.setStartDate(realMsg.getStartDateSearch());
			}
			if (realMsg.getEndDateSearch() != null) {
				query.setEndDate(realMsg.getEndDateSearch());
			}
			
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<ProductReferral> results = productReferralDAO.get(query);
			
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				ProductReferral productReferral = results.get(i);
				CMJSProductReferral.CGEntries entry = new CMJSProductReferral.CGEntries();
				updateMessage(productReferral, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		} 
		
		
		else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CMJSProductReferral.CGEntries[] entries = realMsg.getEntries();

			for (CMJSProductReferral.CGEntries e : entries) {
				ProductReferral productReferral = new ProductReferral();
				updateEntity(productReferral, e);
				try {
					validate(productReferral);
					productReferralDAO.save(productReferral);
				} catch (Exception ex) {
					handleException(ex);
				}
				updateMessage(productReferral, e);
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} 
		return realMsg;

	}


	private void validate(ProductReferral s) throws Exception 
	{
		List<ProductReferral> entries = DAOFactory.getInstance().getProductReferralDAO().getAll();
		Iterator<ProductReferral> it = entries.iterator();
		while(it.hasNext())
		{
			ProductReferral existingProductReferral = it.next();
			
			if(s.getAgentMDN().equals(existingProductReferral.getAgentMDN())  && (s.getID()!=null && !(s.getID().equals(existingProductReferral.getID()))))
			{				
				throw new Exception("ProductReferral already exists");
			}
		}
	}

	
	private CFIXMsg handleException(Exception e) throws Exception {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		String message = MessageText._("ProductReferral with given productDesired name already exists, Please enter different name.");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(CmFinoFIX.CRProductReferral.FieldName_ProductDesired);
		newEntries[0].setErrorDescription(message);
		log.warn(message, e);
		WebContextError.addError(errorMsg);
		throw e;
	}

	
}
