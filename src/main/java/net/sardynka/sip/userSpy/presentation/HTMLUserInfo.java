/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy.presentation;

import java.util.ArrayList;
import java.util.List;
import net.sardynka.sip.userSpy.dataStructures.RealUserInfo;

/**
 *
 * @author marmur
 */
public class HTMLUserInfo {


    private static String makeHTML(ArrayList<String> list, String TDseparator){
        StringBuffer sb = new StringBuffer("<tr>");
        for (int i=0;i<list.size();i++){
            sb.append(TDseparator);
            sb.append(list.get(i));
            sb.append("<td>");
        }
        sb.append("</tr>");
        return sb.toString();
    }


    public static String getHTMLHeader(){
        ArrayList<String> header = new ArrayList<String>();
        header.add("UID");
        header.add("sourceIP");
        header.add("sourcePort");
        return makeHTML(header,"<td>");
    }


    
    public static String userToHTML(RealUserInfo userInfo){
        ArrayList<String> header = new ArrayList<String>();
        header.add(userInfo.getUID());
        header.add(userInfo.getIncomeIP());
        header.add(userInfo.getIncomePort());
        
        StringBuffer userRecord = new StringBuffer(makeHTML(header,"<td bgcolor='66FFFF'>"));
        userRecord.append("<tr><td><pre>");
        userRecord.append(userInfo.getRegisterPayload().toString());
        userRecord.append("</pre></td></tr>");

        return userRecord.toString();
    }
}
