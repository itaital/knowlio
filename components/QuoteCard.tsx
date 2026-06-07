import React from 'react';
import { Language } from '../constants';
import { Icon } from './Icon';

interface QuoteCardProps {
  quotes: string[];
  language: Language;
}

const QuoteCard: React.FC<QuoteCardProps> = ({ quotes, language }) => {
  const handleShare = async () => {
    if (!quotes || quotes.length === 0) return;
    
    const title = language === 'he' ? 'ציטוטים יומיים' : 'Daily Quotes';
    const text = quotes.map(q => {
        const [quoteText, author] = q.split('–');
        return `"${quoteText.trim()}"\n– ${author ? author.trim() : 'Unknown'}`;
    }).join('\n\n');

    if (navigator.share) {
      try {
        await navigator.share({ title, text });
      } catch (error) {
        console.error('Error sharing', error);
      }
    } else {
      navigator.clipboard.writeText(text);
      alert(language === 'he' ? 'הציטוטים הועתקו!' : 'Quotes copied to clipboard!');
    }
  };

  const cardTitle = language === 'he' ? 'ציטוטים יומיים' : 'Quotes of the Day';

  if (!quotes || quotes.length === 0) {
    return null;
  }

  return (
    <div className="bg-white dark:bg-slate-800 rounded-3xl shadow-lg overflow-hidden p-6 md:p-8 transition-shadow hover:shadow-xl">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center space-x-3 rtl:space-x-reverse">
          <div className="w-12 h-12 bg-indigo-100 dark:bg-indigo-900/50 rounded-full flex items-center justify-center">
            <Icon name="quote" className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
          </div>
          <h2 className="text-xl font-bold text-slate-800 dark:text-slate-100">{cardTitle}</h2>
        </div>
        <button
          onClick={handleShare}
          className="p-2 rounded-full text-slate-500 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
          aria-label="Share quotes"
        >
          <Icon name="share" className="w-5 h-5" />
        </button>
      </div>
      <div className="space-y-6">
        {quotes.map((quote, index) => {
          const [text, author] = quote.split('–');
          return (
            <div key={index} className="border-s-4 border-indigo-200 dark:border-indigo-800 ps-4">
              <p className="text-lg md:text-xl text-slate-600 dark:text-slate-300 italic">
                "{text.trim()}"
              </p>
              {author && (
                <p className="text-end text-sm font-medium text-indigo-500 dark:text-indigo-400 mt-2">
                  – {author.trim()}
                </p>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default QuoteCard;