package com.client;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class RestClient {

	public Client client = null;
	public WebResource webResource = null;
	
	public RestClient(String Url, String Username, String Password) throws GeneralSecurityException {
		client = Client.create(RestClient.configureClient());
		client.addFilter(new HTTPBasicAuthFilter(Username, Password));
		webResource = client.resource(Url);
	}
	
	public ClientResponse sendGetRequest() {
		return webResource.accept("application/json").get(ClientResponse.class);
	}
	
	public ClientResponse sendPostRequest(String input) {
		return webResource.accept("application/json").type("application/json").post(ClientResponse.class, input);
	}
	
	/* Method to Skip all Client side authentication*/
	public static ClientConfig configureClient() throws GeneralSecurityException {
		TrustManager[ ] certs = new TrustManager[ ] {
	            new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}
				}
	    };
	    SSLContext ctx = null;
	    try {
	        ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, certs, new SecureRandom());
	    } catch (java.security.GeneralSecurityException ex) {
	    	throw ex;
	    }
	    HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
	    
	    ClientConfig config = new DefaultClientConfig();
	    try {
		    config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
		        new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
		        }, 
		        ctx
		    ));
	    } catch(Exception e) {
	    	throw e;
	    }
	    return config;
	}
}
