--
-- Context Store Table
--
CREATE TABLE maquette.maquette__users (
    job VARCHAR(128) PRIMARY KEY,
    inserted TIMESTAMP NOT NULL,
    type VARCHAR(256) NOT NULL,
    value JSONB NOT NULL
);