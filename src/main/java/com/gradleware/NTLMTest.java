package com.gradleware;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProxySelector;
import java.net.URL;

public class NTLMTest {
    private final String username;
    private final String password;
    private final String domain;
    private final String workstation;

    public NTLMTest(String username, String password, String domain, String workstation) {
        this.username = username;
        this.password = password;
        this.domain = domain;
        this.workstation = workstation;
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        System.out.println("domain = " + domain);
        System.out.println("workstation = " + workstation);
    }

    public static void main(String[] args) throws IOException {
        String proxyHost;
        String proxyPort;
        String username;
        String password;
        String domain = "";
        String workstation = "";
        switch (args.length) {
            case 6:
                workstation = args[5];
            case 5:
                domain = args[4];
            case 4:
                password = args[3];
                username = args[2];
                proxyPort = args[1];
                proxyHost = args[0];
                break;
            default:
                System.out.println("Usage: ntlmtest <proxyHost> <proxyPort> <user> <password> [<domain> [<workstation>]]");
                return;
        }
        
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        NTLMTest test = new NTLMTest(username, password, domain, workstation);
        test.testJavaNetURL();
        test.testHttpClientNative();
    }

    public void testJavaNetURL() throws IOException {
        System.setProperty("http.proxyUser", username);
        System.setProperty("http.proxyPassword", password);
        System.setProperty("http.auth.ntlm.domain", domain);

        URL url = new URL("http://gradle.org");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setUseCaches(false);
        int lines = countLines(urlConnection.getInputStream());
        System.out.println(String.format("java.net.URL: got %s lines from http://gradle.org", lines));
    }

    public void testHttpClientNative() throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(httpClient.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
        httpClient.setRoutePlanner(routePlanner);

        Credentials ntCredentials = new NTCredentials(username, password, workstation, domain);
        httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY), ntCredentials);

        HttpGet getRequest = new HttpGet("http://gradle.org");
        HttpResponse httpResponse = httpClient.execute(getRequest);

        int lines = countLines(httpResponse.getEntity().getContent());
        System.out.println(String.format("HttpClient: got %s lines from http://gradle.org", lines));
    }

    private int countLines(InputStream inStream) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(inStream));

        int lines = 0;
        while (input.readLine() != null) {
            lines++;
        }
        return lines;
    }
}
