package modelo;

public class PruebaPelicula {

    public static void main(String[] args) {

    try {
        Pelicula pelicula = new Pelicula(1, "Avatar", -20);
        System.out.println("Película creada correctamente");

    } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
    }

    System.out.println("El programa continúa ejecutándose");
}
}