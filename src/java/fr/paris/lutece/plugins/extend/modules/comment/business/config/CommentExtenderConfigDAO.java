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
package fr.paris.lutece.plugins.extend.modules.comment.business.config;

import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfigDAO;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentPlugin;
import fr.paris.lutece.util.sql.DAOUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * 
 * CommentExtenderConfigDAO
 * 
 */
@ApplicationScoped
@Named( "extend-comment.CommentExtenderConfigDAO" )
public class CommentExtenderConfigDAO implements IExtenderConfigDAO<CommentExtenderConfig>
{
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_comment_config ( id_extender, is_moderated, nb_comments, id_mailing_list, authorize_sub_comments, use_bbcode, admin_badge, message_comment_created , add_comment_position,is_enabled_auth_mode, is_enabled_display_sub_comments, is_enabled_delete_comments,is_comments_sorted_by_date_creation, id_workflow ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? , ?, ?, ? ) ";
    private static final String SQL_QUERY_UPDATE = " UPDATE extend_comment_config SET is_moderated = ?, nb_comments = ?, id_mailing_list = ?, authorize_sub_comments = ?, use_bbcode = ?, admin_badge = ?, message_comment_created = ? , add_comment_position = ? , is_enabled_auth_mode = ?, is_enabled_display_sub_comments = ?, is_enabled_delete_comments = ?, is_comments_sorted_by_date_creation = ?, id_workflow = ? WHERE id_extender = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_comment_config WHERE id_extender = ? ";
    private static final String SQL_QUERY_SELECT = " SELECT id_extender, is_moderated, nb_comments, id_mailing_list, authorize_sub_comments, use_bbcode, admin_badge, message_comment_created , add_comment_position, is_enabled_auth_mode, is_enabled_display_sub_comments, is_enabled_delete_comments, is_comments_sorted_by_date_creation, id_workflow FROM extend_comment_config WHERE id_extender = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( CommentExtenderConfig config )
    {
        int nIndex = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, CommentPlugin.getPlugin( ) );
        daoUtil.setInt( nIndex++, config.getIdExtender( ) );
        daoUtil.setBoolean( nIndex++, config.isModerated( ) );
        daoUtil.setInt( nIndex++, config.getNbComments( ) );
        daoUtil.setInt( nIndex++, config.getIdMailingList( ) );
        daoUtil.setBoolean( nIndex++, config.getAuthorizeSubComments( ) );
        daoUtil.setBoolean( nIndex++, config.getUseBBCodeEditor( ) );
        daoUtil.setString( nIndex++, config.getAdminBadge( ) );
        daoUtil.setString( nIndex++, config.getMessageCommentCreated( ) );
        daoUtil.setInt( nIndex++, config.getAddCommentPosition( ) );
        daoUtil.setBoolean( nIndex++, config.isEnabledAuthMode( ) );
        daoUtil.setBoolean( nIndex++, config.isDisplaySubComments( ) );
        daoUtil.setBoolean( nIndex++, config.getDeleteComments( ) );
        daoUtil.setBoolean( nIndex++, config.isTriCommentsByCreation( ) );
        daoUtil.setInt( nIndex, config.getIdWorkflow( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( CommentExtenderConfig config )
    {
        int nIndex = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, CommentPlugin.getPlugin( ) );
        daoUtil.setBoolean( nIndex++, config.isModerated( ) );
        daoUtil.setInt( nIndex++, config.getNbComments( ) );
        daoUtil.setInt( nIndex++, config.getIdMailingList( ) );
        daoUtil.setBoolean( nIndex++, config.getAuthorizeSubComments( ) );
        daoUtil.setBoolean( nIndex++, config.getUseBBCodeEditor( ) );
        daoUtil.setString( nIndex++, config.getAdminBadge( ) );
        daoUtil.setString( nIndex++, config.getMessageCommentCreated( ) );
        daoUtil.setInt( nIndex++, config.getAddCommentPosition( ) );
        daoUtil.setBoolean( nIndex++, config.isEnabledAuthMode( ) );
        daoUtil.setBoolean( nIndex++, config.isDisplaySubComments( ) );
        daoUtil.setBoolean( nIndex++, config.getDeleteComments( ) );
        daoUtil.setBoolean( nIndex++, config.isTriCommentsByCreation( ) );
        daoUtil.setInt( nIndex++, config.getIdWorkflow( ) );

        daoUtil.setInt( nIndex, config.getIdExtender( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdExtender )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, CommentPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nIdExtender );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentExtenderConfig load( int nIdExtender )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, CommentPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nIdExtender );
        daoUtil.executeQuery( );

        CommentExtenderConfig config = null;

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            config = new CommentExtenderConfig( );
            config.setIdExtender( daoUtil.getInt( nIndex++ ) );
            config.setModerated( daoUtil.getBoolean( nIndex++ ) );
            config.setNbComments( daoUtil.getInt( nIndex++ ) );
            config.setIdMailingList( daoUtil.getInt( nIndex++ ) );
            config.setAuthorizeSubComments( daoUtil.getBoolean( nIndex++ ) );
            config.setUseBBCodeEditor( daoUtil.getBoolean( nIndex++ ) );
            config.setAdminBadge( daoUtil.getString( nIndex++ ) );
            config.setMessageCommentCreated( daoUtil.getString( nIndex++ ) );
            config.setAddCommentPosition( daoUtil.getInt( nIndex++ ) );
            config.setEnabledAuthMode( daoUtil.getBoolean( nIndex++ ) );
            config.setDisplaySubComments( daoUtil.getBoolean( nIndex++ ) );
            config.setDeleteComments( daoUtil.getBoolean( nIndex++ ) );
            config.setTriCommentsByCreation( daoUtil.getBoolean( nIndex++ ) );
            config.setIdWorkflow( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return config;
    }
}
