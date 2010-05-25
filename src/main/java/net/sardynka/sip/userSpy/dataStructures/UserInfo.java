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
public interface UserInfo {
    public String getHost();
    public String getPort();
    public String getName();
    public URI getURI();
}
