const fs = require('fs');

const inputDir = process.env.UNI_INPUT_DIR || '/home/xiaosong/桌面/资源/three/MallFrontend';
const manifestPath = inputDir + '/manifest.json';

let Manifest = '';
try {
    Manifest = fs.readFileSync(manifestPath, { encoding: 'utf-8' });
} catch(e) {
    console.warn('Warning: Could not read manifest.json at', manifestPath, e.message);
}

function mpliveMainfestPlugin(isOpen) {
    if (process.env.UNI_PLATFORM !== 'mp-weixin') {
        return { name: 'mplive-manifest-plugin', enforce: 'pre' };
    }

    const manifestData = JSON.parse(Manifest || '{}');

    if (isOpen === '0') {
        if (manifestData['mp-weixin']?.plugins) {
            delete manifestData['mp-weixin'].plugins['live-player-plugin'];
        }
    }

    if (isOpen === '1') {
        if (!manifestData['mp-weixin']) manifestData['mp-weixin'] = {};
        if (!manifestData['mp-weixin'].plugins) manifestData['mp-weixin'].plugins = {};
        manifestData['mp-weixin'].plugins['live-player-plugin'] = {
            "version": "1.3.5",
            "provider": "wx2b03c6e691cd7370"
        };
    }

    try {
        fs.writeFileSync(manifestPath, JSON.stringify(manifestData, null, 2), { "flag": "w" });
    } catch(e) {}

    return { name: 'mplive-manifest-plugin', enforce: 'pre' };
}

export default mpliveMainfestPlugin;
