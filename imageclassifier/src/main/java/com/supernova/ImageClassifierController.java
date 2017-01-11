package com.supernova;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

/**
 * Created by anayonkar on 10/1/17.
 */
@RestController
public class ImageClassifierController {
  @RequestMapping("/greeting")
  public String greeting() {
    return "greeting";
  }

  @RequestMapping(value = "/imageClassification",
    method = RequestMethod.POST)
  @ResponseBody
  public String getImageClassification(@RequestBody String image) {
    try {
      byte[] imageBytes = Base64.getDecoder().decode(image);
      ImageClassifier imageClassifier = ImageClassifier.getInstance();
      List<EntityAnnotation> result = imageClassifier.labelImage(imageBytes, 10);
      if(result == null || result.isEmpty()) {
        return null;
      }
      System.out.println("Image classification is : ");
      for(EntityAnnotation entityAnnotation : result) {
        System.out.println(entityAnnotation.getDescription() + "\t" + entityAnnotation.getScore());
      }
      return result.get(0).getDescription();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

}
