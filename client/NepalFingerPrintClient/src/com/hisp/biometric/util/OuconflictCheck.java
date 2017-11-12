/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

/**
 *
 * @author Ahmed
 */
public class OuconflictCheck {
    public static void check(){
        try{
            NetworkCall.getTrackedEntityInstanceWithfid("1");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
}
