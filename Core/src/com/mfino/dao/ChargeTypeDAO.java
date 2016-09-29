/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChargeTypeQuery;
import com.mfino.domain.ChargeType;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ChargeTypeDAO extends BaseDAO<ChargeType> {
	
	public List<ChargeType> get(ChargeTypeQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getExactName())) {
			criteria.add(Restrictions.eq(ChargeType.FieldName_Name, query.getExactName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getName())) {
			addLikeStartRestriction(criteria, ChargeType.FieldName_Name, query.getName());
		}
		if (query.getNotEqualId() != null) {
			criteria.add(Restrictions.ne(ChargeType.FieldName_RecordID, query.getNotEqualId()));
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(ChargeType.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(ChargeType.FieldName_CreateTime, query.getEndDate()));
		}		

		criteria.addOrder(Order.asc(ChargeType.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
			List<ChargeType> lst = criteria.list();
			
		return lst;
	}
	
	
	@SuppressWarnings("unchecked")
	public ChargeType getChargeTypeByName(String Name){
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(Name)) {
			criteria.add(Restrictions.eq(ChargeType.FieldName_Name, Name).ignoreCase());
		}else{
			return null;
		}
		List<ChargeType> lst = criteria.list();
		if(lst==null||lst.isEmpty()){
			return null;
		}
		return lst.get(0);
	}
	
    @Override
    public void save(ChargeType ct) {
        if (ct.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            ct.setMfinoServiceProvider(msp);
        }
        super.save(ct);
    }

}
