package com.agenda.agenda;
import com.agenda.agenda.Modelos.Contacto;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgendaView implements Initializable {
    @FXML
    private TableColumn<Contacto, String> CLcorreo;

    @FXML
    private TableColumn<Contacto, Double> CLsaldo;

    @FXML
    private Button Cerrar;

    @FXML
    private TableColumn<Contacto, Integer> ClID;

    @FXML
    private TableColumn<Contacto, String > ClNOMBRE;

    @FXML
    private Button Eliminar;

    @FXML
    private Button Modificar;

    @FXML
    private Button Nuevo;

    @FXML
    private TableView<Contacto> TableCl;

    @FXML
    private TableColumn<Contacto, String> clTelefono;
    
    @FXML
    private TableColumn<Contacto, String> CLdireccion;

    @FXML
    void CerrarClick(ActionEvent event) {System.exit(0);}

    @FXML
    void EliminarClick(ActionEvent event) {
        Contacto cm = TableCl.getSelectionModel().getSelectedItem();
        int indice = TableCl.getSelectionModel().getFocusedIndex();
        if(HelloApplication.bd.capacidad() <= 0){
            Dialog d = new Dialog<>();
            d.setTitle("base de datos vacia.");
            d.setContentText("No hay elementos en la base de dato.");
            ButtonType okbt = new ButtonType("ok",ButtonType.OK.getButtonData());
            d.getDialogPane().getButtonTypes().add(okbt);
            d.showAndWait();
        }
        else{
            Dialog d = new Dialog();
            d.setTitle("Confirmar eliminacion");
            d.setContentText("Esta seguro que desea elimiinar a este contacto?");

            ButtonType eliminarBn = new ButtonType("Elimnar", ButtonType.OK.getButtonData());
            ButtonType cancelarBn = new ButtonType("Cancelar",ButtonType.CANCEL.getButtonData());
            d.getDialogPane().getButtonTypes().addAll(eliminarBn,cancelarBn);
            Optional<ButtonType> decision = d.showAndWait();
            if(decision.get() == eliminarBn){
                HelloApplication.bd.Eliminar(indice);
                list();
            }
        }
    }

    @FXML
    void ModificarClick(ActionEvent event) {
        Contacto cm = TableCl.getSelectionModel().getSelectedItem();
        int indice = TableCl.getSelectionModel().getFocusedIndex();
        Dialog d = new Dialog();
        FXMLLoader louder = new FXMLLoader(getClass().getResource("Modificar-view.fxml"));
        d.setTitle("Modificador de Contactos!!");
        try{
            d.getDialogPane().setContent(louder.load());
            ModificarView Mv = louder.getController();
            ButtonType btnGuardar = new ButtonType("Modificar",ButtonType.OK.getButtonData());
            ButtonType btnCerrar = new ButtonType("Cerrar",ButtonType.CANCEL.getButtonData());
            d.getDialogPane().getButtonTypes().add(btnGuardar);
            d.getDialogPane().getButtonTypes().add(btnCerrar);
            Mv.Modificar(cm);

            d.setResultConverter(new Callback<ButtonType, Contacto>() {

                @Override
                public Contacto call(ButtonType buttonType) {
                    if(buttonType == d.getDialogPane().getButtonTypes().get(0))
                        return Mv.getContacto();
                    return null;
                }
            });
            Optional<Contacto> contacto = d.showAndWait();
            try{
                if(contacto.get() != null){
                    cm.setNombre(contacto.get().getNombre());
                    HelloApplication.bd.edit(cm,indice);
                    list();
                }
            }catch (Exception e){}
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    @FXML
    void NuevoCLICK(ActionEvent event) {
        Dialog d = new Dialog();
        FXMLLoader louder = new FXMLLoader(getClass().getResource("Agregar-view.fxml"));
        try{
            d.getDialogPane().setContent(louder.load());
            d.setTitle("Agregar Contactos!!");
            NuevoView Nv = louder.getController();
            ButtonType btnGuardar = new ButtonType("Guardar",ButtonType.OK.getButtonData());
            ButtonType btnCerrar = new ButtonType("Cerrar",ButtonType.CANCEL.getButtonData());
            d.getDialogPane().getButtonTypes().add(btnGuardar);
            d.getDialogPane().getButtonTypes().add(btnCerrar);

            d.setResultConverter(new Callback<ButtonType, Contacto>() {

                @Override
                public Contacto call(ButtonType buttonType) {
                    if(buttonType == d.getDialogPane().getButtonTypes().get(0))
                        return Nv.getContacto();
                    return null;
                }
            });
            Optional<Contacto> contacto = d.showAndWait();
            try{
                if(contacto.get() != null){
                    HelloApplication.bd.Agregar(contacto.get());
                    list();
                }
            }catch (Exception e){}
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public  void list(){
        Contacto[] arr = HelloApplication.bd.list();
        List<Contacto> list = new ArrayList<>();
        for (Contacto c : arr)
            list.add(c);
        ClID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contacto, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Contacto, Integer> c) {
                return new SimpleIntegerProperty(c.getValue().getId()).asObject();
            }
        });
        ClNOMBRE.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contacto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Contacto, String> c) {
                return new SimpleStringProperty(c.getValue().getNombre());
            }
        });
        TableCl.setItems(FXCollections.observableList(list));
        CLcorreo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contacto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Contacto, String> c) {
                return new SimpleStringProperty(c.getValue().getEmail());
            }
        });
        clTelefono.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contacto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Contacto, String> c) {
                return new SimpleStringProperty(c.getValue().getTelefono());
            }
        });
        CLsaldo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contacto, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Contacto, Double> c) {
                return new SimpleDoubleProperty(c.getValue().getSaldo()).asObject();
            }
        });
        CLdireccion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contacto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Contacto, String> c) {
                return new SimpleStringProperty(c.getValue().getDireccion());
            }
        });
        TableCl.setItems(FXCollections.observableList(list));
        TableCl.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list();
    }
}
