import React from 'react';
import Deque from 'double-ended-queue';

class WebsocketComponent extends React.Component {
  constructor(props) {
    super(props);
    this.deque = new Deque(50);
  }

  deque;

  handleData(data) {
      if(data.length > 0) {
        data.forEach(element => {
          this.deque.push(element);
        });
        let queueLength = this.deque.toArray.length;
        if(queueLength > 50) {
          for(var i = 0; i < (queueLength - 50); i++) {
            this.deque.shift();
          }
        }
        this.setState({
          messages: this.deque.toArray()
        });
      }
    }
}

export default WebsocketComponent;