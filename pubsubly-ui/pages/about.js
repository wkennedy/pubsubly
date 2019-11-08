import React, {Component} from "react";
import {Box, Container, Paper} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";

class About extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Container maxWidth={"xl"}>
                <br/><br/>
                <Grid container spacing={10}>
                    <Grid item xs={6}>
                        <Card>
                            <CardHeader
                                title="What is it"
                            />
                            <CardContent>
                                Pubsubly is an application to track messages as they flow through a pub/sub backed message driven architecture.

                                It does this by allowing users to group messages by metadata through the use of pluggable Spring auto-configurations. As each message enters the system it is processed and grouped according to one of the user defined tags.
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={6}>
                        <Card>
                            <CardHeader
                                title="Why"
                            />
                            <CardContent>
                                When developing microservices for an event driven architecture utilizing Kafka as the messaging platform, it was clear additional tooling was needed. Viewing and tracking messages across different messaging platforms is necessary for debugging and testing purposes as well as monitoring the health of the system in production.
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={6}>
                        <Card>
                            <CardHeader
                                title="Tech Stack"
                            />
                            <CardContent>
                                <ul>
                                    <li>
                                        Front-End - Javascript / React / NextJS / Material
                                    </li>
                                    <li>
                                        Back-End - Java / Spring-Boot / Spring-Integration
                                    </li>
                                    <li>
                                        Supports the following platforms:
                                        <ul>
                                            <li>
                                                Kafka
                                            </li>
                                            <li>
                                                ActiveMQ
                                            </li>
                                            <li>
                                                Redis
                                            </li>
                                        </ul>
                                    </li>
                                </ul>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={6}>
                        <Card>
                            <CardHeader
                                title="Future"
                            />
                            <CardContent>
                                <ul>
                                    <li>
                                        Sequence classification and machine learning for anomaly detection
                                    </li>
                                    <li>
                                        Notifications
                                    </li>
                                    <li>
                                        Admin portal for live configuration
                                    </li>
                                    <li>
                                        More data analysis and charts
                                    </li>
                                    <li>
                                        Scalability for millions of messages
                                    </li>
                                    <li>
                                        Support for different media types
                                    </li>
                                </ul>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12}>
                        <br/>
                        <Box>
                            <Typography variant="body2" gutterBottom>
                                For any question please contact: <a href="mailto:wildo.waggins@gmail.com">Will Kennedy</a>
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
            </Container>
        )
    }
}

export default About