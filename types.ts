
export interface KnowledgeItem {
  title: string;
  text: string;
}

export interface WhoPerson {
  name: string;
  bio: string;
}

export interface LanguageContent {
  quoteOfTheDay: string[];
  interestingKnowledge: KnowledgeItem[];
  whoWereThey: WhoPerson[];
}

export interface DailyQuoteBundle {
  date: string;
  languages: {
    [key: string]: LanguageContent;
  };
}
