
package pe.gob.segdi.pide.wsentidad.qa;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import pe.gob.segdi.pide.wsentidad.validarEntidad;
import pe.gob.segdi.pide.wsentidad.validarEntidadResponse;
import pe.gob.segdi.wsentidad.ws.EntidadBean;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the pe.gob.segdi.wsentidad.qa package. 
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
    private final static QName _ValidarEntidad_QNAME = new QName("http://ws.wsentidad.segdi.gob.pe/", "validarEntidad"); 
    private final static QName _ValidarEntidadResponse_QNAME = new QName("http://ws.wsentidad.segdi.gob.pe/", "validarEntidadResponse");
     
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pe.gob.segdi.wsentidad.qa
     * 
     */
    public ObjectFactory() {
    }

    

    /**
     * Create an instance of {@link ValidarEntidad }
     * 
     */
    public validarEntidad createValidarEntidad() {
        return new validarEntidad();
    }

    

    /**
     * Create an instance of {@link ValidarEntidadResponse }
     * 
     */
    public validarEntidadResponse createValidarEntidadResponse() {
        return new validarEntidadResponse();
    }

  

    /**
     * Create an instance of {@link EntidadBean }
     * 
     */
    public EntidadBean createEntidadBean() {
        return new EntidadBean();
    }
 
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidarEntidad }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.wsentidad.segdi.gob.pe/", name = "validarEntidad")
    public JAXBElement<validarEntidad> createValidarEntidad(validarEntidad value) {
        return new JAXBElement<validarEntidad>(_ValidarEntidad_QNAME, validarEntidad.class, null, value);
    }
 
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidarEntidadResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.wsentidad.segdi.gob.pe/", name = "validarEntidadResponse")
    public JAXBElement<validarEntidadResponse> createValidarEntidadResponse(validarEntidadResponse value) {
        return new JAXBElement<validarEntidadResponse>(_ValidarEntidadResponse_QNAME, validarEntidadResponse.class, null, value);
    }

    

}
