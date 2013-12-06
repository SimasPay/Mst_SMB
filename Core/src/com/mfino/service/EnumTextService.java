/**
 * 
 */
package com.mfino.service;

import java.util.HashMap;
import java.util.List;

import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;

/**
 * @author Sreenath
 *
 */
public interface EnumTextService {
	/**
     * 
     * @param tagId
     * @param lang
     */
    public void invalidateEnumTextSet(Integer tagId, Integer lang);

    /**
     * Retruns a map conaining the enumCode as the key and the displayText as the value of the enumText records with the 
     * given tagId and language
     * @param tagId
     * @param lang
     * @return
     */
    public HashMap<String, String> getEnumTextSet(Integer tagId, Integer lang);

    /**
     * Returns the enumTextValue in hexadecimal of the enumText record with the given tagID and enumCode
     * @param tagId
     * @param lang
     * @param enumCode
     * @return
     */
    public String getEnumTextValueHex(Integer tagId, Integer lang, Integer enumCode);
    /**
     * Returns the enumTextValue of the enumText record with the given tagID and enumCode
     * @param tagId
     * @param lang
     * @param enumCode
     * @return
     */
    public String getEnumTextValue(Integer tagId, Integer lang, Object enumCode);

    /**
     * 
     * @param tagId
     * @param lang
     * @param enumCode
     * @return
     * @throws NumberFormatException
     */
    public String getRestrictionsText(Integer tagId, Integer lang, String enumCode) throws NumberFormatException;

    /**
     * This method is for converting DistributionPermissions to DistributionPermissionsText and removing "DistributeAll" from the permissionsText 
     * @param permissions
     * @return
     */
    public String getLevelPermissionsText(Integer permissions);
    
    /**
     * Gets list of enumText from database based on query
     * @param enumTextQuery
     * @return
     */
    public List<EnumText> getEnumText(EnumTextQuery enumTextQuery);

}
