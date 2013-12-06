
package org.datacontract.schemas._2004._07.system_data_objects;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.datacontract.schemas._2004._07.system.EntityKey;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.system_data_objects package. 
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

    private final static QName _EntityObject_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", "EntityObject");
    private final static QName _StructuralObject_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", "StructuralObject");
    private final static QName _EntityObjectEntityKey_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", "EntityKey");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.system_data_objects
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EntityObject }
     * 
     */
    public EntityObject createEntityObject() {
        return new EntityObject();
    }

    /**
     * Create an instance of {@link StructuralObject }
     * 
     */
    public StructuralObject createStructuralObject() {
        return new StructuralObject();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", name = "EntityObject")
    public JAXBElement<EntityObject> createEntityObject(EntityObject value) {
        return new JAXBElement<EntityObject>(_EntityObject_QNAME, EntityObject.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StructuralObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", name = "StructuralObject")
    public JAXBElement<StructuralObject> createStructuralObject(StructuralObject value) {
        return new JAXBElement<StructuralObject>(_StructuralObject_QNAME, StructuralObject.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Data.Objects.DataClasses", name = "EntityKey", scope = EntityObject.class)
    public JAXBElement<EntityKey> createEntityObjectEntityKey(EntityKey value) {
        return new JAXBElement<EntityKey>(_EntityObjectEntityKey_QNAME, EntityKey.class, EntityObject.class, value);
    }

}
