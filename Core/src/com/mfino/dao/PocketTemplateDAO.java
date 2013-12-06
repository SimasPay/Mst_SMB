/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;


/**
 * 
 * @author sandeepjs
 */
public class PocketTemplateDAO extends BaseDAO<PocketTemplate> {

	public static final String ID_COLNAME = "ID";

	public List<PocketTemplate> get(PocketTemplateQuery query) {

		Criteria criteria = createCriteria();

		if (query.getPocketTemplateID() != null) {
			criteria.add(Restrictions.eq(ID_COLNAME,
					query.getPocketTemplateID()));
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.gt(
					CmFinoFIX.CRPocketTemplate.FieldName_CreateTime,
					query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			// This is to make today's records return to the user.
			// Date endDatePlus1 = DateUtil.addDays(query.getEndDate(), 1);
			criteria.add(Restrictions.lt(
					CmFinoFIX.CRPocketTemplate.FieldName_CreateTime,
					query.getEndDate()));
		}
		if (query.getDescriptionSearch() != null) {
			addLikeStartRestriction(criteria,
					CmFinoFIX.CRPocketTemplate.FieldName_Description,
					query.getDescriptionSearch());
		}
		if (query.getExactPocketDescription() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPocketTemplate.FieldName_Description,
					query.getExactPocketDescription()).ignoreCase());
		}
		if (query.getPocketType() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPocketTemplate.FieldName_PocketType,
					query.getPocketType()));
		}
		if (query.getCommodityType() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPocketTemplate.FieldName_Commodity,
					query.getCommodityType()));
		}
		if (query.getPocketCode() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPocketTemplate.FieldName_PocketCode,
					query.getPocketCode()).ignoreCase());
		}
		if (query.get_isCollectorPocket() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPocketTemplate.FieldName_IsCollectorPocket,
					query.get_isCollectorPocket()));
		}
		if (query.get_isSuspencePocket() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPocketTemplate.FieldName_IsSuspencePocket,
					query.get_isSuspencePocket()));
		}
		
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<PocketTemplate> results = criteria.list();

		return results;
	}

	public List<PocketTemplate> getByPocketType(Integer pocketType)
	{
		Criteria criteria = createCriteria();
		if(pocketType != null) 
		{
			criteria.add(Restrictions.eq(CmFinoFIX.CRPocketTemplate.FieldName_PocketType,pocketType));
		}
		
		@SuppressWarnings("unchecked")
		List<PocketTemplate> results = criteria.list();
		return results;
	}
	@Override
	public void save(PocketTemplate p) {
		// update information related to every action here
		if (p.getmFinoServiceProviderByMSPID() == null) {
			MfinoServiceProviderDAO mspDao = DAOFactory.getInstance()
					.getMfinoServiceProviderDAO();
			mFinoServiceProvider msp = mspDao.getById(1L);
			p.setmFinoServiceProviderByMSPID(msp);
		}

		super.save(p);
	}
}
