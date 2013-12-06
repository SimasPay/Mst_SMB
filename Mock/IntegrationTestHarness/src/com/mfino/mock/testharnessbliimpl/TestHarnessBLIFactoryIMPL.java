/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.testharnessbli.CallMfinoServerBLI;
import com.mfino.mock.testharnessbli.TestHarnessBLI;

/**
 *
 * @author sunil
 */
public class TestHarnessBLIFactoryIMPL {

     public enum HarnessType {
        Gemalto,
        UTK,
        MockBank
    }
    public enum MfinoServer {
        HttpServer,
        UTK,
        MockBank
    }

    public static TestHarnessBLI createTestHarnessBLI(HarnessType harnessType) {

        switch (harnessType) {
            case Gemalto:
                return new GemaltoTestHarnessBLIIMPL();
            case UTK:
                return null;
            case MockBank:
                return null;
        }
        throw new IllegalArgumentException("The harness type " + harnessType + " is not recognized.");
    }

    public static CallMfinoServerBLI getMfinoServerRequest(MfinoServer serverType) {
        switch (serverType) {
            case HttpServer:
                return new CallMfinoServerBLIImpl();
            case UTK:
                return null;
            case MockBank:
                return null;
        }
        throw new IllegalArgumentException("The harness type " + serverType + " is not recognized.");
    }

}
