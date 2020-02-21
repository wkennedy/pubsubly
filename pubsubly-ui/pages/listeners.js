import React, {Component} from "react";
import {Container} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import withStyles from "@material-ui/core/styles/withStyles";
import TextField from "@material-ui/core/TextField";
import {host} from "../config";
import fetch from "isomorphic-unfetch";
import PropTypes from "prop-types";
import makeStyles from "@material-ui/core/styles/makeStyles";
import ButtonGroup from "@material-ui/core/ButtonGroup";

let message = {};

const styles = makeStyles(theme => ({
    root: {
        '& .MuiTextField-root': {
            margin: theme.spacing(1),
            width: 200,
        },
    },
}));

class Listeners extends Component {

    constructor(props) {
        super(props);

        this.state = {
            topicNames: this.props.addedTopics,
            topicName: '',
            topicPattern: '',
            data: []
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        console.log("target: " + target + " value: " + value + " name: " + name);
        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        let buttonId = event.currentTarget.id;
        let url;
        let postUpdate;
        if (buttonId === 'addTopicButton') {
            url = new URL(this.props.currentHost + "/listeners/kafka/add/topics");
            url.searchParams.set('topics', this.state.topicName);
            postUpdate = (async () => {
                let topicNameList = this.state.topicNames.concat(this.state.topicName);
                this.setState({topicNames: topicNameList});
            })
        } else if (buttonId === 'removeTopicButton') {
            url = new URL(this.props.currentHost + "/listeners/kafka/remove/topics");
            url.searchParams.set('topics', this.state.topicName);
            postUpdate = (async () => {
                let topicNameList = this.state.topicNames.concat(this.state.topicName);
                topicNameList = topicNameList.filter(item => item !== this.state.topicName);
                this.setState({topicNames: topicNameList});
            })
        } else if (buttonId === 'addPatternButton') {
            url = new URL(this.props.currentHost + "/listeners/kafka/add/topic-pattern");
            url.searchParams.set('topicPattern', this.state.topicPattern);
        } else if (buttonId === 'payloadValueSubmit') {
            url = this.props.currentHost + '/payload/' + this.state.payloadValue;
        }

        console.log(url);

        fetch(url, {
            method: "POST"
        })
            .then((response) => response.json())
            .then((responseJson) => {
                console.log(responseJson);
                postUpdate();
                // this.setState({tag: this.state.tag, data: responseJson, isLoading: false})
            })
            .catch((error) => {
                console.error(error);
            });

        event.preventDefault();
    }

    addTopic() {
        console.log("Adding Topic:" + message);
        event.preventDefault();
    }

    render() {
        const {classes} = this.props;

        return (
            <div>
                <Container maxWidth="false">
                    <Card>
                        <CardHeader title="Kafka Information"/>
                        <CardContent>
                            <Typography variant="h6" component="h2">
                                Kafka Brokers:
                            </Typography>
                            <Typography color="textSecondary">
                                {this.props.kafkaInfo.bootstrapServers}
                            </Typography>

                            <Typography variant="h6" component="h2">
                                Group-ID:
                            </Typography>
                            <Typography color="textSecondary">
                                {this.props.kafkaInfo.groupId}
                            </Typography>

                            <Typography variant="h6" component="h2">
                                Default topic-pattern:
                            </Typography>
                            <Typography color="textSecondary">
                                {this.props.kafkaInfo.topicPattern}
                            </Typography>

                            <Typography variant="h6" component="h2">
                                Default topic names:
                            </Typography>
                            <Typography color="textSecondary">
                                {this.props.kafkaInfo.topicNames}
                            </Typography>

                            <Typography variant="h6" component="h2">
                                Additional topic names:
                            </Typography>
                            <Typography color="textSecondary">
                                {this.state.topicNames.join(", ")}
                            </Typography>
                        </CardContent>
                    </Card>
                </Container>

                <Container maxWidth="false">
                    <form className={classes.root} noValidate autoComplete="off">
                        <TextField
                            id='topicNameField'
                            name="topicName"
                            onChange={this.handleInputChange}
                            margin='normal'
                            label="Kafka Topic Name"/>
                    </form>
                    <ButtonGroup
                        orientation="vertical"
                        color="primary"
                        aria-label="vertical outlined primary button group"
                    >
                        <Button id="addTopicButton"
                                onClick={this.handleSubmit}>
                            Add
                        </Button>
                        <Button id="removeTopicButton"
                                onClick={this.handleSubmit}>
                            Remove
                        </Button>
                    </ButtonGroup>
                    <form noValidate autoComplete="off">
                        <TextField
                            id='topicPatternField'
                            name="topicPattern"
                            onChange={this.handleInputChange}
                            margin='normal'
                            label="Kafka Topic Pattern"/>
                    </form>

                    <ButtonGroup
                        orientation="vertical"
                        color="primary"
                        aria-label="vertical outlined primary button group"
                    >
                        <Button id="addPatternButton"
                                onClick={this.handleSubmit}>
                            Add
                        </Button>
                        <Button id="removePatternButton"
                                onClick={this.handleSubmit}>
                            Remove
                        </Button>
                    </ButtonGroup>

                </Container>
            </div>
        )
    }
}

Listeners.getInitialProps = async function ({req}) {
    const currentHost = host(req);
    const kafkaInfoResponse = await fetch(currentHost + 'listeners/kafka/kafka-info');
    const kafkaInfo = await kafkaInfoResponse.json();
    const addedTopicsResponse = await fetch(currentHost + '/listeners/kafka/added-topics');
    const addedTopics = await addedTopicsResponse.json();

    return {
        kafkaInfo, addedTopics, currentHost
    }
};

Listeners.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Listeners);