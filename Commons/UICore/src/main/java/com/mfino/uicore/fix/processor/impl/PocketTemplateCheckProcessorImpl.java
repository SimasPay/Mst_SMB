/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.PocketTemplate;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSPocketTemplateCheck;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PocketTemplateCheckProcessor;

/**
 * 
 * @author sunil
 */
@Service("PocketTemplateCheckProcessorImpl")
public class PocketTemplateCheckProcessorImpl extends BaseFixProcessor implements PocketTemplateCheckProcessor {

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
  public CFIXMsg process(CFIXMsg msg) {


    CMJSPocketTemplateCheck realMsg = (CMJSPocketTemplateCheck) msg;
    PocketTemplateDAO dao = DAOFactory.getInstance().getPocketTemplateDao();
    PocketTemplateQuery query = new PocketTemplateQuery();
    query.setExactPocketDescription(realMsg.getPocketTemplateName());
    List<PocketTemplate> results = dao.get(query);

    // TODO : Send possible username that would be available in the DB

    CMJSError err = new CMJSError();

    if (results.size() > 0) {
      err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
      err.setErrorDescription(MessageText._("Pocket Template Name Already Exists in DB, Please Ener a Different UserName"));
    } else {
      err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
      err.setErrorDescription(MessageText._("Pocket Template Name Available"));
    }

    return err;
  }


}
