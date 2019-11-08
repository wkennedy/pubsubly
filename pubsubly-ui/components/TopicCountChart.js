import React from 'react';
import {Bar, BarChart, CartesianGrid, Tooltip, XAxis, YAxis,} from 'recharts';

function parseDomain(topics) {
    let arr = Object.values(topics);
    let max = Math.max.apply(null, arr.map(entry => entry.count));
    return [
        0,
        Math.ceil(max/100)*100
    ]}

const TopicCountChart = (props) => (

    <BarChart
        width={800}
        height={420}
        data={props.topics}
        margin={{
            top: 20, right: 30, left: 20, bottom: 5,
        }}
    >
        <CartesianGrid stroke="#ccc" strokeDasharray="2 2" />
        <XAxis dataKey="text" hide={true}/>
        <YAxis domain={parseDomain(props.topics)}/>
        <Tooltip />
        <Bar dataKey="count" fill="#8884d8" />
    </BarChart>
);

export default TopicCountChart