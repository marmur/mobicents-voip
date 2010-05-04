<%@ page import="net.sardynka.sip.userSpy.dataStructures.*"%>
<%@ page import="net.sardynka.sip.userSpy.presentation.*"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="javax.servlet.sip.*"%>
<html>
    <head>
    <title>SipServlet User Spy - by Jacek Marmuszewski & Mateusz Mrówka</title>
    </head>
<body>


<p><font size="6"><center>User Spy :)</center></font></p>




<br />
<br />
<hr />
<table border="1" cellspacing="0" align="center" cellpadding="0">
    s

<%
HashMap<String, UserInfo> users = (HashMap<String, UserInfo>)
	getServletContext().getAttribute("registeredUsers");
    if (users == null) {
        out.println("No users registered");
    }else{
        out.println(HTMLUserInfo.getHTMLHeader());
        for (UserInfo uI: users.values()){
            out.println(HTMLUserInfo.userToHTML(uI));
        }
   }
%>

</table>






<form method="GET" action="call">

	<p>To:</p>
	<p><input type="text" name="to" size="20" value="sip:to@127.0.0.1:5050"></p>
	<p>From:</p>
	<p><input type="text" name="from" size="20" value="sip:from@127.0.0.1:5060"></p>
	<p><input type="submit" value="Call" name="B1"><input type="reset" value="Reset" name="B2"></p>
</form>
<p>&nbsp;</p>
<hr/>
<a>Debug</a>
<%
SipApplicationSession appSession = 
        	((ConvergedHttpSession)request.getSession()).getApplicationSession();
out.println("<br/>appSession.setFromSipServletUA1=" + appSession.getAttribute("setFromSipServletUA1"));
out.println("<br/>appSession.setFromSipServletUA2=" + appSession.getAttribute("setFromSipServletUA2"));
out.println("<br/>appSession.setFromHttpServlet=" + appSession.getAttribute("setFromHttpServlet"));
out.println("<br/>JSESSIONID=" + request.getSession().getId());
out.println("<br/>APPSESSIONID=" + appSession.getId());
out.println("<br/>Designed by Mr. Marmur  @ Mrowek");
%>

</body>
</html>
