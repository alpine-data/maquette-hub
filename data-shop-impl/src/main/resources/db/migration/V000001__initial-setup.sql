-- ==================================================
-- Author: Michael Wellner <michaelwellner@kpmg.com>
-- Create date: 2020-12-03
-- Description: Data Shop Metadata Tables.
-- ==================================================

--
-- create schema
--
CREATE SCHEMA IF NOT EXISTS maquette;

--
-- members
--
CREATE TABLE IF NOT EXISTS maquette.mq__members (
    type            TEXT NOT NULL,
    parent          TEXT NOT NULL,
    granted_by      TEXT NOT NULL,
    granted_at      TIMESTAMP NOT NULL,
    role            TEXT NOT NULL,
    auth            TEXT NOT NULL
);

--
-- data assets
--
CREATE TYPE DATA_VISIBILITY AS ENUM ('public', 'private');
CREATE TYPE CLASSIFICATION AS ENUM ('public', 'internal', 'confidential', 'restricted');
CREATE TYPE PERSONAL_INFORMATION AS ENUM ('none', 'pi', 'spi');
CREATE TYPE DATA_ZONE AS ENUM ('raw', 'prepared', 'gold');
CREATE TYPE DATA_ASSET_STATE AS ENUM ('review-required', 'approved', 'declined');

CREATE TABLE IF NOT EXISTS maquette.mq__data_assets (
    id                      TEXT PRIMARY KEY,
    type                    TEXT NOT NULL,

    title                   TEXT NOT NULL,
    name                    TEXT NOT NULL,
    summary                 TEXT NOT NULL,
    visibility              DATA_VISIBILITY NOT NULL,
    classification          CLASSIFICATION NOT NULL,
    personal_information    PERSONAL_INFORMATION NOT NULL,
    data_zone               DATA_ZONE NOT NULL,

    state                   DATA_ASSET_STATE NOT NULL,
    created_by              TEXT NOT NULL,
    created_at              TIMESTAMP NOT NULL,
    updated_by              TEXT NOT NULL,
    updated_at              TIMESTAMP NOT NULL

    custom_properties       TEXT,
    custom_settings         TEXT
);

CREATE TABLE IF NOT EXISTS maquette.mq__data_access_requests (
    id          TEXT,
    asset       TEXT,
    workspace   TEXT NOT NULL,
    request     TEXT NOT NULL,

    PRIMARY KEY(id, asset_id));
