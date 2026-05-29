package com.gameplatform;

import com.gameplatform.model.TaiKhoanDangNhap;
import com.gameplatform.ui.LoginView;
import com.gameplatform.ui.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class App extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Arcadia - Hệ thống quản lý nền tảng phân phối game");
        stage.setMinWidth(1180);
        stage.setMinHeight(760);
        showLogin();
        stage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView(this::showMain);
        Scene scene = new Scene(loginView, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showMain(TaiKhoanDangNhap sessionUser) {
        MainView mainView = new MainView(sessionUser, this::showLogin);
        Scene scene = new Scene(mainView, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



