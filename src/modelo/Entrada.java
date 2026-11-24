
package modelo;

import java.io.Serializable;
import java.util.Objects;

public abstract class Entrada implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final Butaca butaca;
    private final double precioConfirmado;

    protected Entrada(int id, Butaca butaca,
                      double precioConfirmado) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de entrada debe ser positivo");
        }

        if (butaca == null) {
            throw new IllegalArgumentException(
                    "La butaca es obligatoria");
        }

        validarPrecio(precioConfirmado);

        this.id = id;
        this.butaca = butaca;
        this.precioConfirmado = precioConfirmado;
    }

    protected static void validarPrecio(double precio) {
        if (!Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser positivo y finito");
        }
    }

    public int getId() {
        return id;
    }

    public Butaca getButaca() {
        return butaca;
    }

    public double getPrecioConfirmado() {
        return precioConfirmado;
    }

    public abstract double calcularPrecio(double precioBase);

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Entrada)) {
            return false;
        }

        Entrada otra = (Entrada) obj;
        return this.id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
