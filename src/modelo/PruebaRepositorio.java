
package modelo;

public class PruebaRepositorio {

    public static void main(String[] args) {

        EstadoSistema estado = new EstadoSistema();

        Repositorio<Pelicula> peliculas = new Repositorio<>();

        Pelicula p1 = new Pelicula(
                estado.generarIdPelicula(), "Avatar", 162);

        Pelicula p2 = new Pelicula(
                estado.generarIdPelicula(), "Titanic", 195);

        peliculas.agregar(p1);
        peliculas.agregar(p2);

        for (Pelicula pelicula : peliculas.listar()) {
            System.out.println(
                    pelicula.getId() + " - " + pelicula.getTitulo()
            );
        }
    }
}
