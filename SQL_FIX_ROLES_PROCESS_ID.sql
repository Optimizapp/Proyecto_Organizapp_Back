-- ============================================================================
-- SQL FIX: Permitir NULL en roles.process_id
-- ============================================================================
-- Database: grupo14 (PostgreSQL)
-- Purpose: Permitir roles de empresa con processId = null
-- Severity: CRÍTICA
-- Status: REQUERIDO ANTES DE DEPLOYAR CÓDIGO
-- ============================================================================

-- Verificar estado actual
SELECT 
    column_name, 
    is_nullable, 
    data_type, 
    column_default
FROM information_schema.columns 
WHERE table_name = 'roles' AND column_name = 'process_id';

-- ANTES: is_nullable = NO (problemático)
-- DESPUÉS: is_nullable = YES (correcto)

-- ============================================================================
-- PASO 1: Ejecutar el fix principal
-- ============================================================================
ALTER TABLE roles ALTER COLUMN process_id DROP NOT NULL;

-- ============================================================================
-- PASO 2: Verificar que el cambio se aplicó
-- ============================================================================
\d roles

-- Debe mostrar:
-- process_id | integer | 
-- (sin constraint NOT NULL)

-- ============================================================================
-- PASO 3: Validar con SELECT
-- ============================================================================
SELECT * FROM information_schema.columns 
WHERE table_name = 'roles' AND column_name = 'process_id';

-- is_nullable debe ser 'YES'

-- ============================================================================
-- PASO 4: Test de funcionalidad (opcional)
-- ============================================================================
-- Crear un rol de empresa con process_id = NULL
INSERT INTO roles (nombre, company_id, process_id, descripcion)
VALUES ('Test Role Empresa', 1, NULL, 'Rol de prueba con process_id null');

-- Verificar que se insertó
SELECT id, nombre, company_id, process_id FROM roles 
WHERE nombre = 'Test Role Empresa';

-- Limpiar (opcional)
DELETE FROM roles WHERE nombre = 'Test Role Empresa';

-- ============================================================================
-- PASO 5: Verificar constraint UNIQUE (sigue funcionando con NULL)
-- ============================================================================
-- PostgreSQL permite múltiples NULLs en UNIQUE (cada NULL es único)
-- Esto es correcto para nuestra regla de negocio:
-- - Rol de empresa 1: "Admin" con company_id=1, process_id=NULL
-- - Rol de empresa 2: "Admin" con company_id=2, process_id=NULL (PERMITIDO)
-- - Rol de proceso: "Editor" con company_id=1, process_id=5

SELECT constraint_name, constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'roles';

-- Debe mostrar:
-- uk_roles_company_process_nombre | UNIQUE

-- ============================================================================
-- CONFIRMACIÓN
-- ============================================================================
-- ✅ Si los pasos 3 y 4 funcionan sin error, el fix está aplicado
-- ✅ El backend ahora puede crear roles con processId = null
-- ✅ La regla de negocio de roles de empresa es soportada

-- ============================================================================
-- ROLLBACK (en caso de emergencia)
-- ============================================================================
-- ALTER TABLE roles ALTER COLUMN process_id SET NOT NULL;
-- NOTA: Esto solo funcionará si no hay NULLs en la columna

-- ============================================================================
