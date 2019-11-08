import Link from 'next/link'
import fetch from 'isomorphic-unfetch'
import {Container, withStyles} from "@material-ui/core";
import React, {Component} from "react";
import MaterialTable from "material-table";
import PropTypes from "prop-types";
import {host} from "../config";
import VisibilityIcon from "@material-ui/icons/Visibility";

const styles = {
    table: {
        width: '100%',
        textAlign: 'center',
        marginLeft: 'auto',
        marginRight: 'auto'
    },
    paper: {
        width: '50%',
        marginLeft: '25%',
        marginRight: '5%'
    }
};

class Topics extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Container maxWidth={"xl"}>
                <MaterialTable
                    title='Topics'
                    columns={[
                        { title: 'Topic Name', field: 'text', type: 'string'},
                        { title: 'Message Count', field: 'count', type: 'numeric' },
                        { title: 'View Messages', field: 'text', render: rowData => <Link as={'/t/' + rowData.text} href={'/topic?topic=' + rowData.text}><a title='view' href={'/topic?topic=' + rowData.text}><VisibilityIcon/></a></Link> }
                    ]}
                    data={this.props.topics}
                    options={{
                        sorting: true,
                        pageSize: 20,
                        pageSizeOptions: [10, 20, 50, 100]
                    }}
                />
            </Container>
        )
    }
}

Topics.getInitialProps = async function ({req}) {
    const currentHost = host(req);
    const res = await fetch(currentHost + '/topics/counts');
    const topics = await res.json();

    return {
        topics, currentHost
    }
};

Topics.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Topics);