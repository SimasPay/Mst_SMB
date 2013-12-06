package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Partner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckServicePartner;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckServicePartnerProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("CheckServicePartnerProcessorImpl")
public class CheckServicePartnerProcessorImpl extends BaseFixProcessor implements CheckServicePartnerProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
    	boolean duplicate = false;
    	CMJSCheckServicePartner realMsg = (CMJSCheckServicePartner)msg;
		PartnerQuery query = new PartnerQuery();
		PartnerDAO pDAO = DAOFactory.getInstance().getPartnerDAO();
		if (realMsg.getPartnerTypeSearch() != null && CmFinoFIX.BusinessPartnerType_ServicePartner.equals(realMsg.getPartnerTypeSearch())) { 
			query.setPartnerType(realMsg.getPartnerTypeSearch());
			List<Partner> lst = pDAO.get(query);
			if (CollectionUtils.isNotEmpty(lst) && lst.size() > 0 ) {
				duplicate = true;
			}
		}

        CMJSError err=new CMJSError();

        if(duplicate){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Service Partner already defined."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._(""));
        }

        return err;
    }

}

