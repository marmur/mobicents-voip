/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.xml.ws.Response;
import net.sardynka.sip.userSpy.dataStructures.ProxyUserInfo;
import net.sardynka.sip.userSpy.dataStructures.RealUserInfo;
import net.sardynka.sip.userSpy.dataStructures.UserInfo;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;

/**
 *
 * @author marmur
 */
public class UserSpySipServlet extends SimpleSipServlet implements SipErrorListener{
    private static Log logger = LogFactory.getLog(UserSpySipServlet.class);



    private static Properties p = null;
    private static SipURI proxyUri = null;
    private static String proxyString = null;
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
	super.init(servletConfig);

        Enumeration<String> e = servletConfig.getInitParameterNames();
        while (e.hasMoreElements()){
            String s = e.nextElement();
            logger.info(s);
        }


        try {
            InputStream is =  UserSpySipServlet.class.getClassLoader().getResourceAsStream("UserSpy.properties");
            if (is != null){
                p = new Properties();
                p.load(is);

                String host = p.getProperty("ProxyHost");
                String port = p.getProperty("ProxyPort");
                proxyUri = super.sipFactory.createSipURI(null, host);
                proxyUri.setHost(host);
                proxyUri.setPort(Integer.parseInt(port));
                proxyString = host+":"+port;
            }
        } catch (Exception ex) {
            Logger.getLogger(UserSpySipServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




    private boolean checkVia(SipServletRequest req, String match){
        ListIterator<String> via = req.getHeaders("Via");
        while (via.hasNext()){
            String addres = via.next().split(";")[0].split(" ")[1];
            if (addres.equals(match)) return true;
        }
        return false;
    }
    
    @Override
    protected void doMessage(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException{
        if (proxyUri==null){
            logger.info("Using local server");
            SipServletRequest request = super.message(req);
            if (request != null){
                ArrayList<SipURI> route = ((RealUserInfo)(getUserInfo(req.getSubscriberURI().toString()))).getRoute();

                for (int i=route.size()-2;i>=0;i--){
                    request.pushRoute(route.get(i));
                }
                request.send();
            }
        } else {
            if (checkVia(req,proxyString)){
                logger.info("Sending to user");
                req.getProxy().proxyTo(req.getSubscriberURI());
            }else{
                logger.info("Forwarding to remote site: " + proxyUri.toString());
                req.getProxy().proxyTo(proxyUri);
            }
        }
    }





    private ArrayList<SipURI> parseVia(ListIterator<String> via){
        ArrayList<SipURI> result = new ArrayList<SipURI>();
        while (via.hasNext()){
            String addres = via.next().split(";")[0].split(" ")[1];
            logger.info("via -> " + addres);
            result.add(sipFactory.createSipURI(null, addres));
        }
        return result;
    }


    @Override
    protected void doRegister(SipServletRequest request) throws ServletException, IOException {
        if (proxyUri==null){
                logger.info("Using local server");
                SipServletResponse response = super.register(request);
                response.send();

                if (response.getStatus() == SipServletResponse.SC_OK){
                    RealUserInfo rui = (RealUserInfo)getUserInfo(request.getSubscriberURI().toString());
                    if (rui != null){
                        rui.setRoute(parseVia(request.getHeaders("Via")));
                        sendSystemMessage(request, "Witaj ziooom! :)");
                    }
                }
            }else{
                logger.info("Forwarding to remote site: " + proxyUri.toString());
                request.getProxy().proxyTo(proxyUri);
            }
    }



    private void sendSystemMessage(SipServletRequest request, String message) throws IOException {
        logger.info("Creating System Message");
        RealUserInfo rui = (RealUserInfo)getUserInfo(request.getFrom().getURI().toString());
        SipApplicationSession sps = request.getSession().getApplicationSession();
        sendSystemMessage(sps, rui, message);
    }

    private void sendSystemMessage(SipApplicationSession sps, RealUserInfo ui, String message) throws UnsupportedEncodingException, IOException{


        SipServletRequest systemMessage = sipFactory.createRequest(sps, "MESSAGE", ui.getURI(), ui.getURI());
        systemMessage.setContent(message , "text/plain");
        ArrayList<SipURI> route = ui.getRoute();

//        systemMessage.pushRoute(sipFactory.createAddress(ui.getURI()));
        for (int i=route.size()-2;i>=0;i--){
            systemMessage.pushRoute(route.get(i));
        }


        systemMessage.send();
    }


    public void noAckReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void noPrackReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
