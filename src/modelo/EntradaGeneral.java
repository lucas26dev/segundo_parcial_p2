
package modelo;

public class EntradaGeneral extends Entrada {

    private static final long serialVersionUID = 1L;

    public EntradaGeneral(int id, Butaca butaca,
                          double precioBase) {

        super(id, butaca, precioBase);
    }

    @Override
    public double calcularPrecio(double precioBase) {

        validarPrecio(precioBase);

        return precioBase;
    }
}
