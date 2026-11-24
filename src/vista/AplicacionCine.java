package vista;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.EstadoSistema;
import modelo.Pelicula;
import persistencia.ErrorPersistenciaException;
import persistencia.PersistenciaArchivo;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import servicios.GestorPeliculas;
import servicios.GestorSalas;
import servicios.GestorFunciones;
import servicios.GestorClientes;
import servicios.GestorReservas;
import modelo.Sala;
import java.time.LocalTime;
import java.time.DateTimeException;
import javafx.scene.control.DatePicker;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import modelo.Funcion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import modelo.Butaca;
import javafx.stage.FileChooser;
import persistencia.ExportadorReservasCsv;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import modelo.Cliente;
import modelo.Entrada;
import modelo.Reserva;
import modelo.SeleccionEntrada;
import modelo.TipoEntrada;



public class AplicacionCine extends Application {

    private EstadoSistema estado;
    private PersistenciaArchivo persistencia;
    private GestorPeliculas gestorPeliculas;
    private GestorSalas gestorSalas;
    private GestorFunciones gestorFunciones;
    private GestorClientes gestorClientes;
    private GestorReservas gestorReservas;

    private TextField campoTitulo;
    private TextField campoDuracion;
    private ListView<String> listaPeliculas;
    private Button botonEstado;
    private TextField campoFilas;
    private TextField campoButacasPorFila;
    private ListView<String> listaSalas;
    private Button botonEstadoSala;
    private ComboBox<Pelicula> comboPeliculas;
    private ComboBox<Sala> comboSalas;
    private DatePicker campoFechaFuncion;
    private TextField campoHoraFuncion;
    private TextField campoPrecioFuncion;
    private ComboBox<Funcion> comboFuncionesReserva;
    private GridPane grillaReservas;
    private Label etiquetaSeleccion;
    private final Set<Butaca> butacasSeleccionadas =
            new HashSet<>();
    private ListView<String> listaFunciones;   
    private TextField campoBusquedaReserva;
    private ListView<Reserva> listaConsultaReservas;
    private Button botonVerDetalleReserva;
    private Button botonCancelarReserva;
    private ComboBox<Pelicula> filtroPelicula;
    private DatePicker filtroFecha; 

    
    
    @Override
    public void start(Stage stage) {

        persistencia = new PersistenciaArchivo(
                Path.of("datos", "cine.dat")
        );

        try {
            estado = persistencia.cargarEstado();

        } catch (ErrorPersistenciaException e) {
            mostrarError(
                    "Error al iniciar",
                    "No se pudieron recuperar los datos: "
                    + e.getMessage()
            );

            Platform.exit();
            return;
        }

        inicializarGestores();

        Label titulo = new Label(
                "Administración de películas"
        );

        campoTitulo = new TextField();
        campoTitulo.setPromptText("Título de la película");

        campoDuracion = new TextField();
        campoDuracion.setPromptText("Duración en minutos");

        Button botonRegistrar = new Button(
                "Registrar película"
        );
        
        Button botonModificar = new Button(
                "Guardar cambios"
        );
         
        
        botonEstado = new Button("Desactivar película");

        botonEstado.setDisable(true);
        
        botonRegistrar.setOnAction(
                evento -> registrarPelicula()
        );

        
        botonEstado.setOnAction(
                evento -> cambiarEstadoPelicula()
        );



        botonModificar.setOnAction(
                evento -> modificarPelicula()
        );


        listaPeliculas = new ListView<>();
        listaPeliculas.setPrefHeight(250);


        VBox contenedor = new VBox(15);

        contenedor.setPadding(new Insets(25));

        contenedor.getChildren().addAll(
                titulo,
                new Label("Título:"),
                campoTitulo,
                new Label("Duración (minutos):"),
                campoDuracion,
                botonRegistrar,
                botonModificar,
                botonEstado,
                new Label("Películas registradas:"),
                listaPeliculas
        );
        
        
    
    listaPeliculas.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, anterior, seleccionado) -> {

                if (seleccionado == null) {
                    botonEstado.setDisable(true);
                    botonEstado.setText("Desactivar película");
                    return;
                }

                int id = Integer.parseInt(
                        seleccionado.split(" - ", 2)[0]
                );

                Pelicula pelicula = gestorPeliculas.buscarPorId(id);

                if (pelicula != null) {

                    campoTitulo.setText(pelicula.getTitulo());

                    campoDuracion.setText(
                            String.valueOf(pelicula.getDuracion())
                    );

                    botonEstado.setDisable(false);

                    if (pelicula.estaActiva()) {
                        botonEstado.setText("Desactivar película");
                    } else {
                        botonEstado.setText("Reactivar película");
                    }
                } else {
                    botonEstado.setDisable(true);
                }
            });


        
        actualizarListado();

        
        campoFilas = new TextField();
        campoFilas.setPromptText("Ejemplo: 4");

        campoButacasPorFila = new TextField();
        campoButacasPorFila.setPromptText("Ejemplo: 6");

        Button botonRegistrarSala = new Button("Registrar sala");
        
        
        Button botonVerDistribucion =
                new Button("Ver distribución");

        botonVerDistribucion.setOnAction(
                evento -> verDistribucionSala()
        );


        botonRegistrarSala.setOnAction(
                evento -> registrarSala()
        );

        listaSalas = new ListView<>();
        listaSalas.setPrefHeight(250);  
        
        
        listaSalas.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado == null) {
                        botonEstadoSala.setDisable(true);
                        botonEstadoSala.setText("Desactivar sala");
                        return;
                    }

                    int id = obtenerIdSala(seleccionado);

                    Sala sala = gestorSalas.buscarPorId(id);

                    if (sala == null) {
                        botonEstadoSala.setDisable(true);
                        return;
                    }

                    botonEstadoSala.setDisable(false);

                    botonEstadoSala.setText(
                            sala.estaActiva()
                                    ? "Desactivar sala"
                                    : "Reactivar sala"
                    );
                });

        
        botonEstadoSala = new Button("Desactivar sala");

        botonEstadoSala.setDisable(true);

        botonEstadoSala.setOnAction(
                evento -> cambiarEstadoSala()
        );


        VBox panelSalas = new VBox(15);
        panelSalas.setPadding(new Insets(25));

        panelSalas.getChildren().addAll(
                new Label("Administración de salas"),
                new Label("Cantidad de filas:"),
                campoFilas,
                new Label("Butacas por fila:"),
                campoButacasPorFila,
                botonRegistrarSala,
                new Label("Salas registradas:"),
                listaSalas,
                botonVerDistribucion,
                botonEstadoSala
        );
        
        
        comboPeliculas = new ComboBox<>();
        comboPeliculas.setPromptText("Seleccionar película");
        comboPeliculas.setMaxWidth(Double.MAX_VALUE);

        comboSalas = new ComboBox<>();
        comboSalas.setPromptText("Seleccionar sala");
        comboSalas.setMaxWidth(Double.MAX_VALUE);

        campoFechaFuncion = new DatePicker();
        campoFechaFuncion.setMaxWidth(Double.MAX_VALUE);

        campoHoraFuncion = new TextField();
        campoHoraFuncion.setPromptText("Ejemplo: 19:30");

        campoPrecioFuncion = new TextField();
        campoPrecioFuncion.setPromptText("Ejemplo: 5000");

        Button botonProgramarFuncion =
                new Button("Programar función");

        botonProgramarFuncion.setOnAction(
                evento -> programarFuncion()
        );

        listaFunciones = new ListView<>();
        listaFunciones.setPrefHeight(230);


        Button botonModificarPrecio =
                new Button("Modificar precio");

        Button botonDesactivarFuncion =
                new Button("Desactivar función");

        botonModificarPrecio.setDisable(true);
        botonDesactivarFuncion.setDisable(true);

        botonModificarPrecio.setOnAction(
                evento -> modificarPrecioFuncion()
        );

        botonDesactivarFuncion.setOnAction(
                evento -> desactivarFuncion()
        );

        listaFunciones.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) -> {

                    boolean haySeleccion = seleccionado != null;

                    botonModificarPrecio.setDisable(!haySeleccion);

                    if (!haySeleccion) {
                        botonDesactivarFuncion.setDisable(true);
                        return;
                    }

                    int id = obtenerIdFuncion(seleccionado);

                    Funcion funcion = gestorFunciones.buscarPorId(id);

                    if (funcion == null) {
                        botonDesactivarFuncion.setDisable(true);
                        return;
                    }

                    campoPrecioFuncion.setText(
                            String.valueOf(funcion.getPrecioBase())
                    );

                    botonDesactivarFuncion.setDisable(
                            !funcion.estaActiva()
                    );
                });


        VBox panelFunciones = new VBox(12);
        panelFunciones.setPadding(new Insets(25));

        panelFunciones.getChildren().addAll(
                new Label("Programación de funciones"),
                new Label("Película:"),
                comboPeliculas,
                new Label("Sala:"),
                comboSalas,
                new Label("Fecha:"),
                campoFechaFuncion,
                new Label("Hora (HH:mm):"),
                campoHoraFuncion,
                new Label("Precio base:"),
                campoPrecioFuncion,
                botonProgramarFuncion,
                new Label("Funciones registradas:"),
                listaFunciones,
                botonModificarPrecio,
                botonDesactivarFuncion
        );

        
    filtroPelicula = new ComboBox<>();
    filtroPelicula.setPromptText("Todas las películas");
    filtroPelicula.setMaxWidth(Double.MAX_VALUE);

    filtroPelicula.setConverter(
            new StringConverter<Pelicula>() {

                @Override
                public String toString(Pelicula pelicula) {

                    if (pelicula == null) {
                        return "";
                    }

                    return pelicula.getTitulo();
                }

                @Override
                public Pelicula fromString(String texto) {
                    return null;
                }
            }
    );

    filtroFecha = new DatePicker();
    filtroFecha.setPromptText("Todas las fechas");
    filtroFecha.setMaxWidth(Double.MAX_VALUE);

    Button botonLimpiarFiltros =
            new Button("Limpiar filtros");

    botonLimpiarFiltros.setOnAction(evento -> {
        filtroPelicula.setValue(null);
        filtroFecha.setValue(null);
        actualizarFuncionesReserva();
    });

    filtroPelicula.setOnAction(
            evento -> actualizarFuncionesReserva()
    );

    filtroFecha.setOnAction(
            evento -> actualizarFuncionesReserva()
    );

        
        comboFuncionesReserva = new ComboBox<>();

        comboFuncionesReserva.setPromptText(
                "Seleccionar una función"
        );

        comboFuncionesReserva.setMaxWidth(Double.MAX_VALUE);

        comboFuncionesReserva.setConverter(
                new StringConverter<Funcion>() {

                    @Override
                    public String toString(Funcion funcion) {

                        if (funcion == null) {
                            return "";
                        }

                        return funcion.getPelicula().getTitulo()
                                + " - Sala "
                                + funcion.getSala().getId()
                                + " - "
                                + funcion.getFechaHora();
                    }

                    @Override
                    public Funcion fromString(String texto) {
                        return null;
                    }
                }
        );

        grillaReservas = new GridPane();

        grillaReservas.setHgap(8);
        grillaReservas.setVgap(8);
        grillaReservas.setAlignment(Pos.CENTER);

        etiquetaSeleccion = new Label(
                "Butacas seleccionadas: 0"
        );

        Label etiquetaPantalla = new Label("PANTALLA");

        HBox encabezadoPantalla = new HBox(etiquetaPantalla);
        encabezadoPantalla.setAlignment(Pos.CENTER);

        ScrollPane desplazamiento =
                new ScrollPane(grillaReservas);

        desplazamiento.setFitToWidth(true);
        desplazamiento.setPrefHeight(330);

        VBox panelReservas = new VBox(15);
        panelReservas.setPadding(new Insets(25));

        
        panelReservas.getChildren().addAll(
                new Label("Cartelera y nueva reserva"),

                new Label("Filtrar por película:"),
                filtroPelicula,

                new Label("Filtrar por fecha:"),
                filtroFecha,

                botonLimpiarFiltros,

                new Label("Seleccionar función:"),
                comboFuncionesReserva,

                encabezadoPantalla,
                desplazamiento,

                new Label(
                        "Verde: disponible | Azul: seleccionada "
                        + "| Gris: ocupada"
                ),

                etiquetaSeleccion
        );

        
        
        Button botonContinuarReserva =
                new Button("Continuar con la reserva");

        botonContinuarReserva.setOnAction(
                evento -> continuarReserva()
        );

        panelReservas.getChildren().add(botonContinuarReserva);


        comboFuncionesReserva.setOnAction(
                evento -> mostrarButacasFuncion()
        );
        
        
        campoBusquedaReserva = new TextField();
        campoBusquedaReserva.setPromptText(
                "Ingresá un código de reserva o DNI"
        );

        Button botonBuscarCodigo = new Button(
                "Buscar por código"
        );

        Button botonBuscarDni = new Button(
                "Buscar por DNI"
        );

        botonBuscarCodigo.setOnAction(
                evento -> buscarReservaPorCodigo()
        );

        botonBuscarDni.setOnAction(
                evento -> buscarReservasPorDni()
        );

        HBox botonesBusqueda = new HBox(10);
        botonesBusqueda.getChildren().addAll(
                botonBuscarCodigo,
                botonBuscarDni
        );

        listaConsultaReservas = new ListView<>();
        listaConsultaReservas.setPrefHeight(300);

        listaConsultaReservas.setCellFactory(
                lista -> new javafx.scene.control.ListCell<Reserva>() {

                    @Override
                    protected void updateItem(
                            Reserva reserva, boolean vacia) {

                        super.updateItem(reserva, vacia);

                        if (vacia || reserva == null) {
                            setText(null);
                            return;
                        }

                        String detalle =
                                "Reserva " + reserva.getId()
                                + " - "
                                + reserva.getCliente().getNombre()
                                + " "
                                + reserva.getCliente().getApellido()
                                + " - "
                                + reserva.getFuncion().getPelicula().getTitulo()
                                + " - "
                                + reserva.getFuncion().getFechaHora()
                                + " - "
                                + (reserva.estaActiva()
                                        ? "ACTIVA"
                                        : "CANCELADA");


                        setText(detalle);
                    }
                }
        );

        botonVerDetalleReserva = new Button(
                "Ver detalle"
        );

        botonCancelarReserva = new Button(
                "Cancelar reserva"
        );

        botonVerDetalleReserva.setDisable(true);
        botonCancelarReserva.setDisable(true);

        botonVerDetalleReserva.setOnAction(
                evento -> verDetalleReserva()
        );

        botonCancelarReserva.setOnAction(
                evento -> cancelarReservaSeleccionada()
        );

        listaConsultaReservas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionada) -> {

                    botonVerDetalleReserva.setDisable(
                            seleccionada == null
                    );

                    botonCancelarReserva.setDisable(
                            seleccionada == null
                            || !seleccionada.estaActiva()
                    );
                });

        HBox botonesReserva = new HBox(10);

        botonesReserva.getChildren().addAll(
                botonVerDetalleReserva,
                botonCancelarReserva
        );

        
        Button botonExportarCsv =
                new Button("Exportar reservas a CSV");

        botonExportarCsv.setOnAction(
                evento -> exportarReservasCsv(stage)
        );

        VBox panelConsultaReservas = new VBox(15);
        panelConsultaReservas.setPadding(new Insets(25));
       
        panelConsultaReservas.getChildren().addAll(
                new Label("Consulta de reservas"),
                new Label("Código de reserva o DNI:"),
                campoBusquedaReserva,
                botonesBusqueda,
                new Label("Reservas encontradas:"),
                listaConsultaReservas,
                botonesReserva,
                botonExportarCsv
        );
        
        Tab pestañaPeliculas =
                new Tab("Películas", contenedor);
        pestañaPeliculas.setClosable(false);

        Tab pestañaSalas =
                new Tab("Salas", panelSalas);
        pestañaSalas.setClosable(false);

        Tab pestañaFunciones =
                new Tab("Funciones", panelFunciones);
        pestañaFunciones.setClosable(false);

        TabPane pestañas = new TabPane();
        
        Tab pestañaReservas =
                new Tab("Reservas", panelReservas);
        pestañaReservas.setClosable(false); 
        
        Tab pestañaConsultaReservas =
                new Tab("Consultar reservas", panelConsultaReservas);

        pestañaConsultaReservas.setClosable(false); 
            
        
        pestañas.getTabs().addAll(
                pestañaPeliculas,
                pestañaSalas,
                pestañaFunciones,
                pestañaReservas,
                pestañaConsultaReservas
        );

        configurarCombosFunciones();
        actualizarOpcionesFunciones();
        actualizarListadoFunciones();
        
        
        
        pestañaConsultaReservas.setOnSelectionChanged(evento -> {

            if (pestañaConsultaReservas.isSelected()) {
                actualizarConsultaReservas();
            }
        });
     
        pestañaReservas.setOnSelectionChanged(evento -> {

            if (pestañaReservas.isSelected()) {

                filtroPelicula.setValue(null);
                filtroFecha.setValue(null);

                actualizarPeliculasFiltro();
                actualizarFuncionesReserva();
            }
        });



        pestañaFunciones.setOnSelectionChanged(evento -> {

            if (pestañaFunciones.isSelected()) {
                actualizarOpcionesFunciones();
                actualizarListadoFunciones();
            }
        });

        actualizarListadoSalas();

        Scene escena = new Scene(pestañas, 650, 600);


        stage.setTitle("Sistema de Reservas de Cine");
        stage.setScene(escena);

        stage.setOnCloseRequest(evento -> {
            try {
                persistencia.guardarEstado(estado);

            } catch (ErrorPersistenciaException e) {
                evento.consume();

                mostrarError(
                        "Error al cerrar",
                        "No se pudo guardar el estado: "
                        + e.getMessage()
                );
            }
        });

        stage.show();
    }
    
    
    private void actualizarOpcionesFunciones() {

        comboPeliculas.getItems().clear();
        comboSalas.getItems().clear();

        for (Pelicula pelicula
                : gestorPeliculas.listarPeliculas()) {

            if (pelicula.estaActiva()) {
                comboPeliculas.getItems().add(pelicula);
            }
        }

        for (Sala sala : gestorSalas.listarSalas()) {

            if (sala.estaActiva()) {
                comboSalas.getItems().add(sala);
            }
        }
    }

    
    private void configurarCombosFunciones() {

        comboPeliculas.setConverter(
                new StringConverter<Pelicula>() {

                    @Override
                    public String toString(Pelicula pelicula) {

                        if (pelicula == null) {
                            return "";
                        }

                        return pelicula.getTitulo()
                                + " - "
                                + pelicula.getDuracion()
                                + " min";
                    }

                    @Override
                    public Pelicula fromString(String texto) {
                        return null;
                    }
                }
        );

        comboSalas.setConverter(
                new StringConverter<Sala>() {

                    @Override
                    public String toString(Sala sala) {

                        if (sala == null) {
                            return "";
                        }

                        return "Sala " + sala.getId()
                                + " - "
                                + sala.getCapacidad()
                                + " butacas";
                    }

                    @Override
                    public Sala fromString(String texto) {
                        return null;
                    }
                }
        );
    }

    
    
    private int obtenerIdSala(String texto) {

        return Integer.parseInt(
                texto.split(" - ", 2)[0]
                        .replace("Sala ", "")
                        .trim()
        );
    }

    
    private void verDistribucionSala() {

        String seleccionado = listaSalas
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Primero seleccioná una sala del listado"
            );
            return;
        }

        int id = obtenerIdSala(seleccionado);

        Sala sala = gestorSalas.buscarPorId(id);

        if (sala == null) {
            mostrarError(
                    "Sala no encontrada",
                    "La sala seleccionada no existe"
            );
            return;
        }

        GridPane grilla = new GridPane();

        grilla.setHgap(10);
        grilla.setVgap(10);
        grilla.setAlignment(Pos.CENTER);

        for (Butaca butaca : sala.getButacas()) {

            Button botonButaca = new Button(
                    butaca.getFila() + butaca.getNumero()
            );

            botonButaca.setPrefSize(60, 40);
            botonButaca.setDisable(true);

            int indice = sala.getButacas().indexOf(butaca);

            int columna = indice % sala.getButacasPorFila();
            int fila = indice / sala.getButacasPorFila();

            grilla.add(
                    botonButaca,
                    columna,
                    fila
            );
        }

        Label pantalla = new Label("PANTALLA");

        VBox contenedor = new VBox(20);

        contenedor.setPadding(new Insets(20));
        contenedor.setAlignment(Pos.CENTER);

        contenedor.getChildren().addAll(
                pantalla,
                grilla
        );

        ScrollPane scroll = new ScrollPane(contenedor);

        scroll.setFitToWidth(true);

        Alert ventana = new Alert(
                Alert.AlertType.INFORMATION
        );

        ventana.setTitle("Distribución de sala");
        ventana.setHeaderText(
                "Sala " + sala.getId()
                + " - Capacidad: "
                + sala.getCapacidad()
        );

        ventana.getDialogPane().setContent(scroll);
        ventana.getDialogPane().setPrefSize(650, 450);

        ventana.showAndWait();
    }

    
    private void actualizarListadoSalas() {

        listaSalas.getItems().clear();

        for (Sala sala : gestorSalas.listarSalas()) {

            String detalle =
                    "Sala " + sala.getId()
                    + " - Filas: " + sala.getCantidadFilas()
                    + " - Butacas por fila: "
                    + sala.getButacasPorFila()
                    + " - Capacidad: " + sala.getCapacidad()
                    + " - "
                    + (sala.estaActiva() ? "ACTIVA" : "INACTIVA");

            listaSalas.getItems().add(detalle);
        }
    }

    
    private void registrarSala() {

        try {
            int filas = Integer.parseInt(
                    campoFilas.getText().trim()
            );

            int butacasPorFila = Integer.parseInt(
                    campoButacasPorFila.getText().trim()
            );

            EstadoSistema candidato = copiarEstado(estado);

            GestorSalas gestorTemporal =
                    new GestorSalas(candidato);

            gestorTemporal.registrarSala(
                    filas,
                    butacasPorFila
            );

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarListadoSalas();

            campoFilas.clear();
            campoButacasPorFila.clear();

            mostrarInformacion(
                    "Sala registrada",
                    "La sala fue registrada y guardada correctamente."
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "Datos inválidos",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    "No se pudo guardar la sala: "
                    + e.getMessage()
            );
        }
    }

    
    
    private void cambiarEstadoPelicula() {

        String seleccionado = listaPeliculas
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una película del listado"
            );
            return;
        }

        int id = Integer.parseInt(
                seleccionado.split(" - ", 2)[0]
        );

        Pelicula pelicula = gestorPeliculas.buscarPorId(id);

        if (pelicula == null) {
            mostrarError(
                    "Película no encontrada",
                    "La película seleccionada no existe"
            );
            return;
        }

        boolean desactivar = pelicula.estaActiva();

        String accion = desactivar
                ? "desactivar"
                : "reactivar";

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Cambiar estado");
        confirmacion.setHeaderText(
                "¿Querés " + accion + " esta película?"
        );

        confirmacion.setContentText(
                "Película: " + pelicula.getTitulo()
        );

        if (confirmacion.showAndWait().orElse(
                javafx.scene.control.ButtonType.CANCEL)
                != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {

            EstadoSistema candidato = copiarEstado(estado);

            GestorPeliculas gestorTemporal =
                    new GestorPeliculas(candidato);

            if (desactivar) {
                gestorTemporal.desactivarPelicula(id);
            } else {
                gestorTemporal.reactivarPelicula(id);
            }

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarListado();

            listaPeliculas.getSelectionModel().clearSelection();

            campoTitulo.clear();
            campoDuracion.clear();

            mostrarInformacion(
                    "Operación correcta",
                    "La película fue "
                    + (desactivar ? "desactivada" : "reactivada")
                    + " y guardada."
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "No se pudo cambiar el estado",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    e.getMessage()
            );
        }
    }

    
    private void programarFuncion() {

        Pelicula pelicula = comboPeliculas.getValue();
        Sala sala = comboSalas.getValue();

        if (pelicula == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una película"
            );
            return;
        }

        if (sala == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una sala"
            );
            return;
        }

        if (campoFechaFuncion.getValue() == null) {
            mostrarError(
                    "Fecha obligatoria",
                    "Seleccioná una fecha para la función"
            );
            return;
        }

        try {
            LocalTime hora = LocalTime.parse(
                    campoHoraFuncion.getText().trim()
            );

            LocalDateTime fechaHora =
                    LocalDateTime.of(
                            campoFechaFuncion.getValue(),
                            hora
                    );

            double precio = Double.parseDouble(
                    campoPrecioFuncion.getText().trim()
            );

            EstadoSistema candidato = copiarEstado(estado);

            GestorFunciones gestorTemporal =
                    new GestorFunciones(candidato);

            gestorTemporal.programarFuncion(
                    pelicula.getId(),
                    sala.getId(),
                    fechaHora,
                    precio
            );

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarOpcionesFunciones();
            actualizarListadoFunciones();

            campoFechaFuncion.setValue(null);
            campoHoraFuncion.clear();
            campoPrecioFuncion.clear();

            mostrarInformacion(
                    "Función programada",
                    "La función fue registrada y guardada."
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "Precio inválido",
                    "Ingresá un precio numérico válido"
            );

        } catch (DateTimeException e) {

            mostrarError(
                    "Hora inválida",
                    "Ingresá la hora con formato HH:mm, "
                    + "por ejemplo 19:30"
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "No se pudo programar",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    e.getMessage()
            );
        }
    }

    
    private void actualizarListadoFunciones() {

        listaFunciones.getItems().clear();

        for (Funcion funcion
                : gestorFunciones.listarFunciones()) {

            String detalle =
                    "Función " + funcion.getId()
                    + " - " + funcion.getPelicula().getTitulo()
                    + " - Sala " + funcion.getSala().getId()
                    + " - " + funcion.getFechaHora()
                    + " - $" + funcion.getPrecioBase()
                    + " - "
                    + (funcion.estaActiva()
                            ? "ACTIVA"
                            : "INACTIVA");

            listaFunciones.getItems().add(detalle);
        }
    }

    
    private void modificarPelicula() {

        String seleccionado = listaPeliculas
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Primero seleccioná una película del listado"
            );
            return;
        }

        try {
            int id = Integer.parseInt(
                    seleccionado.split(" - ", 2)[0]
            );

            String nuevoTitulo = campoTitulo.getText();

            int nuevaDuracion = Integer.parseInt(
                    campoDuracion.getText().trim()
            );

            EstadoSistema candidato = copiarEstado(estado);

            GestorPeliculas gestorTemporal =
                    new GestorPeliculas(candidato);

            gestorTemporal.actualizarPelicula(
                    id,
                    nuevoTitulo,
                    nuevaDuracion
            );

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();
            actualizarListado();

            listaPeliculas.getSelectionModel().clearSelection();

            campoTitulo.clear();
            campoDuracion.clear();

            mostrarInformacion(
                    "Modificación correcta",
                    "Los cambios fueron guardados"
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "Datos inválidos",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    e.getMessage()
            );
        }
    }

    

    private void inicializarGestores() {

        gestorPeliculas = new GestorPeliculas(estado);
        gestorSalas = new GestorSalas(estado);
        gestorFunciones = new GestorFunciones(estado);
        gestorClientes = new GestorClientes(estado);
        gestorReservas = new GestorReservas(estado);
    }
    
    
    private void desactivarFuncion() {

        String seleccionado = listaFunciones
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una función"
            );
            return;
        }

        int id = obtenerIdFuncion(seleccionado);

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Desactivar función");

        confirmacion.setHeaderText(
                "¿Querés desactivar la función " + id + "?"
        );

        confirmacion.setContentText(
                "No se permitirá si tiene reservas activas."
        );

        if (confirmacion.showAndWait().orElse(
                javafx.scene.control.ButtonType.CANCEL)
                != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            EstadoSistema candidato = copiarEstado(estado);

            GestorFunciones gestorTemporal =
                    new GestorFunciones(candidato);

            gestorTemporal.desactivarFuncion(id);

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarListadoFunciones();

            mostrarInformacion(
                    "Función desactivada",
                    "La función fue desactivada y guardada"
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "No se pudo desactivar",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    e.getMessage()
            );
        }
    }

    
    private void modificarPrecioFuncion() {

        String seleccionado = listaFunciones
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una función"
            );
            return;
        }

        try {
            int id = obtenerIdFuncion(seleccionado);

            double nuevoPrecio = Double.parseDouble(
                    campoPrecioFuncion.getText().trim()
            );

            EstadoSistema candidato = copiarEstado(estado);

            GestorFunciones gestorTemporal =
                    new GestorFunciones(candidato);

            gestorTemporal.actualizarPrecio(
                    id, nuevoPrecio
            );

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarListadoFunciones();

            campoPrecioFuncion.clear();

            mostrarInformacion(
                    "Precio actualizado",
                    "El nuevo precio fue guardado correctamente"
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "Precio inválido",
                    "Ingresá un número válido"
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "No se pudo modificar el precio",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    e.getMessage()
            );
        }
    }

    
    private int obtenerIdFuncion(String texto) {

        return Integer.parseInt(
                texto.split(" - ", 2)[0]
                        .replace("Función ", "")
                        .trim()
        );
    }

    
    
    private void actualizarEtiquetaSeleccion() {

        etiquetaSeleccion.setText(
                "Butacas seleccionadas: "
                + butacasSeleccionadas.size()
        );
    }
    
        private void mostrarButacasFuncion() {

            grillaReservas.getChildren().clear();
            butacasSeleccionadas.clear();

            actualizarEtiquetaSeleccion();

            Funcion funcion = comboFuncionesReserva.getValue();

            if (funcion == null) {
                return;
            }

            Sala sala = funcion.getSala();

            List<Butaca> butacas = sala.getButacas();

            for (int indice = 0; indice < butacas.size(); indice++) {

                Butaca butaca = butacas.get(indice);

                Button botonButaca = new Button(
                        butaca.getFila() + butaca.getNumero()
                );

                botonButaca.setPrefSize(60, 40);

                boolean disponible = gestorReservas
                        .estaDisponible(funcion, butaca);

                if (disponible) {

                    botonButaca.setStyle(
                            "-fx-background-color: #90EE90;"
                    );

                    botonButaca.setOnAction(evento -> {

                        if (butacasSeleccionadas.contains(butaca)) {

                            butacasSeleccionadas.remove(butaca);

                            botonButaca.setStyle(
                                    "-fx-background-color: #90EE90;"
                            );

                        } else {

                            butacasSeleccionadas.add(butaca);

                            botonButaca.setStyle(
                                    "-fx-background-color: #87CEFA;"
                            );
                        }

                        actualizarEtiquetaSeleccion();
                    });

                } else {

                    botonButaca.setDisable(true);

                    botonButaca.setStyle(
                            "-fx-background-color: #B0B0B0;"
                    );
                }

                int columna = indice % sala.getButacasPorFila();

                int fila = indice / sala.getButacasPorFila();

                grillaReservas.add(
                        botonButaca,
                        columna,
                        fila
                );
            }
        }

    

    private void actualizarFuncionesReserva() {

        comboFuncionesReserva.getItems().clear();

        Pelicula peliculaFiltrada =
                filtroPelicula.getValue();

        java.time.LocalDate fechaFiltrada =
                filtroFecha.getValue();

        for (Funcion funcion
                : gestorFunciones.listarFunciones()) {

            boolean habilitada =
                    funcion.estaActiva()
                    && funcion.getPelicula().estaActiva()
                    && funcion.getSala().estaActiva();

            boolean futura =
                    funcion.getFechaHora()
                            .isAfter(LocalDateTime.now());

            boolean coincidePelicula =
                    peliculaFiltrada == null
                    || funcion.getPelicula().getId()
                            == peliculaFiltrada.getId();

            boolean coincideFecha =
                    fechaFiltrada == null
                    || funcion.getFechaHora()
                            .toLocalDate()
                            .equals(fechaFiltrada);

            if (habilitada
                    && futura
                    && coincidePelicula
                    && coincideFecha) {

                comboFuncionesReserva.getItems()
                        .add(funcion);
            }
        }

        butacasSeleccionadas.clear();

        grillaReservas.getChildren().clear();

        etiquetaSeleccion.setText(
                "Butacas seleccionadas: 0"
        );
    }


    
    private void registrarPelicula() {

        String titulo = campoTitulo.getText();
        String textoDuracion = campoDuracion.getText();

        try {
            int duracion = Integer.parseInt(
                    textoDuracion.trim()
            );

            // Trabajamos sobre una copia del estado.
            EstadoSistema candidato = copiarEstado(estado);

            GestorPeliculas gestorTemporal =
                    new GestorPeliculas(candidato);

            gestorTemporal.registrarPelicula(
                    titulo, duracion
            );

            // Guardamos antes de aceptar el cambio.
            persistencia.guardarEstado(candidato);

            // Solo si se guardó, adoptamos el nuevo estado.
            estado = candidato;
            inicializarGestores();

            actualizarListado();

            campoTitulo.clear();
            campoDuracion.clear();

            mostrarInformacion(
                    "Registro correcto",
                    "La película fue registrada y guardada."
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "Datos inválidos",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    "No se pudo guardar la película: "
                    + e.getMessage()
            );
        }
    }

    private EstadoSistema copiarEstado(
            EstadoSistema original) {

        try {
            ByteArrayOutputStream memoria =
                    new ByteArrayOutputStream();

            try (ObjectOutputStream salida =
                    new ObjectOutputStream(memoria)) {

                salida.writeObject(original);
            }

            byte[] datos = memoria.toByteArray();

            try (ObjectInputStream entrada =
                    new ObjectInputStream(
                            new ByteArrayInputStream(datos))) {

                return (EstadoSistema) entrada.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {

            throw new ErrorPersistenciaException(
                    "No se pudo copiar el estado en memoria", e
            );
        }
    }

    private void actualizarListado() {

        listaPeliculas.getItems().clear();

        for (Pelicula pelicula
                : gestorPeliculas.listarPeliculas()) {
  
                String detalle =
                        pelicula.getId() + " - "
                        + pelicula.getTitulo() + " - "
                        + pelicula.getDuracion() + " min - "
                        + (pelicula.estaActiva()
                                ? "ACTIVA"
                                : "INACTIVA");

            listaPeliculas.getItems().add(detalle);
        }
    }

    private void mostrarError(
            String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(
            String titulo, String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    
    private void actualizarConsultaReservas() {

        listaConsultaReservas.getItems().clear();

        listaConsultaReservas.getItems().addAll(
                gestorReservas.listarReservas()
        );
    }
        
    
    private void exportarReservasCsv(Stage stage) {

        FileChooser selector = new FileChooser();

        selector.setTitle("Exportar reservas");

        selector.setInitialFileName("reservas.csv");

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivos CSV (*.csv)", "*.csv"
                )
        );

        java.io.File archivo = selector.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        try {
            ExportadorReservasCsv exportador =
                    new ExportadorReservasCsv();

            exportador.exportar(
                    gestorReservas.listarReservas(),
                    archivo.toPath()
            );

            mostrarInformacion(
                    "Exportación correcta",
                    "Las reservas se exportaron a:\n"
                    + archivo.getAbsolutePath()
            );

        } catch (IOException | SecurityException e) {

            mostrarError(
                    "Error de exportación",
                    "No se pudo generar el CSV: "
                    + e.getMessage()
            );
        }
    }

    
    private void buscarReservaPorCodigo() {

        try {
            int codigo = Integer.parseInt(
                    campoBusquedaReserva.getText().trim()
            );

            Reserva reserva = gestorReservas.buscarPorId(codigo);

            listaConsultaReservas.getItems().clear();

            if (reserva == null) {
                mostrarError(
                        "Reserva no encontrada",
                        "No existe una reserva con ese código"
                );
                return;
            }

            listaConsultaReservas.getItems().add(reserva);

        } catch (NumberFormatException e) {

            mostrarError(
                    "Código inválido",
                    "Ingresá un código numérico válido"
            );
        }
    }
    
    
    private void buscarReservasPorDni() {

        try {
            int dni = Integer.parseInt(
                    campoBusquedaReserva.getText().trim()
            );

            listaConsultaReservas.getItems().clear();

            for (Reserva reserva
                    : gestorReservas.listarReservas()) {

                if (reserva.getCliente().getDni() == dni) {
                    listaConsultaReservas.getItems().add(reserva);
                }
            }

            if (listaConsultaReservas.getItems().isEmpty()) {

                mostrarError(
                        "Sin resultados",
                        "No se encontraron reservas para ese DNI"
                );
            }

        } catch (NumberFormatException e) {

            mostrarError(
                    "DNI inválido",
                    "Ingresá un DNI numérico válido"
            );
        }
    }

    
    private void continuarReserva() {

        Funcion funcion = comboFuncionesReserva.getValue();

        if (funcion == null) {
            mostrarError(
                    "Función obligatoria",
                    "Primero seleccioná una función"
            );
            return;
        }

        if (butacasSeleccionadas.isEmpty()) {
            mostrarError(
                    "Butacas obligatorias",
                    "Seleccioná al menos una butaca"
            );
            return;
        }

        // Ordenamos las butacas para mostrarlas.
        List<Butaca> seleccionadas =
                new ArrayList<>(butacasSeleccionadas);

        seleccionadas.sort(
                Comparator.comparing(Butaca::getFila)
                        .thenComparingInt(Butaca::getNumero)
        );

        // Campos del cliente.
        TextField campoDni = new TextField();
        campoDni.setPromptText("DNI");

        TextField campoNombre = new TextField();
        campoNombre.setPromptText("Nombre");

        TextField campoApellido = new TextField();
        campoApellido.setPromptText("Apellido");

        TextField campoTelefono = new TextField();
        campoTelefono.setPromptText("Opcional");

        VBox formulario = new VBox(10);

        formulario.getChildren().addAll(
                new Label("Datos del cliente"),
                new Label("DNI:"),
                campoDni,
                new Label("Nombre:"),
                campoNombre,
                new Label("Apellido:"),
                campoApellido,
                new Label("Teléfono:"),
                campoTelefono,
                new Label(
                        "Si el DNI ya existe, se utilizará "
                        + "el cliente registrado."
                ),
                new Label("Tipo de entrada por butaca:")
        );

        // Asociamos cada butaca con su tipo de entrada.
        Map<Butaca, ComboBox<TipoEntrada>> tipos =
                new LinkedHashMap<>();

        for (Butaca butaca : seleccionadas) {

            ComboBox<TipoEntrada> comboTipo =
                    new ComboBox<>();

            comboTipo.getItems().addAll(
                    TipoEntrada.GENERAL,
                    TipoEntrada.ESTUDIANTE
            );

            comboTipo.setValue(TipoEntrada.GENERAL);

            Label etiquetaButaca = new Label(
                    butaca.getFila() + butaca.getNumero()
            );

            HBox fila = new HBox(15);
            fila.setAlignment(Pos.CENTER_LEFT);

            fila.getChildren().addAll(
                    etiquetaButaca,
                    comboTipo
            );

            formulario.getChildren().add(fila);

            tipos.put(butaca, comboTipo);
        }
        
        Label etiquetaResumen = new Label();

        etiquetaResumen.setWrapText(true);

        formulario.getChildren().addAll(
                new Label("Resumen de la reserva:"),
                etiquetaResumen
        );

        for (ComboBox<TipoEntrada> comboTipo : tipos.values()) {

            comboTipo.setOnAction(evento ->
                    actualizarResumenPrecios(
                            funcion,
                            tipos,
                            etiquetaResumen
                    )
            );
        }

        actualizarResumenPrecios(
                funcion,
                tipos,
                etiquetaResumen
        );

        
        ScrollPane scroll = new ScrollPane(formulario);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(450);

        Dialog<ButtonType> dialogo = new Dialog<>();

        dialogo.setTitle("Confirmar reserva");

        dialogo.getDialogPane().setContent(scroll);

        ButtonType botonConfirmar = new ButtonType(
                "Confirmar reserva",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE
        );

        dialogo.getDialogPane().getButtonTypes().addAll(
                botonConfirmar,
                ButtonType.CANCEL
        );

        dialogo.getDialogPane().setPrefSize(550, 550);

        ButtonType resultado =
                dialogo.showAndWait().orElse(ButtonType.CANCEL);

        if (resultado != botonConfirmar) {
            return;
        }

        confirmarReserva(
                funcion.getId(),
                seleccionadas,
                tipos,
                campoDni.getText(),
                campoNombre.getText(),
                campoApellido.getText(),
                campoTelefono.getText()
        );
    }
    
    
    
    private void actualizarResumenPrecios(
            Funcion funcion,
            Map<Butaca, ComboBox<TipoEntrada>> tipos,
            Label etiquetaResumen) {

        double precioBase = funcion.getPrecioBase();
        double total = 0;

        StringBuilder resumen = new StringBuilder();

        for (Map.Entry<Butaca, ComboBox<TipoEntrada>> dato
                : tipos.entrySet()) {

            Butaca butaca = dato.getKey();
            TipoEntrada tipo = dato.getValue().getValue();

            Entrada entrada;

            if (tipo == TipoEntrada.GENERAL) {

                entrada = new modelo.EntradaGeneral(
                        1, butaca, precioBase
                );

            } else if (tipo == TipoEntrada.ESTUDIANTE) {

                entrada = new modelo.EntradaEstudiante(
                        1, butaca, precioBase
                );

            } else {
                throw new IllegalArgumentException(
                        "Tipo de entrada no admitido"
                );
            }

            double precio = entrada.calcularPrecio(precioBase);

            total += precio;

            resumen.append(butaca.getFila())
                    .append(butaca.getNumero())
                    .append(" - ")
                    .append(tipo)
                    .append(": $")
                    .append(String.format("%.2f", precio))
                    .append("\n");
        }

        resumen.append("\nTOTAL: $")
                .append(String.format("%.2f", total));

        etiquetaResumen.setText(resumen.toString());
    }


    
    private void confirmarReserva(
            int idFuncion,
            List<Butaca> butacas,
            Map<Butaca, ComboBox<TipoEntrada>> tipos,
            String textoDni,
            String nombre,
            String apellido,
            String telefono) {

        try {
            int dni = Integer.parseInt(textoDni.trim());

            if (dni <= 0) {
                throw new IllegalArgumentException(
                        "El DNI debe ser un número positivo"
                );
            }

            // Copia independiente para no alterar
            // el estado original si algo falla.
            EstadoSistema candidato = copiarEstado(estado);

            GestorClientes clientesTemporal =
                    new GestorClientes(candidato);

            GestorReservas reservasTemporal =
                    new GestorReservas(candidato);

            // Buscar si el cliente ya está registrado.
            Cliente cliente = clientesTemporal.buscarPorDni(dni);

            if (cliente == null) {

                cliente = clientesTemporal.registrarCliente(
                        dni,
                        nombre,
                        apellido,
                        telefono
                );
            }

            // Construir las selecciones de entradas.
            List<SeleccionEntrada> selecciones =
                    new ArrayList<>();

            for (Butaca butaca : butacas) {

                TipoEntrada tipo =
                        tipos.get(butaca).getValue();

                SeleccionEntrada seleccion =
                        new SeleccionEntrada(butaca, tipo);

                selecciones.add(seleccion);
            }

            // Confirmación real en el gestor.
            Reserva reserva =
                    reservasTemporal.confirmarReserva(
                            cliente.getId(),
                            idFuncion,
                            selecciones
                    );

            // Primero guardamos.
            persistencia.guardarEstado(candidato);

            // Después aceptamos el estado nuevo.
            estado = candidato;
            inicializarGestores();

            // Actualizar el plano y las opciones.
            actualizarFuncionesReserva();

            Funcion funcionActualizada =
                    gestorFunciones.buscarPorId(idFuncion);

            if (funcionActualizada != null
                    && comboFuncionesReserva.getItems()
                            .contains(funcionActualizada)) {

                comboFuncionesReserva.setValue(
                        funcionActualizada
                );

                mostrarButacasFuncion();
            }

            mostrarComprobante(reserva);

        } catch (NumberFormatException e) {

            mostrarError(
                    "DNI inválido",
                    "Ingresá un DNI numérico válido"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            mostrarError(
                    "No se pudo confirmar la reserva",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    "No se pudo guardar la reserva: "
                    + e.getMessage()
            );
        }
    }

    
    private void actualizarPeliculasFiltro() {

        Map<Integer, Pelicula> peliculas =
                new LinkedHashMap<>();

        for (Funcion funcion
                : gestorFunciones.listarFunciones()) {

            boolean disponible =
                    funcion.estaActiva()
                    && funcion.getPelicula().estaActiva()
                    && funcion.getSala().estaActiva()
                    && funcion.getFechaHora()
                            .isAfter(LocalDateTime.now());

            if (disponible) {
                Pelicula pelicula = funcion.getPelicula();

                peliculas.put(
                        pelicula.getId(),
                        pelicula
                );
            }
        }

        filtroPelicula.getItems().setAll(
                peliculas.values()
        );
    }

    
    private void mostrarComprobante(Reserva reserva) {

        StringBuilder detalle = new StringBuilder();

        detalle.append("Reserva N.º ")
                .append(reserva.getId())
                .append("\n");

        detalle.append("Cliente: ")
                .append(reserva.getCliente().getNombre())
                .append(" ")
                .append(reserva.getCliente().getApellido())
                .append("\n");

        detalle.append("DNI: ")
                .append(reserva.getCliente().getDni())
                .append("\n");

        detalle.append("Película: ")
                .append(reserva.getFuncion()
                        .getPelicula().getTitulo())
                .append("\n");

        detalle.append("Sala: ")
                .append(reserva.getFuncion()
                        .getSala().getId())
                .append("\n");

        detalle.append("Función: ")
                .append(reserva.getFuncion().getFechaHora())
                .append("\n\n");

        detalle.append("Entradas:\n");

        for (Entrada entrada : reserva.getEntradas()) {

            String tipo = entrada instanceof modelo.EntradaEstudiante
                    ? "Estudiante"
                    : "General";

            detalle.append(
                    entrada.getButaca().getFila()
            );

            detalle.append(
                    entrada.getButaca().getNumero()
            );

            detalle.append(" - ")
                    .append(tipo)
                    .append(" - $")
                    .append(entrada.getPrecioConfirmado())
                    .append("\n");
        }

        detalle.append("\nTOTAL: $")
                .append(reserva.getTotal());

        Alert comprobante = new Alert(
                Alert.AlertType.INFORMATION
        );

        
        comprobante.setTitle("Detalle de reserva");

        comprobante.setHeaderText(
                reserva.estaActiva()
                        ? "Reserva ACTIVA"
                        : "Reserva CANCELADA"
        );


        comprobante.setContentText(detalle.toString());

        comprobante.getDialogPane().setPrefWidth(550);

        comprobante.showAndWait();
    }

    
    private void cancelarReservaSeleccionada() {

        Reserva seleccionada = listaConsultaReservas
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una reserva"
            );
            return;
        }

        int idReserva = seleccionada.getId();

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Cancelar reserva");

        confirmacion.setHeaderText(
                "¿Querés cancelar la reserva " + idReserva + "?"
        );

        confirmacion.setContentText(
                "Las butacas quedarán disponibles "
                + "para nuevas reservas de esa función."
        );

        if (confirmacion.showAndWait().orElse(
                ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            EstadoSistema candidato = copiarEstado(estado);

            GestorReservas gestorTemporal =
                    new GestorReservas(candidato);

            gestorTemporal.cancelarReserva(idReserva);

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarConsultaReservas();

            mostrarInformacion(
                    "Reserva cancelada",
                    "La reserva fue cancelada y guardada. "
                    + "Sus butacas quedaron liberadas."
            );

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            mostrarError(
                    "No se pudo cancelar",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    "No se pudo guardar la cancelación: "
                    + e.getMessage()
            );
        }
    }

    
    private void verDetalleReserva() {

        Reserva seleccionada = listaConsultaReservas
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una reserva"
            );
            return;
        }

        Reserva reservaActual =
                gestorReservas.buscarPorId(
                        seleccionada.getId()
                );

        if (reservaActual == null) {
            mostrarError(
                    "Reserva no encontrada",
                    "La reserva ya no existe en el sistema"
            );
            return;
        }

        mostrarComprobante(reservaActual);
    }

    
    private void cambiarEstadoSala() {

        String seleccionado = listaSalas
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado == null) {
            mostrarError(
                    "Selección obligatoria",
                    "Seleccioná una sala del listado"
            );
            return;
        }

        int id = obtenerIdSala(seleccionado);

        Sala sala = gestorSalas.buscarPorId(id);

        if (sala == null) {
            mostrarError(
                    "Sala no encontrada",
                    "La sala seleccionada no existe"
            );
            return;
        }

        boolean desactivar = sala.estaActiva();

        String accion = desactivar
                ? "desactivar"
                : "reactivar";

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Cambiar estado de sala");

        confirmacion.setHeaderText(
                "¿Querés " + accion + " la Sala " + id + "?"
        );

        confirmacion.setContentText(
                "La sala y sus butacas permanecerán registradas."
        );

        if (confirmacion.showAndWait().orElse(
                javafx.scene.control.ButtonType.CANCEL)
                != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            EstadoSistema candidato = copiarEstado(estado);

            GestorSalas gestorTemporal =
                    new GestorSalas(candidato);

            if (desactivar) {
                gestorTemporal.desactivarSala(id);
            } else {
                gestorTemporal.reactivarSala(id);
            }

            persistencia.guardarEstado(candidato);

            estado = candidato;
            inicializarGestores();

            actualizarListadoSalas();

            listaSalas.getSelectionModel().clearSelection();

            mostrarInformacion(
                    "Operación correcta",
                    "La sala fue "
                    + (desactivar ? "desactivada" : "reactivada")
                    + " y guardada."
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    "No se pudo cambiar el estado",
                    e.getMessage()
            );

        } catch (ErrorPersistenciaException e) {

            mostrarError(
                    "Error de persistencia",
                    e.getMessage()
            );
        }
    }

}
