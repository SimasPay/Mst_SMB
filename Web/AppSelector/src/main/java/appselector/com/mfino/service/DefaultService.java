package appselector.com.mfino.service;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;
/**
 * 
 * @author Shashank
 *
 */

@Service("DefaultService")
 public class DefaultService {

	public void unzipFile(String FilePath, String FileName)
	{
		byte[] buffer = new byte[1024];
		try{
	    	 String outputFolder = FilePath.substring(0,FilePath.lastIndexOf(FileName));
	     	//get the zip file content
	    	ZipInputStream zis =new ZipInputStream(new FileInputStream(FilePath));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	 
	    	while(ze!=null){
	     	   String fileName = ze.getName();
	     	   File newFile = new File(outputFolder + File.separator + fileName);
	           new File(newFile.getParent()).mkdirs();
	           FileOutputStream fos = new FileOutputStream(newFile);             
	           int len;
	           while ((len = zis.read(buffer)) > 0) {
	        	   fos.write(buffer, 0, len);
	           }
	           fos.close();   
	           ze = zis.getNextEntry();
	    	}
	        zis.closeEntry();
	    	zis.close();
	  
	    }catch(IOException ex){
	       ex.printStackTrace(); 
	    }
	   }     
}
