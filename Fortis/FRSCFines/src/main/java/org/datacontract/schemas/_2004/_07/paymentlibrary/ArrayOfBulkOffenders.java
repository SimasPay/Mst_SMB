
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfBulkOffenders complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfBulkOffenders">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BulkOffenders" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}BulkOffenders" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBulkOffenders", propOrder = {
    "bulkOffenders"
})
public class ArrayOfBulkOffenders {

    @XmlElement(name = "BulkOffenders", nillable = true)
    protected List<BulkOffenders> bulkOffenders;

    /**
     * Gets the value of the bulkOffenders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bulkOffenders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBulkOffenders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkOffenders }
     * 
     * 
     */
    public List<BulkOffenders> getBulkOffenders() {
        if (bulkOffenders == null) {
            bulkOffenders = new ArrayList<BulkOffenders>();
        }
        return this.bulkOffenders;
    }

}
