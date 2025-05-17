const BASE_URL = 'http://localhost:8080/api/products';

export const fetchAllProducts = async () => {
    const res = await fetch(`${BASE_URL}/all`);
    return res.json();
};

export const optimizeBasket = async (items) => {
    const query = items.map(i => `items=${encodeURIComponent(i)}`).join('&');
    const res = await fetch(`${BASE_URL}/optimize?${query}`);
    return res.json();
};

export const fetchTopDiscounts = async (limit = 10) => {
    const res = await fetch(`${BASE_URL}/top-discounts?limit=${limit}`);
    return res.json();
};

export const fetchNewDiscounts = async () => {
    const res = await fetch(`${BASE_URL}/new-discounts`);
    return res.json();
};

export const fetchPriceHistory = async (store = null, brand = null, category = null) => {
    const params = new URLSearchParams();
    if (store) params.append('store', store);
    if (brand) params.append('brand', brand);
    if (category) params.append('category', category);

    const res = await fetch(`${BASE_URL}/price-history?${params.toString()}`);
    return res.json();
};

export const fetchBestValueProducts = async () => {
    const res = await fetch(`${BASE_URL}/best-value`);
    return res.json();
};

export const fetchTriggeredAlerts = async () => {
    const res = await fetch(`${BASE_URL}/triggered-alerts`);
    return res.json();
};

