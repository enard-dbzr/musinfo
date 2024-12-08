package com.plux;

import com.plux.infrastructure.adapter.postgres.*;
import com.plux.presentation.Controller;


public class Main {
    public static void main(String[] args) {
        var dbConnectionFactory = new DbConnectionFactory("localhost", 5432, "db_susu");

        var userRepository = new UserRepository(dbConnectionFactory);
        var bandRepository = new BandRepository(dbConnectionFactory);
        var bandMembersRepository = new BandMembersRepository(dbConnectionFactory);
        var albumRepository = new AlbumRepository(dbConnectionFactory);
        var labelRepository = new LabelRepository(dbConnectionFactory);
        var labelContractsRepository = new LabelContractsRepository(dbConnectionFactory);
        var tracksRepository = new TracksRepository(dbConnectionFactory);
        var trackAuthorsRepository = new TrackAuthorsRepository(dbConnectionFactory);
        var memberRepository = new MemberRepository(dbConnectionFactory);

        var formsController = new Controller(dbConnectionFactory, userRepository, bandRepository, bandRepository,
                bandMembersRepository, albumRepository, labelContractsRepository, labelRepository, albumRepository,
                tracksRepository, memberRepository, tracksRepository, trackAuthorsRepository, bandRepository,
                memberRepository, bandMembersRepository, bandMembersRepository, labelRepository,
                labelContractsRepository, labelContractsRepository, memberRepository, memberRepository,
                labelRepository, albumRepository);

        formsController.start();
    }
}