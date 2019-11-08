import {withRouter} from 'next/router'
import fetch from 'isomorphic-unfetch'
import JSONTree from 'react-json-tree'
import React, {Component} from "react";
import MaterialTable from "material-table";
import Typography from "@material-ui/core/Typography";
import withStyles from "@material-ui/core/styles/withStyles";
import {host, jsonTreeTheme} from "../config";
import PropTypes from "prop-types";
import LinearProgress from "@material-ui/core/LinearProgress";
import dynamic from 'next/dynamic';
import Grid from "@material-ui/core/Grid";
import {Container} from "@material-ui/core";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Tooltip from "@material-ui/core/Tooltip";

const styles = theme => ({
    paper: {
        paddingLeft: '55%',
        marginRight: '5%'
    }
});

const MessageFlowGraphDynamic = dynamic({
    loader: () => import('../components/MessageFlowGraph'),
    loading: () => <p>Loading...</p>,
    ssr: false
});

function getMessageColumns(tags) {
    let columns = [
        // {title: 'ID', field: 'message.headers.id', type: 'string'},
        {
            title: 'Timestamp', field: 'message.headers.timestamp', render: rowData => {
                let cellData = rowData.message.headers.timestamp;
                if (rowData.message.headers.kafka_receivedTimestamp) {
                    cellData = rowData.message.headers.kafka_receivedTimestamp;
                }
                return cellData;
            }
        },
        {
            title: 'ISO Time', field: 'message.headers.timestamp', render: rowData => {
                let cellData = rowData.message.headers.timestamp;
                if (rowData.message.headers.kafka_receivedTimestamp) {
                    cellData = rowData.message.headers.kafka_receivedTimestamp;
                }
                return new Date(cellData).toISOString();
            }
        }
    ];

    tags.map((tag) => (
        getMessageColumn(columns, tag)
    ));

    return columns;
}

function getMessageColumn(columns, tag) {
    let fieldName = 'messageKeyMap.' + tag.value;
    return columns.push({title: tag.id, field: fieldName, type: 'string'});
}

class Tags extends Component {

    constructor(props) {
        super(props);

        this.state = {
            isLoading: true,
            messageDetails: null,
            tags: null
        };

        this.getData();
    }

    componentWillUnmount() {
        this.state = {
            isLoading: true,
            messageDetails: null,
            tags: null
        };
    }

    getData() {
        const {router} = this.props;

        fetch(this.props.currentHost + '/tags')
            .then((response) => response.json())
            .then((responseJson) => {
                this.setState({tags: responseJson})
            })
            .catch((error) => {
                console.error(error);
            });

        fetch(this.props.currentHost + `/tag/` + router.query.key + "/" + router.query.value)
            .then((response) => response.json())
            .then((responseJson) => {
                console.log("responseJson: " + JSON.stringify(responseJson));
                this.setState({tags: this.state.tags, messageDetails: responseJson, isLoading: false})
            })
            .catch((error) => {
                console.error(error);
            });
    }

    render() {
        const {classes, router} = this.props;

        if (this.state.isLoading) {
            return (
                <div className={classes.root}>
                    <LinearProgress/>
                    <br/>
                </div>
            );
        }

        return (
            <Container maxWidth={"xl"}>

                <Container maxWidth={"xl"}>
                    <Typography variant="h5" component="h4">
                        <Tooltip title="This gives the average amount of time between related events as they appear from topic to topic"><span>The average time in milliseconds between hops (ms): {this.state.messageDetails.averageTimeBetweenTopics}</span></Tooltip>
                    </Typography>
                </Container>
                <br/><br/><br/>
                <MaterialTable
                    title={'Message Hops for ' + router.query.key}
                    columns={[
                        {title: 'Topic', field: 'messageFlowStopover.topic', type: 'string'},
                        {title: 'Timestamp', field: 'messageFlowStopover.timestamp', type: 'numeric'},
                        {
                            title: 'ISO Time',
                            field: 'messageFlowStopover.timestamp',
                            render: rowData => new Date(rowData.messageFlowStopover.timestamp).toISOString()
                        },
                        {
                            title: 'Time Since Last Topic (ms)',
                            field: 'messageFlowStopover.timeSinceLastTopic',
                            type: 'numeric'
                        }
                    ]}
                    data={this.state.messageDetails.messageFlow.messageFlowStopovers}
                    options={{
                        sorting: true
                    }}
                />
                <br/><br/><br/>
                <MaterialTable
                    title={'Messages for ' + router.query.key}
                    columns={getMessageColumns(this.state.tags)}
                    data={this.state.messageDetails.messageResourceBundle.messageResources}
                    detailPanel={rowData => {
                        return (
                            <JSONTree data={rowData.message} theme={jsonTreeTheme} invertTheme={true}/>
                        )
                    }}
                    options={{
                        grouping: true,
                        sorting: true,
                        pageSize: 10,
                        pageSizeOptions: [10, 20, 50, 100]
                    }}
                />
                <br/>
                <Grid container spacing={8}>
                    <Grid item xs={12}>
                        <Card>
                            <CardHeader
                                title="Graphic Flow of Messages"
                            />
                            <CardContent>
                                <MessageFlowGraphDynamic
                                    messageFlow={this.state.messageDetails.messageFlow}/>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </Container>
        )
    }
}

Tags.propTypes = {
    classes: PropTypes.object.isRequired,
};

Tags.getInitialProps = async function (context) {
    const key = context.query.key;
    const value = context.query.value;
    const currentHost = host(context.req);

    return {key, value, currentHost}
};

export default withRouter(withStyles(styles)(Tags))
