import fetch from 'isomorphic-unfetch'
import {Container, withStyles} from "@material-ui/core";
import React, {Component} from "react";
import PropTypes from "prop-types";
import {host, jsonTreeTheme} from "../config";
import TextField from "@material-ui/core/TextField";
import FormControl from "@material-ui/core/FormControl";
import LinearProgress from "@material-ui/core/LinearProgress";
import MaterialTable from "material-table";
import JSONTree from "react-json-tree";
import MenuItem from "@material-ui/core/MenuItem";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";

const styles = ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {},
    dense: {},
    menu: {},
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

class Search extends Component {

    constructor(props) {
        super(props);

        this.state = {
            tag: '',
            value: '',
            headerKey: '',
            headerValue: '',
            payloadValue: '',
            data: []
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    renderTagOptions() {
        return this.props.tags.map((tag, i) => {
            return (
                <MenuItem key={tag.id} value={tag.id}>{tag.value}</MenuItem>
            );
        });
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
        let url = '';
        if (buttonId === 'headerSearchSubmit') {
            url = this.props.currentHost + '/header/' + this.state.headerKey + '/' + encodeURIComponent(this.state.headerValue);///header/campaignId/c_1234
        } else if (buttonId === 'tagSearchSubmit') {
            url = this.props.currentHost + '/messageResources/tag/' + this.state.tag + '/' + encodeURIComponent(this.state.value);
        } else if (buttonId === 'payloadValueSubmit') {
            url = this.props.currentHost + '/payload/' + this.state.payloadValue;
        }

        console.log(url);

        fetch(url)
            .then((response) => response.json())
            .then((responseJson) => {
                console.log(responseJson);
                this.setState({tag: this.state.tag, data: responseJson, isLoading: false})
            })
            .catch((error) => {
                console.error(error);
            });

        event.preventDefault();
    }

    render() {
        const {classes} = this.props;

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

                <Grid container spacing={5}>
                    <Grid item xs={4}>
                        <form noValidate autoComplete="off" onSubmit={this.handleSubmit}>
                            <FormControl fullWidth={true}>
                                <TextField
                                    id="select-tag"
                                    name='tag'
                                    select
                                    label="Select"
                                    className={classes.textField}
                                    value={this.state.tag}
                                    onChange={this.handleInputChange}
                                    SelectProps={{
                                        MenuProps: {
                                            className: classes.menu,
                                        },
                                    }}
                                    helperText="Please select the tag"
                                    margin="normal"
                                    variant="outlined"
                                >
                                    {this.renderTagOptions()}
                                </TextField>
                            </FormControl>
                            <FormControl fullWidth={true}>
                                <TextField
                                    name='value'
                                    id="outlined-name"
                                    className={classes.textField}
                                    label="Search by tag value"
                                    variant="outlined"
                                    onChange={this.handleInputChange}
                                    margin='normal'
                                />
                            </FormControl>

                            <FormControl>
                                <Button id="tagSearchSubmit" variant="contained" className={classes.button}
                                        onClick={this.handleSubmit}>
                                    Search
                                </Button>
                            </FormControl>
                        </form>
                    </Grid>
                    <Grid item xs={4}>
                        <form className={classes.container} noValidate autoComplete="off"
                              onSubmit={this.handleSubmit}>
                            <FormControl fullWidth={true}>
                                <TextField
                                    id="select-tag"
                                    name='headerKey'
                                    label="Please enter the header key"
                                    className={classes.textField}
                                    value={this.state.headerKey}
                                    onChange={this.handleInputChange}
                                    helperText="Please enter the header"
                                    margin="normal"
                                    variant="outlined"
                                />
                            </FormControl>
                            <FormControl fullWidth={true}>

                                <TextField
                                    name='headerValue'
                                    id="outlined-name"
                                    className={classes.textField}
                                    label="Search by header value"
                                    variant="outlined"
                                    onChange={this.handleInputChange}
                                    margin='normal'
                                />
                            </FormControl>

                            <FormControl>
                                <Button id="headerSearchSubmit" variant="contained" className={classes.button}
                                        onClick={this.handleSubmit}>
                                    Search
                                </Button>
                            </FormControl>
                        </form>
                    </Grid>
                    <Grid item xs={4}>
                        <form className={classes.container} noValidate autoComplete="off"
                              onSubmit={this.handleSubmit}>
                            <FormControl fullWidth={true}>

                                <TextField
                                    name='payloadValue'
                                    id="outlined-name"
                                    className={classes.textField}
                                    label="Search by payload value"
                                    variant="outlined"
                                    onChange={this.handleInputChange}
                                    margin='normal'
                                />
                            </FormControl>

                            <FormControl>
                                <Button id="payloadValueSubmit" variant="contained" className={classes.button}
                                        onClick={this.handleSubmit}>
                                    Search
                                </Button>
                            </FormControl>
                        </form>
                    </Grid>
                </Grid>

                <br/><br/>
                <MaterialTable
                    title={'Messages for search'}
                    columns={getMessageColumns(this.props.tags)}
                    data={this.state.data}
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
        )
    }
}

Search.getInitialProps = async function ({req}) {
    const currentHost = host(req);
    const tagsResponse = await fetch(currentHost + '/tags');
    const tags = await tagsResponse.json();

    return {
        tags, currentHost
    }
};

Search.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Search);