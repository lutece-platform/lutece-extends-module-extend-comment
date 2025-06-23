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
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.portal.service.security.LuteceUser;

/**
 * Interface of listeners of comments
 * 
 * @param <A>
 */
public interface ICommentListener
{
    /**
     * Notify the creation of a comment
     * 
     * @param strIdExtendableResource
     *            The id of the extendable resource associated with the created comment
     * @param bPublished
     *            True if the created comment is published, false otherwise
     */
    void createComment( String strIdExtendableResource, boolean bPublished );

    /**
     * Notify the creation of a comment
     * 
     * @param strIdExtendableResource
     *            The id of the extendable resource associated with the created comment
     * @param bPublished
     *            True if the created comment is published, false otherwise request HttpServletRequest the Http request
     */
    void createComment( String strIdExtendableResource, boolean bPublished, HttpServletRequest request );

    /**
     * Notify the publication or unpublication of a comment
     * 
     * @param strIdExtendableResource
     *            The id of the extendable resource associated with the modified comment
     * @param bPublished
     *            True if the comment was published, false if it was unpublished
     */
    void publishComment( String strIdExtendableResource, boolean bPublished );

    /**
     * Notify the removal of a comment
     * 
     * @param strIdExtendableResource
     *            The id of the extendable resource associated with the removed comment
     * @param listIdRemovedComment
     *            The list of ids of removed comments
     */
    void deleteComment( String strIdExtendableResource, List<Integer> listIdRemovedComment );

    /**
     * Notify the check comment
     * 
     * @param listErrors
     * @return
     */
    public String checkComment( String comment, String uidUser );

    /**
     * Notify the check comment
     * 
     * @param comment
     * @param uidUser
     * @param strResourceType
     * @param strResourceId
     * @return
     */
    public String checkComment( String comment, String uidUser, String strResourceType, String strResourceId );

    /**
     * Check if user can comment
     * 
     * @param user
     *            The lutece user
     * @param strIdExtendableResource
     *            The id of the extendable resource
     * @param strExtendableResourceType
     *            The type of the resource
     * @return true when the user has the rights, otherwise false
     */
    public boolean canComment( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType );
}
