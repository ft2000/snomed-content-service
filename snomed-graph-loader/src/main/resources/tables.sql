-- Table: snomed.relationship

-- DROP TABLE snomed.relationship;

CREATE TABLE snomed.relationship
(
  id character varying NOT NULL,
  "effectiveTime" character varying NOT NULL,
  active "char" NOT NULL,
  moduleid character varying NOT NULL,
  sourceid character varying NOT NULL,
  destinationid character varying NOT NULL,
  relationshipgroup character varying NOT NULL,
  typeid character varying NOT NULL,
  characteristictypeid character varying NOT NULL,
  modifierid character varying NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE snomed.relationship
  OWNER TO snomed;

-- Index: snomed.relationship_id

-- DROP INDEX snomed.relationship_id;

CREATE INDEX relationship_id
  ON snomed.relationship
  USING btree
  (id COLLATE pg_catalog."default");

-- Table: snomed.description

-- DROP TABLE snomed.description;

CREATE TABLE snomed.description
(
  id character varying NOT NULL,
  "effectiveTime" character varying NOT NULL,
  active "char" NOT NULL,
  moduleid character varying NOT NULL,
  conceptid character varying NOT NULL,
  languagecode character varying NOT NULL,
  term text,
  casesignificanceid character varying NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE snomed.description
  OWNER TO snomed;

-- Index: snomed.description_id

-- DROP INDEX snomed.description_id;

CREATE INDEX description_id
  ON snomed.description
  USING btree
  (id COLLATE pg_catalog."default");

-- Table: snomed.concept

-- DROP TABLE snomed.concept;

CREATE TABLE snomed.concept
(
  id character varying NOT NULL,
  "effectiveTime" character varying NOT NULL,
  active "char" NOT NULL,
  moduleid character varying NOT NULL,
  definitionstatusid character varying NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE snomed.concept
  OWNER TO snomed;

-- Index: snomed.concept_id

-- DROP INDEX snomed.concept_id;

CREATE INDEX concept_id
  ON snomed.concept
  USING btree
  (id COLLATE pg_catalog."default");

-- Table: snomed.audit

-- DROP TABLE snomed.audit;

CREATE TABLE snomed.audit
(
  id character varying NOT NULL,
  created timestamp without time zone DEFAULT now(),
  status character varying NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE snomed.audit
  OWNER TO snomed;

-- Index: snomed.id_and_status

-- DROP INDEX snomed.id_and_status;

CREATE INDEX id_and_status
  ON snomed.audit
  USING btree
  (id COLLATE pg_catalog."default", status COLLATE pg_catalog."default");

