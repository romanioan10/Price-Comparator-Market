import React, { useEffect, useState } from 'react';
import { fetchAllProducts } from '../services/api';

function ProductList() {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        const load = async () => {
            try {
                const data = await fetchAllProducts();
                setProducts(data);
            } catch (e) {
                console.error("Eroare la încărcarea produselor:", e);
            }
        };
        load();
    }, []);

    return (
        <div>
            <h2>Produse disponibile</h2>
            <ul>
                {products.map((p) => (
                    <li key={p.productId}>
                        {p.productName} - {p.price} RON ({p.storeName})
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default ProductList;
