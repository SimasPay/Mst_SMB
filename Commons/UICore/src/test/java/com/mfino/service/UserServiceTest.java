/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mfino.dao.RolePermissionDAO;
import com.mfino.domain.RolePermission;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.impl.UserServiceImpl;

/**
 *
 * @author sandeepjs
 */
public class UserServiceTest{

    UserServiceImpl userService = new UserServiceImpl();
    RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    static final Integer ROLE = CmFinoFIX.Role_Subscriber;
    static final Integer PERMISSION = 1;

    public UserServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private void insertData() {

        RolePermission rp = new RolePermission();
        rp.setRole(ROLE);
        rp.setPermission(PERMISSION);
        rp.setCreatetime(new Timestamp());
        rp.setCreatedby("test");
        rp.setLastupdatetime(new Timestamp());
        rp.setUpdatedby("test");


        rolePermissionDAO.save(rp);


    }

    private static Boolean invokeStaticMethod(Class targetClass,
            String methodName, Class[] argClasses, Object[] argObjects)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Method method = targetClass.getDeclaredMethod(methodName, argClasses);
        method.setAccessible(true);
        return (Boolean) method.invoke(null, argObjects);

    }


    @Ignore("Not yet ready")
    @Test
    public void testisAuthorized() {

        /*insertData();

        Boolean result = false;
        try {
            Class[] argClasses = {Integer.class};
            Object[] argObjects = {PERMISSION};
            result = invokeStaticMethod(Authorization.class, "isAuthorized", argClasses, argObjects);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(Boolean.TRUE, result);*/

    }
}
