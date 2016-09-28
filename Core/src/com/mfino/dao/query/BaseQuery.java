/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.QueryConstants;
import com.mfino.domain.Company;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 *
 * @author xchen
 */
public class BaseQuery {

    private Long _id;
    private Integer _start;
    private Integer _limit;
    private Integer _total;
    private LinkedHashMap<String,String> _orderMap = new LinkedHashMap<String,String>();
    private Date _lastUpdateTimeGE;
    private Date _lastUpdateTimeLT;
    
    private Date _createTimeGE;
    private Date _createTimeLT;
    
    private boolean _idOrdered;
    private Company _company;

    public Company getCompany() {
        return _company;
    }

    public void setCompany(Company _company) {
        this._company = _company;
    }

    public Date getCreateTimeGE() {
        return _createTimeGE;
    }

    public void setCreateTimeGE(Date _createTimeGE) {
        this._createTimeGE = _createTimeGE;
    }

    public Date getCreateTimeLT() {
        return _createTimeLT;
    }

    public void setCreateTimeLT(Date _createTimeLT) {
        this._createTimeLT = _createTimeLT;
    }

    private String _sortString;
   
    /**
     * @return the _start
     */
    public Integer getStart() {
        return _start;
    }

    /**
     * @param start the _start to set
     */
    public void setStart(Integer start) {
        this._start = start;
    }

    /**
     * @return the _limit
     */
    public Integer getLimit() {
        return _limit;
    }

    /**
     * @param limit the _limit to set
     */
    public void setLimit(Integer limit) {
        this._limit = limit;
    }

    /**
     * @return the _total
     */
    public Integer getTotal() {
        return _total;
    }

    /**
     * @param total the _total to set
     */
    public void setTotal(Integer total) {
        this._total = total;
    }

    public String getSortString() {
        return _sortString;
    }

    public void setSortString(String _sortString) {
        this._sortString = _sortString;

        if (_sortString != null) {
            parseSortString(_sortString);
        }
    }

    /**
     *  This method populates the orderMap from the input String.
     *
     *  @param String _sortString
     *  This method assumes that the string is composed of unit like
     *  (columnName1:asc,columnName2:desc,........)
     *
     */
    private void parseSortString(String _sortString) {

        String[] columnUnits = _sortString.split(QueryConstants.COLUMN_UNIT_DELIMITER);

        for (String string : columnUnits) {

            String[] columnUnit = string.split(QueryConstants.COLUMN_ORDER_DELIMITER);

            String order = GeneralConstants.EMPTY_STRING;
            if (columnUnit[QueryConstants.ORDER_INDEX] != null) {
                order = columnUnit[1].trim();
            }

            if (!order.trim().equals(GeneralConstants.EMPTY_STRING)) {
                String colName = columnUnit[QueryConstants.COLNAME_INDEX];
                if (colName != null) {
                    colName = colName.trim();
                    if (!colName.equals(GeneralConstants.EMPTY_STRING)) {
                        _orderMap.put(colName, order);
                    }
                }
            }
        }
    }

    public LinkedHashMap<String,String> getOrderMap() {
        return _orderMap;
    }

    public void removeMappingFromOrderMap(String key)
    {
        _orderMap.remove(key);
    }

    /**
     * @return the _id
     */
    public Long getId() {
        return _id;
    }

    /**
     * @param id the _id to set
     */
    public void setId(Long id) {
        this._id = id;
    }

	public void setLastUpdateTimeGE(Date _startTime) {
		this._lastUpdateTimeGE = _startTime;
	}

	public Date getLastUpdateTimeGE() {
		return _lastUpdateTimeGE;
	}

	public void setLastUpdateTimeLT(Date _endTime) {
		this._lastUpdateTimeLT = _endTime;
	}

	public Date getLastUpdateTimeLT() {
		return _lastUpdateTimeLT;
	}
	

    public void setIDOrdered(boolean _idOrdered) {
      this._idOrdered = _idOrdered;
    }

    public boolean isIDOrdered() {
      return _idOrdered;
    }
    
    public String getQueryParams(){
    	StringBuffer buffer = new StringBuffer();
    	if(getCompany()!=null){
    		buffer.append("getCompany:");
    		buffer.append(getCompany().getId());
    		buffer.append(" ");
    	}
    	    	
    	return buffer.toString();
    }

}
