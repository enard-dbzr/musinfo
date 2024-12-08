package com.plux.presentation;

import com.plux.domain.model.Band;
import com.plux.domain.model.Label;
import com.plux.domain.model.Member;
import com.plux.domain.model.User;
import com.plux.port.api.*;
import com.plux.port.api.album.GetAlbumByIdPort;
import com.plux.port.api.album.GetAlbumTracksPort;
import com.plux.port.api.band.*;
import com.plux.port.api.label.GetLabelByIdPort;
import com.plux.port.api.label.SaveLabelPort;
import com.plux.port.api.member.DeleteMemberPort;
import com.plux.port.api.member.GetMemberByIdPort;
import com.plux.port.api.member.SaveMemberPort;
import com.plux.port.api.track.GetTrackAuthorsPort;
import com.plux.port.api.track.GetTrackByIdPort;

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
    private final GetMemberByIdPort getMemberByIdPort;
    private final GetTrackByIdPort getTrackByIdPort;
    private final GetTrackAuthorsPort getTrackAuthorsPort;
    private final SaveBandPort saveBandPort;
    private final GetAllMembersPort getAllMembersPort;
    private final SaveBandMemberPort saveBandMemberPort;
    private final RemoveBandMemberPort removeBandMemberPort;
    private final GetAllLabelsPort getAllLabelsPort;
    private final SaveLabelContractPort saveLabelContractPort;
    private final DeleteLabelContractPort deleteLabelContractPort;
    private final SaveMemberPort saveMemberPort;
    private final DeleteMemberPort deleteMemberPort;
    private final SaveLabelPort saveLabelPort;

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
                      GetAlbumTracksPort getAlbumTracksPort,
                      GetMemberByIdPort getMemberByIdPort,
                      GetTrackByIdPort getTrackByIdPort,
                      GetTrackAuthorsPort getTrackAuthorsPort,
                      SaveBandPort saveBandPort,
                      GetAllMembersPort getAllMembersPort,
                      SaveBandMemberPort saveBandMemberPort,
                      RemoveBandMemberPort removeBandMemberPort,
                      GetAllLabelsPort getAllLabelsPort,
                      SaveLabelContractPort saveLabelContractPort,
                      DeleteLabelContractPort deleteLabelContractPort,
                      SaveMemberPort saveMemberPort,
                      DeleteMemberPort deleteMemberPort,
                      SaveLabelPort saveLabelPort) {

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
        this.getMemberByIdPort = getMemberByIdPort;
        this.getTrackByIdPort = getTrackByIdPort;
        this.getTrackAuthorsPort = getTrackAuthorsPort;
        this.saveBandPort = saveBandPort;
        this.getAllMembersPort = getAllMembersPort;
        this.saveBandMemberPort = saveBandMemberPort;
        this.removeBandMemberPort = removeBandMemberPort;
        this.getAllLabelsPort = getAllLabelsPort;
        this.saveLabelContractPort = saveLabelContractPort;
        this.deleteLabelContractPort = deleteLabelContractPort;
        this.saveMemberPort = saveMemberPort;
        this.deleteMemberPort = deleteMemberPort;
        this.saveLabelPort = saveLabelPort;

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

    BandOverviewForm viewBand(Band band) {
        var bandOverviewForm = new BandOverviewForm(this, band, getBandByIdPort, getBandMembersPort,
                getBandAlbumsPort, getBandContractsPort, saveBandPort, getAllMembersPort, saveBandMemberPort,
                removeBandMemberPort, getAllLabelsPort, saveLabelContractPort, deleteLabelContractPort);

        bandOverviewForm.setVisible(true);
        return bandOverviewForm;
    }

    void viewAlbum(Integer albumId) {
        var albumOverviewForm = new AlbumOverviewForm(this, albumId, getAlbumByIdPort, getAlbumTracksPort);

        albumOverviewForm.setVisible(true);
    }

    LabelOverviewForm viewLabel(Label label) {
        var labelOverviewForm = new LabelOverviewForm(this, label, getLabelByIdPort, saveLabelPort);

        labelOverviewForm.setVisible(true);
        return labelOverviewForm;
    }

    MemberOverviewForm viewMember(Member member) {
        var memberOverviewForm = new MemberOverviewForm(this, member, getMemberByIdPort, saveMemberPort, deleteMemberPort);

        memberOverviewForm.setVisible(true);
        return memberOverviewForm;
    }

    void viewTrack(Integer trackId) {
        var trackOverviewForm = new TrackOverviewForm(this, trackId, getTrackByIdPort, getTrackAuthorsPort);

        trackOverviewForm.setVisible(true);
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
