/**
 * 
 */
package com.mfino.commons.rulesservice;

import java.util.List;

/**
 * Rules Service provides interface to execute rules corresponding to a feature.
 * 
 * For example, execute rules corresponding to Service Charges to decide the service charge applicable.
 * 
 * @author Chaitanya
 *
 */
public interface RulesService {

	public <E> List<E> execute(String featureName, List<E> objects);
	
	
}
