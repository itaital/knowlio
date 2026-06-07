import React from 'react';
import { Language, SUPPORTED_LANGUAGES } from '../constants';
import { Icon } from './Icon';

interface HeaderProps {
  currentView: 'home' | 'history';
  onNavigate: (view: 'home' | 'history') => void;
  currentLanguage: Language;
  onLanguageChange: (lang: Language) => void;
  currentTheme: 'light' | 'dark';
  onToggleTheme: () => void;
}

const Header: React.FC<HeaderProps> = ({
  currentView,
  onNavigate,
  currentLanguage,
  onLanguageChange,
  currentTheme,
  onToggleTheme
}) => {
  const NavButton: React.FC<{ view: 'home' | 'history'; text: string; }> = ({ view, text }) => (
    <button
      onClick={() => onNavigate(view)}
      className={`px-3 py-2 rounded-md text-sm font-medium transition-colors ${
        currentView === view
          ? 'bg-indigo-500 text-white'
          : 'text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'
      }`}
    >
      {text}
    </button>
  );

  return (
    <header className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm sticky top-0 z-10 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-2 rtl:space-x-reverse">
            <Icon name="knowlio-logo" className="h-9 w-9" />
            <h1 className="text-2xl font-bold text-slate-800 dark:text-slate-100">Knowlio</h1>
          </div>
          <div className="flex items-center space-x-2 sm:space-x-4">
            <nav className="flex space-x-2 p-1 bg-slate-100 dark:bg-slate-700 rounded-lg">
              <NavButton view="home" text={currentLanguage === 'he' ? 'היום' : 'Today'} />
              <NavButton view="history" text={currentLanguage === 'he' ? 'היסטוריה' : 'History'} />
            </nav>

            <div className="relative">
              <select
                value={currentLanguage}
                onChange={(e) => onLanguageChange(e.target.value as Language)}
                className="appearance-none bg-slate-100 dark:bg-slate-700 border-none rounded-md py-2 ps-3 pe-8 text-sm font-medium text-slate-600 dark:text-slate-300 focus:ring-2 focus:ring-indigo-500"
                aria-label="Select language"
              >
                {SUPPORTED_LANGUAGES.map(lang => (
                  <option key={lang.code} value={lang.code}>
                    {lang.name.split(' ')[0]}
                  </option>
                ))}
              </select>
              <div className="pointer-events-none absolute inset-y-0 end-0 flex items-center px-2 text-slate-500">
                <Icon name="chevron-down" className="h-4 w-4" />
              </div>
            </div>

            <button
              onClick={onToggleTheme}
              className="p-2 rounded-full text-slate-500 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
              aria-label="Toggle theme"
            >
              <Icon name={currentTheme === 'dark' ? 'sun' : 'moon'} className="h-6 w-6" />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;