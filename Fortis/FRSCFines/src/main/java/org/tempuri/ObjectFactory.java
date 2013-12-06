
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfBulkCaptureResult;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfBulkOffenders;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfOffenderOffences;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfSynchDates;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTOffenders;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tempuri package. 
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

    private final static QName _BulkCaptureOffendersInfo_QNAME = new QName("http://tempuri.org/", "offendersInfo");
    private final static QName _BulkCaptureDeviceID_QNAME = new QName("http://tempuri.org/", "DeviceID");
    private final static QName _BulkCaptureResponseBulkCaptureResult_QNAME = new QName("http://tempuri.org/", "BulkCaptureResult");
    private final static QName _SystemSettingsResponseSystemSettingsResult_QNAME = new QName("http://tempuri.org/", "SystemSettingsResult");
    private final static QName _SystemSettingsSynchDates_QNAME = new QName("http://tempuri.org/", "SynchDates");
    private final static QName _ClientActivationDeviceName_QNAME = new QName("http://tempuri.org/", "DeviceName");
    private final static QName _ClientActivationCommandCode_QNAME = new QName("http://tempuri.org/", "CommandCode");
    private final static QName _AcceptPaymentTicketNo_QNAME = new QName("http://tempuri.org/", "TicketNo");
    private final static QName _AcceptPaymentTransactionRef_QNAME = new QName("http://tempuri.org/", "TransactionRef");
    private final static QName _AcceptPaymentPaymentTime_QNAME = new QName("http://tempuri.org/", "PaymentTime");
    private final static QName _AcceptPaymentOffendersName_QNAME = new QName("http://tempuri.org/", "OffendersName");
    private final static QName _AcceptPaymentPaymentDate_QNAME = new QName("http://tempuri.org/", "PaymentDate");
    private final static QName _AcceptPaymentToken_QNAME = new QName("http://tempuri.org/", "Token");
    private final static QName _AcceptPaymentAmountPaid_QNAME = new QName("http://tempuri.org/", "AmountPaid");
    private final static QName _SingleCaptureOffender_QNAME = new QName("http://tempuri.org/", "offender");
    private final static QName _SingleCaptureOffences_QNAME = new QName("http://tempuri.org/", "offences");
    private final static QName _ClientActivationResponseClientActivationResult_QNAME = new QName("http://tempuri.org/", "ClientActivationResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SystemSettingsResponse }
     * 
     */
    public SystemSettingsResponse createSystemSettingsResponse() {
        return new SystemSettingsResponse();
    }

    /**
     * Create an instance of {@link BulkCaptureResponse }
     * 
     */
    public BulkCaptureResponse createBulkCaptureResponse() {
        return new BulkCaptureResponse();
    }

    /**
     * Create an instance of {@link AcceptPaymentResponse }
     * 
     */
    public AcceptPaymentResponse createAcceptPaymentResponse() {
        return new AcceptPaymentResponse();
    }

    /**
     * Create an instance of {@link ClientActivation }
     * 
     */
    public ClientActivation createClientActivation() {
        return new ClientActivation();
    }

    /**
     * Create an instance of {@link SingleCapture }
     * 
     */
    public SingleCapture createSingleCapture() {
        return new SingleCapture();
    }

    /**
     * Create an instance of {@link ClientActivationResponse }
     * 
     */
    public ClientActivationResponse createClientActivationResponse() {
        return new ClientActivationResponse();
    }

    /**
     * Create an instance of {@link SingleCaptureResponse }
     * 
     */
    public SingleCaptureResponse createSingleCaptureResponse() {
        return new SingleCaptureResponse();
    }

    /**
     * Create an instance of {@link BulkCapture }
     * 
     */
    public BulkCapture createBulkCapture() {
        return new BulkCapture();
    }

    /**
     * Create an instance of {@link org.tempuri.SystemSettings }
     * 
     */
    public org.tempuri.SystemSettings createSystemSettings() {
        return new org.tempuri.SystemSettings();
    }

    /**
     * Create an instance of {@link AcceptPayment }
     * 
     */
    public AcceptPayment createAcceptPayment() {
        return new AcceptPayment();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfBulkOffenders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "offendersInfo", scope = BulkCapture.class)
    public JAXBElement<ArrayOfBulkOffenders> createBulkCaptureOffendersInfo(ArrayOfBulkOffenders value) {
        return new JAXBElement<ArrayOfBulkOffenders>(_BulkCaptureOffendersInfo_QNAME, ArrayOfBulkOffenders.class, BulkCapture.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DeviceID", scope = BulkCapture.class)
    public JAXBElement<String> createBulkCaptureDeviceID(String value) {
        return new JAXBElement<String>(_BulkCaptureDeviceID_QNAME, String.class, BulkCapture.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfBulkCaptureResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "BulkCaptureResult", scope = BulkCaptureResponse.class)
    public JAXBElement<ArrayOfBulkCaptureResult> createBulkCaptureResponseBulkCaptureResult(ArrayOfBulkCaptureResult value) {
        return new JAXBElement<ArrayOfBulkCaptureResult>(_BulkCaptureResponseBulkCaptureResult_QNAME, ArrayOfBulkCaptureResult.class, BulkCaptureResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link org.datacontract.schemas._2004._07.paymentlibrary.SystemSettings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SystemSettingsResult", scope = SystemSettingsResponse.class)
    public JAXBElement<org.datacontract.schemas._2004._07.paymentlibrary.SystemSettings> createSystemSettingsResponseSystemSettingsResult(org.datacontract.schemas._2004._07.paymentlibrary.SystemSettings value) {
        return new JAXBElement<org.datacontract.schemas._2004._07.paymentlibrary.SystemSettings>(_SystemSettingsResponseSystemSettingsResult_QNAME, org.datacontract.schemas._2004._07.paymentlibrary.SystemSettings.class, SystemSettingsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DeviceID", scope = org.tempuri.SystemSettings.class)
    public JAXBElement<String> createSystemSettingsDeviceID(String value) {
        return new JAXBElement<String>(_BulkCaptureDeviceID_QNAME, String.class, org.tempuri.SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfSynchDates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SynchDates", scope = org.tempuri.SystemSettings.class)
    public JAXBElement<ArrayOfSynchDates> createSystemSettingsSynchDates(ArrayOfSynchDates value) {
        return new JAXBElement<ArrayOfSynchDates>(_SystemSettingsSynchDates_QNAME, ArrayOfSynchDates.class, org.tempuri.SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DeviceID", scope = ClientActivation.class)
    public JAXBElement<String> createClientActivationDeviceID(String value) {
        return new JAXBElement<String>(_BulkCaptureDeviceID_QNAME, String.class, ClientActivation.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DeviceName", scope = ClientActivation.class)
    public JAXBElement<String> createClientActivationDeviceName(String value) {
        return new JAXBElement<String>(_ClientActivationDeviceName_QNAME, String.class, ClientActivation.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CommandCode", scope = ClientActivation.class)
    public JAXBElement<String> createClientActivationCommandCode(String value) {
        return new JAXBElement<String>(_ClientActivationCommandCode_QNAME, String.class, ClientActivation.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "TicketNo", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentTicketNo(String value) {
        return new JAXBElement<String>(_AcceptPaymentTicketNo_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "TransactionRef", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentTransactionRef(String value) {
        return new JAXBElement<String>(_AcceptPaymentTransactionRef_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "PaymentTime", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentPaymentTime(String value) {
        return new JAXBElement<String>(_AcceptPaymentPaymentTime_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "OffendersName", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentOffendersName(String value) {
        return new JAXBElement<String>(_AcceptPaymentOffendersName_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "PaymentDate", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentPaymentDate(String value) {
        return new JAXBElement<String>(_AcceptPaymentPaymentDate_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Token", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentToken(String value) {
        return new JAXBElement<String>(_AcceptPaymentToken_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "AmountPaid", scope = AcceptPayment.class)
    public JAXBElement<String> createAcceptPaymentAmountPaid(String value) {
        return new JAXBElement<String>(_AcceptPaymentAmountPaid_QNAME, String.class, AcceptPayment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTOffenders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "offender", scope = SingleCapture.class)
    public JAXBElement<FLTOffenders> createSingleCaptureOffender(FLTOffenders value) {
        return new JAXBElement<FLTOffenders>(_SingleCaptureOffender_QNAME, FLTOffenders.class, SingleCapture.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DeviceID", scope = SingleCapture.class)
    public JAXBElement<String> createSingleCaptureDeviceID(String value) {
        return new JAXBElement<String>(_BulkCaptureDeviceID_QNAME, String.class, SingleCapture.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfOffenderOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "offences", scope = SingleCapture.class)
    public JAXBElement<ArrayOfOffenderOffences> createSingleCaptureOffences(ArrayOfOffenderOffences value) {
        return new JAXBElement<ArrayOfOffenderOffences>(_SingleCaptureOffences_QNAME, ArrayOfOffenderOffences.class, SingleCapture.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "ClientActivationResult", scope = ClientActivationResponse.class)
    public JAXBElement<String> createClientActivationResponseClientActivationResult(String value) {
        return new JAXBElement<String>(_ClientActivationResponseClientActivationResult_QNAME, String.class, ClientActivationResponse.class, value);
    }

}
