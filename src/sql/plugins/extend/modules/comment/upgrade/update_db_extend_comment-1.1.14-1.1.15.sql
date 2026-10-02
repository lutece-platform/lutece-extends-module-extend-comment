-- liquibase formatted sql
-- changeset extend-comment:update_db_extend_comment-1.1.14-1.1.15.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE extend_comment_config ADD COLUMN id_workflow INT DEFAULT NULL;

ALTER TABLE extend_comment MODIFY COLUMN id_comment INT NOT NULL AUTO_INCREMENT;
