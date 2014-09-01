package org.sbaresearch.socialsnapshot.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Photo;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class UserImageTreeExporter {

	private Path snapshotDirPath;
	private Path jsonPath;
	private Path srcImageDirPath;
	private Path linkDirPath;

	private static final Logger log = Logger.getLogger(UserImageTreeExporter.class);

	/**
	 * @param snapshotDirectory
	 *            the absolute path to the snapshot dir
	 * @param userId
	 *            the userId of the invoking user to generate the correct subpaths
	 * @param jsonFile
	 *            the relative or absolute path to snapshotDirectory
	 */
	public UserImageTreeExporter(String snapshotDirectory, String userId, String jsonFile) {
		this.snapshotDirPath = Paths.get(snapshotDirectory);
		this.jsonPath = this.snapshotDirPath.resolve(jsonFile);
		this.srcImageDirPath = this.snapshotDirPath.resolve(userId + "-images");
		this.linkDirPath = this.snapshotDirPath.resolve(userId + "-user-images");
	}

	/**
	 * @return the path to the generated Userimage tree
	 */
	public String export() {
		try {
			Gson gson = new GsonBuilder().create();
			User rootUser;
			
			rootUser = gson.fromJson(new FileReader(this.jsonPath.toFile()), User.class);

			processUser(rootUser);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			log.error("Error reading JSON", e);
		}
		return this.linkDirPath.toString();
	}
	
	private void createHardLink(Path userDir, String linkName, Path existing) {
		String contentType = null;
		try {
			contentType = Files.probeContentType(existing);
		} catch (IOException e1) {
			// nothing
		}
		String extension = "";
		if (contentType != null && contentType.contains("image")) {
			extension = "." + contentType.replace("image/", "");
		}
		
		Path link = userDir.resolve(linkName + extension);
		try {
			Files.createLink(link, existing);
		} catch (FileAlreadyExistsException e) {
			log.warn("Couldn't create link, already existing: " + link.toString());
		} catch (IOException e) {
			log.warn("Couldn't create link: " + link.toString(), e);
		}
	}
	
	private void processUser(User user) {
		Path userDir = this.linkDirPath.resolve(user.getLastName() + " " + user.getFirstName() + " (" + user.getId() + ")");
		try {
			Files.createDirectories(userDir);
		} catch (IOException e) {
			log.error("Error creating directory " + userDir.toString(), e);
		}
		
		if (user.getPicture() != null && user.getPicture().data != null && !user.getPicture().data.isSilhouette) {
			createHardLink(userDir, "Profile", resolveOfflineSrcPath(user.getPicture().data.url));
		}
		if (user.getCover() != null) {
			createHardLink(userDir, "Cover", resolveOfflineSrcPath(user.getCover().source));
		}
		if (user.getPhotos() != null) {
			for (Photo photo : user.getPhotos()) {
				String linkName = photo.getId();
				if (photo.getName() != null) {
					String photoName = photo.getName().replaceAll("[^a-zA-Z0-9]", " ").trim().replaceAll("\\s+", " ");
					linkName = photoName.substring(0, Math.min(photoName.length(), 50)) + " (" + linkName + ")";
				}
				createHardLink(userDir, linkName, resolveOfflineSrcPath(photo.getSource()));
			}
		}
		if (user.getFriends() != null) {
			for (User friend : user.getFriends()) {
				processUser(friend);
			}
		}
	}

	private Path resolveOfflineSrcPath(String url) {
		if (url.contains("?")) {
			url = url.substring(0, url.indexOf("?")); // strip get parameters
		}
		int pos = url.indexOf("/") + 2;
		return this.srcImageDirPath.resolve(url.substring(pos).replace("/", File.separator));
	}

}
