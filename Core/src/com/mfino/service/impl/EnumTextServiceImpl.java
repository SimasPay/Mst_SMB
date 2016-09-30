/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.EnumTextConstants;
import com.mfino.constants.GeneralConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.EnumTextDAO;
import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;

/**
 *
 * @author xchen
 */
@Service("EnumTextServiceImpl")
public class EnumTextServiceImpl implements EnumTextService{
	private static Logger log = LoggerFactory.getLogger(EnumTextServiceImpl.class); 
    private static HashMap<String, HashMap<String, String>> cache =
            new HashMap<String, HashMap<String, String>>();

    /**
     * 
     * @param tagId
     * @param lang
     */
    public void invalidateEnumTextSet(Integer tagId, Integer lang) {
        EnumTextQuery query = new EnumTextQuery();
        query.setTagId(tagId);
        //invalidate the default language too
        String defaultCacheKey = query.getUniqueId();
        query.setLanguage(lang);
        String cacheKey = query.getUniqueId();

        cache.remove(defaultCacheKey);
        cache.remove(cacheKey);
    }

    /**
     * Retruns a map conaining the enumCode as the key and the displayText as the value of the enumText records with the 
     * given tagId and language
     * @param tagId
     * @param lang
     * @return
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public HashMap<String, String> getEnumTextSet(Integer tagId, Integer lang) {
    	
    	EnumTextDAO dao = DAOFactory.getInstance().getEnumTextDAO();
    	//EnumTextDAO dao = new EnumTextDAO();
    	EnumTextQuery query = new EnumTextQuery();
        query.setTagId(tagId);
        if (lang == null) {
            query.setLanguage(CmFinoFIX.Language_English);
        } else {
            query.setLanguage(lang);
        }

        String cacheKey = query.getUniqueId();

        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        } else {
            List<EnumText> results = dao.get(query);

            //if there is no text coming back, fall back to English
            if (results.size() <= 0) {
                query.setLanguage(CmFinoFIX.Language_English);
                results = dao.get(query);

                if (results.size() <= 0) {
                    log.error(String.format("No text for tag %1$s and language %2$s", query.getTagId(), query.getLanguage()));
                }
            }

            HashMap<String, String> map = new HashMap<String, String>();
            for (EnumText e : results) {
                map.put(e.getEnumcode(), e.getDisplaytext());
            }

            cache.put(cacheKey, map);

            return map;
        }
    }

    /**
     * Returns the enumTextValue in hexadecimal of the enumText record with the given tagID and enumCode
     * @param tagId
     * @param lang
     * @param enumCode
     * @return
     */
    public String getEnumTextValueHex(Integer tagId, Integer lang, Integer enumCode) {
        if (enumCode == null) {
            return null;
        }
        //FIXME: Quickfix
        String enumCodeHexStr = "0x" + Integer.toHexString(enumCode);

        //System.out.println(enumCodeHexStr);
        return getEnumTextValue(tagId, lang, enumCodeHexStr);
    }

    /**
     * Returns the enumTextValue of the enumText record with the given tagID and enumCode
     * @param tagId
     * @param lang
     * @param enumCode
     * @return
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public String getEnumTextValue(Integer tagId, Integer lang, Object enumCode) {
        if (enumCode == null) {
            return null;
        }
        String enumCodeString = enumCode.toString();
        HashMap<String, String> enumMap = getEnumTextSet(tagId, lang);
        return enumMap.get(enumCodeString);
    }

    /**
     * 
     * @param tagId
     * @param lang
     * @param enumCode
     * @return
     * @throws NumberFormatException
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public String getRestrictionsText(Integer tagId, Integer lang, String enumCode) throws NumberFormatException {
        String restrictionsText = GeneralConstants.EMPTY_STRING;
        HashMap<String, String> enumMap = getEnumTextSet(tagId, lang);
        List<String> restrictions = new ArrayList<String>();
        int code = Integer.parseInt(enumCode);
        int keyValInt;
        for (Iterator it = enumMap.keySet().iterator(); it.hasNext();) {
            String keyVal = (String) it.next();
            if (keyVal.length() > 2 && keyVal.charAt(1) == 'x') {
                keyValInt = Integer.decode(keyVal);
            } else {
                keyValInt = Integer.parseInt(keyVal);
            }
            if ((code & keyValInt) > 0) {
                restrictions.add(getEnumTextValue(tagId, lang, keyVal));
            }
        }
        Iterator resIt = restrictions.iterator();
        if (resIt != null && resIt.hasNext()) {
            restrictionsText = (String) resIt.next();
            while (resIt.hasNext()) {
                restrictionsText += GeneralConstants.COMMA_STRING + GeneralConstants.SINGLE_SPACE + resIt.next();
            }
        } else {
            restrictionsText = getEnumTextValue(tagId, lang, EnumTextConstants.ENUM_FOR_NONE);
        }
        return restrictionsText;
    }

    /**
     * This method is for converting DistributionPermissions to DistributionPermissionsText and removing "DistributeAll" from the permissionsText 
     * @param permissions
     * @return
     */
    public String getLevelPermissionsText(Integer permissions) {
        String permText = getRestrictionsText(CmFinoFIX.TagID_DistributionPermissions, null, permissions.toString());
        String distributeAll = GeneralConstants.COMMA_STRING + GeneralConstants.SINGLE_SPACE + getEnumTextValueHex(CmFinoFIX.TagID_DistributionPermissions, null, CmFinoFIX.DistributionPermissions_DistributeAll);

        permText = StringUtils.remove(permText, distributeAll);

        return permText;
    }
    
    /**
     * Gets list of enumText from database based on query
     * @param enumTextQuery
     * @return
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<EnumText> getEnumText(EnumTextQuery enumTextQuery){
    	EnumTextDAO enumTextDAO = DAOFactory.getInstance().getEnumTextDAO();
		List<EnumText> lstEnumTexts = enumTextDAO.get(enumTextQuery); 
		return lstEnumTexts;
    }
}
