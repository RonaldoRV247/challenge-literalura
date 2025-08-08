# Literalura Challenge

## Descripción
Aplicación de consola desarrollada con Spring Boot que permite gestionar un catálogo de libros mediante la API de Gutendx. Los usuarios pueden buscar libros, guardarlos en una base de datos PostgreSQL y realizar diversas consultas.

## Características
- 🔍 Búsqueda de libros por título usando la API de Gutendx
- 📚 Almacenamiento de libros y autores en base de datos PostgreSQL
- 📋 Listado de libros y autores registrados
- 🌐 Filtrado de libros por idioma
- 👥 Consulta de autores vivos en un año específico

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Jackson (para manejo de JSON)
- API de Gutendx

## Configuración Previa
1. Instalar PostgreSQL
2. Crear una base de datos llamada `literalura`
3. Configurar usuario y contraseña en `application.properties`

## Estructura del Proyecto
```
src/main/java/com/example/literalura_challenge/
├── model/                 # Entidades y DTOs
│   ├── Autor.java
│   ├── Libro.java
│   ├── DatosAutor.java
│   ├── DatosLibro.java
│   └── DatosRespuesta.java
├── repository/            # Repositorios JPA
│   ├── AutorRepository.java
│   └── LibroRepository.java
├── service/              # Servicios para API y conversión
│   ├── ConsumoAPI.java
│   ├── ConvierteDatos.java
│   └── IConvierteDatos.java
├── principal/            # Lógica principal y menú
│   └── Principal.java
└── LiteraluraChallengeApplication.java
```

## Funcionalidades del Menú
1. **Buscar libro por título**: Busca un libro en la API de Gutendx y lo guarda en la base de datos
2. **Listar libros registrados**: Muestra todos los libros guardados
3. **Listar autores registrados**: Muestra todos los autores guardados
4. **Listar autores vivos en un año**: Filtra autores que estaban vivos en un año específico
5. **Listar libros por idioma**: Filtra libros por idioma (es, en, fr, pt)

## Uso
1. Ejecutar la aplicación con Maven: `mvn spring-boot:run`
2. Seguir las opciones del menú interactivo
3. Ingresar datos según se solicite

## API Utilizada
- **Gutendx API**: https://gutendx.com/books/
- Catálogo de más de 70,000 libros del Project Gutenberg
