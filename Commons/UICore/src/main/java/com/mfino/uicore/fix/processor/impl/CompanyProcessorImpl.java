/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CompanyQuery;
import com.mfino.domain.Company;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCompany;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CompanyProcessor;

/**
 *
 * @author Diwakar
 */
@Service("CompanyProcessorImpl")
public class CompanyProcessorImpl extends BaseFixProcessor implements CompanyProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSCompany realMsg = (CMJSCompany) msg;
        CompanyQuery query = new CompanyQuery();
        CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {            
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            List<Company> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Company s = results.get(i);


                CMJSCompany.CGEntries entry =
                        new CMJSCompany.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        }
        return realMsg;
    }
    private void updateMessage(Company company, CmFinoFIX.CMJSCompany.CGEntries entry) {
        if (company.getCompanyCode() != null) {
            entry.setCompanyCode(company.getCompanyCode());
        }
        if (company.getCompanyName() != null) {
            entry.setCompanyName(company.getCompanyName());
        }
        entry.setID(company.getID());
    }
}
