/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.util;

import com.hisp.biometric.login.LoginCredentials;
import com.hisp.biometric.models.FingerPrint;
import com.hisp.biometric.models.LoginResponse;
import com.hisp.biometric.models.NetworkException;
import com.hisp.biometric.models.NetworkExceptionFactory;
import com.hisp.biometric.models.TrackedEntityInstance;
import com.hisp.biometric.models.TrackedEntityInstancesResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
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
    
    public static final String DHIS_DOMAIN_DEFAULT = "http://localhost:8080/dhis";
    public static final String DHIS_URL_REGISTER_SUFFIX = "/dhis-web-tracker-capture/index.html?key=register";//&string=abcde&fid=1
    public static String dhisRegisterUrl;
    
    public static final String TEI_FROM_CLIENT_SUFFIX = "/api/trackedEntityInstances.json?ouMode=ALL&fields=trackedEntityInstance,attributes[*],orgUnit&filter=";
    public static String TEI_FROM_CLIENT_CODE;
    
    public static final String TEI_FROM_FID_SUFFIX = "/api/trackedEntityInstances.json?ouMode=ALL&fields=trackedEntityInstance,attributes[*],orgUnit&filter=";
    public static String TEI_FROM_FID_URL;
    public static final String TEI_URL_SUFFIX = "/api/trackedEntityInstances";
    public static String DHIS_TEI_URL;
    
    public static final String DASHBOARD_SUFFIX = "/dhis-web-tracker-capture/index.html#/dashboard?";//tei=HzF9UJCZrAw&program=L78QzNqadTV&ou=RXthSzYJfGF";
    public static String DASHBOARD_URL;
    
    public static final String LOGIN_SUFFIX = "/api/me.json";
    public static String LOGIN_URL;
    
    public static ClientConfiguration clientConfiguration;
    public static LoginResponse loginResponse;
    
    
    public static void init(){
        String domain = clientConfiguration.getFingerprintUrl();
        String dhisDomain = clientConfiguration.getDhisUrl();
        if(domain==null){
            domain = DEFAULT_DOMAIN;
            
        }
        if(dhisDomain==null){
            dhisDomain = DHIS_DOMAIN_DEFAULT;
        }
        
        
        //setting values in constants
        Constants.ATTRIBUTE_CLIENT_CODE = clientConfiguration.getAttrubute_client_code();
        Constants.ATTRIBUTE_FID = clientConfiguration.getAttribute_fid();
        Constants.ATTRIBUTE_FID_CODE = clientConfiguration.getAttribute_fid_code();
        Constants.ATTRIBUTE_TEMPLATE = clientConfiguration.getAttribute_template();
        Constants.ATTRIBUTE_TEMPLATE_CODE = clientConfiguration.getAttribute_template_code();
        Constants.PROGRAM_HIV  = clientConfiguration.getProgram_hiv();
        
        apiUrl = domain+API_SUFFIX;
        dhisRegisterUrl = dhisDomain+DHIS_URL_REGISTER_SUFFIX;
        TEI_FROM_CLIENT_CODE = dhisDomain+TEI_FROM_CLIENT_SUFFIX+Constants.ATTRIBUTE_CLIENT_CODE+":EQ:";
        DHIS_TEI_URL = dhisDomain+TEI_URL_SUFFIX;
        DASHBOARD_URL = dhisDomain+DASHBOARD_SUFFIX;
        TEI_FROM_FID_URL = dhisDomain+TEI_FROM_FID_SUFFIX+Constants.ATTRIBUTE_FID+":EQ:";
        LOGIN_URL = dhisDomain+LOGIN_SUFFIX;
        
        
    }
    
    public static FingerPrint sendEnrollment(String template) throws NetworkException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //httpclient.get
        
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
            //HttpClientContext context = getContext();
            CloseableHttpResponse response = httpclient.execute(request);//,context);
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                //InputStream is = response.getEntity().getContent();
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //String responseStr = "";
                //while(reader.ready()){
                 //   responseStr +=reader.readLine();
                //}
                String responseStr = EntityUtils.toString(response.getEntity());
                fp = FingerPrint.fromJson(responseStr);
                System.out.println(responseStr);
                
            }else{
                System.out.println("Request : "+request.toString());
                NetworkException exception = NetworkExceptionFactory.getException(response.getStatusLine().getStatusCode());
                System.out.print(exception);
                throw exception;
                
            }
            
            response.close();
            httpclient.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }
        
        return fp;
    }
    
    public static FingerPrint recognize(String template) throws NetworkException{
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
        FingerPrint fp = new FingerPrint();
        fp.setTemplate(template);
        
        try{
            
            HttpPost request = new HttpPost(apiUrl);
            System.out.println("Recoginze -> "+apiUrl);
            String json = fp.toString();
            
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            request.setHeader("Content-type","application/json");
            
            CloseableHttpResponse response = httpclient.execute(request);
            
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                //InputStream is = response.getEntity().getContent();
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //String responseStr = "";
                //while(reader.ready()){
                    //responseStr +=reader.readLine();
                //}
                String responseStr = EntityUtils.toString(response.getEntity());
                fp = FingerPrint.fromJson(responseStr);
                System.out.println("response string " +responseStr);
            }else{
                System.out.println("Request : "+request.toString());
                NetworkException exception = NetworkExceptionFactory.getException(response.getStatusLine().getStatusCode());
                System.out.print(exception);
                throw exception;
            }
            response.close();
            httpclient.close();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
            
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }
        
        return fp;
    }
    
    public static ClientConfiguration getClientConfiguration(){
        if(clientConfiguration==null){
            clientConfiguration = ConfigurationAccess.getClientConfiguration();
            init();
        }
        
        return clientConfiguration;
    }
    
    public static boolean login() throws NetworkException{
        
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        LoginResponse lr = null;
        
        HttpHost targetHost = new HttpHost(clientConfiguration.getHost(), clientConfiguration.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        /*credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("hispdev", "Devhisp@1"));*/
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(clientConfiguration.getUserName().trim(),clientConfiguration.getPassword().trim()));
        
        
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        try{
            
            HttpGet request = new HttpGet(LOGIN_URL);       
            System.out.println(LOGIN_URL+ " : "+ clientConfiguration.getHost()+ " : "+ clientConfiguration.getPassword());
            CloseableHttpResponse response = httpclient.execute(
                targetHost, request, context);
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                //InputStream is = response.getEntity().getContent();
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //String responseStr = "";
                //while(reader.ready()){
                    //responseStr +=reader.readLine();
                //}
                String responseStr = EntityUtils.toString(response.getEntity());
                //System.out.println(responseStr);
                lr = LoginResponse.fromJson(responseStr);
                loginResponse = lr;
                System.out.println(lr);
                //System.out.println(teiResponse.getTEI(0) );
                //tei = teiResponse.getTEI(0);
                       
            }else{
                System.out.println("Request : "+request.toString());
                NetworkException exception = NetworkExceptionFactory.getException(response.getStatusLine().getStatusCode());
                System.out.print(exception);
                throw exception;
            }
            response.close();
            httpclient.close();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
            
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }
        if(lr!=null)
            return true;
        else
            return false;
    }
    
    public static boolean updateFingerPrint(FingerPrint fp,TrackedEntityInstance tei) throws NetworkException{
        boolean result = false;
        if(tei.getFID()==null){
            tei.getAttributes().add(AttributeFactory.getAttribute(AttributeFactory.ATTRIBUTE_TYPES.FID));
        }
        if(tei.getTemplate()==null){
            tei.getAttributes().add(AttributeFactory.getAttribute(AttributeFactory.ATTRIBUTE_TYPES.FINGERPRINTTEMPLATE));
        }
        
        tei.getFID().setValue(fp.getFid()+"");
        
        tei.getFID().setStoredBy(clientConfiguration.getUserName());
        tei.getTemplate().setValue(fp.getTemplate());
        tei.getTemplate().setStoredBy(clientConfiguration.getUserName());
        
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        
        HttpHost targetHost = new HttpHost(clientConfiguration.getHost(), clientConfiguration.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("hispdev","Devhisp@1"));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        try{
            HttpEntity enitity = new StringEntity(tei.toString());
            String url = DHIS_TEI_URL+"/"+tei.getTrackedEntityInstance();
            System.out.println("TEI request : "+url);
            HttpPut request = new HttpPut(url);           
            request.setEntity(enitity);
            request.setHeader("Content-type","application/json");
            
              CloseableHttpResponse response = httpclient.execute(
                targetHost, request, context);
              System.out.println(tei.toString());
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                String responseStr = EntityUtils.toString(response.getEntity());
                System.out.println(responseStr);
                //TrackedEntityInstancesResponse teiResponse = TrackedEntityInstancesResponse.fromJson(responseStr);
                //System.out.println(teiResponse.getTEI(0) );
                //tei = teiResponse.getTEI(0);
                result =  true;       
            }else{
                System.out.println("Request : "+request.toString());
                NetworkException exception = NetworkExceptionFactory.getException(response.getStatusLine().getStatusCode());
                System.out.print(exception);
                throw exception;
            }
            response.close();
            httpclient.close();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            result = false;
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }
        
        return result;
        
                
    }
    
    public static TrackedEntityInstance getTrackedEntityInstanceWithfid(String fid) throws NetworkException{
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        TrackedEntityInstance tei = null;
        
        HttpHost targetHost = new HttpHost(clientConfiguration.getHost(), clientConfiguration.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("hispdev","Devhisp@1"));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        try{
            
            HttpGet request = new HttpGet(TEI_FROM_FID_URL+fid);           
              CloseableHttpResponse response = httpclient.execute(
                targetHost, request, context);
              System.out.println(response.getStatusLine());
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                //InputStream is = response.getEntity().getContent();
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //String responseStr = "";
                //while(reader.ready()){
                    //responseStr +=reader.readLine();
                //}
                String responseStr = EntityUtils.toString(response.getEntity());
                System.out.println(responseStr);
                TrackedEntityInstancesResponse teiResponse = TrackedEntityInstancesResponse.fromJson(responseStr);
                System.out.println(teiResponse.getTEI(0) );
                tei = teiResponse.getTEI(0);
                       
            }else{
                System.out.println("Request : "+request.toString());
                NetworkException exception = NetworkExceptionFactory.getException(response.getStatusLine().getStatusCode());
                System.out.print(exception);
                throw exception;
            }
            response.close();
            httpclient.close();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
            
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }
        
        return tei;
    }
    
    public static  TrackedEntityInstance getTrackedEntityInstanceWithCode(String code) throws NetworkException{
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        TrackedEntityInstance tei = null;
        
        HttpHost targetHost = new HttpHost(clientConfiguration.getHost(), clientConfiguration.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("hispdev","Devhisp@1"));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        try{
            
            HttpGet request = new HttpGet(TEI_FROM_CLIENT_CODE+code);           
              CloseableHttpResponse response = httpclient.execute(
                targetHost, request, context);
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(response.getStatusLine());
                //InputStream is = response.getEntity().getContent();
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //String responseStr = "";
                //while(reader.ready()){
                    //responseStr +=reader.readLine();
                //}
                String responseStr = EntityUtils.toString(response.getEntity());
                System.out.println(responseStr);
                TrackedEntityInstancesResponse teiResponse = TrackedEntityInstancesResponse.fromJson(responseStr);
                System.out.println(teiResponse.getTEI(0) );
                tei = teiResponse.getTEI(0);
                       
            }else{
                System.out.println("Request : "+request.toString());
                NetworkException exception = NetworkExceptionFactory.getException(response.getStatusLine().getStatusCode());
                System.out.print(exception);
                throw exception;
            }
            response.close();
            httpclient.close();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }catch (IOException ex) {
            Logger.getLogger(NetworkCall.class.getName()).log(Level.SEVERE, null, ex);
            throw NetworkExceptionFactory.getCustomException(ex.getMessage());
        }
        
        return tei;
    }   
    
    
    public static String getUrlForRegister(FingerPrint fp){
        return dhisRegisterUrl+"&string="+fp.getTemplate()+"&fid="+fp.getFid();
    }
    
    public static String getUrlForDashboard(TrackedEntityInstance tei){
        return DASHBOARD_URL+"tei="+tei.getTrackedEntityInstance()+
                "&program="+clientConfiguration.getProgram_hiv()+"&ou="+loginResponse.getOrganisationUnits().get(0).getId();//+tei.getOrgUnit();//tei=HzF9UJCZrAw&program=L78QzNqadTV&ou=RXthSzYJfGF";
    }
    
    private static HttpClientContext getContext(){
        //String usrpasscombine = lc.getUsername()+":"+lc.getPassword();
        String usrpasscombine = "hispdev:Devhisp@1";
        CredentialsProvider crdProvider = new BasicCredentialsProvider();
        crdProvider.setCredentials(AuthScope.ANY, new NTCredentials(usrpasscombine));
        HttpClientContext context = HttpClientContext.create();
        
        return context;
    }
    
    
    
}
