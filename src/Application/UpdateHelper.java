package Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateHelper extends Thread {
	private final String DIR = "./Creation_Directory";
	
	private Controller _homeScreenController;
	private List<Creation> _creations = new ArrayList<Creation>();
	
	public UpdateHelper(Controller homeScreenController) {
		_homeScreenController = homeScreenController;
	}
	
	@Override
	public void run() {
		List<String> listOfFilenames = extractFromDirectory();
		createCreations(listOfFilenames);
		// we need to add to existing list of creations in Runnable runLater class.
	}
	
	private List<String> extractFromDirectory() {
		List<String> listOfFilenames = new ArrayList<>();
		File dir = new File(DIR);
		File[] listOfFiles = dir.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			listOfFilenames.add(listOfFiles[i].getName());
		}
		return listOfFilenames;
	}
	
	private void createCreations(List<String> listOfFilenames) {
		
		for (String file : listOfFilenames) {
			//gets the first occurrence of the file separator pattern
			int firstPatternIndex = file.indexOf("_-_");
			
			//gets the second occurrence of the file separator pattern
			int secondPatternIndex = file.indexOf("_-_", firstPatternIndex);
			
			Creation creation = new Creation(extractName(file, firstPatternIndex), 
					extractTerm(file, firstPatternIndex, secondPatternIndex), 
					extractDateModified(file), 
					extractLength(file, secondPatternIndex));
			
			_creations.add(creation);
		}

	}
	
	private String extractName(String filename, int firstPatternIndex) {
		return filename.substring(0, firstPatternIndex);
	}
	
	private String extractTerm(String filename, int firstPatternIndex, int secondPatternIndex) {
		return filename.substring(firstPatternIndex, secondPatternIndex);
	}
	
	private long extractDateModified(String filename) {
		return new File(DIR + filename).lastModified();
	}
	
	private String extractLength(String filename, int secondPatternIndex) {
		//gets the filename extension index i.e. ".mp4"
		int ext = filename.indexOf(".mp4");
		return filename.substring(secondPatternIndex, ext);
	}
}