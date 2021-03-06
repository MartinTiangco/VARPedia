package Application.Controllers;

import Application.Helpers.Question;
import Application.Helpers.Quiz;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the 'Quiz Score Screen'
 * @author Group 25:
 * 			- Martin Tiangco, mtia116
 * 			- Yuansheng Zhang, yzhb120
 */
public class Quiz_Score_ScreenController extends Controller implements Initializable {

    @FXML private Button _mainMenuButton;
    @FXML private Button _tryAgainButton;
    @FXML private ImageView _medal;
    @FXML private Label _percentageScore;
    @FXML private TableColumn<Question, String> _questionNumber;
    @FXML private TableColumn<Question, String> _userAnswer;
    @FXML private TableColumn<Question, String> _correctAnswer;
    @FXML private TableView _analysisTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Setting up the ListView of questions
        _questionNumber.setCellValueFactory(new PropertyValueFactory<>("questionNumber"));
        _userAnswer.setCellValueFactory(new PropertyValueFactory<>("userAnswer"));
        _correctAnswer.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        // Modifying the ListView by colouring correct questions with green and incorrect questions with red
        for (Object tableColumn : _analysisTable.getColumns()) {
            ((TableColumn<Question, String>) tableColumn).setCellFactory(column -> new TableCell<Question, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        Question question = getTableView().getItems().get(getIndex());
                        if (question.getCorrectness()) {
                            setTextFill(Color.WHITE);
                            setStyle("-fx-background-color: green");
                        } else {
                            setTextFill(Color.WHITE); //The text in red
                            setStyle("-fx-background-color: red");
                        }
                    }
                }
            });
        }
    }

    /**
     * Evaluating the Quiz by showing the result to the user
     */
    public void evaluate() {
        Quiz quiz = ((Quiz_ScreenController)getParentController()).getQuiz();
        int score = quiz.getScore();
        int total = quiz.getTotal();
        int percentage = (int)(((score + 0.0)/(total + 0.0)*100));
        showResults(percentage);
        _percentageScore.setText(percentage + " %");
        _analysisTable.getItems().addAll(quiz.getListOfQuestions());
    }

    public void handleBack() {
        Stage stage = (Stage) _mainMenuButton.getScene().getWindow();
        stage.close();
    }

    /**
     * load the quiz with the same difficulty again
     */
    public void handleTryAgain() {
        loadScreen("Quiz", "/Application/fxml/Quiz_Start.fxml","");
        Stage stage = (Stage) _tryAgainButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show analysis of result based on the user score
     * @param score
     */
    private void showResults(int score) {
        // show a medal icon and message to user depending on results
        String assetPath = "/Application/assets/";
        String icon;
        if (score == 100) {
            icon = "trophy.png";
        } else if (score < 100 && score > 89) {
            icon = "gold_medal.png";
        } else if (score < 90 && score > 79) {
            icon = "silver_medal.png";
        } else if (score < 80 && score > 69) {
            icon = "bronze_medal.png";
        } else {
            icon = "no_medal.png";
        }

        // set the image to show
        Image image = new Image(assetPath + icon);
        _medal.setImage(image);

        // add a fade in transition to the medal image and the percentage score
        Node[] nodes = {_medal, _percentageScore};
        for (Node node : nodes) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(2000));
            fadeIn.setNode(node);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setCycleCount(1);
            fadeIn.setAutoReverse(false);
            fadeIn.playFromStart();
        }
    }
}
