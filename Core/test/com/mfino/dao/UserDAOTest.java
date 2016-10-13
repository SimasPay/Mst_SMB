/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.junit.Test;

import com.mfino.domain.Company;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author xchen
 */
public class UserDAOTest extends TestCase {

    @Override
    public void setUp() {

    }

    @Override
    public void tearDown() {
    }

    @Test
    public void testInsert() {
        UserDAO service = new UserDAO();
        MfinoUser u = new MfinoUser();
        u.setUsername("xchen1234567890");
        u.setPassword("xchen");
        u.setStatustime(new Timestamp());
        u.setCreatetime(new Timestamp());
        u.setLastupdatetime(new Timestamp());
        u.setUpdatedby("xchen");
        u.setCreatedby("xchen");
        //make sure u have atleast one record in company table
        CompanyDAO dao = new CompanyDAO();
        List<Company> results = dao.getAll();
        if (results.size() > 0) {
            u.setCompany(results.get(0));
        }

        //Dummy MSP
        MfinoServiceProvider msp = new MfinoServiceProvider();
        msp.setId((long)0);
        u.setMfinoServiceProvider(msp);

     //   service.save(u);
//
//        UserAuthority auth = new UserAuthority();
//        auth.setAuthority("ROLE_ADMIN");
//        auth.setUser(u);
//        HashSet authSet = new HashSet();
//        authSet.add(auth);
//        u.setUserAuthorityFromUserID(authSet);
        u.setRole(CmFinoFIX.Role_Subscriber.longValue());
       service.save(u);

        assertTrue(u.getId().longValue() > 0);
        //assertTrue(auth.getID() > 0);
        //List<User> results = service.getAll();
       // assertTrue(results.size() > 0);        
    }

    @Test
    public void patterntest(){
        Pattern email = Pattern.compile("^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
        String mail ="RAJPP@PP.PP.PP.QQ";
        Matcher m = email.matcher(mail);
        boolean result = m.find();
        System.out.println(result);
        assertTrue(result);
    }
    
}
