/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sardynka.sip.userSpy.dataStructures;

import java.util.ArrayList;
import javax.servlet.sip.SipServletRequest;

/**
 *
 * @author marmur
 */
public class UserInfo {

    private String UID;
    private String incomePort;
    private String incomeIP;
    private SipServletRequest registerPayload;
    private ArrayList<String> calls;


    public UserInfo(String UID, SipServletRequest registerPayload) {
        this.UID = UID;
        this.registerPayload = registerPayload;
        parsePayload(registerPayload);
        calls = new ArrayList<String>();
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

    public static String getIncomeIP(String via){
        for (String s:via.split(";")){
            if (s.startsWith("received")){
                return s.split("=")[1];
            }
        }
        return null;
    }

    public static String getIncomePort(String via){
        for (String s:via.split(";")){
            if (s.startsWith("rport")){
                return s.split("=")[1];
            }
        }
        return null;
    }

}
