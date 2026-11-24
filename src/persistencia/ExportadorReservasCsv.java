
package persistencia;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import modelo.Entrada;
import modelo.EntradaEstudiante;
import modelo.Reserva;

public class ExportadorReservasCsv {
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void exportar(
            List<Reserva> reservas,
            Path destino) throws IOException {

        if (reservas == null || destino == null) {
            throw new IllegalArgumentException(
                    "Las reservas y el destino son obligatorios"
            );
        }

        StringBuilder csv = new StringBuilder();

        csv.append(
                "codigo_reserva,estado,dni,nombre,apellido,"
                + "pelicula,sala,fecha_funcion,butaca,"
                + "tipo_entrada,precio_entrada,total_reserva\n"
        );

        for (Reserva reserva : reservas) {

            for (Entrada entrada : reserva.getEntradas()) {

                String tipo = entrada instanceof EntradaEstudiante
                        ? "ESTUDIANTE"
                        : "GENERAL";

                agregarFila(
                        csv,
                        String.valueOf(reserva.getId()),
                        reserva.estaActiva()
                                ? "ACTIVA" : "CANCELADA",
                        String.valueOf(
                                reserva.getCliente().getDni()
                        ),
                        reserva.getCliente().getNombre(),
                        reserva.getCliente().getApellido(),
                        reserva.getFuncion()
                                .getPelicula().getTitulo(),
                        String.valueOf(
                                reserva.getFuncion().getSala().getId()
                        ),
                        reserva.getFuncion()
                                .getFechaHora()
                                .format(FORMATO_FECHA),
                        entrada.getButaca().getFila()
                                + entrada.getButaca().getNumero(),
                        tipo,
                        String.valueOf(
                                entrada.getPrecioConfirmado()
                        ),
                        String.valueOf(reserva.getTotal())
                );
            }
        }

        Path archivo = destino.toAbsolutePath().normalize();
        Path carpeta = archivo.getParent();

        Path temporal = Files.createTempFile(
                carpeta, "reservas-", ".tmp"
        );

        try {
            Files.writeString(
                    temporal,
                    csv.toString(),
                    StandardCharsets.UTF_8
            );

            Files.move(
                    temporal,
                    archivo,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private void agregarFila(
            StringBuilder csv,
            String... valores) {

        for (int i = 0; i < valores.length; i++) {

            if (i > 0) {
                csv.append(",");
            }

            csv.append(escaparCsv(valores[i]));
        }

        csv.append("\n");
    }

    private String escaparCsv(String valor) {

        if (valor == null) {
            return "";
        }

        String escapado = valor.replace("\"", "\"\"");

        if (escapado.contains(",")
                || escapado.contains("\"")
                || escapado.contains("\n")
                || escapado.contains("\r")) {

            return "\"" + escapado + "\"";
        }

        return escapado;
    }
}
