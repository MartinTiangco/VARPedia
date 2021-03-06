package Application.Helpers;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class of the Quiz functionality.
 * @author Group 25:
 * 			- Martin Tiangco, mtia116
 * 			- Yuansheng Zhang, yzhb120
 */
public class Quiz {

	private int _currentQuestionNumber = 0;
	private int _score = 0;
	private int _total;
	private List<Question> _listOfQuestions = new ArrayList<>();
	private String _difficulty;

	public Quiz() {
		// retrieves the number of creations in the directory
		String DIR = "Creation_Directory" + System.getProperty("file.separator");
		int total = Objects.requireNonNull(new File(DIR).listFiles(File::isDirectory)).length;
		_total = Math.min(total, 12);

		// Create one question for each creation that exist in the creation directory
		File[] listOfFiles = new File(DIR).listFiles(File::isDirectory);
		for (File file : listOfFiles){
			Question question = new Question();
			question.setCreationTested(file);
			int firstPatternIndex = question.getCreationTested().getName().indexOf("_-_");
			int secondPatternIndex = question.getCreationTested().getName().indexOf("_-_", firstPatternIndex + 3);
			question.setCorrectAnswer(question.getCreationTested().getName().substring(firstPatternIndex + 3, secondPatternIndex));
			_listOfQuestions.add(question);
		}

		// Randomly remove questions from the list of questions until there are less than 12 questions left
		Random rand = new Random();
		while (_listOfQuestions.size() > 12){
			_listOfQuestions.remove(rand.nextInt(_listOfQuestions.size()));
		}
		Collections.shuffle(_listOfQuestions);

	}

	public int getScore() {
		return _score;
	}

	public int getTotal() {
		return _total;
	}

	public int getCurrentQuestionNumber() {
		return _currentQuestionNumber;
	}

	public void incrementCurrentQuestionNumber() {
		_currentQuestionNumber++;
	}

	public void incrementScore() {
		_score++;
		_listOfQuestions.get(_currentQuestionNumber - 1).setCorrectness(true);
	}

	public void setDifficulty(String difficulty) {
		_difficulty = difficulty;
	}

	public void addUserAnswer(String userAnswer) {
		_listOfQuestions.get(_currentQuestionNumber).setUserAnswer(userAnswer);
	}

	/**
	 * Create the video to be displayed easy/medium quiz
	 */
	public Pane createQuizScreen(){
		Question question = _listOfQuestions.get(_currentQuestionNumber);
		question.setQuestionNumber(String.valueOf(_currentQuestionNumber + 1));

		// Create the media to be displayed based on the current question
		Media video;
		if (_difficulty.equals("Easy")) {
			video = new Media(question.getCreationTested().toURI().toString() + System.getProperty("file.separator") + "video.mp4");
		}
		else {
			video = new Media(question.getCreationTested().toURI().toString() + System.getProperty("file.separator") + "slideshow.mp4");
		}
		MediaPlayer player = new MediaPlayer(video);
		player.setAutoPlay(true);
		MediaView mediaView = new MediaView();
		mediaView.setMediaPlayer(player);
		mediaView.setFitHeight(400);
		mediaView.setFitWidth(500);

		return new Pane(mediaView);
	}

	/**
	 * Create the text to be displayed for hard quiz
	 */
	public Pane createQuizTextArea(){
		Question question = _listOfQuestions.get(_currentQuestionNumber);
		question.setQuestionNumber(String.valueOf(_currentQuestionNumber + 1));

		// Extract the text from the current question
		String text = question.getCreationTested().toURI().toString() + System.getProperty("file.separator")
				+ "wikit.txt";
		File c = new File(text.substring(5));

		// Replacing all key terms with blank for the user to guess
		BufferedReader file;
		String line;
		String newline = null;
		try {
			file = new BufferedReader(new FileReader(c));
			while ((line = file.readLine()) != null) {
				newline = line.replace(" " + question.getCorrectAnswer() + " ", "________");
				newline = newline.replace(question.getCorrectAnswer().toUpperCase().charAt(0) + question.getCorrectAnswer().substring(1), "________");
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TextArea textArea = new TextArea(newline);
		textArea.setWrapText(true);
		textArea.setEditable(false);

		return new Pane(textArea);
	}

	public String getCorrectAnswer() {
		return _listOfQuestions.get(_currentQuestionNumber - 1).getCorrectAnswer();
	}

	public List<Question> getListOfQuestions() {
		return _listOfQuestions;
	}

	public String getDifficulty() {
		return _difficulty;
	}
}
