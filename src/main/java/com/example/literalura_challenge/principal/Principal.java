package com.example.literalura_challenge.principal;

import com.example.literalura_challenge.model.*;
import com.example.literalura_challenge.repository.AutorRepository;
import com.example.literalura_challenge.repository.LibroRepository;
import com.example.literalura_challenge.service.ConsumoAPI;
import com.example.literalura_challenge.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/";

    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private AutorRepository autorRepository;

    // Metodo para pausar y esperar input del usuario
    private void esperarEnter() {
        System.out.println("\nPresione Enter para continuar...");
        teclado.nextLine();
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            limpiarPantalla();
            var menu = """
                    =======================================
                    ===    CATALOGO DE LIBROS LITERALURA ===
                    =======================================
                    
                    Elije la opcion a traves de su numero:
                    
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado ano
                    5- Listar libros por idioma
                    6- Mostrar estadisticas de libros por idioma
                    7- Top 10 libros mas descargados
                    
                    0- Salir
                    =======================================
                    """;
            System.out.println(menu);
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = teclado.nextInt();
                teclado.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        buscarLibroWeb();
                        break;
                    case 2:
                        mostrarLibrosRegistrados();
                        break;
                    case 3:
                        mostrarAutoresRegistrados();
                        break;
                    case 4:
                        mostrarAutoresVivosEnAno();
                        break;
                    case 5:
                        mostrarLibrosPorIdioma();
                        break;
                    case 6:
                        mostrarEstadisticasPorIdioma();
                        break;
                    case 7:
                        mostrarTop10LibrosMasDescargados();
                        break;
                    case 0:
                        System.out.println("\nGracias por usar Literalura! Hasta pronto!");
                        break;
                    default:
                        System.out.println("\nX Opcion invalida. Por favor, seleccione un numero del 0 al 7.");
                        esperarEnter();
                }
            } catch (Exception e) {
                System.out.println("\nX Error: Ingrese un numero valido (0-7)");
                teclado.nextLine(); // Limpiar el buffer
                esperarEnter();
            }
        }
    }

    private void limpiarPantalla() {
        // Simular limpieza de pantalla con líneas en blanco
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("\nBUSQUEDA DE LIBROS EN LA API");
        System.out.println("================================");
        System.out.print("Ingrese el titulo del libro que desea buscar: ");
        var nombreLibro = teclado.nextLine();
        
        if (nombreLibro.trim().isEmpty()) {
            System.out.println("\nX Error: Debe ingresar un titulo valido");
            return null;
        }
        
        try {
            System.out.println("\nBuscando '" + nombreLibro + "' en la API de Gutendx...");
            var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
            var datosBusqueda = conversor.obtenerDatos(json, DatosRespuesta.class);
            
            Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                    .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                    .findFirst();
                    
            if (libroBuscado.isPresent()) {
                System.out.println("OK Libro encontrado en la API!");
                return libroBuscado.get();
            } else {
                System.out.println("X Libro no encontrado en la API");
                return null;
            }
        } catch (Exception e) {
            System.out.println("X Error al consultar la API: " + e.getMessage());
            System.out.println("Sugerencias:");
            System.out.println("   - Verifique su conexion a Internet");
            System.out.println("   - Intente con un titulo mas especifico");
            return null;
        }
    }

    private void buscarLibroWeb() {
        DatosLibro datos = getDatosLibro();
        if (datos != null) {
            try {
                // Verificar si el libro ya existe en la base de datos
                Optional<Libro> libroExistente = libroRepository.findByTituloContainsIgnoreCase(datos.titulo());
                if (libroExistente.isPresent()) {
                    System.out.println("\n! El libro ya esta registrado en la base de datos:");
                    System.out.println(libroExistente.get());
                } else {
                    Libro libro = new Libro(datos);
                    libroRepository.save(libro);
                    System.out.println("\nOK Libro guardado exitosamente en la base de datos!");
                    System.out.println(libro);
                }
            } catch (Exception e) {
                System.out.println("\nX Error al guardar el libro: " + e.getMessage());
                System.out.println("Verifique que PostgreSQL este ejecutandose y la base de datos 'literalura' exista");
            }
        }
        esperarEnter();
    }

    private void mostrarLibrosRegistrados() {
        System.out.println("\nLIBROS REGISTRADOS EN LA BASE DE DATOS");
        System.out.println("==========================================");
        
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("X No hay libros registrados en la base de datos.");
            System.out.println("Use la opcion 1 para buscar y agregar libros desde la API");
        } else {
            System.out.println("Total de libros encontrados: " + libros.size());
            System.out.println();
            
            for (int i = 0; i < libros.size(); i++) {
                System.out.println("LIBRO #" + (i + 1));
                System.out.println(libros.get(i));
            }
            
            System.out.println("RESUMEN: " + libros.size() + " libro(s) registrado(s)");
        }
        esperarEnter();
    }

    private void mostrarAutoresRegistrados() {
        System.out.println("\nAUTORES REGISTRADOS EN LA BASE DE DATOS");
        System.out.println("===========================================");
        
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("X No hay autores registrados en la base de datos.");
            System.out.println("Los autores se registran automaticamente al agregar libros");
        } else {
            System.out.println("Total de autores encontrados: " + autores.size());
            System.out.println();
            
            for (int i = 0; i < autores.size(); i++) {
                System.out.println("AUTOR #" + (i + 1));
                System.out.println(autores.get(i));
            }
            
            System.out.println("RESUMEN: " + autores.size() + " autor(es) registrado(s)");
        }
        esperarEnter();
    }

    private void mostrarAutoresVivosEnAno() {
        System.out.println("\nAUTORES VIVOS EN UN ANO ESPECIFICO");
        System.out.println("======================================");
        System.out.print("Ingrese el ano que desea consultar (ej: 1800, 1900, 2000): ");

        try {
            var anoStr = teclado.nextLine();
            Integer ano = Integer.valueOf(anoStr);
            
            if (ano < 1 || ano > 2024) {
                System.out.println("\nX Error: Ingrese un ano valido (entre 1 y 2024)");
                esperarEnter();
                return;
            }
            
            System.out.println("\nBuscando autores vivos en el ano " + ano + "...");
            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAno(ano);
            
            if (autoresVivos.isEmpty()) {
                System.out.println("X No hay autores vivos registrados en el ano " + ano);
                System.out.println("Agregue mas libros para obtener mas datos de autores");
            } else {
                System.out.println("OK Autores encontrados vivos en " + ano + ":");
                System.out.println();
                
                for (int i = 0; i < autoresVivos.size(); i++) {
                    System.out.println("AUTOR #" + (i + 1));
                    System.out.println(autoresVivos.get(i));
                }
                
                System.out.println("TOTAL: " + autoresVivos.size() + " autor(es) vivo(s) en " + ano);
            }
        } catch (NumberFormatException e) {
            System.out.println("\nX Error: Ingrese un ano valido (solo numeros)");
        } catch (Exception e) {
            System.out.println("\nX Error al consultar autores: " + e.getMessage());
        }
        esperarEnter();
    }

    private void mostrarLibrosPorIdioma() {
        System.out.println("\nLIBROS POR IDIOMA");
        System.out.println("====================");
        
        List<String> idiomasDisponibles = libroRepository.findDistinctIdiomas();
        
        if (idiomasDisponibles.isEmpty()) {
            System.out.println("X No hay libros registrados en la base de datos.");
            System.out.println("Use la opcion 1 para agregar libros primero");
            esperarEnter();
            return;
        }
        
        System.out.println("Idiomas disponibles en su base de datos:");
        System.out.println();
        idiomasDisponibles.forEach(idioma -> {
            Long cantidad = libroRepository.countByIdioma(idioma);
            String nombreIdioma = obtenerNombreIdioma(idioma);
            System.out.printf("- %s (%s) - %d libro(s)%n", idioma.toUpperCase(), nombreIdioma, cantidad);
        });
        
        System.out.println();
        System.out.print("Ingrese el codigo del idioma que desea consultar: ");
        var idioma = teclado.nextLine().toLowerCase().trim();
        
        if (idioma.isEmpty()) {
            System.out.println("\nX Error: Debe ingresar un codigo de idioma valido");
            esperarEnter();
            return;
        }
        
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("\nX No hay libros registrados en el idioma: " + idioma);
        } else {
            System.out.println("\nLIBROS EN " + idioma.toUpperCase() + " (" + obtenerNombreIdioma(idioma) + "):");
            System.out.println("=====================================");
            
            for (int i = 0; i < librosPorIdioma.size(); i++) {
                System.out.println("LIBRO #" + (i + 1));
                System.out.println(librosPorIdioma.get(i));
            }
            
            System.out.println("TOTAL: " + librosPorIdioma.size() + " libro(s) en " + idioma.toUpperCase());
        }
        esperarEnter();
    }

    private void mostrarEstadisticasPorIdioma() {
        System.out.println("\nESTADISTICAS DE LIBROS POR IDIOMA");
        System.out.println("=====================================");
        
        List<Object[]> estadisticas = libroRepository.obtenerEstadisticasPorIdioma();
        
        if (estadisticas.isEmpty()) {
            System.out.println("X No hay libros registrados en la base de datos.");
            System.out.println("Use la opcion 1 para agregar libros y generar estadisticas");
            esperarEnter();
            return;
        }
        
        System.out.printf("%-15s %-20s %s%n", "CODIGO", "IDIOMA", "CANTIDAD");
        System.out.println("================================================");
        
        estadisticas.forEach(stat -> {
            String idioma = (String) stat[0];
            Long cantidad = (Long) stat[1];
            String nombreIdioma = obtenerNombreIdioma(idioma);
            System.out.printf("%-15s %-20s %d libro(s)%n", 
                idioma.toUpperCase(), nombreIdioma, cantidad);
        });
        
        // Mostrar total general
        Long totalLibros = estadisticas.stream()
            .mapToLong(stat -> (Long) stat[1])
            .sum();
        
        System.out.println("================================================");
        System.out.println("TOTAL GENERAL: " + totalLibros + " libro(s) registrado(s)");
        System.out.println("IDIOMAS DIFERENTES: " + estadisticas.size());
        esperarEnter();
    }

    private void mostrarTop10LibrosMasDescargados() {
        System.out.println("\nTOP 10 LIBROS MAS DESCARGADOS");
        System.out.println("=================================");
        
        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
        
        if (topLibros.isEmpty()) {
            System.out.println("X No hay libros registrados en la base de datos.");
            System.out.println("Agregue libros para generar el ranking");
            esperarEnter();
            return;
        }
        
        System.out.println("Ranking de los libros mas populares:");
        System.out.println();
        
        for (int i = 0; i < Math.min(10, topLibros.size()); i++) {
            Libro libro = topLibros.get(i);
            String medalla = obtenerMedalla(i + 1);
            System.out.printf("%s %d. %s%n", medalla, (i + 1), libro.getTitulo());
            System.out.printf("   Autor: %s%n",
                libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido");
            System.out.printf("   Descargas: %.0f%n", libro.getNumeroDeDescargas());
            System.out.printf("   Idioma: %s%n", libro.getIdioma().toUpperCase());
            System.out.println();
        }
        
        System.out.println("Total de libros en el ranking: " + Math.min(10, topLibros.size()));
        esperarEnter();
    }

    // Metodos auxiliares para mejor presentacion
    private String obtenerNombreIdioma(String codigo) {
        return switch (codigo.toLowerCase()) {
            case "es" -> "Espanol";
            case "en" -> "Ingles";
            case "fr" -> "Frances";
            case "pt" -> "Portugues";
            case "de" -> "Aleman";
            case "it" -> "Italiano";
            case "ru" -> "Ruso";
            case "zh" -> "Chino";
            case "ja" -> "Japones";
            default -> "Idioma desconocido";
        };
    }

    private String obtenerMedalla(int posicion) {
        return switch (posicion) {
            case 1 -> "[1er]";
            case 2 -> "[2do]";
            case 3 -> "[3er]";
            default -> "[" + posicion + "]";
        };
    }
}
