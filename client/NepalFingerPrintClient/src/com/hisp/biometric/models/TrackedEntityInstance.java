/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.models;

import com.google.gson.Gson;
import com.hisp.biometric.util.Constants;
import java.util.List;

/**
 *
 * @author Ahmed
 */
public class TrackedEntityInstance {
    String orgUnit;
    String trackedEntityInstance;
    List<Attribute> attributes;

    
    public static TrackedEntityInstance fromJson(String jsonStr){
        return new Gson().fromJson(jsonStr, TrackedEntityInstance.class);
    }
    
    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public String getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
    
    public void addAttribute(Attribute atr){
        this.attributes.add(atr);
        
    }
    
    public void removeAttribute(Attribute atr){
        this.attributes.remove(atr);
        
    }
    
    public void removeAttribute(int index){
        this.attributes.remove(index);
        
    }
    
    public String getLastName(){
        for(Attribute atr :attributes){
            if(atr.getDisplayName().equalsIgnoreCase("Last name")){
                return atr.getValue();
            }
        }
        return "";
    }
    
    public String getCode(){
        for(Attribute atr :attributes){
            if(atr.getDisplayName().equalsIgnoreCase("Client code")){
                return atr.getValue();
            }
        }
        
        return "";
    }
    
    public Attribute getFID(){
        for(Attribute atr:attributes){
            if(atr.getAttribute().equals(Constants.ATTRIBUTE_FID))
                return atr;
        }
        return null;
    }
    
    public Attribute getTemplate(){
        for(Attribute atr:attributes){
            if(atr.getAttribute().equals(Constants.ATTRIBUTE_TEMPLATE))
                return atr;
        }
        
        return null;
    }
    
    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
}
