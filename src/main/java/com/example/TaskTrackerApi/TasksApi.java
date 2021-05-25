package com.example.TaskTrackerApi;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import sun.net.www.http.HttpClient;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Providers;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Collectors;

@Path("tasks")
public class TasksApi {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTask(@Context HttpServletResponse response) {
        JSONParser jsonParser = new JSONParser();
        try {
//            File dbJson = new File("db.json");
//            FileUtils.copyURLToFile(
//                    new URL("https://elasticbeanstalk-us-west-2-916208355867.s3-us-west-2.amazonaws.com/db.json"),
//                    dbJson);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("db.json"));
            JSONArray jsonArray = (JSONArray) jsonObject.get("tasks");
            response.getWriter().println(jsonArray);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity("")
                .build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response postTask(@Context HttpServletRequest req,@Context HttpServletResponse res,@Context HttpServletRequest cres) throws IOException {
        String task = IOUtils.toString(cres.getInputStream());
        System.out.println(task);
        JSONParser jsonParser = new JSONParser();
        try {
//            File dbJson = new File("db.json");
//            FileUtils.copyURLToFile(
//                    new URL("https://elasticbeanstalk-us-west-2-916208355867.s3-us-west-2.amazonaws.com/db.json"),
//                    dbJson);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("db.json"));
            JSONObject taskObj = (JSONObject) jsonParser.parse(task);
            JSONArray jsonArray = (JSONArray) jsonObject.get("tasks");
            JSONObject obj = new JSONObject();
            taskObj.put("id",idGenerator(jsonArray));
            jsonArray.add(taskObj);
            jsonObject.put("tasks",jsonArray);
            Files.write(Paths.get("db.json"), jsonObject.toJSONString().getBytes());
            res.addHeader("Access-Control-Allow-Origin", "*");
            res.getWriter().println(taskObj);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return Response
                .status(200)
                .entity("")
                .build();
    }
    public static Integer idGenerator(JSONArray jsonArray){
        JSONObject jsIdObj = (JSONObject) jsonArray.get(jsonArray.size()-1);
        return (Integer.parseInt(String.valueOf(jsIdObj.get("id")))+1);
    }

    @OPTIONS
    public Response option(){
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Headers","Content-type").header("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS")
                .entity("")
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response taskReminderUpdate(@Context UriInfo uriInfo,@Context HttpServletResponse res) throws IOException {
        JSONParser jsonParser = new JSONParser();
        try {
            Integer Id =Integer.parseInt(uriInfo.getPath().split("/")[1])-1;
//            File dbJson = new File("db.json");
//            FileUtils.copyURLToFile(
//                    new URL("https://elasticbeanstalk-us-west-2-916208355867.s3-us-west-2.amazonaws.com/db.json"),
//                    dbJson);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("db.json"));
            JSONArray jsonArray = (JSONArray) jsonObject.get("tasks");
            JSONObject updateReminder = (JSONObject) jsonArray.get(Id);
            Boolean reminder = (Boolean) updateReminder.get("reminder") != true;
            updateReminder.put("reminder",reminder);
            jsonArray.set(Id,updateReminder);
            jsonObject.put("tasks",jsonArray);
            Files.write(Paths.get("db.json"), jsonObject.toJSONString().getBytes());
            res.getWriter().println(updateReminder);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        res.getWriter().println(uriInfo.getPath().split("/")[1]);
        res.addHeader("Access-Control-Allow-Origin", "*");
        return Response
                .status(200)
                .entity("")
                .build();
    }
    @DELETE
    @Path("/{id}")
    public Response deleteTask(@Context UriInfo uriInfo,@Context HttpServletResponse res){
        JSONParser jsonParser = new JSONParser();
        try {
            File dbJson = new File("db.json");
//            FileUtils.copyURLToFile(
//                    new URL("https://elasticbeanstalk-us-west-2-916208355867.s3-us-west-2.amazonaws.com/db.json"),
//                    dbJson);
            Integer Id =Integer.parseInt(uriInfo.getPath().split("/")[1]);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("db.json"));
            JSONArray jsonArray = (JSONArray) jsonObject.get("tasks");
            for(int i=0;i<jsonArray.size();i++){
                JSONObject updateReminder = (JSONObject) jsonArray.get(i);
                Long index=(Long) updateReminder.get("id");
                if(index.equals(Id.longValue())){
                    jsonArray.remove(updateReminder);
                    jsonObject.put("tasks",jsonArray);
                    Files.write(Paths.get("db.json"), jsonObject.toJSONString().getBytes());
                    break;
                }

            }

//            Files.write(Paths.get("D:\\studies\\Java\\Application\\JAX-RS\\db.json"), jsonObject.toJSONString().getBytes());

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        res.getWriter().println(uriInfo.getPath().split("/")[1]);
        res.addHeader("Access-Control-Allow-Origin", "*");
        return Response
                .status(200)
                .entity("")
                .build();
    }
    @OPTIONS
    @Path("/{id}")
    public Response optionPut(){
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Headers","Content-type").header("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS")
                .entity("")
                .build();
    }

//    public static void main(String[] args) {
//        JSONParser jsonParser = new JSONParser();
//        try {
//            Integer Id =3;
//            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("D:\\studies\\Java\\Application\\JAX-RS\\db.json"));
//            JSONArray jsonArray = (JSONArray) jsonObject.get("tasks");
//            JSONObject resReminder;
//            for(int i=0;i<jsonArray.size();i++){
//                JSONObject updateReminder = (JSONObject) jsonArray.get(i);
//                Long index=(Long) updateReminder.get("id");
//                if(index.equals(Id.longValue())){
//                    jsonArray.remove(updateReminder);
//                    jsonObject.put("tasks",jsonArray);
//                    Files.write(Paths.get("D:\\studies\\Java\\Application\\JAX-RS\\db.json"), jsonObject.toJSONString().getBytes());
//                   System.out.println(Id);
//                    break;
//                }
//            }
//
////            Files.write(Paths.get("D:\\studies\\Java\\Application\\JAX-RS\\db.json"), jsonObject.toJSONString().getBytes());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

