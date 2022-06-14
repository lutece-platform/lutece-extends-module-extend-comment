/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.extend.modules.comment.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.portal.service.dashboard.IPublicDashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * The Class MyPublicProjectCounterProfile.
 */
public class PublicDashboardComment implements IPublicDashboardComponent
{

	public static final String DASHBOARD_PROPERTIES_TITLE = "module.extend.comment.publicdashboard.bean.title";
	private String strIdComponent = "extend-comment.PublicDashboardComment";
	private static final String TEMPLATE_DASHBOARD_COMMENTS = "/skin/plugins/extend/modules/comment/publicdashboard_comments.html";
	private static final String MARK_DASHBOARD_COMMENTS = "comments_publidashboard";

	@Override
	public String getComponentDescription( ) {
		return I18nService.getLocalizedString( DASHBOARD_PROPERTIES_TITLE, I18nService.getDefaultLocale( ) );
	}

	@Override
	public String getComponentId( ) {
		return strIdComponent;
	}
	
	@Override
	public String getDashboardTemplate( )
	{
		return TEMPLATE_DASHBOARD_COMMENTS;
	}
	
	@Override
	public Map<String, Object> getDashboardModel( String user_id, Map<String,String[]> additionalParameters )
	{
		Map<String, Object> model = new HashMap<String, Object>( );
		model.put( MARK_DASHBOARD_COMMENTS, searchCommentUser( user_id ) );
		return model;
	}

	
    /**
     * Search project counter.
     *
     * @param guid the guid
     * @return the list
     */
    private static List<Comment> searchCommentUser( String user_id )
    {

        ICommentService commService = SpringContextService.getBean( CommentService.BEAN_SERVICE );

        List<Comment> lstComment = commService.findCommentsByLuteceUser( user_id, 0, 0 );

        return lstComment;
    }

	

}
