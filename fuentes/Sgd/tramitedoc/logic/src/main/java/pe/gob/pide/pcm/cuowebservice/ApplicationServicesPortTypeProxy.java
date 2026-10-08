package pe.gob.pide.pcm.cuowebservice;

public class ApplicationServicesPortTypeProxy implements pe.gob.pide.pcm.cuowebservice.ApplicationServicesPortType {
  private String _endpoint = null;
  private pe.gob.pide.pcm.cuowebservice.ApplicationServicesPortType applicationServicesPortType = null;
  
  public ApplicationServicesPortTypeProxy() {
    _initApplicationServicesPortTypeProxy();
  }
  
  public ApplicationServicesPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initApplicationServicesPortTypeProxy();
  }
  
  private void _initApplicationServicesPortTypeProxy() {
    try {
      applicationServicesPortType = (new pe.gob.pide.pcm.cuowebservice.ApplicationServicesLocator()).getApplicationServicesPort();
      if (applicationServicesPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)applicationServicesPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)applicationServicesPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (applicationServicesPortType != null)
      ((javax.xml.rpc.Stub)applicationServicesPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public pe.gob.pide.pcm.cuowebservice.ApplicationServicesPortType getApplicationServicesPortType() {
    if (applicationServicesPortType == null)
      _initApplicationServicesPortTypeProxy();
    return applicationServicesPortType;
  }
  
  public java.lang.String getCUOEntidad(java.lang.String ruc, java.lang.String servicio) throws java.rmi.RemoteException{
    if (applicationServicesPortType == null)
      _initApplicationServicesPortTypeProxy();
    return applicationServicesPortType.getCUOEntidad(ruc, servicio);
  }
  
  public java.lang.String getCUO(java.lang.String ip) throws java.rmi.RemoteException{
    if (applicationServicesPortType == null)
      _initApplicationServicesPortTypeProxy();
    return applicationServicesPortType.getCUO(ip);
  }
  
  
}