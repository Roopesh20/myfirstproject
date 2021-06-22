package sample;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Controller implements Initializable {
    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final int CIRCLE_DIAMETER=80;
    private static final String discColour1="#24B03E";
    private static final String discColour2="#E3242B";
    private static String Player1="Player 1";
    private static String Player2="Player 2";
    private boolean isPlayer1Turn=true;
    private Disc[][] insertedDiscArray=new Disc[ROWS][COLUMNS];
    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscPane;
    @FXML
    public Label playerNameLabel;
    @FXML
    public TextField playerOneTextField,playerTwoTextField;
    @FXML
    public Button setNamesButton;
    private boolean isAllowedToInsert=true;
    public void createPlayground(){
        setNamesButton.setOnAction(event->{
            Player1=playerOneTextField.getText();
            Player2=playerTwoTextField.getText();

        });
        Shape RectangleWithHoles=gameStructuralGrid();

        rootGridPane.add(RectangleWithHoles,0,1);
        List<Rectangle> rectangleList=createClickableColumns();
        for (Rectangle r :rectangleList){
            rootGridPane.add(r,0,1);
        }

    }
    private Shape gameStructuralGrid(){
        Shape RectangleWithHoles= new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        for (int row=0;row<ROWS;++row){
            for(int col=0;col<COLUMNS;++col){
                Circle c=new Circle();
                c.setRadius(CIRCLE_DIAMETER/2);
                c.setCenterX(CIRCLE_DIAMETER/2);
                c.setCenterY(CIRCLE_DIAMETER/2);
                c.setSmooth(true);
                c.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                c.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                RectangleWithHoles=Shape.subtract(RectangleWithHoles,c);

            }
        }
        RectangleWithHoles.setFill(Color.WHITE);
        return RectangleWithHoles;

    }
    private List<Rectangle> createClickableColumns(){
        List<Rectangle> rectangleList=new ArrayList<>();
        for(int col=0;col<COLUMNS;++col){
        Rectangle r=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        r.setFill(Color.TRANSPARENT);
        r.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        r.setOnMouseEntered(event->r.setFill(Color.valueOf("#eeeeee26")));
        r.setOnMouseExited(event->r.setFill(Color.TRANSPARENT));
        final int column=col;
        r.setOnMouseClicked(event->{
            if(isAllowedToInsert) {
                isAllowedToInsert = false;
                insertDisc(new Disc(isPlayer1Turn), column);
            }
        });
        rectangleList.add(r);
        }

        return rectangleList;
    }
    private void insertDisc(Disc disc,int column){
        int row=ROWS-1;
        while(row>=0) {
            if (getDiscIfPresent(row,column) == null)
                break;
            row--;
        }
        if(row<0)
            return;
        insertedDiscArray[row][column]=disc;
        insertedDiscPane.getChildren().add(disc);
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);

        translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        int currentRow=row;
        translateTransition.setOnFinished(event->{
            isAllowedToInsert=true;
            if(gameEnded(currentRow,column)){
                gameOver();
                return;

            }
            isPlayer1Turn = !isPlayer1Turn;
            playerNameLabel.setText(isPlayer1Turn? Player1:Player2);
        });
        translateTransition.play();

    }



    private boolean gameEnded(int row,int column){
        // vertical points.A small example:player has inserted disc at row=2 and column =3.
        // range of row values =0,1,2,3,4,5.
        //Index of element present in  column [row][column]= 0,3  1,3  2,3  4,3  5,3  6,3.-->Point2D
        List<Point2D> verticalPoints=IntStream.rangeClosed(row-3,row+3).
                                     mapToObj(r->new Point2D(r,column)).
                                     collect(Collectors.toList());
        List<Point2D> horizontalPoints=IntStream.rangeClosed(column-3,column+3).
                mapToObj(c->new Point2D(row,c)).
                collect(Collectors.toList());
        Point2D startPoint1=new Point2D(row-3,column+3);
        List<Point2D> diagonal1Points =IntStream.rangeClosed(0,6).
                                       mapToObj(i->startPoint1.add(i,-i)).
                                       collect(Collectors.toList());
        Point2D startPoint2=new Point2D(row-3,column-3);
        List<Point2D> diagonal2Points =IntStream.rangeClosed(0,6).
                mapToObj(i->startPoint2.add(i,i)).
                collect(Collectors.toList());
        boolean isEnded= checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)||
                          checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);
        return isEnded;

    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain=0;
        for (Point2D point:points) {
            int rowIndexForArray= (int) point.getX();
            int columnIndexForArray= (int) point.getY();
            Disc disc= getDiscIfPresent(rowIndexForArray,columnIndexForArray);
            if(disc!=null&& disc.isPlayer1Move==isPlayer1Turn){
                chain++;
                if(chain==4){
                    return true;
                }

            }else{
                chain=0;

            }

        }
        return false;
    }
    private Disc getDiscIfPresent(int row,int column){
        if(row >=ROWS|| row<0|| column>=COLUMNS|| column<0)
            return null;
        return insertedDiscArray[row][column];

    }
    private void gameOver() {
        String winner=isPlayer1Turn? Player1:Player2;
        System.out.println("Winner is"+" "+winner);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is"+" "+winner);
        alert.setContentText("Want to Play Again ?");
        ButtonType yesButton=new ButtonType("Yes");
        ButtonType noButton=new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton,noButton);
        Platform.runLater(()->{
            Optional<ButtonType>btnClicked=alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get()==yesButton){
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();
        for (int row=0;row<insertedDiscArray.length;row++){
            for(int col=0;col<insertedDiscArray[row].length;col++){
                insertedDiscArray[row][col]=null;
            }
        }
        isPlayer1Turn=true;
        playerNameLabel.setText("Player 1");
        createPlayground();


    }

    private static class Disc extends Circle{
        private final boolean isPlayer1Move ;
        public Disc(boolean isPlayer1Move){
            this.isPlayer1Move=isPlayer1Move;
            setRadius(CIRCLE_DIAMETER/2);
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
            setFill(isPlayer1Move?Color.valueOf(discColour1):Color.valueOf(discColour2));

        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

}