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
package fr.paris.lutece.plugins.extend.modules.comment.service;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.avatar.service.AvatarService;
import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * 
 * PluginAvatarCommentAvatarService : implementation based on the plugin-avatar.
 * <p>
 * This is the only class of the module referencing the plugin-avatar API. It must never be referenced statically : it is instantiated by reflection by
 * {@link CommentAvatarService} once the plugin-avatar has been detected on the classpath.
 * </p>
 *
 */
public class PluginAvatarCommentAvatarService implements ICommentAvatarService
{

    private final boolean _bUseLuteceUserNameAsAvatarKey = AppPropertiesService.getPropertyBoolean(
            CommentConstants.PROPERTY_USE_LUTECE_USER_NAME_AS_AVATAR_KEY, false );

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAvatar( Comment comment )
    {
        return AvatarService.getAvatar( getAvatarKey( comment ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAvatarUrl( Comment comment )
    {
        return AvatarService.getAvatarUrl( getAvatarKey( comment ) );
    }

    /**
     * Get the key identifying the avatar of the comment's author
     * 
     * @param comment
     *            The comment
     * @return the lutece user name if the module is configured to use it and if it is available, the email otherwise
     */
    private String getAvatarKey( Comment comment )
    {
        if ( _bUseLuteceUserNameAsAvatarKey && !StringUtils.isEmpty( comment.getLuteceUserName( ) ) )
        {
            return comment.getLuteceUserName( );
        }

        return comment.getEmail( );
    }

}
