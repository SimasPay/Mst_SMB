/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.application;

import org.junit.Ignore;

/**
 *
 * @author sandeepjs
 */
@Ignore 
//the fake ftp server cannot run on linux server because of security issue. it needs to bind to a port number bigger
//than 2000 instead of 21. 
public class BulkRemittanceExportToolTest {

 /*   static FakeFtpServer fakeFtpServer = new FakeFtpServer();

    public BulkRemittanceExportToolTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        fakeFtpServer.setServerControlPort(21);
        fakeFtpServer.addUserAccount(new UserAccount("mfino", "mfino", "c:"));

        FileSystem fileSystem = new WindowsFakeFileSystem();
        fileSystem.add(new DirectoryEntry("c:\\data"));
        fileSystem.add(new FileEntry("c:\\data\\file1.txt", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("c:\\data\\run.exe"));
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.start();

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        fakeFtpServer.stop();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testUpload() throws Exception {
        BulkRemittanceExportTool.getFTPClient();
        BulkUpload mockBulkUpload = new BulkUpload();
        mockBulkUpload.setOutFileData("File Data .....");
        mockBulkUpload.setOutFileName("TestFileName");
        BulkRemittanceExportTool.doUpload(mockBulkUpload);

        System.out.println(fakeFtpServer.getFileSystem().listFiles("c:\\data"));
    }

    @Test
    public void testExport() throws Exception {
        BulkRemittanceExportTool.startExport();
        System.out.println(fakeFtpServer.getFileSystem().listFiles("c:\\data"));
    }*/
}
