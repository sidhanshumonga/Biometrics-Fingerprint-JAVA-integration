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
public class ConfigurationAccessTest {
    public static void main(String[] args){
        ConfigurationAccess.saveClientConfiguration(ClientConfiguration.getDefault());
        System.out.println(ConfigurationAccess.getClientConfiguration().getDhisUrl());
        
    }
}
