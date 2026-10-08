/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.segdi.pide.wsentidad;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author mvaldera
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validarEntidadResponse", propOrder = {
    "_return"
})
public class validarEntidadResponse {
    @XmlElement(name = "return")
    protected String _return;
    
    
    public String getReturn() {
        if (_return == null) {
            _return = "-1";
        }
        return this._return;
    }
}
