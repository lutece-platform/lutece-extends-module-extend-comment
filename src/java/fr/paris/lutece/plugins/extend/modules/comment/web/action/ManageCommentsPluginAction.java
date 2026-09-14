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
package fr.paris.lutece.plugins.extend.modules.comment.web.action;

import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.portal.web.resource.IExtendableResourcePluginAction;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * 
 * ManageCommentsPluginAction
 * 
 */
@ApplicationScoped
@Named( "extend-comment.manageCommentsAction" )
public class ManageCommentsPluginAction extends AbstractPluginAction<IExtendableResource> implements IExtendableResourcePluginAction
{
    private static final String ACTION_NAME = "Manage comments";

    // PARAMETERS
    private static final String PARAMETER_MANAGE_COMMENTS = "manageComments";

    // TEMPLATE
    private static final String TEMPLATE_BUTTON = "../plugins/extend/modules/comment/actions/install_comment.html";

    // JSP
    private static final String JSP_URL = "jsp/admin/plugins/extend/ViewExtenderInfo.jsp";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        return request.getParameter( PARAMETER_MANAGE_COMMENTS ) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getButtonTemplate( )
    {
        return TEMPLATE_BUTTON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return ACTION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser, IExtendableResource sessionFields )
            throws AccessDeniedException
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL );
        url.addParameter( CommentConstants.PARAMETER_EXTENDER_TYPE, CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        url.addParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE, request.getParameter( CommentConstants.PARAMETER_ID_EXTENDABLE_RESOURCE ) );
        url.addParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE, request.getParameter( CommentConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE ) );
        url.addParameter( CommentConstants.PARAMETER_FROM_URL, StringUtils.replace( request.getHeader( CommentConstants.PARAMETER_REFERER ),
                CommentConstants.CONSTANT_AND, CommentConstants.CONSTANT_AND_HTML ) );

        DefaultPluginActionResult result = new DefaultPluginActionResult( );
        result.setRedirect( url.getUrl( ) );

        return result;
    }
}
