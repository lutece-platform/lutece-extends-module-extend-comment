-- liquibase formatted sql
-- changeset extend-comment:update_db_extend_comment-1.1.5-1.1.6.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- EXTENDCOMMENT- : Add authentication mod
--
ALTER TABLE extend_comment_config ADD COLUMN is_enabled_auth_mode SMALLINT NOT NULL default 0;
ALTER TABLE extend_comment ADD COLUMN lutece_user_name VARCHAR(255) ;

--
-- EXTENDCOMMENT- : Add is_pinned,is_important,comment_order
--
ALTER TABLE extend_comment ADD COLUMN is_pinned SMALLINT default 0 NOT NULL;
ALTER TABLE extend_comment ADD COLUMN is_important SMALLINT default 0 NOT NULL;
ALTER TABLE extend_comment ADD COLUMN comment_order INT DEFAULT 0 NOT NULL;


