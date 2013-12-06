/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author sandeepjs
 */
public class ConfigurationUtilTest {

	public ConfigurationUtilTest() {
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
		assertEquals("3", ConfigurationUtil
				.getMerchantDefaultPocketTemplateID());
	}

	@Test
	public void testGetTempDir() {
		System.out.println(ConfigurationUtil.getTempDir());
	}

	@Test
	public void testGenerateDefault() {
		for (ConfigurationUtil.ConfigurationKey key : ConfigurationUtil.ConfigurationKey
				.values()) {
			if (StringUtils.isNotEmpty(key.getDefaultValue())) {
				System.out.println(key.getKey() + "=" + key.getDefaultValue());
			} else {
				System.out.println("#" + key.getKey() + "="
						+ key.getDefaultValue());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void testMergeExistingConfigurationFiles() throws IOException{
		Properties p = new SortedProperties();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try{
			Iterator<File> fileIterator = FileUtils.iterateFiles(new File("settings"), new String[]{"properties"}, true );
			while(fileIterator.hasNext()){
				File currentFile = fileIterator.next();
				fis = FileUtils.openInputStream(currentFile);
				p.clear();
				p.load(fis);
				fis.close();
				
				for (ConfigurationUtil.ConfigurationKey key : ConfigurationUtil.ConfigurationKey.values()) 
				{
					if( p.containsKey(key.getKey())){
						continue;
					}
					
					p.put(key.getKey(), key.getDefaultValue() == null ? "" : key.getDefaultValue());
				}
				
				fos = FileUtils.openOutputStream(currentFile);
				p.store(fos, "Updated by property merge tool");
				fos.close();
			}
		}finally{
			if(fis != null){
				fis.close();
			}
			if(fos != null){
				fos.close();
			}
		}
	}
}