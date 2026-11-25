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
package fr.paris.lutece.plugins.extend.modules.comment.business;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.test.LuteceTestCase;

import jakarta.inject.Inject;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

/**
 * Removing a comment extender on all the resources of a type keeps the comments of the resources that keep their own comment extender.
 */
public class CommentWildcardRemovalTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "PAGE";
    private static final String ID_KEPT = "424260";
    private static final String ID_WILDCARD_ONLY = "424261";

    @Inject
    private ICommentDAO _commentDAO;
    @Inject
    private IResourceExtenderService _extenderService;

    @Test
    public void testRemovingWildcardKeepsCommentsOfSpecificExtenders( )
    {
        ResourceExtenderDTO specific = extender( ID_KEPT );
        ResourceExtenderDTO wildcard = extender( ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE );

        try
        {
            comment( ID_KEPT );
            comment( ID_WILDCARD_ONLY );

            // what ResourceExtenderJspBean.doRemoveResourceExtender does (CommentResourceExtender.doDeleteResourceAddOn)
            _extenderService.remove( wildcard.getIdExtender( ) );
            _commentDAO.deleteByResource( ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE, RESOURCE_TYPE, plugin( ) );

            assertEquals( 1, _commentDAO.findIdsByResource( ID_KEPT, RESOURCE_TYPE, false, plugin( ) ).size( ), "comment of the resource with its own extender" );
            assertEquals( 0, _commentDAO.findIdsByResource( ID_WILDCARD_ONLY, RESOURCE_TYPE, false, plugin( ) ).size( ) );
        }
        finally
        {
            _extenderService.remove( specific.getIdExtender( ) );
            _extenderService.remove( wildcard.getIdExtender( ) );
            _commentDAO.deleteByResource( ID_KEPT, RESOURCE_TYPE, plugin( ) );
            _commentDAO.deleteByResource( ID_WILDCARD_ONLY, RESOURCE_TYPE, plugin( ) );
        }
    }

    private static fr.paris.lutece.portal.service.plugin.Plugin plugin( )
    {
        return fr.paris.lutece.plugins.extend.modules.comment.service.CommentPlugin.getPlugin( );
    }

    private void comment( String strIdResource )
    {
        Timestamp now = new Timestamp( System.currentTimeMillis( ) );
        Comment comment = new Comment( );
        comment.setIdExtendableResource( strIdResource );
        comment.setExtendableResourceType( RESOURCE_TYPE );
        comment.setName( "e2e" );
        comment.setEmail( "e2e@example.org" );
        comment.setIpAddress( "127.0.0.1" );
        comment.setComment( "e2e comment" );
        comment.setDateComment( now );
        comment.setDateLastModif( now );
        comment.setPublished( true );
        _commentDAO.insert( comment, plugin( ) );
    }

    private ResourceExtenderDTO extender( String strIdResource )
    {
        ResourceExtenderDTO extender = new ResourceExtenderDTO( );
        extender.setExtenderType( CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        extender.setIdExtendableResource( strIdResource );
        extender.setExtendableResourceType( RESOURCE_TYPE );
        extender.setIsActive( true );
        _extenderService.create( extender );

        return extender;
    }
}
