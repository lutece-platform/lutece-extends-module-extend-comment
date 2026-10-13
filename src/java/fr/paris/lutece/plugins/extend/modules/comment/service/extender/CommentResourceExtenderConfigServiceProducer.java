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
package fr.paris.lutece.plugins.extend.modules.comment.service.extender;

import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfigDAO;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.config.ResourceExtenderConfigService;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfigDAO;

import fr.paris.lutece.portal.service.cache.Lutece107Cache;
import fr.paris.lutece.portal.service.cache.LuteceCache;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.inject.Produces;

/**
 * Produces the configuration service of the comment extender.
 */
@ApplicationScoped
public class CommentResourceExtenderConfigServiceProducer {

    @Inject
    @LuteceCache(cacheName = "extendConfigCache", keyType = String.class, valueType = Object.class, enable = true)
    Lutece107Cache<String, Object> _extendConfigCache;

    @Inject 
    @Named( "extend-comment.CommentExtenderConfigDAO" ) 
    IExtenderConfigDAO<CommentExtenderConfig> commentExtenderConfigDAO;

    @Inject
    IResourceExtenderService resourceExtenderService;

    /**
     * Builds the configuration service of the comment extender on the module's DAO.
     *
     * @return the configuration service
     */
    @Produces
    @ApplicationScoped
    @Named( "extend-comment.commentExtenderConfigService" )
    public IResourceExtenderConfigService produceCommentResourceExtenderConfigService( )
    {
        ResourceExtenderConfigService commentExtenderConfigService = new ResourceExtenderConfigService( );

        commentExtenderConfigService.setExtenderConfigDAO( ( IExtenderConfigDAO ) commentExtenderConfigDAO );
        commentExtenderConfigService.setExtenderService( resourceExtenderService );
        commentExtenderConfigService.setExtenderCache( _extendConfigCache );

        return commentExtenderConfigService;
    }
    

}
