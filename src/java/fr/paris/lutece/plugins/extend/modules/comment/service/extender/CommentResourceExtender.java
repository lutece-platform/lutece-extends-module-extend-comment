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
package fr.paris.lutece.plugins.extend.modules.comment.service.extender;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.AbstractResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.plugins.extend.modules.comment.web.component.CommentResourceExtenderComponent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 *
 * CommentResourceExtender
 *
 */
@ApplicationScoped
@Named( "extend-comment.commentResourceExtender" )
public class CommentResourceExtender extends AbstractResourceExtender
{
    /** The Constant EXTENDER_TYPE_COMMENT. */
    public static final String EXTENDER_TYPE_COMMENT = "comment";
    private static final String EXTENDABLE_RESOURCE_TYPE_COMMENT = "comment";

    @Inject
    @Named( "extend-comment.commentExtenderConfigService" )
    private IResourceExtenderConfigService _configService;
    
    @Inject
    @Named( "extend-comment.commentService" )
    private ICommentService _commentService;

    @Inject
    @ConfigProperty( name = "extend.comment.titleKey", defaultValue = "module.comment.rating.extender.rating.label" )
    private String titleKey;

    @Inject
    private CommentResourceExtenderComponent resourceExtenderComponent;

    CommentResourceExtender( )
    {

    }

    @PostConstruct
    public void producesCommentResourceExtender( )
    {
        setResourceExtenderComponent( resourceExtenderComponent );
        setKey( EXTENDABLE_RESOURCE_TYPE_COMMENT );
        setI18nTitleKey( I18nService.getLocalizedString( titleKey, Locale.getDefault( ) ) );
        setConfigRequired( true );
        setHistoryEnable( true );
        setStateEnable( true );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( String strExtenderType )
    {
        if ( StringUtils.isNotBlank( strExtenderType ) )
        {
            return getKey( ).equals( strExtenderType );
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContent( String strIdExtendableResource, String strExtendableResourceType, String strParameters, HttpServletRequest request )
    {
        if ( StringUtils.equals( EXTENDABLE_RESOURCE_TYPE_COMMENT, strExtendableResourceType ) )
        {
            return StringUtils.EMPTY;
        }
        return getResourceExtenderComponent( ).getPageAddOn( strIdExtendableResource, strExtendableResourceType, strParameters, request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doCreateResourceAddOn( ResourceExtenderDTO extender )
    {
        CommentExtenderConfig config = new CommentExtenderConfig( );

        // Default values
        CommentExtenderConfig defaultConfig = _configService.find( -1 );
        if ( defaultConfig != null )
        {
            config = defaultConfig;
        }
        else
        {
            config.setModerated( false );
        }
        config.setIdExtender( extender.getIdExtender( ) );
        _configService.create( config );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDeleteResourceAddOn( ResourceExtenderDTO extender )
    {
        if ( extender.getIdExtender( ) > 0 )
        {
            _configService.remove( extender.getIdExtender( ) );
        }
        _commentService.removeByResource( extender.getIdExtendableResource( ), extender.getExtendableResourceType( ) );
    }
}
