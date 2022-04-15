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
package fr.paris.lutece.plugins.extend.modules.comment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;
import java.util.List;

/**
 * ICommentDAO.
 */
public interface ICommentDAO
{
    /**
     * Delete.
     * 
     * @param nIdComment
     *            the n id comment
     * @param plugin
     *            the plugin
     */
    void delete( int nIdComment, Plugin plugin );

    /**
     * Delete by id hub resource.
     * 
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param strExtendableResourceType
     *            the str extendable resource type
     * @param plugin
     *            the plugin
     */
    void deleteByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin );

    /**
     * Insert.
     * 
     * @param comment
     *            the comment
     * @param plugin
     *            the plugin
     */
    void insert( Comment comment, Plugin plugin );

    /**
     * Load.
     * 
     * @param nIdComment
     *            the n id comment
     * @param plugin
     *            the plugin
     * @return the comment
     */
    Comment load( int nIdComment, Plugin plugin );

    /**
     * Store.
     * 
     * @param comment
     *            the comment
     * @param plugin
     *            the plugin
     */
    void store( Comment comment, Plugin plugin );

    /**
     * Update comment status.
     * 
     * @param nIdComment
     *            the n id comment
     * @param bPublished
     *            the b published
     * @param plugin
     *            the plugin
     */
    void updateCommentStatus( int nIdComment, boolean bPublished, Plugin plugin );

    /**
     * Check comment nb.
     * 
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param bParentsOnly
     *            True to consider only comments with no parent, false otherwise
     * @param bPublishedOnly
     *            True to consider only published comments, false to consider every comments.
     * @param plugin
     *            the plugin
     * @return the number of comments
     */
    int getCommentNb( String strIdExtendableResource, String strExtendableResourceType, boolean bParentsOnly, boolean bPublishedOnly, Plugin plugin );

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
     *            the b published only
     * @param bParentsOnly
     *            True to get only parent comments, false to get every comments.
     * @param plugin
     *            the plugin
     * @param bSortedByDateCreation
     *            true if is sorted by date creation
     * @return the list
     */
    List<Comment> selectLastComments( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, boolean bPublishedOnly,
            boolean bParentsOnly, Plugin plugin, boolean bSortedByDateCreation );
    /**
     * Load comment list by resource
     * @param strIdExtendableResource
     *            the id of the extendable resource
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param plugin
     *            the plugin
     * @return list of comments
     */
    List<Comment> selectByListResource( List<String> listIdExtendableResource, String strExtendableResourceType, Plugin plugin );

    /**
     * Get comments of a given resource. Only parents comments are returned.
     * 
     * @param strIdExtendableResource
     *            The id of the resource
     * @param strExtendableResourceType
     *            The type of the resource
     * @param CommentFilter
     *            The commentFilter
     * @param nItemsOffset
     *            The offset of the items to get, or 0 to get items from the first one
     * @param nMaxItemsNumber
     *            The maximum number of items to return, or 0 to get every items
     * @param plugin
     *            The plugin
     * @return The list of comments associated with the given resource
     */
    List<Comment> findParentCommentsByResource( String strIdExtendableResource, String strExtendableResourceType, CommentFilter commentFilter, int nItemsOffset,
            int nMaxItemsNumber, Plugin plugin );

    /**
     * Get comments from their parent
     * 
     * @param nIdParent
     *            The id of the parent of comments to get
     * @param commentFilter
     *            The comment filter
     * @param plugin
     *            The plugin
     * @return The list of comments associated with the given parent
     */
    List<Comment> findByIdParent( int nIdParent, CommentFilter commentFilter, Plugin plugin );

    /**
     * Get the number of comments associated with a given parent
     * 
     * @param nIdParent
     *            The id of the parent of comments to count.
     * @param bPublishedOnly
     *            True to consider only published comments
     * @param plugin
     *            The plugin
     * @return The number of comments associated with the given parent
     */
    int countByIdParent( int nIdParent, boolean bPublishedOnly, Plugin plugin );

    /**
     * Select ids of comments associated with a given resource
     * 
     * @param strIdExtendableResource
     *            The id of the extendable resource
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param bPublishedOnly
     *            True to consider only published comments, false to consider every comment
     * @param plugin
     *            The plugin
     * @return The list of comment ids, or an empty list if no comment is associated with the given resource
     */
    List<Integer> findIdsByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly, Plugin plugin );

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
     * @param plugin
     *            The plugin
     * @return The list of ids of resources ordered by the number of associated comments
     */
    List<Integer> findIdMostCommentedResources( String strExtendableResourceType, boolean bPublishedOnly, int nItemsOffset, int nMaxItemsNumber,
            Plugin plugin );

    /**
     * Get comments of a lutece user. Only parents comments are returned.
     * 
     * @param strLuteceUserName
     *            The name of the lutece user
     * @param nItemsOffset
     *            The offset of the items to get, or 0 to get items from the first one
     * @param nMaxItemsNumber
     *            The maximum number of items to return, or 0 to get every items
     * @param plugin
     *            The plugin
     * @return The list of comments associated with the given resource
     */
	List<Comment> findCommentsByLuteceUserName(String strLuteceUserName, int nItemsOffset, int nMaxItemsNumber,
			Plugin plugin);

}
