/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author xchen
 */
public class EnumTextQuery extends BaseQuery {

    private Integer _tagId;
    private Integer _language;
    private String _fieldName;
    private String _tagName;
    private String _displayText;
    private String _enumCode;
   
    public String getDisplayText() {
        return _displayText;
    }

    public void setDisplayText(String _displayText) {
        this._displayText = _displayText;
    }
    

    /**
     * @return the _fieldId
     */
    public Integer getTagId() {
        return _tagId;
    }

    /**
     * @param fieldId the _fieldId to set
     */
    public void setTagId(Integer tagId) {
        this._tagId = tagId;
    }

    /**
     * @return the _language
     */
    public Integer getLanguage() {
        return _language;
    }

    /**
     * @param language the _language to set
     */
    public void setLanguage(Integer language) {
        this._language = language;
    }

    public String getUniqueId(){
        return this._tagId + "," + this._language;
    }

    public String getFieldName() {
        return _fieldName;
    }

    public void setFieldName(String _fieldName) {
        this._fieldName = _fieldName;
    }

    public String getTagName() {
        return _tagName;
    }

    public void setTagName(String _tagName) {
        this._tagName = _tagName;
    }

    /**
     * @return the _enumCode
     */
    public String getEnumCode() {
        return _enumCode;
    }

    /**
     * @param enumCode the _enumCode to set
     */
    public void setEnumCode(String enumCode) {
        this._enumCode = enumCode;
    }



}
