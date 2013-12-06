
package com.ztesoft.zsmart.bss.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="RegisterNewSubsReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "registerNewSubsReturn"
})
@XmlRootElement(name = "RegisterNewSubsResponse")
public class RegisterNewSubsResponse {

    @XmlElement(name = "RegisterNewSubsReturn", required = true)
    protected String registerNewSubsReturn;

    /**
     * Gets the value of the registerNewSubsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisterNewSubsReturn() {
        return registerNewSubsReturn;
    }

    /**
     * Sets the value of the registerNewSubsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisterNewSubsReturn(String value) {
        this.registerNewSubsReturn = value;
    }

}
