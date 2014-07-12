--Create a postgres login role
--DROP ROLE refset;
CREATE ROLE refset LOGIN
ENCRYPTED PASSWORD 'refset'
NOSUPERUSER INHERIT CREATEDB NOCREATEROLE NOREPLICATION;
--create database
-- DROP DATABASE snomed;
CREATE DATABASE snomed
WITH OWNER = refset
ENCODING = 'UTF8' 
TABLESPACE = pg_default
LC_COLLATE = 'en_US.UTF-8'
LC_CTYPE = 'en_US.UTF-8'
CONNECTION LIMIT = -1;
COMMENT ON DATABASE snomed IS 'snomed db';
																																																																																	        CONNECTION LIMIT = -1;

																																																																																	 COMMENT ON DATABASE snomed IS 'snomed';
