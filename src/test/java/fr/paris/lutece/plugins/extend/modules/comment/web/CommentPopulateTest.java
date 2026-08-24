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

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.CommentFilter;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.util.ReferenceList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommentPopulateTest
{
    private static final String PARAMETER_ID_EXTENDABLE_RESOURCE = "idExtendableResource";
    private static final String PARAMETER_EXTENDABLE_RESOURCE_TYPE = "extendableResourceType";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_EMAIL = "email";
    private static final String MARK_COMMENT = "comment";
    private static final String PARAMETER_ID_COMMENT = "idComment";

    private FakeCommentService _fakeCommentService;
    private CommentApp _instanceUnderTest;
    private Method _populateCommentMethod;

    @Before
    public void setUp( ) throws Exception
    {
        _fakeCommentService = new FakeCommentService( );
        _instanceUnderTest = new CommentApp( );

        for ( Field field : CommentApp.class.getDeclaredFields( ) )
        {
            if ( ICommentService.class.isAssignableFrom( field.getType( ) ) )
            {
                field.setAccessible( true );
                field.set( _instanceUnderTest, _fakeCommentService );
            }
        }

        _populateCommentMethod = CommentApp.class.getDeclaredMethod( "populateComment", Comment.class, HttpServletRequest.class );
        _populateCommentMethod.setAccessible( true );
    }

    private void invokePopulate( Comment comment, FakeHttpServletRequest request ) throws Exception
    {
        _populateCommentMethod.invoke( _instanceUnderTest, comment, request );
    }

    @Test
    public void testPopulateCommentSetsBasicFields( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        request.setParameter( PARAMETER_ID_EXTENDABLE_RESOURCE, "42" );
        request.setParameter( PARAMETER_EXTENDABLE_RESOURCE_TYPE, "PAGE" );
        request.setParameter( PARAMETER_NAME, "Jean Dupont" );
        request.setParameter( PARAMETER_EMAIL, "jean@example.com" );
        request.setParameter( MARK_COMMENT, "Un commentaire test" );

        Comment comment = new Comment( );
        invokePopulate( comment, request );

        assertEquals( "42", comment.getIdExtendableResource( ) );
        assertEquals( "PAGE", comment.getExtendableResourceType( ) );
        assertEquals( "Jean Dupont", comment.getName( ) );
        assertEquals( "jean@example.com", comment.getEmail( ) );
        assertEquals( "Un commentaire test", comment.getComment( ) );
    }

    @Test
    public void testPopulateCommentIgnoresMaliciousIsAdminAndPinnedParameters( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        request.setParameter( "isAdminComment", "true" );
        request.setParameter( "pinned", "true" );
        request.setParameter( "isImportant", "true" );
        request.setParameter( "published", "true" );

        Comment comment = new Comment( );
        invokePopulate( comment, request );

        assertFalse( comment.getIsAdminComment( ) );
        assertFalse( comment.isPinned( ) );
        assertFalse( comment.getIsImportant( ) );
        assertFalse( comment.isPublished( ) );
        assertEquals( 0, comment.getCommentOrder( ) );
    }

    @Test
    public void testPopulateCommentAlwaysResetsIdCommentToZero( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        request.setParameter( "idComment", "20" );

        Comment comment = new Comment( );
        comment.setIdComment( 999 );
        invokePopulate( comment, request );

        assertEquals( 0, comment.getIdComment( ) );
    }

    @Test
    public void testPopulateCommentIdParentCommentAbsentDefaultsToZero( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );

        Comment comment = new Comment( );
        invokePopulate( comment, request );

        assertEquals( 0, comment.getIdParentComment( ) );
    }

    @Test
    public void testPopulateCommentIdParentCommentNonNumericDefaultsToZero( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        request.setParameter( PARAMETER_ID_COMMENT, "abc" );

        Comment comment = new Comment( );
        invokePopulate( comment, request );

        assertEquals( 0, comment.getIdParentComment( ) );
    }

    @Test
    public void testPopulateCommentParentWithoutGrandparentUsesParentDirectly( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        request.setParameter( PARAMETER_ID_COMMENT, "5" );

        Comment parentComment = new Comment( );
        parentComment.setIdComment( 5 );
        parentComment.setIdParentComment( 0 );
        _fakeCommentService.addComment( parentComment );

        Comment comment = new Comment( );
        invokePopulate( comment, request );

        assertEquals( 5, comment.getIdParentComment( ) );
    }

    @Test
    public void testPopulateCommentParentHasGrandparentReattachesToRoot( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        request.setParameter( PARAMETER_ID_COMMENT, "5" );

        Comment parentComment = new Comment( );
        parentComment.setIdComment( 5 );
        parentComment.setIdParentComment( 2 );
        _fakeCommentService.addComment( parentComment );

        Comment comment = new Comment( );
        invokePopulate( comment, request );

        assertEquals( 2, comment.getIdParentComment( ) );
    }

    @Test
    public void testPopulateCommentSetsDateFields( ) throws Exception
    {
        FakeHttpServletRequest request = new FakeHttpServletRequest( );

        long before = System.currentTimeMillis( );
        Comment comment = new Comment( );
        invokePopulate( comment, request );
        long after = System.currentTimeMillis( );

        assertEquals( comment.getDateComment( ), comment.getDateLastModif( ) );
        Timestamp dateComment = comment.getDateComment( );
        assertFalse( dateComment.getTime( ) < before || dateComment.getTime( ) > after );
    }

    private static class FakeCommentService implements ICommentService
    {
        private final Map<Integer, Comment> _mapComments = new HashMap<>( );

        void addComment( Comment comment )
        {
            _mapComments.put( comment.getIdComment( ), comment );
        }

        @Override
        public void remove( int nIdComment ) { }

        @Override
        public void removeByResource( String strIdExtendableResource, String strExtendableResourceType ) { }

        @Override
        public void create( Comment comment ) { }

        @Override
        public void create( Comment comment, HttpServletRequest request ) { }

        @Override
        public void update( Comment comment ) { }

        @Override
        public void updateCommentStatus( int nIdComment, boolean bPublished ) { }

        @Override
        public void updateFlagImportant( int nIdComment, boolean bImportant ) { }

        @Override
        public void updateCommentPinned( int nIdComment, boolean bPinned ) { }

        @Override
        public Comment findByPrimaryKey( int nIdComment )
        {
            return _mapComments.get( nIdComment );
        }

        @Override
        public List<Integer> findIdsByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly, boolean bAscSort )
        {
            return List.of( );
        }

        @Override
        public int getCommentNb( String strIdExtendableResource, String strExtendableResourceType, boolean bParentsOnly, boolean bPublishedOnly )
        {
            return 0;
        }

        @Override
        public List<Comment> findLastComments( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, boolean bPublishedOnly, boolean bParentsOnly, boolean bGetNumberSubComments, boolean bDisplaySubComments, boolean bSortedByDateCreation )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, boolean bPublishedOnly, String strSortedAttributeName, boolean bAscSort, int nItemsOffset, int nMaxItemsNumber, boolean bLoadSubComments )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findByResource( String strIdExtendableResource, String strExtendableResourceType, CommentFilter commentFilter, int nItemsOffset, int nMaxItemsNumber, boolean bLoadSubComments )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findByIdParent( int nIdParent, boolean bPublishedOnly )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findByIdParent( int nIdParent, boolean bPublishedOnly, String strSortedAttributeName, boolean bAscSort )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findCommentsPinned( String strIdExtendableResource, String strExtendableResourceType, int nNbComments, Integer nCommentState, boolean bParentsOnly, boolean bGetNumberSubComments, String strFilterUserName )
        {
            return List.of( );
        }

        @Override
        public int countByIdParent( int nIdParent, boolean bPublishedOnly )
        {
            return 0;
        }

        @Override
        public List<Integer> findIdMostCommentedResources( String strExtendableResourceType, boolean bPublishedOnly, int nItemsOffset, int nMaxItemsNumber )
        {
            return List.of( );
        }

        @Override
        public List<Comment> findByListResource( List<String> listIdExtendableResource, String strExtendableResourceType )
        {
            return List.of( );
        }

        @Override
        public ReferenceList getRefListCommentStates( Locale locale )
        {
            return null;
        }

        @Override
        public ReferenceList getRefListFilterAsImportant( Locale locale )
        {
            return null;
        }

        @Override
        public ReferenceList getRefListFilterAsPinned( Locale locale )
        {
            return null;
        }

        @Override
        public String getResourceType( String extendableResourceType )
        {
            return "";
        }

        @Override
        public List<Comment> findCommentsByLuteceUser( String strLuteceUserName )
        {
            return List.of( );
        }
    }

    private static class FakeHttpServletRequest extends HttpServletRequestWrapper
    {
        private final Map<String, String> _mapParameters = new HashMap<>( );

        FakeHttpServletRequest( )
        {
            super( new NullHttpServletRequest( ) );
        }

        void setParameter( String strName, String strValue )
        {
            _mapParameters.put( strName, strValue );
        }

        @Override
        public String getParameter( String strName )
        {
            return _mapParameters.get( strName );
        }
    }

    private static class NullHttpServletRequest implements HttpServletRequest
    {
        @Override public String getAuthType( ) { return null; }
        @Override public javax.servlet.http.Cookie[] getCookies( ) { return null; }
        @Override public long getDateHeader( String s ) { return 0; }
        @Override public String getHeader( String s ) { return null; }
        @Override public java.util.Enumeration<String> getHeaders( String s ) { return null; }
        @Override public java.util.Enumeration<String> getHeaderNames( ) { return null; }
        @Override public int getIntHeader( String s ) { return 0; }
        @Override public String getMethod( ) { return null; }
        @Override public String getPathInfo( ) { return null; }
        @Override public String getPathTranslated( ) { return null; }
        @Override public String getContextPath( ) { return null; }
        @Override public String getQueryString( ) { return null; }
        @Override public String getRemoteUser( ) { return null; }
        @Override public boolean isUserInRole( String s ) { return false; }
        @Override public java.security.Principal getUserPrincipal( ) { return null; }
        @Override public String getRequestedSessionId( ) { return null; }
        @Override public String getRequestURI( ) { return null; }
        @Override public StringBuffer getRequestURL( ) { return null; }
        @Override public String getServletPath( ) { return null; }
        @Override public javax.servlet.http.HttpSession getSession( boolean b ) { return null; }
        @Override public javax.servlet.http.HttpSession getSession( ) { return null; }
        @Override public String changeSessionId( ) { return null; }
        @Override public boolean isRequestedSessionIdValid( ) { return false; }
        @Override public boolean isRequestedSessionIdFromCookie( ) { return false; }
        @Override public boolean isRequestedSessionIdFromURL( ) { return false; }
        @Override public boolean isRequestedSessionIdFromUrl( ) { return false; }
        @Override public boolean authenticate( javax.servlet.http.HttpServletResponse httpServletResponse ) { return false; }
        @Override public void login( String s, String s1 ) { }
        @Override public void logout( ) { }
        @Override public java.util.Collection<javax.servlet.http.Part> getParts( ) { return null; }
        @Override public javax.servlet.http.Part getPart( String s ) { return null; }
        @Override public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade( Class<T> aClass ) { return null; }
        @Override public Object getAttribute( String s ) { return null; }
        @Override public java.util.Enumeration<String> getAttributeNames( ) { return null; }
        @Override public String getCharacterEncoding( ) { return null; }
        @Override public void setCharacterEncoding( String s ) { }
        @Override public int getContentLength( ) { return 0; }
        @Override public long getContentLengthLong( ) { return 0; }
        @Override public String getContentType( ) { return null; }
        @Override public javax.servlet.ServletInputStream getInputStream( ) { return null; }
        @Override public String getParameter( String s ) { return null; }
        @Override public java.util.Enumeration<String> getParameterNames( ) { return null; }
        @Override public String[] getParameterValues( String s ) { return null; }
        @Override public Map<String, String[]> getParameterMap( ) { return null; }
        @Override public String getProtocol( ) { return null; }
        @Override public String getScheme( ) { return null; }
        @Override public String getServerName( ) { return null; }
        @Override public int getServerPort( ) { return 0; }
        @Override public java.io.BufferedReader getReader( ) { return null; }
        @Override public String getRemoteAddr( ) { return null; }
        @Override public String getRemoteHost( ) { return null; }
        @Override public void setAttribute( String s, Object o ) { }
        @Override public void removeAttribute( String s ) { }
        @Override public java.util.Locale getLocale( ) { return null; }
        @Override public java.util.Enumeration<java.util.Locale> getLocales( ) { return null; }
        @Override public boolean isSecure( ) { return false; }
        @Override public javax.servlet.RequestDispatcher getRequestDispatcher( String s ) { return null; }
        @Override public String getRealPath( String s ) { return null; }
        @Override public int getRemotePort( ) { return 0; }
        @Override public String getLocalName( ) { return null; }
        @Override public String getLocalAddr( ) { return null; }
        @Override public int getLocalPort( ) { return 0; }
        @Override public javax.servlet.ServletContext getServletContext( ) { return null; }
        @Override public javax.servlet.AsyncContext startAsync( ) { return null; }
        @Override public javax.servlet.AsyncContext startAsync( javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse ) { return null; }
        @Override public boolean isAsyncStarted( ) { return false; }
        @Override public boolean isAsyncSupported( ) { return false; }
        @Override public javax.servlet.AsyncContext getAsyncContext( ) { return null; }
        @Override public javax.servlet.DispatcherType getDispatcherType( ) { return null; }
    }
}