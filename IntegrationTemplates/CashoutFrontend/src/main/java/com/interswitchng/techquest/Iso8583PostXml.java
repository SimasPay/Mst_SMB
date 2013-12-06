
package com.interswitchng.techquest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Service" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MsgType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Fields" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Field_002" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_003" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_004" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_005" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_006" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_007" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_008" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_009" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_010" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_011" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_012" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_013" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_014" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_015" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_016" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_017" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_018" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_019" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_020" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_021" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_022" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_023" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_024" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_025" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_026" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_027" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_028" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_029" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_030" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_031" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_032" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_033" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_034" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_035" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_036" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_037" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_038" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_039" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_040" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_041" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_042" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_043" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_044" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_045" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_046" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_047" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_048" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_049" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_050" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_051" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_052" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_053" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_054" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_055" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_056" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_057" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_058" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_059" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_060" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_061" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_062" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_063" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_064" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_065" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_066" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_067" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_068" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_069" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_070" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_071" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_072" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_073" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_074" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_075" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_076" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_077" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_078" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_079" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_080" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_081" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_082" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_083" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_084" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_085" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_086" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_087" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_088" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_089" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_090" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_091" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_092" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_093" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_094" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_095" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_096" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_097" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_098" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_099" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_100" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_101" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_102" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_103" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_104" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_105" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_106" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_107" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_108" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_109" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_110" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_111" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_112" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_113" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_114" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_115" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_116" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_117" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_118" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_119" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_120" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_121" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_122" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_123" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_124" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_125" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_126" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_001" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_002" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_003" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_004" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_005" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_006" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_007" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_008" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_009" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_010" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_011" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_012" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_013" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_014" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_015" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_016" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_017" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_018" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_019" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_020" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_021" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_022" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="StructureData" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="CustomerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="WithdrawalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Field_127_023" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_024" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_025" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_026" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_027" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_028" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_029" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_030" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_031" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_032" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_033" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_034" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_035" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_036" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_037" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_038" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Field_127_039" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "msgType",
    "fields"
})
public class Iso8583PostXml {

    @XmlElement(name = "Service", namespace = "http://techquest.interswitchng.com/")
    protected String service;
    @XmlElementRef(name = "MsgType", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
    protected JAXBElement<String> msgType;
    @XmlElement(name = "Fields", namespace = "http://techquest.interswitchng.com/", nillable = true)
    protected List<Iso8583PostXml.Fields> fields;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the msgType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMsgType() {
        return msgType;
    }

    /**
     * Sets the value of the msgType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMsgType(JAXBElement<String> value) {
        this.msgType = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the fields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Iso8583PostXml.Fields }
     * 
     * 
     */
    public List<Iso8583PostXml.Fields> getFields() {
        if (fields == null) {
            fields = new ArrayList<Iso8583PostXml.Fields>();
        }
        return this.fields;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Field_002" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_003" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_004" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_005" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_006" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_007" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_008" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_009" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_010" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_011" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_012" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_013" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_014" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_015" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_016" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_017" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_018" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_019" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_020" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_021" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_022" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_023" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_024" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_025" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_026" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_027" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_028" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_029" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_030" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_031" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_032" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_033" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_034" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_035" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_036" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_037" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_038" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_039" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_040" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_041" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_042" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_043" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_044" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_045" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_046" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_047" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_048" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_049" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_050" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_051" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_052" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_053" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_054" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_055" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_056" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_057" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_058" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_059" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_060" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_061" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_062" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_063" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_064" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_065" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_066" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_067" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_068" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_069" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_070" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_071" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_072" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_073" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_074" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_075" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_076" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_077" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_078" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_079" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_080" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_081" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_082" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_083" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_084" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_085" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_086" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_087" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_088" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_089" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_090" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_091" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_092" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_093" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_094" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_095" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_096" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_097" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_098" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_099" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_100" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_101" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_102" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_103" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_104" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_105" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_106" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_107" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_108" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_109" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_110" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_111" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_112" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_113" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_114" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_115" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_116" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_117" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_118" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_119" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_120" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_121" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_122" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_123" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_124" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_125" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_126" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_001" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_002" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_003" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_004" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_005" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_006" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_007" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_008" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_009" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_010" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_011" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_012" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_013" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_014" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_015" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_016" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_017" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_018" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_019" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_020" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_021" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_022" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="StructureData" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="CustomerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="WithdrawalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Field_127_023" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_024" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_025" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_026" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_027" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_028" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_029" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_030" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_031" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_032" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_033" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_034" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_035" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_036" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_037" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_038" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Field_127_039" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "field002",
        "field003",
        "field004",
        "field005",
        "field006",
        "field007",
        "field008",
        "field009",
        "field010",
        "field011",
        "field012",
        "field013",
        "field014",
        "field015",
        "field016",
        "field017",
        "field018",
        "field019",
        "field020",
        "field021",
        "field022",
        "field023",
        "field024",
        "field025",
        "field026",
        "field027",
        "field028",
        "field029",
        "field030",
        "field031",
        "field032",
        "field033",
        "field034",
        "field035",
        "field036",
        "field037",
        "field038",
        "field039",
        "field040",
        "field041",
        "field042",
        "field043",
        "field044",
        "field045",
        "field046",
        "field047",
        "field048",
        "field049",
        "field050",
        "field051",
        "field052",
        "field053",
        "field054",
        "field055",
        "field056",
        "field057",
        "field058",
        "field059",
        "field060",
        "field061",
        "field062",
        "field063",
        "field064",
        "field065",
        "field066",
        "field067",
        "field068",
        "field069",
        "field070",
        "field071",
        "field072",
        "field073",
        "field074",
        "field075",
        "field076",
        "field077",
        "field078",
        "field079",
        "field080",
        "field081",
        "field082",
        "field083",
        "field084",
        "field085",
        "field086",
        "field087",
        "field088",
        "field089",
        "field090",
        "field091",
        "field092",
        "field093",
        "field094",
        "field095",
        "field096",
        "field097",
        "field098",
        "field099",
        "field100",
        "field101",
        "field102",
        "field103",
        "field104",
        "field105",
        "field106",
        "field107",
        "field108",
        "field109",
        "field110",
        "field111",
        "field112",
        "field113",
        "field114",
        "field115",
        "field116",
        "field117",
        "field118",
        "field119",
        "field120",
        "field121",
        "field122",
        "field123",
        "field124",
        "field125",
        "field126",
        "field127001",
        "field127002",
        "field127003",
        "field127004",
        "field127005",
        "field127006",
        "field127007",
        "field127008",
        "field127009",
        "field127010",
        "field127011",
        "field127012",
        "field127013",
        "field127014",
        "field127015",
        "field127016",
        "field127017",
        "field127018",
        "field127019",
        "field127020",
        "field127021",
        "field127022",
        "field127023",
        "field127024",
        "field127025",
        "field127026",
        "field127027",
        "field127028",
        "field127029",
        "field127030",
        "field127031",
        "field127032",
        "field127033",
        "field127034",
        "field127035",
        "field127036",
        "field127037",
        "field127038",
        "field127039"
    })
    public static class Fields {

        @XmlElementRef(name = "Field_002", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field002;
        @XmlElementRef(name = "Field_003", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field003;
        @XmlElement(name = "Field_004", namespace = "http://techquest.interswitchng.com/")
        protected String field004;
        @XmlElement(name = "Field_005", namespace = "http://techquest.interswitchng.com/")
        protected String field005;
        @XmlElementRef(name = "Field_006", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field006;
        @XmlElementRef(name = "Field_007", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field007;
        @XmlElementRef(name = "Field_008", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field008;
        @XmlElement(name = "Field_009", namespace = "http://techquest.interswitchng.com/")
        protected String field009;
        @XmlElementRef(name = "Field_010", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field010;
        @XmlElementRef(name = "Field_011", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field011;
        @XmlElementRef(name = "Field_012", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field012;
        @XmlElementRef(name = "Field_013", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field013;
        @XmlElement(name = "Field_014", namespace = "http://techquest.interswitchng.com/")
        protected String field014;
        @XmlElement(name = "Field_015", namespace = "http://techquest.interswitchng.com/")
        protected String field015;
        @XmlElementRef(name = "Field_016", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field016;
        @XmlElementRef(name = "Field_017", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field017;
        @XmlElementRef(name = "Field_018", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field018;
        @XmlElement(name = "Field_019", namespace = "http://techquest.interswitchng.com/")
        protected String field019;
        @XmlElementRef(name = "Field_020", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field020;
        @XmlElementRef(name = "Field_021", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field021;
        @XmlElementRef(name = "Field_022", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field022;
        @XmlElementRef(name = "Field_023", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field023;
        @XmlElement(name = "Field_024", namespace = "http://techquest.interswitchng.com/")
        protected String field024;
        @XmlElement(name = "Field_025", namespace = "http://techquest.interswitchng.com/")
        protected String field025;
        @XmlElementRef(name = "Field_026", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field026;
        @XmlElementRef(name = "Field_027", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field027;
        @XmlElementRef(name = "Field_028", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field028;
        @XmlElement(name = "Field_029", namespace = "http://techquest.interswitchng.com/")
        protected String field029;
        @XmlElementRef(name = "Field_030", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field030;
        @XmlElementRef(name = "Field_031", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field031;
        @XmlElementRef(name = "Field_032", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field032;
        @XmlElementRef(name = "Field_033", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field033;
        @XmlElement(name = "Field_034", namespace = "http://techquest.interswitchng.com/")
        protected String field034;
        @XmlElement(name = "Field_035", namespace = "http://techquest.interswitchng.com/")
        protected String field035;
        @XmlElementRef(name = "Field_036", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field036;
        @XmlElementRef(name = "Field_037", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field037;
        @XmlElementRef(name = "Field_038", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field038;
        @XmlElement(name = "Field_039", namespace = "http://techquest.interswitchng.com/")
        protected String field039;
        @XmlElementRef(name = "Field_040", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field040;
        @XmlElementRef(name = "Field_041", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field041;
        @XmlElementRef(name = "Field_042", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field042;
        @XmlElementRef(name = "Field_043", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field043;
        @XmlElement(name = "Field_044", namespace = "http://techquest.interswitchng.com/")
        protected String field044;
        @XmlElement(name = "Field_045", namespace = "http://techquest.interswitchng.com/")
        protected String field045;
        @XmlElementRef(name = "Field_046", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field046;
        @XmlElementRef(name = "Field_047", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field047;
        @XmlElementRef(name = "Field_048", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field048;
        @XmlElement(name = "Field_049", namespace = "http://techquest.interswitchng.com/")
        protected String field049;
        @XmlElementRef(name = "Field_050", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field050;
        @XmlElementRef(name = "Field_051", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field051;
        @XmlElementRef(name = "Field_052", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field052;
        @XmlElementRef(name = "Field_053", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field053;
        @XmlElement(name = "Field_054", namespace = "http://techquest.interswitchng.com/")
        protected String field054;
        @XmlElement(name = "Field_055", namespace = "http://techquest.interswitchng.com/")
        protected String field055;
        @XmlElementRef(name = "Field_056", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field056;
        @XmlElementRef(name = "Field_057", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field057;
        @XmlElementRef(name = "Field_058", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field058;
        @XmlElement(name = "Field_059", namespace = "http://techquest.interswitchng.com/")
        protected String field059;
        @XmlElementRef(name = "Field_060", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field060;
        @XmlElementRef(name = "Field_061", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field061;
        @XmlElementRef(name = "Field_062", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field062;
        @XmlElementRef(name = "Field_063", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field063;
        @XmlElement(name = "Field_064", namespace = "http://techquest.interswitchng.com/")
        protected String field064;
        @XmlElement(name = "Field_065", namespace = "http://techquest.interswitchng.com/")
        protected String field065;
        @XmlElementRef(name = "Field_066", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field066;
        @XmlElementRef(name = "Field_067", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field067;
        @XmlElementRef(name = "Field_068", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field068;
        @XmlElement(name = "Field_069", namespace = "http://techquest.interswitchng.com/")
        protected String field069;
        @XmlElementRef(name = "Field_070", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field070;
        @XmlElementRef(name = "Field_071", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field071;
        @XmlElementRef(name = "Field_072", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field072;
        @XmlElementRef(name = "Field_073", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field073;
        @XmlElement(name = "Field_074", namespace = "http://techquest.interswitchng.com/")
        protected String field074;
        @XmlElement(name = "Field_075", namespace = "http://techquest.interswitchng.com/")
        protected String field075;
        @XmlElementRef(name = "Field_076", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field076;
        @XmlElementRef(name = "Field_077", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field077;
        @XmlElementRef(name = "Field_078", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field078;
        @XmlElement(name = "Field_079", namespace = "http://techquest.interswitchng.com/")
        protected String field079;
        @XmlElementRef(name = "Field_080", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field080;
        @XmlElementRef(name = "Field_081", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field081;
        @XmlElementRef(name = "Field_082", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field082;
        @XmlElementRef(name = "Field_083", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field083;
        @XmlElement(name = "Field_084", namespace = "http://techquest.interswitchng.com/")
        protected String field084;
        @XmlElement(name = "Field_085", namespace = "http://techquest.interswitchng.com/")
        protected String field085;
        @XmlElementRef(name = "Field_086", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field086;
        @XmlElementRef(name = "Field_087", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field087;
        @XmlElementRef(name = "Field_088", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field088;
        @XmlElement(name = "Field_089", namespace = "http://techquest.interswitchng.com/")
        protected String field089;
        @XmlElementRef(name = "Field_090", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field090;
        @XmlElementRef(name = "Field_091", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field091;
        @XmlElementRef(name = "Field_092", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field092;
        @XmlElementRef(name = "Field_093", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field093;
        @XmlElement(name = "Field_094", namespace = "http://techquest.interswitchng.com/")
        protected String field094;
        @XmlElement(name = "Field_095", namespace = "http://techquest.interswitchng.com/")
        protected String field095;
        @XmlElementRef(name = "Field_096", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field096;
        @XmlElementRef(name = "Field_097", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field097;
        @XmlElementRef(name = "Field_098", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field098;
        @XmlElement(name = "Field_099", namespace = "http://techquest.interswitchng.com/")
        protected String field099;
        @XmlElementRef(name = "Field_100", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field100;
        @XmlElementRef(name = "Field_101", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field101;
        @XmlElementRef(name = "Field_102", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field102;
        @XmlElementRef(name = "Field_103", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field103;
        @XmlElement(name = "Field_104", namespace = "http://techquest.interswitchng.com/")
        protected String field104;
        @XmlElement(name = "Field_105", namespace = "http://techquest.interswitchng.com/")
        protected String field105;
        @XmlElementRef(name = "Field_106", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field106;
        @XmlElementRef(name = "Field_107", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field107;
        @XmlElementRef(name = "Field_108", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field108;
        @XmlElement(name = "Field_109", namespace = "http://techquest.interswitchng.com/")
        protected String field109;
        @XmlElementRef(name = "Field_110", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field110;
        @XmlElementRef(name = "Field_111", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field111;
        @XmlElementRef(name = "Field_112", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field112;
        @XmlElementRef(name = "Field_113", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field113;
        @XmlElement(name = "Field_114", namespace = "http://techquest.interswitchng.com/")
        protected String field114;
        @XmlElement(name = "Field_115", namespace = "http://techquest.interswitchng.com/")
        protected String field115;
        @XmlElementRef(name = "Field_116", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field116;
        @XmlElementRef(name = "Field_117", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field117;
        @XmlElementRef(name = "Field_118", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field118;
        @XmlElement(name = "Field_119", namespace = "http://techquest.interswitchng.com/")
        protected String field119;
        @XmlElementRef(name = "Field_120", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field120;
        @XmlElementRef(name = "Field_121", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field121;
        @XmlElementRef(name = "Field_122", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field122;
        @XmlElementRef(name = "Field_123", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field123;
        @XmlElement(name = "Field_124", namespace = "http://techquest.interswitchng.com/")
        protected String field124;
        @XmlElement(name = "Field_125", namespace = "http://techquest.interswitchng.com/")
        protected String field125;
        @XmlElementRef(name = "Field_126", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field126;
        @XmlElementRef(name = "Field_127_001", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127001;
        @XmlElementRef(name = "Field_127_002", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127002;
        @XmlElementRef(name = "Field_127_003", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127003;
        @XmlElementRef(name = "Field_127_004", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127004;
        @XmlElementRef(name = "Field_127_005", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127005;
        @XmlElementRef(name = "Field_127_006", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127006;
        @XmlElementRef(name = "Field_127_007", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127007;
        @XmlElementRef(name = "Field_127_008", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127008;
        @XmlElementRef(name = "Field_127_009", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127009;
        @XmlElementRef(name = "Field_127_010", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127010;
        @XmlElementRef(name = "Field_127_011", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127011;
        @XmlElementRef(name = "Field_127_012", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127012;
        @XmlElementRef(name = "Field_127_013", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127013;
        @XmlElementRef(name = "Field_127_014", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127014;
        @XmlElementRef(name = "Field_127_015", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127015;
        @XmlElementRef(name = "Field_127_016", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127016;
        @XmlElementRef(name = "Field_127_017", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127017;
        @XmlElementRef(name = "Field_127_018", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127018;
        @XmlElementRef(name = "Field_127_019", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127019;
        @XmlElementRef(name = "Field_127_020", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127020;
        @XmlElementRef(name = "Field_127_021", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127021;
        @XmlElementRef(name = "Field_127_022", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<Iso8583PostXml.Fields.Field127022> field127022;
        @XmlElementRef(name = "Field_127_023", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127023;
        @XmlElementRef(name = "Field_127_024", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127024;
        @XmlElementRef(name = "Field_127_025", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127025;
        @XmlElementRef(name = "Field_127_026", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127026;
        @XmlElementRef(name = "Field_127_027", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127027;
        @XmlElementRef(name = "Field_127_028", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127028;
        @XmlElementRef(name = "Field_127_029", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127029;
        @XmlElementRef(name = "Field_127_030", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127030;
        @XmlElementRef(name = "Field_127_031", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127031;
        @XmlElementRef(name = "Field_127_032", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127032;
        @XmlElementRef(name = "Field_127_033", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127033;
        @XmlElementRef(name = "Field_127_034", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127034;
        @XmlElementRef(name = "Field_127_035", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127035;
        @XmlElementRef(name = "Field_127_036", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127036;
        @XmlElementRef(name = "Field_127_037", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127037;
        @XmlElementRef(name = "Field_127_038", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127038;
        @XmlElementRef(name = "Field_127_039", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
        protected JAXBElement<String> field127039;

        /**
         * Gets the value of the field002 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField002() {
            return field002;
        }

        /**
         * Sets the value of the field002 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField002(JAXBElement<String> value) {
            this.field002 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field003 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField003() {
            return field003;
        }

        /**
         * Sets the value of the field003 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField003(JAXBElement<String> value) {
            this.field003 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field004 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField004() {
            return field004;
        }

        /**
         * Sets the value of the field004 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField004(String value) {
            this.field004 = value;
        }

        /**
         * Gets the value of the field005 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField005() {
            return field005;
        }

        /**
         * Sets the value of the field005 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField005(String value) {
            this.field005 = value;
        }

        /**
         * Gets the value of the field006 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField006() {
            return field006;
        }

        /**
         * Sets the value of the field006 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField006(JAXBElement<String> value) {
            this.field006 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field007 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField007() {
            return field007;
        }

        /**
         * Sets the value of the field007 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField007(JAXBElement<String> value) {
            this.field007 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field008 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField008() {
            return field008;
        }

        /**
         * Sets the value of the field008 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField008(JAXBElement<String> value) {
            this.field008 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field009 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField009() {
            return field009;
        }

        /**
         * Sets the value of the field009 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField009(String value) {
            this.field009 = value;
        }

        /**
         * Gets the value of the field010 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField010() {
            return field010;
        }

        /**
         * Sets the value of the field010 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField010(JAXBElement<String> value) {
            this.field010 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field011 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField011() {
            return field011;
        }

        /**
         * Sets the value of the field011 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField011(JAXBElement<String> value) {
            this.field011 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field012 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField012() {
            return field012;
        }

        /**
         * Sets the value of the field012 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField012(JAXBElement<String> value) {
            this.field012 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field013 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField013() {
            return field013;
        }

        /**
         * Sets the value of the field013 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField013(JAXBElement<String> value) {
            this.field013 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field014 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField014() {
            return field014;
        }

        /**
         * Sets the value of the field014 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField014(String value) {
            this.field014 = value;
        }

        /**
         * Gets the value of the field015 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField015() {
            return field015;
        }

        /**
         * Sets the value of the field015 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField015(String value) {
            this.field015 = value;
        }

        /**
         * Gets the value of the field016 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField016() {
            return field016;
        }

        /**
         * Sets the value of the field016 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField016(JAXBElement<String> value) {
            this.field016 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field017 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField017() {
            return field017;
        }

        /**
         * Sets the value of the field017 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField017(JAXBElement<String> value) {
            this.field017 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field018 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField018() {
            return field018;
        }

        /**
         * Sets the value of the field018 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField018(JAXBElement<String> value) {
            this.field018 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field019 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField019() {
            return field019;
        }

        /**
         * Sets the value of the field019 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField019(String value) {
            this.field019 = value;
        }

        /**
         * Gets the value of the field020 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField020() {
            return field020;
        }

        /**
         * Sets the value of the field020 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField020(JAXBElement<String> value) {
            this.field020 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field021 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField021() {
            return field021;
        }

        /**
         * Sets the value of the field021 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField021(JAXBElement<String> value) {
            this.field021 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field022 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField022() {
            return field022;
        }

        /**
         * Sets the value of the field022 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField022(JAXBElement<String> value) {
            this.field022 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field023 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField023() {
            return field023;
        }

        /**
         * Sets the value of the field023 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField023(JAXBElement<String> value) {
            this.field023 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field024 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField024() {
            return field024;
        }

        /**
         * Sets the value of the field024 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField024(String value) {
            this.field024 = value;
        }

        /**
         * Gets the value of the field025 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField025() {
            return field025;
        }

        /**
         * Sets the value of the field025 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField025(String value) {
            this.field025 = value;
        }

        /**
         * Gets the value of the field026 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField026() {
            return field026;
        }

        /**
         * Sets the value of the field026 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField026(JAXBElement<String> value) {
            this.field026 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field027 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField027() {
            return field027;
        }

        /**
         * Sets the value of the field027 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField027(JAXBElement<String> value) {
            this.field027 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field028 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField028() {
            return field028;
        }

        /**
         * Sets the value of the field028 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField028(JAXBElement<String> value) {
            this.field028 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field029 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField029() {
            return field029;
        }

        /**
         * Sets the value of the field029 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField029(String value) {
            this.field029 = value;
        }

        /**
         * Gets the value of the field030 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField030() {
            return field030;
        }

        /**
         * Sets the value of the field030 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField030(JAXBElement<String> value) {
            this.field030 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field031 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField031() {
            return field031;
        }

        /**
         * Sets the value of the field031 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField031(JAXBElement<String> value) {
            this.field031 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field032 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField032() {
            return field032;
        }

        /**
         * Sets the value of the field032 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField032(JAXBElement<String> value) {
            this.field032 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field033 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField033() {
            return field033;
        }

        /**
         * Sets the value of the field033 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField033(JAXBElement<String> value) {
            this.field033 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field034 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField034() {
            return field034;
        }

        /**
         * Sets the value of the field034 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField034(String value) {
            this.field034 = value;
        }

        /**
         * Gets the value of the field035 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField035() {
            return field035;
        }

        /**
         * Sets the value of the field035 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField035(String value) {
            this.field035 = value;
        }

        /**
         * Gets the value of the field036 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField036() {
            return field036;
        }

        /**
         * Sets the value of the field036 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField036(JAXBElement<String> value) {
            this.field036 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field037 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField037() {
            return field037;
        }

        /**
         * Sets the value of the field037 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField037(JAXBElement<String> value) {
            this.field037 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field038 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField038() {
            return field038;
        }

        /**
         * Sets the value of the field038 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField038(JAXBElement<String> value) {
            this.field038 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field039 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField039() {
            return field039;
        }

        /**
         * Sets the value of the field039 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField039(String value) {
            this.field039 = value;
        }

        /**
         * Gets the value of the field040 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField040() {
            return field040;
        }

        /**
         * Sets the value of the field040 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField040(JAXBElement<String> value) {
            this.field040 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field041 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField041() {
            return field041;
        }

        /**
         * Sets the value of the field041 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField041(JAXBElement<String> value) {
            this.field041 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field042 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField042() {
            return field042;
        }

        /**
         * Sets the value of the field042 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField042(JAXBElement<String> value) {
            this.field042 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field043 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField043() {
            return field043;
        }

        /**
         * Sets the value of the field043 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField043(JAXBElement<String> value) {
            this.field043 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field044 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField044() {
            return field044;
        }

        /**
         * Sets the value of the field044 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField044(String value) {
            this.field044 = value;
        }

        /**
         * Gets the value of the field045 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField045() {
            return field045;
        }

        /**
         * Sets the value of the field045 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField045(String value) {
            this.field045 = value;
        }

        /**
         * Gets the value of the field046 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField046() {
            return field046;
        }

        /**
         * Sets the value of the field046 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField046(JAXBElement<String> value) {
            this.field046 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field047 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField047() {
            return field047;
        }

        /**
         * Sets the value of the field047 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField047(JAXBElement<String> value) {
            this.field047 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field048 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField048() {
            return field048;
        }

        /**
         * Sets the value of the field048 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField048(JAXBElement<String> value) {
            this.field048 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field049 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField049() {
            return field049;
        }

        /**
         * Sets the value of the field049 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField049(String value) {
            this.field049 = value;
        }

        /**
         * Gets the value of the field050 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField050() {
            return field050;
        }

        /**
         * Sets the value of the field050 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField050(JAXBElement<String> value) {
            this.field050 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field051 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField051() {
            return field051;
        }

        /**
         * Sets the value of the field051 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField051(JAXBElement<String> value) {
            this.field051 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field052 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField052() {
            return field052;
        }

        /**
         * Sets the value of the field052 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField052(JAXBElement<String> value) {
            this.field052 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field053 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField053() {
            return field053;
        }

        /**
         * Sets the value of the field053 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField053(JAXBElement<String> value) {
            this.field053 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field054 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField054() {
            return field054;
        }

        /**
         * Sets the value of the field054 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField054(String value) {
            this.field054 = value;
        }

        /**
         * Gets the value of the field055 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField055() {
            return field055;
        }

        /**
         * Sets the value of the field055 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField055(String value) {
            this.field055 = value;
        }

        /**
         * Gets the value of the field056 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField056() {
            return field056;
        }

        /**
         * Sets the value of the field056 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField056(JAXBElement<String> value) {
            this.field056 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field057 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField057() {
            return field057;
        }

        /**
         * Sets the value of the field057 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField057(JAXBElement<String> value) {
            this.field057 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field058 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField058() {
            return field058;
        }

        /**
         * Sets the value of the field058 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField058(JAXBElement<String> value) {
            this.field058 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field059 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField059() {
            return field059;
        }

        /**
         * Sets the value of the field059 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField059(String value) {
            this.field059 = value;
        }

        /**
         * Gets the value of the field060 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField060() {
            return field060;
        }

        /**
         * Sets the value of the field060 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField060(JAXBElement<String> value) {
            this.field060 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field061 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField061() {
            return field061;
        }

        /**
         * Sets the value of the field061 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField061(JAXBElement<String> value) {
            this.field061 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field062 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField062() {
            return field062;
        }

        /**
         * Sets the value of the field062 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField062(JAXBElement<String> value) {
            this.field062 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field063 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField063() {
            return field063;
        }

        /**
         * Sets the value of the field063 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField063(JAXBElement<String> value) {
            this.field063 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field064 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField064() {
            return field064;
        }

        /**
         * Sets the value of the field064 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField064(String value) {
            this.field064 = value;
        }

        /**
         * Gets the value of the field065 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField065() {
            return field065;
        }

        /**
         * Sets the value of the field065 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField065(String value) {
            this.field065 = value;
        }

        /**
         * Gets the value of the field066 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField066() {
            return field066;
        }

        /**
         * Sets the value of the field066 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField066(JAXBElement<String> value) {
            this.field066 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field067 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField067() {
            return field067;
        }

        /**
         * Sets the value of the field067 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField067(JAXBElement<String> value) {
            this.field067 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field068 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField068() {
            return field068;
        }

        /**
         * Sets the value of the field068 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField068(JAXBElement<String> value) {
            this.field068 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field069 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField069() {
            return field069;
        }

        /**
         * Sets the value of the field069 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField069(String value) {
            this.field069 = value;
        }

        /**
         * Gets the value of the field070 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField070() {
            return field070;
        }

        /**
         * Sets the value of the field070 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField070(JAXBElement<String> value) {
            this.field070 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field071 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField071() {
            return field071;
        }

        /**
         * Sets the value of the field071 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField071(JAXBElement<String> value) {
            this.field071 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field072 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField072() {
            return field072;
        }

        /**
         * Sets the value of the field072 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField072(JAXBElement<String> value) {
            this.field072 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field073 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField073() {
            return field073;
        }

        /**
         * Sets the value of the field073 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField073(JAXBElement<String> value) {
            this.field073 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field074 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField074() {
            return field074;
        }

        /**
         * Sets the value of the field074 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField074(String value) {
            this.field074 = value;
        }

        /**
         * Gets the value of the field075 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField075() {
            return field075;
        }

        /**
         * Sets the value of the field075 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField075(String value) {
            this.field075 = value;
        }

        /**
         * Gets the value of the field076 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField076() {
            return field076;
        }

        /**
         * Sets the value of the field076 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField076(JAXBElement<String> value) {
            this.field076 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field077 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField077() {
            return field077;
        }

        /**
         * Sets the value of the field077 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField077(JAXBElement<String> value) {
            this.field077 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field078 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField078() {
            return field078;
        }

        /**
         * Sets the value of the field078 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField078(JAXBElement<String> value) {
            this.field078 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field079 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField079() {
            return field079;
        }

        /**
         * Sets the value of the field079 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField079(String value) {
            this.field079 = value;
        }

        /**
         * Gets the value of the field080 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField080() {
            return field080;
        }

        /**
         * Sets the value of the field080 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField080(JAXBElement<String> value) {
            this.field080 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field081 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField081() {
            return field081;
        }

        /**
         * Sets the value of the field081 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField081(JAXBElement<String> value) {
            this.field081 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field082 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField082() {
            return field082;
        }

        /**
         * Sets the value of the field082 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField082(JAXBElement<String> value) {
            this.field082 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field083 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField083() {
            return field083;
        }

        /**
         * Sets the value of the field083 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField083(JAXBElement<String> value) {
            this.field083 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field084 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField084() {
            return field084;
        }

        /**
         * Sets the value of the field084 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField084(String value) {
            this.field084 = value;
        }

        /**
         * Gets the value of the field085 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField085() {
            return field085;
        }

        /**
         * Sets the value of the field085 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField085(String value) {
            this.field085 = value;
        }

        /**
         * Gets the value of the field086 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField086() {
            return field086;
        }

        /**
         * Sets the value of the field086 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField086(JAXBElement<String> value) {
            this.field086 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field087 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField087() {
            return field087;
        }

        /**
         * Sets the value of the field087 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField087(JAXBElement<String> value) {
            this.field087 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field088 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField088() {
            return field088;
        }

        /**
         * Sets the value of the field088 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField088(JAXBElement<String> value) {
            this.field088 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field089 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField089() {
            return field089;
        }

        /**
         * Sets the value of the field089 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField089(String value) {
            this.field089 = value;
        }

        /**
         * Gets the value of the field090 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField090() {
            return field090;
        }

        /**
         * Sets the value of the field090 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField090(JAXBElement<String> value) {
            this.field090 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field091 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField091() {
            return field091;
        }

        /**
         * Sets the value of the field091 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField091(JAXBElement<String> value) {
            this.field091 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field092 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField092() {
            return field092;
        }

        /**
         * Sets the value of the field092 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField092(JAXBElement<String> value) {
            this.field092 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field093 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField093() {
            return field093;
        }

        /**
         * Sets the value of the field093 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField093(JAXBElement<String> value) {
            this.field093 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field094 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField094() {
            return field094;
        }

        /**
         * Sets the value of the field094 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField094(String value) {
            this.field094 = value;
        }

        /**
         * Gets the value of the field095 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField095() {
            return field095;
        }

        /**
         * Sets the value of the field095 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField095(String value) {
            this.field095 = value;
        }

        /**
         * Gets the value of the field096 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField096() {
            return field096;
        }

        /**
         * Sets the value of the field096 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField096(JAXBElement<String> value) {
            this.field096 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field097 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField097() {
            return field097;
        }

        /**
         * Sets the value of the field097 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField097(JAXBElement<String> value) {
            this.field097 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field098 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField098() {
            return field098;
        }

        /**
         * Sets the value of the field098 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField098(JAXBElement<String> value) {
            this.field098 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field099 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField099() {
            return field099;
        }

        /**
         * Sets the value of the field099 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField099(String value) {
            this.field099 = value;
        }

        /**
         * Gets the value of the field100 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField100() {
            return field100;
        }

        /**
         * Sets the value of the field100 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField100(JAXBElement<String> value) {
            this.field100 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field101 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField101() {
            return field101;
        }

        /**
         * Sets the value of the field101 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField101(JAXBElement<String> value) {
            this.field101 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field102 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField102() {
            return field102;
        }

        /**
         * Sets the value of the field102 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField102(JAXBElement<String> value) {
            this.field102 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field103 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField103() {
            return field103;
        }

        /**
         * Sets the value of the field103 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField103(JAXBElement<String> value) {
            this.field103 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field104 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField104() {
            return field104;
        }

        /**
         * Sets the value of the field104 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField104(String value) {
            this.field104 = value;
        }

        /**
         * Gets the value of the field105 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField105() {
            return field105;
        }

        /**
         * Sets the value of the field105 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField105(String value) {
            this.field105 = value;
        }

        /**
         * Gets the value of the field106 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField106() {
            return field106;
        }

        /**
         * Sets the value of the field106 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField106(JAXBElement<String> value) {
            this.field106 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field107 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField107() {
            return field107;
        }

        /**
         * Sets the value of the field107 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField107(JAXBElement<String> value) {
            this.field107 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field108 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField108() {
            return field108;
        }

        /**
         * Sets the value of the field108 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField108(JAXBElement<String> value) {
            this.field108 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field109 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField109() {
            return field109;
        }

        /**
         * Sets the value of the field109 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField109(String value) {
            this.field109 = value;
        }

        /**
         * Gets the value of the field110 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField110() {
            return field110;
        }

        /**
         * Sets the value of the field110 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField110(JAXBElement<String> value) {
            this.field110 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field111 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField111() {
            return field111;
        }

        /**
         * Sets the value of the field111 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField111(JAXBElement<String> value) {
            this.field111 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field112 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField112() {
            return field112;
        }

        /**
         * Sets the value of the field112 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField112(JAXBElement<String> value) {
            this.field112 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field113 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField113() {
            return field113;
        }

        /**
         * Sets the value of the field113 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField113(JAXBElement<String> value) {
            this.field113 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field114 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField114() {
            return field114;
        }

        /**
         * Sets the value of the field114 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField114(String value) {
            this.field114 = value;
        }

        /**
         * Gets the value of the field115 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField115() {
            return field115;
        }

        /**
         * Sets the value of the field115 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField115(String value) {
            this.field115 = value;
        }

        /**
         * Gets the value of the field116 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField116() {
            return field116;
        }

        /**
         * Sets the value of the field116 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField116(JAXBElement<String> value) {
            this.field116 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field117 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField117() {
            return field117;
        }

        /**
         * Sets the value of the field117 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField117(JAXBElement<String> value) {
            this.field117 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field118 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField118() {
            return field118;
        }

        /**
         * Sets the value of the field118 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField118(JAXBElement<String> value) {
            this.field118 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field119 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField119() {
            return field119;
        }

        /**
         * Sets the value of the field119 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField119(String value) {
            this.field119 = value;
        }

        /**
         * Gets the value of the field120 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField120() {
            return field120;
        }

        /**
         * Sets the value of the field120 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField120(JAXBElement<String> value) {
            this.field120 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field121 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField121() {
            return field121;
        }

        /**
         * Sets the value of the field121 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField121(JAXBElement<String> value) {
            this.field121 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field122 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField122() {
            return field122;
        }

        /**
         * Sets the value of the field122 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField122(JAXBElement<String> value) {
            this.field122 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field123 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField123() {
            return field123;
        }

        /**
         * Sets the value of the field123 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField123(JAXBElement<String> value) {
            this.field123 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field124 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField124() {
            return field124;
        }

        /**
         * Sets the value of the field124 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField124(String value) {
            this.field124 = value;
        }

        /**
         * Gets the value of the field125 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getField125() {
            return field125;
        }

        /**
         * Sets the value of the field125 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setField125(String value) {
            this.field125 = value;
        }

        /**
         * Gets the value of the field126 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField126() {
            return field126;
        }

        /**
         * Sets the value of the field126 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField126(JAXBElement<String> value) {
            this.field126 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127001 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127001() {
            return field127001;
        }

        /**
         * Sets the value of the field127001 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127001(JAXBElement<String> value) {
            this.field127001 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127002 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127002() {
            return field127002;
        }

        /**
         * Sets the value of the field127002 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127002(JAXBElement<String> value) {
            this.field127002 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127003 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127003() {
            return field127003;
        }

        /**
         * Sets the value of the field127003 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127003(JAXBElement<String> value) {
            this.field127003 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127004 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127004() {
            return field127004;
        }

        /**
         * Sets the value of the field127004 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127004(JAXBElement<String> value) {
            this.field127004 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127005 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127005() {
            return field127005;
        }

        /**
         * Sets the value of the field127005 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127005(JAXBElement<String> value) {
            this.field127005 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127006 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127006() {
            return field127006;
        }

        /**
         * Sets the value of the field127006 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127006(JAXBElement<String> value) {
            this.field127006 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127007 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127007() {
            return field127007;
        }

        /**
         * Sets the value of the field127007 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127007(JAXBElement<String> value) {
            this.field127007 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127008 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127008() {
            return field127008;
        }

        /**
         * Sets the value of the field127008 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127008(JAXBElement<String> value) {
            this.field127008 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127009 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127009() {
            return field127009;
        }

        /**
         * Sets the value of the field127009 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127009(JAXBElement<String> value) {
            this.field127009 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127010 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127010() {
            return field127010;
        }

        /**
         * Sets the value of the field127010 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127010(JAXBElement<String> value) {
            this.field127010 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127011 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127011() {
            return field127011;
        }

        /**
         * Sets the value of the field127011 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127011(JAXBElement<String> value) {
            this.field127011 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127012 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127012() {
            return field127012;
        }

        /**
         * Sets the value of the field127012 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127012(JAXBElement<String> value) {
            this.field127012 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127013 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127013() {
            return field127013;
        }

        /**
         * Sets the value of the field127013 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127013(JAXBElement<String> value) {
            this.field127013 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127014 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127014() {
            return field127014;
        }

        /**
         * Sets the value of the field127014 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127014(JAXBElement<String> value) {
            this.field127014 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127015 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127015() {
            return field127015;
        }

        /**
         * Sets the value of the field127015 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127015(JAXBElement<String> value) {
            this.field127015 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127016 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127016() {
            return field127016;
        }

        /**
         * Sets the value of the field127016 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127016(JAXBElement<String> value) {
            this.field127016 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127017 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127017() {
            return field127017;
        }

        /**
         * Sets the value of the field127017 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127017(JAXBElement<String> value) {
            this.field127017 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127018 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127018() {
            return field127018;
        }

        /**
         * Sets the value of the field127018 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127018(JAXBElement<String> value) {
            this.field127018 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127019 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127019() {
            return field127019;
        }

        /**
         * Sets the value of the field127019 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127019(JAXBElement<String> value) {
            this.field127019 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127020 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127020() {
            return field127020;
        }

        /**
         * Sets the value of the field127020 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127020(JAXBElement<String> value) {
            this.field127020 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127021 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127021() {
            return field127021;
        }

        /**
         * Sets the value of the field127021 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127021(JAXBElement<String> value) {
            this.field127021 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127022 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link Iso8583PostXml.Fields.Field127022 }{@code >}
         *     
         */
        public JAXBElement<Iso8583PostXml.Fields.Field127022> getField127022() {
            return field127022;
        }

        /**
         * Sets the value of the field127022 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link Iso8583PostXml.Fields.Field127022 }{@code >}
         *     
         */
        public void setField127022(JAXBElement<Iso8583PostXml.Fields.Field127022> value) {
            this.field127022 = ((JAXBElement<Iso8583PostXml.Fields.Field127022> ) value);
        }

        /**
         * Gets the value of the field127023 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127023() {
            return field127023;
        }

        /**
         * Sets the value of the field127023 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127023(JAXBElement<String> value) {
            this.field127023 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127024 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127024() {
            return field127024;
        }

        /**
         * Sets the value of the field127024 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127024(JAXBElement<String> value) {
            this.field127024 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127025 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127025() {
            return field127025;
        }

        /**
         * Sets the value of the field127025 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127025(JAXBElement<String> value) {
            this.field127025 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127026 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127026() {
            return field127026;
        }

        /**
         * Sets the value of the field127026 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127026(JAXBElement<String> value) {
            this.field127026 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127027 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127027() {
            return field127027;
        }

        /**
         * Sets the value of the field127027 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127027(JAXBElement<String> value) {
            this.field127027 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127028 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127028() {
            return field127028;
        }

        /**
         * Sets the value of the field127028 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127028(JAXBElement<String> value) {
            this.field127028 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127029 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127029() {
            return field127029;
        }

        /**
         * Sets the value of the field127029 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127029(JAXBElement<String> value) {
            this.field127029 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127030 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127030() {
            return field127030;
        }

        /**
         * Sets the value of the field127030 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127030(JAXBElement<String> value) {
            this.field127030 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127031 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127031() {
            return field127031;
        }

        /**
         * Sets the value of the field127031 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127031(JAXBElement<String> value) {
            this.field127031 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127032 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127032() {
            return field127032;
        }

        /**
         * Sets the value of the field127032 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127032(JAXBElement<String> value) {
            this.field127032 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127033 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127033() {
            return field127033;
        }

        /**
         * Sets the value of the field127033 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127033(JAXBElement<String> value) {
            this.field127033 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127034 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127034() {
            return field127034;
        }

        /**
         * Sets the value of the field127034 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127034(JAXBElement<String> value) {
            this.field127034 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127035 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127035() {
            return field127035;
        }

        /**
         * Sets the value of the field127035 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127035(JAXBElement<String> value) {
            this.field127035 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127036 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127036() {
            return field127036;
        }

        /**
         * Sets the value of the field127036 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127036(JAXBElement<String> value) {
            this.field127036 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127037 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127037() {
            return field127037;
        }

        /**
         * Sets the value of the field127037 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127037(JAXBElement<String> value) {
            this.field127037 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127038 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127038() {
            return field127038;
        }

        /**
         * Sets the value of the field127038 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127038(JAXBElement<String> value) {
            this.field127038 = ((JAXBElement<String> ) value);
        }

        /**
         * Gets the value of the field127039 property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getField127039() {
            return field127039;
        }

        /**
         * Sets the value of the field127039 property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setField127039(JAXBElement<String> value) {
            this.field127039 = ((JAXBElement<String> ) value);
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="StructureData" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="CustomerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="WithdrawalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "structureData"
        })
        public static class Field127022 {

            @XmlElementRef(name = "StructureData", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
            protected JAXBElement<Iso8583PostXml.Fields.Field127022 .StructureData> structureData;

            /**
             * Gets the value of the structureData property.
             * 
             * @return
             *     possible object is
             *     {@link JAXBElement }{@code <}{@link Iso8583PostXml.Fields.Field127022 .StructureData }{@code >}
             *     
             */
            public JAXBElement<Iso8583PostXml.Fields.Field127022 .StructureData> getStructureData() {
                return structureData;
            }

            /**
             * Sets the value of the structureData property.
             * 
             * @param value
             *     allowed object is
             *     {@link JAXBElement }{@code <}{@link Iso8583PostXml.Fields.Field127022 .StructureData }{@code >}
             *     
             */
            public void setStructureData(JAXBElement<Iso8583PostXml.Fields.Field127022 .StructureData> value) {
                this.structureData = ((JAXBElement<Iso8583PostXml.Fields.Field127022 .StructureData> ) value);
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="CustomerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="WithdrawalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "customerID",
                "withdrawalCode"
            })
            public static class StructureData {

                @XmlElementRef(name = "CustomerID", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
                protected JAXBElement<String> customerID;
                @XmlElementRef(name = "WithdrawalCode", namespace = "http://techquest.interswitchng.com/", type = JAXBElement.class)
                protected JAXBElement<String> withdrawalCode;

                /**
                 * Gets the value of the customerID property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link JAXBElement }{@code <}{@link String }{@code >}
                 *     
                 */
                public JAXBElement<String> getCustomerID() {
                    return customerID;
                }

                /**
                 * Sets the value of the customerID property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link JAXBElement }{@code <}{@link String }{@code >}
                 *     
                 */
                public void setCustomerID(JAXBElement<String> value) {
                    this.customerID = ((JAXBElement<String> ) value);
                }

                /**
                 * Gets the value of the withdrawalCode property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link JAXBElement }{@code <}{@link String }{@code >}
                 *     
                 */
                public JAXBElement<String> getWithdrawalCode() {
                    return withdrawalCode;
                }

                /**
                 * Sets the value of the withdrawalCode property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link JAXBElement }{@code <}{@link String }{@code >}
                 *     
                 */
                public void setWithdrawalCode(JAXBElement<String> value) {
                    this.withdrawalCode = ((JAXBElement<String> ) value);
                }

            }

        }

    }

}
