
package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final int dni;
    private String nombre;
    private String apellido;
    private String telefono;

    public Cliente(int id, int dni, String nombre,
                   String apellido, String telefono) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser positivo");
        }

        if (dni <= 0) {
            throw new IllegalArgumentException(
                    "El DNI debe ser positivo");
        }

        validarNombreApellido(nombre, apellido);

        this.id = id;
        this.dni = dni;
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.telefono = normalizarTelefono(telefono);
    }

    private void validarNombreApellido(
            String nombre, String apellido) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException(
                    "El apellido es obligatorio");
        }
    }

    private String normalizarTelefono(String telefono) {

        if (telefono == null || telefono.isBlank()) {
            return null;
        }

        return telefono.trim();
    }

    public int getId() {
        return id;
    }

    public int getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void actualizarDatos(String nombre,
                                String apellido,
                                String telefono) {

        validarNombreApellido(nombre, apellido);

        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.telefono = normalizarTelefono(telefono);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Cliente)) {
            return false;
        }

        Cliente otro = (Cliente) obj;

        return this.id == otro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

