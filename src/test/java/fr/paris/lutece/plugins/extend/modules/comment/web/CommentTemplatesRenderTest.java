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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Renders the module-extend-comment templates that hold HTML or URLs, with HTML auto-escaping (LUT-33153): the captcha
 * and the workflow task form, marked with HtmlMarkup, keep their tags; a fragment left as a String prints as text; a
 * comment typed in the plain editor is text written line by line; the comments written with the rich text editor or
 * from the back office, the administrator badge and the message shown once a comment is created are rich text written
 * with richText(); the links are escaped once; an address taken from the request goes through safeUrl(). The core
 * macros the templates call are replaced by stand-ins that write their arguments (escaped) and their nested content:
 * only the FreeMarker engine is needed.
 */
public class CommentTemplatesRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_COMMENT = "skin/plugins/extend/modules/comment/comment.html";
    private static final String TEMPLATE_VIEW_COMMENTS = "skin/plugins/extend/modules/comment/view_comments.html";
    private static final String TEMPLATE_CREATED = "skin/plugins/extend/modules/comment/message_comment_created.html";
    private static final String TEMPLATE_NOTIFY = "skin/plugins/extend/modules/comment/comment_notify_message.html";
    private static final String TEMPLATE_INFO = "admin/plugins/extend/modules/comment/comment_info.html";
    private static final String TEMPLATE_TASKS_FORM = "admin/plugins/extend/modules/comment/tasks_form_workflow.html";

    /**
     * Stand-ins of the core macros and functions the templates call. The macros taking params write them, which must be
     * a map; safeUrl keeps a relative or http(s) URL and returns the fallback for any other scheme, as the core function
     * does; richText returns its content as markup (the core one sanitizes it first).
     */
    private static final String MACROS = "macros.ftl";
    private static final String MACROS_SOURCE = "<#macro attrs params><#list params as key, param> ${key}=\"${param}\"</#list></#macro>"
            + "<#macro cTpl rest...><#nested></#macro><#macro cContainer rest...><#nested></#macro><#macro cRow rest...><#nested></#macro>"
            + "<#macro cCol rest...><#nested></#macro><#macro cCard id='' rest...><div class=\"card\" id=\"${id}\"><#nested></div></#macro>"
            + "<#macro cTitle rest...><h3><#nested></h3></#macro><#macro cText rest...><p><#nested></p></#macro>"
            + "<#macro cBlock id='' rest...><div id=\"${id}\"><#nested></div></#macro><#macro cIcon rest...><i></i></#macro>"
            + "<#macro cAlert rest...><div class=\"alert\"><#nested></div></#macro>"
            + "<#macro cImg src alt='' params={} rest...><img src=\"${src}\" alt=\"${alt}\"<@attrs params />></#macro>"
            + "<#macro cLink href label='' title='' rest...><a href=\"${href}\" title=\"${title}\">${label}<#nested></a></#macro>"
            + "<#macro cBtn label='' href='' params={} rest...><#if href?has_content><a href=\"${href}\"<@attrs params />>${label}<#nested></a>"
            + "<#else><button<@attrs params />>${label}<#nested></button></#if></#macro>"
            + "<#macro cForm action='' rest...><form action=\"${action}\"><#nested></form></#macro>"
            + "<#macro cField label='' rest...><label>${label}</label><#nested></#macro>"
            + "<#macro cInput name='' value='' rest...><input name=\"${name}\" value=\"${value}\"></#macro>"
            + "<#macro cTextArea name='' rest...><textarea name=\"${name}\"></textarea></#macro>"
            + "<#macro cPagination rest...></#macro><#macro paginationSiteItemCount rest...></#macro><#macro paginationAdmin rest...></#macro>"
            + "<#macro pageContainer rest...><#nested></#macro><#macro pageColumn rest...><#nested></#macro>"
            + "<#macro pageHeader title='' rest...><h1>${title}</h1><#nested></#macro>"
            + "<#macro tform action='' rest...><form action=\"${action}\"><#nested></form></#macro>"
            + "<#macro input name='' value='' rest...><input name=\"${name}\" value=\"${value}\"></#macro>"
            + "<#macro aButton href='' title='' params={} rest...><a href=\"${href}\" title=\"${title}\"<@attrs params />><#nested></a></#macro>"
            + "<#macro button title='' params={} rest...><button<@attrs params />>${title}</button></#macro>"
            + "<#macro img url='' title='' alt='' params={} rest...><img src=\"${url}\" alt=\"${alt}\"<@attrs params />></#macro>"
            + "<#macro div rest...><div><#nested></div></#macro><#macro span rest...><span><#nested></span></#macro>"
            + "<#macro icon rest...><i></i></#macro><#macro tag rest...><span class=\"tag\"><#nested></span></#macro>"
            + "<#macro row rest...><div class=\"row\"><#nested></div></#macro><#macro columns rest...><#nested></#macro>"
            + "<#macro modal rest...><#nested></#macro><#macro modalHeader rest...></#macro><#macro modalBody rest...><#nested></#macro>"
            + "<#macro formGroup rest...><#nested></#macro><#macro select rest...><select><#nested></select></#macro>"
            + "<#macro option label='' value='' rest...><option value=\"${value}\">${label}</option></#macro>"
            + "<#macro manageFeature rest...><ul><#nested></ul></#macro><#macro manageFeatureItem rest...><li><#nested></li></#macro>"
            + "<#macro manageFeatureItemColumn rest...><#nested></#macro><#macro empty title='' rest...><div class=\"empty\">${title}</div></#macro>"
            + "<#function safeUrl url fallback='#'><#if url?matches( '(?i)^\\\\s*(https?:|/|[^:/?#]*([/?#]|$)).*', 's' )><#return url /></#if><#return fallback /></#function>"
            + "<#function richText content><#return content?no_esc /></#function>";

    private static final String CAPTCHA = "<div class=\"captcha\"><img src=\"jcaptcha\" alt=\"captcha\"><input type=\"text\" name=\"j_captcha_response\"></div>";
    private static final String TASKS_FORM = "<div class=\"mb-3\"><label for=\"comment\">Comment</label><textarea name=\"comment\" id=\"comment\"></textarea></div>";
    private static final String RICH = "<p>Thanks for your <strong>answer</strong></p>";
    private static final Timestamp DATE = new Timestamp( 1_794_500_000_000L );
    private static final String BADGE = "<span class=\"badge\">Team</span>";

    /**
     * Renders a template of the module with the core configuration of the output format.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     * @throws IOException
     *             if the template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strTemplate, Map<String, Object> model ) throws IOException, TemplateException
    {
        StringTemplateLoader macros = new StringTemplateLoader( );
        macros.putTemplate( MACROS, MACROS_SOURCE );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setURLEscapingCharset( "UTF-8" );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                macros, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( MACROS );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( model, writer );

        return writer.toString( );
    }

    /**
     * A comment as the templates read it. The lines come from {@link Comment#getCommentLines( )}; the BBCode and the
     * avatar, read through CDI by the business object, are given as is.
     *
     * @param nId
     *            the comment id
     * @param strComment
     *            the stored comment
     * @param bAdmin
     *            true for a comment written from the back office
     * @return the comment
     */
    private static Map<String, Object> comment( int nId, String strComment, boolean bAdmin )
    {
        Comment comment = new Comment( );
        comment.setComment( strComment );

        Map<String, Object> map = new HashMap<>( );
        map.put( "idComment", nId );
        map.put( "comment", strComment );
        map.put( "commentLines", comment.getCommentLines( ) );
        map.put( "BBCodeComment", strComment );
        map.put( "isAdminComment", bAdmin );
        map.put( "name", "Jane <i>Doe</i>" );
        map.put( "email", "jane@example.org" );
        map.put( "dateComment", DATE );
        map.put( "dateLastModif", DATE );
        map.put( "avatarUrl", "" );
        map.put( "numberSubComments", 0 );
        map.put( "listSubComments", List.of( ) );
        map.put( "published", true );
        map.put( "pinned", false );
        map.put( "isImportant", false );
        map.put( "firstUnpublishedSubComment", 0 );
        map.put( "listWorkflowActions", List.of( ) );

        return map;
    }

    /**
     * The model of the comments added to the page of a resource.
     *
     * @param captcha
     *            the captcha
     * @return the model
     */
    private static Map<String, Object> commentModel( Object captcha )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "listComments", List.of( comment( 1234, "Line <b>one</b>\r\n<br />\nline two", false ), comment( 2, RICH, true ) ) );
        model.put( "idExtendableResource", "42\"><script>" );
        model.put( "extendableResourceType", "PAGE&x=1" );
        model.put( "addCommentPosition", 1 );
        model.put( "use_bbcode", false );
        model.put( "adminBadge", BADGE );
        model.put( "allowSubComments", false );
        model.put( "displaySubComments", true );
        model.put( "commentConfig", Map.of( "useBBCodeEditor", false, "enabledAuthMode", false, "moderated", false ) );
        model.put( "is_active_captcha", true );
        model.put( "captcha", captcha );
        model.put( "token", "tok" );

        return model;
    }

    /**
     * The comments of a resource: the plain comment is text kept line by line, the back office comment and the badge
     * are rich text, the captcha marked by the component is HTML, and the links are escaped once.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testCommentsOfAResource( ) throws Exception
    {
        String strHtml = render( TEMPLATE_COMMENT, commentModel( HtmlMarkup.of( CAPTCHA ) ) );

        assertTrue( strHtml.contains( "Line &lt;b&gt;one&lt;/b&gt;<br><br>line two" ), strHtml );
        assertTrue( strHtml.contains( RICH ), strHtml );
        assertTrue( strHtml.contains( BADGE ), strHtml );
        assertTrue( strHtml.contains( CAPTCHA ), strHtml );
        assertTrue( strHtml.contains( "Jane &lt;i&gt;Doe&lt;/i&gt;" ), strHtml );
        assertTrue( strHtml.contains( "href=\"jsp/site/Portal.jsp?page=extend-comment&amp;idExtendableResource=42%22%3E%3Cscript%3E&amp;extendableResourceType=PAGE%26x%3D1\"" ),
                strHtml );
        assertTrue( strHtml.contains( "@Extender[1234,Comment,rating,{show:\"vote\"}]@" ), strHtml );
        assertFalse( strHtml.contains( "&amp;amp;" ), strHtml );
        assertFalse( strHtml.contains( "<script>" ), strHtml );
    }

    /**
     * A captcha left as a String is escaped: the reason the component marks it.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAStringCaptchaIsEscaped( ) throws Exception
    {
        String strHtml = render( TEMPLATE_COMMENT, commentModel( CAPTCHA ) );

        assertFalse( strHtml.contains( CAPTCHA ), strHtml );
        assertTrue( strHtml.contains( "&lt;div class=&quot;captcha&quot;&gt;" ), strHtml );
    }

    /**
     * With the rich text editor, every comment is rich text.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testCommentsWrittenWithTheRichTextEditor( ) throws Exception
    {
        Map<String, Object> model = commentModel( HtmlMarkup.of( CAPTCHA ) );
        model.put( "use_bbcode", true );
        model.put( "listComments", List.of( comment( 3, RICH, false ) ) );

        String strHtml = render( TEMPLATE_COMMENT, model );

        assertTrue( strHtml.contains( RICH ), strHtml );
    }

    /**
     * The model of the comment list page.
     *
     * @param strFromUrl
     *            the page the visitor came from
     * @return the model
     */
    private static Map<String, Object> viewCommentsModel( String strFromUrl )
    {
        Map<String, Object> model = commentModel( HtmlMarkup.of( CAPTCHA ) );
        model.put( "paginator", Map.of( "pageItems", model.get( "listComments" ), "itemsPerPage", 10 ) );
        model.put( "postBackUrl", "jsp/site/Portal.jsp?page=extend-comment" );
        model.put( "from_url", strFromUrl );
        model.put( "asc_sort", "true" );

        return model;
    }

    /**
     * The comment list page escapes its links once and goes back to the page the visitor came from.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testViewComments( ) throws Exception
    {
        String strHtml = render( TEMPLATE_VIEW_COMMENTS, viewCommentsModel( "jsp/site/Portal.jsp?page_id=2%26a=b" ) );

        assertTrue( strHtml.contains( "Line &lt;b&gt;one&lt;/b&gt;<br><br>line two" ), strHtml );
        assertTrue( strHtml.contains( RICH ), strHtml );
        assertTrue( strHtml.contains( "id=\"comment_1234\"" ), strHtml );
        assertTrue( strHtml.contains( "<a href=\"jsp/site/Portal.jsp?page_id=2&amp;a=b\">" ), strHtml );
        assertTrue( strHtml.contains( "&amp;from_url=from_session&amp;sorted_attribute_name=date&amp;asc_sort=true\"" ), strHtml );
        assertFalse( strHtml.contains( "&amp;amp;" ), strHtml );
    }

    /**
     * An address of another scheme in from_url is replaced by safeUrl().
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testViewCommentsReplacesAnotherScheme( ) throws Exception
    {
        String strHtml = render( TEMPLATE_VIEW_COMMENTS, viewCommentsModel( "javascript:alert(1)" ) );

        assertFalse( strHtml.contains( "href=\"javascript:" ), strHtml );
        assertTrue( strHtml.contains( "<a href=\"#\">" ), strHtml );
    }

    /**
     * The page shown once a comment is created writes the configured message as rich text and links back to the comment.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMessageCommentCreated( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "message_comment_created", RICH );
        model.put( "postBackUrl", "jsp/site/Portal.jsp?page=extend-comment" );
        model.put( "idExtendableResource", "42" );
        model.put( "extendableResourceType", "PAGE" );
        model.put( "idComment", 1234 );
        model.put( "returnToCommentList", true );

        String strHtml = render( TEMPLATE_CREATED, model );

        assertTrue( strHtml.contains( "<div class=\"alert\">" + RICH + "</div>" ), strHtml );
        assertTrue( strHtml.contains( "href=\"jsp/site/Portal.jsp?page=extend-comment&amp;idExtendableResource=42&amp;extendableResourceType=PAGE"
                + "&amp;idComment=1234&amp;from_url=from_session#comment_1234\"" ), strHtml );

        model.put( "returnToCommentList", false );
        model.put( "from_url", "javascript:alert(1)" );
        assertTrue( render( TEMPLATE_CREATED, model ).contains( "<a href=\"#\">" ) );
    }

    /**
     * The notification mail writes the comment as text, line by line.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testNotificationMail( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "comment", comment( 1, "Hi <b>all</b>\r\nbye", false ) );
        model.put( "resourceExtenderName", "Home <i>page</i>" );
        model.put( "resourceExtenderUrl", "http://localhost/lutece/jsp/site/Portal.jsp?page=extend-comment" );

        String strHtml = render( TEMPLATE_NOTIFY, model );

        assertTrue( strHtml.contains( "<em>Hi &lt;b&gt;all&lt;/b&gt;<br />bye</em>" ), strHtml );
        assertTrue( strHtml.contains( "Home &lt;i&gt;page&lt;/i&gt;" ), strHtml );
    }

    /**
     * The back office list of the comments: contents as in the front office, links escaped once, the size of a workflow
     * action icon given as attributes.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testCommentInfo( ) throws Exception
    {
        Map<String, Object> plain = comment( 1234, "Line <b>one</b>\nline two", false );
        plain.put( "listWorkflowActions", List.of( Map.of( "id", 5, "name", "Publish <now>", "icon", Map.of( "id", 9, "width", 16, "height", -1 ) ) ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( "listComments", List.of( plain, comment( 2, RICH, true ) ) );
        model.put( "idExtendableResource", "42" );
        model.put( "extendableResourceType", "PAGE" );
        model.put( "allResources", false );
        model.put( "allowSubComments", true );
        model.put( "use_bbcode", false );
        model.put( "from_url", "jsp/admin/Page.jsp?a=1%26b=2" );
        model.put( "paginator", Map.of( ) );
        model.put( "listPinnedFilter", List.of( ) );
        model.put( "listMarkAsImportantFilter", List.of( ) );
        model.put( "listCommentStates", List.of( ) );

        String strHtml = render( TEMPLATE_INFO, model );

        assertTrue( strHtml.contains( "Line &lt;b&gt;one&lt;/b&gt;<br>line two" ), strHtml );
        assertTrue( strHtml.contains( RICH ), strHtml );
        assertTrue( strHtml.contains( "href=\"jsp/admin/plugins/extend/modules/comment/ManageComments.jsp?view=confirmRemoveComment&amp;idComment=1234"
                + "&amp;viewAllResources=false&amp;from_url=jsp%2Fadmin%2FPage.jsp%3Fa%3D1%2526b%3D2\"" ), strHtml );
        assertTrue( strHtml.contains( "<img src=\"image?resource_type=workflow_icon_img&amp;id=9\" alt=\"Publish &lt;now&gt;\" width=\"16\">" ), strHtml );
        assertTrue( strHtml.contains( "data-code=\"#content_1234\"" ), strHtml );
        assertFalse( strHtml.contains( "&amp;amp;" ), strHtml );
    }

    /**
     * The tasks form page writes the task form marked by CommentJspBean as HTML; a String would be escaped.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testTasksFormKeepsTheTaskForm( ) throws Exception
    {
        String strHtml = render( TEMPLATE_TASKS_FORM, new HashMap<>( Map.of( "tasks_form", HtmlMarkup.of( TASKS_FORM ) ) ) );

        assertTrue( strHtml.contains( TASKS_FORM ), strHtml );
        assertTrue( strHtml.contains( "data-lutece-action=\"back\"" ), strHtml );

        String strEscaped = render( TEMPLATE_TASKS_FORM, new HashMap<>( Map.of( "tasks_form", TASKS_FORM ) ) );
        assertFalse( strEscaped.contains( TASKS_FORM ), strEscaped );
    }
}
