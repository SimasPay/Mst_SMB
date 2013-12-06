/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.EnumTextSimpleProcessor;

/**
 *
 * @author xchen
 */
@Service("EnumTextSimpleProcessorImpl")
public class EnumTextSimpleProcessorImpl extends BaseFixProcessor implements EnumTextSimpleProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CmFinoFIX.CMJSEnumTextSimple realMsg = (CmFinoFIX.CMJSEnumTextSimple) msg;

        if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            //TODO : get the language information from the user context
        	
            HashMap<String, String> results;
            if(StringUtils.isNotBlank(realMsg.getEnumCode())){
            	String	enumValue = enumTextService.getEnumTextValue(realMsg.getTagIDSearch(), null, realMsg.getEnumCode());
            	results = new HashMap<String, String>();
            	results.put(realMsg.getEnumCode(), enumValue);
            }else{
            	results= enumTextService.getEnumTextSet(realMsg.getTagIDSearch(), null);
            }
            realMsg.allocateEntries(results.size());
            int i = 0;
            for (Object key : results.keySet()) {
                Object value = results.get(key);
                CmFinoFIX.CMJSEnumTextSimple.CGEntries entry = new CmFinoFIX.CMJSEnumTextSimple.CGEntries();
                entry.setEnumCode((String)key);
                entry.setDisplayText((String)value);
                realMsg.getEntries()[i] = entry;
                i++;
            }
        }
        return realMsg;
    }
}

