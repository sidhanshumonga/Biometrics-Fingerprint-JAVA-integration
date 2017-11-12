/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.response;

import com.google.gson.Gson;
import com.hisp.biometric.main.FingerPrint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ahmed
 */
public class QueryResponse {
    String title;
    List<List<String>> rows;
    
    public int getFID(){
        if(rows.size()>0){
            if(rows.get(0).size()>1){
                try{
                    System.out.println(rows.get(0).get(1));
                    return Integer.parseInt(rows.get(0).get(1));
                }catch(Exception ex){
                    System.out.println("Error converting to int");
                    return -1;
                }
            }
        }else if (rows.size()==0){
            return 0;
        }
        
        return -1;
    }
    
    public List<FingerPrint> getFingerPrints(){
        ArrayList<FingerPrint> fingerprints = new ArrayList<FingerPrint>();
        for(List<String> row :rows){
            FingerPrint fp = new FingerPrint();
            String template = row.get(7);
            String fidStr = row.get(8);
            if(!fidStr.equals("")&& !template.equals("")){
                try{
                    int fid = Integer.parseInt(fidStr);
                    fp.setTemplate(template);
                    fp.setFid(fid);
                    fingerprints.add(fp);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else {
            }
            
            
        }
        return fingerprints;
    }
    
    public static QueryResponse fromJson(String strJson){
        return new Gson().fromJson(strJson, QueryResponse.class);
        
    }
    
}
