/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Ahmed
 */
public class BrowserLaunchTest {
    public static void main(String[] args) throws MalformedURLException{
        BrowserConnection.launchBroswserfor(new URL("http://www.google.com"));
    }
}
