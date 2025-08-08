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

    // Método para pausar y esperar input del usuario
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
                    ===    CATÁLOGO DE LIBROS LITERALURA ===
                    =======================================
                    
                    Elije la opción a través de su número:
                    
                    1- Buscar libro por título
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    6- Mostrar estadísticas de libros por idioma
                    7- Top 10 libros más descargados
                    
                    0- Salir
                    =======================================
                    """;
            System.out.println(menu);
            System.out.print("Seleccione una opción: ");
            
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
                        System.out.println("\n¡Gracias por usar Literalura! ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("\n❌ Opción inválida. Por favor, seleccione un número del 0 al 7.");
                        esperarEnter();
                }
            } catch (Exception e) {
                System.out.println("\n❌ Error: Ingrese un número válido (0-7)");
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
        System.out.println("\n📚 BÚSQUEDA DE LIBROS EN LA API");
        System.out.println("================================");
        System.out.print("Ingrese el título del libro que desea buscar: ");
        var nombreLibro = teclado.nextLine();
        
        if (nombreLibro.trim().isEmpty()) {
            System.out.println("\n❌ Error: Debe ingresar un título válido");
            return null;
        }
        
        try {
            System.out.println("\n🔍 Buscando '" + nombreLibro + "' en la API de Gutendx...");
            var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
            var datosBusqueda = conversor.obtenerDatos(json, DatosRespuesta.class);
            
            Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                    .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                    .findFirst();
                    
            if (libroBuscado.isPresent()) {
                System.out.println("✅ ¡Libro encontrado en la API!");
                return libroBuscado.get();
            } else {
                System.out.println("❌ Libro no encontrado en la API");
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Error al consultar la API: " + e.getMessage());
            System.out.println("💡 Sugerencias:");
            System.out.println("   - Verifique su conexión a Internet");
            System.out.println("   - Intente con un título más específico");
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
                    System.out.println("\n⚠️  El libro ya está registrado en la base de datos:");
                    System.out.println(libroExistente.get());
                } else {
                    Libro libro = new Libro(datos);
                    libroRepository.save(libro);
                    System.out.println("\n✅ ¡Libro guardado exitosamente en la base de datos!");
                    System.out.println(libro);
                }
            } catch (Exception e) {
                System.out.println("\n❌ Error al guardar el libro: " + e.getMessage());
                System.out.println("💡 Verifique que PostgreSQL esté ejecutándose y la base de datos 'literalura' exista");
            }
        }
        esperarEnter();
    }

    private void mostrarLibrosRegistrados() {
        System.out.println("\n📚 LIBROS REGISTRADOS EN LA BASE DE DATOS");
        System.out.println("==========================================");
        
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("❌ No hay libros registrados en la base de datos.");
            System.out.println("💡 Use la opción 1 para buscar y agregar libros desde la API");
        } else {
            System.out.println("📋 Total de libros encontrados: " + libros.size());
            System.out.println();
            
            for (int i = 0; i < libros.size(); i++) {
                System.out.println("📖 LIBRO #" + (i + 1));
                System.out.println(libros.get(i));
            }
            
            System.out.println("📊 RESUMEN: " + libros.size() + " libro(s) registrado(s)");
        }
        esperarEnter();
    }

    private void mostrarAutoresRegistrados() {
        System.out.println("\n👥 AUTORES REGISTRADOS EN LA BASE DE DATOS");
        System.out.println("===========================================");
        
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("❌ No hay autores registrados en la base de datos.");
            System.out.println("💡 Los autores se registran automáticamente al agregar libros");
        } else {
            System.out.println("📋 Total de autores encontrados: " + autores.size());
            System.out.println();
            
            for (int i = 0; i < autores.size(); i++) {
                System.out.println("✍️  AUTOR #" + (i + 1));
                System.out.println(autores.get(i));
            }
            
            System.out.println("📊 RESUMEN: " + autores.size() + " autor(es) registrado(s)");
        }
        esperarEnter();
    }

    private void mostrarAutoresVivosEnAno() {
        System.out.println("\n📅 AUTORES VIVOS EN UN AÑO ESPECÍFICO");
        System.out.println("======================================");
        System.out.print("Ingrese el año que desea consultar (ej: 1800, 1900, 2000): ");
        
        try {
            var anoStr = teclado.nextLine();
            Integer ano = Integer.valueOf(anoStr);
            
            if (ano < 1 || ano > 2024) {
                System.out.println("\n❌ Error: Ingrese un año válido (entre 1 y 2024)");
                esperarEnter();
                return;
            }
            
            System.out.println("\n🔍 Buscando autores vivos en el año " + ano + "...");
            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAno(ano);
            
            if (autoresVivos.isEmpty()) {
                System.out.println("❌ No hay autores vivos registrados en el año " + ano);
                System.out.println("💡 Agregue más libros para obtener más datos de autores");
            } else {
                System.out.println("✅ Autores encontrados vivos en " + ano + ":");
                System.out.println();
                
                for (int i = 0; i < autoresVivos.size(); i++) {
                    System.out.println("👤 AUTOR #" + (i + 1));
                    System.out.println(autoresVivos.get(i));
                }
                
                System.out.println("📊 TOTAL: " + autoresVivos.size() + " autor(es) vivo(s) en " + ano);
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Error: Ingrese un año válido (solo números)");
        } catch (Exception e) {
            System.out.println("\n❌ Error al consultar autores: " + e.getMessage());
        }
        esperarEnter();
    }

    private void mostrarLibrosPorIdioma() {
        System.out.println("\n🌍 LIBROS POR IDIOMA");
        System.out.println("====================");
        
        List<String> idiomasDisponibles = libroRepository.findDistinctIdiomas();
        
        if (idiomasDisponibles.isEmpty()) {
            System.out.println("❌ No hay libros registrados en la base de datos.");
            System.out.println("💡 Use la opción 1 para agregar libros primero");
            esperarEnter();
            return;
        }
        
        System.out.println("📋 Idiomas disponibles en su base de datos:");
        System.out.println();
        idiomasDisponibles.forEach(idioma -> {
            Long cantidad = libroRepository.countByIdioma(idioma);
            String nombreIdioma = obtenerNombreIdioma(idioma);
            System.out.printf("🔤 %s (%s) - %d libro(s)%n", idioma.toUpperCase(), nombreIdioma, cantidad);
        });
        
        System.out.println();
        System.out.print("Ingrese el código del idioma que desea consultar: ");
        var idioma = teclado.nextLine().toLowerCase().trim();
        
        if (idioma.isEmpty()) {
            System.out.println("\n❌ Error: Debe ingresar un código de idioma válido");
            esperarEnter();
            return;
        }
        
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("\n❌ No hay libros registrados en el idioma: " + idioma);
        } else {
            System.out.println("\n📚 LIBROS EN " + idioma.toUpperCase() + " (" + obtenerNombreIdioma(idioma) + "):");
            System.out.println("=====================================");
            
            for (int i = 0; i < librosPorIdioma.size(); i++) {
                System.out.println("📖 LIBRO #" + (i + 1));
                System.out.println(librosPorIdioma.get(i));
            }
            
            System.out.println("📊 TOTAL: " + librosPorIdioma.size() + " libro(s) en " + idioma.toUpperCase());
        }
        esperarEnter();
    }

    private void mostrarEstadisticasPorIdioma() {
        System.out.println("\n📊 ESTADÍSTICAS DE LIBROS POR IDIOMA");
        System.out.println("=====================================");
        
        List<Object[]> estadisticas = libroRepository.obtenerEstadisticasPorIdioma();
        
        if (estadisticas.isEmpty()) {
            System.out.println("❌ No hay libros registrados en la base de datos.");
            System.out.println("💡 Use la opción 1 para agregar libros y generar estadísticas");
            esperarEnter();
            return;
        }
        
        System.out.printf("%-15s %-20s %s%n", "CÓDIGO", "IDIOMA", "CANTIDAD");
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
        System.out.println("📚 TOTAL GENERAL: " + totalLibros + " libro(s) registrado(s)");
        System.out.println("🌍 IDIOMAS DIFERENTES: " + estadisticas.size());
        esperarEnter();
    }

    private void mostrarTop10LibrosMasDescargados() {
        System.out.println("\n🏆 TOP 10 LIBROS MÁS DESCARGADOS");
        System.out.println("=================================");
        
        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
        
        if (topLibros.isEmpty()) {
            System.out.println("❌ No hay libros registrados en la base de datos.");
            System.out.println("💡 Agregue libros para generar el ranking");
            esperarEnter();
            return;
        }
        
        System.out.println("📋 Ranking de los libros más populares:");
        System.out.println();
        
        for (int i = 0; i < Math.min(10, topLibros.size()); i++) {
            Libro libro = topLibros.get(i);
            String medalla = obtenerMedalla(i + 1);
            System.out.printf("%s %d. %s%n", medalla, (i + 1), libro.getTitulo());
            System.out.printf("   👤 Autor: %s%n", 
                libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido");
            System.out.printf("   📥 Descargas: %.0f%n", libro.getNumeroDeDescargas());
            System.out.printf("   🌍 Idioma: %s%n", libro.getIdioma().toUpperCase());
            System.out.println();
        }
        
        System.out.println("📊 Total de libros en el ranking: " + Math.min(10, topLibros.size()));
        esperarEnter();
    }

    // Métodos auxiliares para mejor presentación
    private String obtenerNombreIdioma(String codigo) {
        return switch (codigo.toLowerCase()) {
            case "es" -> "Español";
            case "en" -> "Inglés";
            case "fr" -> "Francés";
            case "pt" -> "Portugués";
            case "de" -> "Alemán";
            case "it" -> "Italiano";
            case "ru" -> "Ruso";
            case "zh" -> "Chino";
            case "ja" -> "Japonés";
            default -> "Idioma desconocido";
        };
    }

    private String obtenerMedalla(int posicion) {
        return switch (posicion) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "🏅";
        };
    }
}
