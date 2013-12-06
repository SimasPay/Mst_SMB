
package org.datacontract.schemas._2004._07.system_data_objects;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTOffences;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTOffenders;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTOffendersOffences;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTRoutes;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTScripts;
import org.datacontract.schemas._2004._07.paymentlibrary.FLTUsers;
import org.datacontract.schemas._2004._07.system.EntityKey;


/**
 * <p>Java class for EntityObject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EntityObject">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses}StructuralObject">
 *       &lt;sequence>
 *         &lt;element name="EntityKey" type="{http://schemas.datacontract.org/2004/07/System.Data}EntityKey" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityObject", propOrder = {
    "entityKey"
})
@XmlSeeAlso({
    FLTUsers.class,
    FLTOffences.class,
    FLTOffenders.class,
    FLTRoutes.class,
    FLTOffendersOffences.class,
    FLTScripts.class
})
public class EntityObject
    extends StructuralObject
{

    @XmlElementRef(name = "EntityKey", namespace = "http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", type = JAXBElement.class)
    protected JAXBElement<EntityKey> entityKey;

    /**
     * Gets the value of the entityKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EntityKey }{@code >}
     *     
     */
    public JAXBElement<EntityKey> getEntityKey() {
        return entityKey;
    }

    /**
     * Sets the value of the entityKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EntityKey }{@code >}
     *     
     */
    public void setEntityKey(JAXBElement<EntityKey> value) {
        this.entityKey = ((JAXBElement<EntityKey> ) value);
    }

}
