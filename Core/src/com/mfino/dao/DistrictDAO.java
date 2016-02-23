package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.DistrictQuery;
import com.mfino.domain.District;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;

/**
 * 
 * @author Srinivaas
 */
public class DistrictDAO extends BaseDAO<District> {

	public List<District> get(DistrictQuery query) {
		Criteria criteria = createCriteria();
		
		criteria.add(Restrictions.eq(CmFinoFIX.CRDistrict.FieldName_IdRegion, query.getIdRegion()));

		addOrder(QueryConstants.ASC_STRING, CmFinoFIX.CRDistrict.FieldName_DistrictId, criteria);
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<District> results = criteria.list();

		return results;
	}
	
    public  String getDistrictName(String distRecId) {
    	Long id = null;
    	try{
    		id = Long.valueOf(distRecId);
    	}catch(Exception e){
    		log.info(MessageText._("Given district code is not a number : ") + distRecId);
    		return null;
    	}
    	District district = getById(id);
        if (district == null) {
            log.info(MessageText._("No District Code defined with the given code : ") + distRecId);
            return null;
        }
        return district.getDisplayText();
    }
}
