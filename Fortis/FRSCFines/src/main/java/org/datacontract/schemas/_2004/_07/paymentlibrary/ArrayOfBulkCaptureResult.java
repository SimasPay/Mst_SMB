
package org.datacontract.schemas._2004._07.paymentlibrary;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfBulkCaptureResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfBulkCaptureResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BulkCaptureResult" type="{http://schemas.datacontract.org/2004/07/PaymentLibrary}BulkCaptureResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBulkCaptureResult", propOrder = {
    "bulkCaptureResult"
})
public class ArrayOfBulkCaptureResult {

    @XmlElement(name = "BulkCaptureResult", nillable = true)
    protected List<BulkCaptureResult> bulkCaptureResult;

    /**
     * Gets the value of the bulkCaptureResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bulkCaptureResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBulkCaptureResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkCaptureResult }
     * 
     * 
     */
    public List<BulkCaptureResult> getBulkCaptureResult() {
        if (bulkCaptureResult == null) {
            bulkCaptureResult = new ArrayList<BulkCaptureResult>();
        }
        return this.bulkCaptureResult;
    }

}
