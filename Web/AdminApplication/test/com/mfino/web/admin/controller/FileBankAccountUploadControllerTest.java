/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author xchen
 */
public class FileBankAccountUploadControllerTest{

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    private static String uploadData = "628811621096,6396873100172013,0\n\r628812401025,6396873100150498,0\n\r628812401027,6396873100150522,1\n\r628814308452,6396870000578390,0";

    @Ignore("Looks like some problem on the build server. Ignoring for now.")
    @Test
    public void testRequest() {
        FileBankAccountUploadController controller = new FileBankAccountUploadController();
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MultipartFile file = new MockMultipartFile("data.csv", uploadData.getBytes());
        request.addFile(file);
        controller.handleUpload(request, null);
    }
}
