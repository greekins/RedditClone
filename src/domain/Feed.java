package domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import domain.exception.PostException;


public class Feed implements Serializable {
	private static final long serialVersionUID = 886230688789652010L;

	protected List<Post> posts;
	
	protected String feedFilename;
	protected File feedFile;
	
	private Post post;
	protected String title;
	protected String link;
	protected URL url;
	protected Calendar date;

	@SuppressWarnings("unchecked")
	public Feed() {
		posts = Collections.synchronizedList(new ArrayList<Post>());
		
		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		feedFilename = prop.getProperty("feed-filename");
		feedFile = new File(feedFilename);
		if (feedFile.exists()) {
			try {
				FileInputStream fileIn = new FileInputStream(feedFile);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				posts = (List<Post>) objectIn.readObject();
				objectIn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		PublishedContent.setContentChangedListener(this);
	}
	
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public List<Post> getPosts() {
		Collections.sort(posts);
		return Collections.unmodifiableList(posts);
	}

	
	public void addPost() throws IOException {
		if(checkEntries()) {
			post = new Post();
			url = new URL(link);
			post.setLink(url);
			post.setTitle(title);
			post.setUsername(UserManager.globalUsername);
			
			posts.add(post);
			save();
		}
	}
	
	public void contentChanged() {
		save();
	}

	private boolean checkEntries() {
		if (title == null || title.equals(""))
			new PostException("Please enter a describing title for your link").handleExcpetion();
		else if (link == null)
			new PostException("Please enter a link to post").handleExcpetion();
		else
			return true;
		return false;
	}
	private void save() {
		try {
			FileOutputStream fileOut = new FileOutputStream(feedFile);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(posts);
			objectOut.flush();
			objectOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


