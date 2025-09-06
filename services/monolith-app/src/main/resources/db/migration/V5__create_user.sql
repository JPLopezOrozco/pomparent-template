create table if not exists users (
    id        bigserial primary key,
    name      varchar(50)  not null,
    email     varchar(255) not null unique,
    password  varchar(72)  not null,
    role      varchar(20)  not null
    );

create index if not exists idx_users_email on users(email);