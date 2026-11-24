
package modelo;

public class SeleccionEntrada {

    private final Butaca butaca;
    private final TipoEntrada tipo;

    public SeleccionEntrada(Butaca butaca, TipoEntrada tipo) {

        if (butaca == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionarse una butaca");
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionarse un tipo de entrada");
        }

        this.butaca = butaca;
        this.tipo = tipo;
    }

    public Butaca getButaca() {
        return butaca;
    }

    public TipoEntrada getTipo() {
        return tipo;
    }
}
