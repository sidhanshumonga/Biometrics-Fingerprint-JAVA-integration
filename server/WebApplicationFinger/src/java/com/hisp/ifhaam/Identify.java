/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.ifhaam;

import com.hisp.ifhaam.model.FingerPrint;
import com.zkteco.biometric.FingerPrintApplicationServerInstance;
import com.zkteco.biometric.FingerprintSensorEx;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Ahmed
 */
@Path("identify")
public class Identify {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Identify
     */
    public Identify() {
    }

    /**
     * Retrieves representation of an instance of com.hisp.ifhaam.Identify
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(String data) {
        //TODO return proper representation object
        return FingerPrintApplicationServerInstance.getInstance().recognize(data);
    }

    /**
     * PUT method for updating or creating an instance of Identify
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(String content) {
       return FingerPrintApplicationServerInstance.getInstance().enroll(content);
    }
}
