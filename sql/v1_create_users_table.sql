CREATE TABLE IF NOT EXISTS factory_users.users
(
    id uuid NOT NULL,
    user_name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    enabled boolean NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_user_name_key UNIQUE (user_name)
);