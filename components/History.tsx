import React, { useState, useEffect } from 'react';
import { getBundle, listSavedDates } from '../services/factsService';
import type { DailyQuoteBundle } from '../types';
import { Language } from '../constants';
import QuoteCard from './QuoteCard';
import KnowledgeCard from './KnowledgeCard';
import WhoWereTheyCard from './WhoWereTheyCard';
import LoadingSpinner from './LoadingSpinner';
import { Icon } from './Icon';

interface HistoryProps {
  language: Language;
}

const History: React.FC<HistoryProps> = ({ language }) => {
  const [dates, setDates] = useState<string[]>([]);
  const [selectedDate, setSelectedDate] = useState<string>('');
  const [bundle, setBundle] = useState<DailyQuoteBundle | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const savedDates = listSavedDates();
    setDates(savedDates);
    if (savedDates.length > 0) {
      setSelectedDate(savedDates[0]);
    }
  }, []);

  useEffect(() => {
    if (!selectedDate) return;
    const fetchBundleByDate = async () => {
      setLoading(true);
      setBundle(null);
      const data = await getBundle(selectedDate);
      setBundle(data);
      setLoading(false);
    };
    fetchBundleByDate();
  }, [selectedDate]);
  
  const content = bundle?.languages[language];
  const direction = language === Language.HEBREW ? 'rtl' : 'ltr';

  return (
    <div className="space-y-6" dir={direction}>
      <div className="max-w-xs mx-auto">
        <label htmlFor="date-select" className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
          {language === Language.HEBREW ? 'בחר תאריך' : 'Select a date'}
        </label>
        <div className="relative">
          <select
            id="date-select"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            disabled={dates.length === 0}
            className="w-full appearance-none bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-600 rounded-md py-2 ps-3 pe-10 text-slate-800 dark:text-slate-200 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            {dates.map(date => (
              <option key={date} value={date}>
                {new Date(date + 'T00:00:00').toLocaleDateString(language, { year: 'numeric', month: 'long', day: 'numeric' })}
              </option>
            ))}
          </select>
          <div className="pointer-events-none absolute inset-y-0 end-0 flex items-center px-2 text-slate-500">
            <Icon name="chevron-down" className="h-5 w-5" />
          </div>
        </div>
      </div>
      
      {loading && (
        <div className="flex justify-center items-center h-64">
          <LoadingSpinner />
        </div>
      )}

      {bundle && content && !loading && (
        <div className="space-y-6 animate-fade-in">
          <QuoteCard quotes={content.quoteOfTheDay} language={language} />
          <KnowledgeCard items={content.interestingKnowledge} language={language} />
          <WhoWereTheyCard people={content.whoWereThey} language={language} />
        </div>
      )}
      
      {!content && !loading && selectedDate && (
        <div className="text-center text-slate-500 p-8 bg-white dark:bg-slate-800 rounded-lg shadow-md">
            <p>{language === Language.HEBREW ? 'אין תוכן זמין עבור התאריך והשפה שנבחרים.' : 'No content available for the selected date and language.'}</p>
        </div>
      )}
    </div>
  );
};

export default History;