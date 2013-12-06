
package com.mfino.datapushserver.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mfino.datapushserver.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RetireSubsResponse_QNAME = new QName("http://ws.datapushserver.mfino.com/", "retireSubsResponse");
    private final static QName _SuspendSubsResponse_QNAME = new QName("http://ws.datapushserver.mfino.com/", "suspendSubsResponse");
    private final static QName _RegisterNewSubs_QNAME = new QName("http://ws.datapushserver.mfino.com/", "registerNewSubs");
    private final static QName _RegisterNewSubsResponse_QNAME = new QName("http://ws.datapushserver.mfino.com/", "registerNewSubsResponse");
    private final static QName _RetireSubs_QNAME = new QName("http://ws.datapushserver.mfino.com/", "retireSubs");
    private final static QName _SuspendSubs_QNAME = new QName("http://ws.datapushserver.mfino.com/", "suspendSubs");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mfino.datapushserver.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RetireSubsResponse }
     * 
     */
    public RetireSubsResponse createRetireSubsResponse() {
        return new RetireSubsResponse();
    }

    /**
     * Create an instance of {@link SuspendSubs }
     * 
     */
    public SuspendSubs createSuspendSubs() {
        return new SuspendSubs();
    }

    /**
     * Create an instance of {@link RegisterNewSubs }
     * 
     */
    public RegisterNewSubs createRegisterNewSubs() {
        return new RegisterNewSubs();
    }

    /**
     * Create an instance of {@link RegisterNewSubsResponse }
     * 
     */
    public RegisterNewSubsResponse createRegisterNewSubsResponse() {
        return new RegisterNewSubsResponse();
    }

    /**
     * Create an instance of {@link SuspendSubsResponse }
     * 
     */
    public SuspendSubsResponse createSuspendSubsResponse() {
        return new SuspendSubsResponse();
    }

    /**
     * Create an instance of {@link RetireSubs }
     * 
     */
    public RetireSubs createRetireSubs() {
        return new RetireSubs();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetireSubsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.datapushserver.mfino.com/", name = "retireSubsResponse")
    public JAXBElement<RetireSubsResponse> createRetireSubsResponse(RetireSubsResponse value) {
        return new JAXBElement<RetireSubsResponse>(_RetireSubsResponse_QNAME, RetireSubsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SuspendSubsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.datapushserver.mfino.com/", name = "suspendSubsResponse")
    public JAXBElement<SuspendSubsResponse> createSuspendSubsResponse(SuspendSubsResponse value) {
        return new JAXBElement<SuspendSubsResponse>(_SuspendSubsResponse_QNAME, SuspendSubsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterNewSubs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.datapushserver.mfino.com/", name = "registerNewSubs")
    public JAXBElement<RegisterNewSubs> createRegisterNewSubs(RegisterNewSubs value) {
        return new JAXBElement<RegisterNewSubs>(_RegisterNewSubs_QNAME, RegisterNewSubs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterNewSubsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.datapushserver.mfino.com/", name = "registerNewSubsResponse")
    public JAXBElement<RegisterNewSubsResponse> createRegisterNewSubsResponse(RegisterNewSubsResponse value) {
        return new JAXBElement<RegisterNewSubsResponse>(_RegisterNewSubsResponse_QNAME, RegisterNewSubsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetireSubs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.datapushserver.mfino.com/", name = "retireSubs")
    public JAXBElement<RetireSubs> createRetireSubs(RetireSubs value) {
        return new JAXBElement<RetireSubs>(_RetireSubs_QNAME, RetireSubs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SuspendSubs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.datapushserver.mfino.com/", name = "suspendSubs")
    public JAXBElement<SuspendSubs> createSuspendSubs(SuspendSubs value) {
        return new JAXBElement<SuspendSubs>(_SuspendSubs_QNAME, SuspendSubs.class, null, value);
    }

}
