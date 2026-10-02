-- liquibase formatted sql
-- changeset extend-comment:update_db_extend_comment-1.1.6-1.1.7.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE extend_comment_config ADD COLUMN is_comments_sorted_by_date_creation SMALLINT default 0 NOT NULL;




