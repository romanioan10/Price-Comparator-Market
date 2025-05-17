export function extractStoreShortName(storePath) {
    if (!storePath) return '';
    const parts = storePath.replace(/\\/g, '/').split('/');
    const name = parts[parts.length - 1];
    return name.includes('_') ? name.split('_')[0] : name;
}
