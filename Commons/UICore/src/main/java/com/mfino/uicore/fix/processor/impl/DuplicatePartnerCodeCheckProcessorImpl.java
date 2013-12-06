package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Partner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDuplicatePartnerCodeCheck;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DuplicatePartnerCodeCheckProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("DuplicatePartnerCodeCheckProcessorImpl")
public class DuplicatePartnerCodeCheckProcessorImpl extends BaseFixProcessor implements DuplicatePartnerCodeCheckProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
    	boolean duplicate = false;
        CMJSDuplicatePartnerCodeCheck realMsg = (CMJSDuplicatePartnerCodeCheck) msg;
        String partnerCode = realMsg.getPartnerCode();
        
        if (StringUtils.isNotBlank(partnerCode)) {
    		PartnerDAO dao = DAOFactory.getInstance().getPartnerDAO();
    		PartnerQuery query = new PartnerQuery();
    		query.setPartnerCode(partnerCode);
    		List<Partner> results = dao.get(query);
    		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
    			duplicate = true;
    		}
        }

        CMJSError err=new CMJSError();

        if(duplicate){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Partner Code already exists in DB, please enter different Partner Code."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._("Name Available"));
        }

        return err;
    }
}