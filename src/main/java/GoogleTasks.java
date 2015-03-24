import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskLists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Tanakan on 15/03/23.
 */
public class GoogleTasks {
    public static void main(String[] args) {
        try {
            String clientId = "912559173078-tepsnl669jkkvic1slap365l8e0u9brn.apps.googleusercontent.com";
            String clientSecret = "6jflEHEk8PDu1xcZIbq860x2";

            String redirectUrl = "http://localhost/oauth2callback";
            String scope = "https://www.googleapis.com/auth/tasks";

            String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUrl, Arrays.asList(scope)).build();

            System.out.println("Go to the following link in your browser:");
            System.out.println(authorizationUrl);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("What is the authorization code?");
            String code = in.readLine();

            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientId, clientSecret, Arrays.asList(scope)).build();
            GoogleTokenResponse response = googleAuthorizationCodeFlow.newTokenRequest(code).setRedirectUri(redirectUrl).execute();
            Credential credential = googleAuthorizationCodeFlow.createAndStoreCredential(response, null);

            Tasks service = new Tasks.Builder(httpTransport, jsonFactory, credential).setApplicationName("Test").build();

            TaskLists taskLists = service.tasklists().list().execute();
            taskLists.getItems().forEach(taskList -> {
                System.out.println("task list title:" + taskList.getTitle());
                try {
                    com.google.api.services.tasks.model.Tasks tasks = service.tasks().list(taskList.getId()).execute();
                    /*
                    for(Task task : tasks.getItems()){
                        System.out.println(task.toString());
                    }
                    */
                    tasks.getItems().forEach(System.out::println);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
