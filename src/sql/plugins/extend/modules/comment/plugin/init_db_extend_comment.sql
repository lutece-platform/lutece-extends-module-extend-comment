-- liquibase formatted sql
-- changeset extend-comment:init_db_extend_comment.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO extend_comment_config (id_extender,is_moderated,nb_comments,id_mailing_list,authorize_sub_comments,use_bbcode,admin_badge,message_comment_created) VALUES(-1,1,10,-1,0,1,'<span class="badge badge-warning">#i18n{module.extend.comment.comment_info.labelAdminComment}</span>','Votre commentaire a bien été créé.<br />Il sera disponible une fois qu\'il aura été validé pas un administrateur.');
