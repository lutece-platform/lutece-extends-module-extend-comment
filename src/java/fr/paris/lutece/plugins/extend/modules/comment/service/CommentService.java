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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.CommentFilter;
import fr.paris.lutece.plugins.extend.modules.comment.business.ICommentDAO;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.ResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;

/**
 * 
 * CommentService
 * 
 */
public class CommentService implements ICommentService
{
    /** The Constant BEAN_SERVICE. */
    public static final String BEAN_SERVICE = "extend-comment.commentService";

    @Inject
    private ICommentDAO _commentDAO;
    @Inject
    @Named( ResourceExtenderService.BEAN_SERVICE )
    private IResourceExtenderService _resourceExtenderService;
    @Inject
    @Named( CommentConstants.BEAN_CONFIG_SERVICE )
    private IResourceExtenderConfigService _configService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public synchronized void create( Comment comment, HttpServletRequest request )
    {
        Timestamp currentTimestamp = new Timestamp( new Date( ).getTime( ) );
        comment.setDateComment( currentTimestamp );
        comment.setDateLastModif( currentTimestamp );
        _commentDAO.insert( comment, CommentPlugin.getPlugin( ) );
        if ( comment.getIdParentComment( ) > 0 )
        {
            Comment parentComment = findByPrimaryKey( comment.getIdParentComment( ) );
            parentComment.setDateLastModif( currentTimestamp );
            update( parentComment );
        }
        processWorkflow( comment );
        CommentListenerService.createComment( comment.getExtendableResourceType( ), comment.getIdExtendableResource( ), comment.isPublished( ), request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public synchronized void create( Comment comment )
    {
        Timestamp currentTimestamp = new Timestamp( new Date( ).getTime( ) );
        comment.setDateComment( currentTimestamp );
        comment.setDateLastModif( currentTimestamp );
        _commentDAO.insert( comment, CommentPlugin.getPlugin( ) );
        if ( comment.getIdParentComment( ) > 0 )
        {
            Comment parentComment = findByPrimaryKey( comment.getIdParentComment( ) );
            parentComment.setDateLastModif( currentTimestamp );
            update( parentComment );
        }
        processWorkflow( comment );
        CommentListenerService.createComment( comment.getExtendableResourceType( ), comment.getIdExtendableResource( ), comment.isPublished( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public void update( Comment comment )
    {
        Comment oldComment = findByPrimaryKey( comment.getIdComment( ) );
        comment.setDateLastModif( new Timestamp( new Date( ).getTime( ) ) );
        _commentDAO.store( comment, CommentPlugin.getPlugin( ) );
        if ( oldComment.isPublished( ) ^ comment.isPublished( ) )
        {
            CommentListenerService.publishComment( comment.getExtendableResourceType( ), comment.getIdExtendableResource( ), comment.isPublished( ) );
        }
        // if ( ( oldComment.isPublished( ) && !comment.isPublished( ) ) || !oldComment.isPublished( ) && comment.isPublished( ) ) )
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public void updateCommentStatus( int nIdComment, boolean bPublished )
    {
        _commentDAO.updateCommentStatus( nIdComment, bPublished, CommentPlugin.getPlugin( ) );
        CommentListenerService.publishComment( nIdComment, bPublished );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public void remove( int nIdComment )
    {
        Comment comment = findByPrimaryKey( nIdComment );
        if ( CommentListenerService.hasListener( ) )
        {

            List<Integer> listIdRemovedComments = new ArrayList<>( );
            listIdRemovedComments.add( nIdComment );
            CommentListenerService.deleteComment( comment.getExtendableResourceType( ), comment.getIdExtendableResource( ), listIdRemovedComments );
        }
        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            String resourceType = getResourceType( comment.getExtendableResourceType( ) );
            WorkflowService.getInstance( ).doRemoveWorkFlowResource( nIdComment, resourceType );

        }
        _commentDAO.delete( nIdComment, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public void removeByResource( String strIdExtendableResource, String strExtendableResourceType )
    {
        List<Integer> listRemovedComments = findIdsByResource( strIdExtendableResource, strExtendableResourceType, false );
        if ( CommentListenerService.hasListener( ) )
        {

            CommentListenerService.deleteComment( strExtendableResourceType, strIdExtendableResource, listRemovedComments );
        }
        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            String resourceType = getResourceType( strExtendableResourceType );
            for ( int nIdComment : listRemovedComments )
            {

                WorkflowService.getInstance( ).doRemoveWorkFlowResource( nIdComment, resourceType );

            }
            // WorkflowService.getInstance( ).doRemoveWorkFlowResourceByListId(listRemovedComments, resourceType , id_wf);

        }
        _commentDAO.deleteByResource( strIdExtendableResource, strExtendableResourceType, CommentPlugin.getPlugin( ) );
    }

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment findByPrimaryKey( int nIdComment )
    {
        return _commentDAO.load( nIdComment, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findIdsByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly )
    {
        return _commentDAO.findIdsByResource( strIdExtendableResource, strExtendableResourceType, bPublishedOnly, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly, boolean bAscSort )
    {

        CommentFilter commentFilter = new CommentFilter( );
        if ( bPublishedOnly )
        {
            commentFilter.setCommentState( Comment.COMMENT_STATE_PUBLISHED );
        }

        commentFilter.setAscSort( bAscSort );

        return _commentDAO.findParentCommentsByResource( strIdExtendableResource, strExtendableResourceType, commentFilter, 0, 0, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCommentNb( String strIdExtendableResource, String strExtendableResourceType, boolean bParentsOnly, boolean bPublishedOnly )
    {
        return _commentDAO.getCommentNb( strIdExtendableResource, strExtendableResourceType, bParentsOnly, bPublishedOnly, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> findLastComments( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, boolean bPublishedOnly,
            boolean bParentsOnly, boolean bGetNumberSubComments, boolean bDisplaySubComments, boolean bSortedByDateCreation )
    {
        Plugin plugin = CommentPlugin.getPlugin( );
        List<Comment> listComments = new ArrayList<>( );
        List<Comment> listSubComments = new ArrayList<>( );

        List<Comment> listCommentsPinned = findCommentsPinned( strIdExtendableResource, strExtendableResourceType, nNbComments,
                bPublishedOnly ? Comment.COMMENT_STATE_PUBLISHED : null, true, bGetNumberSubComments, null );

        listComments.addAll( listCommentsPinned );

        if ( nNbComments == 0 || listCommentsPinned.size( ) != nNbComments )
        {
            if ( listCommentsPinned.size( ) != nNbComments )
            {
                nNbComments = nNbComments - listCommentsPinned.size( );
            }
            List<Comment> listLastComments = _commentDAO.selectLastComments( strIdExtendableResource, strExtendableResourceType, nNbComments, bPublishedOnly,
                    bParentsOnly, plugin, bSortedByDateCreation );
            if ( bGetNumberSubComments )
            {
                for ( Comment comment : listLastComments )
                {
                    listSubComments = findByIdParent( comment.getIdComment( ), bPublishedOnly );

                    comment.setNumberSubComments( listSubComments.size( ) );
                    if ( bDisplaySubComments )
                    {
                        comment.setListSubComments( listSubComments );
                    }
                }
            }
            listComments.addAll( listLastComments );
        }

        return listComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly,
            String strSortedAttributeName, boolean bAscSort, int nItemsOffset, int nMaxItemsNumber, boolean bLoadSubComments )
    {

        CommentFilter commentFilter = new CommentFilter( );
        if ( bPublishedOnly )
        {
            commentFilter.setCommentState( Comment.COMMENT_STATE_PUBLISHED );
        }
        commentFilter.setSortedAttributeName( strSortedAttributeName );
        commentFilter.setAscSort( bAscSort );
        return findByResource( strIdExtendableResource, strExtendableResourceType, commentFilter, nItemsOffset, nMaxItemsNumber, bLoadSubComments );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, CommentFilter commentFilter, int nItemsOffset,
            int nMaxItemsNumber, boolean bLoadSubComments )
    {

        List<Comment> listComments = new ArrayList<>( );

        listComments.addAll( _commentDAO.findParentCommentsByResource( strIdExtendableResource, strExtendableResourceType, commentFilter, nItemsOffset,
                nMaxItemsNumber, CommentPlugin.getPlugin( ) ) );

        if ( bLoadSubComments )
        {
            for ( Comment comment : listComments )
            {
                comment.setListSubComments( this.findByIdParent( comment.getIdComment( ),
                        commentFilter.getCommentState( ) != null && commentFilter.getCommentState( ).equals( Comment.COMMENT_STATE_PUBLISHED ),
                        commentFilter.getSortedAttributeName( ), commentFilter.getAscSort( ) ) );
            }
        }

        return listComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> findByIdParent( int nIdParent, boolean bPublishedOnly )
    {
        return this.findByIdParent( nIdParent, bPublishedOnly, null, true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> findByIdParent( int nIdParent, boolean bPublishedOnly, String strSortedAttributeName, boolean bAscSort )
    {
        CommentFilter commentFilter = new CommentFilter( );

        if ( bPublishedOnly )
        {
            commentFilter.setCommentState( Comment.COMMENT_STATE_PUBLISHED );
        }
        commentFilter.setSortedAttributeName( strSortedAttributeName );
        commentFilter.setAscSort( bAscSort );

        return _commentDAO.findByIdParent( nIdParent, commentFilter, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByIdParent( int nIdParent, boolean bPublishedOnly )
    {
        return _commentDAO.countByIdParent( nIdParent, bPublishedOnly, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findIdMostCommentedResources( String strExtendableResourceType, boolean bPublishedOnly, int nItemsOffset, int nMaxItemsNumber )
    {
        return _commentDAO.findIdMostCommentedResources( strExtendableResourceType, bPublishedOnly, nItemsOffset, nMaxItemsNumber, CommentPlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getRefListCommentStates( Locale locale )
    {
        ReferenceList refListDiggSubmitState = new ReferenceList( );
        refListDiggSubmitState.addItem( "", I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_ALL_STATE, locale ) );
        refListDiggSubmitState.addItem( Comment.COMMENT_STATE_PUBLISHED,
                I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_STATE_PUBLISHED, locale ) );
        refListDiggSubmitState.addItem( Comment.COMMENT_STATE_UN_PUBLISHED,
                I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_STATE_UN_PUBLISHED, locale ) );
        return refListDiggSubmitState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getRefListFilterAsImportant( Locale locale )
    {
        ReferenceList refListFilterAsImportant = new ReferenceList( );
        refListFilterAsImportant.addItem( "", I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_FILTER_BY_IMPORTANT, locale ) );
        refListFilterAsImportant.addItem( Boolean.TRUE.toString( ),
                I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_FILTER_BY_IMPORTANT_ALL_FLAG_IMPORTANT, locale ) );
        refListFilterAsImportant.addItem( Boolean.FALSE.toString( ),
                I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_FILTER_BY_IMPORTANT_ALL_NOT_FLAG_AS_IMPORTANT, locale ) );
        return refListFilterAsImportant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getRefListFilterAsPinned( Locale locale )
    {
        ReferenceList refListDiggSubmitState = new ReferenceList( );
        refListDiggSubmitState.addItem( "", I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_FILTER_BY_PINNED, locale ) );
        refListDiggSubmitState.addItem( Boolean.TRUE.toString( ),
                I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_FILTER_BY_PINNED_ALL_PINNED, locale ) );
        refListDiggSubmitState.addItem( Boolean.FALSE.toString( ),
                I18nService.getLocalizedString( CommentConstants.PROPERTY_COMMENT_FILTER_BY_PINNED_ALL_NOT_PINNED, locale ) );
        return refListDiggSubmitState;
    }

    @Override
    public List<Comment> findCommentsPinned( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, Integer nCommentState,
            boolean bParentsOnly, boolean bGetNumberSubComments, String strFilterUserName )
    {
        Plugin plugin = CommentPlugin.getPlugin( );
        CommentFilter filter = new CommentFilter( );
        filter.setPinned( true );
        filter.setAscSort( false );
        filter.setSortedAttributeName( CommentConstants.SORT_BY_COMMENT_ORDER );
        filter.setCommentState( nCommentState );
        filter.setLuteceUserName( strFilterUserName );

        List<Comment> listComments = _commentDAO.findParentCommentsByResource( strIdExtendableResource, strExtendableResourceType, filter, 0, nNbComments,
                plugin );

        if ( bGetNumberSubComments )
        {
            for ( Comment comment : listComments )
            {
                comment.setNumberSubComments( _commentDAO.countByIdParent( comment.getIdComment( ),
                        ( nCommentState != null && nCommentState.equals( Comment.COMMENT_STATE_PUBLISHED ) ), plugin ) );
            }
        }
        return listComments;

    }

    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public void updateFlagImportant( int nIdComment, boolean bImportant )
    {
        Plugin plugin = CommentPlugin.getPlugin( );
        Comment comment = findByPrimaryKey( nIdComment );
        if ( comment != null )
        {
            comment.setIsImportant( bImportant );
        }
        _commentDAO.store( comment, plugin );
    }

    @Override
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    public void updateCommentPinned( int nIdComment, boolean bPinned )
    {
        Plugin plugin = CommentPlugin.getPlugin( );
        Comment comment = findByPrimaryKey( nIdComment );
        if ( comment != null )
        {
            comment.setPinned( bPinned );
            if ( bPinned )
            {
                // update comment Order
                CommentFilter filter = new CommentFilter( );
                filter.setPinned( true );
                List<Comment> listComment = findByResource( comment.getIdExtendableResource( ), comment.getExtendableResourceType( ), filter, 0, 10000, false );
                int nOrder = 1;
                for ( Comment commentPinned : listComment )
                {
                    if ( commentPinned.getCommentOrder( ) >= nOrder )
                    {
                        nOrder = commentPinned.getCommentOrder( ) + 1;
                    }
                }
                comment.setCommentOrder( nOrder );
            }

        }
        _commentDAO.store( comment, plugin );
    }

    private void processWorkflow( Comment comment )
    {

        String resourceType = getResourceType( comment.getExtendableResourceType( ) );
        ResourceExtenderDTOFilter filter = new ResourceExtenderDTOFilter( );
        filter.setFilterExtendableResourceType( comment.getExtendableResourceType( ) );
        filter.setFilterIdExtendableResource( comment.getIdExtendableResource( ) );
        filter.setFilterExtenderType( CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        filter.setIncludeWildcardResource( true );

        List<ResourceExtenderDTO> listResourceExtender = _resourceExtenderService.findByFilter( filter );
        int nIdExtendable = listResourceExtender.get( 0 ).getIdExtender( );

        CommentExtenderConfig config = (CommentExtenderConfig) _configService.find( nIdExtendable );
        int idWorkflow = config.getIdWorkflow( );
        if ( idWorkflow > 0 )
        {

            WorkflowService.getInstance( ).getState( comment.getIdComment( ), resourceType, idWorkflow, nIdExtendable );
            WorkflowService.getInstance( ).executeActionAutomatic( comment.getIdComment( ), resourceType, idWorkflow, nIdExtendable );
        }
    }

    public String getResourceType( String extendableResourceType )
    {

        StringBuilder resourceType = new StringBuilder( );
        resourceType.append( CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        resourceType.append( "-" );
        resourceType.append( extendableResourceType );

        return resourceType.toString( );
    }

	@Override
	public List<Comment> findByListResource(List<String> listIdExtendableResource, String strExtendableResourceType) {
		
		return _commentDAO.selectByListResource(listIdExtendableResource, strExtendableResourceType, CommentPlugin.getPlugin( ) );
	}

	@Override
	public List<Comment> findCommentsByLuteceUser(String strLuteceUserName) {
		return _commentDAO.findCommentsByLuteceUserName(strLuteceUserName, CommentPlugin.getPlugin( ));
	}

}
