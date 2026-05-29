package com.gameplatform.ui;

import com.gameplatform.controller.XacThucTaiKhoanController;
import com.gameplatform.model.TaiKhoanDangNhap;
import com.gameplatform.service.AuthException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

public final class LoginView extends BorderPane {
    private final XacThucTaiKhoanController xacThucTaiKhoanController = new XacThucTaiKhoanController();

    public LoginView(Consumer<TaiKhoanDangNhap> onLoginSuccess) {
        getStyleClass().add("login-shell");

        VBox showcase = new VBox(18);
        showcase.getStyleClass().add("login-showcase");
        showcase.setPrefWidth(560);
        Label logo = new Label("Arcadia");
        logo.getStyleClass().add("login-logo");
        Label headline = new Label("Nền tảng phân phối game Arcadia");
        headline.getStyleClass().add("login-headline");
        HBox covers = new HBox(12, coverChip("NEON RUNNER"), coverChip("LOTUS QUEST"), coverChip("SERVER TYCOON"));
        Label status = new Label(xacThucTaiKhoanController.canConnectDatabase() ? "Oracle 21c: đã kết nối" : "Oracle 21c: chưa kết nối");
        status.getStyleClass().add("login-db-status");
        Region showcaseFill = new Region();
        VBox.setVgrow(showcaseFill, Priority.ALWAYS);
        showcase.getChildren().addAll(logo, showcaseFill, headline, covers, status);

        VBox card = new VBox(16);
        card.setMaxWidth(430);
        card.getStyleClass().add("login-card");

        Label title = new Label("Đăng nhập hệ thống");
        title.getStyleClass().add("app-title");

        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("input");
        usernameField.setPromptText("Tên đăng nhập");

        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("input");
        passwordField.setPromptText("Mật khẩu");

        Button loginButton = UiUtils.primaryButton("Đăng nhập");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        Button registerPlayer = UiUtils.secondaryButton("Đăng ký người chơi");
        Button registerDeveloper = UiUtils.secondaryButton("Đăng ký nhà phát triển");
        registerPlayer.setMaxWidth(Double.MAX_VALUE);
        registerDeveloper.setMaxWidth(Double.MAX_VALUE);

        GridPane demoAccounts = demoAccounts();

        loginButton.setOnAction(event -> {
            try {
                TaiKhoanDangNhap user = xacThucTaiKhoanController.login(usernameField.getText(), passwordField.getText());
                onLoginSuccess.accept(user);
            } catch (AuthException | SQLException exception) {
                UiUtils.showError("Không đăng nhập được", exception);
            } catch (RuntimeException exception) {
                exception.printStackTrace();
                UiUtils.showError("Đăng nhập được nhưng không mở được giao diện hệ thống", exception);
            }
        });
        passwordField.setOnAction(event -> loginButton.fire());
        registerPlayer.setOnAction(event -> showPlayerRegistration());
        registerDeveloper.setOnAction(event -> showDeveloperRegistration());

        HBox registerRow = new HBox(10, registerPlayer, registerDeveloper);
        HBox.setHgrow(registerPlayer, Priority.ALWAYS);
        HBox.setHgrow(registerDeveloper, Priority.ALWAYS);

        card.getChildren().addAll(title,
                UiUtils.formRow("Tài khoản", usernameField),
                UiUtils.formRow("Mật khẩu", passwordField),
                loginButton,
                registerRow,
                demoAccounts);

        HBox layout = new HBox(28, showcase, card);
        layout.getStyleClass().add("login-layout");
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(34));
        HBox.setHgrow(showcase, Priority.ALWAYS);
        setCenter(layout);
    }

    private VBox coverChip(String title) {
        VBox chip = new VBox(6);
        chip.getStyleClass().add("login-cover-chip");
        Label name = new Label(title);
        name.getStyleClass().add("login-cover-title");
        Label tag = new Label("Đang hiện hành");
        tag.getStyleClass().add("login-cover-tag");
        chip.getChildren().addAll(name, tag);
        return chip;
    }

    private GridPane demoAccounts() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(12);
        gridPane.setVgap(7);
        gridPane.getStyleClass().add("login-demo-card");

        String[][] accounts = {
                {"Người chơi", "player01"},
                {"Nhà phát triển", "dev01"},
                {"Quản lý nền tảng", "manager01"},
                {"Kiểm duyệt viên", "moderator01"},
                {"Marketing", "marketing01"},
                {"CSKH", "cskh01"}
        };

        Label note = new Label("Tài khoản demo, mật khẩu chung: 123456");
        note.getStyleClass().add("subtitle");
        gridPane.add(note, 0, 0, 2, 1);
        for (int i = 0; i < accounts.length; i++) {
            Label role = new Label(accounts[i][0]);
            role.getStyleClass().add("stat-label");
            Label username = new Label(accounts[i][1]);
            username.getStyleClass().add("login-demo-username");
            gridPane.add(role, 0, i + 1);
            gridPane.add(username, 1, i + 1);
        }
        return gridPane;
    }

    private void showPlayerRegistration() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đăng ký tài khoản người chơi");
        TextField username = input("Tên đăng nhập");
        PasswordField password = passwordInput("Mật khẩu");
        TextField displayName = input("Tên hiển thị");
        DatePicker birthDate = new DatePicker(LocalDate.of(2000, 1, 1));
        TextField email = input("Email");
        TextField phone = input("SĐT");
        TextField country = input("Quốc gia");
        VBox form = new VBox(8,
                UiUtils.formRow("Tên đăng nhập", username),
                UiUtils.formRow("Mật khẩu", password),
                UiUtils.formRow("Tên hiển thị", displayName),
                UiUtils.formRow("Ngày sinh", birthDate),
                UiUtils.formRow("Email", email),
                UiUtils.formRow("SĐT", phone),
                UiUtils.formRow("Quốc gia", country));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> {
            try {
                int id = xacThucTaiKhoanController.registerPlayer(username.getText(), password.getText(), displayName.getText(),
                        birthDate.getValue(), email.getText(), phone.getText(), country.getText());
                UiUtils.showInfo("Đăng ký thành công", "Tài khoản người chơi #" + id + " đã được tạo.");
            } catch (AuthException | SQLException exception) {
                UiUtils.showError("Không đăng ký được người chơi", exception);
            }
        });
    }

    private void showDeveloperRegistration() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đăng ký tài khoản nhà phát triển");
        TextField username = input("Tên đăng nhập");
        PasswordField password = passwordInput("Mật khẩu");
        TextField developerName = input("Tên nhà phát triển/studio");
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Studio", "Cá nhân", "Doanh nghiệp");
        type.getSelectionModel().selectFirst();
        TextField email = input("Email");
        TextField phone = input("SĐT");
        TextField address = input("Địa chỉ");
        VBox form = new VBox(8,
                UiUtils.formRow("Tên đăng nhập", username),
                UiUtils.formRow("Mật khẩu", password),
                UiUtils.formRow("Tên NPT", developerName),
                UiUtils.formRow("Loại NPT", type),
                UiUtils.formRow("Email", email),
                UiUtils.formRow("SĐT", phone),
                UiUtils.formRow("Địa chỉ", address));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> {
            try {
                int id = xacThucTaiKhoanController.registerDeveloper(username.getText(), password.getText(), developerName.getText(),
                        type.getValue(), email.getText(), phone.getText(), address.getText());
                UiUtils.showInfo("Đăng ký thành công", "Tài khoản nhà phát triển #" + id + " đã được tạo.");
            } catch (AuthException | SQLException exception) {
                UiUtils.showError("Không đăng ký được nhà phát triển", exception);
            }
        });
    }

    private TextField input(String prompt) {
        TextField textField = new TextField();
        textField.getStyleClass().add("input");
        textField.setPromptText(prompt);
        return textField;
    }

    private PasswordField passwordInput(String prompt) {
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("input");
        passwordField.setPromptText(prompt);
        return passwordField;
    }
}




