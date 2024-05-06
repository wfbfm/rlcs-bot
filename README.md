# Rocket League Commentary Service

### Automated, real-time RLCS text feed

Inspired by live text coverage of traditional sports, RL Commentary Service parses live score information and audio transcripts of the official
[Rocket League Twitch Broadcast](https://www.twitch.tv/rocketleague):

![rlcs-bot](https://github.com/wfbfm/rlcs-bot/assets/49768006/acf37487-bcb2-4a48-ba2a-36eaecc215b0)

### Questions? Join us on Discord:

[![RL Commentary Service Discord](https://discordapp.com/api/guilds/1048640527920271381/embed.png?style=banner2)](https://discord.gg/Y8sBAmrzrq)


### Local Installation

First, make any necessary changes to the placeholder passwords/secret ports in `.env`.

```bash
docker-compose up --build
```

The docker-compose script will spin up:
- An ElasticSearch instance - which stores score update/commentary data
- The React UI - available locally at `http://localhost:80`
- The backend Java app.  This also serves a secret admin websocket at `ws://localhost:<SECRET_ADMIN_APP_PORT>`

### Admin Commands

The admin websocket listens for a number of commands that control the start/stop of the broadcast.
Eventually, I'll make a simple control panel and fit this into the UI.  For now:

#### Update Broadcast & Liquipedia URL

You can pass in a Twitch page for a live broadcast, or a VOD.  Make sure you also update the Liquipedia URL with the corresponding page!

Live broadcasts:

```
{"command": "BROADCAST_URL", "broadcastUrl": "https://www.twitch.tv/rocketleague"}
```
```
{"command": "LIQUIPEDIA_URL", "liquipediaUrl": "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_2/Europe/Open_Qualifier_4"}
```

VOD:

```
{"command": "BROADCAST_URL", "broadcastUrl": "https://www.twitch.tv/videos/2131987392?t=03h05m37s"}
```
```
{"command": "LIQUIPEDIA_URL", "liquipediaUrl": "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_2/North_America/Open_Qualifier_4"}
```

#### Start/Stop Broadcast

```
{"command": "START_BROADCAST"}
```

```
{"command": "STOP_BROADCAST"}
```

#### Twitch -> Liquipedia Name mapping

Some team names are very long, and are thus shown differently on the broadcast.
In case the backend config is missing a mapping, you can add one with:

```
{"command": "DISPLAY_NAME", "displayName": "GRIDSERVE RSV", "liquipediaName":"GRIDSERVE Resolve"}
```

#### Starting a broadcast mid-stream
There is validation in the app to only initiate a new series in the opening seconds of the game.
This prevents accidentally creating a new series during highlight packages in between matches.

If you want to start the feed during a series, you can temporarily relax the validation with:

```
{"command": "ALLOW_MIDSERIES", "allowMidSeries": "true"}
```

Don't forget to re-enforce the validation, once the game has been picked up:
```
{"command": "ALLOW_MIDSERIES", "allowMidSeries": "false"}
```
