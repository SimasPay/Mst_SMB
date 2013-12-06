/**
 * 
 */
package com.mfino.uicore.fix.processor;

import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.processor.IFixProcessor;

/**
 * @author Shashank
 *
 */
public interface MfinoServiceproviderProcessor extends IFixProcessor {

	public mFinoServiceProvider getById(long id);
}
