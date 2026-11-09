/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.extend.modules.comment.service;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Avatar of the comment authors. The plugin-avatar is an optional dependency: when it is deployed (and the avatars are enabled), the avatars come from it;
 * otherwise the comments are displayed without avatar. This class must not reference the plugin-avatar classes, they are only reached through
 * {@link PluginAvatarCommentAvatarService}, loaded by reflection.
 */
@ApplicationScoped
public class CommentAvatarService implements ICommentAvatarService
{
    private static final String CLASS_AVATAR_SERVICE = "fr.paris.lutece.plugins.avatar.service.AvatarService";

    @Inject
    @ConfigProperty( name = CommentConstants.PROPERTY_AVATAR_ENABLED, defaultValue = "true" )
    private boolean _bAvatarEnabled;

    @Inject
    @ConfigProperty( name = CommentConstants.PROPERTY_USE_LUTECE_USER_NAME_AS_AVATAR_KEY, defaultValue = "false" )
    private boolean _bUseLuteceUserNameAsAvatarKey;

    private ICommentAvatarService _avatarProvider;

    /**
     * Chooses the avatar provider once the configuration is injected.
     */
    @PostConstruct
    void init( )
    {
        _avatarProvider = _bAvatarEnabled ? newPluginAvatarProvider( ) : null;
        if ( _avatarProvider == null )
        {
            AppLogService.info( "module-extend-comment : no avatar provider, comments are displayed without avatar" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAvatar( Comment comment )
    {
        return ( _avatarProvider != null ) ? _avatarProvider.getAvatar( comment ) : StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAvatarUrl( Comment comment )
    {
        return ( _avatarProvider != null ) ? _avatarProvider.getAvatarUrl( comment ) : StringUtils.EMPTY;
    }

    /**
     * Builds the provider based on the plugin-avatar, if this plugin is deployed.
     *
     * @return the provider, or null when the plugin-avatar is not deployed
     */
    private ICommentAvatarService newPluginAvatarProvider( )
    {
        try
        {
            Class.forName( CLASS_AVATAR_SERVICE );
        }
        catch( ClassNotFoundException | LinkageError e )
        {
            // The plugin-avatar is not deployed: a valid setup, the dependency is optional
            return null;
        }
        AppLogService.info( "module-extend-comment : avatars provided by the plugin-avatar" );
        return new PluginAvatarCommentAvatarService( _bUseLuteceUserNameAsAvatarKey );
    }
}
