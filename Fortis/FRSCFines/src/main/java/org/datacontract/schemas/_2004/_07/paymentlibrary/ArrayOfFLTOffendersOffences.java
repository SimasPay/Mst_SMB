
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFLT_Offenders_Offences complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFLT_Offenders_Offences">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FLT_Offenders_Offences" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}FLT_Offenders_Offences" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFLT_Offenders_Offences", propOrder = {
    "fltOffendersOffences"
})
public class ArrayOfFLTOffendersOffences {

    @XmlElement(name = "FLT_Offenders_Offences", nillable = true)
    protected List<FLTOffendersOffences> fltOffendersOffences;

    /**
     * Gets the value of the fltOffendersOffences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fltOffendersOffences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFLTOffendersOffences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FLTOffendersOffences }
     * 
     * 
     */
    public List<FLTOffendersOffences> getFLTOffendersOffences() {
        if (fltOffendersOffences == null) {
            fltOffendersOffences = new ArrayList<FLTOffendersOffences>();
        }
        return this.fltOffendersOffences;
    }

}
