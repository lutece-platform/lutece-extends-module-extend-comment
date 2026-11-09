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
package fr.paris.lutece.plugins.extend.modules.comment.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * LUT-33206: the comment posted by a visitor is populated only with the fields a visitor may set.
 */
public class CommentAppPopulateTest
{
    private final Map<Integer, Comment> _mapComments = new HashMap<>( );
    private CommentApp _app;
    private Method _populateComment;

    @BeforeEach
    public void setUp( ) throws Exception
    {
        ICommentService commentService = (ICommentService) Proxy.newProxyInstance( getClass( ).getClassLoader( ), new Class<?> [ ] {
                ICommentService.class
        }, ( proxy, method, args ) -> "findByPrimaryKey".equals( method.getName( ) ) ? _mapComments.get( args [0] ) : null );

        _app = new CommentApp( );
        Field field = CommentApp.class.getDeclaredField( "_commentService" );
        field.setAccessible( true );
        field.set( _app, commentService );

        _populateComment = CommentApp.class.getDeclaredMethod( "populateComment", Comment.class, HttpServletRequest.class );
        _populateComment.setAccessible( true );
    }

    private Comment populate( MockHttpServletRequest request ) throws Exception
    {
        Comment comment = new Comment( );
        _populateComment.invoke( _app, comment, request );
        return comment;
    }

    private static Comment comment( int nId, int nIdParent )
    {
        Comment comment = new Comment( );
        comment.setIdComment( nId );
        comment.setIdParentComment( nIdParent );
        return comment;
    }

    /**
     * The fields of the form are read.
     */
    @Test
    public void readsTheFormFields( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "idExtendableResource", "42" );
        request.setParameter( "extendableResourceType", "PAGE" );
        request.setParameter( "name", "Jean Dupont" );
        request.setParameter( "email", "jean@example.com" );
        request.setParameter( "comment", "Un commentaire" );

        Comment comment = populate( request );

        assertEquals( "42", comment.getIdExtendableResource( ) );
        assertEquals( "PAGE", comment.getExtendableResourceType( ) );
        assertEquals( "Jean Dupont", comment.getName( ) );
        assertEquals( "jean@example.com", comment.getEmail( ) );
        assertEquals( "Un commentaire", comment.getComment( ) );
        assertEquals( 0, comment.getIdParentComment( ) );
        assertNotNull( comment.getDateComment( ) );
        assertNotNull( comment.getDateLastModif( ) );
    }

    /**
     * The fields reserved to the back office are ignored, whatever the request contains.
     */
    @Test
    public void ignoresTheReservedFields( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "isAdminComment", "true" );
        request.setParameter( "adminComment", "true" );
        request.setParameter( "pinned", "true" );
        request.setParameter( "isImportant", "true" );
        request.setParameter( "important", "true" );
        request.setParameter( "published", "true" );
        request.setParameter( "commentOrder", "99" );
        request.setParameter( "idComment", "999" );
        request.setParameter( "luteceUserName", "admin" );
        request.setParameter( "ipAddress", "6.6.6.6" );

        Comment comment = populate( request );

        assertFalse( comment.getIsAdminComment( ) );
        assertFalse( comment.isPinned( ) );
        assertFalse( comment.getIsImportant( ) );
        assertFalse( comment.isPublished( ) );
        assertEquals( 0, comment.getCommentOrder( ) );
        assertEquals( 0, comment.getIdComment( ) );
        assertEquals( null, comment.getLuteceUserName( ) );
        assertEquals( request.getRemoteAddr( ), comment.getIpAddress( ) );
    }

    /**
     * A reply keeps its parent, sent by the forms as idParentComment.
     */
    @Test
    public void keepsTheParentOfAReply( ) throws Exception
    {
        _mapComments.put( 10, comment( 10, 0 ) );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "idParentComment", "10" );

        assertEquals( 10, populate( request ).getIdParentComment( ) );
    }

    /**
     * A reply to a reply is attached to the top level comment.
     */
    @Test
    public void attachesAReplyToAReplyToTheTopLevelComment( ) throws Exception
    {
        _mapComments.put( 10, comment( 10, 0 ) );
        _mapComments.put( 20, comment( 20, 10 ) );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "idParentComment", "20" );

        assertEquals( 10, populate( request ).getIdParentComment( ) );
    }

    /**
     * An unknown or malformed parent is ignored.
     */
    @Test
    public void ignoresAnInvalidParent( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "idParentComment", "12345" );
        assertEquals( 0, populate( request ).getIdParentComment( ) );

        request.setParameter( "idParentComment", "-1" );
        assertEquals( 0, populate( request ).getIdParentComment( ) );

        request.setParameter( "idParentComment", "abc" );
        assertEquals( 0, populate( request ).getIdParentComment( ) );
    }
}
