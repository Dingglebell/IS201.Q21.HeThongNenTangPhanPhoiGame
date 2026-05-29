package com.gameplatform.ui;

import com.gameplatform.controller.QuanLyDanhMucGameController;
import com.gameplatform.controller.QuanLyMuaHangController;
import com.gameplatform.controller.QuanLyMaGiamGiaController;
import com.gameplatform.controller.QuanLyThongTinGameController;
import com.gameplatform.controller.QuanLyHoSoController;
import com.gameplatform.controller.QuanLyKhuyenMaiController;
import com.gameplatform.controller.QuanLyYeuCauPhatHanhController;
import com.gameplatform.controller.QuanLyDoanhThuController;
import com.gameplatform.controller.QuanLyTicketHoTroController;
import com.gameplatform.controller.QuanLyNhanVienController;
import com.gameplatform.controller.QuanLyTaiKhoanController;
import com.gameplatform.model.LoaiTaiKhoan;
import com.gameplatform.model.GameTrongGioHang;
import com.gameplatform.model.TheLoaiGame;
import com.gameplatform.model.GameCuaNhaPhatTrien;
import com.gameplatform.model.ThongTinMaGiamGia;
import com.gameplatform.model.VaiTroNhanVien;
import com.gameplatform.model.MediaGame;
import com.gameplatform.model.ThongTinGame;
import com.gameplatform.model.PhienBanGame;
import com.gameplatform.model.GameTrongThuVien;
import com.gameplatform.model.ThongTinHoSo;
import com.gameplatform.model.GameTrongKhuyenMai;
import com.gameplatform.model.ChuongTrinhKhuyenMai;
import com.gameplatform.model.YeuCauPhatHanh;
import com.gameplatform.model.DongBaoCaoDoanhThu;
import com.gameplatform.model.TaiKhoanDangNhap;
import com.gameplatform.model.ThongTinTicket;
import com.gameplatform.model.ThongTinGiaoDich;
import com.gameplatform.model.ThongTinNguoiDung;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class MainView extends BorderPane {
    private final TaiKhoanDangNhap session;
    private final Runnable onLogout;
    private final QuanLyThongTinGameController quanLyThongTinGameController = new QuanLyThongTinGameController();
    private final QuanLyMuaHangController quanLyMuaHangController = new QuanLyMuaHangController();
    private final QuanLyDanhMucGameController quanLyDanhMucGameController = new QuanLyDanhMucGameController();
    private final QuanLyYeuCauPhatHanhController quanLyYeuCauPhatHanhController = new QuanLyYeuCauPhatHanhController();
    private final QuanLyKhuyenMaiController quanLyKhuyenMaiController = new QuanLyKhuyenMaiController();
    private final QuanLyMaGiamGiaController quanLyMaGiamGiaController = new QuanLyMaGiamGiaController();
    private final QuanLyTicketHoTroController quanLyTicketHoTroController = new QuanLyTicketHoTroController();
    private final QuanLyTaiKhoanController quanLyTaiKhoanController = new QuanLyTaiKhoanController();
    private final QuanLyNhanVienController quanLyNhanVienController = new QuanLyNhanVienController();
    private final QuanLyHoSoController quanLyHoSoController = new QuanLyHoSoController();
    private final QuanLyDoanhThuController quanLyDoanhThuController = new QuanLyDoanhThuController();

    public MainView(TaiKhoanDangNhap session, Runnable onLogout) {
        this.session = session;
        this.onLogout = onLogout;
        getStyleClass().add(session.accountType() == LoaiTaiKhoan.NGUOI_CHOI ? "game-client" : "ops-client");
        if (session.accountType() == LoaiTaiKhoan.NGUOI_CHOI) {
            setTop(playerTopBar());
        } else {
            setLeft(sidebar());
        }

        showDashboard();
    }

    private VBox sidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(session.accountType() == LoaiTaiKhoan.NGUOI_CHOI ? 214 : 260);
        sidebar.getStyleClass().add("sidebar");

        Label title = new Label("Arcadia");
        title.getStyleClass().add("sidebar-title");
        Label user = new Label(session.displayName());
        user.getStyleClass().add("sidebar-subtitle");
        Label role = new Label(roleLabel());
        role.getStyleClass().add("sidebar-subtitle");

        sidebar.getChildren().addAll(title, user, role, spacer(8));

        if (session.accountType() == LoaiTaiKhoan.NGUOI_CHOI) {
            sidebar.getChildren().addAll(playerNavButtons());
        } else if (session.accountType() == LoaiTaiKhoan.NHA_PHAT_TRIEN) {
            sidebar.getChildren().addAll(
                    nav("Tổng quan", this::showDashboard),
                    nav("Quản lý game của tôi", this::showGameCuaNhaPhatTrienManagement),
                    nav("Yêu cầu phát hành game", this::showDeveloperReleaseManagement),
                    nav("Quản lý doanh thu", this::showDeveloperRevenue),
                    nav("Hồ sơ", this::showProfile)
            );
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.QUAN_LY_NEN_TANG)) {
            sidebar.getChildren().addAll(
                    nav("Quản lý nhân viên", this::showEmployeeManagement),
                    nav("Quản lý doanh thu nền tảng", this::showPlatformRevenue)
            );
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.KIEM_DUYET_VIEN)) {
            sidebar.getChildren().addAll(
                    nav("Duyệt yêu cầu phát hành", this::showModeratorRequests),
                    nav("Quản lý thể loại game", this::showCategories),
                    nav("Quản lý nhà phát triển", this::showDeveloperManagement),
                    nav("Quản lý người chơi", this::showPlayerManagement),
                    nav("Đổi mật khẩu", this::showChangePassword)
            );
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.MARKETING)) {
            sidebar.getChildren().addAll(
                    nav("Quản lý chương trình khuyến mãi", this::showPromotions),
                    nav("Quản lý mã giảm giá", this::showDiscountCodes),
                    nav("Danh sách game trên nền tảng", this::showGameManagement),
                    nav("Đổi mật khẩu", this::showChangePassword)
            );
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.CSKH)) {
            sidebar.getChildren().addAll(
                    nav("Quản lý ticket", this::showSupportTickets),
                    nav("Danh sách người chơi", this::showPlayerManagement),
                    nav("Đổi mật khẩu", this::showChangePassword)
            );
        }

        Region fill = new Region();
        VBox.setVgrow(fill, Priority.ALWAYS);
        if (session.accountType() == LoaiTaiKhoan.NGUOI_CHOI) {
            sidebar.getChildren().add(playerProfileCard());
        }
        Button logout = nav("Đăng xuất", onLogout);
        sidebar.getChildren().addAll(fill, logout);
        return sidebar;
    }

    private HBox playerTopBar() {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("player-topbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Arcadia");
        title.getStyleClass().add("sidebar-title");
        Label user = new Label(session.displayName());
        user.getStyleClass().add("sidebar-subtitle");
        VBox identity = new VBox(2, title, user);

        HBox nav = new HBox(6);
        nav.getChildren().addAll(playerNavButtons());

        Region fill = new Region();
        HBox.setHgrow(fill, Priority.ALWAYS);
        Button logout = nav("Đăng xuất", onLogout);
        bar.getChildren().addAll(identity, nav, fill, logout);
        return bar;
    }

    private List<Button> playerNavButtons() {
        return List.of(
                nav("Cửa hàng", this::showStore),
                nav("Wishlist", this::showWishlist),
                nav("Giỏ hàng", this::showCart),
                nav("Thư viện", this::showLibrary),
                nav("Lịch sử giao dịch", this::showTransactionHistory),
                nav("Hỗ trợ", this::showPlayerTickets),
                nav("Tài khoản", this::showProfile)
        );
    }

    private VBox playerProfileCard() {
        VBox card = new VBox(5);
        card.getStyleClass().add("player-profile");
        Label name = new Label(session.displayName());
        name.getStyleClass().add("player-profile-name");
        Label id = new Label("#" + session.profileId() + " • Online");
        id.getStyleClass().add("sidebar-subtitle");
        card.getChildren().addAll(name, id);
        return card;
    }

    private Button nav(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        return region;
    }

    private String roleLabel() {
        if (session.accountType() == LoaiTaiKhoan.NHAN_VIEN) {
            return "Nhân viên - " + session.employeeRole().dbValue();
        }
        return session.accountType().dbValue();
    }

    private void setPage(Node node) {
        ScrollPane scrollPane = new ScrollPane(node);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        setCenter(scrollPane);
    }

    private VBox page(String title) {
        VBox content = new VBox(16);
        content.getStyleClass().add("content");
        content.getChildren().add(UiUtils.pageTitle(title));
        return content;
    }

    private void showDashboard() {
        if (session.accountType() == LoaiTaiKhoan.NGUOI_CHOI) {
            showStore();
        } else if (session.accountType() == LoaiTaiKhoan.NHA_PHAT_TRIEN) {
            showDeveloperHome();
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.KIEM_DUYET_VIEN)) {
            showModeratorRequests();
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.MARKETING)) {
            showPromotions();
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.CSKH)) {
            showSupportTickets();
        } else if (session.isVaiTroNhanVien(VaiTroNhanVien.QUAN_LY_NEN_TANG)) {
            showEmployeeManagement();
        } else {
            showChangePassword();
        }
    }

    private void showDeveloperHome() {
        VBox content = page("Studio dashboard");
        try {
            List<GameCuaNhaPhatTrien> games = quanLyThongTinGameController.findGamesByDeveloper(session.profileId());
            List<YeuCauPhatHanh> requests = quanLyYeuCauPhatHanhController.findByDeveloper(session.profileId());
            long pending = requests.stream().filter(request -> "Chờ duyệt".equals(request.status())).count();
            int purchases = games.stream().mapToInt(GameCuaNhaPhatTrien::purchases).sum();

            VBox hero = heroCard(
                    session.displayName(),
                    "Theo dõi game đã gửi, trạng thái kiểm duyệt và hiệu quả phát hành.",
                    "Yêu cầu phát hành game",
                    this::showDeveloperReleaseManagement
            );

            GridPane cards = new GridPane();
            cards.setHgap(12);
            cards.setVgap(12);
            cards.add(statCard("Game của studio", String.valueOf(games.size())), 0, 0);
            cards.add(statCard("Yêu cầu chờ duyệt", String.valueOf(pending)), 1, 0);
            cards.add(statCard("Tổng lượt mua", String.valueOf(purchases)), 2, 0);

            TableView<GameCuaNhaPhatTrien> table = developerGameTable();
            table.setPrefHeight(300);
            table.getItems().setAll(games);

            content.getChildren().addAll(hero, cards, UiUtils.sectionTitle("Danh mục game của studio"), table);
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được dashboard nhà phát triển", exception));
        }
        setPage(content);
    }

    private VBox statCard(String label, String value) {
        VBox card = UiUtils.card();
        card.setMinWidth(180);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("stat-label");
        card.getChildren().addAll(valueLabel, labelNode);
        return card;
    }

    private VBox heroCard(String title, String subtitle, String actionText, Runnable action) {
        VBox hero = new VBox(12);
        hero.getStyleClass().add("hero-card");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("hero-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("hero-subtitle");
        Button button = UiUtils.primaryButton(actionText);
        button.setOnAction(event -> action.run());
        hero.getChildren().addAll(titleLabel, subtitleLabel, button);
        return hero;
    }

    private VBox errorCard(String title, Throwable exception) {
        VBox card = UiUtils.card();
        card.getChildren().addAll(UiUtils.sectionTitle(title), new Label(exception.getMessage()));
        return card;
    }

    private void showStore() {
        VBox content = page("Cửa hàng game");
        try {
            List<ThongTinGame> games = quanLyThongTinGameController.findReleasedGamesNotOwned(session.profileId());
            TextField search = input("Tìm theo tên game");
            ComboBox<String> genre = genreFilter(games.stream().map(ThongTinGame::genres).toList());
            TilePane tilePane = gameTilePane(games, true);
            Runnable reload = () -> {
                List<ThongTinGame> filtered = games.stream()
                        .filter(game -> matchesGame(game, search.getText(), genre.getValue()))
                        .toList();
                tilePane.getChildren().setAll(filtered.stream().map(game -> gameCard(game, true)).toList());
            };
            search.textProperty().addListener((observable, oldValue, newValue) -> reload.run());
            genre.setOnAction(event -> reload.run());
            HBox filters = new HBox(10, search, genre);
            HBox.setHgrow(search, Priority.ALWAYS);
            Label quickTitle = new Label("GAMES");
            quickTitle.getStyleClass().add("launcher-section-title");
            content.getChildren().addAll(filters, quickTitle, tilePane);
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được cửa hàng", exception));
        }
        setPage(content);
    }

    private TilePane gameTilePane(List<ThongTinGame> games, boolean allowPurchase) {
        TilePane tilePane = new TilePane();
        tilePane.setHgap(14);
        tilePane.setVgap(14);
        tilePane.setPrefColumns(3);
        for (ThongTinGame game : games) {
            tilePane.getChildren().add(gameCard(game, allowPurchase));
        }
        return tilePane;
    }

    private ComboBox<String> genreFilter(List<String> genreTexts) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(genreOptions(genreTexts));
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }

    private List<String> genreOptions(List<String> genreTexts) {
        Set<String> options = new LinkedHashSet<>();
        options.add("Tất cả thể loại");
        for (String text : genreTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            for (String genre : text.split(",")) {
                String clean = genre.trim();
                if (!clean.isBlank()) {
                    options.add(clean);
                }
            }
        }
        return new ArrayList<>(options);
    }

    private boolean matchesGame(ThongTinGame game, String keyword, String genre) {
        return (containsText(game.title(), keyword)
                || containsText(game.developerName(), keyword)
                || containsText(game.genres(), keyword))
                && matchesGenre(game.genres(), genre);
    }

    private boolean matchesLibrary(GameTrongThuVien game, String keyword, String genre) {
        return containsText(game.title(), keyword) && matchesGenre(game.genres(), genre);
    }

    private boolean containsText(String value, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        return safe(value).toLowerCase().contains(keyword.trim().toLowerCase());
    }

    private boolean matchesGenre(String genres, String selectedGenre) {
        if (selectedGenre == null || selectedGenre.equals("Tất cả thể loại")) {
            return true;
        }
        return safe(genres).toLowerCase().contains(selectedGenre.toLowerCase());
    }

    private VBox gameCard(ThongTinGame game, boolean allowPurchase) {
        VBox card = new VBox(10);
        card.getStyleClass().add("game-card");
        card.setPrefWidth(220);
        card.setMinHeight(286);

        StackPane coverStack = new StackPane();
        coverStack.getStyleClass().add("game-cover-stack");
        Node cover = gameCoverNode(game);
        Label beta = new Label("BETA");
        beta.getStyleClass().add("beta-ribbon");
        StackPane.setAlignment(beta, Pos.TOP_CENTER);
        coverStack.getChildren().addAll(cover, beta);

        Label genre = new Label(game.genres());
        genre.getStyleClass().add("stat-label");
        HBox price = priceBox(game);
        Label meta = new Label(game.developerName() + " • " + game.ageRating() + "+ • " + game.purchases() + " lượt mua");
        meta.getStyleClass().add("stat-label");

        card.getChildren().addAll(coverStack, genre, price, meta);
        if (allowPurchase && session.accountType() == LoaiTaiKhoan.NGUOI_CHOI) {
            Button detail = UiUtils.secondaryButton("Chi tiết");
            Button cart = UiUtils.primaryButton("Thêm vào giỏ");
            Button wish = UiUtils.secondaryButton("Wishlist");
            detail.setMaxWidth(Double.MAX_VALUE);
            cart.setMaxWidth(Double.MAX_VALUE);
            wish.setMaxWidth(Double.MAX_VALUE);
            detail.setOnAction(event -> showGameDetail(game));
            cart.setOnAction(event -> {
                try {
                    addGameToCart(game);
                } catch (SQLException exception) {
                    UiUtils.showError("Không thêm được vào giỏ hàng", exception);
                }
            });
            wish.setOnAction(event -> {
                try {
                    addGameToWishlist(game);
                } catch (SQLException exception) {
                    UiUtils.showError("Không thêm được wishlist", exception);
                }
            });
            card.getChildren().addAll(detail, cart, wish);
        }
        return card;
    }

    private void addGameToCart(ThongTinGame game) throws SQLException {
        if (quanLyMuaHangController.addToCart(session.profileId(), game.gameId())) {
            UiUtils.showInfo("Đã thêm vào giỏ", game.title() + " đã sẵn sàng để thanh toán.");
        } else {
            UiUtils.showInfo("Đã có trong giỏ", game.title() + " đã nằm trong giỏ hàng.");
        }
    }

    private void addGameToWishlist(ThongTinGame game) throws SQLException {
        if (quanLyMuaHangController.addToWishlist(session.profileId(), game.gameId())) {
            UiUtils.showInfo("Đã thêm wishlist", game.title() + " đã được lưu vào wishlist.");
        } else {
            UiUtils.showInfo("Đã có trong wishlist", game.title() + " đã nằm trong wishlist.");
        }
    }

    private void showGameDetail(ThongTinGame game) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(game.title());

        VBox content = new VBox(14);
        content.getStyleClass().add("game-detail-dialog");
        StackPane coverStack = new StackPane();
        coverStack.getStyleClass().add("game-cover-stack");
        Node cover = gameCoverNode(game);
        coverStack.getChildren().add(cover);

        Label title = new Label(game.title());
        title.getStyleClass().add("launcher-hero-title");
        Label meta = new Label(game.developerName() + " • " + game.genres() + " • " + game.ageRating() + "+");
        meta.getStyleClass().add("stat-label");
        HBox price = priceBox(game);
        Label description = new Label(safe(game.description()));
        description.getStyleClass().add("launcher-hero-body");
        description.setWrapText(true);

        VBox mediaBox = UiUtils.card();
        mediaBox.getChildren().add(UiUtils.sectionTitle("Media game"));
        try {
            List<MediaGame> media = quanLyDanhMucGameController.findMediaByGame(game.gameId());
            if (media.isEmpty()) {
                mediaBox.getChildren().add(new Label("Chưa có media cho game này."));
            } else {
                boolean hasExtraMedia = false;
                for (MediaGame item : media) {
                    if (!"Ảnh bìa".equals(item.mediaType())) {
                        mediaBox.getChildren().add(mediaPreviewNode(item));
                        hasExtraMedia = true;
                    }
                }
                if (!hasExtraMedia) {
                    mediaBox.getChildren().add(new Label("Chưa có ảnh phụ cho game này."));
                }
            }
        } catch (SQLException exception) {
            mediaBox.getChildren().add(new Label(exception.getMessage()));
        }

        Button cart = UiUtils.primaryButton("Thêm vào giỏ");
        Button wish = UiUtils.secondaryButton("Wishlist");
        cart.setOnAction(event -> {
            try {
                addGameToCart(game);
            } catch (SQLException exception) {
                UiUtils.showError("Không thêm được vào giỏ hàng", exception);
            }
        });
        wish.setOnAction(event -> {
            try {
                addGameToWishlist(game);
            } catch (SQLException exception) {
                UiUtils.showError("Không thêm được wishlist", exception);
            }
        });

        content.getChildren().addAll(coverStack, title, meta, price, description, mediaBox, new HBox(10, cart, wish));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private Node mediaPreviewNode(MediaGame item) {
        VBox box = new VBox(6);
        Label title = new Label(item.mediaType());
        title.getStyleClass().add("stat-value");
        Node image = imageNode(item.fileMedia(), 520, 230);
        if (image == null) {
            Label file = new Label(safe(item.fileMedia()));
            file.getStyleClass().add("stat-label");
            file.setWrapText(true);
            box.getChildren().addAll(title, file);
        } else {
            box.getChildren().addAll(title, image);
        }
        return box;
    }

    private HBox priceBox(ThongTinGame game) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        if (game.discountPercent() > 0) {
            Text original = new Text(UiUtils.money(game.originalPrice()));
            original.getStyleClass().add("old-price");
            original.setStrikethrough(true);
            Label sale = new Label(UiUtils.money(game.salePrice()));
            sale.getStyleClass().add("stat-value");
            Label discount = new Label("-" + game.discountPercent() + "%");
            discount.getStyleClass().add("discount-pill");
            box.getChildren().addAll(original, sale, discount);
        } else {
            Label price = new Label(UiUtils.money(game.salePrice()));
            price.getStyleClass().add("stat-value");
            box.getChildren().add(price);
        }
        return box;
    }

    private Node gameCoverNode(ThongTinGame game) {
        Node image = imageNode(game.coverMedia(), 220, 228);
        if (image != null) {
            image.getStyleClass().add("game-cover-image");
            return image;
        }
        Label cover = new Label(game.title());
        cover.getStyleClass().add("game-cover");
        cover.setMaxWidth(Double.MAX_VALUE);
        return cover;
    }

    private Node imageNode(String fileName, double width, double height) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        Path path = resolveLocalPath(fileName);
        if (!Files.exists(path)) {
            return null;
        }
        String lower = path.getFileName().toString().toLowerCase();
        if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".webp"))) {
            return null;
        }
        Image image = new Image(path.toUri().toString(), width, height, false, true, false);
        if (image.isError()) {
            return null;
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);
        return imageView;
    }

    private Path resolveLocalPath(String fileName) {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path path = Path.of(fileName);
        if (path.isAbsolute()) {
            return path;
        }
        path = projectRoot.resolve(path).normalize();
        if (Files.exists(path)) {
            return path;
        }

        String normalized = fileName.replace("\\", "/");
        String migrated = normalized;
        if (normalized.startsWith("images/")) {
            migrated = "anhBiaGame/" + normalized.substring("images/".length());
        } else if (normalized.startsWith("media/uploads/")) {
            migrated = "tepMedia/taiLen/" + normalized.substring("media/uploads/".length());
        } else if (normalized.startsWith("builds/uploads/")) {
            migrated = "tepBuild/taiLen/" + normalized.substring("builds/uploads/".length());
        } else if (normalized.startsWith("builds/")) {
            migrated = "tepBuild/" + normalized.substring("builds/".length());
        }
        return projectRoot.resolve(migrated).normalize();
    }
    private TableView<ThongTinGame> gameTable() {
        TableView<ThongTinGame> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setPrefHeight(520);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, game -> String.valueOf(game.gameId())),
                UiUtils.stringColumn("Tên game", 180, ThongTinGame::title),
                UiUtils.stringColumn("Nhà phát triển", 150, ThongTinGame::developerName),
                UiUtils.stringColumn("Thể loại", 160, ThongTinGame::genres),
                UiUtils.stringColumn("Tuổi", 60, game -> String.valueOf(game.ageRating())),
                UiUtils.stringColumn("Giá gốc", 110, game -> UiUtils.money(game.originalPrice())),
                UiUtils.stringColumn("Giá bán", 110, game -> UiUtils.money(game.salePrice())),
                UiUtils.stringColumn("KM", 70, game -> game.discountPercent() + "%"),
                UiUtils.stringColumn("Trạng thái", 130, ThongTinGame::status),
                UiUtils.stringColumn("Ngày phát hành", 120, game -> UiUtils.date(game.releaseDate())),
                UiUtils.stringColumn("Lượt mua", 90, game -> String.valueOf(game.purchases()))
        );
        return table;
    }

    private TableView<ThongTinGame> wishlistTable() {
        TableView<ThongTinGame> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setPrefHeight(420);
        table.getColumns().addAll(
                UiUtils.stringColumn("Tên game", 220, ThongTinGame::title),
                UiUtils.stringColumn("Nhà phát triển", 180, ThongTinGame::developerName),
                UiUtils.stringColumn("Thể loại", 180, ThongTinGame::genres),
                UiUtils.stringColumn("Giá hiện tại", 120, game -> UiUtils.money(game.salePrice())),
                UiUtils.stringColumn("Khuyến mãi", 100, game -> game.discountPercent() > 0 ? "-" + game.discountPercent() + "%" : "")
        );
        return table;
    }

    private void showWishlist() {
        VBox content = page("Wishlist");
        TableView<ThongTinGame> table = wishlistTable();
        TextField search = input("Tìm theo tên game");
        ComboBox<String> genre = new ComboBox<>();
        Button addToCart = UiUtils.primaryButton("Đưa vào giỏ hàng");
        Button remove = UiUtils.dangerButton("Xóa khỏi wishlist");
        addToCart.setOnAction(event -> {
            ThongTinGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game trong wishlist.");
                return;
            }
            try {
                if (quanLyMuaHangController.addToCart(session.profileId(), selected.gameId())) {
                    UiUtils.showInfo("Đã thêm vào giỏ", selected.title() + " đã được đưa vào giỏ hàng.");
                } else {
                    UiUtils.showInfo("Đã có trong giỏ", selected.title() + " đã nằm trong giỏ hàng.");
                }
            } catch (SQLException exception) {
                UiUtils.showError("Không thêm được vào giỏ", exception);
            }
        });
        remove.setOnAction(event -> {
            ThongTinGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game trong wishlist.");
                return;
            }
            try {
                quanLyMuaHangController.removeFromWishlist(session.profileId(), selected.gameId());
                showWishlist();
            } catch (SQLException exception) {
                UiUtils.showError("Không xóa được wishlist", exception);
            }
        });
        try {
            List<ThongTinGame> wishlist = quanLyMuaHangController.findWishlist(session.profileId());
            genre.getItems().setAll(genreOptions(wishlist.stream().map(ThongTinGame::genres).toList()));
            genre.getSelectionModel().selectFirst();
            Runnable reload = () -> table.getItems().setAll(wishlist.stream()
                    .filter(game -> matchesGame(game, search.getText(), genre.getValue()))
                    .toList());
            search.textProperty().addListener((observable, oldValue, newValue) -> reload.run());
            genre.setOnAction(event -> reload.run());
            reload.run();
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được wishlist", exception));
        }
        HBox filters = new HBox(10, search, genre);
        HBox.setHgrow(search, Priority.ALWAYS);
        content.getChildren().addAll(filters, new HBox(10, addToCart, remove), table);
        setPage(content);
    }

    private void showCart() {
        VBox content = page("Giỏ hàng");
        TableView<GameTrongGioHang> table = new TableView<>();
        table.setPrefHeight(420);
        table.getColumns().addAll(
                UiUtils.stringColumn("Tên game", 240, GameTrongGioHang::title),
                UiUtils.stringColumn("Nhà phát triển", 200, GameTrongGioHang::developerName),
                UiUtils.stringColumn("Giá gốc", 130, item -> UiUtils.money(item.originalPrice())),
                UiUtils.stringColumn("Giá sau khuyến mãi", 160, item -> UiUtils.money(item.salePrice()))
        );
        Label total = new Label();
        total.getStyleClass().add("stat-value");
        TextField discountCode = input("Mã giảm giá nếu có");
        Button remove = UiUtils.dangerButton("Xóa khỏi giỏ");
        Button checkout = UiUtils.primaryButton("Thanh toán");
        Runnable reload = () -> loadCart(table, total);
        remove.setOnAction(event -> {
            GameTrongGioHang selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game trong giỏ hàng.");
                return;
            }
            try {
                quanLyMuaHangController.removeFromCart(session.profileId(), selected.gameId());
                reload.run();
            } catch (SQLException exception) {
                UiUtils.showError("Không xóa được khỏi giỏ", exception);
            }
        });
        checkout.setOnAction(event -> {
            try {
                int transactionId = quanLyMuaHangController.checkoutCart(session.profileId(), discountCode.getText());
                UiUtils.showInfo("Thanh toán thành công", "Giao dịch #" + transactionId + " đã thêm game vào thư viện.");
                discountCode.clear();
                reload.run();
            } catch (SQLException exception) {
                UiUtils.showError("Không thanh toán được", exception);
            }
        });
        reload.run();
        VBox summary = UiUtils.card();
        summary.getChildren().addAll(UiUtils.sectionTitle("Thanh toán qua cổng thanh toán mô phỏng"),
                total, UiUtils.formRow("Mã giảm giá", discountCode), new HBox(10, remove, checkout));
        content.getChildren().addAll(table, summary);
        setPage(content);
    }

    private void loadCart(TableView<GameTrongGioHang> table, Label total) {
        try {
            List<GameTrongGioHang> items = quanLyMuaHangController.findCart(session.profileId());
            table.getItems().setAll(items);
            BigDecimal sum = items.stream()
                    .map(GameTrongGioHang::salePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            total.setText("Tạm tính: " + UiUtils.money(sum));
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được giỏ hàng", exception);
        }
    }

    private void showTransactionHistory() {
        VBox content = page("Lịch sử giao dịch");
        TableView<ThongTinGiaoDich> table = transactionTable();
        try {
            table.getItems().setAll(quanLyMuaHangController.findTransactions(session.profileId()));
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được lịch sử giao dịch", exception));
        }
        content.getChildren().add(table);
        setPage(content);
    }

    private void showAllTransactions() {
        VBox content = page("Danh sách giao dịch");
        TableView<ThongTinGiaoDich> table = transactionTable();
        List<ThongTinGiaoDich> source = new ArrayList<>();
        TextField search = input("Tìm theo mã giao dịch, game, phương thức hoặc trạng thái");
        Button searchButton = UiUtils.secondaryButton("Tìm kiếm");
        Runnable applySearch = () -> table.getItems().setAll(source.stream()
                .filter(transaction -> matchesTransaction(transaction, search.getText()))
                .toList());
        search.textProperty().addListener((observable, oldValue, newValue) -> applySearch.run());
        searchButton.setOnAction(event -> applySearch.run());
        try {
            source.addAll(quanLyMuaHangController.findAllTransactions());
            applySearch.run();
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được danh sách giao dịch", exception));
        }
        HBox filters = new HBox(10, search, searchButton);
        HBox.setHgrow(search, Priority.ALWAYS);
        content.getChildren().addAll(filters, table);
        setPage(content);
    }

    private boolean matchesTransaction(ThongTinGiaoDich transaction, String keyword) {
        return containsText(String.valueOf(transaction.transactionId()), keyword)
                || containsText(transaction.games(), keyword)
                || containsText(transaction.paymentMethod(), keyword)
                || containsText(transaction.status(), keyword);
    }

    private TableView<ThongTinGiaoDich> transactionTable() {
        TableView<ThongTinGiaoDich> table = new TableView<>();
        table.setPrefHeight(520);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã GD", 70, item -> String.valueOf(item.transactionId())),
                UiUtils.stringColumn("Game", 260, ThongTinGiaoDich::games),
                UiUtils.stringColumn("Tổng gốc", 110, item -> UiUtils.money(item.totalOriginal())),
                UiUtils.stringColumn("Giảm", 110, item -> UiUtils.money(item.totalDiscount())),
                UiUtils.stringColumn("Thanh toán", 120, item -> UiUtils.money(item.totalPaid())),
                UiUtils.stringColumn("Phương thức", 120, ThongTinGiaoDich::paymentMethod),
                UiUtils.stringColumn("Ngày GD", 150, item -> UiUtils.dateTime(item.createdAt())),
                UiUtils.stringColumn("Trạng thái", 120, ThongTinGiaoDich::status)
        );
        return table;
    }

    private void showLibrary() {
        VBox content = page("Thư viện của tôi");
        try {
            List<GameTrongThuVien> library = quanLyThongTinGameController.findLibrary(session.profileId());
            TextField search = input("Tìm theo tên game");
            ComboBox<String> genre = genreFilter(library.stream().map(GameTrongThuVien::genres).toList());
            TilePane tilePane = libraryTilePane(library);
            Runnable reload = () -> {
                List<GameTrongThuVien> filtered = library.stream()
                        .filter(game -> matchesLibrary(game, search.getText(), genre.getValue()))
                        .toList();
                tilePane.getChildren().setAll(filtered.stream().map(this::libraryCard).toList());
            };
            search.textProperty().addListener((observable, oldValue, newValue) -> reload.run());
            genre.setOnAction(event -> reload.run());
            HBox filters = new HBox(10, search, genre);
            HBox.setHgrow(search, Priority.ALWAYS);
            Label count = new Label(library.size() + " game trong thư viện");
            count.getStyleClass().add("stat-label");
            content.getChildren().addAll(filters, count, tilePane);
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được thư viện", exception));
        }
        setPage(content);
    }

    private TilePane libraryTilePane(List<GameTrongThuVien> games) {
        TilePane tilePane = new TilePane();
        tilePane.setHgap(14);
        tilePane.setVgap(14);
        tilePane.setPrefColumns(3);
        for (GameTrongThuVien game : games) {
            tilePane.getChildren().add(libraryCard(game));
        }
        return tilePane;
    }

    private VBox libraryCard(GameTrongThuVien game) {
        VBox card = new VBox(10);
        card.getStyleClass().add("game-card");
        card.setPrefWidth(230);
        card.setMinHeight(318);

        StackPane coverStack = new StackPane();
        coverStack.getStyleClass().add("game-cover-stack");
        Label cover = new Label(game.title());
        cover.getStyleClass().add("game-cover");
        cover.setMaxWidth(Double.MAX_VALUE);
        Label owned = new Label("OWNED");
        owned.getStyleClass().add("beta-ribbon");
        StackPane.setAlignment(owned, Pos.TOP_CENTER);
        coverStack.getChildren().addAll(cover, owned);

        Label developer = new Label(game.developerName());
        developer.getStyleClass().add("stat-label");
        Label genres = new Label(game.genres());
        genres.getStyleClass().add("stat-label");
        Label played = new Label(game.playHours() + " giờ chơi");
        played.getStyleClass().add("stat-value");
        Label rating = new Label(game.rating() == null ? "Chưa đánh giá" : "Đánh giá: " + game.rating() + "/5");
        rating.getStyleClass().add("stat-label");
        Label ownedAt = new Label("Sở hữu: " + UiUtils.dateTime(game.ownedAt()));
        ownedAt.getStyleClass().add("stat-label");

        Button download = UiUtils.primaryButton("Tải game");
        Button review = UiUtils.secondaryButton("Đánh giá");
        Button deleteReview = UiUtils.dangerButton("Xóa đánh giá");
        download.setMaxWidth(Double.MAX_VALUE);
        review.setMaxWidth(Double.MAX_VALUE);
        deleteReview.setMaxWidth(Double.MAX_VALUE);
        download.setOnAction(event -> downloadBuildFile(game));
        review.setOnAction(event -> showReviewDialog(game));
        deleteReview.setOnAction(event -> {
            try {
                quanLyThongTinGameController.deleteReview(session.profileId(), game.gameId());
                showLibrary();
            } catch (SQLException exception) {
                UiUtils.showError("Không xóa được đánh giá", exception);
            }
        });

        card.getChildren().addAll(coverStack, developer, genres, played, rating, ownedAt, download, review, deleteReview);
        return card;
    }

    private void showReviewDialog(GameTrongThuVien game) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đánh giá " + game.title());
        Spinner<Integer> rating = new Spinner<>(1, 5, game.rating() == null ? 5 : game.rating());
        TextArea review = new TextArea();
        review.getStyleClass().add("input");
        review.setPromptText("Nội dung đánh giá");
        review.setPrefRowCount(4);
        VBox form = new VBox(10, UiUtils.formRow("Điểm", rating), review);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> {
            try {
                quanLyThongTinGameController.rateGame(session.profileId(), game.gameId(), rating.getValue(), review.getText());
                UiUtils.showInfo("Đã lưu đánh giá", "Đánh giá của người chơi đã được cập nhật.");
                showLibrary();
            } catch (SQLException exception) {
                UiUtils.showError("Không lưu được đánh giá", exception);
            }
        });
    }

    private void showPlayerTickets() {
        VBox content = page("Ticket hỗ trợ của tôi");
        TableView<ThongTinTicket> table = ticketTable();
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Thanh toán", "Lỗi game", "Báo cáo vi phạm", "Khác");
        type.getSelectionModel().selectFirst();
        TextArea body = new TextArea();
        body.getStyleClass().add("input");
        body.setPromptText("Mô tả yêu cầu hỗ trợ");
        body.setPrefRowCount(3);
        ComboBox<GameTrongThuVien> gameBox = new ComboBox<>();
        gameBox.setPromptText("Chọn game liên quan nếu có");
        gameBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(GameTrongThuVien item) {
                return item == null ? "" : item.title() + " (#" + item.gameId() + ")";
            }

            @Override
            public GameTrongThuVien fromString(String string) {
                return null;
            }
        });
        try {
            gameBox.getItems().addAll(quanLyThongTinGameController.findLibrary(session.profileId()));
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được danh sách game trong thư viện", exception));
        }
        TextField transactionId = input("Mã giao dịch nếu có");
        Button create = UiUtils.primaryButton("Gửi ticket");
        create.setOnAction(event -> {
            try {
                GameTrongThuVien selectedGame = gameBox.getSelectionModel().getSelectedItem();
                quanLyTicketHoTroController.taoTicket(
                        session.profileId(),
                        type.getValue(),
                        body.getText(),
                        selectedGame == null ? null : selectedGame.gameId(),
                        nullableInt(transactionId)
                );
                body.clear();
                table.getItems().setAll(quanLyTicketHoTroController.traCuuTicketTheoNguoiChoi(session.profileId()));
            } catch (Exception exception) {
                UiUtils.showError("Không gửi được ticket", exception);
            }
        });
        try {
            table.getItems().setAll(quanLyTicketHoTroController.traCuuTicketTheoNguoiChoi(session.profileId()));
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được ticket", exception));
        }
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Tạo ticket"), UiUtils.formRow("Loại yêu cầu", type), body,
                UiUtils.formRow("Game", gameBox), UiUtils.formRow("Giao dịch", transactionId), create);
        content.getChildren().addAll(table, form);
        setPage(content);
    }

    private TableView<ThongTinTicket> ticketTable() {
        TableView<ThongTinTicket> table = new TableView<>();
        table.setPrefHeight(440);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, ticket -> String.valueOf(ticket.ticketId())),
                UiUtils.stringColumn("Loại", 120, ThongTinTicket::type),
                UiUtils.stringColumn("Nội dung", 260, ticket -> safe(ticket.content())),
                UiUtils.stringColumn("Người chơi", 130, ThongTinTicket::playerName),
                UiUtils.stringColumn("Game", 140, ticket -> safe(ticket.gameTitle())),
                UiUtils.stringColumn("GD", 80, ticket -> safe(ticket.transactionId())),
                UiUtils.stringColumn("Ngày tạo", 150, ticket -> UiUtils.dateTime(ticket.createdAt())),
                UiUtils.stringColumn("Trạng thái", 120, ThongTinTicket::status),
                UiUtils.stringColumn("Nhân viên xử lý", 150, ticket -> safe(ticket.handledByName())),
                UiUtils.stringColumn("Phản hồi", 280, ticket -> safe(ticket.response()))
        );
        return table;
    }

    private void showGameCuaNhaPhatTriens() {
        VBox content = page("Quản lý game của tôi");
        content.getChildren().add(developerGamesContent());
        setPage(content);
    }

    private void showGameCuaNhaPhatTrienManagement() {
        VBox content = page("Quản lý game của tôi");
        content.getChildren().add(developerGamesContent());
        setPage(content);
    }

    private VBox developerGamesContent() {
        VBox content = new VBox(14);
        TableView<GameCuaNhaPhatTrien> table = developerGameTable();
        Spinner<Integer> age = new Spinner<>(0, 18, 12);
        TextField price = input("Giá gốc mới");
        TextArea description = new TextArea();
        description.getStyleClass().add("input");
        description.setPromptText("Mô tả cập nhật");
        description.setPrefRowCount(3);
        Button update = UiUtils.primaryButton("Cập nhật game");
        Button versions = UiUtils.secondaryButton("Quản lý phiên bản game");
        Button media = UiUtils.secondaryButton("Quản lý media game");
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null) {
                price.setText(selected.originalPrice().toPlainString());
            }
        });
        update.setOnAction(event -> {
            GameCuaNhaPhatTrien selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game của nhà phát triển.");
                return;
            }
            try {
                quanLyDanhMucGameController.updateDeveloperGame(session.profileId(), selected.gameId(),
                        age.getValue(), moneyInput(price), description.getText());
                table.getItems().setAll(quanLyThongTinGameController.findGamesByDeveloper(session.profileId()));
            } catch (Exception exception) {
                UiUtils.showError("Không cập nhật được game", exception);
            }
        });
        versions.setOnAction(event -> {
            GameCuaNhaPhatTrien selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game trước khi quản lý phiên bản.");
                return;
            }
            showDeveloperVersionsForGame(selected);
        });
        media.setOnAction(event -> {
            GameCuaNhaPhatTrien selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game trước khi quản lý media.");
                return;
            }
            showDeveloperMediaForGame(selected);
        });
        try {
            table.getItems().setAll(quanLyThongTinGameController.findGamesByDeveloper(session.profileId()));
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được game", exception));
        }
        HBox actions = new HBox(10, versions, media);
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Cập nhật thông tin game"),
                UiUtils.formRow("Độ tuổi", age),
                UiUtils.formRow("Giá gốc", price), UiUtils.formRow("Mô tả", description), update);
        content.getChildren().addAll(table, actions, form);
        return content;
    }

    private TableView<GameCuaNhaPhatTrien> developerGameTable() {
        TableView<GameCuaNhaPhatTrien> table = new TableView<>();
        table.setPrefHeight(520);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, game -> String.valueOf(game.gameId())),
                UiUtils.stringColumn("Tên game", 220, GameCuaNhaPhatTrien::title),
                UiUtils.stringColumn("Giá gốc", 120, game -> UiUtils.money(game.originalPrice())),
                UiUtils.stringColumn("Trạng thái", 130, GameCuaNhaPhatTrien::status),
                UiUtils.stringColumn("Ngày phát hành", 130, game -> UiUtils.date(game.releaseDate())),
                UiUtils.stringColumn("Lượt mua", 90, game -> String.valueOf(game.purchases()))
        );
        return table;
    }

    private void showSubmitGame() {
        VBox content = page("Gửi game mới chờ kiểm duyệt");
        content.getChildren().add(submitGameForm());
        setPage(content);
    }

    private VBox submitGameForm() {
        VBox form = UiUtils.card();
        TextField title = input("Tên game");
        Spinner<Integer> age = new Spinner<>(0, 18, 12);
        TextField price = input("Giá gốc");
        TextArea description = new TextArea();
        description.getStyleClass().add("input");
        description.setPromptText("Mô tả game");
        description.setPrefRowCount(4);
        TextField version = input("Tên phiên bản, ví dụ 1.0.0");
        TextField file = input("File bản build, ví dụ tepBuild/taiLen/game.zip");
        TextField size = input("Dung lượng tối thiểu (MB)");
        TextField cover = input("Ảnh bìa, ví dụ tepMedia/taiLen/cover.jpg");
        Button chooseCover = UiUtils.secondaryButton("Chọn ảnh bìa");
        Button chooseBuild = UiUtils.secondaryButton("Chọn file build");
        chooseCover.setOnAction(event -> {
            try {
                String uploaded = chooseAndCopyImage();
                if (uploaded != null) {
                    cover.setText(uploaded);
                }
            } catch (IOException exception) {
                UiUtils.showError("Không tải được ảnh bìa", exception);
            }
        });
        chooseBuild.setOnAction(event -> {
            try {
                String uploaded = chooseAndCopyBuildFile();
                if (uploaded != null) {
                    file.setText(uploaded);
                }
            } catch (IOException exception) {
                UiUtils.showError("Không tải được file build", exception);
            }
        });
        HBox fileRow = new HBox(10, file, chooseBuild);
        HBox.setHgrow(file, Priority.ALWAYS);
        HBox coverRow = new HBox(10, cover, chooseCover);
        HBox.setHgrow(cover, Priority.ALWAYS);
        Button submit = UiUtils.primaryButton("Tạo yêu cầu phát hành");
        submit.setOnAction(event -> {
            try {
                int requestId = quanLyThongTinGameController.submitGame(
                        session.profileId(),
                        title.getText(),
                        age.getValue(),
                        moneyInput(price),
                        description.getText(),
                        version.getText(),
                        file.getText(),
                        moneyInput(size),
                        cover.getText()
                );
                UiUtils.showInfo("Đã gửi kiểm duyệt", "Yêu cầu phát hành #" + requestId + " đã ở trạng thái Chờ duyệt.");
                title.clear();
                price.clear();
                description.clear();
                version.clear();
                file.clear();
                size.clear();
                cover.clear();
            } catch (Exception exception) {
                UiUtils.showError("Không tạo được yêu cầu", exception);
            }
        });
        form.getChildren().addAll(
                UiUtils.formRow("Tên game", title),
                UiUtils.formRow("Độ tuổi", age),
                UiUtils.formRow("Giá gốc", price),
                UiUtils.formRow("Mô tả", description),
                UiUtils.formRow("Phiên bản", version),
                UiUtils.formRow("File build", fileRow),
                UiUtils.formRow("Dung lượng tối thiểu (MB)", size),
                UiUtils.formRow("Ảnh bìa", coverRow),
                submit
        );
        return form;
    }

    private void showDeveloperVersions() {
        VBox content = page("Quản lý phiên bản game");
        content.getChildren().add(versionsContent(session.profileId(), true));
        setPage(content);
    }

    private void showDeveloperVersionsForGame(GameCuaNhaPhatTrien game) {
        VBox content = page("Quản lý phiên bản game - " + game.title());
        Button back = UiUtils.secondaryButton("Quay lại danh sách game");
        back.setOnAction(event -> showGameCuaNhaPhatTrienManagement());
        content.getChildren().addAll(back, versionsContentForGame(game));
        setPage(content);
    }

    private void showAllVersions() {
        showVersions("Tra cứu phiên bản game", null, false);
    }

    private void showVersions(String title, Integer developerId, boolean allowCreate) {
        VBox content = page(title);
        content.getChildren().add(versionsContent(developerId, allowCreate));
        setPage(content);
    }

    private VBox versionsContent(Integer developerId, boolean allowCreate) {
        VBox content = new VBox(14);
        TableView<PhienBanGame> table = versionTable();
        try {
            table.getItems().setAll(quanLyDanhMucGameController.findVersions(developerId));
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được phiên bản game", exception));
        }
        content.getChildren().add(table);
        if (allowCreate) {
            ComboBox<GameCuaNhaPhatTrien> gameBox = developerGameCombo();
            TextField versionName = input("Tên phiên bản, ví dụ 1.1.0");
            TextArea versionContent = new TextArea();
            versionContent.getStyleClass().add("input");
            versionContent.setPromptText("Nội dung phiên bản");
            versionContent.setPrefRowCount(3);
            TextField file = input("File build");
            TextField size = input("Dung lượng");
            Button chooseBuild = UiUtils.secondaryButton("Chọn file build");
            chooseBuild.setOnAction(event -> {
                try {
                    String uploaded = chooseAndCopyBuildFile();
                    if (uploaded != null) {
                        file.setText(uploaded);
                    }
                } catch (IOException exception) {
                    UiUtils.showError("Không tải được file build", exception);
                }
            });
            HBox fileRow = new HBox(10, file, chooseBuild);
            HBox.setHgrow(file, Priority.ALWAYS);
            Button create = UiUtils.primaryButton("Thêm phiên bản và gửi duyệt");
            create.setOnAction(event -> {
                GameCuaNhaPhatTrien selected = gameBox.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn game", "Vui lòng chọn game cần tạo phiên bản.");
                    return;
                }
                try {
                    int requestId = quanLyDanhMucGameController.addVersionAndReleaseRequest(session.profileId(), selected.gameId(),
                            versionName.getText(), versionContent.getText(), file.getText(), moneyInput(size));
                    UiUtils.showInfo("Đã gửi yêu cầu", "Yêu cầu phát hành #" + requestId + " đã được tạo.");
                    table.getItems().setAll(quanLyDanhMucGameController.findVersions(developerId));
                } catch (Exception exception) {
                    UiUtils.showError("Không thêm được phiên bản", exception);
                }
            });
            VBox form = UiUtils.card();
            form.getChildren().addAll(UiUtils.sectionTitle("Thêm phiên bản game"),
                    UiUtils.formRow("Game", gameBox),
                    UiUtils.formRow("Phiên bản", versionName),
                    UiUtils.formRow("Nội dung", versionContent),
                    UiUtils.formRow("File", fileRow),
                    UiUtils.formRow("Dung lượng", size),
                    create);
            content.getChildren().add(form);
        }
        return content;
    }

    private VBox versionsContentForGame(GameCuaNhaPhatTrien game) {
        VBox content = new VBox(14);
        TableView<PhienBanGame> table = versionTable();
        Runnable reload = () -> {
            try {
                table.getItems().setAll(quanLyDanhMucGameController.findVersionsByGame(game.gameId()));
            } catch (SQLException exception) {
                UiUtils.showError("Không tải được phiên bản game", exception);
            }
        };
        reload.run();
        content.getChildren().add(table);

        TextField versionName = input("Tên phiên bản, ví dụ 1.1.0");
        TextArea versionContent = new TextArea();
        versionContent.getStyleClass().add("input");
        versionContent.setPromptText("Nội dung phiên bản");
        versionContent.setPrefRowCount(3);
        TextField file = input("File build");
        TextField size = input("Dung lượng");
        Button chooseBuild = UiUtils.secondaryButton("Chọn file build");
        chooseBuild.setOnAction(event -> {
            try {
                String uploaded = chooseAndCopyBuildFile();
                if (uploaded != null) {
                    file.setText(uploaded);
                }
            } catch (IOException exception) {
                UiUtils.showError("Không tải được file build", exception);
            }
        });
        HBox fileRow = new HBox(10, file, chooseBuild);
        HBox.setHgrow(file, Priority.ALWAYS);
        Button create = UiUtils.primaryButton("Thêm phiên bản và gửi duyệt");
        create.setOnAction(event -> {
            try {
                int requestId = quanLyDanhMucGameController.addVersionAndReleaseRequest(session.profileId(), game.gameId(),
                        versionName.getText(), versionContent.getText(), file.getText(), moneyInput(size));
                UiUtils.showInfo("Đã gửi yêu cầu", "Yêu cầu phát hành #" + requestId + " đã được tạo.");
                versionName.clear();
                versionContent.clear();
                file.clear();
                size.clear();
                reload.run();
            } catch (Exception exception) {
                UiUtils.showError("Không thêm được phiên bản", exception);
            }
        });
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Thêm phiên bản cho " + game.title()),
                UiUtils.formRow("Phiên bản", versionName),
                UiUtils.formRow("Nội dung", versionContent),
                UiUtils.formRow("File", fileRow),
                UiUtils.formRow("Dung lượng", size),
                create);
        content.getChildren().add(form);
        return content;
    }

    private TableView<PhienBanGame> versionTable() {
        TableView<PhienBanGame> table = new TableView<>();
        table.setPrefHeight(420);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã PB", 70, item -> String.valueOf(item.versionId())),
                UiUtils.stringColumn("Game", 180, PhienBanGame::gameTitle),
                UiUtils.stringColumn("Phiên bản", 110, PhienBanGame::versionName),
                UiUtils.stringColumn("File", 180, item -> safe(item.fileVersion())),
                UiUtils.stringColumn("Dung lượng", 100, item -> item.sizeMb().toPlainString() + " MB"),
                UiUtils.stringColumn("Ngày tạo", 110, item -> UiUtils.date(item.createdAt())),
                UiUtils.stringColumn("Trạng thái", 130, PhienBanGame::status),
                UiUtils.stringColumn("Nội dung", 260, item -> safe(item.content()))
        );
        return table;
    }

    private void showDeveloperMedia() {
        VBox content = page("Quản lý media game");
        content.getChildren().add(mediaContent(session.profileId(), true));
        setPage(content);
    }

    private void showDeveloperMediaForGame(GameCuaNhaPhatTrien game) {
        VBox content = page("Quản lý media game - " + game.title());
        Button back = UiUtils.secondaryButton("Quay lại danh sách game");
        back.setOnAction(event -> showGameCuaNhaPhatTrienManagement());
        content.getChildren().addAll(back, mediaContentForGame(game));
        setPage(content);
    }

    private void showAllMedia() {
        showMedia("Tra cứu media game", null, false);
    }

    private void showMedia(String title, Integer developerId, boolean allowEdit) {
        VBox content = page(title);
        content.getChildren().add(mediaContent(developerId, allowEdit));
        setPage(content);
    }

    private VBox mediaContent(Integer developerId, boolean allowEdit) {
        VBox content = new VBox(14);
        TableView<MediaGame> table = mediaTable();
        Runnable reload = () -> loadMedia(table, developerId);
        reload.run();
        content.getChildren().add(table);
        if (allowEdit) {
            ComboBox<GameCuaNhaPhatTrien> gameBox = developerGameCombo();
            ComboBox<String> mediaType = new ComboBox<>();
            mediaType.getItems().addAll("Ảnh bìa", "Ảnh phụ", "Video");
            mediaType.getSelectionModel().selectFirst();
            TextField file = input("Đường dẫn file media");
            Button chooseFile = UiUtils.secondaryButton("Chọn ảnh từ máy");
            Button add = UiUtils.primaryButton("Thêm media");
            Button update = UiUtils.secondaryButton("Cập nhật media");
            Button delete = UiUtils.dangerButton("Xóa media");
            table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
                if (selected != null) {
                    mediaType.getSelectionModel().select(selected.mediaType());
                    file.setText(selected.fileMedia());
                }
            });
            chooseFile.setOnAction(event -> {
                try {
                    String uploaded = chooseAndCopyImage();
                    if (uploaded != null) {
                        file.setText(uploaded);
                        mediaType.getSelectionModel().select("Ảnh bìa");
                    }
                } catch (IOException exception) {
                    UiUtils.showError("Không tải được ảnh", exception);
                }
            });
            add.setOnAction(event -> {
                GameCuaNhaPhatTrien selected = gameBox.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn game", "Vui lòng chọn game để thêm media.");
                    return;
                }
                try {
                    quanLyDanhMucGameController.addMedia(selected.gameId(), mediaType.getValue(), file.getText());
                    reload.run();
                } catch (SQLException exception) {
                    UiUtils.showError("Không thêm được media", exception);
                }
            });
            update.setOnAction(event -> {
                MediaGame selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn media", "Vui lòng chọn media cần cập nhật.");
                    return;
                }
                try {
                    quanLyDanhMucGameController.updateMedia(selected.mediaId(), mediaType.getValue(), file.getText());
                    reload.run();
                } catch (SQLException exception) {
                    UiUtils.showError("Không cập nhật được media", exception);
                }
            });
            delete.setOnAction(event -> {
                MediaGame selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn media", "Vui lòng chọn media cần xóa.");
                    return;
                }
                try {
                    quanLyDanhMucGameController.deleteMedia(selected.mediaId());
                    reload.run();
                } catch (SQLException exception) {
                    UiUtils.showError("Không xóa được media", exception);
                }
            });
            VBox form = UiUtils.card();
            form.getChildren().addAll(UiUtils.sectionTitle("Media game"),
                    UiUtils.formRow("Game", gameBox), UiUtils.formRow("Loại media", mediaType),
                    UiUtils.formRow("File", file), new HBox(10, chooseFile, add, update, delete));
            content.getChildren().add(form);
        }
        return content;
    }

    private VBox mediaContentForGame(GameCuaNhaPhatTrien game) {
        VBox content = new VBox(14);
        TableView<MediaGame> table = mediaTable();
        Runnable reload = () -> {
            try {
                table.getItems().setAll(quanLyDanhMucGameController.findMediaByGame(game.gameId()));
            } catch (SQLException exception) {
                UiUtils.showError("Không tải được media", exception);
            }
        };
        reload.run();
        content.getChildren().add(table);

        ComboBox<String> mediaType = new ComboBox<>();
        mediaType.getItems().addAll("Ảnh bìa", "Ảnh phụ", "Video");
        mediaType.getSelectionModel().selectFirst();
        TextField file = input("Đường dẫn file media");
        Button chooseFile = UiUtils.secondaryButton("Chọn ảnh từ máy");
        Button add = UiUtils.primaryButton("Thêm media");
        Button update = UiUtils.secondaryButton("Cập nhật media");
        Button delete = UiUtils.dangerButton("Xóa media");
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null) {
                mediaType.getSelectionModel().select(selected.mediaType());
                file.setText(selected.fileMedia());
            }
        });
        chooseFile.setOnAction(event -> {
            try {
                String uploaded = chooseAndCopyImage();
                if (uploaded != null) {
                    file.setText(uploaded);
                    mediaType.getSelectionModel().select("Ảnh bìa");
                }
            } catch (IOException exception) {
                UiUtils.showError("Không tải được ảnh", exception);
            }
        });
        add.setOnAction(event -> {
            try {
                quanLyDanhMucGameController.addMedia(game.gameId(), mediaType.getValue(), file.getText());
                reload.run();
            } catch (SQLException exception) {
                UiUtils.showError("Không thêm được media", exception);
            }
        });
        update.setOnAction(event -> {
            MediaGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn media", "Vui lòng chọn media cần cập nhật.");
                return;
            }
            try {
                quanLyDanhMucGameController.updateMedia(selected.mediaId(), mediaType.getValue(), file.getText());
                reload.run();
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được media", exception);
            }
        });
        delete.setOnAction(event -> {
            MediaGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn media", "Vui lòng chọn media cần xóa.");
                return;
            }
            try {
                quanLyDanhMucGameController.deleteMedia(selected.mediaId());
                reload.run();
            } catch (SQLException exception) {
                UiUtils.showError("Không xóa được media", exception);
            }
        });
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Media của " + game.title()),
                UiUtils.formRow("Loại media", mediaType),
                UiUtils.formRow("File", file),
                new HBox(10, chooseFile, add, update, delete));
        content.getChildren().add(form);
        return content;
    }

    private void loadMedia(TableView<MediaGame> table, Integer developerId) {
        try {
            table.getItems().setAll(quanLyDanhMucGameController.findMedia(developerId));
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được media", exception);
        }
    }

    private String lowerCamelUploadName(File source, String fallbackStem) {
        String originalName = source.getName();
        int dotIndex = originalName.lastIndexOf('.');
        String rawStem = dotIndex > 0 ? originalName.substring(0, dotIndex) : originalName;
        String extension = dotIndex > 0 ? originalName.substring(dotIndex).toLowerCase() : "";
        String[] parts = rawStem.replaceAll("[^a-zA-Z0-9]+", " ").trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            String lower = part.toLowerCase();
            if (builder.isEmpty()) {
                if (Character.isDigit(lower.charAt(0))) {
                    builder.append(fallbackStem);
                }
                builder.append(lower);
            } else {
                builder.append(Character.toUpperCase(lower.charAt(0))).append(lower.substring(1));
            }
        }
        if (builder.isEmpty()) {
            builder.append(fallbackStem);
        }
        builder.append(System.currentTimeMillis());
        return builder.append(extension).toString();
    }

    private String chooseAndCopyImage() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh game");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ảnh", "*.png", "*.jpg", "*.jpeg", "*.webp"));
        File source = chooser.showOpenDialog(getScene().getWindow());
        if (source == null) {
            return null;
        }
        Path uploadDir = Path.of("tepMedia", "taiLen");
        Files.createDirectories(uploadDir);
        String fileName = lowerCamelUploadName(source, "anhTaiLen");
        Path target = uploadDir.resolve(fileName);
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString().replace("\\", "/");
    }

    private String chooseAndCopyBuildFile() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file build game");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File build", "*.zip", "*.rar", "*.7z", "*.exe", "*.jar"),
                new FileChooser.ExtensionFilter("Tất cả file", "*.*")
        );
        File source = chooser.showOpenDialog(getScene().getWindow());
        if (source == null) {
            return null;
        }
        Path uploadDir = Path.of("tepBuild", "taiLen");
        Files.createDirectories(uploadDir);
        String fileName = lowerCamelUploadName(source, "banBuildTaiLen");
        Path target = uploadDir.resolve(fileName);
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString().replace("\\", "/");
    }

    private void downloadBuildFile(GameTrongThuVien game) {
        String fileVersion = game.fileVersion();
        if (fileVersion == null || fileVersion.isBlank()) {
            UiUtils.showInfo("Chưa có file build", "Game này chưa có file build để tải.");
            return;
        }
        try {
            Path source = Path.of(fileVersion);
            if (!source.isAbsolute()) {
                source = Path.of("").toAbsolutePath().resolve(source);
            }
            if (!Files.exists(source)) {
                UiUtils.showInfo("Không tìm thấy file", "Không tìm thấy file build: " + fileVersion);
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Lưu file game");
            chooser.setInitialFileName(source.getFileName().toString());
            File target = chooser.showSaveDialog(getScene().getWindow());
            if (target == null) {
                return;
            }
            Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            UiUtils.showInfo("Tải game", "Đã tải " + game.title() + " về " + target.getAbsolutePath());
        } catch (IOException exception) {
            UiUtils.showError("Không tải được file game", exception);
        }
    }

    private TableView<MediaGame> mediaTable() {
        TableView<MediaGame> table = new TableView<>();
        table.setPrefHeight(420);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, item -> String.valueOf(item.mediaId())),
                UiUtils.stringColumn("Game", 180, MediaGame::gameTitle),
                UiUtils.stringColumn("Loại", 110, MediaGame::mediaType),
                UiUtils.stringColumn("File", 360, MediaGame::fileMedia)
        );
        return table;
    }

    private ComboBox<GameCuaNhaPhatTrien> developerGameCombo() {
        ComboBox<GameCuaNhaPhatTrien> comboBox = new ComboBox<>();
        comboBox.setPromptText("Chọn game");
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(GameCuaNhaPhatTrien game) {
                return game == null ? "" : game.title() + " (#" + game.gameId() + ")";
            }

            @Override
            public GameCuaNhaPhatTrien fromString(String string) {
                return null;
            }
        });
        try {
            comboBox.getItems().setAll(quanLyThongTinGameController.findGamesByDeveloper(session.profileId()));
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được game của nhà phát triển", exception);
        }
        return comboBox;
    }

    private void showDeveloperRequests() {
        showDeveloperReleaseManagement();
    }

    private void showDeveloperReleaseManagement() {
        VBox content = page("Yêu cầu phát hành game");
        TabPane tabs = new TabPane();
        Tab requests = new Tab("Yêu cầu của tôi", releaseRequestsContent(false, true));
        Tab submit = new Tab("Gửi game mới", submitGameForm());
        requests.setClosable(false);
        submit.setClosable(false);
        tabs.getTabs().addAll(requests, submit);
        content.getChildren().add(tabs);
        setPage(content);
    }

    private void showAllYeuCauPhatHanhs() {
        showYeuCauPhatHanhs("Yêu cầu phát hành", false, false);
    }

    private void showModeratorRequests() {
        showYeuCauPhatHanhs("Duyệt phát hành game", true, false);
    }

    private void showYeuCauPhatHanhs(String title, boolean pendingOnly, boolean developerOnly) {
        VBox content = page(title);
        content.getChildren().add(releaseRequestsContent(pendingOnly, developerOnly));
        setPage(content);
    }

    private VBox releaseRequestsContent(boolean pendingOnly, boolean developerOnly) {
        VBox content = new VBox(14);
        TableView<YeuCauPhatHanh> table = releaseTable();
        List<YeuCauPhatHanh> source = new ArrayList<>();
        TextField search = input("Tìm theo tên game hoặc nhà phát triển");
        Button searchButton = UiUtils.secondaryButton("Tìm kiếm");
        Runnable applySearch = () -> table.getItems().setAll(source.stream()
                .filter(request -> matchesYeuCauPhatHanh(request, search.getText()))
                .toList());
        search.textProperty().addListener((observable, oldValue, newValue) -> applySearch.run());
        searchButton.setOnAction(event -> applySearch.run());
        HBox searchBar = new HBox(10, search, searchButton);
        HBox.setHgrow(search, Priority.ALWAYS);
        HBox actions = new HBox(10);

        if (session.isVaiTroNhanVien(VaiTroNhanVien.KIEM_DUYET_VIEN)) {
            Button approve = UiUtils.primaryButton("Duyệt");
            Button reject = UiUtils.dangerButton("Từ chối");
            approve.setOnAction(event -> {
                YeuCauPhatHanh selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn yêu cầu", "Vui lòng chọn yêu cầu phát hành.");
                    return;
                }
                try {
                    quanLyYeuCauPhatHanhController.approve(selected.requestId(), session.profileId());
                    source.clear();
                    source.addAll(quanLyYeuCauPhatHanhController.findPending());
                    applySearch.run();
                } catch (SQLException exception) {
                    UiUtils.showError("Không duyệt được yêu cầu", exception);
                }
            });
            reject.setOnAction(event -> {
                YeuCauPhatHanh selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn yêu cầu", "Vui lòng chọn yêu cầu phát hành.");
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Lý do từ chối");
                dialog.setHeaderText("Nhập lý do từ chối yêu cầu #" + selected.requestId());
                dialog.showAndWait().ifPresent(reason -> {
                    try {
                        quanLyYeuCauPhatHanhController.reject(selected.requestId(), session.profileId(), reason);
                        source.clear();
                        source.addAll(quanLyYeuCauPhatHanhController.findPending());
                        applySearch.run();
                    } catch (SQLException exception) {
                        UiUtils.showError("Không từ chối được yêu cầu", exception);
                    }
                });
            });
            actions.getChildren().addAll(approve, reject);
        }

        try {
            if (developerOnly) {
                source.addAll(quanLyYeuCauPhatHanhController.findByDeveloper(session.profileId()));
            } else if (pendingOnly) {
                source.addAll(quanLyYeuCauPhatHanhController.findPending());
            } else {
                source.addAll(quanLyYeuCauPhatHanhController.findAll());
            }
            applySearch.run();
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được yêu cầu phát hành", exception));
        }
        content.getChildren().addAll(searchBar, actions, table);
        return content;
    }

    private boolean matchesYeuCauPhatHanh(YeuCauPhatHanh request, String keyword) {
        return containsText(request.gameTitle(), keyword)
                || containsText(request.developerName(), keyword)
                || containsText(request.versionName(), keyword);
    }

    private TableView<YeuCauPhatHanh> releaseTable() {
        TableView<YeuCauPhatHanh> table = new TableView<>();
        table.setPrefHeight(520);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã YC", 70, request -> String.valueOf(request.requestId())),
                UiUtils.stringColumn("Game", 190, YeuCauPhatHanh::gameTitle),
                UiUtils.stringColumn("Nhà phát triển", 160, YeuCauPhatHanh::developerName),
                UiUtils.stringColumn("Phiên bản", 100, request -> safe(request.versionName())),
                UiUtils.stringColumn("Ngày yêu cầu", 150, request -> UiUtils.dateTime(request.requestedAt())),
                UiUtils.stringColumn("Trạng thái", 120, YeuCauPhatHanh::status),
                UiUtils.stringColumn("NV xử lý", 140, request -> safe(request.handledByName())),
                UiUtils.stringColumn("Lý do từ chối", 220, request -> safe(request.rejectReason()))
        );
        return table;
    }

    private void showGameManagement() {
        VBox content = page(session.isVaiTroNhanVien(VaiTroNhanVien.CSKH)
                ? "Danh sách game"
                : session.isVaiTroNhanVien(VaiTroNhanVien.MARKETING)
                ? "Danh sách game trên nền tảng"
                : "Kho game trên nền tảng");
        TableView<ThongTinGame> table = gameTable();
        List<ThongTinGame> source = new ArrayList<>();
        TextField search = input("Tìm theo tên game, nhà phát triển hoặc thể loại");
        ComboBox<String> genre = new ComboBox<>();
        genre.getItems().add("Tất cả thể loại");
        genre.getSelectionModel().selectFirst();
        Button searchButton = UiUtils.secondaryButton("Tìm kiếm");
        Runnable applySearch = () -> table.getItems().setAll(source.stream()
                .filter(game -> matchesGame(game, search.getText(), genre.getValue()))
                .toList());
        search.textProperty().addListener((observable, oldValue, newValue) -> applySearch.run());
        genre.setOnAction(event -> applySearch.run());
        searchButton.setOnAction(event -> applySearch.run());
        try {
            source.addAll(quanLyThongTinGameController.findAllGames());
            genre.getItems().addAll(genreOptions(source.stream().map(ThongTinGame::genres).toList()));
            applySearch.run();
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được kho game", exception));
        }
        HBox filters = new HBox(10, search, genre, searchButton);
        HBox.setHgrow(search, Priority.ALWAYS);
        content.getChildren().add(filters);
        if (session.isVaiTroNhanVien(VaiTroNhanVien.KIEM_DUYET_VIEN)) {
            Button removeGame = UiUtils.dangerButton("Gỡ game vi phạm");
            removeGame.setOnAction(event -> {
                ThongTinGame selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    UiUtils.showInfo("Chọn game", "Vui lòng chọn game cần gỡ khỏi cửa hàng.");
                    return;
                }
                try {
                    quanLyDanhMucGameController.unpublishGame(selected.gameId());
                    UiUtils.showInfo("Đã gỡ game", selected.title() + " đã chuyển sang trạng thái Đã gỡ bỏ.");
                    source.clear();
                    source.addAll(quanLyThongTinGameController.findAllGames());
                    applySearch.run();
                } catch (SQLException exception) {
                    UiUtils.showError("Không gỡ được game", exception);
                }
            });
            content.getChildren().add(removeGame);
        }
        content.getChildren().add(table);
        setPage(content);
    }

    private void showCategories() {
        VBox content = page("Quản lý thể loại game");
        TableView<TheLoaiGame> table = new TableView<>();
        table.setPrefHeight(320);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, item -> String.valueOf(item.categoryId())),
                UiUtils.stringColumn("Tên thể loại", 180, TheLoaiGame::name),
                UiUtils.stringColumn("Mô tả", 360, item -> safe(item.description()))
        );
        TextField name = input("Tên thể loại");
        TextArea description = new TextArea();
        description.getStyleClass().add("input");
        description.setPromptText("Mô tả thể loại");
        description.setPrefRowCount(3);
        Button create = UiUtils.primaryButton("Thêm thể loại");
        Button update = UiUtils.secondaryButton("Cập nhật thể loại");
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null) {
                name.setText(selected.name());
                description.setText(selected.description());
            }
        });
        create.setOnAction(event -> {
            try {
                quanLyDanhMucGameController.createCategory(name.getText(), description.getText());
                table.getItems().setAll(quanLyDanhMucGameController.findCategories());
            } catch (SQLException exception) {
                UiUtils.showError("Không thêm được thể loại", exception);
            }
        });
        update.setOnAction(event -> {
            TheLoaiGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn thể loại", "Vui lòng chọn thể loại cần cập nhật.");
                return;
            }
            try {
                quanLyDanhMucGameController.updateCategory(selected.categoryId(), name.getText(), description.getText());
                table.getItems().setAll(quanLyDanhMucGameController.findCategories());
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được thể loại", exception);
            }
        });

        ComboBox<ThongTinGame> gameBox = gameCombo(false);
        ComboBox<TheLoaiGame> categoryBox = categoryCombo();
        Button assign = UiUtils.primaryButton("Gắn thể loại vào game");
        assign.setOnAction(event -> {
            ThongTinGame selectedGame = gameBox.getSelectionModel().getSelectedItem();
            TheLoaiGame selectedCategory = categoryBox.getSelectionModel().getSelectedItem();
            if (selectedGame == null || selectedCategory == null) {
                UiUtils.showInfo("Chọn dữ liệu", "Vui lòng chọn game và thể loại.");
                return;
            }
            try {
                quanLyDanhMucGameController.assignCategory(selectedGame.gameId(), selectedCategory.categoryId());
                UiUtils.showInfo("Đã gắn thể loại", "Game đã được cập nhật danh mục thể loại.");
            } catch (SQLException exception) {
                UiUtils.showError("Không gắn được thể loại", exception);
            }
        });
        try {
            table.getItems().setAll(quanLyDanhMucGameController.findCategories());
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được thể loại", exception));
        }
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Thể loại"),
                UiUtils.formRow("Tên", name), UiUtils.formRow("Mô tả", description), new HBox(10, create, update));
        VBox assignForm = UiUtils.card();
        assignForm.getChildren().addAll(UiUtils.sectionTitle("Phân loại game"),
                UiUtils.formRow("Game", gameBox), UiUtils.formRow("Thể loại", categoryBox), assign);
        content.getChildren().addAll(table, new HBox(14, form, assignForm));
        setPage(content);
    }

    private ComboBox<ThongTinGame> gameCombo(boolean releasedOnly) {
        ComboBox<ThongTinGame> comboBox = new ComboBox<>();
        comboBox.setPromptText("Chọn game");
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ThongTinGame game) {
                return game == null ? "" : game.title() + " (#" + game.gameId() + ")";
            }

            @Override
            public ThongTinGame fromString(String string) {
                return null;
            }
        });
        try {
            comboBox.getItems().setAll(releasedOnly ? quanLyThongTinGameController.findReleasedGames() : quanLyThongTinGameController.findAllGames());
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được danh sách game", exception);
        }
        return comboBox;
    }

    private ComboBox<TheLoaiGame> categoryCombo() {
        ComboBox<TheLoaiGame> comboBox = new ComboBox<>();
        comboBox.setPromptText("Chọn thể loại");
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TheLoaiGame category) {
                return category == null ? "" : category.name() + " (#" + category.categoryId() + ")";
            }

            @Override
            public TheLoaiGame fromString(String string) {
                return null;
            }
        });
        try {
            comboBox.getItems().setAll(quanLyDanhMucGameController.findCategories());
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được danh sách thể loại", exception);
        }
        return comboBox;
    }

    private void showPlayerManagement() {
        VBox content = page(session.isVaiTroNhanVien(VaiTroNhanVien.CSKH) ? "Danh sách người chơi" : "Quản lý người chơi");
        TableView<ThongTinNguoiDung> table = userProfileTable(false);
        TextField keyword = input("Tìm theo mã, username, tên hiển thị hoặc email");
        ComboBox<String> status = accountStatusCombo();
        Button search = UiUtils.secondaryButton("Tìm kiếm");
        Button updateStatus = UiUtils.primaryButton("Cập nhật trạng thái");
        search.setOnAction(event -> loadPlayers(table, keyword.getText()));
        updateStatus.setOnAction(event -> {
            ThongTinNguoiDung selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn người chơi", "Vui lòng chọn một người chơi.");
                return;
            }
            try {
                quanLyTaiKhoanController.capNhatTrangThaiTaiKhoan(selected.accountId(), status.getValue());
                loadPlayers(table, keyword.getText());
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được trạng thái", exception);
            }
        });
        HBox actions = new HBox(10, keyword, search, status, updateStatus);
        HBox.setHgrow(keyword, Priority.ALWAYS);
        loadPlayers(table, null);
        content.getChildren().addAll(actions, table);
        setPage(content);
    }

    private void loadPlayers(TableView<ThongTinNguoiDung> table, String keyword) {
        try {
            table.getItems().setAll(quanLyTaiKhoanController.traCuuNguoiChoi(keyword));
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được người chơi", exception);
        }
    }

    private void showDeveloperManagement() {
        VBox content = page("Quản lý nhà phát triển");
        boolean moderator = session.isVaiTroNhanVien(VaiTroNhanVien.KIEM_DUYET_VIEN);
        TableView<ThongTinNguoiDung> table = userProfileTable(!moderator);
        TextField keyword = input("Tìm theo mã, username, tên studio hoặc email");
        ComboBox<String> status = accountStatusCombo();
        TextField revenueShare = input("Tỷ lệ chia sẻ, ví dụ 0.70");
        Button search = UiUtils.secondaryButton("Tìm kiếm");
        Button updateStatus = UiUtils.primaryButton("Cập nhật trạng thái");
        Button updateShare = UiUtils.secondaryButton("Cập nhật tỷ lệ");
        Button viewGames = UiUtils.secondaryButton("Xem game của NPT");
        search.setOnAction(event -> loadDevelopers(table, keyword.getText()));
        viewGames.setOnAction(event -> {
            ThongTinNguoiDung selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn nhà phát triển", "Vui lòng chọn một nhà phát triển.");
                return;
            }
            showGameCuaNhaPhatTriensForEmployee(selected);
        });
        updateStatus.setOnAction(event -> {
            ThongTinNguoiDung selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn nhà phát triển", "Vui lòng chọn một nhà phát triển.");
                return;
            }
            try {
                quanLyTaiKhoanController.capNhatTrangThaiTaiKhoan(selected.accountId(), status.getValue());
                loadDevelopers(table, keyword.getText());
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được trạng thái", exception);
            }
        });
        updateShare.setOnAction(event -> {
            ThongTinNguoiDung selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn nhà phát triển", "Vui lòng chọn một nhà phát triển.");
                return;
            }
            try {
                quanLyTaiKhoanController.capNhatTyLeChiaSeNhaPhatTrien(selected.profileId(), moneyInput(revenueShare));
                loadDevelopers(table, keyword.getText());
            } catch (Exception exception) {
                UiUtils.showError("Không cập nhật được tỷ lệ chia sẻ", exception);
            }
        });
        HBox actions = moderator
                ? new HBox(10, keyword, search, viewGames)
                : new HBox(10, keyword, search, status, updateStatus, revenueShare, updateShare, viewGames);
        HBox.setHgrow(keyword, Priority.ALWAYS);
        loadDevelopers(table, null);
        content.getChildren().addAll(actions, table);
        setPage(content);
    }

    private void showGameCuaNhaPhatTriensForEmployee(ThongTinNguoiDung developer) {
        VBox content = page("Game của " + developer.displayName());
        Button back = UiUtils.secondaryButton("Quay lại nhà phát triển");
        back.setOnAction(event -> showDeveloperManagement());
        TableView<GameCuaNhaPhatTrien> table = developerGameTable();
        try {
            table.getItems().setAll(quanLyThongTinGameController.findGamesByDeveloper(developer.profileId()));
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được game của nhà phát triển", exception));
        }
        content.getChildren().addAll(back, table);
        setPage(content);
    }

    private void loadDevelopers(TableView<ThongTinNguoiDung> table, String keyword) {
        try {
            table.getItems().setAll(quanLyTaiKhoanController.traCuuNhaPhatTrien(keyword));
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được nhà phát triển", exception);
        }
    }

    private void showEmployeeManagement() {
        VBox content = page("Quản lý nhân viên");
        TableView<ThongTinNguoiDung> table = userProfileTable(false);
        TextField keyword = input("Tìm theo mã, username, họ tên hoặc email");
        ComboBox<String> status = accountStatusCombo();
        ComboBox<String> assignRole = employeeRoleCombo();
        Button search = UiUtils.secondaryButton("Tìm kiếm");
        Button updateStatus = UiUtils.primaryButton("Cập nhật trạng thái");
        Button updateRole = UiUtils.secondaryButton("Phân quyền");
        search.setOnAction(event -> loadEmployees(table, keyword.getText()));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null && selected.roleOrType() != null) {
                assignRole.getSelectionModel().select(selected.roleOrType());
            }
        });
        updateStatus.setOnAction(event -> {
            ThongTinNguoiDung selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn nhân viên", "Vui lòng chọn một nhân viên.");
                return;
            }
            try {
                quanLyNhanVienController.capNhatTrangThaiTaiKhoan(selected.accountId(), status.getValue());
                loadEmployees(table, keyword.getText());
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được trạng thái", exception);
            }
        });
        updateRole.setOnAction(event -> {
            ThongTinNguoiDung selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn nhân viên", "Vui lòng chọn một nhân viên để phân quyền.");
                return;
            }
            try {
                quanLyNhanVienController.updateVaiTroNhanVien(selected.profileId(), VaiTroNhanVien.fromDbValue(assignRole.getValue()));
                loadEmployees(table, keyword.getText());
            } catch (SQLException exception) {
                UiUtils.showError("Không phân quyền được nhân viên", exception);
            }
        });

        TextField username = input("Username");
        TextField password = input("Mật khẩu");
        TextField fullName = input("Họ tên");
        ComboBox<String> role = employeeRoleCombo();
        TextField email = input("Email");
        TextField phone = input("SĐT");
        Button create = UiUtils.primaryButton("Thêm nhân viên");
        create.setOnAction(event -> {
            try {
                int employeeId = quanLyNhanVienController.themNhanVien(
                        username.getText(),
                        password.getText(),
                        fullName.getText(),
                        VaiTroNhanVien.fromDbValue(role.getValue()),
                        email.getText(),
                        phone.getText()
                );
                UiUtils.showInfo("Đã thêm nhân viên", "Nhân viên #" + employeeId + " đã được tạo.");
                username.clear();
                password.clear();
                fullName.clear();
                email.clear();
                phone.clear();
                loadEmployees(table, keyword.getText());
            } catch (Exception exception) {
                UiUtils.showError("Không thêm được nhân viên", exception);
            }
        });

        HBox actions = new HBox(10, keyword, search, status, updateStatus, assignRole, updateRole);
        HBox.setHgrow(keyword, Priority.ALWAYS);
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Thêm nhân viên theo phân quyền thiết kế"),
                UiUtils.formRow("Username", username),
                UiUtils.formRow("Mật khẩu", password),
                UiUtils.formRow("Họ tên", fullName),
                UiUtils.formRow("Vai trò", role),
                UiUtils.formRow("Email", email),
                UiUtils.formRow("SĐT", phone),
                create);
        loadEmployees(table, null);
        content.getChildren().addAll(actions, table, form);
        setPage(content);
    }

    private void loadEmployees(TableView<ThongTinNguoiDung> table, String keyword) {
        try {
            table.getItems().setAll(quanLyNhanVienController.traCuuNhanVien(keyword));
        } catch (SQLException exception) {
            UiUtils.showError("Không tải được nhân viên", exception);
        }
    }

    private ComboBox<String> employeeRoleCombo() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Quản lý nền tảng", "Kiểm duyệt viên", "Marketing", "CSKH");
        comboBox.getSelectionModel().select("CSKH");
        return comboBox;
    }

    private TableView<ThongTinNguoiDung> userProfileTable(boolean includeRevenueShare) {
        TableView<ThongTinNguoiDung> table = new TableView<>();
        table.setPrefHeight(480);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã TK", 70, user -> String.valueOf(user.accountId())),
                UiUtils.stringColumn("Mã hồ sơ", 80, user -> String.valueOf(user.profileId())),
                UiUtils.stringColumn("Username", 130, ThongTinNguoiDung::username),
                UiUtils.stringColumn("Loại TK", 130, ThongTinNguoiDung::accountType),
                UiUtils.stringColumn("Trạng thái", 130, ThongTinNguoiDung::accountStatus),
                UiUtils.stringColumn("Tên hiển thị", 180, ThongTinNguoiDung::displayName),
                UiUtils.stringColumn("Vai trò/Loại", 140, user -> safe(user.roleOrType())),
                UiUtils.stringColumn("Email", 190, ThongTinNguoiDung::email),
                UiUtils.stringColumn("SĐT", 110, user -> safe(user.phone())),
                UiUtils.stringColumn("Khu vực/Địa chỉ", 150, user -> safe(user.location())),
                UiUtils.stringColumn("Ngày tạo", 110, user -> UiUtils.date(user.createdAt()))
        );
        if (includeRevenueShare) {
            table.getColumns().add(UiUtils.stringColumn("Tỷ lệ chia sẻ", 110,
                    user -> user.revenueShare() == null ? "" : user.revenueShare().toPlainString()));
        }
        return table;
    }

    private ComboBox<String> accountStatusCombo() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Đang hoạt động", "Ngưng hoạt động", "Bị khóa");
        comboBox.getSelectionModel().select("Đang hoạt động");
        return comboBox;
    }

    private void showPromotions() {
        VBox content = page("Quản lý chương trình khuyến mãi");
        TableView<ChuongTrinhKhuyenMai> table = new TableView<>();
        table.setPrefHeight(320);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, promotion -> String.valueOf(promotion.promotionId())),
                UiUtils.stringColumn("Tên chương trình", 220, ChuongTrinhKhuyenMai::name),
                UiUtils.stringColumn("Bắt đầu", 110, promotion -> UiUtils.date(promotion.startDate())),
                UiUtils.stringColumn("Kết thúc", 110, promotion -> UiUtils.date(promotion.endDate())),
                UiUtils.stringColumn("Trạng thái", 120, ChuongTrinhKhuyenMai::status),
                UiUtils.stringColumn("Số game", 80, promotion -> String.valueOf(promotion.gameCount())),
                UiUtils.stringColumn("Nội dung", 260, promotion -> safe(promotion.content()))
        );

        TextField name = input("Tên chương trình");
        DatePicker start = new DatePicker(LocalDate.now());
        DatePicker end = new DatePicker(LocalDate.now().plusDays(14));
        TextArea contentText = new TextArea();
        contentText.getStyleClass().add("input");
        contentText.setPromptText("Nội dung chương trình");
        contentText.setPrefRowCount(3);
        ComboBox<String> promotionStatus = new ComboBox<>();
        promotionStatus.getItems().addAll("Đang hiệu lực", "Hết hiệu lực");
        promotionStatus.getSelectionModel().selectFirst();
        Button create = UiUtils.primaryButton("Tạo khuyến mãi");
        Button capNhatChuongTrinh = UiUtils.secondaryButton("Cập nhật khuyến mãi");
        create.setOnAction(event -> {
            try {
                int id = quanLyKhuyenMaiController.taoChuongTrinh(name.getText(), start.getValue(), end.getValue(), contentText.getText());
                UiUtils.showInfo("Đã tạo khuyến mãi", "Chương trình #" + id + " đã được tạo.");
                table.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
            } catch (SQLException exception) {
                UiUtils.showError("Không tạo được khuyến mãi", exception);
            }
        });
        capNhatChuongTrinh.setOnAction(event -> {
            ChuongTrinhKhuyenMai selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn khuyến mãi", "Vui lòng chọn chương trình cần cập nhật.");
                return;
            }
            try {
                quanLyKhuyenMaiController.capNhatChuongTrinh(selected.promotionId(), name.getText(), start.getValue(), end.getValue(),
                        promotionStatus.getValue(), contentText.getText());
                table.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được khuyến mãi", exception);
            }
        });

        ComboBox<ChuongTrinhKhuyenMai> promotionBox = new ComboBox<>();
        promotionBox.setPromptText("Chọn chương trình khuyến mãi");
        promotionBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ChuongTrinhKhuyenMai promotion) {
                return promotion == null ? "" : promotion.name() + " (#" + promotion.promotionId() + ")";
            }

            @Override
            public ChuongTrinhKhuyenMai fromString(String string) {
                return null;
            }
        });
        ComboBox<ThongTinGame> gameBox = new ComboBox<>();
        gameBox.setPromptText("Chọn game");
        gameBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ThongTinGame game) {
                return game == null ? "" : game.title() + " (#" + game.gameId() + ")";
            }

            @Override
            public ThongTinGame fromString(String string) {
                return null;
            }
        });
        try {
            promotionBox.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
            gameBox.getItems().setAll(quanLyThongTinGameController.findReleasedGames());
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được danh sách chọn khuyến mãi/game", exception));
        }
        TextField discount = input("Phần trăm giảm");
        TableView<GameTrongKhuyenMai> detailTable = new TableView<>();
        detailTable.setPrefHeight(220);
        detailTable.getColumns().addAll(
                UiUtils.stringColumn("Mã KM", 70, item -> String.valueOf(item.promotionId())),
                UiUtils.stringColumn("Game", 200, GameTrongKhuyenMai::gameTitle),
                UiUtils.stringColumn("% giảm", 90, item -> item.discountPercent().toPlainString())
        );
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null) {
                name.setText(selected.name());
                start.setValue(selected.startDate());
                end.setValue(selected.endDate());
                promotionStatus.getSelectionModel().select(selected.status());
                contentText.setText(selected.content());
                promotionBox.getSelectionModel().select(selected);
                try {
                    detailTable.getItems().setAll(quanLyKhuyenMaiController.traCuuGameTrongKhuyenMai(selected.promotionId()));
                } catch (SQLException exception) {
                    UiUtils.showError("Không tải được game trong khuyến mãi", exception);
                }
            }
        });
        Button attach = UiUtils.secondaryButton("Thêm game");
        Button updateDiscount = UiUtils.secondaryButton("Cập nhật %");
        attach.setMinWidth(120);
        updateDiscount.setMinWidth(120);
        attach.setOnAction(event -> {
            try {
                ChuongTrinhKhuyenMai selectedPromotion = promotionBox.getSelectionModel().getSelectedItem();
                ThongTinGame selectedGame = gameBox.getSelectionModel().getSelectedItem();
                if (selectedPromotion == null || selectedGame == null) {
                    UiUtils.showInfo("Chọn dữ liệu", "Vui lòng chọn chương trình khuyến mãi và game.");
                    return;
                }
                quanLyKhuyenMaiController.ganGameVaoKhuyenMai(
                        selectedPromotion.promotionId(),
                        selectedGame.gameId(),
                        moneyInput(discount)
                );
                table.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
                promotionBox.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
            } catch (Exception exception) {
                UiUtils.showError("Không thêm được game", exception);
            }
        });
        updateDiscount.setOnAction(event -> {
            GameTrongKhuyenMai selected = detailTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn game", "Vui lòng chọn game trong chi tiết khuyến mãi.");
                return;
            }
            try {
                quanLyKhuyenMaiController.capNhatChuongTrinhGame(selected.promotionId(), selected.gameId(), moneyInput(discount));
                detailTable.getItems().setAll(quanLyKhuyenMaiController.traCuuGameTrongKhuyenMai(selected.promotionId()));
                table.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
            } catch (Exception exception) {
                UiUtils.showError("Không cập nhật được mức khuyến mãi", exception);
            }
        });

        try {
            table.getItems().setAll(quanLyKhuyenMaiController.traCuuChuongTrinh());
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được khuyến mãi", exception));
        }

        VBox createForm = UiUtils.card();
        createForm.getChildren().addAll(UiUtils.sectionTitle("Tạo chương trình"), UiUtils.formRow("Tên", name),
                UiUtils.formRow("Ngày bắt đầu", start), UiUtils.formRow("Ngày kết thúc", end),
                UiUtils.formRow("Trạng thái", promotionStatus), contentText, new HBox(10, create, capNhatChuongTrinh));
        VBox attachForm = UiUtils.card();
        attachForm.getChildren().addAll(UiUtils.sectionTitle("Gắn game vào khuyến mãi"), UiUtils.formRow("Chương trình", promotionBox),
                UiUtils.formRow("Game", gameBox), UiUtils.formRow("% giảm", discount), new HBox(10, attach, updateDiscount), detailTable);
        HBox forms = new HBox(14, createForm, attachForm);
        HBox.setHgrow(createForm, Priority.ALWAYS);
        HBox.setHgrow(attachForm, Priority.ALWAYS);
        content.getChildren().addAll(table, forms);
        setPage(content);
    }

    private void showDiscountCodes() {
        VBox content = page("Quản lý mã giảm giá");
        TableView<ThongTinMaGiamGia> table = new TableView<>();
        List<ThongTinMaGiamGia> source = new ArrayList<>();
        TextField searchText = input("Tìm theo mã giảm giá, trạng thái hoặc mô tả");
        Button search = UiUtils.secondaryButton("Tìm kiếm");
        Runnable applySearch = () -> table.getItems().setAll(source.stream()
                .filter(codeItem -> matchesDiscountCode(codeItem, searchText.getText()))
                .toList());
        search.textProperty().addListener((observable, oldValue, newValue) -> applySearch.run());
        search.setOnAction(event -> applySearch.run());
        table.setPrefHeight(360);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã", 60, item -> String.valueOf(item.discountCodeId())),
                UiUtils.stringColumn("Code", 120, ThongTinMaGiamGia::code),
                UiUtils.stringColumn("Số tiền giảm", 120, item -> UiUtils.money(item.discountAmount())),
                UiUtils.stringColumn("Lượt dùng", 100, item -> item.usedCount() + "/" + item.usageLimit()),
                UiUtils.stringColumn("Bắt đầu", 110, item -> UiUtils.date(item.startDate())),
                UiUtils.stringColumn("Hết hạn", 110, item -> UiUtils.date(item.endDate())),
                UiUtils.stringColumn("Tối thiểu", 120, item -> UiUtils.money(item.minimumTotal())),
                UiUtils.stringColumn("Trạng thái", 130, ThongTinMaGiamGia::status),
                UiUtils.stringColumn("Mô tả", 220, item -> safe(item.description()))
        );

        TextField code = input("Code, ví dụ WELCOME50");
        TextField amount = input("Số tiền giảm");
        TextField limit = input("Giới hạn lượt dùng");
        DatePicker start = new DatePicker(LocalDate.now());
        DatePicker end = new DatePicker(LocalDate.now().plusMonths(1));
        TextField minimum = input("Giá trị giao dịch tối thiểu");
        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Đang hiệu lực", "Hết hiệu lực");
        status.getSelectionModel().selectFirst();
        TextArea description = new TextArea();
        description.getStyleClass().add("input");
        description.setPromptText("Mô tả mã giảm giá");
        description.setPrefRowCount(3);
        Button create = UiUtils.primaryButton("Tạo mã");
        Button update = UiUtils.secondaryButton("Cập nhật mã");
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null) {
                code.setText(selected.code());
                amount.setText(selected.discountAmount().toPlainString());
                limit.setText(String.valueOf(selected.usageLimit()));
                start.setValue(selected.startDate());
                end.setValue(selected.endDate());
                minimum.setText(selected.minimumTotal().toPlainString());
                status.getSelectionModel().select(selected.status());
                description.setText(selected.description());
            }
        });
        create.setOnAction(event -> {
            try {
                int id = quanLyMaGiamGiaController.taoMaGiamGia(code.getText(), moneyInput(amount), Integer.parseInt(limit.getText()),
                        start.getValue(), end.getValue(), moneyInput(minimum), description.getText());
                UiUtils.showInfo("Đã tạo mã giảm giá", "Mã giảm giá #" + id + " đã được tạo.");
                source.clear();
                source.addAll(quanLyMaGiamGiaController.traCuuMaGiamGia());
                applySearch.run();
            } catch (Exception exception) {
                UiUtils.showError("Không tạo được mã giảm giá", exception);
            }
        });
        update.setOnAction(event -> {
            ThongTinMaGiamGia selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn mã", "Vui lòng chọn mã giảm giá cần cập nhật.");
                return;
            }
            try {
                quanLyMaGiamGiaController.capNhatMaGiamGia(selected.discountCodeId(), moneyInput(amount), Integer.parseInt(limit.getText()),
                        start.getValue(), end.getValue(), moneyInput(minimum), status.getValue(), description.getText());
                source.clear();
                source.addAll(quanLyMaGiamGiaController.traCuuMaGiamGia());
                applySearch.run();
            } catch (Exception exception) {
                UiUtils.showError("Không cập nhật được mã giảm giá", exception);
            }
        });
        try {
            source.addAll(quanLyMaGiamGiaController.traCuuMaGiamGia());
            applySearch.run();
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được mã giảm giá", exception));
        }
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Mã giảm giá"),
                UiUtils.formRow("Code", code), UiUtils.formRow("Số tiền giảm", amount),
                UiUtils.formRow("Giới hạn lượt dùng", limit), UiUtils.formRow("Bắt đầu", start),
                UiUtils.formRow("Hết hạn", end), UiUtils.formRow("Giá trị giao dịch tối thiểu", minimum),
                UiUtils.formRow("Trạng thái", status), UiUtils.formRow("Mô tả", description),
                new HBox(10, create, update));
        HBox searchBar = new HBox(10, searchText, search);
        HBox.setHgrow(searchText, Priority.ALWAYS);
        content.getChildren().addAll(searchBar, table, form);
        setPage(content);
    }

    private boolean matchesDiscountCode(ThongTinMaGiamGia code, String keyword) {
        return containsText(code.code(), keyword)
                || containsText(code.status(), keyword)
                || containsText(code.description(), keyword);
    }

    private void showPlatformRevenue() {
        showRevenueReport("Quản lý doanh thu nền tảng", null);
    }

    private void showDeveloperRevenue() {
        showRevenueReport("Quản lý doanh thu", session.profileId());
    }

    private void showRevenueReport(String title, Integer developerId) {
        VBox content = page(title);
        TableView<DongBaoCaoDoanhThu> table = revenueTable();
        DatePicker from = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker to = new DatePicker(LocalDate.now());
        Label total = new Label();
        total.getStyleClass().add("stat-value");
        Button search = UiUtils.primaryButton("Tra cứu doanh thu");
        Button export = UiUtils.secondaryButton("Xuất CSV");
        Runnable reload = () -> {
            try {
                List<DongBaoCaoDoanhThu> rows = developerId == null
                        ? quanLyDoanhThuController.doanhThuNenTang(from.getValue(), to.getValue())
                        : quanLyDoanhThuController.doanhThuNhaPhatTrien(developerId, from.getValue(), to.getValue());
                table.getItems().setAll(rows);
                BigDecimal sum = rows.stream()
                        .map(developerId == null ? DongBaoCaoDoanhThu::doanhThuNenTang : DongBaoCaoDoanhThu::doanhThuNhaPhatTrien)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int downloads = rows.stream().mapToInt(DongBaoCaoDoanhThu::soLuongBan).sum();
                total.setText("Tổng doanh thu kỳ này: " + UiUtils.money(sum) + "  |  Số lượng tải/bán: " + downloads);
            } catch (SQLException exception) {
                UiUtils.showError("Không tra cứu được doanh thu", exception);
            }
        };
        search.setOnAction(event -> reload.run());
        export.setOnAction(event -> {
            try {
                Path file = quanLyDoanhThuController.xuatCsv(developerId == null ? "platformRevenue" : "developerRevenue",
                        List.copyOf(table.getItems()));
                UiUtils.showInfo("Đã xuất báo cáo", "File CSV: " + file);
            } catch (Exception exception) {
                UiUtils.showError("Không xuất được báo cáo", exception);
            }
        });
        reload.run();
        HBox filters = new HBox(10, UiUtils.formRow("Từ ngày", from), UiUtils.formRow("Đến ngày", to), search, export);
        filters.setAlignment(Pos.BOTTOM_LEFT);
        VBox chartCard = UiUtils.card();
        chartCard.getChildren().addAll(UiUtils.sectionTitle("Biểu đồ doanh thu theo game"), revenueReportChart(table.getItems(), developerId));
        search.setOnAction(event -> {
            reload.run();
            chartCard.getChildren().setAll(UiUtils.sectionTitle("Biểu đồ doanh thu theo game"), revenueReportChart(table.getItems(), developerId));
        });
        content.getChildren().addAll(filters, total, chartCard, table);
        setPage(content);
    }

    private BarChart<String, Number> revenueReportChart(List<DongBaoCaoDoanhThu> rows, Integer developerId) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setMinHeight(280);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (DongBaoCaoDoanhThu row : rows) {
            BigDecimal value = developerId == null ? row.doanhThuNenTang() : row.doanhThuNhaPhatTrien();
            series.getData().add(new XYChart.Data<>(row.tenGame(), value));
        }
        chart.getData().add(series);
        return chart;
    }

    private TableView<DongBaoCaoDoanhThu> revenueTable() {
        TableView<DongBaoCaoDoanhThu> table = new TableView<>();
        table.setPrefHeight(520);
        table.getColumns().addAll(
                UiUtils.stringColumn("Mã game", 80, item -> String.valueOf(item.gameId())),
                UiUtils.stringColumn("Game", 190, DongBaoCaoDoanhThu::tenGame),
                UiUtils.stringColumn("Nhà phát triển", 170, DongBaoCaoDoanhThu::tenNhaPhatTrien),
                UiUtils.stringColumn("Đã bán", 80, item -> String.valueOf(item.soLuongBan())),
                UiUtils.stringColumn("Doanh thu gốc", 130, item -> UiUtils.money(item.doanhThuGoc())),
                UiUtils.stringColumn("Phần NPT", 130, item -> UiUtils.money(item.doanhThuNhaPhatTrien())),
                UiUtils.stringColumn("Phần nền tảng", 130, item -> UiUtils.money(item.doanhThuNenTang()))
        );
        return table;
    }

    private void showProfile() {
        if (session.accountType() == LoaiTaiKhoan.NHAN_VIEN) {
            showChangePassword();
            return;
        }
        VBox content = page("Hồ sơ tài khoản");
        TextField displayName = input("Tên hiển thị");
        TextField email = input("Email");
        TextField phone = input("SĐT");
        TextField extra = input("Thông tin bổ sung");
        Label extraLabel = new Label("Thông tin bổ sung");
        extraLabel.getStyleClass().add("stat-label");
        PasswordField currentPassword = new PasswordField();
        currentPassword.getStyleClass().add("input");
        currentPassword.setPromptText("Mật khẩu hiện tại");
        PasswordField newPassword = new PasswordField();
        newPassword.getStyleClass().add("input");
        newPassword.setPromptText("Mật khẩu mới");
        Button saveProfile = UiUtils.primaryButton("Cập nhật hồ sơ");
        Button changePassword = UiUtils.secondaryButton("Đổi mật khẩu");
        try {
            ThongTinHoSo profile = quanLyHoSoController.loadProfile(session.accountType(), session.profileId());
            displayName.setText(profile.displayName());
            email.setText(profile.email());
            phone.setText(profile.phone());
            extraLabel.setText(profile.extraLabel());
            extra.setText(profile.extraValue());
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được hồ sơ", exception));
        }
        saveProfile.setOnAction(event -> {
            try {
                switch (session.accountType()) {
                    case NGUOI_CHOI -> quanLyHoSoController.updatePlayerProfile(session.profileId(), displayName.getText(),
                            email.getText(), phone.getText(), extra.getText());
                    case NHA_PHAT_TRIEN -> quanLyHoSoController.updateDeveloperProfile(session.profileId(), displayName.getText(),
                            email.getText(), phone.getText(), extra.getText());
                    case NHAN_VIEN -> quanLyHoSoController.updateEmployeeProfile(session.profileId(), displayName.getText(),
                            email.getText(), phone.getText());
                }
                UiUtils.showInfo("Đã cập nhật hồ sơ", "Thông tin hồ sơ đã được lưu.");
            } catch (SQLException exception) {
                UiUtils.showError("Không cập nhật được hồ sơ", exception);
            }
        });
        changePassword.setOnAction(event -> {
            try {
                quanLyHoSoController.changePassword(session.accountId(), currentPassword.getText(), newPassword.getText());
                currentPassword.clear();
                newPassword.clear();
                UiUtils.showInfo("Đã đổi mật khẩu", "Mật khẩu mới có hiệu lực từ lần đăng nhập sau.");
            } catch (SQLException exception) {
                UiUtils.showError("Không đổi được mật khẩu", exception);
            }
        });
        VBox profileForm = UiUtils.card();
        profileForm.getChildren().addAll(UiUtils.sectionTitle("Thông tin hồ sơ"),
                UiUtils.formRow("Tên hiển thị", displayName),
                UiUtils.formRow("Email", email),
                UiUtils.formRow("SĐT", phone),
                new VBox(5, extraLabel, extra),
                saveProfile);
        VBox passwordForm = UiUtils.card();
        passwordForm.getChildren().addAll(UiUtils.sectionTitle("Đổi mật khẩu"),
                UiUtils.formRow("Mật khẩu hiện tại", currentPassword),
                UiUtils.formRow("Mật khẩu mới", newPassword),
                changePassword);
        content.getChildren().addAll(profileForm, passwordForm);
        setPage(content);
    }

    private void showChangePassword() {
        VBox content = page("Đổi mật khẩu");
        PasswordField currentPassword = new PasswordField();
        currentPassword.getStyleClass().add("input");
        currentPassword.setPromptText("Mật khẩu hiện tại");
        PasswordField newPassword = new PasswordField();
        newPassword.getStyleClass().add("input");
        newPassword.setPromptText("Mật khẩu mới");
        Button changePassword = UiUtils.primaryButton("Đổi mật khẩu");
        changePassword.setOnAction(event -> {
            try {
                quanLyHoSoController.changePassword(session.accountId(), currentPassword.getText(), newPassword.getText());
                currentPassword.clear();
                newPassword.clear();
                UiUtils.showInfo("Đã đổi mật khẩu", "Mật khẩu mới có hiệu lực từ lần đăng nhập sau.");
            } catch (SQLException exception) {
                UiUtils.showError("Không đổi được mật khẩu", exception);
            }
        });
        VBox passwordForm = UiUtils.card();
        passwordForm.getChildren().addAll(UiUtils.sectionTitle("Đổi mật khẩu"),
                UiUtils.formRow("Mật khẩu hiện tại", currentPassword),
                UiUtils.formRow("Mật khẩu mới", newPassword),
                changePassword);
        content.getChildren().add(passwordForm);
        setPage(content);
    }

    private void showSupportTickets() {
        VBox content = page("Quản lý ticket");
        TableView<ThongTinTicket> table = ticketTable();
        TextArea requestContent = new TextArea();
        requestContent.getStyleClass().add("input");
        requestContent.setPromptText("Chọn ticket để xem nội dung yêu cầu");
        requestContent.setPrefRowCount(5);
        requestContent.setEditable(false);
        requestContent.setWrapText(true);
        TextArea response = new TextArea();
        response.getStyleClass().add("input");
        response.setPromptText("Nội dung phản hồi");
        response.setPrefRowCount(3);
        response.setWrapText(true);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldTicket, selectedTicket) -> {
            requestContent.setText(selectedTicket == null ? "" : safe(selectedTicket.content()));
            response.setText(selectedTicket == null ? "" : safe(selectedTicket.response()));
        });
        Button take = UiUtils.secondaryButton("Nhận xử lý");
        Button phanHoiTicket = UiUtils.primaryButton("Phản hồi và đóng ticket");
        boolean canHandle = session.isVaiTroNhanVien(VaiTroNhanVien.CSKH);
        take.setDisable(!canHandle);
        phanHoiTicket.setDisable(!canHandle);
        take.setOnAction(event -> {
            ThongTinTicket selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn ticket", "Vui lòng chọn ticket.");
                return;
            }
            try {
                quanLyTicketHoTroController.nhanXuLyTicket(selected.ticketId(), session.profileId());
                table.getItems().setAll(quanLyTicketHoTroController.traCuuTatCaTicket());
            } catch (SQLException exception) {
                UiUtils.showError("Không nhận được ticket", exception);
            }
        });
        phanHoiTicket.setOnAction(event -> {
            ThongTinTicket selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UiUtils.showInfo("Chọn ticket", "Vui lòng chọn ticket.");
                return;
            }
            try {
                if (response.getText().isBlank()) {
                    UiUtils.showInfo("Thiếu nội dung phản hồi", "Vui lòng nhập nội dung phản hồi trước khi đóng ticket.");
                    return;
                }
                quanLyTicketHoTroController.phanHoiTicket(selected.ticketId(), session.profileId(), response.getText());
                response.clear();
                table.getItems().setAll(quanLyTicketHoTroController.traCuuTatCaTicket());
            } catch (SQLException exception) {
                UiUtils.showError("Không phản hồi được ticket", exception);
            }
        });
        try {
            table.getItems().setAll(quanLyTicketHoTroController.traCuuTatCaTicket());
        } catch (SQLException exception) {
            content.getChildren().add(errorCard("Không tải được ticket", exception));
        }
        VBox form = UiUtils.card();
        form.getChildren().addAll(UiUtils.sectionTitle("Nghiệp vụ CSKH"),
                UiUtils.formRow("Nội dung ticket", requestContent),
                UiUtils.formRow("Nội dung phản hồi", response),
                new HBox(10, take, phanHoiTicket));
        content.getChildren().addAll(table, form);
        setPage(content);
    }

    private TextField input(String prompt) {
        TextField textField = new TextField();
        textField.getStyleClass().add("input");
        textField.setPromptText(prompt);
        return textField;
    }

    private BigDecimal moneyInput(TextField textField) {
        String value = textField.getText() == null ? "" : textField.getText().trim().replace(",", "");
        if (value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }

    private Integer nullableInt(TextField textField) {
        String value = textField.getText() == null ? "" : textField.getText().trim();
        return value.isBlank() ? null : Integer.parseInt(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
































