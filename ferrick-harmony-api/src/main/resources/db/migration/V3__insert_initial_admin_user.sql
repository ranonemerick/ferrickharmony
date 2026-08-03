INSERT INTO users (id, email, password, role, created_at, active)
VALUES (
        gen_random_uuid(),
        'admin@admin.com',
        '$2a$12$G9nQj1A4dGcNWVAZTgEWreEouagdYo.KICWcfJDLVEvhaT7C9i8p6',
        'ADMIN',
        CURRENT_TIMESTAMP,
        TRUE
       );