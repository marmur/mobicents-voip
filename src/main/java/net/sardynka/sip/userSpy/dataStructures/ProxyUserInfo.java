/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy.dataStructures;

import javax.servlet.sip.URI;

/**
 *
 * @author marmur
 */
public class ProxyUserInfo implements UserInfo{
    private String host;
    private String port;
    private URI uri;

    public ProxyUserInfo(URI uri) {
        this.uri = uri;
    }


    public ProxyUserInfo(String host, String port){
        this.host = host;
        this.port = port;
    }


    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getName() {
        return "";
    }

    public URI getURI() {
        return uri;
    }
    


}
