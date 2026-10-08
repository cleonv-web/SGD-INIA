/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.autentica.model;

import java.io.Serializable;

/**
 *
 * @author crosales
 */
public class RemoteAttribs implements Serializable{
    private String ipCliente;
    private String remoteHost;
    private String remoteUser;
    private String sessionId;

    public String getIpCliente() {
        return ipCliente;
    }

    public void setIpCliente(String ipCliente) {
        this.ipCliente = ipCliente;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
