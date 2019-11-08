import {withRouter} from 'next/router'
import fetch from 'isomorphic-unfetch'
import JSONTree from 'react-json-tree'
import React from "react";
import MaterialTable from "material-table";
import Link from "next/link";
import {host, jsonTreeTheme} from "../config";
import {Container} from "@material-ui/core";
import Tooltip from "@material-ui/core/Tooltip";
import {getTooltipForTag} from "../displayUtil";

function getColumns(props) {
    let columns =     [
        // { title: 'ID', field: 'message.headers.id', type: 'string'},
        { title: 'Timestamp', field: 'message.headers.timestamp', render: rowData => {
                let cellData = rowData.message.headers.timestamp;
                if(rowData.message.headers.kafka_receivedTimestamp) {
                    cellData = rowData.message.headers.kafka_receivedTimestamp;
                }
                return cellData;
            } },
        { title: 'ISO Time', field: 'message.headers.timestamp', render: rowData => {
                let cellData = rowData.message.headers.timestamp;
                if(rowData.message.headers.kafka_receivedTimestamp) {
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

    return columns.push({ title: tag.id, field: fieldName, removable: true, render: rowData =>
            getLink(rowData, fieldName, tag)
    });

}

function getLink(rowData, fieldName, tag) {
    let toolTipText = getTooltipForTag(tag);

    if(rowData.messageKeyMap != null) {
        return (<Link as={'/tag/' + tag.value + '/' + encodeURIComponent(rowData.messageKeyMap[tag.value])} href={'/tags?key=' + tag.value + '&value=' + encodeURIComponent(rowData.messageKeyMap[tag.value])}>
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

const TopicMessages = withRouter(props => (
    <Container maxWidth={"xl"}>
        <MaterialTable
            title={props.router.query.topic}
            columns={getColumns(props)}
            data={(query) => new Promise((resolve, reject) => {
                let url = props.currentHost + `/topics/` + props.topic;
                url += "?pageSize=" + query.pageSize;
                url += "&page=" + (query.page + 1);

                fetch(url).then(response => response.json()).then(result => {
                    resolve({
                        data: result.data,
                        page: result.page - 1,
                        totalCount: result.total
                    });
                });
            })}
            detailPanel={rowData => {
                return (
                    <JSONTree data={rowData.message} theme={jsonTreeTheme} invertTheme={true}/>
                )
            }}
            options={{
                grouping: true,
                sorting: true,
                pageSize: 20,
                pageSizeOptions: [10, 20, 50, 100]
            }}
        />
    </Container>
));

TopicMessages.getInitialProps = async function(context) {
    const currentHost = host(context.req);
    const topic = context.query.topic;
    const res = await fetch(currentHost + `/topics/` + topic);
    const messageResources = await res.json();
    const tagsResponse = await fetch(currentHost + '/tags');
    const tags = await tagsResponse.json();
    return {messageResources, tags, topic, currentHost}
};

export default TopicMessages