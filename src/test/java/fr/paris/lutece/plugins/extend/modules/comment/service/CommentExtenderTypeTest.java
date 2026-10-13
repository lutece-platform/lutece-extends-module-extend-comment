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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.ICommentDAO;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.facade.IExtendableResourceResult;
import fr.paris.lutece.plugins.extend.service.extender.facade.InfoExtenderException;
import fr.paris.lutece.plugins.extend.service.extender.facade.ResourceExtenderServiceFacade;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Registration of the comment extender type with the extend facade.
 */
public class CommentExtenderTypeTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "unit_test_comment";
    private static final String ID_RESOURCE = "1";

    @Inject
    private CommentService _commentService;

    @Inject
    private ICommentDAO _commentDAO;

    /**
     * Removes the comments of the test resource.
     */
    @AfterEach
    public void removeComments( )
    {
        _commentDAO.deleteByResource( ID_RESOURCE, RESOURCE_TYPE, CommentPlugin.getPlugin( ) );
    }

    /**
     * Once registered at startup, the facade knows the comment type, lists every comment of a resource and counts its
     * published parent comments.
     *
     * @throws InfoExtenderException
     *             if the facade does not know the comment type
     */
    @Test
    public void testFacadeAnswersForComments( ) throws InfoExtenderException
    {
        Comment parent = newComment( "published parent", true, 0 );
        newComment( "second published parent", true, 0 );
        newComment( "unpublished parent", false, 0 );
        newComment( "published answer", true, parent.getIdComment( ) );

        _commentService.registerExtenderType( null );

        assertTrue( ResourceExtenderServiceFacade.getListExtenderType( ).stream( )
                .anyMatch( type -> CommentResourceExtender.EXTENDER_TYPE_COMMENT.equals( type.getType( ) ) ), "comment type registered" );
        assertTrue( ResourceExtenderServiceFacade.getExtenderType( Comment.class ).isPresent( ), "comment class registered" );
        assertEquals( "2", ResourceExtenderServiceFacade.getInfoForRecap( CommentResourceExtender.EXTENDER_TYPE_COMMENT, ID_RESOURCE, RESOURCE_TYPE ),
                "recap" );
        assertEquals( "2", ResourceExtenderServiceFacade.getInfoForExport( CommentResourceExtender.EXTENDER_TYPE_COMMENT, ID_RESOURCE, RESOURCE_TYPE ),
                "export" );

        List<IExtendableResourceResult> listComments = ResourceExtenderServiceFacade.getInfoExtender( CommentResourceExtender.EXTENDER_TYPE_COMMENT,
                ID_RESOURCE, RESOURCE_TYPE );
        assertEquals( 4, listComments.size( ), "comments of the resource" );
    }

    /**
     * Inserts a comment on the test resource.
     *
     * @param strComment
     *            the comment text
     * @param bPublished
     *            true if the comment is published
     * @param nIdParent
     *            the parent comment id, 0 for none
     * @return the inserted comment
     */
    private Comment newComment( String strComment, boolean bPublished, int nIdParent )
    {
        Timestamp now = new Timestamp( System.currentTimeMillis( ) );
        Comment comment = new Comment( );
        comment.setIdExtendableResource( ID_RESOURCE );
        comment.setExtendableResourceType( RESOURCE_TYPE );
        comment.setName( "unit test" );
        comment.setEmail( "unit.test@e2e.local" );
        comment.setIpAddress( "127.0.0.1" );
        comment.setComment( strComment );
        comment.setPublished( bPublished );
        comment.setIdParentComment( nIdParent );
        comment.setDateComment( now );
        comment.setDateLastModif( now );
        _commentDAO.insert( comment, CommentPlugin.getPlugin( ) );

        return comment;
    }
}
