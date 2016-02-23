package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.ProvinceQuery;
import com.mfino.domain.Province;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;

/**
 * 
 * @author Srinivaas
 */
public class ProvinceDAO extends BaseDAO<Province> {

	public List<Province> get(ProvinceQuery query) {
		Criteria criteria = createCriteria();
		
		addOrder(QueryConstants.ASC_STRING, CmFinoFIX.CRProvince.FieldName_ProvinceId, criteria);
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<Province> results = criteria.list();

		return results;
	}

    public  String getProvinceName(String proRecId) {
    	Long id = null;
    	try{
    		id = Long.valueOf(proRecId);
    	}catch(Exception e){
    		log.info(MessageText._("Given province code is not a number : ") + proRecId);
    		return null;
    	}
		Province province = getById(id);
        if (province == null) {
            log.info(MessageText._("No Province Code defined with the given code : ") + proRecId);
            return null;
        }
        return province.getDisplayText();
    }
}
