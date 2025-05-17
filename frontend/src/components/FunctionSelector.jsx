import React from 'react';

export default function FunctionSelector({ onSelect }) {
    const options = [
        { label: 'Afișează toate produsele', value: 'all' },
        { label: 'Coș optimizat', value: 'optimize' },
        { label: 'Reduceri actuale', value: 'top-discounts' },
        { label: 'Reduceri noi', value: 'new-discounts' },
        { label: 'Istoric prețuri', value: 'history' }
    ];

    return (
        <select onChange={(e) => onSelect(e.target.value)} defaultValue="">
            <option value="" disabled>Alege o funcționalitate...</option>
            {options.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
        </select>
    );
}
