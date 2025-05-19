import React, { useState } from 'react';
import './style.css';
import {
    fetchAllProducts,
    optimizeBasket,
    fetchTopDiscounts,
    fetchNewDiscounts,
    fetchPriceHistory,
    fetchBestValueProducts,
    fetchTriggeredAlerts
} from './services/api';
import { extractStoreShortName } from './services/utils.js';
import PriceHistoryChart from './components/PriceHistoryChart';

export default function App() {
    const [selectedOption, setSelectedOption] = useState('');
    const [inputValue, setInputValue] = useState('');
    const [limit, setLimit] = useState(10);
    const [results, setResults] = useState([]);
    const [allProducts, setAllProducts] = useState([]);

    const [filterStore, setFilterStore] = useState('');
    const [filterBrand, setFilterBrand] = useState('');
    const [filterCategory, setFilterCategory] = useState('');

    const handleAction = async () => {
        try {
            if (selectedOption === 'all') {
                const data = await fetchAllProducts();
                setAllProducts(data);
                setResults(data);
            } else if (selectedOption === 'optimize') {
                if (!inputValue.trim()) {
                    alert("Introduceți produse separate prin virgulă.");
                    return;
                }

                const items = inputValue
                    .split(',')
                    .map(i => i.trim())
                    .filter(i => i.length > 0);

                const data = await optimizeBasket(items);

                if (data && typeof data === 'object' && !Array.isArray(data)) {
                    const entries = Object.entries(data);
                    setResults(Array.isArray(entries) ? entries : []);
                } else {
                    console.error("Răspuns invalid de la optimizeBasket:", data);
                    setResults([]);
                }
            } else if (selectedOption === 'top') {
                const discounts = await fetchTopDiscounts(limit);
                const products = await fetchAllProducts();
                setAllProducts(products);
                setResults(discounts);
            } else if (selectedOption === 'new') {
                const data = await fetchNewDiscounts();
                const products = await fetchAllProducts();
                setAllProducts(products);
                setResults(data);
            } else if (selectedOption === 'value') {
                const data = await fetchBestValueProducts();
                setResults(data);
            } else if (selectedOption === 'alerts') {
                const data = await fetchTriggeredAlerts();
                setResults(data);
            } else if (selectedOption === 'history') {
                const data = await fetchPriceHistory(filterStore, filterBrand, filterCategory);
                setResults(data);
            }
        } catch (err) {
            console.error("Eroare:", err);
        }
    };

    return (
        <div className="container">
            <h1>Price Comparator</h1>

            <select
                value={selectedOption}
                onChange={e => setSelectedOption(e.target.value)}
                className="input"
            >
                <option value="">Alege o funcționalitate</option>
                <option value="all">Vezi toate produsele</option>
                <option value="optimize">Optimizează coșul</option>
                <option value="top">Reduceri maxime</option>
                <option value="new">Reduceri recente</option>
                <option value="value">Produse cel mai rentabile</option>
                <option value="alerts">Alerte declanșate</option>
                <option value="history">Istoric prețuri</option>
            </select>

            {selectedOption === 'optimize' && (
                <input
                    type="text"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    placeholder="Ex: lapte, pâine albă, cafea"
                    className="input"
                />
            )}

            {selectedOption === 'top' && (
                <input
                    type="number"
                    min="1"
                    value={limit}
                    onChange={(e) => setLimit(parseInt(e.target.value))}
                    placeholder="Număr de reduceri"
                    className="input"
                    style={{ marginLeft: '10px' }}
                />
            )}

            {selectedOption === 'history' && (
                <div className="filters">
                    <input
                        type="text"
                        placeholder="Magazin (ex: Kaufland)"
                        value={filterStore}
                        onChange={(e) => setFilterStore(e.target.value)}
                        className="input"
                    />
                    <input
                        type="text"
                        placeholder="Brand (ex: Zuzu)"
                        value={filterBrand}
                        onChange={(e) => setFilterBrand(e.target.value)}
                        className="input"
                    />
                    <input
                        type="text"
                        placeholder="Categorie (ex: lactate)"
                        value={filterCategory}
                        onChange={(e) => setFilterCategory(e.target.value)}
                        className="input"
                    />
                </div>
            )}

            <button onClick={handleAction}>Execută</button>

            <div className="results">
                {selectedOption === 'optimize' &&
                    Array.isArray(results) &&
                    results.map(([k, v]) =>
                        v ? (
                            <div key={k}>
                                <strong>{k}</strong> → {v.productName} - {v.price} RON ({extractStoreShortName(v.storeName)})<br />
                                {v.packageQuantity} {v.packageUnit} • {v.brand} • {v.productCategory} • {v.date}
                                <hr />
                            </div>
                        ) : (
                            <div key={k}>
                                <strong>{k}</strong> → <em>indisponibil</em>
                                <hr />
                            </div>
                        )
                    )
                }

                {selectedOption === 'all' &&
                    results.map((item, index) => (
                        <div key={index}>
                            <strong>{item.productName}</strong> - {item.price} RON ({extractStoreShortName(item.storeName)})<br />
                            {item.packageQuantity} {item.packageUnit} • {item.brand} • {item.productCategory} • {item.date}
                            <hr />
                        </div>
                    ))
                }

                {selectedOption === 'top' &&
                    results.map((item, index) => {
                        const found = allProducts.find(p => p.productId === item.productId);

                        return (
                            <div key={index}>
                                {found ? (
                                    <>
                                        <strong>{found.productName}</strong> - {found.price} RON ({extractStoreShortName(found.storeName)})<br />
                                        {found.packageQuantity} {found.packageUnit} • {found.brand} • {found.productCategory} • {found.date}<br />
                                        Reducere: {item.percentageOfDiscount}%
                                    </>
                                ) : (
                                    <>
                                        <strong>{item.productId}</strong> - <em>Produsul nu a fost găsit</em><br />
                                        Reducere: {item.percentageOfDiscount}%
                                    </>
                                )}
                                <hr />
                            </div>
                        );
                    })
                }

                {selectedOption === 'new' &&
                    results.map((item, index) => {
                        const found = allProducts.find(p => p.productId === item.productId) || item;
                        return (
                            <div key={index}>
                                <strong>{found.productName}</strong> - {found.price} RON ({extractStoreShortName(found.storeName)})<br />
                                {found.packageQuantity} {found.packageUnit} • {found.brand} • {found.productCategory} • {found.date}
                                <hr />
                            </div>
                        );
                    })
                }

                {selectedOption === 'value' &&
                    results.map((item, index) => (
                        <div key={index}>
                            <strong>{item.productName}</strong> - {item.price} RON ({extractStoreShortName(item.storeName)})<br />
                            {item.packageQuantity} {item.packageUnit} • {item.brand} • {item.productCategory} • {item.date}<br />
                            Preț / unitate: {(item.price / item.packageQuantity).toFixed(2)} RON
                            <hr />
                        </div>
                    ))
                }

                {selectedOption === 'alerts' &&
                    results.map((alert, index) => (
                        <div key={index}>
                            Produs: <strong>{alert.productId}</strong> | Țintă: {alert.targetPrice} RON<br />
                            <span style={{ color: 'green' }}>Alertă activată ✅</span>
                            <hr />
                        </div>
                    ))
                }

                {selectedOption === 'history' && results.length > 0 && (
                    <>
                        <h2>Istoric prețuri: {results[0].productName}</h2>
                        <PriceHistoryChart data={results} />
                    </>
                )}
            </div>
        </div>
    );
}
