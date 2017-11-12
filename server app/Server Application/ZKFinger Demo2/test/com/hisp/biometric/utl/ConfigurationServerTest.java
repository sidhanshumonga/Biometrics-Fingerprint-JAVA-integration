/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.utl;

import com.hisp.biometric.util.ConfigurationAccess;
import com.hisp.biometric.util.ServerConfiguration;

/**
 *
 * @author Ahmed
 */
public class ConfigurationServerTest {
    public static void main(String[] args){
        //System.out.println(ServerConfiguration.getDefault().toString());
        ConfigurationAccess.saveServerConfiguration(ServerConfiguration.getDefault());
    }
}
