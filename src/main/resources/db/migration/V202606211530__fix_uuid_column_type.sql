ALTER TABLE IF EXISTS public.users ALTER COLUMN uuid SET DATA TYPE uuid USING uuid::uuid;
