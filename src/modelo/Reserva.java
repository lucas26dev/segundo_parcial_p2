
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Entrada> entradas;
    private final double total;

    private boolean activa;

    public Reserva(int id, Cliente cliente,
                   Funcion funcion, List<Entrada> entradas) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de reserva debe ser positivo");
        }

        if (cliente == null || funcion == null) {
            throw new IllegalArgumentException(
                    "El cliente y la función son obligatorios");
        }

        if (entradas == null || entradas.isEmpty()) {
            throw new IllegalArgumentException(
                    "La reserva debe tener al menos una entrada");
        }

        Set<Butaca> butacasSeleccionadas = new HashSet<>();
        double importeTotal = 0;

        for (Entrada entrada : entradas) {

            if (entrada == null) {
                throw new IllegalArgumentException(
                        "No se permiten entradas nulas");
            }

        if (!funcion.getSala().getButacas()
                .contains(entrada.getButaca())) {

            throw new IllegalArgumentException(
                    "La butaca no pertenece a la distribución de la sala"
            );
        }

            if (!butacasSeleccionadas.add(entrada.getButaca())) {
                throw new IllegalArgumentException(
                        "Hay butacas repetidas en la reserva");
            }

            importeTotal += entrada.getPrecioConfirmado();
        }

        if (!Double.isFinite(importeTotal)) {
            throw new IllegalArgumentException(
                    "El importe total no es válido");
        }

        this.id = id;
        this.cliente = cliente;
        this.funcion = funcion;
        this.entradas = new ArrayList<>(entradas);
        this.total = importeTotal;
        this.activa = true;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Entrada> getEntradas() {
        return new ArrayList<>(entradas);
    }

    public double getTotal() {
        return total;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void cancelar() {

        if (!activa) {
            throw new IllegalStateException(
                    "La reserva ya está cancelada");
        }

        this.activa = false;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Reserva)) {
            return false;
        }

        Reserva otra = (Reserva) obj;
        return this.id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
