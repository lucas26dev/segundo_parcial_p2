
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Repositorio<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> elementos;

    public Repositorio() {
        this.elementos = new ArrayList<>();
    }

    
    public void agregar(T elemento) {

        if (elemento == null) {
            throw new IllegalArgumentException(
                    "No se puede agregar un elemento nulo"
            );
        }

        if (elementos.contains(elemento)) {
            throw new IllegalArgumentException(
                    "El elemento ya existe en el repositorio"
            );
        }

        elementos.add(elemento);
    }


    public List<T> listar() {
        return new ArrayList<>(elementos);
    }

    public boolean eliminar(T elemento) {
        return elementos.remove(elemento);
    }
}
