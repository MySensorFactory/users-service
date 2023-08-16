CREATE TABLE IF NOT EXISTS factory_users.user_roles
(
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_users FOREIGN KEY (user_id)
        REFERENCES factory_users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user_roles_roles FOREIGN KEY (role_id)
        REFERENCES factory_users.roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON factory_users.user_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON factory_users.user_roles (role_id);