package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PTC_Group_MapQuery;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.PTC_Group_Map;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRGroup;
import com.mfino.fix.CmFinoFIX.CRPTC_Group_Map;
import com.mfino.fix.CmFinoFIX.CRPocketTemplateConfig;

public class PTC_Group_MapDAO extends BaseDAO<PTC_Group_Map> {
	
	public List<PTC_Group_Map> get(PTC_Group_MapQuery query) {
		Criteria criteria = createCriteria();

		if (query.getGroupID() != null) {
			criteria.createAlias(CRPTC_Group_Map.FieldName_Group, "g");
			criteria.add(Restrictions.eq("g."+CRGroup.FieldName_RecordID, query.getGroupID()));
			
		}
		if (query.getPtcID() != null) {
			criteria.createAlias(CRPTC_Group_Map.FieldName_PocketTemplateConfigByPtcID,"p");
			criteria.add(Restrictions.eq("p."+CRPocketTemplateConfig.FieldName_RecordID,query.getPtcID()));
			
		}
	
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<PTC_Group_Map> results = criteria.list();

		return results;
	}
	//deleting any previous rows in ptc_group_mapping file with same ptcid as ptcid to be updated
	public void deleteById(long id) {
		PTC_Group_MapQuery pgmq =new PTC_Group_MapQuery();
		pgmq.setPtcID(id);
		List<PTC_Group_Map> result = get(pgmq);
		if (CollectionUtils.isNotEmpty(result)) {
			for (PTC_Group_Map pgm: result) {
				delete(pgm);
			}	
		}

    }
	//for getting related columns from ptc_group_map table according to ptcid
	public PTC_Group_Map getByPtcId(long id){
		PTC_Group_MapQuery pgmq=new PTC_Group_MapQuery();
		pgmq.setPtcID(id);
		List<PTC_Group_Map> result=get(pgmq);
		if((null != result) && (result.size() > 0)){
		return result.get(0);
		}
		return null;
	}

}
