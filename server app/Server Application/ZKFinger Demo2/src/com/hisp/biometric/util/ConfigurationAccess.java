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
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed
 */
public class ConfigurationAccess {
    public static final String FILE_NAME ="dhis.config";
    public static final String HOME_VARIABLE="DHF_HOME";
    public static ServerConfiguration getServerConfiguration(){
        String content = null;
        try {
            
            if(System.getenv(HOME_VARIABLE)==null)System.out.println("Configuration Path Not found");
            String path = System.getenv(HOME_VARIABLE)+"\\"+FILE_NAME;
            Scanner scn = new Scanner(new File(path)).useDelimiter("\\Z");
            if(scn.hasNext()){
                content = scn.next();
            }
            //System.out.println(content);
            
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ConfigurationAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return ServerConfiguration.fromJson(content);
    }

    public static boolean saveServerConfiguration(ServerConfiguration config){
        try {
            if(System.getenv(HOME_VARIABLE)==null)System.out.println("Configuration Path Not found");
            String path = System.getenv(HOME_VARIABLE)+"\\"+FILE_NAME;
            System.out.println(path);
            PrintWriter writer = new PrintWriter(path,"UTF-8");
            //System.out.println("SAving : "+url);
            writer.print(config.toString());
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
}
