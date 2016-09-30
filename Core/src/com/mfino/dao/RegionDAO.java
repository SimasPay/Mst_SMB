/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RegionQuery;
import com.mfino.domain.Region;

/**
 *
 * @author Raju
 */
public class RegionDAO extends BaseDAO<Region> {

    public static final String ID_COLNAME = "ID";
    public static final String NAME_COLNAME = Region.FieldName_RegionCode;

    public List<Region> get(RegionQuery query) {

        Criteria criteria = createCriteria();
        if (query.getRegionID() != null) {
            criteria.add(Restrictions.eq(ID_COLNAME, query.getRegionID()));
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(Region.FieldName_Company, query.getCompany()));
        }
        if (query.getRegionName() != null) {
            criteria.add(Restrictions.like(Region.FieldName_RegionName, query.getRegionName()).ignoreCase());
        }
        if (query.getRegionCode() != null) {
            criteria.add(Restrictions.like(Region.FieldName_RegionCode, query.getRegionCode()).ignoreCase());
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<Region> results = criteria.list();

        return results;
    }

    public List<Region> getByCode(String name) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(NAME_COLNAME, name));
    	@SuppressWarnings("unchecked")
        List<Region> results = criteria.list();
        return results;

    }

    @Override
    public void save(Region reg) {
        super.save(reg);
    }
}
