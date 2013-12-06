/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.User;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.service.CustomUserDetailsService;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class CustomUserDetailsServiceTest {

    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();

    @Test //if there no test in the test class, it is throwing an error
    public void testDummy() {
      }

    @Ignore //Should not run every time. Only run it when you want to.
    @Test
    public void loadUserData() {
        HibernateUtil.getCurrentSession().beginTransaction();
        addUser("mfino", "mfino123", CmFinoFIX.Role_System_Admin);
        addUser("user", "user123", CmFinoFIX.Role_Customer_Care);
        addUser("admin", "admin123", CmFinoFIX.Role_Customer_Care_Manager);
        HibernateUtil.getCurrentSession().getTransaction().commit();

        CustomUserDetailsService myService = new CustomUserDetailsService();
        UserDetails uDetails = myService.loadUserByUsername("mfino");
        Collection<GrantedAuthority> authorities = uDetails.getAuthorities();
        assert (authorities.size() > 0);
        Assert.assertNotNull(uDetails);
    }

    private void addUser(String username, String password, Integer role) {
        UserDAO service = new UserDAO();

        String encPassword;
        PasswordEncoder encoder = new ShaPasswordEncoder(1);
        encPassword = encoder.encodePassword(password, username);

        //User u = new User(username, encPassword, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        User u = new User();
        u.setUsername(username);
        u.setPassword(encPassword);
        //NOTE: instead of fetching the real record, you can create an empty record to
        //fool hibernate. But don't use update cascade, that could cause problem. 
        mFinoServiceProvider msp = new mFinoServiceProvider();
        msp.setID(1l);
        u.setmFinoServiceProviderByMSPID(msp);

        u.setRole(role);
        u.setCreateTime(new Timestamp());
        u.setStatusTime(new Timestamp());
        u.setFailedLoginCount(0);
        u.setRestrictions(0);
        u.setLastUpdateTime(new Timestamp());
        u.setTimezone("IST");
        u.setFirstName(username);
        u.setLastName(username);

        service.save(u);
    }
}
