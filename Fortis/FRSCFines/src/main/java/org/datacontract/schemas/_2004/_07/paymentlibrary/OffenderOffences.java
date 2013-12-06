
package org.datacontract.schemas._2004._07.paymentlibrary;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OffenderOffences complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OffenderOffences">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/PaymentLibrary}FLT_Offenders_Offences">
 *       &lt;sequence>
 *         &lt;element name="OffenceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OffenderOffences", propOrder = {
    "offenceCode"
})
public class OffenderOffences
    extends FLTOffendersOffences
{

    @XmlElementRef(name = "OffenceCode", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<String> offenceCode;

    /**
     * Gets the value of the offenceCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOffenceCode() {
        return offenceCode;
    }

    /**
     * Sets the value of the offenceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOffenceCode(JAXBElement<String> value) {
        this.offenceCode = ((JAXBElement<String> ) value);
    }

}
