package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SettlementTemplateDAO;
import com.mfino.dao.query.SettlementTemplateQuery;
import com.mfino.domain.SettlementTemplate;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSSettlementTemplateCheck;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SettlementTemplateCheckProcessor;

@Service("SettlementTemplateCheckProcessorImpl")
public class SettlementTemplateCheckProcessorImpl extends BaseFixProcessor implements SettlementTemplateCheckProcessor{
    
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {

        CMJSSettlementTemplateCheck realMsg = (CMJSSettlementTemplateCheck) msg;
        SettlementTemplateDAO dao = DAOFactory.getInstance().getSettlementTemplateDAO();
        SettlementTemplateQuery query = new SettlementTemplateQuery();
        query.setExactSettlementName(realMsg.getSettlementName());
        query.setPartnerId(realMsg.getPartnerID());
        
        List<SettlementTemplate> results = dao.get(query);

        CMJSError err=new CMJSError();

        if(results.size()> 0){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Settlement Template Name Already Exists in DB, Please select a different Name"));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._("Template Name Available"));
        }

        return err;
    }
}
