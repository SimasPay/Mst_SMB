/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.domain.MoneyClearanceGraved;

/**
 * @author Sreenath
 *
 */
public interface MoneyClearanceGravedService {

	public List<MoneyClearanceGraved> getMoneyClearanceGravedByQuery(MoneyClearanceGravedQuery mcgQuery);
	
	public void saveMoneyClearanceGraved(MoneyClearanceGraved moneyClearanceGraved);
}
