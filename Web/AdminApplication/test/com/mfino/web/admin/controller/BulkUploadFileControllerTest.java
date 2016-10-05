/**
 * 
 */
package com.mfino.web.admin.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMdn;

/**
 * @author Deva
 * 
 */
public class BulkUploadFileControllerTest extends TestCase{
	
	private BulkUploadFileController controller = new BulkUploadFileController();
	
	private long mdn = 123456456L;
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

//	private static String uploadData = "0,${serviceType},${mdn},${imsi},VIP,SMART,${firstName},${lastName},raju@mfino.com,en_US,IDR,PREPAID,Initialized,,0,,,,,,";
	private static String uploadData = "6262626881,Merchant0,en_US,Central_Indonesia_Time,,IDR,,ACTIVE,341,Merchant E-Load,Hyderabad,Merchant0,Last0,,gen@mfino.com,TEST,,ABC,,Hyderabad,,,500032,,62124512,,1,1,www.mfino.com,,2010,,,,,,1,Rep0,,,,,";

	@Test
	public void testSubscriberRequest() throws FileNotFoundException, IOException {
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		request.setParameter("RecordType", "0");
//		MultipartFile file = new MockMultipartFile("data.csv", new FileInputStream("E:\\GenerateSubscribers1.csv"));
		MultipartFile file = new MockMultipartFile("data.csv", uploadData.getBytes());
		request.addFile(file);
//		testCreateNew(request);
	}
	// Update Field and check if it is working
	
	// Update MDN
	
	//Update
	private void testCreateNew(MockMultipartHttpServletRequest request) {
		controller.handleUpload(request, null);
		uploadData.replaceAll("{mdn}", String.valueOf(mdn));
		uploadData.replaceAll("{imsi}", String.valueOf(mdn));
		uploadData.replaceAll("{serviceType}", "1001");
		uploadData.replaceAll("{firstName}", "mfino");
		uploadData.replaceAll("{lastName}", "dev");
		long startTime = System.currentTimeMillis();
		controller.handleUpload(request, null);
		// Get subscriber and check if it is created
		SubscriberMDNDAO subscriberMDNDAO = new SubscriberMDNDAO();
		SubscriberMdn subscriberMDN = subscriberMDNDAO.getByMDN(String.valueOf(mdn));
		assertNotNull(subscriberMDN);
		assertEquals(String.valueOf(mdn), subscriberMDN.getMdn());
		System.out.println("Time taken = " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	@Test
	public void testMerchantRequest() throws Exception {
		BulkUploadFileController controller = new BulkUploadFileController();
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		request.setParameter("RecordType", "1");
//		MultipartFile file = new MockMultipartFile("data.csv", new FileInputStream("E:\\GenerateSubscribers1.csv"));
		MultipartFile file = new MockMultipartFile("data.csv", uploadData.getBytes());
		request.addFile(file);
		long startTime = System.currentTimeMillis();
		controller.handleUpload(request, null);
		System.out.println("Time taken = " + (System.currentTimeMillis() - startTime) + " ms");
	}
}
