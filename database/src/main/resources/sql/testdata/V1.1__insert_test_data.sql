INSERT INTO "user" ("handle", "first_name", "last_name")
VALUES('cdivvela', 'Chaitanya', 'Divvela');

INSERT INTO "user" ("handle", "first_name", "last_name")
VALUES('caddala', 'Chandra', 'Addala');

insert into "project" ("project_name", "project_owner")
values ('spacex-landing-pad', select "id" from "user" where "handle" = 'cdivvela');

insert into "task" ("task_name", "project_id", "created_by", "assigned_to", "percentage_complete")
values ('dock-cargo-ship',
        select "id" from "project" where "project_name" = 'spacex-landing-pad',
        select "id" from "user" where "handle" = 'cdivvela',
        select "id" from "user" where "handle" = 'cdivvela', 40.8);

insert into "task" ("task_name", "project_id", "created_by", "assigned_to", "percentage_complete")
values ('bring-snacks',
        select "id" from "project" where "project_name" = 'spacex-landing-pad',
        select "id" from "user" where "handle" = 'cdivvela',
        select "id" from "user" where "handle" = 'caddala', 10.56);