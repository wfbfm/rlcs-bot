package com.wfbfm.rlcsbot.liquipedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LiquipediaRefDataFetcherTest
{
    private static final String LIQUIPEDIA_URL = "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_1";
    private final LiquipediaRefDataFetcher liquipediaRefDataFetcher = new LiquipediaRefDataFetcher();
    private Map<String, Map<String, String>> teamToPlayerAndCoachMap;
    private Map<String, Set<String>> teamToPlayerNameMap;
    private Map<String, String> playerToTeamNameMap;
    private Map<String, String> uppercasePlayerNameMap;
    private Map<String, String> uppercaseTeamNameMap;

    @BeforeEach
    public void setUp()
    {
        liquipediaRefDataFetcher.setLiquipediaUrl(LIQUIPEDIA_URL);
        assertTrue(liquipediaRefDataFetcher.updateLiquipediaRefData());
        teamToPlayerAndCoachMap = liquipediaRefDataFetcher.getTeamToPlayerAndCoachMap();
        teamToPlayerNameMap = liquipediaRefDataFetcher.getTeamToPlayerNameMap();
        playerToTeamNameMap = liquipediaRefDataFetcher.getPlayerToTeamNameMap();
        uppercasePlayerNameMap = liquipediaRefDataFetcher.getUppercasePlayerNameMap();
        uppercaseTeamNameMap = liquipediaRefDataFetcher.getUppercaseTeamNameMap();
    }

    @Test
    public void testGetTeamVitalityDataFromLiquipedia()
    {
        final Map<String, String> vitalityRoster = teamToPlayerAndCoachMap.get("Team Vitality");
        assertEquals(4, vitalityRoster.size());
        assertEquals("Alpha54", vitalityRoster.get("1"));
        assertEquals("Radosin", vitalityRoster.get("2"));
        assertEquals("zen", vitalityRoster.get("3"));
        assertEquals("Fairy Peak!", vitalityRoster.get("C"));

        final Set<String> playerSet = teamToPlayerNameMap.get("Team Vitality");
        assertTrue(playerSet.contains("Alpha54"));
        assertTrue(playerSet.contains("Radosin"));
        assertTrue(playerSet.contains("zen"));

        assertEquals("Team Vitality", playerToTeamNameMap.get("Alpha54"));
        assertEquals("Team Vitality", playerToTeamNameMap.get("Radosin"));
        assertEquals("Team Vitality", playerToTeamNameMap.get("zen"));

        assertEquals("Team Vitality", uppercaseTeamNameMap.get("TEAM VITALITY"));
        assertEquals("Alpha54", uppercasePlayerNameMap.get("ALPHA54"));
        assertEquals("Radosin", uppercasePlayerNameMap.get("RADOSIN"));
        assertEquals("zen", uppercasePlayerNameMap.get("ZEN"));
        assertEquals("Fairy Peak!", uppercasePlayerNameMap.get("FAIRY PEAK!"));
    }

    @Test
    public void testGetTeamBdsDataFromLiquipedia()
    {
        final Map<String, String> bdsRoster = teamToPlayerAndCoachMap.get("Team BDS");
        assertEquals(4, bdsRoster.size());
        assertEquals("M0nkey M00n", bdsRoster.get("1"));
        assertEquals("dralii", bdsRoster.get("2"));
        assertEquals("ExoTiiK", bdsRoster.get("3"));
        assertEquals("Kassio", bdsRoster.get("C"));

        final Set<String> playerSet = teamToPlayerNameMap.get("Team BDS");
        assertTrue(playerSet.contains("M0nkey M00n"));
        assertTrue(playerSet.contains("dralii"));
        assertTrue(playerSet.contains("ExoTiiK"));

        assertEquals("Team BDS", playerToTeamNameMap.get("M0nkey M00n"));
        assertEquals("Team BDS", playerToTeamNameMap.get("dralii"));
        assertEquals("Team BDS", playerToTeamNameMap.get("ExoTiiK"));

        assertEquals("Team BDS", uppercaseTeamNameMap.get("TEAM BDS"));
        assertEquals("M0nkey M00n", uppercasePlayerNameMap.get("M0NKEY M00N"));
        assertEquals("dralii", uppercasePlayerNameMap.get("DRALII"));
        assertEquals("ExoTiiK", uppercasePlayerNameMap.get("EXOTIIK"));
        assertEquals("Kassio", uppercasePlayerNameMap.get("KASSIO"));
    }
}