-- liquibase formatted sql
-- changeset extend-comment:update_db_extend_comment-1.0.1-1.1.4.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- EXTENDCOMMENT- : Add possibility to choose position of comment form
--
ALTER TABLE extend_comment_config ADD COLUMN add_comment_position SMALLINT NOT NULL default 0;

