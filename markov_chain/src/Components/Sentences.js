import WebsocketComponent from './WebsocketComponent.js';
import React from 'react';
import SockJsClient from 'react-stomp';

class SentenceList extends React.Component {
    constructor(props) {
      super(props);
      this.idKey = 0;
    }

    render() {
      let listOfMessages = this.props.messages.map((sentence) =>
            <li id={'sentence' + this.idKey++}>{sentence}</li>
          );
      return (
        <ol reversed id="sentences">{listOfMessages}</ol>
      );
    }
}

class Sentences extends WebsocketComponent {
 
    constructor(props) {
      super(props);
      this.state = {
        messages: this.deque.toArray()
      };
    }

    render() {
    return (
      <div>
        <SockJsClient url='http://localhost:8090/gs-guide-websocket' topics={['/chain/generatedSentences']} onMessage={(msg) => super.handleData(msg)} />
        <SentenceList messages={this.state.messages} />
      </div>
    );
  }
}

export default Sentences;