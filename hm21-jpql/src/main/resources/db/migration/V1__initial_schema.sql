create sequence client_seq start with 1 increment by 1;
create sequence address_seq start with 1 increment by 1;
create sequence phone_seq start with 1 increment by 1;

create table address (
                         id bigint not null primary key,
                         street varchar(255)
);

create table client (
                        id bigint not null primary key,
                        name varchar(255),
                        address_id bigint,
                        constraint fk_client_address foreign key (address_id) references address(id)
);

create table phone (
                       id bigint not null primary key,
                       number varchar(255),
                       client_id bigint,
                       constraint fk_phone_client foreign key (client_id) references client(id)
);