
import React from 'react';
import type { KnowledgeItem } from '../types';
import { Language } from '../constants';
import { Icon } from './Icon';

interface KnowledgeCardProps {
  items: KnowledgeItem[];
  language: Language;
}

const KnowledgeCard: React.FC<KnowledgeCardProps> = ({ items, language }) => {
  const cardTitle = language === 'he' ? 'פינת הידע' : 'Interesting Knowledge';

  if (!items || items.length === 0) {
    return null;
  }

  return (
    <div className="bg-white dark:bg-slate-800 rounded-3xl shadow-lg overflow-hidden p-6 md:p-8 transition-shadow hover:shadow-xl">
      <div className="flex items-center space-x-3 rtl:space-x-reverse mb-4">
        <div className="w-12 h-12 bg-amber-100 dark:bg-amber-900/50 rounded-full flex items-center justify-center">
          <Icon name="lightbulb" className="w-6 h-6 text-amber-500 dark:text-amber-400" />
        </div>
        <h2 className="text-xl font-bold text-slate-800 dark:text-slate-100">{cardTitle}</h2>
      </div>
      <div className="space-y-4">
        {items.map((item, index) => (
          <div key={index}>
            <h3 className="text-lg font-semibold text-slate-700 dark:text-slate-200">{item.title}</h3>
            <p className="text-slate-600 dark:text-slate-300 mt-1">{item.text}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default KnowledgeCard;
