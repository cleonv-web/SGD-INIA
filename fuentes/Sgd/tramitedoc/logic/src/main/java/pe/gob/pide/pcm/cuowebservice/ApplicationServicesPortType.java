/**
 * ApplicationServicesPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package pe.gob.pide.pcm.cuowebservice;

public interface ApplicationServicesPortType extends java.rmi.Remote {

    /**
     * Retorna string CUO
     */
    public java.lang.String getCUOEntidad(java.lang.String ruc, java.lang.String servicio) throws java.rmi.RemoteException;

    /**
     * Retorna string CUO
     */
    public java.lang.String getCUO(java.lang.String ip) throws java.rmi.RemoteException;
}
