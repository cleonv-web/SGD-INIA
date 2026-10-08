/**
 * ApplicationServicesLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package pe.gob.pide.pcm.cuowebservice;

public class ApplicationServicesLocator extends org.apache.axis.client.Service implements pe.gob.pide.pcm.cuowebservice.ApplicationServices {

    public ApplicationServicesLocator() {
    }


    public ApplicationServicesLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ApplicationServicesLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ApplicationServicesPort
    private java.lang.String ApplicationServicesPort_address = "http://pcm.pide.gob.pe/cuowebservice/serverTramite.php";

    public java.lang.String getApplicationServicesPortAddress() {
        return ApplicationServicesPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ApplicationServicesPortWSDDServiceName = "ApplicationServicesPort";

    public java.lang.String getApplicationServicesPortWSDDServiceName() {
        return ApplicationServicesPortWSDDServiceName;
    }

    public void setApplicationServicesPortWSDDServiceName(java.lang.String name) {
        ApplicationServicesPortWSDDServiceName = name;
    }

    public pe.gob.pide.pcm.cuowebservice.ApplicationServicesPortType getApplicationServicesPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ApplicationServicesPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getApplicationServicesPort(endpoint);
    }

    public pe.gob.pide.pcm.cuowebservice.ApplicationServicesPortType getApplicationServicesPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            pe.gob.pide.pcm.cuowebservice.ApplicationServicesBindingStub _stub = new pe.gob.pide.pcm.cuowebservice.ApplicationServicesBindingStub(portAddress, this);
            _stub.setPortName(getApplicationServicesPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setApplicationServicesPortEndpointAddress(java.lang.String address) {
        ApplicationServicesPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (pe.gob.pide.pcm.cuowebservice.ApplicationServicesPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                pe.gob.pide.pcm.cuowebservice.ApplicationServicesBindingStub _stub = new pe.gob.pide.pcm.cuowebservice.ApplicationServicesBindingStub(new java.net.URL(ApplicationServicesPort_address), this);
                _stub.setPortName(getApplicationServicesPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ApplicationServicesPort".equals(inputPortName)) {
            return getApplicationServicesPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://pcm.pide.gob.pe/cuowebservice", "ApplicationServices");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://pcm.pide.gob.pe/cuowebservice", "ApplicationServicesPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ApplicationServicesPort".equals(portName)) {
            setApplicationServicesPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
