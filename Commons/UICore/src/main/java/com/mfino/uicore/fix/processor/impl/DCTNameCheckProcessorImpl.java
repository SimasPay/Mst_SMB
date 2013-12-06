/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDistributionTemplateCheck;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DCTNameCheckProcessor;

/**
 *
 * @author sunil
 */
@Service("DCTNameCheckProcessorImpl")
public class DCTNameCheckProcessorImpl extends BaseFixProcessor implements DCTNameCheckProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

        CMJSDistributionTemplateCheck realMsg = (CMJSDistributionTemplateCheck) msg;
        DistributionChainTemplateDAO templateDAO = DAOFactory.getInstance().getDistributionChainTemplateDAO();
        DistributionChainTemplateQuery query = new DistributionChainTemplateQuery();
        query.setExactdistributionChainTemplateName(realMsg.getDCTName());

        List<DistributionChainTemplate> results = templateDAO.get(query);

        CMJSError err=new CMJSError();

        if(results.size()> 0){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Template Name Already Exists in DB, Please select a different TemplateName"));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._("Template Name Available"));
        }

        return err;
    }
}