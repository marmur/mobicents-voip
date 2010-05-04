 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sardynka.sip.userSpy;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author marmur
 */
public class UserSpyHttpServlet extends HttpServlet{
    private static Log logger = LogFactory.getLog(UserSpyHttpServlet.class);


        @Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
        	throws ServletException, IOException {

		// Write the output html
		PrintWriter	out;
		response.setContentType("text/html");
		out = response.getWriter();

		// Just redirect to the index
		out.println("<HTML><META HTTP-EQUIV=\"Refresh\"CONTENT=\"0; URL=index.jsp\"><HEAD><TITLE></HTML>");
		out.close();
	}

}
