-- bands_id_seq, albums_id_seq, tracks_id_seq, members_id_seq, labels_id_seq, bandmembers_id_seq, labelcontracts_id_seq
SELECT setval('bands_id_seq', (SELECT MAX(id) FROM bands));
SELECT setval('albums_id_seq', (SELECT MAX(id) FROM albums));
SELECT setval('tracks_id_seq', (SELECT MAX(id) FROM tracks));
SELECT setval('members_id_seq', (SELECT MAX(id) FROM members));
SELECT setval('labels_id_seq', (SELECT MAX(id) FROM labels));
SELECT setval('bandmembers_id_seq', (SELECT MAX(id) FROM bandmembers));
SELECT setval('labelcontracts_id_seq', (SELECT MAX(id) FROM labelcontracts));