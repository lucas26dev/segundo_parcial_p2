
package persistencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import modelo.EstadoSistema;

public class PersistenciaArchivo {

    private final Path archivo;

    public PersistenciaArchivo(Path archivo) {

        if (archivo == null) {
            throw new IllegalArgumentException(
                    "La ruta del archivo es obligatoria");
        }

        this.archivo = archivo.toAbsolutePath().normalize();
    }

    public void guardarEstado(EstadoSistema estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado no puede ser null");
        }

        Path directorio = archivo.getParent();
        Path temporal = null;

        try {
            Files.createDirectories(directorio);

            temporal = Files.createTempFile(
                    directorio, "cine-", ".tmp");

            try (ObjectOutputStream salida =
                    new ObjectOutputStream(
                            Files.newOutputStream(temporal))) {

                salida.writeObject(estado);
            }

            Files.move(
                    temporal,
                    archivo,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (AtomicMoveNotSupportedException e) {

            throw new ErrorPersistenciaException(
                    "El sistema de archivos no permite "
                    + "reemplazar los datos de forma atómica", e);

        } catch (IOException e) {

            throw new ErrorPersistenciaException(
                    "No se pudo guardar el estado del cine", e);

        } finally {

            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException e) {
                    // No ocultar el resultado del guardado.
                }
            }
        }
    }

    public EstadoSistema cargarEstado() {

        if (Files.notExists(archivo)) {
            return new EstadoSistema();
        }

        try (ObjectInputStream entrada =
                new ObjectInputStream(
                        Files.newInputStream(archivo))) {

            Object objeto = entrada.readObject();

            if (!(objeto instanceof EstadoSistema)) {
                throw new ErrorPersistenciaException(
                        "El archivo no contiene un EstadoSistema");
            }

            return (EstadoSistema) objeto;

        } catch (IOException | ClassNotFoundException e) {

            throw new ErrorPersistenciaException(
                    "No se pudo recuperar el estado del cine", e);
        }
    }
}
