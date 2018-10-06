
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
 *         &lt;element name="VerifyBillerResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "verifyBillerResult"
})
@XmlRootElement(name = "VerifyBillerResponse")
public class VerifyBillerResponse {

    @XmlElementRef(name = "VerifyBillerResult", namespace = "http://services.interswitchng.com/quicktellerservice/", type = JAXBElement.class)
    protected JAXBElement<String> verifyBillerResult;

    /**
     * Gets the value of the verifyBillerResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVerifyBillerResult() {
        return verifyBillerResult;
    }

    /**
     * Sets the value of the verifyBillerResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVerifyBillerResult(JAXBElement<String> value) {
        this.verifyBillerResult = ((JAXBElement<String> ) value);
    }

}
