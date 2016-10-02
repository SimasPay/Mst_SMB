/**
 * 
 */
package com.mfino.uicore.fix.processor;

import com.mfino.domain.MfinoServiceProvider;
import com.mfino.fix.processor.IFixProcessor;

/**
 * @author Shashank
 *
 */
public interface MfinoServiceproviderProcessor extends IFixProcessor {

	public MfinoServiceProvider getById(long id);
}
