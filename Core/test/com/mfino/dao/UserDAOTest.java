/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.domain.Company;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Test;

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
        u.setStatusTime(new Timestamp());
        u.setCreateTime(new Timestamp());
        u.setLastUpdateTime(new Timestamp());
        u.setUpdatedBy("xchen");
        u.setCreatedBy("xchen");
        //make sure u have atleast one record in company table
        CompanyDAO dao = new CompanyDAO();
        List<Company> results = dao.getAll();
        if (results.size() > 0) {
            u.setCompany(results.get(0));
        }

        //Dummy MSP
        mFinoServiceProvider msp = new mFinoServiceProvider();
        msp.setID(1l);
        u.setmFinoServiceProviderByMSPID(msp);

     //   service.save(u);
//
//        UserAuthority auth = new UserAuthority();
//        auth.setAuthority("ROLE_ADMIN");
//        auth.setUser(u);
//        HashSet authSet = new HashSet();
//        authSet.add(auth);
//        u.setUserAuthorityFromUserID(authSet);
        u.setRole(CmFinoFIX.Role_Subscriber);
       service.save(u);

        assertTrue(u.getID() > 0);
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
