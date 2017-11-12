/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.models;

import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author Ahmed
 */
public class LoginResponse {
    String name;
    String displayName;
    List<OrganizationUnit> organisationUnits;
    public static LoginResponse fromJson(String json){
        return new Gson().fromJson(json,LoginResponse.class);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<OrganizationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    public void setOrganisationUnits(List<OrganizationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }
    
    
    
    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
    
    
}
