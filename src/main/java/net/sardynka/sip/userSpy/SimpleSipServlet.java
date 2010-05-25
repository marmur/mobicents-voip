/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy;

import java.io.IOException;
import java.util.HashMap;
import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import net.sardynka.sip.userSpy.dataStructures.RealUserInfo;
import net.sardynka.sip.userSpy.dataStructures.UserInfo;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 *
 * @author marmur
 */
public class SimpleSipServlet extends SipServlet implements SipErrorListener {
    private static Log logger = LogFactory.getLog(SimpleSipServlet.class);

    
        private static final String CONTACT_HEADER = "Contact";

        @Resource
        protected SipFactory sipFactory;


	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

                HashMap<String,UserInfo> users = new HashMap<String, UserInfo>();
                getServletContext().setAttribute("registeredUsers", users);
	}



        private boolean addUserToMap(RealUserInfo userInfo){
            HashMap<String, RealUserInfo> users = (HashMap<String, RealUserInfo>) getServletContext().getAttribute("registeredUsers");

            if (users.containsKey(userInfo.getUID())){
                logger.info("User ("+userInfo.getUID() +") already exists");
                return false;
            }else{
                logger.info("Adding new user ("+userInfo.getUID() +")");
                users.put(userInfo.getUID(), userInfo);
                return true;
            }
        }

        private void removeUserFromMap (String uid){
            HashMap<String, RealUserInfo> users = (HashMap<String, RealUserInfo>) getServletContext().getAttribute("registeredUsers");
            users.remove(uid.toLowerCase());
        }

        protected UserInfo getUserInfo(String uid){
            HashMap<String, RealUserInfo> users = (HashMap<String, RealUserInfo>) getServletContext().getAttribute("registeredUsers");
            return users.get(uid.toLowerCase());
        }

	protected SipServletResponse register(SipServletRequest request) throws ServletException, IOException {
		logger.info("New REGISTER ("+ request.getAddressHeader(CONTACT_HEADER).getExpires() + ")request from " + request.getSubscriberURI());
                SipServletResponse responese = null;
                if (request.getAddressHeader(CONTACT_HEADER).getExpires()==0){
                    removeUserFromMap(request.getSubscriberURI().toString());
                    responese = request.createResponse(SipServletResponse.SC_OK);
                } else {
                    RealUserInfo userInfo = new RealUserInfo( request.getSubscriberURI().toString(), request);
                    if (addUserToMap(userInfo)){
                        String via = request.getHeader("Via");
                        userInfo.setIncomeIP(RealUserInfo.unmarshalIncomeIP(via));
                        userInfo.setIncomePort(RealUserInfo.unmarshalIncomePort(via));

                        responese = request.createResponse(SipServletResponse.SC_OK);

                    }else{
                        responese = request.createResponse(SipServletResponse.SC_DECLINE);
                    }
                }
                return responese;
        }

        @Override
        protected void doRegister(SipServletRequest request) throws ServletException, IOException {
            register(request).send();
        }

        @Override
        protected void doInvite(SipServletRequest req) throws ServletException, IOException{
              req.getProxy().proxyTo(req.getRequestURI());
        }



        protected SipServletRequest message(SipServletRequest req) throws IOException{
            logger.info("Received message for: " + req.getTo().getURI().toString());
            UserInfo userInfo = getUserInfo(req.getTo().getURI().toString());
            if (userInfo == null){
                logger.info("No suche user in DB");
                req.createResponse(SipServletResponse.SC_GONE).send();
                return null;
            }else{
                logger.info("Sending message");
                req.createResponse(SipServletResponse.SC_OK).send();

                SipApplicationSession sas = sipFactory.createApplicationSession();
                SipServletRequest request = sipFactory.createRequest(sas, "MESSAGE", req.getFrom().getURI(), userInfo.getURI());
                request.setContent(req.getContent(), req.getContentType());
                return request;
            }


        }

        @Override
        protected void doMessage(SipServletRequest req) throws ServletException,IOException{
            SipServletRequest request = message(req);
            if (request != null) request.send();
        }



    
    
    public void noAckReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void noPrackReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
