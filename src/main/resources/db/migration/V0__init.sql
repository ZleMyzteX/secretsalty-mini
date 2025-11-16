create table if not exists users
(
    user_id  varchar     not null primary key,
    username varchar(50) not null unique,
    wishes   text,
    first_seen_at timestamp not null default current_timestamp
);

create table if not exists exemptions
(
    user_id          varchar not null references users (user_id) on delete cascade,
    excluded_user_id varchar not null references users (user_id) on delete cascade,
    primary key (user_id, excluded_user_id),
    constraint chk_exemption_no_self check (user_id != excluded_user_id)
);

create table if not exists assignments
(
    giver_id    varchar   not null primary key references users (user_id) on delete cascade,
    receiver_id varchar   not null unique references users (user_id) on delete cascade,
    created_at  timestamp not null default current_timestamp,
    constraint chk_assignment_no_self check (giver_id != receiver_id)
);

create index if not exists idx_exemptions_user on exemptions (user_id);
create index if not exists idx_assignments_receiver on assignments (receiver_id);