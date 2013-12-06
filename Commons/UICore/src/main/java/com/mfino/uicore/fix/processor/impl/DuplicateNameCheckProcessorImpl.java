package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.RoleDAO;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.dao.query.ChargeTypeQuery;
import com.mfino.dao.query.RoleQuery;
import com.mfino.dao.query.TransactionRuleQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.ChargeType;
import com.mfino.domain.Group;
import com.mfino.domain.Role;
import com.mfino.domain.TransactionRule;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDuplicateNameCheck;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DuplicateNameCheckProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("DuplicateNameCheckProcessorImpl")
public class DuplicateNameCheckProcessorImpl extends BaseFixProcessor implements DuplicateNameCheckProcessor{

	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
    	boolean duplicate = false;
        CMJSDuplicateNameCheck realMsg = (CMJSDuplicateNameCheck) msg;
        String tableName = realMsg.getTableName();
        String name = realMsg.getName();
        
        if (StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(name)) {
        	if ("Charge Type".equals(tableName)) {
        		ChargeTypeDAO dao = DAOFactory.getInstance().getChargeTypeDAO();
        		ChargeTypeQuery query = new ChargeTypeQuery();
        		query.setExactName(name);
        		List<ChargeType> results = dao.get(query);
        		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
        			duplicate = true;
        		}
        	} else if ("Charge Definition".equals(tableName)) {
        		ChargeDefinitionDAO dao = DAOFactory.getInstance().getChargeDefinitionDAO();
        		ChargeDefinitionQuery query = new ChargeDefinitionQuery();
        		query.setExactName(name);
        		List<ChargeDefinition> results = dao.get(query);
        		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
        			duplicate = true;
        		}
        	} else if ("Transaction Rule".equals(tableName)) {
        		TransactionRuleDAO dao = DAOFactory.getInstance().getTransactionRuleDAO();
        		TransactionRuleQuery query = new TransactionRuleQuery();
        		query.setExactName(name);
        		List<TransactionRule> results = dao.get(query);
        		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
        			duplicate = true;
        		}
        	} else if ("Group".equals(tableName)) {
        		GroupDao dao = DAOFactory.getInstance().getGroupDao();
        		Group group = dao.getByName(name);
        		if(group != null){
        			duplicate = true;
        		}
        	} else if ("Role".equals(tableName)) {
        		RoleDAO roleDao = DAOFactory.getInstance().getRoleDAO();
        		RoleQuery query = new RoleQuery();
        		query.setDisplayText(name);
        		List<Role> results = roleDao.get(query);
        		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
        			duplicate = true;
        		}
        	}
        }

        CMJSError err=new CMJSError();

        if(duplicate){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._(tableName + " Name already exists in DB, please enter different name."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._("Name Available"));
        }

        return err;
    }
}