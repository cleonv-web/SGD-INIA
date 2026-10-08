/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.segdi.pide.wsentidad;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
/**
 *
 * @author mvaldera
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _validarEntidad_QNAME = new QName("http://ws.wsentidad.segdi.gob.pe/", "validarEntidad");
    private final static QName _validarEntidadResponse_QNAME = new QName("http://ws.wsentidad.segdi.gob.pe/", "validarEntidadResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pe.gob.segdi.wsentidad.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetListaEntidadResponse }
     * 
     */
    public validarEntidadResponse createvalidarEntidadResponse() {
        return new validarEntidadResponse();
    }

    /**
     * Create an instance of {@link GetListaEntidad }
     * 
     */
    public validarEntidad createvalidarEntidad() {
        return new validarEntidad();
    }

    /**
     * Create an instance of {@link EntidadBean }
     * 
     */
//    public EntidadBean createEntidadBean() {
//        return new EntidadBean();
//    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetListaEntidad }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.wsentidad.segdi.gob.pe/", name = "validarEntidad")
    public JAXBElement<validarEntidad> createValidarEntidad(validarEntidad value) {
        return new JAXBElement<validarEntidad>(_validarEntidad_QNAME, validarEntidad.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetListaEntidadResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.wsentidad.segdi.gob.pe/", name = "validarEntidadResponse")
    public JAXBElement<validarEntidadResponse> createValidarResponse(validarEntidadResponse value) {
        return new JAXBElement<validarEntidadResponse>(_validarEntidadResponse_QNAME, validarEntidadResponse.class, null, value);
    }

}

