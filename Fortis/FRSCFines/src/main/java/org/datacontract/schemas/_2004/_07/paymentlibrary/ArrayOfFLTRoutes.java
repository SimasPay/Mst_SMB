
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFLT_Routes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFLT_Routes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FLT_Routes" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}FLT_Routes" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFLT_Routes", propOrder = {
    "fltRoutes"
})
public class ArrayOfFLTRoutes {

    @XmlElement(name = "FLT_Routes", nillable = true)
    protected List<FLTRoutes> fltRoutes;

    /**
     * Gets the value of the fltRoutes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fltRoutes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFLTRoutes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FLTRoutes }
     * 
     * 
     */
    public List<FLTRoutes> getFLTRoutes() {
        if (fltRoutes == null) {
            fltRoutes = new ArrayList<FLTRoutes>();
        }
        return this.fltRoutes;
    }

}
