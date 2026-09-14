<%@page import="fr.paris.lutece.plugins.extend.web.ResourceExtenderJspBean"%>
<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.extend.modules.comment.web.CommentJspBean"%>

${ commentJspBean.init( pageContext.request, ResourceExtenderJspBean.RIGHT_MANAGE_RESOURCE_EXTENDER ) }
${ pageContext.response.sendRedirect( commentJspBean.getConfirmRemoveComment( pageContext.request ) ) }