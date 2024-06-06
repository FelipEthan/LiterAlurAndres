package LiteraLurAndres.principal;

import LiteraLurAndres.model.*;
import LiteraLurAndres.repository.Autorepo;
import LiteraLurAndres.repository.LibrosRepo;
import LiteraLurAndres.service.ConsumoAPI;
import LiteraLurAndres.service.ConvierteDatos;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    @Autowired
    private LibrosRepo librosRepo;
    @Autowired
    private Autorepo autorepo;

    public void Menu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Lista de todos los libros
                    2 - Búsqueda de libro por título
                    3 - Lista de todos los autores disponibles
                    4 - Buscar idiomas disponibles
                    5 - Autores vivos durante la fecha ingresada
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    Libros();
                    break;
                case 2:
                    BuscarLibroXTitulo();
                    break;
                case 3:
                    Autores();
                    break;
                case 4:
                    Idioma(teclado);
                    break;
                case 5:
                    AutoresVivos();
                    break;
                case 0:
                    System.out.println("Cerrando proceso");
                    break;
                default:
                    System.out.println("Opción no valida");
            }
        }

    }

    public void Libros() {
        //Listar todos los libros
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        System.out.println("Libros en disponibilidad:");
        System.out.println("");
        datosBusqueda.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::titulo))
                .map(l -> l.titulo())
                .forEach(System.out::println);
    }

    public void Autores() {
        //Listar todos los libros
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        System.out.println("Autores en disponibilidad");
        System.out.println("");
        datosBusqueda.resultados().stream()
                .flatMap(libro -> libro.autor().stream()) // Obtener stream de autores
                .map(DatosAutor::nombre) // Mapear a los nombres de los autores
                .distinct() // Eliminar duplicados
                .sorted() // Ordenar alfabéticamente
                .forEach(System.out::println); // Imprimir cada nombre de autor
    }

    public void BuscarLibroXTitulo() {
        // Busqueda de libros por nombre
        System.out.println("Ingrese el nombre del libro: ");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            // Obtener el primer autor del libro (si hay)
            DatosAutor primerAutor = libroBuscado.get().autor().stream().findFirst().orElse(null);
            if (primerAutor != null) {
                Autor autor = autorepo.findByNombre(primerAutor.nombre());
                if (autor == null) {
                    autor = new Autor();
                    autor.setNombre(primerAutor.nombre());
                    autor.setFechaDeNacimiento(primerAutor.fechaDeNacimiento());
                    autor.setFechaDeFallecimiento(primerAutor.fechaDeFallecimiento());
                    autor.setLibros(new ArrayList<>()); // Inicializar la lista de libros
                }

                // Crear un nuevo libro y asignar el autor
                Libros libro = new Libros();
                libro.setTitulo(libroBuscado.get().titulo());
                libro.setAutores(autor);
                libro.setIdiomas(String.join(", ", libroBuscado.get().idiomas()));
                libro.setNumeroDeDescargas(libroBuscado.get().numeroDeDescargas());

                // Verificar si la lista de libros del autor es nula y crearla si es necesario
                if (autor.getLibros() == null) {
                    autor.setLibros(new ArrayList<>());
                }

                // Agregar el libro al autor y guardar ambos en la base de datos
                autor.getLibros().add(libro);
                autorepo.save(autor);

                // Mostrar información del libro encontrado por consola
                System.out.println("");
                System.out.println("Libro encontrado:");
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Idiomas: " + libro.getIdiomas());
                System.out.println("Número de descargas: " + libro.getNumeroDeDescargas());
                System.out.println("Autor: " + autor.getNombre());
                System.out.println("Fecha de nacimiento del autor: " + autor.getFechaDeNacimiento());
                System.out.println("Fecha de fallecimiento del autor: " + autor.getFechaDeFallecimiento());
                System.out.println("");
            } else {
                System.out.println("No se encontró autor");
            }
        } else {
            System.out.println("No encontrado");
        }
    }

    public void Idioma(Scanner teclado) {
        System.out.println("Ingrese el idioma:");
        var idioma = teclado.nextLine();
        List<Libros> libros = librosRepo.findByIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma ingresado.");
        } else {
            System.out.println("Libros encontrados en el idioma " + idioma + ":");
            for (Libros libro : libros) {
                System.out.println("");
                System.out.println("Libro encontrado:");
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Idiomas: " + libro.getIdiomas());
                System.out.println("Número de descargas: " + libro.getNumeroDeDescargas());
                System.out.println("");
            }
        }
    }

    public void AutoresVivos() {
        System.out.println("Ingrese el año para listar los autores vivos:");
        String años = teclado.nextLine();
        List<Autor> autores = autorepo.findAutoresVivosEnAño(años);

        if (autores.isEmpty()) {
            System.out.println("");
            System.out.println("No se encontraron autores vivos durante el año ingresado. Por favor, intente con otro año.");
            System.out.println("");
        } else {
            autores.forEach(System.out::println);
        }
    }
}

