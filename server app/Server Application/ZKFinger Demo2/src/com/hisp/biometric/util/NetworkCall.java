/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

import com.hisp.biometric.login.LoginCredentials;
import com.hisp.biometric.main.FingerPrint;
import com.hisp.biometric.response.QueryResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *This class to be used only in client application 
 * @author Ahmed
 */
public class NetworkCall {
    public static LoginCredentials lc;
    public static final String DEFAULT_DOMAIN= "http://localhost:8080/fingerprint";
    public static final String API_SUFFIX = "/api/identify";
    public static String apiUrl;
    
    public static String dhis_domain;
    public static final String LATES_FID_SUFFIX = "/api/sqlViews/";//Ugohq30jgpi/data";
    public static String LATEST_FID_URL ;  //= "http://localhost:8080/dhis/api/sqlViews/Ugohq30jgpi/data";
    public static final String ALL_TEI_DATA_SUFFIX = "/api/trackedEntityInstances/query.json?ouMode=ALL";
    public static String ALL_TEI_DATA_URL; // = "http://localhost:8080/dhis/api/trackedEntityInstances/query.json?ouMode=ALL&attribute=ySaNYnlAMWL&attribute=ePbX8aM22Nb";
    
    private static ServerConfiguration config;
    
    //this method was used with client earlier
    public static void initString(LoginCredentials lcp){
        lc = lcp;
        String domain=lc.getUrl();
        if(domain==null){
            domain=DEFAULT_DOMAIN;
        }
        apiUrl=domain+API_SUFFIX;
    }
    
    public static void init(){
        config = ConfigurationAccess.getServerConfiguration();
        if(config!=null){
            dhis_domain = config.getDhisUrl();
            LATEST_FID_URL = dhis_domain+LATES_FID_SUFFIX+config.getSqlViewID()+"/data?paging=false";
            ALL_TEI_DATA_URL = dhis_domain+ALL_TEI_DATA_SUFFIX+"&attribute="
                    +config.getFingerprintStringAttribute()+"&attribute="+config.getFidAttribute();
            
            
        }else{
            System.out.println("Configuration File not found Default would be loaded ");
            config = ServerConfiguration.getDefault();
            ConfigurationAccess.saveServerConfiguration(config);
             dhis_domain = config.getDhisUrl();
            LATEST_FID_URL = dhis_domain+LATES_FID_SUFFIX+config.getSqlViewID()+"/data?paging=false";
            ALL_TEI_DATA_URL = dhis_domain+ALL_TEI_DATA_SUFFIX+"&attribute="
                    +config.getFingerprintStringAttribute()+"&attribute="+config.getFidAttribute();
        }
        System.out.println("Dhis Domain : "+dhis_domain);
        
    }
    
    public static FingerPrint sendEnrollment(String template) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        FingerPrint fp = new FingerPrint();
        fp.setTemplate(template);
        fp.setFid(-1);
        try{

            HttpPut request = new HttpPut(apiUrl);
            
            String json = fp.toString();
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            request.setHeader("Content-type","application/json");
            
            
            
            //Future<HttpResponse> future = httpclient.execute(request,null);
            //HttpResponse response = future.get();
            CloseableHttpResponse response = httpclient.execute(request);
            if(response.getStatusLine().getStatusCode()==200){
                //System.out.println(response.getStatusLine());
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String responseStr = "";
                while(reader.ready()){
                    responseStr +=reader.readLine();
                }
                fp = FingerPrint.fromJson(responseStr);
                //System.out.println(responseStr);
            }else{
                System.out.println("Failed request");
            }
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fp;
    }
    
    public static FingerPrint recognize(String template){
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
        FingerPrint fp = new FingerPrint();
        fp.setTemplate(template);
        
        try{
            
            HttpPost request = new HttpPost(apiUrl);
            
            String json = fp.toString();
            
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            request.setHeader("Content-type","application/json");
            
            
            
            //Future<HttpResponse> future = httpclient.execute(request,null);
            //HttpResponse response = future.get();
            CloseableHttpResponse response = httpclient.execute(request);
            if(response.getStatusLine().getStatusCode()==200){
                //System.out.println(response.getStatusLine());
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String responseStr = "";
                while(reader.ready()){
                    responseStr +=reader.readLine();
                }
                fp = FingerPrint.fromJson(responseStr);
                //System.out.println(responseStr);
            }else{
                System.out.println("Failed request");
                return null;
            }
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return fp;
    }
    
    public static int getLatestFid(){
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        HttpHost targetHost = new HttpHost(config.getHost(), config.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("hispdev", "Devhisp@1"));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        //System.out.println(LATEST_FID_URL);
        HttpGet httpget = new HttpGet(LATEST_FID_URL);
        System.out.println("LT FID URL :"+LATEST_FID_URL);
        //for (int i = 0; i < 3; i++) {
            
        try {
            CloseableHttpResponse response = httpclient.execute(
                targetHost, httpget, context);
            HttpEntity entity = response.getEntity();
            String st = EntityUtils.toString(entity);
            QueryResponse responseMapped = QueryResponse.fromJson(st);
            System.out.println("-------------------------------");
            //System.out.println(st);
            System.out.println("Latest FID "+responseMapped.getFID());
            response.close();
            httpclient.close();
            return responseMapped.getFID();
        } catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return -1;
        }
        //}
        
    }
    
    public static long getLatestFid3(){
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("localhost:8080/dhis",8080),
                new UsernamePasswordCredentials("hispdev", "Devhisp@1"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpget = new HttpGet(LATEST_FID_URL);

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
            httpclient.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
        return 1;
    }
    
    public static long getLatestFid2(){
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("hispdev", "Devhisp@1");
        provider.setCredentials(AuthScope.ANY, credentials);
        
        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();
        //CloseableHttpClient httpclient = HttpClients.createDefault();
        
        try{
            
           // HttpGet request = new HttpGet(LATEST_FID_URL);
            //System.out.println("Recoginze -> "+apiUrl)
            //HttpClientContext context = getContext();
            //CloseableHttpResponse response = httpclient.execute(request,context);
            CloseableHttpResponse response = httpclient.execute(new HttpGet(LATEST_FID_URL));
            //System.out.println(response.toString());
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String responseStr = "";
                while(reader.ready()){
                    responseStr +=reader.readLine();
                }
                System.out.println(responseStr);
                //fp = FingerPrint.fromJson(responseStr);
                QueryResponse lidResponse = QueryResponse.fromJson(responseStr);
                return lidResponse.getFID();
                
            }else{
                System.out.println("Failed request");
                return -1;
            }
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    
    public static List<FingerPrint> getAllFingerPrints(){
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        HttpHost targetHost = new HttpHost(config.getHost(), config.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("hispdev", "Devhisp@1"));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        HttpGet httpget = new HttpGet(ALL_TEI_DATA_URL);
        //for (int i = 0; i < 3; i++) {
            
        try {
            CloseableHttpResponse response = httpclient.execute(
                targetHost, httpget, context);
            HttpEntity entity = response.getEntity();
            String st = EntityUtils.toString(entity);
            QueryResponse responseMapped = QueryResponse.fromJson(st);
            System.out.println("-------------------------------");
            //System.out.println(st);
            
            response.close();
            httpclient.close();
            return responseMapped.getFingerPrints();
            //return responseMapped.getFID();
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    
     private static HttpClientContext getContext(){
        //String usrpasscombine = lc.getUsername()+":"+lc.getPassword();
        String usrpasscombine = "hispdev:Devhisp@1";
        CredentialsProvider crdProvider = new BasicCredentialsProvider();
        crdProvider.setCredentials(AuthScope.ANY, new NTCredentials(usrpasscombine));
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(crdProvider);
        return context;
    }
}
