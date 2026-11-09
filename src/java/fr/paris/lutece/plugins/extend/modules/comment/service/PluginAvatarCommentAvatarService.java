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

import fr.paris.lutece.plugins.avatar.service.AvatarService;
import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;

/**
 * Avatar provider based on the plugin-avatar. Not a CDI bean: it is only instantiated by {@link CommentAvatarService} once the plugin-avatar classes are
 * known to be deployed.
 */
class PluginAvatarCommentAvatarService implements ICommentAvatarService
{
    private final boolean _bUseLuteceUserNameAsAvatarKey;

    /**
     * Constructor
     *
     * @param bUseLuteceUserNameAsAvatarKey
     *            true to use the lutece user name as avatar key when the comment has one, false to use the email
     */
    PluginAvatarCommentAvatarService( boolean bUseLuteceUserNameAsAvatarKey )
    {
        _bUseLuteceUserNameAsAvatarKey = bUseLuteceUserNameAsAvatarKey;
    }

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
     * Gets the key of the avatar of a comment author.
     *
     * @param comment
     *            the comment
     * @return the lutece user name or the email of the author
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
