/**
 * 
 */
package com.mfino.service;

import java.security.InvalidKeyException;

import com.mfino.domain.common.KeyTokenPair;
import com.mfino.exceptions.CoreException;

/**
 * @author Shashank
 *
 */
public interface TokenService {

	public boolean validateToken(KeyTokenPair keyTokenPair, String userName) throws CoreException, InvalidKeyException;
}
