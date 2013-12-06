
package org.datacontract.schemas._2004._07.paymentlibrary;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BulkOffenders complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkOffenders">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Offences" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfOffenderOffences" minOccurs="0"/>
 *         &lt;element name="Offender" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}FLT_Offenders" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BulkOffenders", propOrder = {
    "offences",
    "offender"
})
public class BulkOffenders {

    @XmlElementRef(name = "Offences", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<ArrayOfOffenderOffences> offences;
    @XmlElementRef(name = "Offender", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<FLTOffenders> offender;

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

}
