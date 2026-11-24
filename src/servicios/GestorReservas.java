
package servicios;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelo.Butaca;
import modelo.Cliente;
import modelo.Entrada;
import modelo.EntradaEstudiante;
import modelo.EntradaGeneral;
import modelo.EstadoSistema;
import modelo.Funcion;
import modelo.Repositorio;
import modelo.Reserva;
import modelo.SeleccionEntrada;
import modelo.TipoEntrada;

public class GestorReservas {

    private final EstadoSistema estado;
    private final Repositorio<Reserva> reservas;
    private final Repositorio<Funcion> funciones;
    private final Repositorio<Cliente> clientes;

    public GestorReservas(EstadoSistema estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del sistema no puede ser null");
        }

        this.estado = estado;
        this.reservas = estado.getReservas();
        this.funciones = estado.getFunciones();
        this.clientes = estado.getClientes();
    }

    public Reserva confirmarReserva(
            int idCliente,
            int idFuncion,
            List<SeleccionEntrada> selecciones) {

        Cliente cliente = buscarCliente(idCliente);
        Funcion funcion = buscarFuncion(idFuncion);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe");
        }

        if (funcion == null) {
            throw new IllegalArgumentException(
                    "La función no existe");
        }

        if (!funcion.estaActiva()
                || !funcion.getPelicula().estaActiva()
                || !funcion.getSala().estaActiva()) {

            throw new IllegalArgumentException(
                    "La función no está habilitada");
        }

        if (!funcion.getFechaHora().isAfter(
                LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "La función ya comenzó");
        }

        if (selecciones == null || selecciones.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar al menos una butaca");
        }

        Set<Butaca> butacasSeleccionadas = new HashSet<>();

        for (SeleccionEntrada seleccion : selecciones) {

            if (seleccion == null) {
                throw new IllegalArgumentException(
                        "Hay una selección nula");
            }

            Butaca butaca = seleccion.getButaca();

            if (seleccion.getTipo() == null) {
                throw new IllegalArgumentException(
                        "El tipo de entrada es obligatorio");
            }

            if (!funcion.getSala().getButacas()
                    .contains(butaca)) {

                throw new IllegalArgumentException(
                        "La butaca no pertenece a la sala");
            }

            if (!butacasSeleccionadas.add(butaca)) {
                throw new IllegalArgumentException(
                        "Se seleccionó una butaca repetida");
            }

            if (!estaDisponible(funcion, butaca)) {
                throw new IllegalArgumentException(
                        "La butaca ya está reservada");
            }
        }

        List<Entrada> entradas = new ArrayList<>();

        double precioBase = funcion.getPrecioBase();

        for (SeleccionEntrada seleccion : selecciones) {

            int idEntrada = estado.generarIdEntrada();

            Entrada entrada;

            if (seleccion.getTipo() == TipoEntrada.GENERAL) {

                entrada = new EntradaGeneral(
                        idEntrada,
                        seleccion.getButaca(),
                        precioBase
                );

            } else if (seleccion.getTipo()
                    == TipoEntrada.ESTUDIANTE) {

                entrada = new EntradaEstudiante(
                        idEntrada,
                        seleccion.getButaca(),
                        precioBase
                );

            } else {
                throw new IllegalArgumentException(
                        "Tipo de entrada no admitido");
            }

            entradas.add(entrada);
        }

        int idReserva = estado.generarIdReserva();

        Reserva reserva = new Reserva(
                idReserva,
                cliente,
                funcion,
                entradas
        );

        reservas.agregar(reserva);

        return reserva;
    }

    public boolean estaDisponible(
            Funcion funcion, Butaca butaca) {

        if (funcion == null || butaca == null) {
            return false;
        }

        if (!funciones.listar().contains(funcion)
                || !funcion.getSala().getButacas()
                        .contains(butaca)) {
            return false;
        }

        for (Reserva reserva : reservas.listar()) {

            if (!reserva.estaActiva()) {
                continue;
            }

            if (!reserva.getFuncion().equals(funcion)) {
                continue;
            }

            for (Entrada entrada : reserva.getEntradas()) {

                if (entrada.getButaca().equals(butaca)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void cancelarReserva(int idReserva) {

        Reserva reserva = buscarPorId(idReserva);

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no existe");
        }

        reserva.cancelar();
    }

    public Reserva buscarPorId(int id) {

        for (Reserva reserva : reservas.listar()) {

            if (reserva.getId() == id) {
                return reserva;
            }
        }

        return null;
    }

    public List<Reserva> listarReservas() {
        return reservas.listar();
    }

    private Cliente buscarCliente(int id) {

        for (Cliente cliente : clientes.listar()) {

            if (cliente.getId() == id) {
                return cliente;
            }
        }

        return null;
    }

    private Funcion buscarFuncion(int id) {

        for (Funcion funcion : funciones.listar()) {

            if (funcion.getId() == id) {
                return funcion;
            }
        }

        return null;
    }
}
