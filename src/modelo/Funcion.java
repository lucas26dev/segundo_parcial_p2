
package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Funcion implements Activable, Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final Pelicula pelicula;
    private final Sala sala;
    private final LocalDateTime fechaHora;

    private double precioBase;
    private boolean activa;

    public Funcion(int id, Pelicula pelicula, Sala sala,
                   LocalDateTime fechaHora, double precioBase) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de función debe ser positivo");
        }

        if (pelicula == null || sala == null) {
            throw new IllegalArgumentException(
                    "La película y la sala son obligatorias");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException(
                    "La fecha y hora son obligatorias");
        }

        validarPrecio(precioBase);

        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase;
        this.activa = true;
    }

    private void validarPrecio(double precio) {
        if (!Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser un número positivo y finito");
        }
    }

    public int getId() {
        return id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHora.plusMinutes(pelicula.getDuracion());
    }

    public void actualizarPrecio(double nuevoPrecio) {
        validarPrecio(nuevoPrecio);
        this.precioBase = nuevoPrecio;
    }

    @Override
    public boolean estaActiva() {
        return activa;
    }

    public void cambiarEstado(boolean activa) {
        this.activa = activa;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Funcion)) {
            return false;
        }

        Funcion otra = (Funcion) obj;

        return this.id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

