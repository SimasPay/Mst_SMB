/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMJSGetBulkUploadFileData;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.GetBulkUploadFileDataProcessor;

/**
 *
 * @author Raju
 */
@Service("GetBulkUploadFileDataProcessorImpl")
public class GetBulkUploadFileDataProcessorImpl extends BaseFixProcessor implements GetBulkUploadFileDataProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        BulkUploadDAO dao = DAOFactory.getInstance().getBulkUploadDAO();
        BulkUploadQuery query = new BulkUploadQuery();
        CMJSGetBulkUploadFileData realMsg = (CMJSGetBulkUploadFileData) msg;
        query.setId(realMsg.getBulkUploadID());
        List<BulkUpload> results = dao.get(query);
        if(results.size() > 0)
        {
        	String reportfiledata = results.get(0).getReportfiledata();
            realMsg.setReportFileData(reportfiledata);
            realMsg.setsuccess(Boolean.TRUE);
        }
        else
        {
            realMsg.setsuccess(Boolean.FALSE);
        }
        return realMsg;
    }
}

