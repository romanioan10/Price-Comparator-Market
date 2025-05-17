import React, { useEffect, useState } from 'react';
import {
    fetchAllProducts,
    optimizeBasket,
    fetchTopDiscounts,
    fetchNewDiscounts,
    fetchPriceHistory
} from '../services/api';

export default function ResultView({ selected }) {
    const [data, setData] = useState([]);

    useEffect(() => {
        const load = async () => {
            if (selected === 'all') setData(await fetchAllProducts());
            else if (selected === 'optimize') setData(await optimizeBasket(['lapte zuzu', 'ulei']));
            else if (selected === 'top-discounts') setData(await fetchTopDiscounts());
            else if (selected === 'new-discounts') setData(await fetchNewDiscounts());
            else if (selected === 'history') setData(await fetchPriceHistory(null, 'Zuzu', null));
        };
        if (selected) load();
    }, [selected]);

    return (
        <div>
            <h2>Rezultate</h2>
            <pre>{JSON.stringify(data, null, 2)}</pre>
        </div>
    );
}
