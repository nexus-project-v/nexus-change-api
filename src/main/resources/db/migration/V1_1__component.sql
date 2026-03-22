create table change.tb_component
(
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    name               varchar(255)                                not null,
    version            varchar(255)                                not null,
    create_by          varchar(255)                                not null,
    created_date       timestamp(6)                                not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    status             varchar(255)                                not null
);