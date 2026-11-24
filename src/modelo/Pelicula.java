
package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Pelicula implements Activable, Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private String titulo;
    private int duracion;
    private boolean activa;

    public Pelicula(int id, String titulo, int duracion) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero");
        }

        validarDatos(titulo, duracion);

        this.id = id;
        this.titulo = titulo.trim();
        this.duracion = duracion;
        this.activa = true;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracion() {
        return duracion;
    }

    @Override
    public boolean estaActiva() {
        return activa;
    }

    public void actualizarDatos(String titulo, int duracion) {
        validarDatos(titulo, duracion);

        this.titulo = titulo.trim();
        this.duracion = duracion;
    }

    public void cambiarEstado(boolean activa) {
        this.activa = activa;
    }

    private void validarDatos(String titulo, int duracion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título no puede estar vacío");
        }

        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duración debe ser mayor que cero");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Pelicula)) {
            return false;
        }

        Pelicula otra = (Pelicula) obj;

        return this.id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
