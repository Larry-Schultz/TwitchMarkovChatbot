import React from 'react';
import './App.css';
import ChatMessages from './Components/ChatMessages.js';
import Sentences from './Components/Sentences.js';
import Stats from './Components/Stats.js';
import Channels from './Components/Channels.js';
import { Button  } from 'react-native';
import { Col, Row, Grid } from 'react-native-easy-grid';


class App extends React.Component {
  render() {
    return (
      <div className="App">
        <Button />
        <p><strong>Stats</strong></p>
        <Stats />
        <p><strong>Channels</strong></p>
        <Channels />
        <p><strong>Chat Messages</strong></p>
        <ChatMessages />
        <p><strong>Generated Sentences</strong></p>
        <Sentences />
      </div> 
    );
  }
}

export default App;
