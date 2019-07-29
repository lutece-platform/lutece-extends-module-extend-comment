<%@page import="fr.paris.lutece.plugins.extend.web.ResourceExtenderJspBean"%>
<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:useBean id="extendComment" scope="session" class="fr.paris.lutece.plugins.extend.modules.comment.web.CommentJspBean" />
<%
	extendComment.init( request, ResourceExtenderJspBean.RIGHT_MANAGE_RESOURCE_EXTENDER );
	String pageContent = extendComment.getModifyAdminComment( request ); 
%>

<jsp:include page="../../../../AdminHeader.jsp" />
<%= pageContent %>
<%@ include file="../../../../AdminFooter.jsp" %>