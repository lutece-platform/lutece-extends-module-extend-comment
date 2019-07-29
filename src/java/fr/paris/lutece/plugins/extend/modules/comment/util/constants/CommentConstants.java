/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.extend.modules.comment.util.constants;

/**
 * 
 * CommentConstants
 * 
 */
public final class CommentConstants
{
    // PROPERTIES
    public static final String PROPERTY_XPAGE_VIEW_COMMENTS_PAGE_TITLE = "module.extend.comment.view_comments.pageTitle";
    public static final String PROPERTY_XPAGE_VIEW_COMMENTS_PATH_LABEL = "module.extend.comment.view_comments.pageLabel";
    public static final String PROPERTY_XPAGE_ADD_COMMENT_PAGE_TITLE = "module.extend.comment.add_comment.pageTitle";
    public static final String PROPERTY_XPAGE_ADD_COMMENT_PAGE_LABEL = "module.extend.comment.add_comment.pageLabel";
    public static final String PROPERTY_MANAGE_COMMENTS_PAGE_TITLE = "module.extend.comment.manage_comments.pageTitle";
    public static final String PROPERTY_COMMENT_CONFIG_LABEL_NO_MAILING_LIST = "module.extend.comment.comment_config.labelNoMailingList";
    public static final String PROPERTY_DEFAULT_LIST_COMMENTS_PER_PAGE = "module.extend.comment.commentInfo.itemsPerPage";
    public static final String PROPERTY_USE_CAPTCHA = "module.extend.comment.useCaptcha";
    public static final String PROPERTY_USE_LUTECE_USER_NAME_AS_AVATAR_KEY = "module.extend.comment.useLuteceUserNameAsAvatarKey";
    public static final String PROPERTY_COMMENT_ALL_STATE = "module.extend.comment.comment_info.commentAllState";
    public static final String PROPERTY_COMMENT_STATE_PUBLISHED = "module.extend.comment.comment_info.statePublished";
    public static final String PROPERTY_COMMENT_STATE_UN_PUBLISHED = "module.extend.comment.comment_info.stateNotPublished";
    public static final String PROPERTY_COMMENT_FILTER_BY_PINNED = "module.extend.comment.comment_info.filterByPinned";
    public static final String PROPERTY_COMMENT_FILTER_BY_PINNED_ALL_PINNED = "module.extend.comment.comment_info.filterByPinnedAllPinned";
    public static final String PROPERTY_COMMENT_FILTER_BY_PINNED_ALL_NOT_PINNED = "module.extend.comment.comment_info.filterByPinnedAllNotPinned";
    public static final String PROPERTY_COMMENT_FILTER_BY_IMPORTANT = "module.extend.comment.comment_info.filterByImportant";
    public static final String PROPERTY_COMMENT_FILTER_BY_IMPORTANT_ALL_FLAG_IMPORTANT = "module.extend.comment.comment_info.filterByImportantAllFlagAsImportant";
    public static final String PROPERTY_COMMENT_FILTER_BY_IMPORTANT_ALL_NOT_FLAG_AS_IMPORTANT = "module.extend.comment.comment_info.filterByImportantAllNotFlagAsImportant";

    // MESSAGES
    public static final String MESSAGE_CONFIRM_REMOVE_COMMENT = "module.extend.comment.message.confirm.removeComment";
    public static final String MESSAGE_ERROR_GENERIC_MESSAGE = "module.extend.comment.message.error.genericMessage";
    public static final String MESSAGE_NOTIFY_SUBJECT = "module.extend.comment.message.notify.subject";
    public static final String MESSAGE_ERROR_CANNOT_MODIFY_USER_COMMENT = "module.extend.comment.message.error.cannotModifyUserComment";

    public static final String MESSAGE_ERROR_CANNOT_DELETE = "module.extend.comment.message.error.cannotDelete";

    // PARAMETERS
    public static final String PARAMETER_ACTION = "action";
    public static final String PARAMETER_EXTENDER_TYPE = "extenderType";
    public static final String PARAMETER_ID_EXTENDABLE_RESOURCE = "idExtendableResource";
    public static final String PARAMETER_EXTENDABLE_RESOURCE_TYPE = "extendableResourceType";
    public static final String PARAMETER_ID_COMMENT = "idComment";
    public static final String PARAMETER_ID_PARENT_COMMENT = "idParentComment";
    public static final String PARAMETER_REFERER = "referer";
    public static final String PARAMETER_FROM_URL = "from_url";
    public static final String PARAMETER_AUTHORIZE_SUB_COMMENTS = "authorizeSubComments";
    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_VIEW_ALL_RESOURCES="viewAllResources";
    public static final String PARAMETER_FILTER_STATE = "filterState";
    public static final String PARAMETER_FILTER_PINNED = "filterPinned";
    public static final String PARAMETER_FILTER_MARK_AS_IMPORTANT = "filterMarkAsImportant";
    public static final String PARAMETER_CONFIRM_REMOVE_COMMENT = "confirm_remove_comment";
    public static final String PARAMETER_PAGE = "page";

    // BEANS
    public static final String BEAN_CONFIG_SERVICE = "extend-comment.commentExtenderConfigService";

    // MARKS
    public static final String MARK_LIST_COMMENTS = "listComments";
    public static final String MARK_ID_EXTENDABLE_RESOURCE = "idExtendableResource";
    public static final String MARK_EXTENDABLE_RESOURCE_TYPE = "extendableResourceType";
    public static final String MARK_COMMENT_CONFIG = "commentConfig";
    public static final String MARK_MYLUTECE_USER = "myLuteceUser";
    public static final String MARK_LIST_IDS_MAILING_LIST = "listIdsMailingList";
    public static final String MARK_COMMENT = "comment";
    public static final String MARK_ID_PARENT_COMMENT = "idParentComment";
    public static final String MARK_RESOURCE_EXTENDER = "resourceExtender";
    public static final String MARK_RESOURCE_EXTENDER_MAP = "resourceExtenderMap";
    public static final String MARK_RESOURCE_EXTENDER_NAME = "resourceExtenderName";
    public static final String MARK_RESOURCE_EXTENDER_DESCRIPTION = "resourceExtenderDescription";
    public static final String MARK_RESOURCE_EXTENDER_URL = "resourceExtenderUrl";
    public static final String MARK_PAGINATOR = "paginator";
    public static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    public static final String MARK_WEBAPP_URL = "webapp_url";
    public static final String MARK_LOCALE = "locale";
    public static final String MARK_USE_BBCODE = "use_bbcode";
    public static final String MARK_ALLOW_SUB_COMMENTS = "allowSubComments";
    public static final String MARK_DISPLAY_SUB_COMMENTS = "displaySubComments";
    public static final String MARK_MESSAGE_COMMENT_CREATED = "message_comment_created";
    public static final String MARK_RETURN_TO_COMMENT_LIST = "returnToCommentList";
    public static final String MARK_ENABLE_AUTH_MODE="enable_auth_mode";
    public static final String MARK_ALL_RESOURCES="allResources";
    public static final String MARK_LIST_COMMENT_STATES = "listCommentStates";
    public static final String MARK_LIST_MARK_AS_IMPORTANT_FILTER = "listMarkAsImportantFilter";
    public static final String MARK_LIST_PINNED_FILTER = "listPinnedFilter";
    public static final String MARK_REGISTERED_USER_EMAIL = "registeredUserEmail";
    public static final String MARK_NB_PUBLISHED_COMMENTS = "nbPublishedComments";
    public static final String MARK_CAN_DELETE_COMMENTS = "canDeleteComments";
    public static final String MARK_TRI_COMMENTS_BY_DATE_CREATION = "triCommentsByDateCreation";
    
    
    public static final String MARK_ADD_COMMENT_POSITION = "addCommentPosition";
    public static final String MARK_ADD_COMMENT_POSITIONS = "allPositions";
    public static final String MARK_CAPTCHA = "captcha";
    public static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    public static final String MARK_FILTER_STATE = "filterState";
    public static final String MARK_FILTER_PINNED = "filterPinned";
    public static final String MARK_FILTER_MARK_AS_IMPORTANT = "filterMarkAsImportant";
    public static final String MARK_COMMENT_CLOSED = "commentClosed";

    // ACTIONS
    public static final String ACTION_ADD_COMMENT = "addComment";
    public static final String ACTION_DO_ADD_COMMENT = "doAddComment";
    public static final String ACTION_CONFIRM_REMOVE_COMMENT = "confirmRemoveComment";
    public static final String ACTION_REMOVE_COMMENT = "removeComment";

    // CONSTANTS
    public static final String MARK_ASC_SORT = "asc_sort";

    public static final String CONSTANT_FIRST_PAGE_NUMBER = "1";
    public static final String CONSTANT_AND = "&";
    public static final String CONSTANT_AND_HTML = "%26";
    public static final String CONSTANT_ALL_RESSOURCE_ID = "*";
  

    public static final String SESSION_COMMENT_ITEMS_PER_PAGE = "session_comment_fo_items_per_page";
    public static final String SESSION_COMMENT_CURRENT_PAGE_INDEX = "session_comment_fo_current_page_index";
    public static final String SESSION_COMMENT_IS_ASC_SORT = "session_comment_fo_is_asc_sort";
    public static final String SESSION_COMMENT_ADMIN_ITEMS_PER_PAGE = "session_comment_bo_items_per_page";
    public static final String SESSION_COMMENT_ADMIN_CURRENT_PAGE_INDEX = "session_comment_bo_current_page_index";
    public static final String SESSION_COMMENT_ADMIN_IS_ASC_SORT = "session_comment_bo_is_asc_sort";
    public static final String SESSION_COMMENT_ADMIN_SORTED_ATTRIBUTE_NAME = "session_comment_bo_sorted_attribute_name";
    public static final String SESSION_COMMENT_ADMIN_FILTER_STATE = "session_comment_bo_state";
    public static final String SESSION_COMMENT_ADMIN_FILTER_PINNED = "session_comment_bo_filter_pinned";
    public static final String SESSION_COMMENT_ADMIN_FILTER_MARK_AS_IMPORTANT = "session_comment_bo_filter_mark_as_important";
    
    
    public static final String MARK_ADMIN_BADGE = "adminBadge";
    public static final String SESSION_COMMENT_POST_BACK_URL = "commentPostBackUrl";
    public static final String PARAMETER_POST_BACK_URL = "postBackUrl";
    public static final String SESSION_COMMENT_ADMIN_POST_BACK_URL = "adminInfoPostBackUrl";
    public static final String SESSION_COMMENT_ADD_MESSAGE_RESULT = "addCommentMessageResult";

   
    public static final String MARK_NICKNAME = "nickname";
    public static final String JCAPTCHA_PLUGIN = "jcaptcha";
	public static final String ADD_COMMENT_MESSAGE_RESULT_ANCHOR = "message";
	public static final String MARK_ADD_COMMENT_MESSAGE_RESULT = "addCommentMessageResult";
	public static final String FROM_SESSION = "from_session";
	
	public static final String  SORT_BY_COMMENT_ORDER ="comment_order" ;
	
    /**
     * Instantiates a new comment constants.
     */
    private CommentConstants( )
    {
    }
}
