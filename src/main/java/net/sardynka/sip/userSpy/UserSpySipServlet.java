/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy;

import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServlet;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 *
 * @author marmur
 */
public class UserSpySipServlet extends SipServlet implements SipErrorListener{
    private static Log logger = LogFactory.getLog(UserSpySipServlet.class);

    public void noAckReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void noPrackReceived(SipErrorEvent see) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
