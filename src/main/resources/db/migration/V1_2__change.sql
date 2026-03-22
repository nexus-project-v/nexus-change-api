create table changes.tb_change
(
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    title               varchar(255)                                not null,
    description         text,
    component_id        UUID                                        not null,
    environment         varchar(255)                                not null,
    change_type_id      UUID                                        not null,
    request_by          varchar(255)                                not null,
    create_by           varchar(255)                                not null,
    created_date        timestamp(6)                                not null,
    last_modified_by    varchar(255),
    last_modified_date  timestamp(6),
    status              varchar(255)                                not null,
    CONSTRAINT fk_change_component FOREIGN KEY (component_id) REFERENCES changes.tb_component(id) ON DELETE CASCADE,
    CONSTRAINT fk_change_type FOREIGN KEY (change_type_id) REFERENCES changes.tb_change_type(id) ON DELETE CASCADE
);
