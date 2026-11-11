-- Flyway: disableTransaction=true
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_projects_updated_at_desc_active
ON projects (updated_at DESC)
WHERE deleted_at IS NULL;