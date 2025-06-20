import { promises as fs } from 'fs';

export interface StandaloneConfig {
  botToken: string;
  vaultPath: string;
}

export async function loadConfig(path: string): Promise<StandaloneConfig> {
  const data = await fs.readFile(path, 'utf8');
  return JSON.parse(data) as StandaloneConfig;
}

export async function saveConfig(path: string, config: StandaloneConfig): Promise<void> {
  await fs.writeFile(path, JSON.stringify(config, null, 2), 'utf8');
}
