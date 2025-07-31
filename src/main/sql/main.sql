create database main_email_;
use main_email_;

create table users (
    id Integer auto_increment primary key ,
    name nvarchar(100) not null ,
    email nvarchar(100) not null unique ,
    password nvarchar(100) not null
);

create table emails (
    id Integer auto_increment primary key,
    sender_id integer not null ,
    code nvarchar(6) not null ,
    subject nvarchar(250) not null ,
    body text,
    sent_date date not null default (current_date),
    sent_time time not null default (current_time),

    foreign key (sender_id) references users(id) on delete cascade
);

create table email_recipients (
    email_id integer not null ,
    recipient_id integer not null ,
    is_read boolean default false,
    primary key (email_id, recipient_id),
    foreign key (email_id) references emails(id) on delete cascade ,
    foreign key (recipient_id) references users(id) on delete cascade
);


