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

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.ICommentDAO;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.inject.Inject;
import jakarta.enterprise.inject.Instance;

/**
 * Service to manage listeners over comments
 */
public class CommentListenerService
{
    /**
     * Constant that represents every extendable resource type
     */
    public static final String CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE = "*";

    private static Map<String, List<ICommentListener>> _mapListeners = new HashMap<String, List<ICommentListener>>( );
    private static boolean _bHasListeners;

    private static volatile ICommentDAO _commentDAO;

    @Inject
    private static Instance<ICommentDAO> commentDAOList;

    /**
     * Private constructor
     */
    private CommentListenerService( )
    {

    }

    /**
     * Register a comment listener.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type associated with the listener. Use {@link #CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE} to associated the listener
     *            with every resource type.
     * @param listener
     *            The listener to register
     */
    public static synchronized void registerListener( String strExtendableResourceType, ICommentListener listener )
    {
        List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners == null )
        {
            listListeners = new ArrayList<ICommentListener>( );
            _mapListeners.put( strExtendableResourceType, listListeners );
        }
        listListeners.add( listener );
        _bHasListeners = true;
    }

    /**
     * Check if there is listeners to notify
     * 
     * @return True if there is at last one listener, false otherwise
     */
    public static boolean hasListener( )
    {
        return _bHasListeners;
    }

    /**
     * Notify to listeners the creation of a comment. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type of the created comment
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param bPublished
     *            True if the comment is published, false otherwise
     */
    public static void createComment( String strExtendableResourceType, String strIdExtendableResource, boolean bPublished )
    {
        List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( ICommentListener listener : listListeners )
            {
                listener.createComment( strIdExtendableResource, bPublished );
            }
        }
        listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
        if ( listListeners != null )
        {
            for ( ICommentListener listener : listListeners )
            {
                listener.createComment( strIdExtendableResource, bPublished );
            }
        }
    }

    /**
     * Notify to listeners the creation of a comment. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type of the created comment
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param bPublished
     *            True if the comment is published, false otherwise
     * @param request
     *            the HTTP request
     */
    public static void createComment( String strExtendableResourceType, String strIdExtendableResource, boolean bPublished, HttpServletRequest request )
    {
        List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( ICommentListener listener : listListeners )
            {
                listener.createComment( strIdExtendableResource, bPublished, request );
            }
        }
        listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
        if ( listListeners != null )
        {
            for ( ICommentListener listener : listListeners )
            {
                listener.createComment( strIdExtendableResource, bPublished, request );
            }
        }
    }

    /**
     * Notify to listeners the modification of a comment. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param nIdComment
     *            The id of the updated comment
     * @param bPublished
     *            True if the comment was published, false if it was unpublished
     */
    public static void publishComment( int nIdComment, boolean bPublished )
    {
        if ( _mapListeners.size( ) > 0 )
        {
            Comment comment = getCommentDAO( ).load( nIdComment, CommentPlugin.getPlugin( ) );
            publishComment( comment.getExtendableResourceType( ), comment.getIdExtendableResource( ), bPublished );
        }
    }

    /**
     * Notify to listeners the modification of a comment. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type of the updated comment
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param bPublished
     *            True if the comment was published, false if it was unpublished
     */
    public static void publishComment( String strExtendableResourceType, String strIdExtendableResource, boolean bPublished )
    {
        List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( ICommentListener listener : listListeners )
            {
                listener.publishComment( strIdExtendableResource, bPublished );
            }
        }
        listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
        if ( listListeners != null )
        {
            for ( ICommentListener listener : listListeners )
            {
                listener.publishComment( strIdExtendableResource, bPublished );
            }
        }
    }

    /**
     * Notify to listeners the modification of a comment. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type of the removed comment
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param listIdRemovedComment
     *            The list of ids of removed comments
     */
    public static void deleteComment( String strExtendableResourceType, String strIdExtendableResource, List<Integer> listIdRemovedComment )
    {
        try
        {
            List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    listener.deleteComment( strIdExtendableResource, listIdRemovedComment );
                }
            }
            listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    listener.deleteComment( strIdExtendableResource, listIdRemovedComment );
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
    }

    /**
     * Notify the check comment
     * 
     * @param listErrors
     * @return
     */
    public static String checkComment( String comment, String strExtendableResourceType, String uidUser )
    {

        StringBuilder sbError = new StringBuilder( );
        try
        {
            List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    String strError = listener.checkComment( comment, uidUser );
                    if ( strError != null && !strError.isEmpty( ) )
                    {
                        sbError.append( strError );
                    }
                }
            }
            listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    String strError = listener.checkComment( comment, uidUser );
                    if ( strError != null && !strError.isEmpty( ) )
                    {
                        sbError.append( strError );
                    }
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
        return sbError.toString( );
    }

    /**
     * Notify the check comment
     * 
     * @param listErrors
     * @return
     */
    public static String checkComment( String comment, String strExtendableResourceType, String strResourceId, String uidUser )
    {
        StringBuilder sbError = new StringBuilder( );
        try
        {
            List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    String strError = listener.checkComment( comment, uidUser, strExtendableResourceType, strResourceId );
                    if ( strError != null && !strError.isEmpty( ) )
                    {
                        sbError.append( strError );
                    }
                }
            }
            listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    String strError = listener.checkComment( comment, uidUser, strExtendableResourceType, strResourceId );
                    if ( strError != null && !strError.isEmpty( ) )
                    {
                        sbError.append( strError );
                    }
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
        return sbError.toString( );
    }

    /**
     * Check if a user can comment. Call listeners.
     * 
     * @param user
     *            The lutece user
     * @param strIdExtendableResource
     *            The id of the extendable resource
     * @param strExtendableResourceType
     *            The type of the resource
     * @return true when the user has the rights, otherwise false
     */
    public static boolean canComment( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType )
    {
        try
        {
            List<ICommentListener> listListeners = _mapListeners.get( strExtendableResourceType );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    if ( !listener.canComment( user, strIdExtendableResource, strExtendableResourceType ) )
                    {
                        return false;
                    }

                }
            }
            listListeners = _mapListeners.get( CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE );
            if ( listListeners != null )
            {
                for ( ICommentListener listener : listListeners )
                {
                    if ( !listener.canComment( user, strIdExtendableResource, strExtendableResourceType ) )
                    {
                        return false;
                    }

                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
        return true;
    }

    /**
     * Get the comment DAO
     * 
     * @return the comment DAO
     */
    private static ICommentDAO getCommentDAO( )
    {
        if ( _commentDAO == null )
        {
            synchronized( CommentListenerService.class )
            {
                if ( _commentDAO == null )
                {
                    List<ICommentDAO> listDao = commentDAOList.stream( ).collect( Collectors.toList( ) );
                    if ( listDao != null && listDao.size( ) > 0 )
                    {
                        _commentDAO = listDao.get( 0 );
                    }
                }
            }
        }
        return _commentDAO;
    }
}
