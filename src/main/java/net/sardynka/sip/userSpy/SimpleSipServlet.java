/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
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


	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

                HashMap<String,UserInfo> users = new HashMap<String, UserInfo>();
                getServletContext().setAttribute("registeredUsers", users);
	}



        private boolean addUserToMap(UserInfo userInfo){
            HashMap<String, UserInfo> users = (HashMap<String, UserInfo>) getServletContext().getAttribute("registeredUsers");

            if (users.containsKey(userInfo.getUID())){
                return false;
            }else{
                users.put(userInfo.getUID(), userInfo);
                return true;
            }
        }



        @Override
	protected void doRegister(SipServletRequest request) throws ServletException, IOException {
		logger.info("New REGISTER request from " + request.getSubscriberURI());


                //Jaks si? klient przedstawi?: sip:Marmur@localhost:5061
                //request.getSubscriberURI()
                UserInfo userInfo = new UserInfo( request.getSubscriberURI().toString(), request);



                SipServletResponse responese = null;
                if (addUserToMap(userInfo)){
                    String via = request.getHeader("Via");
                    userInfo.setIncomeIP(UserInfo.getIncomeIP(via));
                    userInfo.setIncomePort(UserInfo.getIncomePort(via));

                    responese = request.createResponse(SipServletResponse.SC_OK);

                }else{
                    responese = request.createResponse(SipServletResponse.SC_DECLINE);
                }


                responese.send();
        }

        @Override
        protected void doInvite(SipServletRequest req) throws javax.servlet.ServletException, java.io.IOException{
//            SipServletResponse response = req.createResponse(SipServletResponse.SC_BAD_EVENT);
//            response.send();

              req.getProxy().proxyTo(req.getRequestURI());
        }


       



    
    
    public void noAckReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void noPrackReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
