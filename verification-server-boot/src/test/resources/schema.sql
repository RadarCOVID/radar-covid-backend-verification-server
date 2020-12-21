DROP SCHEMA IF EXISTS VERIFICATION CASCADE;
CREATE SCHEMA VERIFICATION;

-- CCAA

CREATE TABLE VERIFICATION.CCAA
(
    DE_CCAA_ID         CHAR (2),
    DE_CCAA_NAME       CHAR VARYING(128),
    DE_CCAA_PUBLIC_KEY CHAR VARYING(1024),
    DE_CCAA_ISSUER     CHAR VARYING(128),
    CONSTRAINT PK_CCAA
        PRIMARY KEY (DE_CCAA_ID)
);

-- CODE

CREATE SEQUENCE VERIFICATION.SQ_NM_ID_CODE;

CREATE TABLE VERIFICATION.CODE
(
    NM_ID_CODE            INTEGER DEFAULT nextval('VERIFICATION.SQ_NM_ID_CODE'),
    NM_VERSION            INTEGER DEFAULT 0,
    FC_CREATION_DATE      TIMESTAMP DEFAULT now(),
    DE_CCAA_ID            CHAR (2),
    IN_CCAA_CREATION      BOOLEAN DEFAULT TRUE,
    FC_CODE_VALID_FROM    DATE DEFAULT now(),
    FC_CODE_VALID_UNTIL   DATE DEFAULT '9999-12-31',
    DE_CODE_HASH          CHAR VARYING(64),
    IN_CODE_REDEEMED      BOOLEAN DEFAULT FALSE,
    FC_CODE_REDEEMED_DATE TIMESTAMP,
    CONSTRAINT PK_CODE
        PRIMARY KEY (NM_ID_CODE, IN_CODE_REDEEMED),
    CONSTRAINT UNQ_CODE_HASH
        UNIQUE (DE_CODE_HASH, IN_CODE_REDEEMED),
    CONSTRAINT FK_CODE_CCAA
        FOREIGN KEY (DE_CCAA_ID)
            REFERENCES VERIFICATION.CCAA (DE_CCAA_ID)
) PARTITION BY LIST (IN_CODE_REDEEMED);

ALTER SEQUENCE VERIFICATION.SQ_NM_ID_CODE
    OWNED BY VERIFICATION.CODE.NM_ID_CODE;

CREATE INDEX IN_VERIFICATION_CODE_HASH
    ON VERIFICATION.CODE(DE_CODE_HASH);

CREATE INDEX IN_VERIFICATION_CODE_CREATION_DATE
    ON VERIFICATION.CODE(FC_CREATION_DATE);

CREATE INDEX IN_VERIFICATION_CODE_HASH_REDEEMED
    ON VERIFICATION.CODE(DE_CODE_HASH, IN_CODE_REDEEMED);

CREATE TABLE VERIFICATION.CODE_REDEEMED
    PARTITION OF VERIFICATION.CODE FOR VALUES IN (TRUE);

CREATE TABLE VERIFICATION.CODE_NO_REDEEMED
    PARTITION OF VERIFICATION.CODE FOR VALUES IN (FALSE);

-- CCAA_KPI

CREATE SEQUENCE VERIFICATION.SQ_NM_ID_CCAA_KPI;

CREATE TABLE VERIFICATION.CCAA_KPI
(
    NM_ID_CCAA_KPI   INTEGER DEFAULT nextval('VERIFICATION.SQ_NM_ID_CCAA_KPI'),
    NM_VERSION       INTEGER DEFAULT 0,
    FC_CREATION_DATE TIMESTAMP DEFAULT now() NOT NULL,
    FC_UPDATE_DATE   TIMESTAMP DEFAULT NULL,
    DE_CCAA_ID       CHAR (2) NOT NULL,
    IN_CCAA_CREATION BOOLEAN DEFAULT TRUE NOT NULL,
    FC_KPI_DATE      DATE NOT NULL,
    DE_KPI_TYPE      CHAR VARYING (25) NOT NULL,
    NM_KPI_VALUE     INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT PK_CCAA_KPI
        PRIMARY KEY (NM_ID_CCAA_KPI),
    CONSTRAINT UNQ_CCAA_KPI_CCAA_DATE_TYPE
        UNIQUE (DE_CCAA_ID, FC_KPI_DATE, DE_KPI_TYPE),
    CONSTRAINT FK_CCAA_KPI_CCAA
        FOREIGN KEY (DE_CCAA_ID)
            REFERENCES VERIFICATION.CCAA (DE_CCAA_ID)
);

ALTER SEQUENCE VERIFICATION.SQ_NM_ID_CCAA_KPI
    OWNED BY VERIFICATION.CCAA_KPI.NM_ID_CCAA_KPI;

CREATE INDEX IN_VERIFICATION_CCAA_KPI_DATE_KPI
    ON VERIFICATION.CCAA_KPI(FC_KPI_DATE, DE_KPI_TYPE);

CREATE INDEX IN_VERIFICATION_CCAA_KPI_CCAA_TYPE
    ON VERIFICATION.CCAA_KPI(DE_CCAA_ID, DE_KPI_TYPE);

-- CCAA_AUTH

CREATE SEQUENCE VERIFICATION.SQ_NM_ID_CCAA_AUTH;

CREATE TABLE VERIFICATION.CCAA_AUTH
(
    NM_ID_CCAA_AUTH  INTEGER DEFAULT nextval('VERIFICATION.SQ_NM_ID_CCAA_AUTH'),
    NM_VERSION       INTEGER DEFAULT 0,
    FC_CREATION_DATE TIMESTAMP DEFAULT now() NOT NULL,
    DE_CCAA_ID       CHAR (2) NOT NULL,
    DE_AUTH          CHAR VARYING (25) NOT NULL,
    CONSTRAINT PK_CCAA_AUTH
        PRIMARY KEY (NM_ID_CCAA_AUTH),
    CONSTRAINT UNQ_CCAA_AUTH
        UNIQUE (DE_CCAA_ID, DE_AUTH),
    CONSTRAINT FK_CCAA_AUTH_CCAA
        FOREIGN KEY (DE_CCAA_ID)
            REFERENCES VERIFICATION.CCAA (DE_CCAA_ID)
);

ALTER SEQUENCE VERIFICATION.SQ_NM_ID_CCAA_AUTH
    OWNED BY VERIFICATION.CCAA_AUTH.NM_ID_CCAA_AUTH;

CREATE INDEX IN_VERIFICATION_CCAA_AUTH_CCAA
    ON VERIFICATION.CCAA_AUTH (DE_CCAA_ID);
