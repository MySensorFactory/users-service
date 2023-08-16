CREATE TABLE IF NOT EXISTS factory_users.roles
(
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT roles_name_key UNIQUE (name)
);