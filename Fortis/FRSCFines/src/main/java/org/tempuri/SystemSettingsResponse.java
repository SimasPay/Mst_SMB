
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.paymentlibrary.SystemSettings;


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
 *         &lt;element name="SystemSettingsResult" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}SystemSettings" minOccurs="0"/>
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
    "systemSettingsResult"
})
@XmlRootElement(name = "SystemSettingsResponse")
public class SystemSettingsResponse {

    @XmlElementRef(name = "SystemSettingsResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<SystemSettings> systemSettingsResult;

    /**
     * Gets the value of the systemSettingsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SystemSettings }{@code >}
     *     
     */
    public JAXBElement<SystemSettings> getSystemSettingsResult() {
        return systemSettingsResult;
    }

    /**
     * Sets the value of the systemSettingsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SystemSettings }{@code >}
     *     
     */
    public void setSystemSettingsResult(JAXBElement<SystemSettings> value) {
        this.systemSettingsResult = ((JAXBElement<SystemSettings> ) value);
    }

}
