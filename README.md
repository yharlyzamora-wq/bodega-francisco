# Bodega Francisco - Sistema web de pedidos e inventario

Proyecto APF3 (UTP). Java 21 · Spring Boot · Thymeleaf · H2 · Maven.

## Arquitectura
Vista (Thymeleaf) ↔ Controller ↔ Service ↔ DAO (interfaz + JDBC) ↔ H2.

## Requisitos
JDK 21 o superior y Maven 3.9+. No requiere instalar ninguna base de datos.

## Configuración
Ejecuta `.\configurar.ps1` una vez: crea `application-local.properties` (excluido de Git) con la cuenta del administrador.
La base se guarda en la carpeta `data/` y se crea sola al arrancar.

## Ejecutar
```bash
mvn spring-boot:run
```
Abrir http://localhost:8080 (catálogo) y http://localhost:8080/admin (panel).

## Pruebas
```bash
mvn test
```

## Librerías de apoyo
Guava (control de intentos de login), Apache POI (Excel), Apache Commons Lang (validaciones), Logback (logs).
