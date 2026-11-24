
package servicios;

import java.time.LocalDateTime;
import java.util.List;
import modelo.EstadoSistema;
import modelo.Funcion;
import modelo.Pelicula;
import modelo.Repositorio;
import modelo.Sala;
import modelo.Reserva;

public class GestorFunciones {

    private static final int MINUTOS_ENTRE_FUNCIONES = 15;

    private final EstadoSistema estado;
    private final Repositorio<Funcion> funciones;
    private final Repositorio<Pelicula> peliculas;
    private final Repositorio<Sala> salas;

    public GestorFunciones(EstadoSistema estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del sistema no puede ser null");
        }

        this.estado = estado;
        this.funciones = estado.getFunciones();
        this.peliculas = estado.getPeliculas();
        this.salas = estado.getSalas();
    }

    public Funcion programarFuncion(
            int idPelicula,
            int idSala,
            LocalDateTime fechaHora,
            double precioBase) {

        Pelicula pelicula = buscarPelicula(idPelicula);
        Sala sala = buscarSala(idSala);

        if (pelicula == null || !pelicula.estaActiva()) {
            throw new IllegalArgumentException(
                    "La película no existe o está inactiva");
        }

        if (sala == null || !sala.estaActiva()) {
            throw new IllegalArgumentException(
                    "La sala no existe o está inactiva");
        }

        if (fechaHora == null
                || !fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La función debe programarse para una fecha futura");
        }

        if (!Double.isFinite(precioBase) || precioBase <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser positivo y finito");
        }

        validarHorario(sala, pelicula, fechaHora);

        int id = estado.generarIdFuncion();

        Funcion funcion = new Funcion(
                id, pelicula, sala, fechaHora, precioBase);

        funciones.agregar(funcion);

        return funcion;
    }

    private Pelicula buscarPelicula(int id) {

        for (Pelicula pelicula : peliculas.listar()) {
            if (pelicula.getId() == id) {
                return pelicula;
            }
        }

        return null;
    }

    private Sala buscarSala(int id) {

        for (Sala sala : salas.listar()) {
            if (sala.getId() == id) {
                return sala;
            }
        }

        return null;
    }

    private void validarHorario(
            Sala sala,
            Pelicula pelicula,
            LocalDateTime nuevoInicio) {

        LocalDateTime nuevoFin = nuevoInicio.plusMinutes(
                pelicula.getDuracion());

        for (Funcion existente : funciones.listar()) {

            if (!existente.estaActiva()) {
                continue;
            }

            if (!existente.getSala().equals(sala)) {
                continue;
            }

            LocalDateTime inicioExistente =
                    existente.getFechaHora();

            LocalDateTime finExistente =
                    existente.getFechaHoraFin();

            LocalDateTime nuevoFinConMargen =
                    nuevoFin.plusMinutes(MINUTOS_ENTRE_FUNCIONES);

            LocalDateTime finExistenteConMargen =
                    finExistente.plusMinutes(MINUTOS_ENTRE_FUNCIONES);

            boolean hayConflicto =
                    nuevoInicio.isBefore(finExistenteConMargen)
                    && inicioExistente.isBefore(nuevoFinConMargen);

            if (hayConflicto) {
                throw new IllegalArgumentException(
                        "La sala ya tiene una función "
                        + "en un horario incompatible");
            }
        }
    }

    public List<Funcion> listarFunciones() {
        return funciones.listar();
    }

    public Funcion buscarPorId(int id) {

        for (Funcion funcion : funciones.listar()) {
            if (funcion.getId() == id) {
                return funcion;
            }
        }

        return null;
    }

    public void actualizarPrecio(int id, double nuevoPrecio) {

        Funcion funcion = buscarPorId(id);

        if (funcion == null) {
            throw new IllegalArgumentException(
                    "No existe una función con ID: " + id);
        }

        funcion.actualizarPrecio(nuevoPrecio);
    }
    
    public void desactivarFuncion(int id) {

        Funcion funcion = buscarPorId(id);

        if (funcion == null) {
            throw new IllegalArgumentException(
                    "La función no existe");
        }

        if (!funcion.estaActiva()) {
            throw new IllegalArgumentException(
                    "La función ya está desactivada");
        }

        for (Reserva reserva : estado.getReservas().listar()) {

            if (reserva.estaActiva()
                    && reserva.getFuncion().equals(funcion)) {

                throw new IllegalArgumentException(
                        "No se puede desactivar la función "
                        + "porque tiene reservas activas");
            }
        }

        funcion.cambiarEstado(false);
    }

}
