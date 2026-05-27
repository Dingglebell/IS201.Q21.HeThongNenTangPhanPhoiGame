package com.gameplatform.ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

final class UiUtils {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private UiUtils() {
    }

    static Label pageTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-title");
        return label;
    }

    static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    static VBox card() {
        VBox box = new VBox(8);
        box.getStyleClass().add("card");
        return box;
    }

    static Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button-primary");
        return button;
    }

    static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }

    static Button dangerButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("danger-button");
        return button;
    }

    static <T> TableColumn<T, String> stringColumn(String title, int width, Function<T, String> mapper) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(mapper.apply(data.getValue())));
        return column;
    }

    static String money(BigDecimal value) {
        if (value == null) {
            return "0 đ";
        }
        return MONEY_FORMAT.format(value) + " đ";
    }

    static String date(LocalDate value) {
        return value == null ? "" : DATE_FORMAT.format(value);
    }

    static String dateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMAT.format(value);
    }

    static void showError(String title, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(throwable.getMessage());
        alert.showAndWait();
    }

    static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static VBox formRow(String labelText, javafx.scene.Node input) {
        Label label = new Label(labelText);
        label.getStyleClass().add("stat-label");
        VBox box = new VBox(5, label, input);
        box.setPadding(new Insets(0, 0, 6, 0));
        return box;
    }
}

