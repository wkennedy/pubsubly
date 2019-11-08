import React from 'react';
import { Graph } from 'react-d3-graph';

// graph payload (with minimalist structure)
const data = {
    nodes: [{ id: 'Harry' }, { id: 'Sally' }, { id: 'Alice' }],
    links: [{ source: 'Harry', target: 'Sally' }, { source: 'Harry', target: 'Alice' }]
};

// the graph configuration, you only need to pass down properties
// that you want to override, otherwise default ones will be used
const myConfig = {
    "automaticRearrangeAfterDropNode": false,
    "collapsible": true,
    "directed": true,
    "focusAnimationDuration": 0.75,
    "focusZoom": 1,
    "height": 800,
    "highlightDegree": 1,
    "highlightOpacity": 0.2,
    "linkHighlightBehavior": true,
    "maxZoom": 8,
    "minZoom": 0.1,
    "nodeHighlightBehavior": true,
    "panAndZoom": true,
    "staticGraph": false,
    "width": 1600,
    "d3": {
        "alphaTarget": 0.05,
        "gravity": -2500,
        "linkLength": 100,
        "linkStrength": 1
    },
    "node": {
        "color": "#d3d3d3",
        "fontColor": "black",
        "fontSize": 12,
        "fontWeight": "normal",
        "highlightColor": "red",
        "highlightFontSize": 12,
        "highlightFontWeight": "bold",
        "highlightStrokeColor": "SAME",
        "highlightStrokeWidth": 1.5,
        "labelProperty": "name",
        "mouseCursor": "pointer",
        "opacity": 1,
        "renderLabel": true,
        "size": 450,
        "strokeColor": "none",
        "strokeWidth": 1.5,
        "svg": "",
        "symbolType": "circle"
    },
    "link": {
        "color": "#d3d3d3",
        "fontColor": "black",
        "fontSize": 8,
        "fontWeight": "normal",
        "highlightColor": "blue",
        "highlightFontSize": 8,
        "highlightFontWeight": "normal",
        "labelProperty": "label",
        "mouseCursor": "pointer",
        "opacity": 1,
        "renderLabel": false,
        "semanticStrokeWidth": false,
        "strokeWidth": 4
    }
};

// graph event callbacks
// const onClickGraph = function() {
//     window.alert(`Clicked the graph background`);
// };
//
// const onClickNode = function(nodeId) {
//     window.alert(`Clicked node ${nodeId}`);
// };
//
// const onRightClickNode = function(event, nodeId) {
//     window.alert(`Right clicked node ${nodeId}`);
// };
//
// const onMouseOverNode = function(nodeId) {
//     window.alert(`Mouse over node ${nodeId}`);
// };
//
// const onMouseOutNode = function(nodeId) {
//     window.alert(`Mouse out node ${nodeId}`);
// };
//
// const onClickLink = function(source, target) {
//     window.alert(`Clicked link between ${source} and ${target}`);
// };
//
// const onRightClickLink = function(event, source, target) {
//     window.alert(`Right clicked link between ${source} and ${target}`);
// };
//
// const onMouseOverLink = function(source, target) {
//     window.alert(`Mouse over in link between ${source} and ${target}`);
// };
//
// const onMouseOutLink = function(source, target) {
//     window.alert(`Mouse out link between ${source} and ${target}`);
// };



const MessageFlowGraph = (props) => (

    <Graph
        id="message-flow-graph-id" // id is mandatory, if no id is defined rd3g will throw an error
        data={props.messageFlow}
        config={myConfig}
    />
);

export default MessageFlowGraph