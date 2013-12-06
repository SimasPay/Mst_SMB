
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SystemSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SystemSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Demurrage" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="Offences" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfFLT_Offences" minOccurs="0"/>
 *         &lt;element name="Routes" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfFLT_Routes" minOccurs="0"/>
 *         &lt;element name="Scripts" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfFLT_Scripts" minOccurs="0"/>
 *         &lt;element name="Users" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}ArrayOfFLT_Users" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SystemSettings", propOrder = {
    "demurrage",
    "offences",
    "routes",
    "scripts",
    "users"
})
public class SystemSettings {

    @XmlElementRef(name = "Demurrage", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<BigDecimal> demurrage;
    @XmlElementRef(name = "Offences", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<ArrayOfFLTOffences> offences;
    @XmlElementRef(name = "Routes", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<ArrayOfFLTRoutes> routes;
    @XmlElementRef(name = "Scripts", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<ArrayOfFLTScripts> scripts;
    @XmlElementRef(name = "Users", namespace = "http://schemas.datacontract.org/2004/07/PaymentLibrary", type = JAXBElement.class)
    protected JAXBElement<ArrayOfFLTUsers> users;

    /**
     * Gets the value of the demurrage property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getDemurrage() {
        return demurrage;
    }

    /**
     * Sets the value of the demurrage property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setDemurrage(JAXBElement<BigDecimal> value) {
        this.demurrage = ((JAXBElement<BigDecimal> ) value);
    }

    /**
     * Gets the value of the offences property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTOffences }{@code >}
     *     
     */
    public JAXBElement<ArrayOfFLTOffences> getOffences() {
        return offences;
    }

    /**
     * Sets the value of the offences property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTOffences }{@code >}
     *     
     */
    public void setOffences(JAXBElement<ArrayOfFLTOffences> value) {
        this.offences = ((JAXBElement<ArrayOfFLTOffences> ) value);
    }

    /**
     * Gets the value of the routes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTRoutes }{@code >}
     *     
     */
    public JAXBElement<ArrayOfFLTRoutes> getRoutes() {
        return routes;
    }

    /**
     * Sets the value of the routes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTRoutes }{@code >}
     *     
     */
    public void setRoutes(JAXBElement<ArrayOfFLTRoutes> value) {
        this.routes = ((JAXBElement<ArrayOfFLTRoutes> ) value);
    }

    /**
     * Gets the value of the scripts property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTScripts }{@code >}
     *     
     */
    public JAXBElement<ArrayOfFLTScripts> getScripts() {
        return scripts;
    }

    /**
     * Sets the value of the scripts property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTScripts }{@code >}
     *     
     */
    public void setScripts(JAXBElement<ArrayOfFLTScripts> value) {
        this.scripts = ((JAXBElement<ArrayOfFLTScripts> ) value);
    }

    /**
     * Gets the value of the users property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTUsers }{@code >}
     *     
     */
    public JAXBElement<ArrayOfFLTUsers> getUsers() {
        return users;
    }

    /**
     * Sets the value of the users property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfFLTUsers }{@code >}
     *     
     */
    public void setUsers(JAXBElement<ArrayOfFLTUsers> value) {
        this.users = ((JAXBElement<ArrayOfFLTUsers> ) value);
    }

}
