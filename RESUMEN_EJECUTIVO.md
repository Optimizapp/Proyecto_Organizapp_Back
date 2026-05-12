# 🎯 RESUMEN EJECUTIVO - Error 500: Crear Roles de Empresa

## ⚡ Diagrama del Problema

```
┌─────────────┐
│   FRONTEND  │
└──────┬──────┘
       │ POST /api/roles
       │ { nombre, companyId, processId: null }
       ▼
┌─────────────────────────────────┐
│   BACKEND (JAVA/SPRING BOOT)    │ ✅ CORRECTO
│ - RoleController                │
│ - RoleServiceImpl                │
│ - Role Entity                   │
│ - Valida regla de negocio       │
└──────┬──────────────────────────┘
       │ roleRepository.save(role)
       │ role.processo = null
       ▼
┌─────────────────────────────────┐
│   HIBERNATE/JPA                 │ ✅ CORRECTO
│ INSERT INTO roles (...)         │
│   process_id = null             │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│   POSTGRESQL DATABASE           │ ❌ PROBLEMA
│                                 │
│ ALTER TABLE roles               │
│   process_id NOT NULL           │ ← CONFLICTO
└─────────────────────────────────┘
       │
       ▼
❌ ERROR 500
"null value in column "process_id" 
violates not-null constraint"
```

---

## 📊 Análisis de Componentes

| Componente | Estado | Evidencia |
|-----------|--------|----------|
| **Frontend** | ✅ OK | Envía `{ nombre, companyId, processId: null }` correctamente |
| **RoleController** | ✅ OK | Recibe POST /api/roles y mapea @Valid @RequestBody |
| **RoleRequestDto** | ✅ OK | `processId` es `private Long` sin `@NotNull` |
| **RoleServiceImpl.create()** | ✅ OK | `resolveProcessForCompany(null)` retorna `null` |
| **RoleServiceImpl.toDto()** | ✅ CORREGIDO | Mapeo manual seguro (no usa ModelMapper) |
| **Role Entity** | ✅ OK | `@ManyToOne` sin `nullable=false` ni `optional=false` |
| **RoleRepository** | ✅ OK | Métodos soportan `existsByCompanyIdAndProcesoIsNullAndNombre()` |
| **Tests** | ✅ PASS | 45/45 tests - Incluye test para `processId = null` |
| **Build** | ✅ OK | `mvn package` SUCCESS |
| **Database Schema** | ❌ FAIL | `process_id NOT NULL` - Bloquea inserciones con null |

---

## 🔧 Solución

### Paso 1: Backend Code (Ya Hecho ✅)
**Rama:** `fix/role-dto-mapping`  
**Commit 1:** edbd464 - Mapeo manual en RoleServiceImpl.toDto()  
**Commit 2:** d25a1fa - Documentación y análisis

**Status:** ✅ Listo para deploy

### Paso 2: Base de Datos (Requerido)
**Archivo:** `SQL_FIX_ROLES_PROCESS_ID.sql`

**Comando a ejecutar en PostgreSQL:**
```sql
ALTER TABLE roles ALTER COLUMN process_id DROP NOT NULL;
```

**Tiempo de ejecución:** < 1 segundo  
**Impacto:** Cambio reversible (ver ROLLBACK en archivo SQL)  
**Risk:** BAJO (no afecta datos existentes)

---

## 📝 Contrato API POST /api/roles

### Request (Correcto)
```json
{
  "nombre": "Jefe de Equipo",
  "companyId": 1,
  "processId": null
}
```

### Response (After Fix)
```json
{
  "id": 100,
  "nombre": "Jefe de Equipo",
  "companyId": 1,
  "processId": null
}
```

### HTTP Status
- ✅ **201 Created** - Después del fix
- ❌ **500 Internal Server Error** - Antes del fix

---

## 🧪 Verificación

### Tests Unitarios
```bash
$ mvn clean test
✅ Tests run: 45, Failures: 0, Errors: 0
```

### Build Maven
```bash
$ mvn package -DskipTests
✅ BUILD SUCCESS
```

### Test Manual (Post-Deploy)
```bash
# Crear rol de empresa
curl -X POST http://grupo14.inphotech.co/api/roles \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","companyId":1,"processId":null}'

# Respuesta: 201 Created ✅
```

---

## 🚀 Plan de Deployment

### Timeline
1. **Ahora:** PR en rama `fix/role-dto-mapping` ✅
2. **Review:** Code review del PR
3. **Merge:** A rama `main`
4. **Build:** `mvn clean package`
5. **Deploy:** Docker image / Kubernetes (k8s/deployment.yaml)
6. **SQL Fix:** Ejecutar en BD de grupo14 (CRÍTICO)
7. **Test:** Crear rol de empresa en frontend

### Comandos de Ejecución

**En la BD de Kubernetes:**
```bash
sudo microk8s kubectl exec -it <postgres-pod> -n grupo14 -- psql -U usuario -d grupo14

# Ejecutar:
ALTER TABLE roles ALTER COLUMN process_id DROP NOT NULL;

# Verificar:
SELECT * FROM information_schema.columns 
WHERE table_name = 'roles' AND column_name = 'process_id';
```

---

## 📋 Checklist de Deployment

- [ ] Mergear PR `fix/role-dto-mapping` a `main`
- [ ] Ejecutar `mvn clean package`
- [ ] Deployar WAR a Kubernetes
- [ ] ⚠️ **CRÍTICO:** Ejecutar SQL fix en PostgreSQL
- [ ] Verificar logs sin errores LazyInitializationException
- [ ] Test POST /api/roles con `processId: null`
- [ ] Test POST /api/roles con `processId: <id>`
- [ ] Verificar que no hay duplicados de rol

---

## 🎓 Lecciones Aprendidas

### Por qué pasó esto
1. **Desajuste ORM-DB:** El ORM no genera automáticamente el constraint correcto
2. **Falta de migración:** Sin Flyway/Liquibase, cambios de BD son manuales
3. **Sin test de integración:** Los tests unitarios no ejecutan en BD real

### Recomendaciones Futuras
1. **Usar Flyway** para versionamiento de migraciones
2. **Agregar @Column(nullable = true)** explícitamente en entidades opcionales
3. **Test de integración** con BD real (TestContainers)
4. **Documentar** regla de negocio: roles de empresa vs roles de proceso

---

## 📞 Archivos Entregados

```
✅ REPORTE_ANALISIS_ROLES_DB.md
   └─ Análisis detallado de 10 secciones
   └─ Matriz de conformidad
   └─ Instrucciones post-deploy

✅ SQL_FIX_ROLES_PROCESS_ID.sql
   └─ Script SQL listo para ejecutar
   └─ Pasos de verificación
   └─ Test de funcionalidad
   └─ Rollback en caso de emergencia

✅ Código Backend
   └─ fix/role-dto-mapping (rama)
   └─ RoleServiceImpl.toDto() corregido
   └─ Tests: 45/45 passing
```

---

## 🎯 Conclusión

**ESTADO:** ✅ **LISTO PARA DEPLOYMENT**

- ✅ Backend correcto y compilable
- ✅ Tests unitarios en verde
- ✅ WAR generado sin errores
- ❌ BD requiere 1 línea SQL (crítico)

**Próximo paso:** Mergear PR y ejecutar SQL fix en la BD de producción.

---

**Analysis by:** Backend Senior  
**Date:** 2026-05-12  
**Severity:** CRÍTICA (bloquea feature)  
**Effort:** 5 minutos (incluye SQL fix)
