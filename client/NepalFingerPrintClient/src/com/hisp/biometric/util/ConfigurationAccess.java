/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed
 */
public class ConfigurationAccess {
    public static final String FILE_NAME ="cdhis.config";
    
    public static String getURL(){
        String content = null;
        try {
            Scanner scn = new Scanner(new File(FILE_NAME)).useDelimiter("\\Z");
            if(scn.hasNext()){
                content = scn.next();
            }
            //System.out.println(content);
            
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return content;
    }
    
    public static ClientConfiguration getClientConfiguration(){
        ClientConfiguration content = null;
        try {
            Scanner scn = new Scanner(new File(FILE_NAME)).useDelimiter("\\Z");
            String response="";
            if(scn.hasNext()){
                response = scn.next();
            }
            //System.out.println(content);
            content = ClientConfiguration.fromJson(response);
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return content;
    }
    public static boolean saveClientConfiguration(ClientConfiguration cc){
        try {
            PrintWriter writer = new PrintWriter(FILE_NAME,"UTF-8");
            //System.out.println("SAving : "+url);
            writer.print(cc.toString());
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static boolean saveURL(String url){
        try {
            PrintWriter writer = new PrintWriter(FILE_NAME,"UTF-8");
            //System.out.println("SAving : "+url);
            writer.print(url);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static String getDHISUrl(){
        return null;
    }
}
