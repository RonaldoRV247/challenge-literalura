# Literalura Challenge

## Descripción
Aplicación de consola desarrollada con Spring Boot que permite gestionar un catálogo personal de libros mediante la API de Gutendx. Los usuarios pueden buscar libros, guardarlos en su biblioteca personal (base de datos PostgreSQL) y realizar diversas consultas de manera intuitiva.

## Características
- 🔍 Búsqueda de libros por título usando la API de Gutendx
- 📚 Biblioteca personal con almacenamiento en base de datos PostgreSQL
- 📋 Listado de libros y autores en su biblioteca
- 🌐 Filtrado de libros por idioma
- 👥 Consulta de autores vivos en un año específico
- 📊 Estadísticas de su biblioteca por idioma
- 🏆 Ranking de libros más populares (por descargas)
- 🎯 Interfaz amigable para usuarios no técnicos

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Jackson 2.16.1 (para manejo de JSON)
- HttpClient (para consumo de API REST)
- API de Gutendex (catálogo de Project Gutenberg)

## Configuración Previa
1. **Instalar PostgreSQL**
   - Descargar desde https://www.postgresql.org/download/
   - Instalar con configuración predeterminada

2. **Crear base de datos**
   ```sql
   CREATE DATABASE literalura;
   ```

3. **Configurar credenciales**
   - Editar `src/main/resources/application.properties`
   - Ajustar usuario y contraseña de PostgreSQL según su instalación

## Estructura del Proyecto
```
src/main/java/com/example/literalura_challenge/
├── model/                 # Entidades y DTOs
│   ├── Autor.java        # Entidad JPA para autores
│   ├── Libro.java        # Entidad JPA para libros
│   ├── DatosAutor.java   # DTO para mapear JSON de autores
│   ├── DatosLibro.java   # DTO para mapear JSON de libros
│   └── DatosRespuesta.java # DTO para respuesta completa de API
├── repository/            # Repositorios JPA
│   ├── AutorRepository.java    # Consultas personalizadas de autores
│   └── LibroRepository.java    # Consultas personalizadas de libros
├── service/              # Servicios para API y conversión
│   ├── ConsumoAPI.java         # Cliente HTTP para Gutendx
│   ├── ConvierteDatos.java     # Conversión JSON a objetos Java
│   └── IConvierteDatos.java    # Interfaz de conversión
├── principal/            # Lógica principal y menú
│   └── Principal.java          # Menú interactivo y funcionalidades
└── LiteraluraChallengeApplication.java # Clase principal Spring Boot
```

## Funcionalidades del Menú

### 1. Buscar libro por título
- Conecta con la API de Gutendx
- Permite buscar libros por título
- Guarda automáticamente en su biblioteca personal
- Evita duplicados

### 2. Listar libros registrados
- Muestra todos los libros en su biblioteca
- Información completa: título, autor, idioma, popularidad

### 3. Listar autores registrados
- Muestra todos los autores de su biblioteca
- Incluye fechas de nacimiento y fallecimiento

### 4. Autores vivos en un año específico
- Consulta que autores estaban vivos en un año determinado
- Útil para investigación histórica y literaria

### 5. Listar libros por idioma
- Filtra libros por código de idioma (es, en, fr, pt, etc.)
- Muestra idiomas disponibles en su biblioteca

### 6. Estadísticas por idioma
- Resumen cuantitativo de libros por idioma
- Total general de la biblioteca

### 7. Top 10 libros más populares
- Ranking basado en número de descargas de Project Gutenberg
- Muestra los libros más leídos históricamente

## Instalación y Ejecución

### Prerrequisitos
- Java 17 o superior
- PostgreSQL instalado y ejecutándose
- Conexión a Internet (para consultar API de Gutendx)

### Pasos para ejecutar
1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/RonaldoRV247/challenge-literalura.git
   cd challenge-literalura
   ```

2. **Configurar base de datos**
   - Crear base de datos `literalura` en PostgreSQL
   - Verificar credenciales en `application.properties`

3. **Ejecutar aplicación**
   ```bash
   # Usando Maven wrapper (recomendado)
   ./mvnw.cmd spring-boot:run
   
   # O si tiene Maven instalado
   mvn spring-boot:run
   ```

4. **Usar la aplicación**
   - Seguir el menú interactivo
   - Comenzar buscando libros (opción 1)
   - Explorar su biblioteca con las demás opciones

## Ejemplos de Uso

### Búsquedas recomendadas
- "Pride" → Pride and Prejudice
- "Alice" → Alice's Adventures in Wonderland  
- "Don Quixote" → Don Quixote
- "Romeo" → Romeo and Juliet
- "Frankenstein" → Frankenstein

### Años interesantes para consultar autores
- 1800 → Autores del Romanticismo
- 1900 → Autores de finales del siglo XIX
- 1950 → Autores del siglo XX

## API Utilizada
- **Gutendx API**: https://gutendex.com/books/
- Catálogo gratuito de más de 70,000 libros del Project Gutenberg
- No requiere autenticación
- Formato de respuesta: JSON

## Características Técnicas

### Arquitectura
- **Patrón MVC**: Separación clara de responsabilidades
- **Inyección de dependencias**: Uso de Spring Framework
- **Repositorio**: Abstracción de acceso a datos con JPA
- **DTOs**: Mapeo eficiente de JSON a objetos Java

### Base de Datos
- **Hibernate**: ORM para manejo de entidades
- **Derived Queries**: Consultas automáticas basadas en nombres de métodos
- **Relaciones**: Mapeo de relación Many-to-One entre Libro y Autor
- **DDL**: Creación automática de tablas al iniciar

### Manejo de Errores
- Validación de entrada de usuario
- Manejo graceful de errores de red
- Mensajes amigables (sin jerga técnica)
- Recuperación automática de errores menores

## Mejoras Implementadas

### Usabilidad
- **Pausas entre operaciones**: Evita que se pierdan resultados
- **Mensajes claros**: Lenguaje natural sin términos técnicos
- **Validaciones robustas**: Manejo de entradas incorrectas
- **Navegación intuitiva**: Menú numerado fácil de usar

### Rendimiento
- **Timeouts configurados**: Evita esperas indefinidas
- **Lazy Loading**: Carga eficiente de relaciones
- **Índices automáticos**: Hibernate optimiza consultas
- **Conexión persistente**: Reutilización de conexiones HTTP

## Contribuciones
Este proyecto fue desarrollado como parte del Challenge Literalura, implementando:
- ✅ Conexión con API REST externa
- ✅ Persistencia en base de datos relacional
- ✅ Interfaz de consola interactiva
- ✅ Manejo de JSON con Jackson
- ✅ Consultas personalizadas con JPA
- ✅ Arquitectura limpia y mantenible

## Autor
Desarrollado por RonaldoRV247 como parte del programa de formación en desarrollo backend con Spring Boot.

## Licencia
Proyecto educativo - uso libre para aprendizaje.
