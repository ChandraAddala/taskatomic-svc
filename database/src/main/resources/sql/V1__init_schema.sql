-- DROP SCHEMA IF EXISTS "ta";
-- CREATE SCHEMA "ta";

DROP SEQUENCE IF EXISTS "user_id_seq";
CREATE SEQUENCE "user_id_seq";

DROP TABLE IF EXISTS "user";
CREATE TABLE "user"
(
  "id" integer DEFAULT nextval('user_id_seq') UNIQUE,
  "user_id" integer PRIMARY KEY, -- unique user id
  "first_name" character varying(50),
  "last_name" character varying(30)
);

DROP SEQUENCE IF EXISTS "project_id_seq";
CREATE SEQUENCE "project_id_seq";

DROP TABLE IF EXISTS "project";
CREATE TABLE "project"
(
  "id" integer DEFAULT nextval('project_id_seq') UNIQUE,
  "project_name" character varying(20),
  "project_owner" integer NOT NULL REFERENCES "user"("id"),
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("project_name", "project_owner") --no one can own two projects with the same name
);

DROP SEQUENCE IF EXISTS "task_id_seq";
CREATE SEQUENCE "task_id_seq";

DROP TABLE IF EXISTS "task";
CREATE TABLE "task"
(
  "id" integer DEFAULT nextval('task_id_seq') UNIQUE,
  "task_name" character varying(30),
  "project_id" integer NOT NULL REFERENCES "project"("id"),
  "created_by" integer NOT NULL REFERENCES "user"("id"),
  "assigned_to" integer NOT NULL REFERENCES "user"("id"),
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "percentage_complete" numeric,
  PRIMARY KEY ("task_name", "project_id") --no project have two tasks with the same name
);