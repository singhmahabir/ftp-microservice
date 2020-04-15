create table ftp.ftp_file_entity (file_id bigint generated by default as identity, checksum varchar(255), created_by varchar(255), 
created_date timestamp, file_data binary(255), file_name varchar(255), file_size bigint, file_type integer, last_modified_by varchar(255),
last_modified_date timestamp, status integer, user_name varchar(255), primary key (file_id));

create table ftp.ftp_user_entity (ftp_user_name varchar(255) not null, created_by varchar(255), creation_date timestamp,
active boolean, last_activated_by varchar(255), last_modified_by varchar(255), last_modified_date timestamp,
last_password_changed_date timestamp, password varchar(255), primary key (ftp_user_name));

create table oauth_client (client_id varchar(255) not null, access_token_validity_seconds integer,
authorities varchar(255), authorized_grant_types varchar(255), auto_approve boolean not null, 
client_secret varchar(255) not null, refresh_token_validity_seconds integer, resource_ids varchar(255) not null,
scope varchar(255) not null, scoped boolean not null, secret_required boolean not null, primary key (client_id));

create table user (id integer not null, active boolean not null, password varchar(255), roles varchar(255), user_name varchar(255), primary key (id));

alter table user drop constraint if exists UK_lqjrcobrh9jc8wpcar64q1bfh;

alter table user add constraint UK_lqjrcobrh9jc8wpcar64q1bfh unique (user_name);

create sequence hibernate_sequence start with 1 increment by 1;

-- india $2y$31$yTdNCfakJquiRquDtUkb5OlEOXJ3melLey3WEukWF4yfxJh4LN52m
insert into ftp.FTP_USER_ENTITY(ftp_user_name, password, last_Modified_By, last_Modified_date, creation_date, created_by,
last_activated_by, last_password_changed_date, active)
values('india', '$2y$31$yTdNCfakJquiRquDtUkb5OlEOXJ3melLey3WEukWF4yfxJh4LN52m', 'Mahabir', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Mahabir',
'Mahabir', CURRENT_TIMESTAMP, true);

-- msdeo4u $2y$31$k7v1G/Y3tnns8QgYlysskeTwTykIf7RvhvcI5/luCbkT7pZexwP0C
insert into ftp.FTP_USER_ENTITY(ftp_user_name, password, last_Modified_By, last_Modified_date, creation_date, created_by,
last_activated_by, last_password_changed_date, active)
values('msdeo4u', '$2y$31$k7v1G/Y3tnns8QgYlysskeTwTykIf7RvhvcI5/luCbkT7pZexwP0C', 'Mahabir', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Mahabir',
'Mahabir', CURRENT_TIMESTAMP, true);


insert into User
values(hibernate_sequence.NEXTVAL, true, '$2y$31$yTdNCfakJquiRquDtUkb5OlEOXJ3melLey3WEukWF4yfxJh4LN52m', 'ROLE_ADMIN','india');

insert into User
values(hibernate_sequence.NEXTVAL, true, '$2y$31$k7v1G/Y3tnns8QgYlysskeTwTykIf7RvhvcI5/luCbkT7pZexwP0C', 'ROLE_ADMIN', 'msdeo4u');



