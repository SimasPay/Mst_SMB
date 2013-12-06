
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.paymentlibrary.ArrayOfBulkOffenders;


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
 *         &lt;element name="offendersInfo" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfBulkOffenders" minOccurs="0"/>
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
    "offendersInfo",
    "deviceID"
})
@XmlRootElement(name = "BulkCapture")
public class BulkCapture {

    @XmlElementRef(name = "offendersInfo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfBulkOffenders> offendersInfo;
    @XmlElementRef(name = "DeviceID", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> deviceID;

    /**
     * Gets the value of the offendersInfo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfBulkOffenders }{@code >}
     *     
     */
    public JAXBElement<ArrayOfBulkOffenders> getOffendersInfo() {
        return offendersInfo;
    }

    /**
     * Sets the value of the offendersInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfBulkOffenders }{@code >}
     *     
     */
    public void setOffendersInfo(JAXBElement<ArrayOfBulkOffenders> value) {
        this.offendersInfo = ((JAXBElement<ArrayOfBulkOffenders> ) value);
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
