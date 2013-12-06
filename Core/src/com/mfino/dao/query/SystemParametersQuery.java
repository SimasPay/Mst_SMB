/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

/**
 *
 * @author Amar
 */
public class SystemParametersQuery extends BaseQuery {

    private String _parameterName;
    private String _paremeterValue;
    private String _Description;
    
    
	public String getParameterName() {
		return _parameterName;
	}
	public void setParameterName(String _parameterName) {
		this._parameterName = _parameterName;
	}
	public String getParemeterValue() {
		return _paremeterValue;
	}
	public void setParemeterValue(String _paremeterValue) {
		this._paremeterValue = _paremeterValue;
	}
	public String getDescription() {
		return _Description;
	}
	public void setDescription(String _Description) {
		this._Description = _Description;
	}
    
}
    
    
    