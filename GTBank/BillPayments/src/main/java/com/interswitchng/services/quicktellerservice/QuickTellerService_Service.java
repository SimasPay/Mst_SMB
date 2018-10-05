
/*
 * 
 */

package com.interswitchng.services.quicktellerservice;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.2.10
 * Mon Oct 01 16:52:08 WIB 2018
 * Generated source version: 2.2.10
 * 
 */


@WebServiceClient(name = "QuickTellerService", 
                  wsdlLocation = "file:/Users/kris/Documents/rnspace/Mst_SMB/GTBank/BillPayments/src/main/resources/wsdl/QuickTeller.wsdl",
                  targetNamespace = "http://services.interswitchng.com/quicktellerservice/") 
public class QuickTellerService_Service extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://services.interswitchng.com/quicktellerservice/", "QuickTellerService");
    public final static QName BasicHttpBindingQuickTellerService = new QName("http://services.interswitchng.com/quicktellerservice/", "BasicHttpBinding_QuickTellerService");
    static {
        URL url = null;
        try {
            url = new URL("file:/Users/kris/Documents/rnspace/Mst_SMB/GTBank/BillPayments/src/main/resources/wsdl/QuickTeller.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:/Users/kris/Documents/rnspace/Mst_SMB/GTBank/BillPayments/src/main/resources/wsdl/QuickTeller.wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public QuickTellerService_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public QuickTellerService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public QuickTellerService_Service() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public QuickTellerService_Service(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }
    public QuickTellerService_Service(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public QuickTellerService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns QuickTellerService
     */
    @WebEndpoint(name = "BasicHttpBinding_QuickTellerService")
    public QuickTellerService getBasicHttpBindingQuickTellerService() {
        return super.getPort(BasicHttpBindingQuickTellerService, QuickTellerService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns QuickTellerService
     */
    @WebEndpoint(name = "BasicHttpBinding_QuickTellerService")
    public QuickTellerService getBasicHttpBindingQuickTellerService(WebServiceFeature... features) {
        return super.getPort(BasicHttpBindingQuickTellerService, QuickTellerService.class, features);
    }

}
