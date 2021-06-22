package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        GridPane rootGridPane = loader.load();
        controller=loader.getController();
        controller.createPlayground();
        MenuBar mb=createMenu();
        mb.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane= (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(mb);

        Scene sc=new Scene(rootGridPane);

        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(sc);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public MenuBar createMenu(){
        //File Menu
        Menu fileMenu=new Menu("File");
        MenuItem newGame=new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> {
            controller.resetGame();

        });
        MenuItem resetGame=new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> {
            controller.resetGame();

        });
        SeparatorMenuItem smp=new SeparatorMenuItem();
        MenuItem exitGame=new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> {
            exitGame();

        });
        fileMenu.getItems().addAll(newGame,resetGame,smp,exitGame);
        //Help Menu
        Menu helpMenu=new Menu("Help");
        MenuItem aboutGame=new MenuItem("About Connect 4");
        aboutGame.setOnAction(actionEvent -> {
            aboutConnect4();
            
        });
        SeparatorMenuItem smi=new SeparatorMenuItem();
        MenuItem aboutMe=new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> {
            aboutMe();
            
        });
        helpMenu.getItems().addAll(aboutGame,smi,aboutMe);

        MenuBar mb=new MenuBar();
        mb.getMenus().addAll(fileMenu,helpMenu);
        return mb;


    }

    private void aboutMe() {
        Alert al=new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("About The Developer");
        al.setHeaderText("Gulimcherla Roopesh Reddy");
        al.setContentText("I Love to play around with the code and create games. Connect4 is one of them."
        +" "+"In free time, I like to spend time with nears and dears. ");
        al.show();

    }

    private void aboutConnect4() {
        Alert al=new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("About Connect4");
        al.setHeaderText("How to Play ?");
        al.setContentText("Connect4 is a two player Connection game in which the"
        +"players first choose a colour and then take turns dropping coloured discs"
        +"from the top into Seven column,six-row vertically suspended grid."
        +"The pieces fall down straight down,occupying the next available space within the column."
        +"The objective of the game is to be the first to form a horizontal,vertical"
        +"or Diagonal line of four of one's own discs.Connect4 is a solved game."+
                "The first player can always win by playing right moves.");
        al.show();




    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);

    }

    private void resetGame() {
    }

    public static void main(String[]args){
    launch(args);}
}


