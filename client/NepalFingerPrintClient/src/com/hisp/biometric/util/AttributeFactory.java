/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;
import com.hisp.biometric.models.Attribute;
import java.util.Calendar;



/**
 *
 * @author Ahmed
 */
public class AttributeFactory {
    public enum ATTRIBUTE_TYPES{FID,FINGERPRINTTEMPLATE}
    
    public static Attribute getAttribute(ATTRIBUTE_TYPES type){
        Attribute atrToReturn = new Attribute();
        switch(type){
            case FID:
                atrToReturn.setAttribute(Constants.ATTRIBUTE_FID);
                atrToReturn.setCode(Constants.ATTRIBUTE_FID_CODE);
                atrToReturn.setCreated(Calendar.getInstance().getTime().toString());
                atrToReturn.setDisplayName("Fingerprint Id");
                atrToReturn.setLastUpdated(Calendar.getInstance().getTime().toString());
                atrToReturn.setStoredBy("biometric");
                atrToReturn.setValueType("INTEGER");
                break;
                
            case FINGERPRINTTEMPLATE:
                atrToReturn.setAttribute(Constants.ATTRIBUTE_TEMPLATE);
                atrToReturn.setCode(Constants.ATTRIBUTE_TEMPLATE_CODE);
                atrToReturn.setCreated(Calendar.getInstance().getTime().toString());
                atrToReturn.setDisplayName("Fingerprint String");
                atrToReturn.setLastUpdated(Calendar.getInstance().getTime().toString());
                atrToReturn.setStoredBy("biometric");
                atrToReturn.setValueType("LONG_TEXT");
                break;
        }
        return atrToReturn;
    }
    
}
