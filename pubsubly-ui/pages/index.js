import React, {Component} from "react";
import Grid from "@material-ui/core/Grid";
import withStyles from "@material-ui/core/styles/withStyles";
import {host} from "../config";
import fetch from 'isomorphic-unfetch'
import dynamic from 'next/dynamic';
import {interval} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';
import MaterialTable from "material-table";
import Link from "next/link";
import {Container} from "@material-ui/core";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Tooltip from "@material-ui/core/Tooltip";
import {getTooltipForTag} from "../displayUtil";

const DynamicComponentWithCustomLoading = dynamic({
    loader: () => import('../components/TopicCountChart'),
    loading: () => <p>Loading...</p>,
    ssr: false
});

const BubbleChartDynamic = dynamic({
    loader: () => import('../components/BubbleChart'),
    loading: () => <p>Loading...</p>,
    ssr: false
});

const styles = theme => ({
    root: {
        flexGrow: 1,
    },
    paper: {
        padding: theme.spacing(2),
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
    control: {
        padding: theme.spacing(2)
    }
});

function getColumns(props) {
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

    props.tags.map((tag) => (
        getColumn(columns, tag)
    ));

    return columns;
}

function getColumn(columns, tag) {
    let fieldName = 'messageKeyMap.' + tag.value;

    return columns.push({
        title: tag.id, field: fieldName, removable: true, render: rowData =>
            getLink(rowData, fieldName, tag)
    });

}

function getLink(rowData, fieldName, tag) {
    let toolTipText = getTooltipForTag(tag);

    if (rowData.messageKeyMap != null) {
        return (<Link as={'/tag/' + tag.value + '/' + encodeURIComponent(rowData.messageKeyMap[tag.value])}
                      href={'/tags?key=' + tag.value + '&value=' + encodeURIComponent(rowData.messageKeyMap[tag.value])}>
            <a>
                <Tooltip title={toolTipText}>
                    <span>
                        {rowData.messageKeyMap[tag.value]}
                    </span>
                </Tooltip>
            </a>
        </Link>)
    }
    return (<a>{rowData}</a>)
}

class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isLoading: true,
            messageResources: [],
        };

    }

    static async getInitialProps({req}) {
        const currentHost = host(req);

        const tagsResponse = await fetch(currentHost + '/tags');
        const tags = await tagsResponse.json();

        const res = await fetch(currentHost + '/stats/topicCount');
        const topics = await res.json();

        const countsByDateRes = await fetch(currentHost + '/stats/countsByDate');
        const countsByDate = await countsByDateRes.json();

        return {
            tags, topics, countsByDate, currentHost
        }
    }

    componentDidMount() {
        const request = interval(2000).pipe(
            startWith(0),
            switchMap(() =>
                fetch(this.props.currentHost + '/messages/stream')
                    .then((response) => response.json())
            ));


        this._subscription = request.subscribe(
            messageResource => {
                console.log(messageResource);
                this.setState({messageResources: messageResource, isLoading: false});
            }
        )
    }

    componentWillUnmount() {
        this._subscription.unsubscribe();
        this.state = {
            isLoading: false,
            messageResources: []
        };
    }

    render() {
        return (
                <Container maxWidth={"xl"}>
                <Grid container spacing={8}>
                    <Grid item xs={6}>
                        <Card>
                            <CardHeader
                                title="Message Counts per Topic"
                            />
                            <CardContent>
                                <DynamicComponentWithCustomLoading topics={this.props.topics}/>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={6}>
                        <Card>
                            <CardHeader
                                title="Message Counts per Hour per Day (Heat Map)"
                            />
                            <CardContent>
                                <BubbleChartDynamic
                                    countsByDate={this.props.countsByDate}/>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
                <Grid container spacing={8}>
                    <Grid item xs={12}>
                        <MaterialTable
                            title={'Latest messages'}
                            columns={getColumns(this.props)}
                            data={this.state.messageResources}
                            options={{
                                grouping: false,
                                sorting: false,
                                pageSize: 20,
                                pageSizeOptions: [10, 20, 50, 100]
                            }}
                        />
                    </Grid>
                </Grid>
                </Container>
        );
    }
}

export default withStyles(styles)(Home);