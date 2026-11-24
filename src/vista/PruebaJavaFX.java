
package vista;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PruebaJavaFX extends Application {

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("Sistema de Reservas de Cine");

        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.getChildren().add(titulo);

        Scene escena = new Scene(contenedor, 500, 300);

        stage.setTitle("Sistema de Reservas de Cine");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
