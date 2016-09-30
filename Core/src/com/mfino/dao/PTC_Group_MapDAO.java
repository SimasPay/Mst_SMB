package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PTC_Group_MapQuery;
import com.mfino.domain.Groups;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.PtcGroupMapping;

public class PTC_Group_MapDAO extends BaseDAO<PtcGroupMapping> {
	
	public List<PtcGroupMapping> get(PTC_Group_MapQuery query) {
		Criteria criteria = createCriteria();

		if (query.getGroupID() != null) {
			criteria.createAlias(PtcGroupMapping.FieldName_Group, "g");
			criteria.add(Restrictions.eq("g."+Groups.FieldName_RecordID, query.getGroupID()));
			
		}
		if (query.getPtcID() != null) {
			criteria.createAlias(PtcGroupMapping.FieldName_PocketTemplateConfigByPtcID, "p");
			criteria.add(Restrictions.eq("p."+PocketTemplateConfig.FieldName_RecordID, query.getPtcID()));
			
		}
	
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<PtcGroupMapping> results = criteria.list();

		return results;
	}
	//deleting any previous rows in ptc_group_mapping file with same ptcid as ptcid to be updated
	public void deleteById(long id) {
		PTC_Group_MapQuery pgmq =new PTC_Group_MapQuery();
		pgmq.setPtcID(id);
		List<PtcGroupMapping> result = get(pgmq);
		if (CollectionUtils.isNotEmpty(result)) {
			for (PtcGroupMapping pgm: result) {
				delete(pgm);
			}	
		}

    }
	//for getting related columns from ptc_group_map table according to ptcid
	public PtcGroupMapping getByPtcId(long id){
		PTC_Group_MapQuery pgmq=new PTC_Group_MapQuery();
		pgmq.setPtcID(id);
		List<PtcGroupMapping> result=get(pgmq);
		if((null != result) && (result.size() > 0)){
		return result.get(0);
		}
		return null;
	}

}
