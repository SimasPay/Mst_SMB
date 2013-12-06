
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfSynchDates;


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
 *         &lt;element name="DeviceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SynchDates" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfSynchDates" minOccurs="0"/>
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
    "deviceID",
    "synchDates"
})
@XmlRootElement(name = "SystemSettings")
public class SystemSettings {

    @XmlElementRef(name = "DeviceID", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> deviceID;
    @XmlElementRef(name = "SynchDates", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfSynchDates> synchDates;

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

    /**
     * Gets the value of the synchDates property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfSynchDates }{@code >}
     *     
     */
    public JAXBElement<ArrayOfSynchDates> getSynchDates() {
        return synchDates;
    }

    /**
     * Sets the value of the synchDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfSynchDates }{@code >}
     *     
     */
    public void setSynchDates(JAXBElement<ArrayOfSynchDates> value) {
        this.synchDates = ((JAXBElement<ArrayOfSynchDates> ) value);
    }

}
