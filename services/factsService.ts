
import type { DailyQuoteBundle } from '../types';
import { MOCK_DATA } from './mockData';

const GIST_SIMULATION_DELAY = 500; // ms

// Simulates fetching from a remote source like a Gist
async function fetchBundleFromGist(date: string): Promise<DailyQuoteBundle | null> {
  console.log(`Simulating fetch for date: ${date}`);
  return new Promise(resolve => {
    setTimeout(() => {
      const data = MOCK_DATA[date];
      if (data) {
        resolve(data);
      } else {
        // In a real app, this would be a 404.
        console.warn(`No mock data found for date: ${date}`);
        resolve(null);
      }
    }, GIST_SIMULATION_DELAY);
  });
}

function getBundleKey(date: string): string {
  return `bundle_${date}`;
}

export async function getBundle(date: string): Promise<DailyQuoteBundle | null> {
  const key = getBundleKey(date);
  
  // 1. Try to load from localStorage (offline-first)
  const cachedRaw = localStorage.getItem(key);
  if (cachedRaw) {
    try {
      console.log(`Loaded from cache for date: ${date}`);
      return JSON.parse(cachedRaw) as DailyQuoteBundle;
    } catch (e) {
      console.error("Failed to parse cached bundle", e);
      // If parsing fails, remove the corrupted item
      localStorage.removeItem(key);
    }
  }

  // 2. If not in cache, fetch from "network"
  const bundle = await fetchBundleFromGist(date);
  if (bundle) {
    // 3. Save to localStorage for future offline access
    try {
      localStorage.setItem(key, JSON.stringify(bundle));
      console.log(`Saved to cache for date: ${date}`);
    } catch (e) {
      console.error("Failed to save bundle to localStorage", e);
    }
  }
  
  return bundle;
}

export function listSavedDates(): string[] {
  const dates: string[] = [];
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (key && key.startsWith('bundle_')) {
      dates.push(key.replace('bundle_', ''));
    }
  }
  // Also add mock data keys that might not be in localStorage yet
  Object.keys(MOCK_DATA).forEach(date => {
      if (!dates.includes(date)) {
          dates.push(date);
      }
  });

  // Sort descending (most recent first) and remove duplicates
  return [...new Set(dates)].sort((a, b) => b.localeCompare(a));
}
