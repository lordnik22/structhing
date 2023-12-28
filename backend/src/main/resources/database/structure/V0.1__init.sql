CREATE TABLE IF NOT EXISTS public.struct_watch_path (
    id UUID,
    create_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    directory_path CHARACTER VARYING
);

CREATE TABLE IF NOT EXISTS public.struct_watch_file (
    id UUID default random_uuid() PRIMARY KEY,
    create_timestamp TIMESTAMP GENERATED ALWAYS AS CURRENT_TIMESTAMP(),
    old_file_path CHARACTER VARYING,
    current_file_path CHARACTER VARYING
);