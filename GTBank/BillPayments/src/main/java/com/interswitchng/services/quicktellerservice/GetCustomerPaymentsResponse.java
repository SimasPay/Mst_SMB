
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
 *         &lt;element name="GetCustomerPaymentsResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getCustomerPaymentsResult"
})
@XmlRootElement(name = "GetCustomerPaymentsResponse")
public class GetCustomerPaymentsResponse {

    @XmlElementRef(name = "GetCustomerPaymentsResult", namespace = "http://services.interswitchng.com/quicktellerservice/", type = JAXBElement.class)
    protected JAXBElement<String> getCustomerPaymentsResult;

    /**
     * Gets the value of the getCustomerPaymentsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGetCustomerPaymentsResult() {
        return getCustomerPaymentsResult;
    }

    /**
     * Sets the value of the getCustomerPaymentsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGetCustomerPaymentsResult(JAXBElement<String> value) {
        this.getCustomerPaymentsResult = ((JAXBElement<String> ) value);
    }

}
