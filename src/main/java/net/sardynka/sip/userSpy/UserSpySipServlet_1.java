/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
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

/**
 *
 * @author marmur
 */
public class UserSpySipServlet_1 extends SimpleSipServlet implements SipErrorListener{
    private static Log logger = LogFactory.getLog(UserSpySipServlet.class);
    private static SipURI myUri;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
	super.init(servletConfig);

        Enumeration<String> e = servletConfig.getInitParameterNames();
        while (e.hasMoreElements()){
            String s = e.nextElement();
            logger.info(s);
        }

        logger.info(servletConfig);
        myUri = sipFactory.createSipURI(null, "127.0.0.1");
        myUri.setHost("localhost");
        myUri.setPort(5080);
    }





    
    @Override
    protected void doMessage(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException{
        logger.error("MAM WIADOMO??!!!!!!-------------------------------------------------------");

    }


    private boolean  addProxyRegistaration(SipServletRequest request) throws IOException{
        HashMap<String, UserInfo> users = (HashMap<String, UserInfo>) getServletContext().getAttribute("registeredUsers");
        String uid = request.getContent().toString();
        if (users.containsKey(uid)){
            logger.info("User already registered: " + uid);
            return false;
        }else{
            ProxyUserInfo userInfo = new ProxyUserInfo(request.getFrom().getURI());
            logger.info("Adding Proxy ("+ userInfo.getURI() +" for: " + uid);
            users.put(uid, userInfo);
            return true;
        }
    }


    @Override
    protected void doRegister(SipServletRequest request) throws ServletException, IOException {
        /*
        SipServletResponse response = null;
        if (request.getHeader("User-Agent").equals("UserSpy")){
            logger.info("Received Proxy register request");
            if (addProxyRegistaration(request)){
                response = request.createResponse(SipServletResponse.SC_OK);
            }else{
                response = request.createResponse(SipServletResponse.SC_OK);
                response.setContent(request.getContent(), "text/plain");
                response.setHeader("User-Agent","UserSpy");
            }
            response.send();
        }else{
            //Register User in db
            response = super.register(request);

            if (response.getStatus() == SipServletResponse.SC_OK){
                //Send Welcome message
                sendSystemMessage(request, "Witaj ziooom! :)");
//                response.send();

                //Route to master Sip Server
                //TODO: Read sth form specification
                SipURI proxyURI = sipFactory.createSipURI(null, "127.0.0.1");
                proxyURI.setHost("127.0.0.1");
                proxyURI.setPort(5080);
//                brodcastRegisterMessage(request, proxyURI);
  
//                request.addHeader("User-Agent","UserSpy");
                request.pushRoute(proxyURI);
                request.send();

            }else{
                response.send();
            }
         *
         */



//            super.register(request).send();
            if (request.getHeader("Route") == null){
                logger.info("Empty route");

                SipURI proxyURI = sipFactory.createSipURI(null, "127.0.0.1");
                proxyURI.setHost("127.0.0.1");
                proxyURI.setPort(5080);

                request.pushRoute(proxyURI);
                request.getProxy().proxyTo(proxyURI);
            }else{
                logger.info("Route available");
            }


        /*

        SipURI proxyURI = sipFactory.createSipURI(null, "127.0.0.1");
        proxyURI.setHost("127.0.0.1");
        proxyURI.setPort(5080);
        comercial.pushRoute(proxyURI);
        comercial.send();
*/
    }

    private void brodcastRegisterMessage(SipServletRequest request, SipURI to) throws IOException{
        logger.info("Preparing SipRegister Request for routing");
        SipApplicationSession sps = request.getSession().getApplicationSession();
        SipServletRequest registerRequest = sipFactory.createRequest(sps, "REGISTER", myUri,to);
        registerRequest.setHeader("User-Agent","UserSpy");
        registerRequest.setContent(request.getSubscriberURI().toString(), "text/plain");
        registerRequest.send();
        //TODO: Request validation & rollback
        logger.info("Register Request send...");
    }


    private void sendSystemMessage(SipServletRequest request, String message) throws IOException {
        logger.info("Creating System Message");
        URI to = request.getFrom().getURI();
        SipApplicationSession sps = request.getSession().getApplicationSession();
        sendSystemMessage(sps, to, message);
    }

    private void sendSystemMessage(SipApplicationSession sps, URI to, String message) throws UnsupportedEncodingException, IOException{
        SipServletRequest systemMessage = sipFactory.createRequest(sps, "MESSAGE", to, to);
        systemMessage.setContent(message , "text/plain");
        systemMessage.send();
    }


    public void noAckReceived(SipErrorEvent see) {
        logger.error("No ACK received");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void noPrackReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private void rollbackUser (RealUserInfo userInfo, String sipId, SipServletResponse resp) throws UnsupportedEncodingException, IOException{
        logger.info("Rolling back real user");
        SipApplicationSession sap = resp.getSession().getApplicationSession();
        sendSystemMessage(sap, userInfo.getURI(), sipId);

    }

    private void rollbackUser (ProxyUserInfo userInfo, String sipId, SipServletResponse resp){
        logger.info("Rolling bac roxy info");
        //TODO
    }

    private void rollbackUser (UserInfo userInfo, String sipId, SipServletResponse resp){
        logger.error("Unrecognised userInfo type: " + userInfo.getClass().toString());
    }


    @Override
    protected void doErrorResponse(SipServletResponse resp) throws ServletException,IOException{
        if (resp.getHeader("User-Agent").equals("UserSpy")){
            if (resp.getRequest().getMethod().equals("REGISTER")){
                String sipId = (String)resp.getContent();
                logger.info("Rollbacking user: " + sipId);
                HashMap<String, UserInfo> users = (HashMap<String, UserInfo>) getServletContext().getAttribute("registeredUsers");
                UserInfo userInfo = users.get(sipId);

                rollbackUser(userInfo, sipId,resp);
            }
        }
    }

    @Override
    protected void doSuccessResponse(SipServletResponse resp) throws ServletException,IOException{
        super.doSuccessResponse(resp);
        logger.info("Received ACK");

        if (resp.getRequest().getMethod().equals("REGISTER")){
            logger.info("REGISTER");
            
        }
    }

}
