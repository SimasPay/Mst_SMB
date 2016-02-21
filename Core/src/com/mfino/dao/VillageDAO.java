package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.VillageQuery;
import com.mfino.domain.Village;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;

/**
 * 
 * @author Srinivaas
 */
public class VillageDAO extends BaseDAO<Village> {

	public List<Village> get(VillageQuery query) {
		Criteria criteria = createCriteria();
		
		criteria.add(Restrictions.eq(CmFinoFIX.CRVillage.FieldName_IdDistrict, query.getIdDistrict()));

		addOrder(QueryConstants.ASC_STRING, CmFinoFIX.CRVillage.FieldName_VillageId, criteria);	
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<Village> results = criteria.list();

		return results;
	}

    public  String getVillageName(String villRecId) {
    	Long id = null;
    	try{
    		id = Long.valueOf(villRecId);
    	}catch(Exception e){
    		log.info(MessageText._("Given village code is not a number : ") + villRecId);
    		return null;
    	}
    	Village village = getById(id);
        if (village == null) {
            log.info(MessageText._("No Village Code defined with the given code : ") + villRecId);
            return null;
        }
        return village.getDisplayText();
    }
}