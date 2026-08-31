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

import java.util.List;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;

/**
 * 
 * CommentAvatarService : provides the {@link ICommentAvatarService} implementation to use.
 * <p>
 * The plugin-avatar is an optional dependency of this module. The implementation is resolved once, in this order :
 * </p>
 * <ol>
 * <li>a bean of type {@link ICommentAvatarService} declared in a Spring context (lets a site plug its own implementation),</li>
 * <li>{@link PluginAvatarCommentAvatarService} if the plugin-avatar is available and if the property
 * <code>module.extend.comment.avatar.enabled</code> is not set to false,</li>
 * <li>{@link DefaultCommentAvatarService}, which displays no avatar at all.</li>
 * </ol>
 *
 */
public final class CommentAvatarService
{

    /** Class of the plugin-avatar API. Referenced by name only : the plugin may not be deployed. */
    private static final String CLASS_AVATAR_SERVICE = "fr.paris.lutece.plugins.avatar.service.AvatarService";
    private static final String CLASS_PLUGIN_AVATAR_COMMENT_AVATAR_SERVICE = "fr.paris.lutece.plugins.extend.modules.comment.service.PluginAvatarCommentAvatarService";

    private static volatile ICommentAvatarService _singleton;

    /** Private constructor */
    private CommentAvatarService( )
    {
    }

    /**
     * 
     * @return the {@link ICommentAvatarService} implementation to use. Never null.
     */
    public static ICommentAvatarService getInstance( )
    {
        ICommentAvatarService service = _singleton;

        if ( service == null )
        {
            synchronized( CommentAvatarService.class )
            {
                service = _singleton;

                if ( service == null )
                {
                    service = resolveService( );
                    _singleton = service;
                }
            }
        }

        return service;
    }

    /**
     * Resolve the implementation to use
     * 
     * @return the implementation. Never null.
     */
    private static ICommentAvatarService resolveService( )
    {
        List<ICommentAvatarService> listServices = SpringContextService.getBeansOfType( ICommentAvatarService.class );

        if ( listServices != null && !listServices.isEmpty( ) )
        {
            ICommentAvatarService service = listServices.get( 0 );
            AppLogService.info( "module-extend-comment : avatar service provided by the bean " + service.getClass( ).getName( ) );

            return service;
        }

        if ( AppPropertiesService.getPropertyBoolean( CommentConstants.PROPERTY_AVATAR_ENABLED, true ) )
        {
            ICommentAvatarService service = newPluginAvatarService( );

            if ( service != null )
            {
                return service;
            }
        }

        AppLogService.info( "module-extend-comment : no avatar provider available, comments will be displayed without avatar" );

        return new DefaultCommentAvatarService( );
    }

    /**
     * Instantiate the plugin-avatar based implementation, by reflection, so that the module can run without the plugin-avatar
     * 
     * @return the implementation, or null if the plugin-avatar is not available
     */
    private static ICommentAvatarService newPluginAvatarService( )
    {
        try
        {
            Class.forName( CLASS_AVATAR_SERVICE );

            ICommentAvatarService service = (ICommentAvatarService) Class.forName( CLASS_PLUGIN_AVATAR_COMMENT_AVATAR_SERVICE ).newInstance( );
            AppLogService.info( "module-extend-comment : avatar service provided by the plugin-avatar" );

            return service;
        }
        catch( ClassNotFoundException e )
        {
            // The plugin-avatar is not deployed : this is a valid setup, the dependency is optional
            return null;
        }
        catch( Exception e )
        {
            AppLogService.error( "module-extend-comment : unable to initialize the plugin-avatar based avatar service : " + e.getMessage( ), e );

            return null;
        }
    }

}
