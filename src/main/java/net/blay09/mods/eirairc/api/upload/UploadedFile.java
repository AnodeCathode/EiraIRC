package net.blay09.mods.eirairc.api.upload;

public class UploadedFile {

	public final String url;
	public final String directURL;
	public final String deleteURL;
	
	public UploadedFile(String url, String directURL, String deleteURL) {
		this.url = url;
		this.directURL = directURL;
		this.deleteURL = deleteURL;
	}
	
}
