package com.gameplatform;

import com.gameplatform.model.TaiKhoanDangNhap;
import com.gameplatform.ui.LoginView;
import com.gameplatform.ui.MainView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
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
        stage.setMaximized(true);
        stage.show();
        maximizeAfterSceneChange();
    }

    private void showLogin() {
        LoginView loginView = new LoginView(this::showMain);
        setScene(loginView);
    }

    private void showMain(TaiKhoanDangNhap sessionUser) {
        MainView mainView = new MainView(sessionUser, this::showLogin);
        setScene(mainView);
    }

    private void setScene(Parent root) {
        double width = primaryStage.isShowing() ? Math.max(primaryStage.getWidth(), 1180) : 1180;
        double height = primaryStage.isShowing() ? Math.max(primaryStage.getHeight(), 760) : 760;
        boolean forceRelayout = primaryStage.isShowing() && primaryStage.isMaximized();
        if (forceRelayout) {
            primaryStage.setMaximized(false);
        }
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        primaryStage.setScene(scene);
        maximizeAfterSceneChange();
    }

    private void maximizeAfterSceneChange() {
        Platform.runLater(() -> {
            primaryStage.setMaximized(true);
            if (primaryStage.getScene() != null) {
                primaryStage.getScene().getRoot().requestLayout();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}



