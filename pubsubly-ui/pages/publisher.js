import React, {Component} from "react";
import {Box, Container, Paper} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import JSONInput from "react-json-editor-ajrm";
import locale    from 'react-json-editor-ajrm/locale/en';
import FormControl from "@material-ui/core/FormControl";
import Button from "@material-ui/core/Button";
import withStyles from "@material-ui/core/styles/withStyles";

let message = {};

class Publisher extends Component {

    constructor(props) {
        super(props);

        this.state = {
            message: {example:"value"}
        };
        console.log("in constructor: " + this.state);
        // this.jsonInputOnChange = this.jsonInputOnChange.bind(this)
    }

    jsonInputOnChange(jsonInput) {
        console.log(jsonInput);
        message = jsonInput.json;
        // this.setState({message: jsonInput.json});
        // this.state.message = jsonInput.json;
    }

    publishMessage() {
        console.log(message);
        event.preventDefault();
    }

    render() {
        return (
            <Container maxWidth={"xl"}>
                <JSONInput
                    id          = 'a_unique_id'
                    placeholder = { this.state.message }
                    locale      = { locale }
                    height      = '550px'
                    onChange    = {this.jsonInputOnChange}
                />

                <FormControl>
                    <Button id="publishButton"
                            onClick={this.publishMessage}>
                        Publish
                    </Button>
                </FormControl>
            </Container>
        )
    }
}

export default Publisher