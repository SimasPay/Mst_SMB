
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfOffenderOffences;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTOffenders;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="offender" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}FLT_Offenders" minOccurs="0"/>
 *         &lt;element name="offences" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfOffenderOffences" minOccurs="0"/>
 *         &lt;element name="DeviceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "offender",
    "offences",
    "deviceID"
})
@XmlRootElement(name = "SingleCapture")
public class SingleCapture {

    @XmlElementRef(name = "offender", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<FLTOffenders> offender;
    @XmlElementRef(name = "offences", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfOffenderOffences> offences;
    @XmlElementRef(name = "DeviceID", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> deviceID;

    /**
     * Gets the value of the offender property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FLTOffenders }{@code >}
     *     
     */
    public JAXBElement<FLTOffenders> getOffender() {
        return offender;
    }

    /**
     * Sets the value of the offender property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FLTOffenders }{@code >}
     *     
     */
    public void setOffender(JAXBElement<FLTOffenders> value) {
        this.offender = ((JAXBElement<FLTOffenders> ) value);
    }

    /**
     * Gets the value of the offences property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOffenderOffences }{@code >}
     *     
     */
    public JAXBElement<ArrayOfOffenderOffences> getOffences() {
        return offences;
    }

    /**
     * Sets the value of the offences property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOffenderOffences }{@code >}
     *     
     */
    public void setOffences(JAXBElement<ArrayOfOffenderOffences> value) {
        this.offences = ((JAXBElement<ArrayOfOffenderOffences> ) value);
    }

    /**
     * Gets the value of the deviceID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the value of the deviceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDeviceID(JAXBElement<String> value) {
        this.deviceID = ((JAXBElement<String> ) value);
    }

}
