
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.paymentlibrary package. 
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

    private final static QName _FLTOffendersOffences_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FLT_Offenders_Offences");
    private final static QName _ArrayOfBulkCaptureResult_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfBulkCaptureResult");
    private final static QName _ArrayOfSynchDates_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfSynchDates");
    private final static QName _FLTUsers_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FLT_Users");
    private final static QName _FLTOffenders_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FLT_Offenders");
    private final static QName _SynchDates_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "SynchDates");
    private final static QName _BulkOffenders_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "BulkOffenders");
    private final static QName _FLTOffences_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FLT_Offences");
    private final static QName _ArrayOfOffenderOffences_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfOffenderOffences");
    private final static QName _FLTRoutes_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FLT_Routes");
    private final static QName _SystemSettings_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "SystemSettings");
    private final static QName _ItemType_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ItemType");
    private final static QName _BulkCaptureResult_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "BulkCaptureResult");
    private final static QName _ArrayOfFLTRoutes_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfFLT_Routes");
    private final static QName _FLTScripts_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FLT_Scripts");
    private final static QName _OffenderOffences_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OffenderOffences");
    private final static QName _ArrayOfFLTUsers_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfFLT_Users");
    private final static QName _ArrayOfFLTScripts_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfFLT_Scripts");
    private final static QName _ArrayOfBulkOffenders_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfBulkOffenders");
    private final static QName _ArrayOfFLTOffences_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "ArrayOfFLT_Offences");
    private final static QName _FLTScriptsType_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Type");
    private final static QName _FLTScriptsInsertionDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "InsertionDate");
    private final static QName _FLTScriptsDescription_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Description");
    private final static QName _FLTScriptsScript_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Script");
    private final static QName _OffenderOffencesOffenceCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OffenceCode");
    private final static QName _FLTOffendersLicenceIssueDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "LicenceIssueDate");
    private final static QName _FLTOffendersStateID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "StateID");
    private final static QName _FLTOffendersCFDriversLicence_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_DriversLicence");
    private final static QName _FLTOffendersPushedTime_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "PushedTime");
    private final static QName _FLTOffendersPenalty_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Penalty");
    private final static QName _FLTOffendersStreetAddress_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "StreetAddress");
    private final static QName _FLTOffendersCFInsurance_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_Insurance");
    private final static QName _FLTOffendersSurname_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Surname");
    private final static QName _FLTOffendersCFVehicle_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_Vehicle");
    private final static QName _FLTOffendersCity_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "City");
    private final static QName _FLTOffendersAgeGroup_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "AgeGroup");
    private final static QName _FLTOffendersCFVehicleLicence_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_VehicleLicence");
    private final static QName _FLTOffendersLocation_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Location");
    private final static QName _FLTOffendersPOSTxnID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "POSTxnID");
    private final static QName _FLTOffendersCFVehicleKeys_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_VehicleKeys");
    private final static QName _FLTOffendersCFHackney_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_Hackney");
    private final static QName _FLTOffendersPIN_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "PIN");
    private final static QName _FLTOffendersOffenceTime_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OffenceTime");
    private final static QName _FLTOffendersOtherName_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OtherName");
    private final static QName _FLTOffendersFirstName_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "FirstName");
    private final static QName _FLTOffendersWanted_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Wanted");
    private final static QName _FLTOffendersDeviceID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "DeviceID");
    private final static QName _FLTOffendersIsWarning_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "IsWarning");
    private final static QName _FLTOffendersOffenceDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OffenceDate");
    private final static QName _FLTOffendersLicenceExpiryDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "LicenceExpiryDate");
    private final static QName _FLTOffendersPaidStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "PaidStatus");
    private final static QName _FLTOffendersPOSTerminalID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "POSTerminalID");
    private final static QName _FLTOffendersCFPOC_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_POC");
    private final static QName _FLTOffendersCFOthers_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_Others");
    private final static QName _FLTOffendersGPSLatitude_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "GPSLatitude");
    private final static QName _FLTOffendersCFPassport_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_Passport");
    private final static QName _FLTOffendersCFIDCard_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_IDCard");
    private final static QName _FLTOffendersPhoneNumber_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "PhoneNumber");
    private final static QName _FLTOffendersVehicleRegNo_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "VehicleRegNo");
    private final static QName _FLTOffendersCFWayBill_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_WayBill");
    private final static QName _FLTOffendersMakenModel_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "MakenModel");
    private final static QName _FLTOffendersTicketNo_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "TicketNo");
    private final static QName _FLTOffendersDriversLicenceNo_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "DriversLicenceNo");
    private final static QName _FLTOffendersPushedDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "PushedDate");
    private final static QName _FLTOffendersRoute_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Route");
    private final static QName _FLTOffendersVehicleColor_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "VehicleColor");
    private final static QName _FLTOffendersVehicleTypeID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "VehicleTypeID");
    private final static QName _FLTOffendersGPSLongitude_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "GPSLongitude");
    private final static QName _FLTOffendersVehicleUseID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "VehicleUseID");
    private final static QName _FLTOffendersDOB_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "DOB");
    private final static QName _FLTOffendersCFStateCarriage_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_StateCarriage");
    private final static QName _FLTOffendersPOSTxnFee_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "POSTxnFee");
    private final static QName _FLTOffendersCFRoadWorthiness_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CF_RoadWorthiness");
    private final static QName _FLTOffendersQualification_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Qualification");
    private final static QName _FLTOffendersPaymentDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "PaymentDate");
    private final static QName _BulkOffendersOffender_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Offender");
    private final static QName _BulkOffendersOffences_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Offences");
    private final static QName _SynchDatesLastSyncDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "LastSyncDate");
    private final static QName _SystemSettingsDemurrage_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Demurrage");
    private final static QName _SystemSettingsUsers_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Users");
    private final static QName _SystemSettingsRoutes_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Routes");
    private final static QName _SystemSettingsScripts_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Scripts");
    private final static QName _FLTRoutesLastActivityDate_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "LastActivityDate");
    private final static QName _FLTRoutesStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Status");
    private final static QName _FLTRoutesCommandCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "CommandCode");
    private final static QName _FLTRoutesRouteCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "RouteCode");
    private final static QName _FLTRoutesRouteName_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "RouteName");
    private final static QName _BulkCaptureResultTicketNumber_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "TicketNumber");
    private final static QName _FLTUsersLastName_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "LastName");
    private final static QName _FLTUsersUserName_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "UserName");
    private final static QName _FLTOffendersOffencesOffenderID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OffenderID");
    private final static QName _FLTOffendersOffencesPoint_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Point");
    private final static QName _FLTOffendersOffencesOffenceID_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "OffenceID");
    private final static QName _FLTOffencesCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/PaymentLibrary", "Code");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.paymentlibrary
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayOfBulkOffenders }
     * 
     */
    public ArrayOfBulkOffenders createArrayOfBulkOffenders() {
        return new ArrayOfBulkOffenders();
    }

    /**
     * Create an instance of {@link ArrayOfFLTScripts }
     * 
     */
    public ArrayOfFLTScripts createArrayOfFLTScripts() {
        return new ArrayOfFLTScripts();
    }

    /**
     * Create an instance of {@link ArrayOfSynchDates }
     * 
     */
    public ArrayOfSynchDates createArrayOfSynchDates() {
        return new ArrayOfSynchDates();
    }

    /**
     * Create an instance of {@link SynchDates }
     * 
     */
    public SynchDates createSynchDates() {
        return new SynchDates();
    }

    /**
     * Create an instance of {@link FLTRoutes }
     * 
     */
    public FLTRoutes createFLTRoutes() {
        return new FLTRoutes();
    }

    /**
     * Create an instance of {@link BulkCaptureResult }
     * 
     */
    public BulkCaptureResult createBulkCaptureResult() {
        return new BulkCaptureResult();
    }

    /**
     * Create an instance of {@link FLTUsers }
     * 
     */
    public FLTUsers createFLTUsers() {
        return new FLTUsers();
    }

    /**
     * Create an instance of {@link ArrayOfBulkCaptureResult }
     * 
     */
    public ArrayOfBulkCaptureResult createArrayOfBulkCaptureResult() {
        return new ArrayOfBulkCaptureResult();
    }

    /**
     * Create an instance of {@link FLTOffences }
     * 
     */
    public FLTOffences createFLTOffences() {
        return new FLTOffences();
    }

    /**
     * Create an instance of {@link FLTScripts }
     * 
     */
    public FLTScripts createFLTScripts() {
        return new FLTScripts();
    }

    /**
     * Create an instance of {@link OffenderOffences }
     * 
     */
    public OffenderOffences createOffenderOffences() {
        return new OffenderOffences();
    }

    /**
     * Create an instance of {@link FLTOffenders }
     * 
     */
    public FLTOffenders createFLTOffenders() {
        return new FLTOffenders();
    }

    /**
     * Create an instance of {@link BulkOffenders }
     * 
     */
    public BulkOffenders createBulkOffenders() {
        return new BulkOffenders();
    }

    /**
     * Create an instance of {@link SystemSettings }
     * 
     */
    public SystemSettings createSystemSettings() {
        return new SystemSettings();
    }

    /**
     * Create an instance of {@link ArrayOfFLTRoutes }
     * 
     */
    public ArrayOfFLTRoutes createArrayOfFLTRoutes() {
        return new ArrayOfFLTRoutes();
    }

    /**
     * Create an instance of {@link ArrayOfFLTOffences }
     * 
     */
    public ArrayOfFLTOffences createArrayOfFLTOffences() {
        return new ArrayOfFLTOffences();
    }

    /**
     * Create an instance of {@link ArrayOfFLTUsers }
     * 
     */
    public ArrayOfFLTUsers createArrayOfFLTUsers() {
        return new ArrayOfFLTUsers();
    }

    /**
     * Create an instance of {@link ArrayOfOffenderOffences }
     * 
     */
    public ArrayOfOffenderOffences createArrayOfOffenderOffences() {
        return new ArrayOfOffenderOffences();
    }

    /**
     * Create an instance of {@link FLTOffendersOffences }
     * 
     */
    public FLTOffendersOffences createFLTOffendersOffences() {
        return new FLTOffendersOffences();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTOffendersOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FLT_Offenders_Offences")
    public JAXBElement<FLTOffendersOffences> createFLTOffendersOffences(FLTOffendersOffences value) {
        return new JAXBElement<FLTOffendersOffences>(_FLTOffendersOffences_QNAME, FLTOffendersOffences.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfBulkCaptureResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfBulkCaptureResult")
    public JAXBElement<ArrayOfBulkCaptureResult> createArrayOfBulkCaptureResult(ArrayOfBulkCaptureResult value) {
        return new JAXBElement<ArrayOfBulkCaptureResult>(_ArrayOfBulkCaptureResult_QNAME, ArrayOfBulkCaptureResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfSynchDates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfSynchDates")
    public JAXBElement<ArrayOfSynchDates> createArrayOfSynchDates(ArrayOfSynchDates value) {
        return new JAXBElement<ArrayOfSynchDates>(_ArrayOfSynchDates_QNAME, ArrayOfSynchDates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTUsers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FLT_Users")
    public JAXBElement<FLTUsers> createFLTUsers(FLTUsers value) {
        return new JAXBElement<FLTUsers>(_FLTUsers_QNAME, FLTUsers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTOffenders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FLT_Offenders")
    public JAXBElement<FLTOffenders> createFLTOffenders(FLTOffenders value) {
        return new JAXBElement<FLTOffenders>(_FLTOffenders_QNAME, FLTOffenders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SynchDates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "SynchDates")
    public JAXBElement<SynchDates> createSynchDates(SynchDates value) {
        return new JAXBElement<SynchDates>(_SynchDates_QNAME, SynchDates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkOffenders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "BulkOffenders")
    public JAXBElement<BulkOffenders> createBulkOffenders(BulkOffenders value) {
        return new JAXBElement<BulkOffenders>(_BulkOffenders_QNAME, BulkOffenders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FLT_Offences")
    public JAXBElement<FLTOffences> createFLTOffences(FLTOffences value) {
        return new JAXBElement<FLTOffences>(_FLTOffences_QNAME, FLTOffences.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfOffenderOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfOffenderOffences")
    public JAXBElement<ArrayOfOffenderOffences> createArrayOfOffenderOffences(ArrayOfOffenderOffences value) {
        return new JAXBElement<ArrayOfOffenderOffences>(_ArrayOfOffenderOffences_QNAME, ArrayOfOffenderOffences.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTRoutes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FLT_Routes")
    public JAXBElement<FLTRoutes> createFLTRoutes(FLTRoutes value) {
        return new JAXBElement<FLTRoutes>(_FLTRoutes_QNAME, FLTRoutes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SystemSettings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "SystemSettings")
    public JAXBElement<SystemSettings> createSystemSettings(SystemSettings value) {
        return new JAXBElement<SystemSettings>(_SystemSettings_QNAME, SystemSettings.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ItemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ItemType")
    public JAXBElement<ItemType> createItemType(ItemType value) {
        return new JAXBElement<ItemType>(_ItemType_QNAME, ItemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkCaptureResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "BulkCaptureResult")
    public JAXBElement<BulkCaptureResult> createBulkCaptureResult(BulkCaptureResult value) {
        return new JAXBElement<BulkCaptureResult>(_BulkCaptureResult_QNAME, BulkCaptureResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTRoutes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfFLT_Routes")
    public JAXBElement<ArrayOfFLTRoutes> createArrayOfFLTRoutes(ArrayOfFLTRoutes value) {
        return new JAXBElement<ArrayOfFLTRoutes>(_ArrayOfFLTRoutes_QNAME, ArrayOfFLTRoutes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTScripts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FLT_Scripts")
    public JAXBElement<FLTScripts> createFLTScripts(FLTScripts value) {
        return new JAXBElement<FLTScripts>(_FLTScripts_QNAME, FLTScripts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OffenderOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OffenderOffences")
    public JAXBElement<OffenderOffences> createOffenderOffences(OffenderOffences value) {
        return new JAXBElement<OffenderOffences>(_OffenderOffences_QNAME, OffenderOffences.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTUsers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfFLT_Users")
    public JAXBElement<ArrayOfFLTUsers> createArrayOfFLTUsers(ArrayOfFLTUsers value) {
        return new JAXBElement<ArrayOfFLTUsers>(_ArrayOfFLTUsers_QNAME, ArrayOfFLTUsers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTScripts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfFLT_Scripts")
    public JAXBElement<ArrayOfFLTScripts> createArrayOfFLTScripts(ArrayOfFLTScripts value) {
        return new JAXBElement<ArrayOfFLTScripts>(_ArrayOfFLTScripts_QNAME, ArrayOfFLTScripts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfBulkOffenders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfBulkOffenders")
    public JAXBElement<ArrayOfBulkOffenders> createArrayOfBulkOffenders(ArrayOfBulkOffenders value) {
        return new JAXBElement<ArrayOfBulkOffenders>(_ArrayOfBulkOffenders_QNAME, ArrayOfBulkOffenders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "ArrayOfFLT_Offences")
    public JAXBElement<ArrayOfFLTOffences> createArrayOfFLTOffences(ArrayOfFLTOffences value) {
        return new JAXBElement<ArrayOfFLTOffences>(_ArrayOfFLTOffences_QNAME, ArrayOfFLTOffences.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Type", scope = FLTScripts.class)
    public JAXBElement<Integer> createFLTScriptsType(Integer value) {
        return new JAXBElement<Integer>(_FLTScriptsType_QNAME, Integer.class, FLTScripts.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "InsertionDate", scope = FLTScripts.class)
    public JAXBElement<XMLGregorianCalendar> createFLTScriptsInsertionDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_FLTScriptsInsertionDate_QNAME, XMLGregorianCalendar.class, FLTScripts.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Description", scope = FLTScripts.class)
    public JAXBElement<String> createFLTScriptsDescription(String value) {
        return new JAXBElement<String>(_FLTScriptsDescription_QNAME, String.class, FLTScripts.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Script", scope = FLTScripts.class)
    public JAXBElement<String> createFLTScriptsScript(String value) {
        return new JAXBElement<String>(_FLTScriptsScript_QNAME, String.class, FLTScripts.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OffenceCode", scope = OffenderOffences.class)
    public JAXBElement<String> createOffenderOffencesOffenceCode(String value) {
        return new JAXBElement<String>(_OffenderOffencesOffenceCode_QNAME, String.class, OffenderOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LicenceIssueDate", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersLicenceIssueDate(String value) {
        return new JAXBElement<String>(_FLTOffendersLicenceIssueDate_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "StateID", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersStateID(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersStateID_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_DriversLicence", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFDriversLicence(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFDriversLicence_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PushedTime", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPushedTime(String value) {
        return new JAXBElement<String>(_FLTOffendersPushedTime_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Penalty", scope = FLTOffenders.class)
    public JAXBElement<BigDecimal> createFLTOffendersPenalty(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_FLTOffendersPenalty_QNAME, BigDecimal.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "StreetAddress", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersStreetAddress(String value) {
        return new JAXBElement<String>(_FLTOffendersStreetAddress_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_Insurance", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFInsurance(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFInsurance_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Surname", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersSurname(String value) {
        return new JAXBElement<String>(_FLTOffendersSurname_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_Vehicle", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFVehicle(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFVehicle_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "City", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersCity(String value) {
        return new JAXBElement<String>(_FLTOffendersCity_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "AgeGroup", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersAgeGroup(String value) {
        return new JAXBElement<String>(_FLTOffendersAgeGroup_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_VehicleLicence", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFVehicleLicence(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFVehicleLicence_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Location", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersLocation(String value) {
        return new JAXBElement<String>(_FLTOffendersLocation_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "POSTxnID", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPOSTxnID(String value) {
        return new JAXBElement<String>(_FLTOffendersPOSTxnID_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_VehicleKeys", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFVehicleKeys(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFVehicleKeys_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_Hackney", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFHackney(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFHackney_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PIN", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPIN(String value) {
        return new JAXBElement<String>(_FLTOffendersPIN_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OffenceTime", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersOffenceTime(String value) {
        return new JAXBElement<String>(_FLTOffendersOffenceTime_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OtherName", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersOtherName(String value) {
        return new JAXBElement<String>(_FLTOffendersOtherName_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FirstName", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersFirstName(String value) {
        return new JAXBElement<String>(_FLTOffendersFirstName_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Wanted", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersWanted(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersWanted_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "DeviceID", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersDeviceID(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersDeviceID_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "IsWarning", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersIsWarning(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersIsWarning_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OffenceDate", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersOffenceDate(String value) {
        return new JAXBElement<String>(_FLTOffendersOffenceDate_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LicenceExpiryDate", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersLicenceExpiryDate(String value) {
        return new JAXBElement<String>(_FLTOffendersLicenceExpiryDate_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PaidStatus", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersPaidStatus(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersPaidStatus_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "POSTerminalID", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPOSTerminalID(String value) {
        return new JAXBElement<String>(_FLTOffendersPOSTerminalID_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_POC", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFPOC(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFPOC_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_Others", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersCFOthers(String value) {
        return new JAXBElement<String>(_FLTOffendersCFOthers_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "GPSLatitude", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersGPSLatitude(String value) {
        return new JAXBElement<String>(_FLTOffendersGPSLatitude_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_Passport", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFPassport(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFPassport_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_IDCard", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFIDCard(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFIDCard_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PhoneNumber", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPhoneNumber(String value) {
        return new JAXBElement<String>(_FLTOffendersPhoneNumber_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "VehicleRegNo", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersVehicleRegNo(String value) {
        return new JAXBElement<String>(_FLTOffendersVehicleRegNo_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_WayBill", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFWayBill(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFWayBill_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "MakenModel", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersMakenModel(String value) {
        return new JAXBElement<String>(_FLTOffendersMakenModel_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "TicketNo", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersTicketNo(String value) {
        return new JAXBElement<String>(_FLTOffendersTicketNo_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "DriversLicenceNo", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersDriversLicenceNo(String value) {
        return new JAXBElement<String>(_FLTOffendersDriversLicenceNo_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PushedDate", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPushedDate(String value) {
        return new JAXBElement<String>(_FLTOffendersPushedDate_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Route", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersRoute(String value) {
        return new JAXBElement<String>(_FLTOffendersRoute_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "VehicleColor", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersVehicleColor(String value) {
        return new JAXBElement<String>(_FLTOffendersVehicleColor_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "VehicleTypeID", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersVehicleTypeID(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersVehicleTypeID_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "GPSLongitude", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersGPSLongitude(String value) {
        return new JAXBElement<String>(_FLTOffendersGPSLongitude_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "VehicleUseID", scope = FLTOffenders.class)
    public JAXBElement<Integer> createFLTOffendersVehicleUseID(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersVehicleUseID_QNAME, Integer.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "DOB", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersDOB(String value) {
        return new JAXBElement<String>(_FLTOffendersDOB_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_StateCarriage", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFStateCarriage(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFStateCarriage_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "POSTxnFee", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPOSTxnFee(String value) {
        return new JAXBElement<String>(_FLTOffendersPOSTxnFee_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CF_RoadWorthiness", scope = FLTOffenders.class)
    public JAXBElement<Boolean> createFLTOffendersCFRoadWorthiness(Boolean value) {
        return new JAXBElement<Boolean>(_FLTOffendersCFRoadWorthiness_QNAME, Boolean.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Qualification", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersQualification(String value) {
        return new JAXBElement<String>(_FLTOffendersQualification_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PaymentDate", scope = FLTOffenders.class)
    public JAXBElement<String> createFLTOffendersPaymentDate(String value) {
        return new JAXBElement<String>(_FLTOffendersPaymentDate_QNAME, String.class, FLTOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FLTOffenders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Offender", scope = BulkOffenders.class)
    public JAXBElement<FLTOffenders> createBulkOffendersOffender(FLTOffenders value) {
        return new JAXBElement<FLTOffenders>(_BulkOffendersOffender_QNAME, FLTOffenders.class, BulkOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfOffenderOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Offences", scope = BulkOffenders.class)
    public JAXBElement<ArrayOfOffenderOffences> createBulkOffendersOffences(ArrayOfOffenderOffences value) {
        return new JAXBElement<ArrayOfOffenderOffences>(_BulkOffendersOffences_QNAME, ArrayOfOffenderOffences.class, BulkOffenders.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LastSyncDate", scope = SynchDates.class)
    public JAXBElement<String> createSynchDatesLastSyncDate(String value) {
        return new JAXBElement<String>(_SynchDatesLastSyncDate_QNAME, String.class, SynchDates.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTOffences }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Offences", scope = SystemSettings.class)
    public JAXBElement<ArrayOfFLTOffences> createSystemSettingsOffences(ArrayOfFLTOffences value) {
        return new JAXBElement<ArrayOfFLTOffences>(_BulkOffendersOffences_QNAME, ArrayOfFLTOffences.class, SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Demurrage", scope = SystemSettings.class)
    public JAXBElement<BigDecimal> createSystemSettingsDemurrage(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SystemSettingsDemurrage_QNAME, BigDecimal.class, SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTUsers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Users", scope = SystemSettings.class)
    public JAXBElement<ArrayOfFLTUsers> createSystemSettingsUsers(ArrayOfFLTUsers value) {
        return new JAXBElement<ArrayOfFLTUsers>(_SystemSettingsUsers_QNAME, ArrayOfFLTUsers.class, SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTRoutes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Routes", scope = SystemSettings.class)
    public JAXBElement<ArrayOfFLTRoutes> createSystemSettingsRoutes(ArrayOfFLTRoutes value) {
        return new JAXBElement<ArrayOfFLTRoutes>(_SystemSettingsRoutes_QNAME, ArrayOfFLTRoutes.class, SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfFLTScripts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Scripts", scope = SystemSettings.class)
    public JAXBElement<ArrayOfFLTScripts> createSystemSettingsScripts(ArrayOfFLTScripts value) {
        return new JAXBElement<ArrayOfFLTScripts>(_SystemSettingsScripts_QNAME, ArrayOfFLTScripts.class, SystemSettings.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LastActivityDate", scope = FLTRoutes.class)
    public JAXBElement<XMLGregorianCalendar> createFLTRoutesLastActivityDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_FLTRoutesLastActivityDate_QNAME, XMLGregorianCalendar.class, FLTRoutes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Status", scope = FLTRoutes.class)
    public JAXBElement<Integer> createFLTRoutesStatus(Integer value) {
        return new JAXBElement<Integer>(_FLTRoutesStatus_QNAME, Integer.class, FLTRoutes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CommandCode", scope = FLTRoutes.class)
    public JAXBElement<String> createFLTRoutesCommandCode(String value) {
        return new JAXBElement<String>(_FLTRoutesCommandCode_QNAME, String.class, FLTRoutes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "RouteCode", scope = FLTRoutes.class)
    public JAXBElement<String> createFLTRoutesRouteCode(String value) {
        return new JAXBElement<String>(_FLTRoutesRouteCode_QNAME, String.class, FLTRoutes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "RouteName", scope = FLTRoutes.class)
    public JAXBElement<String> createFLTRoutesRouteName(String value) {
        return new JAXBElement<String>(_FLTRoutesRouteName_QNAME, String.class, FLTRoutes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "TicketNumber", scope = BulkCaptureResult.class)
    public JAXBElement<String> createBulkCaptureResultTicketNumber(String value) {
        return new JAXBElement<String>(_BulkCaptureResultTicketNumber_QNAME, String.class, BulkCaptureResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LastActivityDate", scope = FLTUsers.class)
    public JAXBElement<XMLGregorianCalendar> createFLTUsersLastActivityDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_FLTRoutesLastActivityDate_QNAME, XMLGregorianCalendar.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Status", scope = FLTUsers.class)
    public JAXBElement<Integer> createFLTUsersStatus(Integer value) {
        return new JAXBElement<Integer>(_FLTRoutesStatus_QNAME, Integer.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LastName", scope = FLTUsers.class)
    public JAXBElement<String> createFLTUsersLastName(String value) {
        return new JAXBElement<String>(_FLTUsersLastName_QNAME, String.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "CommandCode", scope = FLTUsers.class)
    public JAXBElement<String> createFLTUsersCommandCode(String value) {
        return new JAXBElement<String>(_FLTRoutesCommandCode_QNAME, String.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "UserName", scope = FLTUsers.class)
    public JAXBElement<String> createFLTUsersUserName(String value) {
        return new JAXBElement<String>(_FLTUsersUserName_QNAME, String.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "PIN", scope = FLTUsers.class)
    public JAXBElement<String> createFLTUsersPIN(String value) {
        return new JAXBElement<String>(_FLTOffendersPIN_QNAME, String.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OtherName", scope = FLTUsers.class)
    public JAXBElement<String> createFLTUsersOtherName(String value) {
        return new JAXBElement<String>(_FLTOffendersOtherName_QNAME, String.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "FirstName", scope = FLTUsers.class)
    public JAXBElement<String> createFLTUsersFirstName(String value) {
        return new JAXBElement<String>(_FLTOffendersFirstName_QNAME, String.class, FLTUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OffenderID", scope = FLTOffendersOffences.class)
    public JAXBElement<Integer> createFLTOffendersOffencesOffenderID(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersOffencesOffenderID_QNAME, Integer.class, FLTOffendersOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Penalty", scope = FLTOffendersOffences.class)
    public JAXBElement<BigDecimal> createFLTOffendersOffencesPenalty(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_FLTOffendersPenalty_QNAME, BigDecimal.class, FLTOffendersOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Point", scope = FLTOffendersOffences.class)
    public JAXBElement<Integer> createFLTOffendersOffencesPoint(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersOffencesPoint_QNAME, Integer.class, FLTOffendersOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "OffenceID", scope = FLTOffendersOffences.class)
    public JAXBElement<Integer> createFLTOffendersOffencesOffenceID(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersOffencesOffenceID_QNAME, Integer.class, FLTOffendersOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Description", scope = FLTOffences.class)
    public JAXBElement<String> createFLTOffencesDescription(String value) {
        return new JAXBElement<String>(_FLTScriptsDescription_QNAME, String.class, FLTOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Penalty", scope = FLTOffences.class)
    public JAXBElement<BigDecimal> createFLTOffencesPenalty(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_FLTOffendersPenalty_QNAME, BigDecimal.class, FLTOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "LastActivityDate", scope = FLTOffences.class)
    public JAXBElement<XMLGregorianCalendar> createFLTOffencesLastActivityDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_FLTRoutesLastActivityDate_QNAME, XMLGregorianCalendar.class, FLTOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Point", scope = FLTOffences.class)
    public JAXBElement<Integer> createFLTOffencesPoint(Integer value) {
        return new JAXBElement<Integer>(_FLTOffendersOffencesPoint_QNAME, Integer.class, FLTOffences.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", name = "Code", scope = FLTOffences.class)
    public JAXBElement<String> createFLTOffencesCode(String value) {
        return new JAXBElement<String>(_FLTOffencesCode_QNAME, String.class, FLTOffences.class, value);
    }

}
