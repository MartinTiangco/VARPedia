package Application.Helpers;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Class that retrieves the images from Flickr using an adapted version of Nasser's example.
 * @author Group 25:
 * 			- Martin Tiangco, mtia116
 * 			- Yuansheng Zhang, yzhb120
 */
public class ImageGenerator extends Task<Long> {
	private int _numPics;
	private String _term;
	
	public ImageGenerator(String term, int numPics) {
		_numPics = numPics;
		_term = term;
	}

	@Override
	protected Long call() {
		// delete all images first from the images directory
		Cleaner cleaner = new Cleaner();
		cleaner.cleanImage();
		
		// retrieves from Flickr
		retrievePhotos();
		return null;
	}
	
	/*
	 *  Uses flickr's API to retrieve photos
	 */
	private void retrievePhotos() {
		try {
			String apiKey = getAPIKey("apiKey");
			String sharedSecret = getAPIKey("sharedSecret");
			
			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
			
			String query = _term;
			int resultsPerPage = _numPics;
			int page = 0;
			
			PhotosInterface photos = flickr.getPhotosInterface();
			SearchParameters params = new SearchParameters();
			params.setSort(SearchParameters.RELEVANCE);
			params.setMedia("photos");
			params.setText(query);
			
			PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
			
			int numId = 0;
			for (Photo photo : results) {
				try {
					BufferedImage image = photos.getImage(photo, Size.LARGE);
					String filename = query.trim().replace(' ', '-') + "-00" + numId + ".jpg";
					File outputfile = new File(".Image_Directory", filename);
					ImageIO.write(image, "jpg", outputfile);
					numId++;
				} catch (FlickrException ignored) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Retrieves the APIKey from the file "flickr-api-keys.txt"
	 */
	private static String getAPIKey(String key) throws Exception {
		String config = System.getProperty("user.dir") + System.getProperty("file.separator") + "flickr-api-keys.txt";
		
		File file = new File(config);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line;
		while ((line = br.readLine()) != null) {
			if ((line.trim().startsWith(key))) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find " + key + " in config file " + file.getName());
	}
}
