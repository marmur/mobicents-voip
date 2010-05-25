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
<center>
<table border="1" cellspacing="0" align="center" cellpadding="0">
<%
HashMap<String, UserInfo> users = (HashMap<String, UserInfo>)
	getServletContext().getAttribute("registeredUsers");
    if (users == null || users.size() == 0) {
        out.println("No users registered");
    }else{
        out.println(HTMLUserInfo.getHTMLHeader());
        for (UserInfo uI: users.values()){
            out.println(HTMLUserInfo.userToHTML((RealUserInfo)uI));
        }
   }
%>

</table>
</center>






<hr/>
<center>
Designed by Mr. Marmur  @ Mrowek
</center>
</body>
</html>
