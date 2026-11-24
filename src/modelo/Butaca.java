
package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Butaca implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idSala;
    private final String fila;
    private final int numero;

    public Butaca(int idSala, String fila, int numero) {

        if (idSala <= 0) {
            throw new IllegalArgumentException(
                    "El ID de sala debe ser positivo");
        }

        if (fila == null || fila.isBlank()) {
            throw new IllegalArgumentException(
                    "La fila no puede estar vacía");
        }

        if (numero <= 0) {
            throw new IllegalArgumentException(
                    "El número de butaca debe ser positivo");
        }

        this.idSala = idSala;
        this.fila = fila.trim().toUpperCase();
        this.numero = numero;
    }

    public int getIdSala() {
        return idSala;
    }

    public String getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Butaca)) {
            return false;
        }

        Butaca otra = (Butaca) obj;

        return idSala == otra.idSala
                && numero == otra.numero
                && fila.equals(otra.fila);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSala, fila, numero);
    }
}
