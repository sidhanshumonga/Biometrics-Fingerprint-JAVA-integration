/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

import java.awt.Desktop;
import java.net.URL;

/**
 *
 * @author Ahmed
 */
public class BrowserConnection {
    
    public static boolean launchBroswserfor(URL url){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(url.toURI());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }else{
            System.out.println("Failed to get Desktop");
            return false;
        }
    }

    
    
}
