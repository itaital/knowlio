import { useState, useEffect, Dispatch, SetStateAction } from 'react';

function getStorageValue<T,>(key: string, defaultValue: T): T {
  if (typeof window !== 'undefined') {
    const saved = localStorage.getItem(key);
    if (saved) {
      try {
        return JSON.parse(saved) as T;
      } catch (e) {
        console.error('Failed to parse localStorage value', e);
        return defaultValue;
      }
    }
  }
  return defaultValue;
}

// FIX: Use `Dispatch` and `SetStateAction` from React for the return type to fix namespace errors.
export function useLocalStorage<T,>(key: string, defaultValue: T): [T, Dispatch<SetStateAction<T>>] {
  const [value, setValue] = useState<T>(() => {
    return getStorageValue(key, defaultValue);
  });

  useEffect(() => {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (e) {
      console.error('Failed to set localStorage value', e);
    }
  }, [key, value]);

  return [value, setValue];
}
