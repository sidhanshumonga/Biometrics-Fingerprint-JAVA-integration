/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.models;



/**
 *
 * @author Ahmed
 */
import com.google.gson.Gson;
import java.util.List;
public class TrackedEntityInstancesResponse {
    List<TrackedEntityInstance> trackedEntityInstances; 
    
    public static TrackedEntityInstancesResponse fromJson(String strJson){
        return new Gson().fromJson(strJson, TrackedEntityInstancesResponse.class);
    }
    
    public TrackedEntityInstance getTEI(int index){
        if(index>=trackedEntityInstances.size()){
            return null;
        }else{
            return trackedEntityInstances.get(index);
        }
    }
}
