import WebsocketComponent from './WebsocketComponent.js';
import React from 'react';
import SockJsClient from 'react-stomp';

class ChatMessageList extends React.Component {
    constructor(props) {
      super(props);
      this.idKey = 0;
    }

    render() {
      let listOfMessages = this.props.messages.map((chatMessage) =>
            <li id={'chatMessage' + this.idKey++}><strong>#{chatMessage.channel} {chatMessage.username}: </strong> {chatMessage.message}</li>
          );
      return (
        <ul id="chatMessages">{listOfMessages}</ul>
      );
    }
}



class ChatMessages extends WebsocketComponent {

    constructor(props) {
      super(props);
      this.state = {
        messages: this.deque.toArray()
      };
    }

  render() {
    return (
      <div>
        <SockJsClient url='http://localhost:8090/gs-guide-websocket' topics={['/chain/chatMessages']} onMessage={(msg) => super.handleData(msg)} />
        <ChatMessageList messages={this.state.messages} />
      </div>
    );
  }
}

export default ChatMessages;