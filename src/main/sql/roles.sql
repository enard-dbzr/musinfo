REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM guest, subscriber, manager;
DROP ROLE guest, subscriber, manager;

CREATE ROLE guest;
CREATE ROLE subscriber;
CREATE ROLE manager;

GRANT SELECT ON bands, albums, tracks, labels, trackauthors, trackinalbums TO guest;

GRANT SELECT ON bands, albums, tracks, members, labels, trackauthors, trackinalbums, bandmembers, labelcontracts TO subscriber;

GRANT SELECT, INSERT, UPDATE, DELETE ON bands, albums, tracks, members, labels, trackauthors, trackinalbums, bandmembers, labelcontracts TO manager;





