/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.domain.AuthorizingPerson;
import com.mfino.domain.mFinoServiceProvider;

/**
 *
 * @author Maruthi
 */
public class AuthorizingPersonDAO extends BaseDAO<AuthorizingPerson> {

//	  @Override
    public void save(AuthorizingPerson s) {
        //FIXME : everyone save it as 1 for now
        if (s.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            s.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(s);
    }
    
    @Override
    public void saveWithoutFlush(AuthorizingPerson s){
        //FIXME : everyone save it as 1 for now
        if (s.getmFinoServiceProviderByMSPID() == null) {
            s.setmFinoServiceProviderByMSPID((mFinoServiceProvider) this.getSession().load(mFinoServiceProvider.class, 1l));
        }
        super.saveWithoutFlush(s);
    }
}
