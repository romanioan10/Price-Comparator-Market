import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import React from 'react';
import {extractStoreShortName} from "../services/utils.js";

export default function PriceHistoryChart({ data }) {
    const grouped = {};

    data.forEach(entry => {
        if (!grouped[extractStoreShortName(entry.storeName)]) grouped[extractStoreShortName(entry.storeName)] = [];
        grouped[extractStoreShortName(entry.storeName)].push({
            date: entry.date,
            price: entry.price
        });
    });

    const allDates = [...new Set(data.map(e => e.date))].sort();
    const chartData = allDates.map(date => {
        const entry = { date };
        for (const store in grouped) {
            const match = grouped[store].find(e => e.date === date);
            if (match) entry[store] = match.price;
        }
        return entry;
    });

    return (
        <ResponsiveContainer width="100%" height={400}>
            <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                {Object.keys(grouped).map((store, idx) => (
                    <Line key={store} type="monotone" dataKey={store} stroke="#8884d8" />
                ))}
            </LineChart>
        </ResponsiveContainer>
    );
}
