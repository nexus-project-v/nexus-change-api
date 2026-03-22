create table change.tb_schedule_window
(
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    responsible        varchar(255)                                not null,
    schedule_start     timestamp(6)                                not null,
    schedule_end       timestamp(6)                                not null,
    change_id          UUID                                        not null,
    create_by          varchar(255)                                not null,
    created_date       timestamp(6)                                not null,
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    status             varchar(255)                                not null,
    CONSTRAINT fk_schedule_window_change FOREIGN KEY (change_id) REFERENCES change.tb_change(id) ON DELETE CASCADE,
    CONSTRAINT chk_schedule_window_time CHECK (schedule_end > schedule_start)
);

