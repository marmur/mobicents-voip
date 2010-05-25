/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sardynka.sip.userSpy.dataStructures;

import java.util.ArrayList;
import java.util.ListIterator;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

/**
 *
 * @author marmur
 */
public class RealUserInfo implements UserInfo {

    private String UID;
    private String orginalUID;
    private String incomePort;
    private String incomeIP;
    private SipServletRequest registerPayload;
    private ArrayList<String> calls;
    private URI userURI;
    private ArrayList<SipURI> route;

    public ArrayList<SipURI> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<SipURI> route) {
        this.route = route;
    }

    public String getOrginalUID() {
        return orginalUID;
    }


    public RealUserInfo(String UID, SipServletRequest registerPayload) {
        this.orginalUID = UID;
        this.UID = UID.toLowerCase();
        this.userURI = registerPayload.getSubscriberURI();
        this.registerPayload = registerPayload;
        parsePayload(registerPayload);
        calls = new ArrayList<String>();


        parsePayload(registerPayload);
    }


    private void parsePayload(SipServletRequest request){
    }


    public ArrayList<String> getUserCalls(){
        return calls;
    }





    public void setIncomeIP(String incomeIP) {
        this.incomeIP = incomeIP;
    }

    public void setIncomePort(String incomePort) {
        this.incomePort = incomePort;
    }



    public SipServletRequest getRegisterPayload() {
        return registerPayload;
    }
    
    public String getUID() {
        return UID;
    }

    public String getIncomeIP() {
        return incomeIP;
    }

    public String getIncomePort() {
        return incomePort;
    }



    public String toString(){
        return "USER: " + UID +"=>" + incomeIP +":"+ incomePort + "\nPAYLOAD:" + registerPayload.toString();
    }

    public static String unmarshalIncomeIP(String via){
        for (String s:via.split(";")){
            if (s.startsWith("received")){
                return s.split("=")[1];
            }
        }
        return null;
    }

    public static String unmarshalIncomePort(String via){
        for (String s:via.split(";")){
            if (s.startsWith("rport")){
                return s.split("=")[1];
            }
        }
        return null;
    }

    
    public String getHost() {
        return getIncomeIP();
    }

    public String getPort() {
        return getIncomePort();
    }

    public String getName() {
        return getUID();
    }

    public URI getURI() {
        return userURI;
    }

}
