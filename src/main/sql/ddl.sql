drop table if exists Members cascade;
create table Members (
    id serial primary key,
    name text,
    display_name varchar(255) not null,
    birthday date,
    country varchar(3)
);

drop table if exists Bands cascade ;
create table Bands (
    id serial primary key,
    name varchar(255) not null ,
    description text
);

drop table if exists Labels cascade ;
create table Labels (
    id serial primary key,
    name varchar(255) not null,
    description text,
    address varchar(255),
    contact_information text
);

drop table if exists Albums cascade ;
drop type if exists album_type;
create type album_type as enum ('album', 'single', 'mini-album');
create table Albums (
    id serial primary key,
    band_id integer references Bands(id) not null,
    label_id integer references Labels(id) on delete set null ,
    title varchar(255) not null,
    release_date date not null,
    type album_type default 'album' not null
);

drop table if exists Tracks cascade ;
create table Tracks (
    id serial primary key,
    title varchar(255) not null,
    duration interval not null
);

drop table if exists BandMembers cascade;
create table BandMembers (
    id serial primary key,
    member_id integer references Members(id) on delete cascade not null,
    band_id integer references Bands(id) on delete cascade not null ,
    role varchar(100),
    start_date date not null,
    end_date date not null default '3000-01-01'
);

drop table if exists TrackAuthors cascade;
drop type if exists band_role;
create type band_role as enum ('main', 'feat', 'remix');
create table TrackAuthors (
    track_id integer references Tracks(id) on delete cascade not null,
    band_id integer references Bands(id) not null,
    role band_role default 'main',
    primary key (track_id, band_id)
);

drop table if exists TrackInAlbums cascade;
create table TrackInAlbums (
    track_id integer references Tracks(id) on delete cascade not null ,
    album_id integer references Albums(id) on delete cascade not null ,
    primary key (track_id, album_id)
);

drop table if exists LabelContracts cascade;
create table LabelContracts (
    id serial primary key ,
    band_id integer references Bands(id) on delete cascade not null ,
    label_id integer references Labels(id) on delete cascade not null ,
    start_date date not null,
    end_date date not null default '3000-01-01'
);
