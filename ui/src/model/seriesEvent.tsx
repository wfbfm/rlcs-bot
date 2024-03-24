export default interface SeriesEvent {
  _index: 'seriesevent';
  _id: string;
  _score: number;
  _ignored: string[];
  _source: {
    eventId: string;
    seriesId: string;
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
    bestOf: number;
    currentGameNumber: number;
    evaluation: string;
    commentary: string;
  };
}