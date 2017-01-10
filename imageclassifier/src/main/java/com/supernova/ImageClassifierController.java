package com.supernova;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;

/**
 * Created by anayonkar on 10/1/17.
 */
@RestController
public class ImageClassifierController {
  @RequestMapping("/greeting")
  public String greeting() {
    String value = System.getenv().get("GOOGLE_APPLICATION_CREDENTIALS");
    return value == null || value.isEmpty() ? "null" : value;
    //return "greeting";
  }

  @RequestMapping(value = "/imageClassification",
    method = RequestMethod.POST)
  @ResponseBody
  public String getImageClassification(@RequestBody String image) {
    try {
      byte[] imageBytes = Base64.getDecoder().decode(image);
      //ImageClassifier imageClassifier = new ImageClassifier(ImageClassifier.getVisionService());
      ImageClassifier imageClassifier = ImageClassifier.getInstance();
      //System.out.println("body : " + image);
      List<EntityAnnotation> result = imageClassifier.labelImage(/*Paths.get("data/flower.jpg")*/imageBytes, 1);
      /*StringBuilder sb = new StringBuilder();
      for(EntityAnnotation entityAnnotation : result) {
        System.out.println(entityAnnotation.getDescription() + "\t" + entityAnnotation.getScore());
        sb.append(entityAnnotation.getDescription() + "," + entityAnnotation.getScore()).append("\n");
      }
      return sb.toString();*/
      if(result == null || result.isEmpty()) {
        return null;
      }
      return result.get(0).getDescription();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
