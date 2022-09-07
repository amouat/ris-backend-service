CREATE TABLE
  IF NOT EXISTS doc_unit (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    uuid uuid NOT NULL UNIQUE,
    documentnumber VARCHAR(14) NOT NULL UNIQUE,
    creationtimestamp TIMESTAMP
    WITH
      TIME ZONE NOT NULL,
      fileuploadtimestamp TIMESTAMP
    WITH
      TIME ZONE,
      s3path VARCHAR(255),
      filetype VARCHAR(30),
      filename VARCHAR(255),
      aktenzeichen VARCHAR(255),
      gerichtstyp VARCHAR(255),
      dokumenttyp VARCHAR(255),
      vorgang VARCHAR(255),
      ecli VARCHAR(255),
      spruchkoerper VARCHAR(255),
      entscheidungsdatum VARCHAR(255),
      gerichtssitz VARCHAR(255),
      rechtskraft VARCHAR(255),
      eingangsart VARCHAR(255),
      dokumentationsstelle VARCHAR(255),
      region VARCHAR(255),
      entscheidungsname TEXT,
      titelzeile TEXT,
      leitsatz TEXT,
      orientierungssatz TEXT,
      tenor TEXT,
      gruende TEXT,
      tatbestand TEXT,
      entscheidungsgruende TEXT
  );

CREATE TABLE
  IF NOT EXISTS document_number_counter (
    id SERIAL NOT NULL PRIMARY KEY,
    nextnumber INT NOT NULL,
    currentyear INT NOT NULL
  );

CREATE TABLE
  IF NOT EXISTS xml_mail (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    document_unit_id BIGINT NOT NULL,
    mail_subject VARCHAR(256) NOT NULL,
    xml TEXT,
    status_code VARCHAR(20),
    status_messages TEXT,
    file_name VARCHAR(50) NOT NULL,
    publish_date TIMESTAMP NOT NULL
  );

ALTER TABLE
  xml_mail
ADD
  COLUMN IF NOT EXISTS receiver_address VARCHAR(256);
