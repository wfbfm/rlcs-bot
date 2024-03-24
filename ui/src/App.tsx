import React, { useEffect, useState } from 'react';
import './App.css';

interface Series {
  title: string;
  name: string;
  type: string;
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
        // setMessages((prevMessages) => [...prevMessages, newData]);
      };
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
