=========================================================

-- Table: "TA"."PROJECT"

-- DROP TABLE "TA"."PROJECT";

CREATE TABLE "TA"."PROJECT"
(
  project_id integer NOT NULL,
  project_name character varying(20),
  project_owner integer,
  created_date date,
  CONSTRAINT project_id_pk PRIMARY KEY (project_id),
  CONSTRAINT project_owner_fk FOREIGN KEY (project_owner)
      REFERENCES "TA"."USER" (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "TA"."PROJECT"
  OWNER TO postgres;


=========================================================

-- Table: "TA"."TASK"

-- DROP TABLE "TA"."TASK";

CREATE TABLE "TA"."TASK"
(
  task_id integer NOT NULL,
  task_name character varying(30),
  created_by integer,
  created_date date,
  percentage_complete numeric,
  project_id integer,
  assigned_to integer,
  CONSTRAINT task_id_pk PRIMARY KEY (task_id),
  CONSTRAINT assigned_to_fk FOREIGN KEY (assigned_to)
      REFERENCES "TA"."USER" (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT created_by_fk FOREIGN KEY (created_by)
      REFERENCES "TA"."USER" (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT project_id_fk FOREIGN KEY (project_id)
      REFERENCES "TA"."PROJECT" (project_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "TA"."TASK"
  OWNER TO postgres;
=========================================================

-- Table: "TA"."USER"

-- DROP TABLE "TA"."USER";

CREATE TABLE "TA"."USER"
(
  user_id integer NOT NULL, -- unique user id
  first_name character varying(50),
  last_name character varying(30),
  CONSTRAINT user_id_pk PRIMARY KEY (user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "TA"."USER"
  OWNER TO postgres;
COMMENT ON COLUMN "TA"."USER".user_id IS 'unique user id ';

=========================================================
