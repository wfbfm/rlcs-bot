import React, { useEffect, useState } from 'react';
import './App.css';

interface Series {
  title: string;
  name: string;
  type: string;
}

interface SeriesEvent {
  title: string;
  name: string;
  type: string;
}

const App: React.FC = () => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
  // const [messages, setMessages] = useState<(Series | SeriesEvent)[]>([]);
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
      // const newData = JSON.parse(event.data);
      // console.log("newData", newData);
      // setMessages((prevMessages) => [...prevMessages, newData]);
      setMessages((prevMessages) => [...prevMessages, event.data]);
      console.log("messages", messages);
    };

    return () => {
      socket.onmessage = null;
    };
  }, [socket]);

  return (
    <div>
      <h1>Hello, WebSocket!</h1>
      <div>
        {messages.map((message, index) => (
          <div key={index}>
            {message}
          </div>
        ))}
      </div>
    </div>
  );
};

export default App;
