package fr.paris.lutece.plugins.extend.modules.comment.service.extender;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.AbstractResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfigDAO;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.config.ResourceExtenderConfigService;
import fr.paris.lutece.plugins.extend.modules.comment.web.component.CommentResourceExtenderComponent;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfigDAO;

import fr.paris.lutece.portal.service.cache.Lutece107Cache;
import fr.paris.lutece.portal.service.cache.LuteceCache;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
