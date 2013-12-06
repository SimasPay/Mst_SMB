/**
 * 
 */
package com.mfino.uicore.fix.processor;

import org.springframework.web.multipart.MultipartFile;

import com.mfino.fix.processor.IFixProcessor;

/**
 * @author Sreenath
 *
 */
public interface BulkUploadFileProcessor extends IFixProcessor {

	public void processFileData(MultipartFile file, int recordType,
			int linecount, String desc) throws Throwable;

}
