package ch.bbw.view;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author TheBromo
 */
public class FXMLStarter extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        
        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*try {
            File file = new File("log_" + System.currentTimeMillis() + ".log");
            System.out.println(file.getAbsolutePath());
            if (!file.exists()) file.createNewFile();
            PrintStream writer = new PrintStream(file);
            System.setOut(writer);
            System.setErr(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        launch(args);
    }
    
}
