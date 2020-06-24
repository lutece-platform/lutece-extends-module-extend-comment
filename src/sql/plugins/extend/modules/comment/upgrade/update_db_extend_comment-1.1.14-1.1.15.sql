
ALTER TABLE extend_comment_config ADD COLUMN id_workflow INT DEFAULT NULL;

ALTER TABLE extend_comment MODIFY COLUMN id_comment INT NOT NULL AUTO_INCREMENT;
