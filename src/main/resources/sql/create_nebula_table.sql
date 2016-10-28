/* Create database nebula first before running the following DDL */

DROP DATABASE IF EXISTS nebula;
CREATE DATABASE nebula;

CREATE USER 'nebula'@'%' IDENTIFIED BY 'nebula';
GRANT ALL PRIVILEGES ON nebula.* TO 'nebula'@'%';

CREATE USER 'nebula'@'localhost' IDENTIFIED BY 'nebula';
GRANT ALL PRIVILEGES ON nebula.* TO 'nebula'@'localhost';
FLUSH PRIVILEGES;

USE nebula;

DROP TABLE IF EXISTS events;
CREATE TABLE events(
  id INT NOT NULL AUTO_INCREMENT COMMENT 'event ID',
  registrationId VARCHAR(64) NOT NULL COMMENT 'registration ID',
  eventType VARCHAR(64) NOT NULL COMMENT 'event Type',
  precedingId INT NOT NULL COMMENT 'previous event ID',
  instanceId VARCHAR(64) NOT NULL COMMENT 'instance ID',
  createdDate DATETIME NOT NULL COMMENT 'event created date',
  data TEXT COMMENT 'event data such as input or output',
  PRIMARY KEY(id)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'workflow event table';
 

CREATE INDEX idx_instanceId ON events (instanceId);
CREATE INDEX idx_regId_eventType ON events (registrationId, eventType);
CREATE UNIQUE INDEX un_idx_type_preId_instId ON events (eventType, precedingId, instanceId);

DROP TABLE IF EXISTS history_events;

CREATE TABLE history_events(
  id INT NOT NULL COMMENT 'event ID',
  registrationId VARCHAR(64) NOT NULL COMMENT 'registration ID',
  eventType VARCHAR(64) NOT NULL COMMENT 'event Type',
  precedingId INT NOT NULL COMMENT 'previous event ID',
  instanceId VARCHAR(64) NOT NULL COMMENT 'instance ID',
  createdDate DATETIME NOT NULL COMMENT 'event created date',
  data TEXT COMMENT 'event data such as input or output',
  PRIMARY KEY(id)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'workflow history event table';
 

CREATE INDEX idx_his_instanceId ON history_events (instanceId);
CREATE INDEX idx_his_regId_eventType ON history_events (registrationId, eventType);
CREATE UNIQUE INDEX un_idx_his_type_preId_instId ON history_events (eventType, precedingId, instanceId);


DROP TABLE IF EXISTS registrations;
CREATE TABLE registrations(
  id   VARCHAR(64) NOT NULL COMMENT 'registration ID',
  user VARCHAR(10) NOT NULL COMMENT 'Nebula user name',
  name VARCHAR(255) NOT NULL COMMENT 'Workflow or Activity name',
  version VARCHAR(255) NULL COMMENT 'Workflow or Activity version',
  type VARCHAR(10) NOT NULL COMMENT 'WORKFLOW or ACTIVITY',
  enabled TINYINT(1) NOT NULL COMMENT 'True if the workflow is enabled, false otherwise',
  createdDate DATETIME NOT NULL COMMENT 'registration date',
  modifiedDate DATETIME NOT NULL COMMENT 'modified date',
  data TEXT COMMENT 'registration info'
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'Nebula node registration table';

ALTER TABLE registrations ADD UNIQUE INDEX UNIQUE_USER_NAME_VERSION_TYPE(user, name, version, `type`);

DROP TABLE IF EXISTS heartbeats;
CREATE TABLE heartbeats(
  id VARCHAR(64) NOT NULL COMMENT 'heartbeat ID, UUID',
  host VARCHAR(255) NOT NULL COMMENT 'node hostname',
  ip VARCHAR(15) NOT NULL COMMENT 'node ip',
  processId VARCHAR(10) NOT NULL COMMENT 'node process id',
  workingDir VARCHAR(255) NOT NULL COMMENT 'node working directory',
  registrationId VARCHAR(64) NOT NULL COMMENT 'registration ID',
  heartbeatTime DATETIME NOT NULL COMMENT 'heartbeat time'
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'Nebula heartbeat table';

CREATE UNIQUE INDEX un_idx_regId_host_ip_workingDir ON heartbeats (registrationId, host, ip, workingDir);


DROP TABLE IF EXISTS workflow_timers;
CREATE TABLE workflow_timers(
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'workflow timer id',
  registrationId VARCHAR(64) NOT NULL COMMENT 'registration ID',
  username VARCHAR(10) NOT NULL COMMENT 'Nebula user name',
  realms  VARCHAR(512) NOT NULL COMMENT 'mq realms',
  lockOwner VARCHAR(64) COMMENT 'owner Id for scanning lock',
  lockExpireTime DATETIME COMMENT 'time when the lock expired',
  nextFireTime DATETIME NOT NULL COMMENT 'time when it is ready to invocation next instance',
  cronExpression VARCHAR(100) COMMENT 'cron expression for timer.',
  `serial` TINYINT(1) COMMENT 'Whether or not invocation after the completion of the previous one: 0 means false and 1 means true',
  createdDate DATETIME NOT NULL COMMENT 'created time',
  modifiedDate DATETIME NOT NULL COMMENT 'modified time',
  PRIMARY KEY (`id`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'Nebula workflow timers table';

CREATE UNIQUE INDEX un_idx_regId ON workflow_timers (registrationId);

DROP TABLE IF EXISTS activity_timers;
CREATE TABLE activity_timers(
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'activity timer id',
  registrationId VARCHAR(64) NOT NULL COMMENT 'registration ID',
  instanceId VARCHAR(64) COMMENT 'instance ID',
  eventId INT COMMENT 'event ID',
  username VARCHAR(10) NOT NULL COMMENT 'Nebula user name',
  realms  VARCHAR(512) NOT NULL COMMENT 'mq realms',
  lockOwner VARCHAR(64) COMMENT 'owner Id for scanning lock',
  lockExpireTime DATETIME COMMENT 'time when the lock expired',
  nextFireTime DATETIME NOT NULL COMMENT 'time when it is ready to invocation next instance',
  `interval` INT COMMENT 'interval in second between to two instance invocation',
  createdDate DATETIME NOT NULL COMMENT 'created time',
  modifiedDate DATETIME NOT NULL COMMENT 'modified time',
  PRIMARY KEY (`id`)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'Nebula activity timers table';

CREATE UNIQUE INDEX un_idx_instId_eventId ON activity_timers (instanceId, eventId);
