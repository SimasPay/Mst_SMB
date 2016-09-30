/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.domain.AuthPersonDetails;
import com.mfino.domain.MfinoServiceProvider;

/**
 *
 * @author Maruthi
 */
public class AuthorizingPersonDAO extends BaseDAO<AuthPersonDetails> {

//	  @Override
    public void save(AuthPersonDetails s) {
        //FIXME : everyone save it as 1 for now
        if (s.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            s.setMfinoServiceProvider(msp);
        }
        super.save(s);
    }
    
    @Override
    public void saveWithoutFlush(AuthPersonDetails s){
        //FIXME : everyone save it as 1 for now
        if (s.getMfinoServiceProvider() == null) {
            s.setMfinoServiceProvider((MfinoServiceProvider) this.getSession().load(MfinoServiceProvider.class, 1l));
        }
        super.saveWithoutFlush(s);
    }
}
