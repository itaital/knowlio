import React, { useState, useEffect, useCallback } from 'react';
import { getBundle } from '../services/factsService';
import type { DailyQuoteBundle } from '../types';
import { Language } from '../constants';
import QuoteCard from './QuoteCard';
import KnowledgeCard from './KnowledgeCard';
import WhoWereTheyCard from './WhoWereTheyCard';
import LoadingSpinner from './LoadingSpinner';
import { getContentDateKey } from '../services/dateUtils';

interface HomeProps {
  language: Language;
}

const Home: React.FC<HomeProps> = ({ language }) => {
  const [bundle, setBundle] = useState<DailyQuoteBundle | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchTodayBundle = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const today = getContentDateKey();
      const data = await getBundle(today);
      if (data) {
        setBundle(data);
      } else {
        setError(language === Language.HEBREW ? 'לא נמצא תוכן להיום. נסה לרענן.' : 'No content found for today. Please try refreshing.');
      }
    } catch (err) {
      setError(language === Language.HEBREW ? 'שגיאה בטעינת הנתונים.' : 'Failed to load data.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [language]);

  useEffect(() => {
    fetchTodayBundle();
  }, [fetchTodayBundle]);

  const content = bundle?.languages[language];
  const direction = language === Language.HEBREW ? 'rtl' : 'ltr';

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center text-red-500 p-8 bg-white dark:bg-slate-800 rounded-lg shadow-md">
        <p>{error}</p>
        <button
          onClick={fetchTodayBundle}
          className="mt-4 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600 transition-colors"
        >
          {language === Language.HEBREW ? 'רענן' : 'Refresh'}
        </button>
      </div>
    );
  }

  if (!bundle || !content) {
    return (
        <div className="text-center text-slate-500 p-8 bg-white dark:bg-slate-800 rounded-lg shadow-md">
            <p>{language === Language.HEBREW ? 'אין תוכן זמין עבור השפה שנבחרה.' : 'No content available for the selected language.'}</p>
        </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in" dir={direction}>
      <QuoteCard quotes={content.quoteOfTheDay} language={language} />
      <KnowledgeCard items={content.interestingKnowledge} language={language} />
      <WhoWereTheyCard people={content.whoWereThey} language={language} />
    </div>
  );
};

export default Home;
