
package org.datacontract.schemas._2004._07.paymentlibrary;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ItemType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DEMURRAGE"/>
 *     &lt;enumeration value="OFFENCES"/>
 *     &lt;enumeration value="SCRIPTS"/>
 *     &lt;enumeration value="USERS"/>
 *     &lt;enumeration value="ROUTES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ItemType")
@XmlEnum
public enum ItemType {

    DEMURRAGE,
    OFFENCES,
    SCRIPTS,
    USERS,
    ROUTES;

    public String value() {
        return name();
    }

    public static ItemType fromValue(String v) {
        return valueOf(v);
    }

}
