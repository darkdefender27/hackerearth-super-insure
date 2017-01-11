package com.supernova;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by anayonkar on 10/1/17.
 */
public class ImageClassifier {
  private static final String APPLICATION_NAME = "Google-VisionLabelSample/1.0";
  private final Vision vision;
  private static ImageClassifier instance;

  private ImageClassifier(Vision vision) {
    this.vision = vision;
  }

  public static synchronized ImageClassifier getInstance() throws IOException, GeneralSecurityException {
    if (instance == null) {
      instance = new ImageClassifier(ImageClassifier.getVisionService());
    }
    return instance;
  }

  public static Vision getVisionService() throws IOException, GeneralSecurityException {
    InputStream inputStream = ImageClassifier.class.getClassLoader().getResourceAsStream("google_application_credentials.json");
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleCredential credential = GoogleCredential.fromStream(inputStream).createScoped(VisionScopes.all());
    return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
      .setApplicationName(APPLICATION_NAME)
      .build();
  }

  public List<EntityAnnotation> labelImage(byte[] data, int maxResults) throws IOException {
    // [START construct_request]
    AnnotateImageRequest request =
      new AnnotateImageRequest()
        .setImage(new Image().encodeContent(data))
        .setFeatures(ImmutableList.of(
          new Feature()
            .setType("LABEL_DETECTION")
            .setMaxResults(maxResults)));
    Vision.Images.Annotate annotate =
      vision.images()
        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
    // Due to a bug: requests to Vision API containing large images fail when GZipped.
    annotate.setDisableGZipContent(true);
    // [END construct_request]

    // [START parse_response]
    BatchAnnotateImagesResponse batchResponse = annotate.execute();
    assert batchResponse.getResponses().size() == 1;
    AnnotateImageResponse response = batchResponse.getResponses().get(0);
    if (response.getLabelAnnotations() == null) {
      throw new IOException(
        response.getError() != null
          ? response.getError().getMessage()
          : "Unknown error getting image annotations");
    }
    return response.getLabelAnnotations();
    // [END parse_response]
  }
}
