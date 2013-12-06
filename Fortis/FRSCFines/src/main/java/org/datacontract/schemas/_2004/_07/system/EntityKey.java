
package org.datacontract.schemas._2004._07.system;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for EntityKey complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EntityKey">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EntityContainerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EntityKeyValues" type="{http://schemas.datacontract.org/2004/07/System.Data}ArrayOfEntityKeyMember" minOccurs="0"/>
 *         &lt;element name="EntitySetName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://schemas.microsoft.com/2003/10/Serialization/}Id"/>
 *       &lt;attribute ref="{http://schemas.microsoft.com/2003/10/Serialization/}Ref"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityKey", propOrder = {
    "entityContainerName",
    "entityKeyValues",
    "entitySetName"
})
public class EntityKey {

    @XmlElementRef(name = "EntityContainerName", namespace = "http://schemas.datacontract.org/2004/07/System.Data", type = JAXBElement.class)
    protected JAXBElement<String> entityContainerName;
    @XmlElementRef(name = "EntityKeyValues", namespace = "http://schemas.datacontract.org/2004/07/System.Data", type = JAXBElement.class)
    protected JAXBElement<ArrayOfEntityKeyMember> entityKeyValues;
    @XmlElementRef(name = "EntitySetName", namespace = "http://schemas.datacontract.org/2004/07/System.Data", type = JAXBElement.class)
    protected JAXBElement<String> entitySetName;
    @XmlAttribute(name = "Id", namespace = "http://schemas.microsoft.com/2003/10/Serialization/")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "Ref", namespace = "http://schemas.microsoft.com/2003/10/Serialization/")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object ref;

    /**
     * Gets the value of the entityContainerName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEntityContainerName() {
        return entityContainerName;
    }

    /**
     * Sets the value of the entityContainerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEntityContainerName(JAXBElement<String> value) {
        this.entityContainerName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the entityKeyValues property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEntityKeyMember }{@code >}
     *     
     */
    public JAXBElement<ArrayOfEntityKeyMember> getEntityKeyValues() {
        return entityKeyValues;
    }

    /**
     * Sets the value of the entityKeyValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfEntityKeyMember }{@code >}
     *     
     */
    public void setEntityKeyValues(JAXBElement<ArrayOfEntityKeyMember> value) {
        this.entityKeyValues = ((JAXBElement<ArrayOfEntityKeyMember> ) value);
    }

    /**
     * Gets the value of the entitySetName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEntitySetName() {
        return entitySetName;
    }

    /**
     * Sets the value of the entitySetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEntitySetName(JAXBElement<String> value) {
        this.entitySetName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRef(Object value) {
        this.ref = value;
    }

}
