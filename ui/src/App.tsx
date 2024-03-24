import React, { useEffect, useState } from 'react';
import './App.css';

interface Series {
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


interface SeriesEvent {
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

const App: React.FC = () => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
  // FIXME: these can't be lists, they need to be maps - as the same ID can be sent down multiple times on update
  const [seriesEvents, setSeriesEvents] = useState<(SeriesEvent)[]>([]);
  const [series, setSeries] = useState<(Series)[]>([]);
  const [messages, setMessages] = useState<(String)[]>([]);

  useEffect(() => {
    const ws = new WebSocket('ws://localhost:8887'); // Replace with your WebSocket server URL
    setSocket(ws);

    return () => {
      ws.close();
    };
  }, []);

  useEffect(() => {
    if (!socket)
    {
      return;
    } 

    socket.onmessage = (event) => {
      console.log("event", event);
      const newData = JSON.parse(event.data);
      console.log("newData", newData);
      if (newData._index === 'seriesevent')
      {
        console.log("it's a series event");
        setSeriesEvents((prevSeriesEvents) => [...prevSeriesEvents, newData]);
      } else if (newData._index === 'series')
      {
        console.log("it's a series");
        setSeries((prevSeries) => [...prevSeries, newData]);
      }
      setMessages((prevMessages) => [...prevMessages, event.data]);
    }

    return () => {
      socket.onmessage = null;
    };
  }, [socket]);

  return (
    <div>
      <h1>Hello, WebSocket!</h1>
      <h2>All messages</h2>
      <div>
        {messages.map((message, index) => (
          <div key={index}>
            {message}
          </div>
        ))}
      </div>
      <h2>All series events</h2>
      <div>
        {seriesEvents.map((seriesEvent, index) => (
          <div key={index}>
            {JSON.stringify(seriesEvent)};
          </div>
        ))}
      </div>
      <h2>All series</h2>
      <div>
        {series.map((series, index) => (
          <div key={index}>
            {JSON.stringify(series)};
          </div>
        ))}
      </div>
    </div>
  );
};

export default App;
