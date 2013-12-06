/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbli;

import com.mfino.mock.testharnessbliimpl.TestHarnessValueObject;

/**
 *
 * @author sunil
 */
public interface CallMfinoServerBLI {

    public void invokeMfinoServer(TestHarnessValueObject input) throws Exception;
    public void invokeMfinoServer_noVO(String outgoingUrl)throws Exception;

}
