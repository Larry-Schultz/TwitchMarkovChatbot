import React from 'react';
import SockJsClient from 'react-stomp';
import { WithContext as ReactTags } from 'react-tag-input';
import axios from 'axios';

const KeyCodes = {
  comma: 188,
  enter: 13,
};
 
const delimiters = [KeyCodes.comma, KeyCodes.enter];

class Channels extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        tags: [
            { id: "Thailand", text: "Thailand" }
        ]
    }
    this.handleDelete = this.handleDelete.bind(this);
    this.handleAddition = this.handleAddition.bind(this);
  }

    handleDelete(i) {
        const { tags } = this.state;
        this.setState({
            tags: tags.filter((tag, index) => index !== i),
        });
    }

    handleAddition(tag) {
        this.setState(state => ({ tags: [...state.tags, tag] }));
    }

    handleData(message) {
        message.map((channel) => {
            this.handleAddition({id: channel, text: channel});
        });

    }

  render() {
    const { tags} = this.state;
    return (
        <div>
          <SockJsClient url='http://localhost:8090/gs-guide-websocket' topics={['/chain/channels']} onMessage={(msg) => this.handleData(msg)} />
          <ReactTags tags={tags}
                    handleDelete={this.handleDelete}
                    handleAddition={this.handleAddition}
                    handleDrag={this.handleDrag}
                    delimiters={delimiters} />
        </div>
    );
  }
}

export default Channels;