package org.nethercutt.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;

//libraries for Apache Tika
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

//libraries for Amazon Lambda and S3
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class TikaLambdaHandler implements RequestHandler<S3Event, String> {

    public String handleRequest(S3Event s3event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("received : " + s3event.toJson());
        Tika tika = new Tika();

        try {
            //Get S3 Event and bucket name
            S3EventNotificationRecord record = s3event.getRecords().get(0);
            String bucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String key = URLDecoder.decode(record.getS3().getObject().getKey().replace('+', ' '), "UTF-8");

            //Create S3Client and retrieve object (file) from S3 bucket
            AmazonS3 s3Client = new AmazonS3Client();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucket, key));
            String extractedText;

            //Use Tika to extract text from file
            try (InputStream objectData = s3Object.getObjectContent()) {
                extractedText = tika.parseToString(objectData);
            }
            //Get size of extractedText as encoded as UTF-8
            final byte[] utf8Bytes = extractedText.getBytes("UTF-8");
            System.out.println(utf8Bytes.length);

            if (utf8Bytes.length > 125000) {
              System.out.println("File is larger than what can be pushed through Comprehend in batch.\n");
              return "{}";

            } else {
                System.out.println("The following output was successful.\n");
                String[] fileSplits = justify(extractedText, (int)Math.ceil((double)utf8Bytes.length / Math.ceil((double)utf8Bytes.length / 5000)));
                JSONObject jo = new JSONObject();
                jo.put("filename",s3Object.getKey());

                JSONObject joTexts = new JSONObject();
                for(int i=0; i<fileSplits.length; i++) {
                    joTexts.put("text" + i,fileSplits[i].replaceAll("\n",""));
                }
                JSONArray ja = new JSONArray();
                ja.put(joTexts);

                jo.put("Texts",ja);

                System.out.println(jo);
                JSONObject toReturn = new JSONObject();
                toReturn.put("ParsedFile", coatCheck(bucket, key, jo.toString()));
                toReturn.put("Bucket",bucket);
                return toReturn.toString(5);
            } //end if

        } catch (IOException | TikaException e) {
            logger.log("Exception: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

    }//end handleRequest

    private String[] justify(String s, int limit) {
        ArrayList<String> chunks = new ArrayList<>();
        StringBuilder justifiedLine = new StringBuilder();
        String[] words = s.split(" ");
        for (int i = 0; i < words.length; i++) {
            justifiedLine.append(words[i]).append(" ");
            if (i+1 == words.length || justifiedLine.length() + words[i+1].length() > limit) {
                justifiedLine.deleteCharAt(justifiedLine.length() - 1);
                chunks.add(justifiedLine.toString());
                justifiedLine = new StringBuilder();
            }
        }
        System.out.println("Returned array size is: " + chunks.size());
        return chunks.toArray(new String[0]);
    } //end justify

    public String coatCheck(String s3bucket, String originalFileName, String finalJSON) {

      AmazonS3 s3outputClient = new AmazonS3Client();
      //----- Todo - Change MD5 Hash to include ALL text from the file, not just file name.
      String outFileName = originalFileName.split("/")[1];
      File outputFile = null;
      BufferedWriter writer = null;

      //Create a temporary file and write the text meta data extracted from Tika to the temp file.
      try {
          outputFile = File.createTempFile(outFileName, "");
          writer = new BufferedWriter(new FileWriter(outputFile));
          writer.write(finalJSON);
          writer.close();
      } catch (IOException ex) {
          //logger.log("Exception: " + ex.getLocalizedMessage());
          throw new RuntimeException(ex);
      }

      //Output raw text data to s3 staging/output bucket.
      s3outputClient.putObject(new PutObjectRequest(s3bucket, "tikaout/" + outFileName + ".tika", outputFile));

      //------ Todo - Delete file from temp directly.

      return "tikaout/" + outFileName + ".tika";
    } //end processExtractedText

} //end class
