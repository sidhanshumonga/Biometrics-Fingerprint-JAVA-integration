/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

import com.google.gson.Gson;

/**
 *
 * @author Ahmed
 */
public class ClientConfiguration {
    String dhisUrl;
    String fingerprintUrl;
    transient String userName;
    transient String password;
    
    String host;
    int port;
    
    String attribute_fid ;//= "ePbX8aM22Nb";
    String attribute_fid_code;// = "fingerprint_id";
    
    String attribute_template;// = "ySaNYnlAMWL";
    String attribute_template_code; //= "fingerprint_str";
    
    String attrubute_client_code;// = "drKkLxaGFwv";
    
    String program_hiv = "L78QzNqadTV";
    
    public static ClientConfiguration getDefault(){
        ClientConfiguration cc = new ClientConfiguration();
        
        cc.dhisUrl = "http://localhost:8080/dhis";
        cc.fingerprintUrl = "http://localhost:8080/NepalFingerprintServer";
        
        cc.attribute_fid = "ePbX8aM22Nb";
        cc.attribute_fid_code = "fingerprint_id";
        
        cc.attribute_template = "ySaNYnlAMWL";
        cc.attribute_template_code = "fingerprint_str";
        
        cc.attrubute_client_code = "drKkLxaGFwv";
        cc.program_hiv = "L78QzNqadTV";
        
        cc.host = "localhost";
        cc.port = 8080;
        return cc;
    }
    
    public static ClientConfiguration fromJson(String json){
        return new Gson().fromJson(json,ClientConfiguration.class);
    }

    public String getDhisUrl() {
        return dhisUrl;
    }

    public void setDhisUrl(String dhisUrl) {
        this.dhisUrl = dhisUrl;
    }

    public String getFingerprintUrl() {
        return fingerprintUrl;
    }

    public void setFingerprintUrl(String fingerprintUrl) {
        this.fingerprintUrl = fingerprintUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    
    
    public String getAttribute_fid() {
        return attribute_fid;
    }

    public void setAttribute_fid(String attribute_fid) {
        this.attribute_fid = attribute_fid;
    }

    public String getAttribute_fid_code() {
        return attribute_fid_code;
    }

    public void setAttribute_fid_code(String attribute_fid_code) {
        this.attribute_fid_code = attribute_fid_code;
    }

    public String getAttribute_template() {
        return attribute_template;
    }

    public void setAttribute_template(String attribute_template) {
        this.attribute_template = attribute_template;
    }

    public String getAttribute_template_code() {
        return attribute_template_code;
    }

    public void setAttribute_template_code(String attribute_template_code) {
        this.attribute_template_code = attribute_template_code;
    }

    public String getAttrubute_client_code() {
        return attrubute_client_code;
    }

    public void setAttrubute_client_code(String attrubute_client_code) {
        this.attrubute_client_code = attrubute_client_code;
    }

    public String getProgram_hiv() {
        return program_hiv;
    }

    public void setProgram_hiv(String program_hiv) {
        this.program_hiv = program_hiv;
    }
    
    
    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
            
}
