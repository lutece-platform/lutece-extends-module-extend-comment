/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.extend.modules.comment.web;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentPlugin;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.service.extender.history.ResourceExtenderHistoryService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.http.SecurityUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * 
 * CommentJspBean
 * 
 */
public class CommentJspBean extends PluginAdminPageJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -1787559203779077088L;

    // JSP
    private static final String JSP_VIEW_EXTENDER_INFO = "../../ViewExtenderInfo.jsp";
    private static final String JSP_URL_DO_REMOVE_COMMENT = "jsp/admin/plugins/extend/modules/comment/DoRemoveComment.jsp";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";
    private static final String MESSAGE_TITLE_CREATE_COMMENT = "module.extend.comment.create_comment.pageTitle";
    private static final String MESSAGE_TITLE_MODIFY_ADMIN_COMMENT = "module.extend.comment.modify_admin_comment.pageTitle";
    private static final String MESSAGE_ERROR_ = "module.extend.comment.modify_admin_comment.pageTitle";

    // TEMPLATE
    private static final String TEMPLATE_CREATE_COMMENT = "admin/plugins/extend/modules/comment/create_comment.html";
    private static final String TEMPLATE_MODIFY_ADMIN_COMMENT = "admin/plugins/extend/modules/comment/modify_admin_comment.html";

    // CONSTANT
    private static final String CONSTANT_SPACE = " ";

    private ICommentService _commentService = SpringContextService.getBean( CommentService.BEAN_SERVICE );
    private IResourceExtenderHistoryService _resourceHistoryService = SpringContextService
            .getBean( ResourceExtenderHistoryService.BEAN_SERVICE );

    /**
     * Do publish unpublish comment.
     * 
     * @param request the request
     * @return the string
     */
    public String doPublishUnpublishComment( HttpServletRequest request )
    {
        String strIdComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
       
        if ( StringUtils.isNotBlank( strIdComment ) && StringUtils.isNumeric( strIdComment ) )
        {
            int nIdComment = Integer.parseInt( strIdComment );
            Comment comment = _commentService.findByPrimaryKey( nIdComment );

            if ( comment != null )
            {
                try
                {
                    _commentService.updateCommentStatus( comment.getIdComment( ), !comment.isPublished( ) );
                }
                catch ( Exception ex )
                {
                    // Something wrong happened... a database check might be needed
                    AppLogService.error( ex.getMessage( ) + " when updating a comment", ex );

                    return AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE,
                            AdminMessage.TYPE_ERROR );
                }

                String strPostBackUrl = (String) request.getSession( ).getAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL );
                request.getSession( ).setAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL, null );
                if ( StringUtils.isEmpty( strPostBackUrl ) )
                {
                    strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
                }
                UrlItem url = new UrlItem( strPostBackUrl );
                url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE,
                        CommentResourceExtender.EXTENDER_TYPE_COMMENT );
               addIdExtendableResourceInUrl(comment.getIdExtendableResource(), request, url);
               
                url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE,
                        comment.getExtendableResourceType( ) );
                if ( comment.getIdParentComment( ) > 0 )
                {
                    url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, comment.getIdParentComment( ) );
                }
                url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                        request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                        CommentConstants.CONSTANT_AND_HTML ) );

                return url.getUrl( );
            }
        }

        return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
    }
    
    
    /**
     * Do flag  comment as important
     * 
     * @param request the request
     * @param cancelFlag true if the flag important of the comment must be cancel 
     * @return the string
     */
    public String doFlagImportantComment( HttpServletRequest request,boolean cancelFlag )
    {
        String strIdComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
       
        if ( StringUtils.isNotBlank( strIdComment ) && StringUtils.isNumeric( strIdComment ) )
        {
            int nIdComment = Integer.parseInt( strIdComment );
            Comment comment = _commentService.findByPrimaryKey( nIdComment );

            if ( comment != null )
            {
                try
                {
                	
                    _commentService.updateFlagImportant(comment.getIdComment(), !cancelFlag);
                }
                catch ( Exception ex )
                {
                    // Something wrong happened... a database check might be needed
                    AppLogService.error( ex.getMessage( ) + " when updating a comment", ex );

                    return AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE,
                            AdminMessage.TYPE_ERROR );
                }

                String strPostBackUrl = (String) request.getSession( ).getAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL );
                request.getSession( ).setAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL, null );
                if ( StringUtils.isEmpty( strPostBackUrl ) )
                {
                    strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
                }
                UrlItem url = new UrlItem( strPostBackUrl );
                url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE,
                        CommentResourceExtender.EXTENDER_TYPE_COMMENT );
               addIdExtendableResourceInUrl(comment.getIdExtendableResource(), request, url);
               
                url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE,
                        comment.getExtendableResourceType( ) );
                if ( comment.getIdParentComment( ) > 0 )
                {
                    url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, comment.getIdParentComment( ) );
                }
                url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                        request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                        CommentConstants.CONSTANT_AND_HTML ) );

                return url.getUrl( );
            }
        }

        return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
    }
    
    /**
     * Do pinned a comment.
     * 
     * @param request the request
     * @param cancelPinned true if the comment must be unpinned
     * @return the string
     */
    public String doPinned( HttpServletRequest request,boolean cancelPinned)
    {
        String strIdComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
       
        if ( StringUtils.isNotBlank( strIdComment ) && StringUtils.isNumeric( strIdComment ) )
        {
            int nIdComment = Integer.parseInt( strIdComment );
            Comment comment = _commentService.findByPrimaryKey( nIdComment );

            if ( comment != null )
            {
                try
                {
                	_commentService.updateCommentPinned(nIdComment, !cancelPinned);
                }
                catch ( Exception ex )
                {
                    // Something wrong happened... a database check might be needed
                    AppLogService.error( ex.getMessage( ) + " when updating a comment", ex );

                    return AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE,
                            AdminMessage.TYPE_ERROR );
                }

                String strPostBackUrl = (String) request.getSession( ).getAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL );
                request.getSession( ).setAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL, null );
                if ( StringUtils.isEmpty( strPostBackUrl ) )
                {
                    strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
                }
                UrlItem url = new UrlItem( strPostBackUrl );
                url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE,
                        CommentResourceExtender.EXTENDER_TYPE_COMMENT );
               addIdExtendableResourceInUrl(comment.getIdExtendableResource(), request, url);
               
                url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE,
                        comment.getExtendableResourceType( ) );
                if ( comment.getIdParentComment( ) > 0 )
                {
                    url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, comment.getIdParentComment( ) );
                }
                url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                        request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                        CommentConstants.CONSTANT_AND_HTML ) );

                return url.getUrl( );
            }
        }

        return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
    }
    
    

    /**
     * Gets the confirm remove comment.
     * 
     * @param request the request
     * @return the confirm remove comment
     */
    public String getConfirmRemoveComment( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_URL_DO_REMOVE_COMMENT );
        url.addParameter( CommentConstants.PARAMETER_ID_COMMENT,
                request.getParameter( CommentConstants.PARAMETER_ID_COMMENT ) );
        url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                CommentConstants.CONSTANT_AND_HTML ) );
        addViewResourceInUrl(request, url);

        return AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_CONFIRM_REMOVE_COMMENT,
                url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Do remove comment.
     * 
     * @param request the request
     * @return the string
     */
    public String doRemoveComment( HttpServletRequest request )
    {
        String strIdComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
      
        if ( StringUtils.isNotBlank( strIdComment ) && StringUtils.isNumeric( strIdComment ) )
        {
            int nIdComment = Integer.parseInt( strIdComment );
            Comment comment = _commentService.findByPrimaryKey( nIdComment );

            if ( comment != null )
            {
                try
                {
                    _commentService.remove( nIdComment );
                }
                catch ( Exception ex )
                {
                    // Something wrong happened... a database check might be needed
                    AppLogService.error( ex.getMessage( ) + " when updating a comment", ex );

                    return AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE,
                            AdminMessage.TYPE_ERROR );
                }

                String strPostBackUrl = (String) request.getSession( ).getAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL );
                request.getSession( ).setAttribute(
                        CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL, null );
                if ( StringUtils.isEmpty( strPostBackUrl ) )
                {
                    strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
                }
                UrlItem url = new UrlItem( strPostBackUrl );
                url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE,
                        CommentResourceExtender.EXTENDER_TYPE_COMMENT );
                addIdExtendableResourceInUrl(comment.getIdExtendableResource(), request, url);
                url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE,
                        comment.getExtendableResourceType( ) );
                if ( comment.getIdParentComment( ) > 0 )
                {
                    url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, comment.getIdParentComment( ) );
                }
                url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                        request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                        CommentConstants.CONSTANT_AND_HTML ) );

                return url.getUrl( );
            }
        }

        return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
    }

    /**
     * Get the comment creation page
     * @param request The request
     * @return The HTML content to display
     */
    public String getCreateComment( HttpServletRequest request )
    {
        setPageTitleProperty( MESSAGE_TITLE_CREATE_COMMENT );

        String strExtendableResourceType = request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strIdExtendableResource = request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strIdParentComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
        String strViewAllResources = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES);

        AdminUser user = AdminUserService.getAdminUser( request );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( CommentConstants.MARK_ALL_RESOURCES, !StringUtils.isEmpty(strViewAllResources) && new Boolean(strViewAllResources) );
        model.put( CommentConstants.MARK_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );
        model.put( CommentConstants.MARK_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
        model.put( CommentConstants.PARAMETER_ID_COMMENT, strIdParentComment );
        model.put( CommentConstants.PARAMETER_NAME, user.getFirstName( ) + CONSTANT_SPACE
                + user.getLastName( ).toUpperCase( ) );
        model.put( CommentConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( CommentConstants.MARK_LOCALE, AdminUserService.getLocale( request ) );
        model.put( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                CommentConstants.CONSTANT_AND_HTML ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_COMMENT,
                AdminUserService.getLocale( request ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do create a comment
     * @param request The request
     * @return The URL to redirect to
     */
    public String doCreateComment( HttpServletRequest request )
    {
        String strExtendableResourceType = request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strIdExtendableResource = request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strIdParentComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
        String strName = request.getParameter( CommentConstants.PARAMETER_NAME );
       
        String strComment = request.getParameter( CommentConstants.MARK_COMMENT );
        if ( StringUtils.isEmpty( strComment ) || StringUtils.isEmpty( strName ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, AdminMessage.TYPE_STOP );
        }

        int nIdParentComment = 0;
        if ( StringUtils.isNotEmpty( strIdParentComment ) && StringUtils.isNumeric( strIdParentComment ) )
        {
            nIdParentComment = Integer.parseInt( strIdParentComment );
            // We check that the parent has no parent. If it has one, then we use it instead
            if ( nIdParentComment > 0 )
            {
                Comment parrentComment = _commentService.findByPrimaryKey( nIdParentComment );
                // If the parent has a parent
                if ( parrentComment.getIdParentComment( ) > 0 )
                {
                    nIdParentComment = parrentComment.getIdParentComment( );
                }
            }
        }
        AdminUser user = AdminUserService.getAdminUser( request );

        Comment comment = new Comment( );
        comment.setIdExtendableResource( strIdExtendableResource );
        comment.setExtendableResourceType( strExtendableResourceType );
        comment.setIdParentComment( nIdParentComment );
        comment.setComment( strComment );
        Timestamp currentDate = new Timestamp( new Date( ).getTime( ) );
        comment.setDateComment( currentDate );
        comment.setDateLastModif( currentDate );
        comment.setName( strName );
        comment.setEmail( user.getEmail( ) );
        comment.setPublished( true );
        comment.setIpAddress( SecurityUtil.getRealIp( request ) );
        comment.setIsAdminComment( true );

        _commentService.create( comment, request );
        _resourceHistoryService.create( CommentResourceExtender.EXTENDER_TYPE_COMMENT, strIdExtendableResource,
                strExtendableResourceType, request );

        // we redirect the user to the manage comment page
        String strPostBackUrl = (String) request.getSession( ).getAttribute(
                CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL );
        request.getSession( ).setAttribute(
                CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL, null );
        if ( StringUtils.isEmpty( strPostBackUrl ) )
        {
            strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
        }
        UrlItem url = new UrlItem( strPostBackUrl );
        url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE, CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        addIdExtendableResourceInUrl(strIdExtendableResource, request, url);
         url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );

        if ( nIdParentComment > 0 )
        {
            url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, nIdParentComment );
        }
        url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                CommentConstants.CONSTANT_AND_HTML ) );

        return url.getUrl( );
    }
    
    /**
     * Get the modify creation page, only for admin comments
     * 
     * @param request The request
     * @return The HTML content to display
     */
    public String getModifyAdminComment( HttpServletRequest request ) throws AccessDeniedException
    {
        setPageTitleProperty( MESSAGE_TITLE_MODIFY_ADMIN_COMMENT );

        String strExtendableResourceType = request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strIdExtendableResource   = request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strIdComment              = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
        String strIdParentComment        = request.getParameter( CommentConstants.PARAMETER_ID_PARENT_COMMENT );
        String strViewAllResources       = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES);

        // AdminUser user = AdminUserService.getAdminUser( request );
        
        // Getting the comment to modify
        int nIdComment = Integer.parseInt( strIdComment );
        Comment comment = _commentService.findByPrimaryKey( nIdComment );

        // Only admin comment can be modified 
        if ( !comment.getIsAdminComment( ) )
        {
        	throw new AccessDeniedException(CommentConstants.MESSAGE_ERROR_CANNOT_MODIFY_USER_COMMENT);
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        
        model.put( CommentConstants.MARK_ALL_RESOURCES, !StringUtils.isEmpty(strViewAllResources) && new Boolean(strViewAllResources) );
        model.put( CommentConstants.MARK_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );
        model.put( CommentConstants.MARK_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
        model.put( CommentConstants.MARK_ID_PARENT_COMMENT, strIdParentComment );
        
        model.put( CommentConstants.MARK_COMMENT, comment );
        
        model.put( CommentConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( CommentConstants.MARK_LOCALE, AdminUserService.getLocale( request ) );
        model.put( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace( request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND, CommentConstants.CONSTANT_AND_HTML ) );

        // Token for CSRF security
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, TEMPLATE_MODIFY_ADMIN_COMMENT ) );
        
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ADMIN_COMMENT, AdminUserService.getLocale( request ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do modify an admin comment
     * @param request The request
     * @return The URL to redirect to
     */
    public String doModifyAdminComment( HttpServletRequest request ) throws AccessDeniedException
    {
        
    	String strExtendableResourceType = request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strIdExtendableResource   = request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strIdComment              = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
        String strIdParentComment        = request.getParameter( CommentConstants.PARAMETER_ID_PARENT_COMMENT );
        String strName                   = request.getParameter( CommentConstants.PARAMETER_NAME );
        String strComment                = request.getParameter( CommentConstants.MARK_COMMENT );
        
        int nIdComment = Integer.parseInt( strIdComment );
        Comment comment = _commentService.findByPrimaryKey( nIdComment );
        
        // Only admin comment can be modified 
        if ( !comment.getIsAdminComment( ) )
        {
        	 return AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_CANNOT_MODIFY_USER_COMMENT, AdminMessage.TYPE_STOP );
        }

        if ( StringUtils.isEmpty( strComment ) )
        {
        	 Object[] tabRequiredFields = { CommentConstants.MARK_COMMENT };
        	 return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( StringUtils.isEmpty( strName ) )
        {
        	 Object[] tabRequiredFields = { CommentConstants.PARAMETER_NAME };
        	 return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        // Validating CSRF token
    	if ( !SecurityTokenService.getInstance( ).validate( request, TEMPLATE_MODIFY_ADMIN_COMMENT ) )
    	{
    		throw new AccessDeniedException( "Invalid security token" );
    	}
    	
        AdminUser user = AdminUserService.getAdminUser( request );

        comment.setName   ( strName );
        comment.setComment( strComment );

        _commentService.update( comment );
        
        // we redirect the user to the manage comment page
        String strPostBackUrl = (String) request.getSession( ).getAttribute(
                CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL );
        request.getSession( ).setAttribute(
                CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL, null );
        if ( StringUtils.isEmpty( strPostBackUrl ) )
        {
            strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
        }
        UrlItem url = new UrlItem( strPostBackUrl );
        url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE, CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        addIdExtendableResourceInUrl(strIdExtendableResource, request, url);
         url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );

        if ( StringUtils.isNotEmpty( strIdParentComment ) )
        {
            url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, strIdParentComment );
        }
        url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace(
                request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                CommentConstants.CONSTANT_AND_HTML ) );

        return url.getUrl( );
    }

    private void addIdExtendableResourceInUrl(String strIdExtendableResource,HttpServletRequest request,UrlItem url )
    {
    	 String strViewAllResources = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES);
    	 if(!StringUtils.isEmpty(strViewAllResources) && new Boolean(strViewAllResources))
         {
         	url.addParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE, CommentConstants.CONSTANT_ALL_RESSOURCE_ID);
         }
         else
         {
         	url.addParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
         }
    	
    }
    private void addViewResourceInUrl(HttpServletRequest request,UrlItem url )
    {
    
    	 String strViewAllResources = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES);
         if(!StringUtils.isEmpty(strViewAllResources))
         {
         	url.addParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES, strViewAllResources);
         }
          
    }
    
    
    
}
