/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.BankAdmin;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author deva
 */
public class BankAdminDAO extends BaseDAO<BankAdmin>{
    public static final String USER_TABLE_NAME = "User";
    final String userTableNameAlias = USER_TABLE_NAME + DAOConstants.ALIAS_SUFFIX;
    public List<BankAdmin> get(UserQuery query) {
        Criteria criteria = createCriteria();
        criteria.createAlias(USER_TABLE_NAME, userTableNameAlias);
        if (query.getUserName() != null) {
                criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_Username, query.getUserName()).ignoreCase());
            }
         if (query.getFirstNameLike() != null) {
                criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_FirstName, query.getFirstNameLike()).ignoreCase());
            }
            if (query.getLastNameLike() != null) {
                criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_LastName, query.getLastNameLike()).ignoreCase());
            }
            if (query.getRole() != null) {
                criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_Role, query.getRole()));
            }
            if (query.getRestrictions() != null) {
            criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_UserRestrictions, query.getRestrictions()));
             }
            if (query.getStatus() != null) {
            criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_UserStatus, query.getStatus()));
            }
            if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(userTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_Company, query.getCompany()));
             }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BankAdmin> results = criteria.list();

        return results;        
    }

}
