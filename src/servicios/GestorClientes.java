
package servicios;

import java.util.List;
import modelo.Cliente;
import modelo.EstadoSistema;
import modelo.Repositorio;

public class GestorClientes {

    private final EstadoSistema estado;
    private final Repositorio<Cliente> clientes;

    public GestorClientes(EstadoSistema estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del sistema no puede ser null");
        }

        this.estado = estado;
        this.clientes = estado.getClientes();
    }

    public Cliente registrarCliente(
            int dni,
            String nombre,
            String apellido,
            String telefono) {

        if (dni <= 0) {
            throw new IllegalArgumentException(
                    "El DNI debe ser positivo");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException(
                    "El apellido es obligatorio");
        }

        if (buscarPorDni(dni) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con ese DNI");
        }

        int id = estado.generarIdCliente();

        Cliente cliente = new Cliente(
                id, dni, nombre, apellido, telefono);

        clientes.agregar(cliente);

        return cliente;
    }

    public Cliente buscarPorDni(int dni) {

        for (Cliente cliente : clientes.listar()) {

            if (cliente.getDni() == dni) {
                return cliente;
            }
        }

        return null;
    }

    public Cliente buscarPorId(int id) {

        for (Cliente cliente : clientes.listar()) {

            if (cliente.getId() == id) {
                return cliente;
            }
        }

        return null;
    }

    public List<Cliente> listarClientes() {
        return clientes.listar();
    }

    public void actualizarCliente(
            int id,
            String nombre,
            String apellido,
            String telefono) {

        Cliente cliente = buscarPorId(id);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "No existe un cliente con ID: " + id);
        }

        cliente.actualizarDatos(
                nombre, apellido, telefono);
    }
}
