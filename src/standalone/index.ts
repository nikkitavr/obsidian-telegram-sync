import { loadConfig } from './config';

async function main() {
  const configArgIndex = process.argv.indexOf('--config');
  const configPath = configArgIndex !== -1 && process.argv[configArgIndex + 1] ? process.argv[configArgIndex + 1] : 'config.json';
  try {
    const cfg = await loadConfig(configPath);
    console.log('Telegram Sync started with config:', configPath);
    console.log(cfg);
  } catch (err) {
    console.error('Failed to load config', err);
    process.exit(1);
  }
}

main();
