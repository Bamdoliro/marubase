CREATE TABLE IF NOT EXISTS tbl_admission_schedule (
    schedule_id BIGSERIAL PRIMARY KEY,
    admission_year INTEGER NOT NULL UNIQUE,
    active BOOLEAN NOT NULL,
    application_form_start TIMESTAMP NOT NULL,
    application_form_end TIMESTAMP NOT NULL,
    select_first_pass_at TIMESTAMP NOT NULL,
    announcement_of_first_pass TIMESTAMP NOT NULL,
    announcement_of_second_pass TIMESTAMP NOT NULL,
    coding_test TIMESTAMP NOT NULL,
    ncs TIMESTAMP NOT NULL,
    depth_interview TIMESTAMP NOT NULL,
    physical_examination TIMESTAMP NOT NULL,
    entrance_registration_period_start TIMESTAMP NOT NULL,
    entrance_registration_period_end TIMESTAMP NOT NULL,
    meister_talent_entrance_time TIMESTAMP NOT NULL,
    meister_talent_exclusion_entrance_time TIMESTAMP NOT NULL,
    admission_and_pledge_start TIMESTAMP NOT NULL,
    admission_and_pledge_end TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_admission_schedule_active
    ON tbl_admission_schedule (active)
    WHERE active = TRUE;

CREATE TABLE IF NOT EXISTS tbl_first_pass_selection_execution (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_first_pass_selection_schedule
        FOREIGN KEY (schedule_id) REFERENCES tbl_admission_schedule (schedule_id)
);

CREATE TABLE IF NOT EXISTS tbl_admission_schedule_change_log (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    client_ip VARCHAR(255) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    change_type VARCHAR(20) NOT NULL,
    before_schedule TEXT,
    after_schedule TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_schedule_change_log_schedule
        FOREIGN KEY (schedule_id) REFERENCES tbl_admission_schedule (schedule_id),
    CONSTRAINT fk_schedule_change_log_user
        FOREIGN KEY (user_id) REFERENCES tbl_user (user_id)
);
