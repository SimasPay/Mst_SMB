package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;

import com.mfino.dao.query.FundEventsQuery;
import com.mfino.domain.FundEvents;

public class FundEventsDAO extends BaseDAO<FundEvents> {

	public List<FundEvents> get(FundEventsQuery query) {
		Criteria criteria = createCriteria();
		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<FundEvents> results = criteria.list();
		return results;
	}

}
