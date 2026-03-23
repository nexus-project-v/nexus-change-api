create table change.tb_change_log
(
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    change_id           UUID                                        not null,
    change_status       varchar(60)                                 not null,
    create_by           varchar(255)                                not null,
    created_date        timestamp(6)                                not null,
    last_modified_by    varchar(255),
    last_modified_date  timestamp(6),
    status              varchar(255)                                not null,
    CONSTRAINT fk_change_status_id FOREIGN KEY (change_id) REFERENCES change.tb_change(id) ON DELETE CASCADE
);
