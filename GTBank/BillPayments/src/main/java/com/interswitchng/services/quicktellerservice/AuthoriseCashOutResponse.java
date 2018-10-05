
package com.interswitchng.services.quicktellerservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="AuthoriseCashOutResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "authoriseCashOutResult"
})
@XmlRootElement(name = "AuthoriseCashOutResponse")
public class AuthoriseCashOutResponse {

    @XmlElementRef(name = "AuthoriseCashOutResult", namespace = "http://services.interswitchng.com/quicktellerservice/", type = JAXBElement.class)
    protected JAXBElement<String> authoriseCashOutResult;

    /**
     * Gets the value of the authoriseCashOutResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAuthoriseCashOutResult() {
        return authoriseCashOutResult;
    }

    /**
     * Sets the value of the authoriseCashOutResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAuthoriseCashOutResult(JAXBElement<String> value) {
        this.authoriseCashOutResult = ((JAXBElement<String> ) value);
    }

}
