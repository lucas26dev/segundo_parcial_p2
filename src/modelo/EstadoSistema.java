
package modelo;

import java.io.Serializable;

public class EstadoSistema implements Serializable {

    private static final long serialVersionUID = 1L;

    //repos
    private final Repositorio<Pelicula> peliculas;
    private final Repositorio<Sala> salas;
    private final Repositorio<Funcion> funciones;
    private final Repositorio<Cliente> clientes;
    private final Repositorio<Reserva> reservas;

    
    private int siguienteIdPelicula;
    private int siguienteIdSala;
    private int siguienteIdFuncion;
    private int siguienteIdCliente;
    private int siguienteIdReserva;
    private int siguienteIdEntrada;

    
    public EstadoSistema() {

        this.peliculas = new Repositorio<>();
        this.salas = new Repositorio<>();

        this.siguienteIdPelicula = 1;
        this.siguienteIdSala = 1;
        this.siguienteIdFuncion = 1;
        this.siguienteIdCliente = 1;
        this.siguienteIdReserva = 1;
        this.siguienteIdEntrada = 1;
        this.clientes = new Repositorio<>();
        this.reservas = new Repositorio<>();
        this.funciones = new Repositorio<>();
    }

    
    public Repositorio<Pelicula> getPeliculas() {
        return peliculas;
    }

    public Repositorio<Sala> getSalas() {
        return salas;
    }
    
    public Repositorio<Funcion> getFunciones() {
        return funciones;
    }
    
    public Repositorio<Reserva> getReservas() {
        return reservas;
    }
    
    public Repositorio<Cliente> getClientes() {
        return clientes;
    }

    // Generadore  id
    public int generarIdPelicula() {
        return siguienteIdPelicula++;
    }

    public int generarIdSala() {
        return siguienteIdSala++;
    }

    public int generarIdFuncion() {
        return siguienteIdFuncion++;
    }

    public int generarIdCliente() {
        return siguienteIdCliente++;
    }

    public int generarIdReserva() {
        return siguienteIdReserva++;
    }

    public int generarIdEntrada() {
        return siguienteIdEntrada++;
    }
}
