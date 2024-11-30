package com.plux.presentation;

import com.plux.domain.model.User;
import com.plux.port.api.*;
import com.plux.port.api.album.GetAlbumByIdPort;
import com.plux.port.api.album.GetAlbumTracksPort;
import com.plux.port.api.band.*;
import com.plux.port.api.label.GetLabelByIdPort;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

public class Controller {
    private LoginForm loginForm;
    private MainForm mainForm;

    private final GetUserByIdPort getUserByIdPort;
    private final SearchBandsPort searchBandsPort;
    private final GetBandByIdPort getBandByIdPort;
    private final GetBandMembersPort getBandMembersPort;
    private final GetBandAlbumsPort getBandAlbumsPort;
    private final GetBandContractsPort getBandContractsPort;
    private final GetLabelByIdPort getLabelByIdPort;
    private final GetAlbumByIdPort getAlbumByIdPort;
    private final GetAlbumTracksPort getAlbumTracksPort;

    UUID userId;
    User user;

    private final AuthPort authPort;

    public Controller(AuthPort authPort,
                      GetUserByIdPort getUserByIdPort,
                      SearchBandsPort searchBandsPort,
                      GetBandByIdPort getBandByIdPort,
                      GetBandMembersPort getBandMembersPort,
                      GetBandAlbumsPort getBandAlbumsPort,
                      GetBandContractsPort getBandContractsPort,
                      GetLabelByIdPort getLabelByIdPort,
                      GetAlbumByIdPort getAlbumByIdPort,
                      GetAlbumTracksPort getAlbumTracksPort) {

        this.authPort = authPort;
        this.getUserByIdPort = getUserByIdPort;
        this.searchBandsPort = searchBandsPort;
        this.getBandByIdPort = getBandByIdPort;
        this.getBandMembersPort = getBandMembersPort;
        this.getBandAlbumsPort = getBandAlbumsPort;
        this.getBandContractsPort = getBandContractsPort;
        this.getLabelByIdPort = getLabelByIdPort;
        this.getAlbumByIdPort = getAlbumByIdPort;
        this.getAlbumTracksPort = getAlbumTracksPort;

        loadFonts();

        this.loginForm = new LoginForm(this, authPort);
    }

    public void start() {
        loginForm.setVisible(true);

        var id = authPort.authenticate("pr_m", "1234");
        authSuccess(id);
    }

    void authSuccess(UUID userId) {
        this.userId = userId;
        this.user = getUserByIdPort.getUserById(userId);

        loginForm.setVisible(false);

        mainForm = new MainForm(this, searchBandsPort);
        mainForm.setVisible(true);
    }

    void viewBand(Integer bandId) {
        var bandOverviewForm = new BandOverviewForm(this, bandId, getBandByIdPort, getBandMembersPort,
                getBandAlbumsPort, getBandContractsPort);

        bandOverviewForm.setVisible(true);
    }

    void viewAlbum(Integer albumId) {
        var albumOverviewForm = new AlbumOverviewForm(this, albumId, getAlbumByIdPort, getAlbumTracksPort);

        albumOverviewForm.setVisible(true);
    }

    void viewLabel(Integer labelId) {
        var labelOverviewForm = new LabelOverviewForm(this, labelId, getLabelByIdPort);

        labelOverviewForm.setVisible(true);
    }

    private void loadFonts() {
        try {
            var font1 = Font.createFont(Font.TRUETYPE_FONT,
                    Controller.class.getResourceAsStream("/fs6-regular.otf"));

            var font2 = Font.createFont(Font.TRUETYPE_FONT,
                    Controller.class.getResourceAsStream("/fs6-solid.otf"));

            var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            ge.registerFont(font1);
            ge.registerFont(font2);

        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
