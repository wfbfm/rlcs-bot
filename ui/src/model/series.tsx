export default interface Series {
  _index: 'series';
  _id: string;
  _score: number;
  _source: {
    seriesId: string;
    seriesMetaData: {
      date: number[];
      seriesDescription: string;
      liquipediaPage: string;
    };
    completedGames: {
      score: {
        blueScore: number;
        orangeScore: number;
        teamInLead: string;
      };
      clock: {
        displayedTime: string;
        elapsedSeconds: number;
        overtime: boolean;
      };
      winner: string;
    }[];
    currentGame: {
      score: {
        blueScore: number;
        orangeScore: number;
        teamInLead: string;
      };
      clock: {
        displayedTime: string;
        elapsedSeconds: number;
        overtime: boolean;
      };
      winner: string;
    };
    seriesScore: {
      blueScore: number;
      orangeScore: number;
      teamInLead: string;
    };
    blueTeam: {
      player1: { name: string };
      player2: { name: string };
      player3: { name: string };
      teamColour: string;
      teamName: string;
      playerNames: string;
    };
    orangeTeam: {
      player1: { name: string };
      player2: { name: string };
      player3: { name: string };
      teamColour: string;
      teamName: string;
      playerNames: string;
    };
    bestOf: number;
    currentGameNumber: number;
    seriesWinningGameScore: number;
    complete: boolean;
  };
}
