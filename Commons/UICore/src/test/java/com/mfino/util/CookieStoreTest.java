/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import javax.servlet.http.Cookie;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.mfino.uicore.util.CookieStore;

/**
 *
 * @author sandeepjs
 */
public class CookieStoreTest {

    CookieStore store = new CookieStore();

    public CookieStoreTest() {
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

    @Test
    public void testGet() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie[]{new Cookie("username", "sandeep")});
        assertEquals("sandeep", store.get(request, "username"));

    }

    @Test
    public void testSet() {

        MockHttpServletResponse response = new MockHttpServletResponse();
        store.set(response, "username", "sandeep");
        Cookie cookie[] = response.getCookies();
        assertEquals(cookie[0].getName(), "username");
        assertEquals(cookie[0].getValue(), "sandeep");
    }
}
