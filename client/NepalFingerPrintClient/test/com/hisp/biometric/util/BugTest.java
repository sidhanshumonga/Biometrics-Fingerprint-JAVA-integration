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
public class BugTest {
    public static void main(String[] args){
        
        NetworkCall.clientConfiguration =  ConfigurationAccess.getClientConfiguration();
        NetworkCall.init();;
        System.out.println(NetworkCall.getTrackedEntityInstanceWithfid("7"));
                
    }
}
