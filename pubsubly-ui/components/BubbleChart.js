import React, { PureComponent } from 'react';
import {
    ScatterChart, Scatter, XAxis, YAxis, ZAxis, Tooltip,
    Legend,
} from 'recharts';

function parseDomain(countsByDate) {
    let arr = Object.values(countsByDate);
    let max = Math.max.apply(null, arr.map(entry => entry.max));
    let average = max / arr.length;
    return [
    0,
    average,
]}

export default class BubbleChart extends PureComponent {
    renderTooltip = (props) => {
        const { active, payload } = props;

        if (active && payload && payload.length) {
            const data = payload[0] && payload[0].payload;

            return (
                <div style={{
                    backgroundColor: '#fff', border: '1px solid #999', margin: 0, padding: 10,
                }}
                >
                    <p>{data.hour}</p>
                    <p>
                        <span>value: </span>
                        {data.value}
                    </p>
                </div>
            );
        }

        return null;
    };

    render() {
        const domain = parseDomain(this.props.countsByDate);
        const range = [16, 225];

        return (
            <div>
                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" interval={0} tick={{ fontSize: 0 }} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" name="sunday" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Sunday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.SUNDAY.hourValues} fill="#8884d8" />
                </ScatterChart>

                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" name="hour" interval={0} tick={{ fontSize: 0 }} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Monday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.MONDAY.hourValues} fill="#8884d8" />
                </ScatterChart>

                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" name="hour" interval={0} tick={{ fontSize: 0 }} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Tuesday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.TUESDAY.hourValues} fill="#8884d8" />
                </ScatterChart>

                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" name="hour" interval={0} tick={{ fontSize: 0 }} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Wednesday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.WEDNESDAY.hourValues} fill="#8884d8" />
                </ScatterChart>

                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" name="hour" interval={0} tick={{ fontSize: 0 }} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Thursday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.THURSDAY.hourValues} fill="#8884d8" />
                </ScatterChart>

                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" name="hour" interval={0} tick={{ fontSize: 0 }} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Friday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.FRIDAY.hourValues} fill="#8884d8" />
                </ScatterChart>

                <ScatterChart
                    width={800}
                    height={60}
                    margin={{
                        top: 10, right: 0, bottom: 0, left: 0,
                    }}
                >
                    <XAxis type="category" dataKey="hour" name="hour" interval={0} tickLine={{ transform: 'translate(0, -6)' }} />
                    <YAxis type="number" dataKey="index" height={10} width={80} tick={false} tickLine={false} axisLine={false} label={{ value: 'Saturday', position: 'insideRight' }} />
                    <ZAxis type="number" dataKey="value" domain={domain} range={range} />
                    <Tooltip cursor={{ strokeDasharray: '3 3' }} wrapperStyle={{ zIndex: 100 }} content={this.renderTooltip} />
                    <Scatter data={this.props.countsByDate.WEDNESDAY.hourValues} fill="#8884d8" />
                </ScatterChart>
            </div>
        );
    }
}
