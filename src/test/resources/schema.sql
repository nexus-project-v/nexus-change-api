CREATE SCHEMA IF NOT EXISTS change;

CREATE TABLE IF NOT EXISTS change.tb_component (
	id UUID NOT NULL,
	name VARCHAR(255) NOT NULL,
	version VARCHAR(255),
	created_date TIMESTAMP NOT NULL,
	create_by VARCHAR(255) NOT NULL,
	last_modified_date TIMESTAMP,
	last_modified_by VARCHAR(255),
	status VARCHAR(20) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS change.tb_change (
	id UUID DEFAULT RANDOM_UUID() NOT NULL,
	title VARCHAR(255) NOT NULL,
	description VARCHAR(255),
	component_id UUID,
	environment VARCHAR(20),
	change_type VARCHAR(30),
	change_status VARCHAR(30),
	request_by VARCHAR(255) NOT NULL,
	created_date TIMESTAMP NOT NULL,
	create_by VARCHAR(255) NOT NULL,
	last_modified_date TIMESTAMP,
	last_modified_by VARCHAR(255),
	status VARCHAR(20) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_tb_change_component FOREIGN KEY (component_id) REFERENCES change.tb_component (id)
);

CREATE TABLE IF NOT EXISTS change.tb_change_log (
	id UUID DEFAULT RANDOM_UUID() NOT NULL,
	change_id UUID,
	change_status VARCHAR(30),
	created_date TIMESTAMP NOT NULL,
	create_by VARCHAR(255) NOT NULL,
	last_modified_date TIMESTAMP,
	last_modified_by VARCHAR(255),
	status VARCHAR(20) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_tb_change_log_change FOREIGN KEY (change_id) REFERENCES change.tb_change (id)
);

CREATE TABLE IF NOT EXISTS change.tb_schedule_window (
	id UUID NOT NULL,
	responsible VARCHAR(255) NOT NULL,
	schedule_start TIMESTAMP NOT NULL,
	schedule_end TIMESTAMP NOT NULL,
	change_id UUID NOT NULL,
	created_date TIMESTAMP NOT NULL,
	create_by VARCHAR(255) NOT NULL,
	last_modified_date TIMESTAMP,
	last_modified_by VARCHAR(255),
	status VARCHAR(20) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_tb_schedule_window_change FOREIGN KEY (change_id) REFERENCES change.tb_change (id)
);

