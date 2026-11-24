
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sala implements Activable, Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final int cantidadFilas;
    private final int butacasPorFila;
    private final List<Butaca> butacas;
    private boolean activa;

    public Sala(int id, int cantidadFilas, int butacasPorFila) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de sala debe ser positivo");
        }

        if (cantidadFilas <= 0 || butacasPorFila <= 0) {
            throw new IllegalArgumentException(
                    "Las filas y butacas por fila deben ser positivas");
        }

        this.id = id;
        this.cantidadFilas = cantidadFilas;
        this.butacasPorFila = butacasPorFila;
        this.activa = true;
        this.butacas = new ArrayList<>();

        generarButacas();
    }

    private void generarButacas() {

        for (int i = 0; i < cantidadFilas; i++) {

            String fila = nombreFila(i);

            for (int numero = 1; numero <= butacasPorFila; numero++) {
                butacas.add(new Butaca(id, fila, numero));
            }
        }
    }

    private String nombreFila(int indice) {
        StringBuilder nombre = new StringBuilder();
        int posicion = indice;

        do {
            nombre.insert(0, (char) ('A' + posicion % 26));
            posicion = posicion / 26 - 1;
        } while (posicion >= 0);

        return nombre.toString();
    }

    public int getId() {
        return id;
    }

    public int getCantidadFilas() {
        return cantidadFilas;
    }

    public int getButacasPorFila() {
        return butacasPorFila;
    }

    public List<Butaca> getButacas() {
        return new ArrayList<>(butacas);
    }

    public int getCapacidad() {
        return butacas.size();
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

        if (!(obj instanceof Sala)) {
            return false;
        }

        Sala otra = (Sala) obj;
        return this.id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

