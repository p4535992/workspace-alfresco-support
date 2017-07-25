import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * e.g. 127.0.0.1:4567/bulkimport?path=<PATH TO FOLDER OR FILE>
 * e.g. 127.0.0.1:4567/search?q=<KEYWORD>
 */
public class Biblio {

    final private static String basepath = "http://127.0.0.1:8080/alfresco/api/-default-/public/";
    final private static String search = "search/versions/1/search";
    final private static String upload = "alfresco/versions/1/nodes/-shared-/children";
    final private static String content = "alfresco/versions/1/nodes/%%ID%%/content";
    final private static String download = "alfresco/versions/1/nodes/%%ID%%/content?attachment=true";


    public static void main(String[] args)  {

        // intercetta l'url /
        Spark.get("/", (req,res) -> {

            Map<String, Object> model = new HashMap<>();

            return new ThymeleafTemplateEngine().render(
                    new ModelAndView(model, "search")
            );
        });

        // trigger del bulk import dei contenuti
        Spark.get("/bulkimport", (req,res) -> {

            String importPath = req.queryParams("path");

            massiveDocumentImport(importPath);

            return new ThymeleafTemplateEngine().render(
                    new ModelAndView(new HashMap<>() , "search")
            );
        });


        // Risultato di una ricerca
        Spark.get("/search", (req, res) -> {

            String keyWords = req.queryParams("q");

            String queryBody = "{ \"query\": { \"query\" : \"TEXT:'%%KEYWORDS%%' AND PATH:'/app:company_home/app:shared//*'\" } }"
                    .replace("%%KEYWORDS%%", keyWords);

            HttpResponse<JsonNode> jsonData = Unirest.post(basepath+search)
                    .basicAuth("admin", "admin")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(queryBody)
                    .asJson();

            ObjectMapper objectMapper = new ObjectMapper();

            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(jsonData.getRawBody());

            List<com.fasterxml.jackson.databind.JsonNode> results = rootNode.findValues("entry");


            Map<String, Object> model = new HashMap<>();
            model.put("answer", results);
            model.put("question", keyWords);

            return new ThymeleafTemplateEngine().render(
                    new ModelAndView(model, "search")
            );

        });
        
        /*
         * Agganciare una logica autorizzativa a tutte le route che rispondono ai verbi POST, PUT e DELETE.
         */
	    Spark.before((request,response)->{
	        String method = request.requestMethod();
	        if(method.equals("POST") || method.equals("PUT") || method.equals("DELETE")){
	                  String authentication = request.headers("Authentication");
	                  if(!"PASSWORD".equals(authentication)){
	                	  Spark.halt(401, "User Unauthorized");
	                  }
	        }
	    });






//    curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'Authorization: Basic YWRtaW46YWRtaW4=' -d '{
//       "name":"Biblioteca", "nodeType":"cm:folder"
//    }' 'http://192.168.99.100:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/-shared-/children'

//        curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'Authorization: Basic YWRtaW46YWRtaW4=' -d '{"name":"Spec_RP3.pdf", "nodeType":"cm:content", "relativePath":"Biblioteca"}' 'http://192.168.99.100:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/-shared-/children'

//        curl -X PUT --header 'Content-Type: application/octet-stream' --header 'Accept: application/json' --header 'Authorization: Basic YWRtaW46YWRtaW4=' -F 'file=@filename' 'http://192.168.99.100:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/30f88402-73b0-427c-93c9-0aa7a0dd5ccb/content'

    }



    private static void massiveDocumentImport(String folderPath) {

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(folderPath))) {

            for (Path path : ds) {
                File newDoc = path.toFile();
                if (newDoc.isFile() && !newDoc.isHidden()) {
                    uploadDoc(newDoc);
                }
            }
        }
        catch (IOException | UnirestException e) {
            //TODO manage exception
        }

    }


    private static void uploadDoc (File newDoc) throws UnirestException, IOException {

        String body = "{\"name\":\"%%FILENAME%%\", \"nodeType\":\"cm:content\"}"
                .replace("%%FILENAME%%", newDoc.getName());

        // eseguiamo la chiamata REST per creare il nodo relativo al nostro documento
        HttpResponse<JsonNode> jsonData = Unirest.post(basepath+upload)
                .basicAuth("admin", "admin")
                .header("Accept", "application/json")
                .field("filedata", newDoc)
                .asJson();

        // controlliamo se si è verificato un errore nel caricamento
        if (jsonData.getStatus() >= 400) {
            com.fasterxml.jackson.databind.JsonNode rootNode = (new ObjectMapper()).readTree(jsonData.getRawBody());
            String errorText = rootNode.get("error").get("errorKey").asText();
            //TODO gestire l'errore
        }
//        String nodeid = rootNode.get("entry").get("id").asText();
//        String mimetype  = rootNode.get("entry").get("content").get("mimeType").asText();
//
//        // Ora possiamo caricare il contenuto del documento, sempre via REST
//        String documentEntryPoint = basepath+content.replace("%%ID%%", nodeid);
//
//        HttpResponse<JsonNode> jsonDoc = Unirest.put(documentEntryPoint)
//                .basicAuth("admin", "admin")
//                .header("Content-Type", mimetype)
//                .header("Accept", "application/json")
//                .field("file", newDoc)
//                .asJson();
//
//        com.fasterxml.jackson.databind.JsonNode newNode = (new ObjectMapper()).readTree(jsonDoc.getRawBody());
    }


}
