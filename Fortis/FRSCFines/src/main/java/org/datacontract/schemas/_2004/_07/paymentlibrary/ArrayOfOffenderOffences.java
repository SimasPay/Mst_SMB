
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfOffenderOffences complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfOffenderOffences">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OffenderOffences" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}OffenderOffences" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfOffenderOffences", propOrder = {
    "offenderOffences"
})
public class ArrayOfOffenderOffences {

    @XmlElement(name = "OffenderOffences", nillable = true)
    protected List<OffenderOffences> offenderOffences;

    /**
     * Gets the value of the offenderOffences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the offenderOffences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOffenderOffences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OffenderOffences }
     * 
     * 
     */
    public List<OffenderOffences> getOffenderOffences() {
        if (offenderOffences == null) {
            offenderOffences = new ArrayList<OffenderOffences>();
        }
        return this.offenderOffences;
    }

}
