
package servicios;

import java.time.LocalDateTime;
import java.util.List;

import modelo.EstadoSistema;
import modelo.Funcion;
import modelo.Pelicula;
import modelo.Repositorio;

public class GestorPeliculas {

    private final EstadoSistema estado;
    private final Repositorio<Pelicula> peliculas;
    private final Repositorio<Funcion> funciones;

    public GestorPeliculas(EstadoSistema estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del sistema no puede ser null");
        }

        this.estado = estado;
        this.peliculas = estado.getPeliculas();
        this.funciones = estado.getFunciones();
    }

    public Pelicula registrarPelicula(String titulo, int duracion) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título no puede estar vacío");
        }

        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duración debe ser mayor que cero");
        }

        int id = estado.generarIdPelicula();

        Pelicula pelicula = new Pelicula(id, titulo, duracion);
        peliculas.agregar(pelicula);

        return pelicula;
    }

    public List<Pelicula> listarPeliculas() {
        return peliculas.listar();
    }

    public Pelicula buscarPorId(int id) {

        for (Pelicula pelicula : peliculas.listar()) {
            if (pelicula.getId() == id) {
                return pelicula;
            }
        }

        return null;
    }

    public void actualizarPelicula(
            int id, String titulo, int duracion) {

        Pelicula pelicula = buscarPorId(id);

        if (pelicula == null) {
            throw new IllegalArgumentException(
                    "No existe una película con ID: " + id);
        }

        if (duracion != pelicula.getDuracion()) {

            for (Funcion funcion : funciones.listar()) {

                if (funcion.estaActiva()
                        && funcion.getPelicula().equals(pelicula)
                        && funcion.getFechaHora()
                                .isAfter(LocalDateTime.now())) {

                    throw new IllegalArgumentException(
                            "No se puede cambiar la duración: "
                            + "la película tiene funciones futuras activas");
                }
            }
        }

        pelicula.actualizarDatos(titulo, duracion);
    }
    
    
    public void desactivarPelicula(int id) {

        Pelicula pelicula = buscarPorId(id);

        if (pelicula == null) {
            throw new IllegalArgumentException(
                    "No existe una película con ID: " + id);
        }

        if (!pelicula.estaActiva()) {
            throw new IllegalArgumentException(
                    "La película ya está desactivada");
        }

        for (Funcion funcion : funciones.listar()) {

            if (funcion.estaActiva()
                    && funcion.getPelicula().equals(pelicula)
                    && funcion.getFechaHora()
                            .isAfter(LocalDateTime.now())) {

                throw new IllegalArgumentException(
                        "No se puede desactivar la película "
                        + "porque tiene funciones futuras activas");
            }
        }

        pelicula.cambiarEstado(false);
    }
    public void reactivarPelicula(int id) {

        Pelicula pelicula = buscarPorId(id);

        if (pelicula == null) {
            throw new IllegalArgumentException(
                    "No existe una película con ID: " + id);
        }

        if (pelicula.estaActiva()) {
            throw new IllegalArgumentException(
                    "La película ya está activa");
        }

        pelicula.cambiarEstado(true);
    }

}
