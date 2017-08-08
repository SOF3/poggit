ALTER TABLE builds ADD COLUMN triggerEvent VARCHAR(63) NOT NULL DEFAULT 'UnknownEvent' AFTER sha;
ALTER TABLE builds ADD COLUMN prNumber INT UNSIGNED DEFAULT NULL AFTER triggerEvent;
UPDATE builds SET triggerEvent = 'PushEvent' WHERE class=1;
UPDATE builds SET triggerEvent = 'PullRequestEvent' WHERE class=4;
UPDATE builds SET prNumber = substring_index(substring(cause, locate('"prNumber"', cause) + 11), ',', 1) WHERE class=4;
ALTER TABLE builds ADD COLUMN prHeadOwner INT UNSIGNED DEFAULT NULL AFTER prNumber;
ALTER TABLE builds ADD COLUMN prHeadRef VARCHAR(255) DEFAULT NULL AFTER prHeadOwner;
ALTER TABLE builds DROP COLUMN cause;
