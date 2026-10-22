-- PostgreSQL. 애플리케이션 배포 전에 실행한다. 기존 일정/원서/점수는 변경하지 않는다.
-- 정책 미등록 일정은 코드의 기존 기본값을 읽기만 한다. 첫 수정/선발 시 행을 생성한다.
BEGIN;

CREATE TABLE IF NOT EXISTS tbl_admission_policy (
    schedule_id BIGINT PRIMARY KEY REFERENCES tbl_admission_schedule(id),
    version BIGINT NOT NULL DEFAULT 0,
    selection_json TEXT NOT NULL,
    ticket_json TEXT NOT NULL,
    selection_locked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS tbl_admission_policy_change_log (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL REFERENCES tbl_admission_schedule(id),
    user_id BIGINT NOT NULL,
    before_policy TEXT NOT NULL,
    after_policy TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_admission_policy_history
    ON tbl_admission_policy_change_log(schedule_id, id DESC);

COMMIT;
