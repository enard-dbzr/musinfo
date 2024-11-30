

-- Триггер на добавление трека в альбом
CREATE OR REPLACE FUNCTION check_track_album_author()
RETURNS TRIGGER AS $$
DECLARE
    album_band_id INTEGER;
    track_author_exists BOOLEAN;
BEGIN
    SELECT band_id INTO album_band_id FROM Albums WHERE id = NEW.album_id;


    SELECT EXISTS (
        SELECT 1
        FROM TrackAuthors
        WHERE track_id = NEW.track_id AND band_id = album_band_id
    ) INTO track_author_exists;

    IF NOT track_author_exists THEN
        RAISE EXCEPTION 'Track % does not have an author from the album`s band.', NEW.track_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER validate_track_album_author
BEFORE INSERT ON TrackInAlbums
FOR EACH ROW EXECUTE FUNCTION check_track_album_author();


--Триггер на предотвращение удаления автора трека
CREATE OR REPLACE FUNCTION prevent_author_removal()
RETURNS TRIGGER AS $$
DECLARE
    album_exists BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM TrackInAlbums AS tia
        JOIN Albums AS a ON tia.album_id = a.id
        WHERE tia.track_id = OLD.track_id AND a.band_id = OLD.band_id
    ) INTO album_exists;

    IF album_exists THEN
        RAISE EXCEPTION 'Cannot delete author % for track %: track is part of an album by this band.', OLD.band_id, OLD.track_id;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER validate_author_deletion
AFTER DELETE ON TrackAuthors
FOR EACH ROW
EXECUTE FUNCTION prevent_author_removal();


--Триггер на предотвращение изменения автора группы
CREATE OR REPLACE FUNCTION prevent_album_band_change()
RETURNS TRIGGER AS $$
DECLARE
    unmatched_tracks BOOLEAN;
BEGIN
    -- Проверяем, что нет треков в альбоме без авторов из новой группы
    SELECT EXISTS (
        SELECT 1
        FROM TrackInAlbums AS tia
        LEFT JOIN TrackAuthors AS ta ON tia.track_id = ta.track_id AND ta.band_id = NEW.band_id
        WHERE tia.album_id = OLD.id AND ta.track_id IS NULL
    ) INTO unmatched_tracks;

    -- Если такие треки есть, не даем изменить band_id
    IF unmatched_tracks THEN
        RAISE EXCEPTION 'Cannot change band_id for album %: not all tracks have authors from the new band.', OLD.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER validate_band_change
BEFORE UPDATE OF band_id ON Albums
FOR EACH ROW
EXECUTE FUNCTION prevent_album_band_change();


--Триггер на удаление TrackInAlbums
CREATE OR REPLACE FUNCTION delete_orphan_tracks()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM Tracks
    WHERE id = OLD.track_id
      AND NOT EXISTS (
          SELECT 1 FROM TrackInAlbums WHERE track_id = OLD.track_id
      );

    DELETE FROM Albums
    WHERE id = OLD.album_id
      AND NOT EXISTS (
          SELECT 1 FROM TrackInAlbums WHERE album_id = OLD.album_id
      );
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER delete_orphan_tracks_trigger
AFTER DELETE ON TrackInAlbums
FOR EACH ROW
EXECUTE FUNCTION delete_orphan_tracks();
