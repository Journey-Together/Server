package Journey.Together.external.aws;

public record ImagePresignedUrlResponse(String fileName, String preSignedUrl) {
	public static ImagePresignedUrlResponse of(String fileName, String url){
		return new ImagePresignedUrlResponse(fileName, url);
	}
}
