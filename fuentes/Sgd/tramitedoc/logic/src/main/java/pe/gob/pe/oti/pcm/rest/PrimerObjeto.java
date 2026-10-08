/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.pe.oti.pcm.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ypino
 */
@Path("/primero")
public class PrimerObjeto {
    
    @GET
    @Produces (MediaType.APPLICATION_JSON)
    public String primero()
    {
        return "Priemro REST";
    }
}
