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

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.CommentFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

/**
 * ICommentService.
 */
public interface ICommentService
{
    /**
     * Delete.
     * 
     * @param nIdComment
     *            the n id comment
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void remove( int nIdComment );

    /**
     * Delete by resource
     * 
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param strExtendableResourceType
     *            the str extendable resource type
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void removeByResource( String strIdExtendableResource, String strExtendableResourceType );

    /**
     * Insert.
     * 
     * @param comment
     *            the comment
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void create( Comment comment );

    /**
     * Insert.
     * 
     * @param comment
     *            the comment
     * @param request
     *            the HTTP request
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void create( Comment comment, HttpServletRequest request );

    /**
     * Store.
     * 
     * @param comment
     *            the comment
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void update( Comment comment );

    /**
     * Update comment status.
     * 
     * @param nIdComment
     *            the n id comment
     * @param bPublished
     *            the b published
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void updateCommentStatus( int nIdComment, boolean bPublished );

    /**
     * Update flag important.
     * 
     * @param nIdComment
     *            the n id comment
     * @param bImportant
     *            true if the comment is important
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void updateFlagImportant( int nIdComment, boolean bImportant );

    /**
     * Update flag important.
     * 
     * @param nIdComment
     *            the n id comment
     * @param bPinned
     *            true if the comment must be pinned
     */
    @Transactional( CommentPlugin.TRANSACTION_MANAGER )
    void updateCommentPinned( int nIdComment, boolean bPinned );

    /**
     * Load.
     * 
     * @param nIdComment
     *            the n id comment
     * @return the comment
     */
    Comment findByPrimaryKey( int nIdComment );

    /**
     * Select ids of comments associated with a given resource
     * 
     * @param strIdExtendableResource
     *            The id of the extendable resource
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param bPublishedOnly
     *            True to consider only published comments, false to consider every comment
     * @return The list of comment ids, or an empty list if no comment is associated with the given resource
     */
    List<Integer> findIdsByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly );

    /**
     * Select by resource.
     * 
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param bPublishedOnly
     *            Get only published comments
     * @param bAscSort
     *            True if comments should be sorted ascendantly, false otherwise
     * @return the list
     */
    List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly, boolean bAscSort );

    /**
     * Get the number of comment
     * 
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param bParentsOnly
     *            True to consider only comments with no parent, false otherwise
     * @param bPublishedOnly
     *            True to get only published comments, false to get every comments
     * @return the number of comments
     */
    int getCommentNb( String strIdExtendableResource, String strExtendableResourceType, boolean bParentsOnly, boolean bPublishedOnly );

    /**
     * Load last comments.
     * 
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param nNbComments
     *            the number of comments
     * @param bPublishedOnly
     *            True to get only published comments, false to get every comments
     * @param bParentsOnly
     *            True to get only parent comments, false to get every comments.
     * @param bGetNumberSubComments
     *            True to get the number of sub comments of each comment, false otherwise
     * @param bDisplaySubComments
     *            True if the mod display sub comments is enable
     * @param bSortedByDateCreation
     *            true if the list is sorted by date creation
     * @return the list
     */
    List<Comment> findLastComments( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, boolean bPublishedOnly,
            boolean bParentsOnly, boolean bGetNumberSubComments, boolean bDisplaySubComments, boolean bSortedByDateCreation );

    /**
     * Get comments of a given resource
     * 
     * @param strIdExtendableResource
     *            The id of the resource
     * @param strExtendableResourceType
     *            The type of the resource
     * @param bPublishedOnly
     *            True to consider only published comments
     * @param strSortedAttributeName
     *            The name of the attribute to sort, or null if no sort should be done
     * @param bAscSort
     *            True to sort ascendantly, false otherwise
     * @param nItemsOffset
     *            The offset of the items to get, or 0 to get items from the first one
     * @param nMaxItemsNumber
     *            The maximum number of items to return, or 0 to get every items
     * @param bLoadSubComments
     *            True if sub comments should be loaded, false if they should be ignored
     * @return The list of comments associated with the given resource
     */
    List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly, String strSortedAttributeName,
            boolean bAscSort, int nItemsOffset, int nMaxItemsNumber, boolean bLoadSubComments );

    /**
     * Get comments of a given resource
     * 
     * @param strIdExtendableResource
     *            The id of the resource
     * @param strExtendableResourceType
     *            The type of the resource
     * @param commentFilter
     *            The commentFilter Object
     * @param nItemsOffset
     *            The offset of the items to get, or 0 to get items from the first one
     * @param nMaxItemsNumber
     *            The maximum number of items to return, or 0 to get every items
     * @param bLoadSubComments
     *            True if sub comments should be loaded, false if they should be ignored
     * @return The list of comments associated with the given resource
     */
    List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, CommentFilter commentFilter, int nItemsOffset,
            int nMaxItemsNumber, boolean bLoadSubComments );

    /**
     * Get comments from their parent
     * 
     * @param nIdParent
     *            The id of the parent of comments to get
     * @param bPublishedOnly
     *            True to consider only published comments
     * @return The list of comments associated with the given parent
     */
    List<Comment> findByIdParent( int nIdParent, boolean bPublishedOnly );

    /**
     * Get comments from their parent
     * 
     * @param nIdParent
     *            The id of the parent of comments to get
     * @param bPublishedOnly
     *            True to consider only published comments
     * @param strSortedAttributeName
     *            The name of the attribute to sort, or null if no sort should be done
     * @param bAscSort
     *            True to sort ascendantly, false otherwise
     * @return The list of comments associated with the given parent
     */
    List<Comment> findByIdParent( int nIdParent, boolean bPublishedOnly, String strSortedAttributeName, boolean bAscSort );

    /**
     * Load comments pinned.
     * 
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param nNbComments
     *            the number of comments
     * @param bPublishedOnly
     *            True to get only published comments, false to get every comments
     * @param bParentsOnly
     *            True to get only parent comments, false to get every comments.
     * @param bGetNumberSubComments
     *            True to get the number of sub comments of each comment, false otherwise
     * @param add
     *            filter on user name strFilterUserName
     * @return the list of comments pinned
     */
    List<Comment> findCommentsPinned( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, Integer nCommentState,
            boolean bParentsOnly, boolean bGetNumberSubComments, String strFilterUserName );

    /**
     * Get the number of comments associated with a given parent
     * 
     * @param nIdParent
     *            The id of the parent of comments to count.
     * @param bPublishedOnly
     *            True to consider only published comments
     * @return The number of comments associated with the given parent
     */
    int countByIdParent( int nIdParent, boolean bPublishedOnly );

    /**
     * Get the ids of resources ordered by their number of comments
     * 
     * @param strExtendableResourceType
     *            The type of resources to consider
     * @param bPublishedOnly
     *            True to consider only published comments, false to consider every comments
     * @param nItemsOffset
     *            The offset of the items to get, or 0 to get items from the first one
     * @param nMaxItemsNumber
     *            The maximum number of items to return, or 0 to get every items
     * @return The list of ids of resources ordered by the number of associated comments
     */
    List<Integer> findIdMostCommentedResources( String strExtendableResourceType, boolean bPublishedOnly, int nItemsOffset, int nMaxItemsNumber );

    /**
     * Find comment list by resource
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @return list of comments
     */
    List<Comment> findByListResource( List<String> listIdExtendableResource, String strExtendableResourceType );

    /**
     * return a referenceList of comment states
     * 
     * @param locale
     *            the locale
     * @return a referenceList of comment states
     */
    ReferenceList getRefListCommentStates( Locale locale );

    /**
     * 
     * return a referenceList containing the filter on the flag important
     * 
     * @param locale
     *            the locale
     * @return a referenceList containing the filter on the flag important
     */
    ReferenceList getRefListFilterAsImportant( Locale locale );

    /**
     * return a referenceList containing the filter on the flag As Pinned
     * 
     * @param locale
     *            the locale
     * @return a referenceList containing the filter on the flag As Pinned
     */
    ReferenceList getRefListFilterAsPinned( Locale locale );

    /**
     * return resource type
     * 
     * @param extendableResourceType
     * @return
     */
    String getResourceType( String extendableResourceType );
    
    /**
     * Load comments by lutece user name.
     * 
     * @param strLuteceUserName
     *            the name of the lutece user
     * @param nItemsOffset
     *            The offset of the items to get, or 0 to get items from the first one
     * @param nMaxItemsNumber
     *            The maximum number of items to return, or 0 to get every items
     * @return the list of comments by lutece user name
     */
    List<Comment> findCommentsByLuteceUser( String strLuteceUserName );

}
