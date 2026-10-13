/*
 * Copyright (c) 2002-2021, City of Paris
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
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentPlugin;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.CommentUtils;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.web.ResourceExtenderJspBean;
import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.workflowcore.exception.ResourceWorkflowLockedException;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.http.SecurityUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Back office controller of the comments of a resource.
 */
@RequestScoped
@Named( "extendCommentJspBean" )
@Controller( controllerJsp = "ManageComments.jsp", controllerPath = "jsp/admin/plugins/extend/modules/comment/", right = ResourceExtenderJspBean.RIGHT_MANAGE_RESOURCE_EXTENDER, securityTokenEnabled = true )
public class CommentJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -1787559203779077088L;

    private static final String VIEW_CREATE_COMMENT = "createComment";
    private static final String VIEW_CONFIRM_REMOVE_COMMENT = "confirmRemoveComment";
    private static final String VIEW_TASKS_FORM_WORKFLOW = "tasksFormWorkflow";
    private static final String VIEW_CONFIRM_UPDATE_COMMENT = "confirmUpdateComment";

    private static final String ACTION_CREATE_COMMENT = "createComment";
    private static final String ACTION_REMOVE_COMMENT = "removeComment";
    private static final String ACTION_SAVE_TASK_FORM = "saveTaskForm";

    private static final String JSP_VIEW_EXTENDER_INFO = "../../ViewExtenderInfo.jsp";

    private static final String MESSAGE_TITLE_CREATE_COMMENT = "module.extend.comment.create_comment.pageTitle";
    private static final String MESSAGE_CONFIRM_UPDATE_COMMENT = "module.extend.comment.message.confirm.updateComment";
    private static final String MESSAGE_TITLE_TASKS_FORM_WORKFLOW = "module.extend.comment.comment_task_form_workflow.page_title";

    private static final String TEMPLATE_CREATE_COMMENT = "admin/plugins/extend/modules/comment/create_comment.html";
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/extend/modules/comment/tasks_form_workflow.html";

    private static final String MARK_TASK_FORM = "tasks_form";

    private static final String CONSTANT_SPACE = " ";

    private static final Map<String, String> MAP_OPERATION_LABELS = Map.of( CommentConstants.OPERATION_PUBLISH, "module.extend.comment.comment_info.buttonPublish",
            CommentConstants.OPERATION_UNPUBLISH, "module.extend.comment.comment_info.buttonUnpublish", CommentConstants.OPERATION_FLAG_IMPORTANT,
            "module.extend.comment.comment_info.labelDoFlagImportant", CommentConstants.OPERATION_CANCEL_FLAG_IMPORTANT,
            "module.extend.comment.comment_info.labelCancelFlagImportant", CommentConstants.OPERATION_PIN, "module.extend.comment.comment_info.labelDoPinned",
            CommentConstants.OPERATION_CANCEL_PIN, "module.extend.comment.comment_info.labelCancelPinned" );

    @Inject
    private ICommentService _commentService;

    @Inject
    private IResourceExtenderHistoryService _resourceHistoryService;

    @Inject
    private IResourceExtenderService _resourceExtenderService;

    @Inject
    private WorkflowService _workflowService;

    @Inject
    @Named( "extend-comment.commentExtenderConfigService" )
    private IResourceExtenderConfigService _configService;

    /**
     * Displays the comment creation form.
     *
     * @param request
     *            the request
     * @param model
     *            the model
     * @return the page
     */
    @View( value = VIEW_CREATE_COMMENT, defaultView = true )
    public String getCreateComment( HttpServletRequest request, Models model )
    {
        String strViewAllResources = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES );
        AdminUser user = getUser( );

        model.put( CommentConstants.MARK_ALL_RESOURCES, StringUtils.isNotEmpty( strViewAllResources ) && Boolean.parseBoolean( strViewAllResources ) );
        model.put( CommentConstants.MARK_EXTENDABLE_RESOURCE_TYPE, request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE ) );
        model.put( CommentConstants.MARK_ID_EXTENDABLE_RESOURCE, request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE ) );
        model.put( CommentConstants.PARAMETER_ID_COMMENT, request.getParameter( CommentConstants.PARAMETER_ID_COMMENT ) );
        model.put( CommentConstants.PARAMETER_NAME, user.getFirstName( ) + CONSTANT_SPACE + user.getLastName( ).toUpperCase( ) );
        model.put( CommentConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( CommentConstants.MARK_LOCALE, getLocale( ) );
        model.put( CommentConstants.PARAMETER_FROM_URL, getFromUrl( request ) );

        return getPage( MESSAGE_TITLE_CREATE_COMMENT, TEMPLATE_CREATE_COMMENT, model );
    }

    /**
     * Creates a comment written by the administrator.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( ACTION_CREATE_COMMENT )
    public String doCreateComment( HttpServletRequest request )
    {
        String strExtendableResourceType = request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strIdExtendableResource = request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strIdParentComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );
        String strName = request.getParameter( CommentConstants.PARAMETER_NAME );
        String strComment = request.getParameter( CommentConstants.MARK_COMMENT );

        if ( StringUtils.isEmpty( strComment ) || StringUtils.isEmpty( strName ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        int nIdParentComment = 0;
        if ( StringUtils.isNumeric( strIdParentComment ) )
        {
            nIdParentComment = Integer.parseInt( strIdParentComment );
            Comment parentComment = nIdParentComment > 0 ? _commentService.findByPrimaryKey( nIdParentComment ) : null;
            if ( parentComment != null && parentComment.getIdParentComment( ) > 0 )
            {
                nIdParentComment = parentComment.getIdParentComment( );
            }
        }

        AdminUser user = getUser( );
        Timestamp currentDate = new Timestamp( new Date( ).getTime( ) );

        Comment comment = new Comment( );
        comment.setIdExtendableResource( strIdExtendableResource );
        comment.setExtendableResourceType( strExtendableResourceType );
        comment.setIdParentComment( nIdParentComment );
        comment.setComment( strComment );
        comment.setDateComment( currentDate );
        comment.setDateLastModif( currentDate );
        comment.setName( strName );
        comment.setEmail( user.getEmail( ) );
        comment.setPublished( true );
        comment.setIpAddress( SecurityUtil.getRealIp( request ) );
        comment.setIsAdminComment( true );

        try
        {
            _commentService.create( comment, request );
        }
        catch( Exception ex )
        {
            AppLogService.error( "{} when creating a comment", ex.getMessage( ), ex );
            if ( comment.getIdComment( ) > 0 )
            {
                _commentService.remove( comment.getIdComment( ) );
            }

            return redirect( request, AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE, AdminMessage.TYPE_ERROR ) );
        }
        _resourceHistoryService.create( CommentResourceExtender.EXTENDER_TYPE_COMMENT, strIdExtendableResource, strExtendableResourceType,
                CommentUtils.getLuteceUserGuid( request ), request.getRemoteAddr( ) );

        return redirect( request, getPostBackUrl( request, strIdExtendableResource, strExtendableResourceType, nIdParentComment ) );
    }

    /**
     * Asks the confirmation of an operation or a workflow action on a comment.
     *
     * @param request
     *            the request
     * @return the redirection to the confirmation message
     */
    @View( value = VIEW_CONFIRM_UPDATE_COMMENT, securityTokenAction = CommentConstants.ACTION_UPDATE_COMMENT )
    public String getConfirmUpdateComment( HttpServletRequest request )
    {
        Comment comment = getComment( request );
        String strIdAction = request.getParameter( CommentConstants.PARAMETER_ID_ACTION );
        String strOperation = request.getParameter( CommentConstants.PARAMETER_OPERATION );
        String strLabel = null;

        if ( comment != null && StringUtils.isNumeric( strIdAction ) )
        {
            strLabel = getWorkflowActionName( comment, Integer.parseInt( strIdAction ) );
        }
        else
            if ( comment != null && MAP_OPERATION_LABELS.containsKey( strOperation ) )
            {
                strLabel = I18nService.getLocalizedString( MAP_OPERATION_LABELS.get( strOperation ), getLocale( ) );
            }

        if ( strLabel == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( CommentConstants.ACTION_UPDATE_COMMENT ) );
        url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, comment.getIdComment( ) );
        if ( StringUtils.isNumeric( strIdAction ) )
        {
            url.addParameter( CommentConstants.PARAMETER_ID_ACTION, strIdAction );
        }
        else
        {
            url.addParameter( CommentConstants.PARAMETER_OPERATION, strOperation );
        }
        url.addParameter( CommentConstants.PARAMETER_FROM_URL, getFromUrl( request ) );
        addViewResourceInUrl( request, url );

        Object [ ] args = {
                strLabel
        };

        return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_UPDATE_COMMENT, args, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Applies an operation or a workflow action to a comment.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( CommentConstants.ACTION_UPDATE_COMMENT )
    public String doUpdateComment( HttpServletRequest request )
    {
        Comment comment = getComment( request );

        if ( comment == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        String strIdAction = request.getParameter( CommentConstants.PARAMETER_ID_ACTION );

        if ( StringUtils.isNumeric( strIdAction ) )
        {
            return processWorkflowAction( request, comment, Integer.parseInt( strIdAction ) );
        }

        String strOperation = StringUtils.defaultString( request.getParameter( CommentConstants.PARAMETER_OPERATION ) );

        try
        {
            switch( strOperation )
            {
                case CommentConstants.OPERATION_PUBLISH:
                    _commentService.updateCommentStatus( comment.getIdComment( ), true );
                    break;
                case CommentConstants.OPERATION_UNPUBLISH:
                    _commentService.updateCommentStatus( comment.getIdComment( ), false );
                    break;
                case CommentConstants.OPERATION_FLAG_IMPORTANT:
                    _commentService.updateFlagImportant( comment.getIdComment( ), true );
                    break;
                case CommentConstants.OPERATION_CANCEL_FLAG_IMPORTANT:
                    _commentService.updateFlagImportant( comment.getIdComment( ), false );
                    break;
                case CommentConstants.OPERATION_PIN:
                    _commentService.updateCommentPinned( comment.getIdComment( ), true );
                    break;
                case CommentConstants.OPERATION_CANCEL_PIN:
                    _commentService.updateCommentPinned( comment.getIdComment( ), false );
                    break;
                default:
                    return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
            }
        }
        catch( Exception ex )
        {
            AppLogService.error( "{} when updating a comment", ex.getMessage( ), ex );

            return redirect( request, AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE, AdminMessage.TYPE_ERROR ) );
        }

        return redirect( request, getPostBackUrl( request, comment ) );
    }

    /**
     * Asks the confirmation of the removal of a comment.
     *
     * @param request
     *            the request
     * @return the redirection to the confirmation message
     */
    @View( value = VIEW_CONFIRM_REMOVE_COMMENT, securityTokenAction = ACTION_REMOVE_COMMENT )
    public String getConfirmRemoveComment( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_COMMENT ) );
        url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, request.getParameter( CommentConstants.PARAMETER_ID_COMMENT ) );
        url.addParameter( CommentConstants.PARAMETER_FROM_URL, getFromUrl( request ) );
        addViewResourceInUrl( request, url );

        return redirect( request,
                AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_CONFIRM_REMOVE_COMMENT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Removes a comment.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( ACTION_REMOVE_COMMENT )
    public String doRemoveComment( HttpServletRequest request )
    {
        Comment comment = getComment( request );

        if ( comment == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        try
        {
            _commentService.remove( comment.getIdComment( ) );
        }
        catch( Exception ex )
        {
            AppLogService.error( "{} when removing a comment", ex.getMessage( ), ex );

            return redirect( request, AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE, AdminMessage.TYPE_ERROR ) );
        }

        return redirect( request, getPostBackUrl( request, comment ) );
    }

    /**
     * Displays the tasks form of a workflow action.
     *
     * @param request
     *            the request
     * @param model
     *            the model
     * @return the page
     */
    @View( value = VIEW_TASKS_FORM_WORKFLOW, securityTokenAction = ACTION_SAVE_TASK_FORM )
    public String getTasksFormWorkflow( HttpServletRequest request, Models model )
    {
        String strIdAction = request.getParameter( CommentConstants.PARAMETER_ID_ACTION );
        Comment comment = getComment( request );

        if ( comment == null || !StringUtils.isNumeric( strIdAction ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        String strResourceType = _commentService.getResourceType( comment.getExtendableResourceType( ) );
        String strHtmlTasksForm = _workflowService.getDisplayTasksForm( comment.getIdComment( ), strResourceType, Integer.parseInt( strIdAction ), request,
                getLocale( ), getUser( ) );

        model.put( CommentConstants.PARAMETER_ID_ACTION, strIdAction );
        model.put( CommentConstants.PARAMETER_ID_COMMENT, Integer.toString( comment.getIdComment( ) ) );
        model.put( MARK_TASK_FORM, strHtmlTasksForm );

        return getPage( MESSAGE_TITLE_TASKS_FORM_WORKFLOW, TEMPLATE_TASKS_FORM_WORKFLOW, model );
    }

    /**
     * Saves the tasks form of a workflow action and processes the action.
     *
     * @param request
     *            the request
     * @return the redirection
     */
    @Action( ACTION_SAVE_TASK_FORM )
    public String doSaveTaskForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( CommentConstants.PARAMETER_ID_ACTION );
        Comment comment = getComment( request );

        if ( comment == null || !StringUtils.isNumeric( strIdAction ) )
        {
            return redirect( request, JSP_VIEW_EXTENDER_INFO );
        }

        int nIdAction = Integer.parseInt( strIdAction );
        String strResourceType = _commentService.getResourceType( comment.getExtendableResourceType( ) );
        int nIdExtender = getIdExtender( comment );

        try
        {
            String strError = _workflowService.doSaveTasksForm( comment.getIdComment( ), strResourceType, nIdAction, nIdExtender, request, getLocale( ),
                    getUser( ) );

            if ( strError != null )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_GENERIC_MESSAGE, AdminMessage.TYPE_ERROR ) );
            }

            _workflowService.doProcessAction( comment.getIdComment( ), strResourceType, nIdAction, nIdExtender, request, getLocale( ), false, getUser( ) );
        }
        catch( AppException e )
        {
            if ( e.getCause( ) instanceof ResourceWorkflowLockedException )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_ACTION_LOCKED, AdminMessage.TYPE_STOP ) );
            }

            AppLogService.error( "Error processing action for record {}", comment.getIdComment( ), e );
        }

        return redirect( request, getPostBackUrl( request, comment ) );
    }

    /**
     * Processes a workflow action on a comment, or shows its tasks form first.
     *
     * @param request
     *            the request
     * @param comment
     *            the comment
     * @param nIdAction
     *            the workflow action id
     * @return the redirection
     */
    private String processWorkflowAction( HttpServletRequest request, Comment comment, int nIdAction )
    {
        String strResourceType = _commentService.getResourceType( comment.getExtendableResourceType( ) );

        try
        {
            if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale( ) ) )
            {
                Map<String, String> mapParameters = new HashMap<>( );
                mapParameters.put( CommentConstants.PARAMETER_ID_ACTION, Integer.toString( nIdAction ) );
                mapParameters.put( CommentConstants.PARAMETER_ID_COMMENT, Integer.toString( comment.getIdComment( ) ) );

                return redirect( request, VIEW_TASKS_FORM_WORKFLOW, mapParameters );
            }

            _workflowService.doProcessAction( comment.getIdComment( ), strResourceType, nIdAction, getIdExtender( comment ), request, getLocale( ), false,
                    getUser( ) );
        }
        catch( Exception e )
        {
            if ( e.getCause( ) instanceof ResourceWorkflowLockedException )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, CommentConstants.MESSAGE_ERROR_ACTION_LOCKED, AdminMessage.TYPE_STOP ) );
            }

            AppLogService.error( "Error processing action for record {}", comment.getIdComment( ), e );
        }

        return redirect( request, getPostBackUrl( request, comment ) );
    }

    /**
     * Gets the name of a workflow action the current user may run on a comment.
     *
     * @param comment
     *            the comment
     * @param nIdAction
     *            the workflow action id
     * @return the action name, or null when the action is not available
     */
    private String getWorkflowActionName( Comment comment, int nIdAction )
    {
        CommentExtenderConfig config = _configService.find( getIdExtender( comment ) );

        if ( config == null || !_workflowService.isAvailable( ) )
        {
            return null;
        }

        return _workflowService
                .getActions( comment.getIdComment( ), _commentService.getResourceType( comment.getExtendableResourceType( ) ), config.getIdWorkflow( ),
                        (User) getUser( ) )
                .stream( ).filter( action -> action.getId( ) == nIdAction ).map( action -> action.getName( ) ).findFirst( ).orElse( null );
    }

    /**
     * Loads the comment named by the request.
     *
     * @param request
     *            the request
     * @return the comment, or null when the id is missing or unknown
     */
    private Comment getComment( HttpServletRequest request )
    {
        String strIdComment = request.getParameter( CommentConstants.PARAMETER_ID_COMMENT );

        if ( StringUtils.isNotBlank( strIdComment ) && StringUtils.isNumeric( strIdComment ) )
        {
            return _commentService.findByPrimaryKey( Integer.parseInt( strIdComment ) );
        }

        return null;
    }

    /**
     * Gets the origin URL parameter, its ampersands encoded.
     *
     * @param request
     *            the request
     * @return the origin URL
     */
    private String getFromUrl( HttpServletRequest request )
    {
        return Strings.CS.replace( request.getParameter( CommentConstants.PARAMETER_FROM_URL ), CommentConstants.CONSTANT_AND,
                CommentConstants.CONSTANT_AND_HTML );
    }

    /**
     * Adds the resource id to the URL, or the wildcard when every resource is displayed.
     *
     * @param strIdExtendableResource
     *            the resource id
     * @param request
     *            the request
     * @param url
     *            the URL
     */
    private void addIdExtendableResourceInUrl( String strIdExtendableResource, HttpServletRequest request, UrlItem url )
    {
        String strViewAllResources = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES );
        if ( StringUtils.isNotEmpty( strViewAllResources ) && Boolean.parseBoolean( strViewAllResources ) )
        {
            url.addParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE, CommentConstants.CONSTANT_ALL_RESSOURCE_ID );
        }
        else
        {
            url.addParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
        }
    }

    /**
     * Adds the view all resources parameter to the URL when the request carries it.
     *
     * @param request
     *            the request
     * @param url
     *            the URL
     */
    private void addViewResourceInUrl( HttpServletRequest request, UrlItem url )
    {
        String strViewAllResources = request.getParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES );
        if ( StringUtils.isNotEmpty( strViewAllResources ) )
        {
            url.addParameter( CommentConstants.PARAMETER_VIEW_ALL_RESOURCES, strViewAllResources );
        }
    }

    /**
     * Gets the id of the comment extender of the resource of a comment.
     *
     * @param comment
     *            the comment
     * @return the extender id
     */
    private int getIdExtender( Comment comment )
    {
        ResourceExtenderDTOFilter filter = new ResourceExtenderDTOFilter( );
        filter.setFilterExtendableResourceType( comment.getExtendableResourceType( ) );
        filter.setFilterIdExtendableResource( comment.getIdExtendableResource( ) );
        filter.setFilterExtenderType( CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        filter.setIncludeWildcardResource( true );

        List<ResourceExtenderDTO> listResourceExtender = _resourceExtenderService.findByFilter( filter );
        return listResourceExtender.get( 0 ).getIdExtender( );
    }

    /**
     * Gets the URL of the page that displays a comment.
     *
     * @param request
     *            the request
     * @param comment
     *            the comment
     * @return the URL
     */
    private String getPostBackUrl( HttpServletRequest request, Comment comment )
    {
        return getPostBackUrl( request, comment.getIdExtendableResource( ), comment.getExtendableResourceType( ), comment.getIdParentComment( ) );
    }

    /**
     * Gets the URL of the comments page saved in session, reset after use.
     *
     * @param request
     *            the request
     * @param strIdExtendableResource
     *            the resource id
     * @param strExtendableResourceType
     *            the resource type
     * @param nIdParentComment
     *            the parent comment id, 0 for none
     * @return the URL
     */
    private String getPostBackUrl( HttpServletRequest request, String strIdExtendableResource, String strExtendableResourceType, int nIdParentComment )
    {
        String strSessionKey = CommentPlugin.PLUGIN_NAME + CommentConstants.SESSION_COMMENT_ADMIN_POST_BACK_URL;
        String strPostBackUrl = (String) request.getSession( ).getAttribute( strSessionKey );
        request.getSession( ).setAttribute( strSessionKey, null );

        if ( StringUtils.isEmpty( strPostBackUrl ) )
        {
            strPostBackUrl = JSP_VIEW_EXTENDER_INFO;
        }

        UrlItem url = new UrlItem( strPostBackUrl );
        url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE, CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        addIdExtendableResourceInUrl( strIdExtendableResource, request, url );
        url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );

        if ( nIdParentComment > 0 )
        {
            url.addParameter( CommentConstants.PARAMETER_ID_COMMENT, nIdParentComment );
        }

        url.addParameter( CommentConstants.PARAMETER_FROM_URL, getFromUrl( request ) );

        return url.getUrl( );
    }
}
