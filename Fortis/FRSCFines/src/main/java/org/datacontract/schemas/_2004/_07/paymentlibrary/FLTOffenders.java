
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.system_data_objects.EntityObject;


/**
 * <p>Java class for FLT_Offenders complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FLT_Offenders">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses}EntityObject">
 *       &lt;sequence>
 *         &lt;element name="AgeGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CF_DriversLicence" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_Hackney" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_IDCard" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_Insurance" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_Others" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CF_POC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_Passport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_RoadWorthiness" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_StateCarriage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_Vehicle" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_VehicleKeys" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_VehicleLicence" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CF_WayBill" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeviceID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DriversLicenceNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GPSLatitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GPSLongitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="IsWarning" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="LicenceExpiryDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LicenceIssueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MakenModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OffenceDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OffenceTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OtherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="POSTerminalID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="POSTxnFee" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="POSTxnID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PaidStatus" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="PaymentDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Penalty" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="PhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PushedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PushedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Qualification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Route" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StateID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StreetAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TicketNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleColor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleRegNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleTypeID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="VehicleUseID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Wanted" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FLT_Offenders", propOrder = {
    "ageGroup",
    "cfDriversLicence",
    "cfHackney",
    "cfidCard",
    "cfInsurance",
    "cfOthers",
    "cfpoc",
    "cfPassport",
    "cfRoadWorthiness",
    "cfStateCarriage",
    "cfVehicle",
    "cfVehicleKeys",
    "cfVehicleLicence",
    "cfWayBill",
    "city",
    "dob",
    "deviceID",
    "driversLicenceNo",
    "firstName",
    "gpsLatitude",
    "gpsLongitude",
    "id",
    "isWarning",
    "licenceExpiryDate",
    "licenceIssueDate",
    "location",
    "makenModel",
    "offenceDate",
    "offenceTime",
    "otherName",
    "pin",
    "posTerminalID",
    "posTxnFee",
    "posTxnID",
    "paidStatus",
    "paymentDate",
    "penalty",
    "phoneNumber",
    "pushedDate",
    "pushedTime",
    "qualification",
    "route",
    "stateID",
    "streetAddress",
    "surname",
    "ticketNo",
    "vehicleColor",
    "vehicleRegNo",
    "vehicleTypeID",
    "vehicleUseID",
    "wanted"
})
public class FLTOffenders
    extends EntityObject
{

    @XmlElementRef(name = "AgeGroup", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> ageGroup;
    @XmlElementRef(name = "CF_DriversLicence", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfDriversLicence;
    @XmlElementRef(name = "CF_Hackney", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfHackney;
    @XmlElementRef(name = "CF_IDCard", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfidCard;
    @XmlElementRef(name = "CF_Insurance", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfInsurance;
    @XmlElementRef(name = "CF_Others", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> cfOthers;
    @XmlElementRef(name = "CF_POC", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfpoc;
    @XmlElementRef(name = "CF_Passport", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfPassport;
    @XmlElementRef(name = "CF_RoadWorthiness", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfRoadWorthiness;
    @XmlElementRef(name = "CF_StateCarriage", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfStateCarriage;
    @XmlElementRef(name = "CF_Vehicle", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfVehicle;
    @XmlElementRef(name = "CF_VehicleKeys", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfVehicleKeys;
    @XmlElementRef(name = "CF_VehicleLicence", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfVehicleLicence;
    @XmlElementRef(name = "CF_WayBill", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Boolean> cfWayBill;
    @XmlElementRef(name = "City", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> city;
    @XmlElementRef(name = "DOB", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> dob;
    @XmlElementRef(name = "DeviceID", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> deviceID;
    @XmlElementRef(name = "DriversLicenceNo", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> driversLicenceNo;
    @XmlElementRef(name = "FirstName", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> firstName;
    @XmlElementRef(name = "GPSLatitude", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> gpsLatitude;
    @XmlElementRef(name = "GPSLongitude", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> gpsLongitude;
    @XmlElement(name = "ID")
    protected Integer id;
    @XmlElementRef(name = "IsWarning", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> isWarning;
    @XmlElementRef(name = "LicenceExpiryDate", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> licenceExpiryDate;
    @XmlElementRef(name = "LicenceIssueDate", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> licenceIssueDate;
    @XmlElementRef(name = "Location", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> location;
    @XmlElementRef(name = "MakenModel", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> makenModel;
    @XmlElementRef(name = "OffenceDate", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> offenceDate;
    @XmlElementRef(name = "OffenceTime", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> offenceTime;
    @XmlElementRef(name = "OtherName", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> otherName;
    @XmlElementRef(name = "PIN", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> pin;
    @XmlElementRef(name = "POSTerminalID", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> posTerminalID;
    @XmlElementRef(name = "POSTxnFee", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> posTxnFee;
    @XmlElementRef(name = "POSTxnID", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> posTxnID;
    @XmlElementRef(name = "PaidStatus", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> paidStatus;
    @XmlElementRef(name = "PaymentDate", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> paymentDate;
    @XmlElementRef(name = "Penalty", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<BigDecimal> penalty;
    @XmlElementRef(name = "PhoneNumber", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> phoneNumber;
    @XmlElementRef(name = "PushedDate", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> pushedDate;
    @XmlElementRef(name = "PushedTime", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> pushedTime;
    @XmlElementRef(name = "Qualification", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> qualification;
    @XmlElementRef(name = "Route", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> route;
    @XmlElementRef(name = "StateID", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> stateID;
    @XmlElementRef(name = "StreetAddress", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> streetAddress;
    @XmlElementRef(name = "Surname", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> surname;
    @XmlElementRef(name = "TicketNo", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> ticketNo;
    @XmlElementRef(name = "VehicleColor", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> vehicleColor;
    @XmlElementRef(name = "VehicleRegNo", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> vehicleRegNo;
    @XmlElementRef(name = "VehicleTypeID", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> vehicleTypeID;
    @XmlElementRef(name = "VehicleUseID", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> vehicleUseID;
    @XmlElementRef(name = "Wanted", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<Integer> wanted;

    /**
     * Gets the value of the ageGroup property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAgeGroup() {
        return ageGroup;
    }

    /**
     * Sets the value of the ageGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAgeGroup(JAXBElement<String> value) {
        this.ageGroup = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the cfDriversLicence property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFDriversLicence() {
        return cfDriversLicence;
    }

    /**
     * Sets the value of the cfDriversLicence property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFDriversLicence(JAXBElement<Boolean> value) {
        this.cfDriversLicence = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfHackney property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFHackney() {
        return cfHackney;
    }

    /**
     * Sets the value of the cfHackney property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFHackney(JAXBElement<Boolean> value) {
        this.cfHackney = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfidCard property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFIDCard() {
        return cfidCard;
    }

    /**
     * Sets the value of the cfidCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFIDCard(JAXBElement<Boolean> value) {
        this.cfidCard = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfInsurance property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFInsurance() {
        return cfInsurance;
    }

    /**
     * Sets the value of the cfInsurance property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFInsurance(JAXBElement<Boolean> value) {
        this.cfInsurance = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfOthers property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCFOthers() {
        return cfOthers;
    }

    /**
     * Sets the value of the cfOthers property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCFOthers(JAXBElement<String> value) {
        this.cfOthers = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the cfpoc property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFPOC() {
        return cfpoc;
    }

    /**
     * Sets the value of the cfpoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFPOC(JAXBElement<Boolean> value) {
        this.cfpoc = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfPassport property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFPassport() {
        return cfPassport;
    }

    /**
     * Sets the value of the cfPassport property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFPassport(JAXBElement<Boolean> value) {
        this.cfPassport = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfRoadWorthiness property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFRoadWorthiness() {
        return cfRoadWorthiness;
    }

    /**
     * Sets the value of the cfRoadWorthiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFRoadWorthiness(JAXBElement<Boolean> value) {
        this.cfRoadWorthiness = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfStateCarriage property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFStateCarriage() {
        return cfStateCarriage;
    }

    /**
     * Sets the value of the cfStateCarriage property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFStateCarriage(JAXBElement<Boolean> value) {
        this.cfStateCarriage = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfVehicle property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFVehicle() {
        return cfVehicle;
    }

    /**
     * Sets the value of the cfVehicle property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFVehicle(JAXBElement<Boolean> value) {
        this.cfVehicle = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfVehicleKeys property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFVehicleKeys() {
        return cfVehicleKeys;
    }

    /**
     * Sets the value of the cfVehicleKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFVehicleKeys(JAXBElement<Boolean> value) {
        this.cfVehicleKeys = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfVehicleLicence property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFVehicleLicence() {
        return cfVehicleLicence;
    }

    /**
     * Sets the value of the cfVehicleLicence property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFVehicleLicence(JAXBElement<Boolean> value) {
        this.cfVehicleLicence = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the cfWayBill property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCFWayBill() {
        return cfWayBill;
    }

    /**
     * Sets the value of the cfWayBill property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCFWayBill(JAXBElement<Boolean> value) {
        this.cfWayBill = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCity(JAXBElement<String> value) {
        this.city = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDOB() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDOB(JAXBElement<String> value) {
        this.dob = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the deviceID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the value of the deviceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setDeviceID(JAXBElement<Integer> value) {
        this.deviceID = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the driversLicenceNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDriversLicenceNo() {
        return driversLicenceNo;
    }

    /**
     * Sets the value of the driversLicenceNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDriversLicenceNo(JAXBElement<String> value) {
        this.driversLicenceNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFirstName(JAXBElement<String> value) {
        this.firstName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the gpsLatitude property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGPSLatitude() {
        return gpsLatitude;
    }

    /**
     * Sets the value of the gpsLatitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGPSLatitude(JAXBElement<String> value) {
        this.gpsLatitude = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the gpsLongitude property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGPSLongitude() {
        return gpsLongitude;
    }

    /**
     * Sets the value of the gpsLongitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGPSLongitude(JAXBElement<String> value) {
        this.gpsLongitude = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setID(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the isWarning property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getIsWarning() {
        return isWarning;
    }

    /**
     * Sets the value of the isWarning property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setIsWarning(JAXBElement<Integer> value) {
        this.isWarning = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the licenceExpiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLicenceExpiryDate() {
        return licenceExpiryDate;
    }

    /**
     * Sets the value of the licenceExpiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLicenceExpiryDate(JAXBElement<String> value) {
        this.licenceExpiryDate = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the licenceIssueDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLicenceIssueDate() {
        return licenceIssueDate;
    }

    /**
     * Sets the value of the licenceIssueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLicenceIssueDate(JAXBElement<String> value) {
        this.licenceIssueDate = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocation(JAXBElement<String> value) {
        this.location = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the makenModel property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMakenModel() {
        return makenModel;
    }

    /**
     * Sets the value of the makenModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMakenModel(JAXBElement<String> value) {
        this.makenModel = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the offenceDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOffenceDate() {
        return offenceDate;
    }

    /**
     * Sets the value of the offenceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOffenceDate(JAXBElement<String> value) {
        this.offenceDate = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the offenceTime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOffenceTime() {
        return offenceTime;
    }

    /**
     * Sets the value of the offenceTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOffenceTime(JAXBElement<String> value) {
        this.offenceTime = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the otherName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOtherName() {
        return otherName;
    }

    /**
     * Sets the value of the otherName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOtherName(JAXBElement<String> value) {
        this.otherName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pin property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIN() {
        return pin;
    }

    /**
     * Sets the value of the pin property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIN(JAXBElement<String> value) {
        this.pin = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the posTerminalID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPOSTerminalID() {
        return posTerminalID;
    }

    /**
     * Sets the value of the posTerminalID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPOSTerminalID(JAXBElement<String> value) {
        this.posTerminalID = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the posTxnFee property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPOSTxnFee() {
        return posTxnFee;
    }

    /**
     * Sets the value of the posTxnFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPOSTxnFee(JAXBElement<String> value) {
        this.posTxnFee = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the posTxnID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPOSTxnID() {
        return posTxnID;
    }

    /**
     * Sets the value of the posTxnID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPOSTxnID(JAXBElement<String> value) {
        this.posTxnID = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the paidStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getPaidStatus() {
        return paidStatus;
    }

    /**
     * Sets the value of the paidStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setPaidStatus(JAXBElement<Integer> value) {
        this.paidStatus = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the paymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPaymentDate(JAXBElement<String> value) {
        this.paymentDate = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the penalty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getPenalty() {
        return penalty;
    }

    /**
     * Sets the value of the penalty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setPenalty(JAXBElement<BigDecimal> value) {
        this.penalty = ((JAXBElement<BigDecimal> ) value);
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPhoneNumber(JAXBElement<String> value) {
        this.phoneNumber = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pushedDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPushedDate() {
        return pushedDate;
    }

    /**
     * Sets the value of the pushedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPushedDate(JAXBElement<String> value) {
        this.pushedDate = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pushedTime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPushedTime() {
        return pushedTime;
    }

    /**
     * Sets the value of the pushedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPushedTime(JAXBElement<String> value) {
        this.pushedTime = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the qualification property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getQualification() {
        return qualification;
    }

    /**
     * Sets the value of the qualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setQualification(JAXBElement<String> value) {
        this.qualification = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the route property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRoute() {
        return route;
    }

    /**
     * Sets the value of the route property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRoute(JAXBElement<String> value) {
        this.route = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the stateID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getStateID() {
        return stateID;
    }

    /**
     * Sets the value of the stateID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setStateID(JAXBElement<Integer> value) {
        this.stateID = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the streetAddress property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStreetAddress() {
        return streetAddress;
    }

    /**
     * Sets the value of the streetAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStreetAddress(JAXBElement<String> value) {
        this.streetAddress = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSurname(JAXBElement<String> value) {
        this.surname = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the ticketNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTicketNo() {
        return ticketNo;
    }

    /**
     * Sets the value of the ticketNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTicketNo(JAXBElement<String> value) {
        this.ticketNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the vehicleColor property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVehicleColor() {
        return vehicleColor;
    }

    /**
     * Sets the value of the vehicleColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVehicleColor(JAXBElement<String> value) {
        this.vehicleColor = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the vehicleRegNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVehicleRegNo() {
        return vehicleRegNo;
    }

    /**
     * Sets the value of the vehicleRegNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVehicleRegNo(JAXBElement<String> value) {
        this.vehicleRegNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the vehicleTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getVehicleTypeID() {
        return vehicleTypeID;
    }

    /**
     * Sets the value of the vehicleTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setVehicleTypeID(JAXBElement<Integer> value) {
        this.vehicleTypeID = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the vehicleUseID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getVehicleUseID() {
        return vehicleUseID;
    }

    /**
     * Sets the value of the vehicleUseID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setVehicleUseID(JAXBElement<Integer> value) {
        this.vehicleUseID = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the wanted property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getWanted() {
        return wanted;
    }

    /**
     * Sets the value of the wanted property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setWanted(JAXBElement<Integer> value) {
        this.wanted = ((JAXBElement<Integer> ) value);
    }

}
