
package modelo;

public class EntradaEstudiante extends Entrada {

    private static final long serialVersionUID = 1L;

    private static final double DESCUENTO = 0.20;

    public EntradaEstudiante(int id, Butaca butaca,
                             double precioBase) {

        super(id, butaca, calcularPrecioEstudiante(precioBase));
    }

    private static double calcularPrecioEstudiante(
            double precioBase) {

        validarPrecio(precioBase);

        return precioBase * (1 - DESCUENTO);
    }

    @Override
    public double calcularPrecio(double precioBase) {
        return calcularPrecioEstudiante(precioBase);
    }
}
