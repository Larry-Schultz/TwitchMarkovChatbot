import React from 'react';
import SockJsClient from 'react-stomp';

class Stats extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        cpuUsage: '0%',
        ramUsage: '0%',
        totalMarkovChains: '0'
      };
    }

    handleData(message) {
      this.setState({
        cpuUsage: message.cpuUsage,
        ramUsage: message.ramUsage,
        totalMarkovChains: message.markovChainCount
      });
    }

    render() {
      const spanStyle = {
        color:'red'
      };
      return (
        <div>
          <SockJsClient url='http://localhost:8090/gs-guide-websocket' topics={['/chain/stats']} onMessage={(msg) => this.handleData(msg)} />
          <div>
            <p id="statsLine">
              <b>CPU Usage:</b> <span style={spanStyle}>{this.state.cpuUsage}</span>  <b>Ram Usage:</b> <span style={spanStyle}>{this.state.ramUsage}</span>  <b>Total Markov Chains:</b> <span style={spanStyle}>{this.state.totalMarkovChains}</span></p>
          </div>
        </div>
      );
    }
}

export default Stats;