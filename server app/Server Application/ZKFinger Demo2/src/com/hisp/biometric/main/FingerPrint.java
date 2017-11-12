/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.main;

import com.google.gson.Gson;


/**
 *
 * @author Ahmed
 */
public class FingerPrint {
    int fid;
    String template;
    
    public static FingerPrint fromJson(String json){
        return new Gson().fromJson(json, FingerPrint.class);
    }

    public FingerPrint(int fid, String template) {
        this.fid = fid;
        this.template = template;
    }

    public FingerPrint() {
    }

    
    
    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
                
    public String toString(){
        return new Gson().toJson(this);
    }
}
