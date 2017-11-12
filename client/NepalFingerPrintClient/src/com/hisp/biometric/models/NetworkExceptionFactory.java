/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.models;

/**
 *
 * @author Ahmed
 */
public class NetworkExceptionFactory {
    public static final int REQUEST_TIMEOUT = 408;
    public static final int CONFLICT = 409;
    public static final int NOT_FOUND = 404;
    
    public static final int BAD_REQUEST = 400;
    public static final int UN_AUTHORIZED = 401;
    
    
    public static NetworkException getException(int code){
        switch(code){
            case REQUEST_TIMEOUT:
                return new NetworkException("Request timeout please try again",code);
                
                
            case CONFLICT:
                return new NetworkException("Conflict Occured check the inputs",code);
               
                
            case NOT_FOUND:
                return new NetworkException("Requested URL not found (Configuration error)",code);
                               
            case BAD_REQUEST:
                return new NetworkException("Bad Request",code);
                
            case UN_AUTHORIZED:
                return new NetworkException("Un authorized for the request contact administrator",code);
            default:
                System.out.println("Exception id" + code);
                return new NetworkException("Un handled exception occurred while connecting ",code);
                
                        
        }
    }
    
    public static NetworkException getCustomException(String message){
        return new NetworkException(message,-1);
    }
    
    
}
