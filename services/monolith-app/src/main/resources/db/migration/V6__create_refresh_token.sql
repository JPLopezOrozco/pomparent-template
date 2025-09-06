create table if not exists refresh_tokens (
    id          bigserial primary key,
    user_id     bigint       not null,
    jti         varchar(64)  not null unique,
    issued_at   timestamptz  not null,
    expires_at  timestamptz  not null,
    revoked     boolean      not null default false,
    constraint fk_rt_user foreign key (user_id)
    references users(id) on delete cascade
);

create index if not exists idx_rt_user_valid
    on refresh_tokens(user_id, revoked, expires_at);

create index if not exists idx_rt_expires_at on refresh_tokens(expires_at);