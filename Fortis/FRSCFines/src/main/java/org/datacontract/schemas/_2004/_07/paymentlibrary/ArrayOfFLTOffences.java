
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFLT_Offences complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFLT_Offences">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FLT_Offences" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}FLT_Offences" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFLT_Offences", propOrder = {
    "fltOffences"
})
public class ArrayOfFLTOffences {

    @XmlElement(name = "FLT_Offences", nillable = true)
    protected List<FLTOffences> fltOffences;

    /**
     * Gets the value of the fltOffences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fltOffences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFLTOffences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FLTOffences }
     * 
     * 
     */
    public List<FLTOffences> getFLTOffences() {
        if (fltOffences == null) {
            fltOffences = new ArrayList<FLTOffences>();
        }
        return this.fltOffences;
    }

}
