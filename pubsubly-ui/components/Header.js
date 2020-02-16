import PropTypes from 'prop-types';
import {withStyles} from '@material-ui/core/styles';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import React, {Component} from "react";
import Typography from "@material-ui/core/Typography";
import MenuIcon from '@material-ui/icons/Menu';
import Drawer from "@material-ui/core/Drawer";
import HomeIcon from '@material-ui/icons/Home';
import ExploreIcon from '@material-ui/icons/Explore';
import InfoIcon from '@material-ui/icons/Info';
import Link from "next/link";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import SearchIcon from "@material-ui/icons/Search";
import LocalHospitalIcon from '@material-ui/icons/LocalHospital';
import {host} from "../config";
import HearingIcon from '@material-ui/icons/Hearing';

const styles = theme => ({
    root: {
        width: '100%',
        paddingBottom: '2rem'
    },
    flex: {
        flex: 1,
    },
    menuButton: {
        marginLeft: -12,
        marginRight: 20,
    },
    drawerHeader: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: '0 8px',
    },
    drawerPaper: {
        width: '200'
    },
    list: {
        width: 250,
    },
    fullList: {
        width: 'auto',
    }
});

class Header extends Component {

    constructor(props) {
        super(props);
    }

    state = {
        left: false,
    };

    toggleDrawer = (side, open) => () => {
        this.setState({
            [side]: open,
        });
    };

    render() {
        const {classes} = this.props;

        const sideList = (
            <div className={classes.list}>
                <List>
                    <ListItem button key='home'>
                        <ListItemIcon>{<HomeIcon/>}</ListItemIcon>
                        <Link href={'/'}><a><ListItemText primary='Home'/></a></Link>
                    </ListItem>
                    <ListItem button key='topics'>
                        <ListItemIcon>{<ExploreIcon/>}</ListItemIcon>
                        <Link href={'/topics'}><a><ListItemText primary='Topics'/></a></Link>
                    </ListItem>
                    <ListItem button key='listeners'>
                        <ListItemIcon>{<HearingIcon/>}</ListItemIcon>
                        <Link href={'/listeners'}><a><ListItemText primary='Listeners'/></a></Link>
                    </ListItem>
                    <ListItem button key='search'>
                        <ListItemIcon>{<SearchIcon/>}</ListItemIcon>
                        <Link href={'/search'}><a><ListItemText primary='Search'/></a></Link>
                    </ListItem>
                    {/*<ListItem button key='publisher'>*/}
                    {/*    <ListItemIcon>{<PublishIcon/>}</ListItemIcon>*/}
                    {/*    <Link href={'/publisher'}><a><ListItemText primary='Publisher'/></a></Link>*/}
                    {/*</ListItem>*/}
                    {/*<ListItem button key='api'>*/}
                    {/*    <ListItemIcon>{<CloudIcon/>}</ListItemIcon>*/}
                    {/*    <a href={this.props.currentHost + '/swagger-ui.html'} target="_blank"><ListItemText primary='API'/></a>*/}
                    {/*</ListItem>*/}
                    <ListItem button key='ui_status'>
                        <ListItemIcon>{<LocalHospitalIcon/>}</ListItemIcon>
                        <a href={"/status"} target="_blank"><ListItemText primary='UI Status'/></a>
                    </ListItem>
                    <ListItem button key='about'>
                        <ListItemIcon>{<InfoIcon/>}</ListItemIcon>
                        <Link href={'/about'}><a><ListItemText primary='About'/></a></Link>
                    </ListItem>
                </List>
            </div>
        );

        return (
            <div className={classes.root}>
                <AppBar position="static">
                    <Toolbar>
                        <IconButton onClick={this.toggleDrawer('left', true)} className={classes.menuButton}
                                    color="inherit" aria-label="Menu">
                            <MenuIcon/>
                        </IconButton>
                        <Typography variant="h5" color="inherit" className={classes.flex}>
                            Pubsubly - Message Tracker
                        </Typography>
                    </Toolbar>
                </AppBar>

                <Drawer open={this.state.left} onClose={this.toggleDrawer('left', false)}>
                    <div
                        tabIndex={0}
                        role="button"
                        onClick={this.toggleDrawer('left', false)}
                        onKeyDown={this.toggleDrawer('left', false)}
                    >
                        {sideList}
                    </div>
                </Drawer>

            </div>
        );
    }
}

Header.propTypes = {
    classes: PropTypes.object.isRequired,
};

Header.getInitialProps = async function(context) {
    const currentHost = host(context.req);

    return {currentHost}
};

export default withStyles(styles)(Header);



