
package servicios;
import java.util.List;
import modelo.EstadoSistema;
import modelo.Repositorio;
import modelo.Sala;
import java.time.LocalDateTime;
import modelo.Funcion;

public class GestorSalas {

    private final EstadoSistema estado;
    private final Repositorio<Sala> salas;

    public GestorSalas(EstadoSistema estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del sistema no puede ser null");
        }

        this.estado = estado;
        this.salas = estado.getSalas();
    }

    public Sala registrarSala(int cantidadFilas, int butacasPorFila) {

        if (cantidadFilas <= 0 || butacasPorFila <= 0) {
            throw new IllegalArgumentException(
                    "Las filas y butacas por fila deben ser positivas");
        }

        int id = estado.generarIdSala();

        Sala sala = new Sala(id, cantidadFilas, butacasPorFila);

        salas.agregar(sala);

        return sala;
    }

    public List<Sala> listarSalas() {
        return salas.listar();
    }

    public Sala buscarPorId(int id) {

    for (Sala sala : salas.listar()) {

        if (sala.getId() == id) {
            return sala;
        }
    }

    return null;
}


    public void desactivarSala(int id) {

        Sala sala = buscarPorId(id);

        if (sala == null) {
            throw new IllegalArgumentException(
                    "No existe una sala con ID: " + id);
        }

        if (!sala.estaActiva()) {
            throw new IllegalArgumentException(
                    "La sala ya está desactivada");
        }

        for (Funcion funcion : estado.getFunciones().listar()) {

            if (funcion.estaActiva()
                    && funcion.getSala().equals(sala)
                    && funcion.getFechaHora()
                            .isAfter(LocalDateTime.now())) {

                throw new IllegalArgumentException(
                        "No se puede desactivar la sala "
                        + "porque tiene funciones futuras activas");
            }
        }

        sala.cambiarEstado(false);
    }

    public void reactivarSala(int id) {

    Sala sala = buscarPorId(id);

    if (sala == null) {
        throw new IllegalArgumentException(
                "No existe una sala con ID: " + id);
    }

    if (sala.estaActiva()) {
        throw new IllegalArgumentException(
                "La sala ya está activa");
    }

    sala.cambiarEstado(true);
    }

}
