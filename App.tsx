import React, { useState, useEffect } from 'react';
import Home from './components/Home';
import History from './components/History';
import Header from './components/Header';
import { useLocalStorage } from './hooks/useLocalStorage';
import { Language, SUPPORTED_LANGUAGES } from './constants';
import { getBundle } from './services/factsService';
import { knowlioLogoSvg } from './components/Icon';


type View = 'home' | 'history';

function App() {
  const [view, setView] = useState<View>('home');
  const [language, setLanguage] = useLocalStorage<Language>('knowlio-lang', Language.ENGLISH);
  const [theme, setTheme] = useLocalStorage<'light' | 'dark'>('knowlio-theme', 'light');
  const [notificationPermission, setNotificationPermission] = useState('default');


  useEffect(() => {
    if ('Notification' in window) {
      setNotificationPermission(Notification.permission);
    }
  }, []);

  const requestNotificationPermission = () => {
    if (!('Notification' in window)) {
      alert(language === Language.HEBREW ? 'הדפדפן הזה אינו תומך בהתראות.' : 'This browser does not support desktop notification');
      return;
    }
    Notification.requestPermission().then(permission => {
      setNotificationPermission(permission);
    });
  };

  useEffect(() => {
    const notificationInterval = setInterval(() => {
      if (notificationPermission !== 'granted') return;

      const now = new Date();
      // Check if it's 8:00 AM
      if (now.getHours() === 8 && now.getMinutes() === 0) {
        const todayStr = now.toISOString().split('T')[0];
        const notifiedKey = `notified_for_${todayStr}`;

        if (!localStorage.getItem(notifiedKey)) {
          // Pre-fetch data for offline access then show notification
          getBundle(todayStr).then((bundle) => {
            if (bundle) {
               const iconUrl = `data:image/svg+xml;base64,${btoa(knowlioLogoSvg)}`;
               new Notification('🧠 Knowlio', {
                body: language === Language.HEBREW ? '✨ הידע היומי שלך מוכן!' : '✨ Your daily knowledge is ready!',
                icon: iconUrl,
              });
              localStorage.setItem(notifiedKey, 'true');
            }
          });
        }
      }
    }, 60000); // Check every minute

    return () => clearInterval(notificationInterval);
  }, [notificationPermission, language]);


  useEffect(() => {
    const html = document.documentElement;
    html.lang = language;
    html.dir = SUPPORTED_LANGUAGES.find(l => l.code === language)?.dir || 'ltr';
    if (theme === 'dark') {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
  }, [language, theme]);
  
  const toggleTheme = () => {
    setTheme(prevTheme => prevTheme === 'light' ? 'dark' : 'light');
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 text-slate-800 dark:text-slate-200 font-sans transition-colors duration-300">
      <Header
        currentView={view}
        onNavigate={setView}
        currentLanguage={language}
        onLanguageChange={setLanguage}
        currentTheme={theme}
        onToggleTheme={toggleTheme}
      />
       {notificationPermission === 'default' && (
        <div className="bg-yellow-100 dark:bg-yellow-900/50 border-l-4 border-yellow-500 text-yellow-800 dark:text-yellow-200 p-4 mx-4 sm:mx-6 md:mx-8 my-4 rounded-r-lg shadow-md" role="alert">
          <div className="flex justify-between items-center">
            <div>
              <p className="font-bold">{language === Language.HEBREW ? 'התראות יומיות' : 'Daily Notifications'}</p>
              <p className="text-sm">{language === Language.HEBREW ? 'קבל התראה יומית ב-8 בבוקר כשהידע החדש שלך מוכן!' : 'Get a daily notification at 8 AM when your new knowledge is ready!'}</p>
            </div>
            <button onClick={requestNotificationPermission} className="ms-4 px-3 py-1 bg-yellow-500 text-white text-sm rounded hover:bg-yellow-600 transition-colors">
              {language === Language.HEBREW ? 'הפעל' : 'Enable'}
            </button>
          </div>
        </div>
      )}
      <main className="p-4 sm:p-6 md:p-8">
        {view === 'home' && <Home language={language} />}
        {view === 'history' && <History language={language} />}
      </main>
      <footer className="text-center p-4 text-slate-500 dark:text-slate-400 text-sm">
        <p>&copy; {new Date().getFullYear()} Knowlio. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default App;