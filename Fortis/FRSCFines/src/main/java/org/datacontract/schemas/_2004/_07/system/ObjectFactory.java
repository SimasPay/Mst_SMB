
package org.datacontract.schemas._2004._07.system;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.system package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArrayOfEntityKeyMember_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "ArrayOfEntityKeyMember");
    private final static QName _EntityKey_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "EntityKey");
    private final static QName _EntityKeyMember_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "EntityKeyMember");
    private final static QName _EntityKeyEntitySetName_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "EntitySetName");
    private final static QName _EntityKeyEntityContainerName_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "EntityContainerName");
    private final static QName _EntityKeyEntityKeyValues_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "EntityKeyValues");
    private final static QName _EntityKeyMemberKey_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "Key");
    private final static QName _EntityKeyMemberValue_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data", "Value");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.system
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EntityKey }
     * 
     */
    public EntityKey createEntityKey() {
        return new EntityKey();
    }

    /**
     * Create an instance of {@link ArrayOfEntityKeyMember }
     * 
     */
    public ArrayOfEntityKeyMember createArrayOfEntityKeyMember() {
        return new ArrayOfEntityKeyMember();
    }

    /**
     * Create an instance of {@link EntityKeyMember }
     * 
     */
    public EntityKeyMember createEntityKeyMember() {
        return new EntityKeyMember();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfEntityKeyMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "ArrayOfEntityKeyMember")
    public JAXBElement<ArrayOfEntityKeyMember> createArrayOfEntityKeyMember(ArrayOfEntityKeyMember value) {
        return new JAXBElement<ArrayOfEntityKeyMember>(_ArrayOfEntityKeyMember_QNAME, ArrayOfEntityKeyMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "EntityKey")
    public JAXBElement<EntityKey> createEntityKey(EntityKey value) {
        return new JAXBElement<EntityKey>(_EntityKey_QNAME, EntityKey.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityKeyMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "EntityKeyMember")
    public JAXBElement<EntityKeyMember> createEntityKeyMember(EntityKeyMember value) {
        return new JAXBElement<EntityKeyMember>(_EntityKeyMember_QNAME, EntityKeyMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "EntitySetName", scope = EntityKey.class)
    public JAXBElement<String> createEntityKeyEntitySetName(String value) {
        return new JAXBElement<String>(_EntityKeyEntitySetName_QNAME, String.class, EntityKey.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "EntityContainerName", scope = EntityKey.class)
    public JAXBElement<String> createEntityKeyEntityContainerName(String value) {
        return new JAXBElement<String>(_EntityKeyEntityContainerName_QNAME, String.class, EntityKey.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfEntityKeyMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "EntityKeyValues", scope = EntityKey.class)
    public JAXBElement<ArrayOfEntityKeyMember> createEntityKeyEntityKeyValues(ArrayOfEntityKeyMember value) {
        return new JAXBElement<ArrayOfEntityKeyMember>(_EntityKeyEntityKeyValues_QNAME, ArrayOfEntityKeyMember.class, EntityKey.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "Key", scope = EntityKeyMember.class)
    public JAXBElement<String> createEntityKeyMemberKey(String value) {
        return new JAXBElement<String>(_EntityKeyMemberKey_QNAME, String.class, EntityKeyMember.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data", name = "Value", scope = EntityKeyMember.class)
    public JAXBElement<Object> createEntityKeyMemberValue(Object value) {
        return new JAXBElement<Object>(_EntityKeyMemberValue_QNAME, Object.class, EntityKeyMember.class, value);
    }

}
