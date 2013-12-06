/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.PocketTemplate;

/**
 * @author Sreenath
 *
 */
public interface PocketTemplateService {
	/**
	 * Returns the pocketType of the pocket Template got by the pocket template id
	 * @param pocketTemplateID
	 * @return
	 */
    public Integer getPocketType(Long pocketTemplateID);

    /**
     * 
     * @param template1
     * @param template2
     * @return
     */
    public boolean areCompatible(PocketTemplate template1, PocketTemplate template2);

    /**
     * 
     * @param intA
     * @param intB
     * @return
     */
    public boolean equals(Integer intA, Integer intB);
    	
    /**
     * Gets the pocketTemplate by the given pocketTemplateID
     * @param pocketTemplateId
     * @return
     */
    public PocketTemplate getById(Long pocketTemplateId);

}
