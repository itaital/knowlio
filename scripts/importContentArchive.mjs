import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';

const LANGUAGE_ALIASES = {
  en: 'en',
  english: 'en',
  he: 'he',
  hebrew: 'he',
  'עברית': 'he',
  es: 'es',
  spanish: 'es',
  'español': 'es',
  fr: 'fr',
  french: 'fr',
  'français': 'fr',
  de: 'de',
  german: 'de',
  deutsch: 'de',
  tr: 'tr',
  turkish: 'tr',
  'türkçe': 'tr',
};

const Language = {
  ENGLISH: 'en',
  HEBREW: 'he',
  SPANISH: 'es',
  FRENCH: 'fr',
  GERMAN: 'de',
  TURKISH: 'tr',
};

function usage() {
  console.error('Usage: node scripts/importContentArchive.mjs <source-file> [--start-date=YYYY-MM-DD]');
  process.exit(1);
}

function stripCodeFence(source) {
  const match = source.match(/```(?:ts|typescript|json)?\s*([\s\S]*?)```/i);
  return match ? match[1] : source;
}

function extractExpression(source) {
  const cleaned = stripCodeFence(source)
    .replace(/import\s+[^;]+;?/g, '')
    .replace(/export\s+default\s+/g, 'module.exports.default = ')
    .replace(/export\s+(const|let|var)\s+/g, '$1 ')
    .replace(/\b(const|let|var)\s+([A-Za-z_$][\w$]*)\s*:\s*[^=]+=/g, '$1 $2 =')
    .replace(/:\s*DailyRecord\[\]/g, '')
    .replace(/:\s*DailyQuoteBundle\[\]/g, '')
    .replace(/:\s*Record<string,\s*DailyQuoteBundle>/g, '');

  const directJson = cleaned.trim();
  if (directJson.startsWith('{') || directJson.startsWith('[')) {
    return JSON.parse(directJson);
  }

  const context = {
    module: { exports: {} },
    exports: {},
    Language,
  };
  vm.createContext(context);

  const names = [
    'CONTENT_ARCHIVE',
    'DAILY_CONTENT',
    'DAILY_RECORDS',
    'dailyContent',
    'dailyRecords',
    'contentArchive',
    'archive',
  ];

  const probe = `${cleaned}\nthis.__archiveProbe = { ${names
    .map((name) => `${name}: typeof ${name} !== 'undefined' ? ${name} : undefined`)
    .join(', ')} };`;
  vm.runInContext(probe, context, { timeout: 10_000 });

  for (const name of names) {
    if (context.__archiveProbe?.[name]) return context.__archiveProbe[name];
  }
  if (context.module.exports.default) return context.module.exports.default;
  if (Object.keys(context.module.exports).length) return context.module.exports;

  throw new Error('Could not find a supported archive export in source file.');
}

function normalizeLanguageKey(key) {
  const normalized = String(key).trim().toLowerCase();
  return LANGUAGE_ALIASES[normalized] ?? key;
}

function normalizeQuote(quote) {
  if (typeof quote === 'string') return quote.trim();
  if (quote && typeof quote === 'object') {
    const text = String(quote.text ?? quote.quote ?? '').trim();
    const author = String(quote.author ?? quote.name ?? '').trim();
    return author ? `${text} - ${author}` : text;
  }
  return '';
}

function normalizeFact(item) {
  if (typeof item === 'string') {
    return { title: item.slice(0, 60), text: item };
  }
  return {
    title: String(item?.title ?? '').trim(),
    text: String(item?.text ?? item?.description ?? '').trim(),
  };
}

function normalizePerson(item) {
  return {
    name: String(item?.name ?? item?.author ?? '').trim(),
    bio: String(item?.bio ?? item?.description ?? '').trim(),
  };
}

function addDays(dateKey, offset) {
  const date = new Date(`${dateKey}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() + offset);
  return date.toISOString().slice(0, 10);
}

function normalizeBundle(input, fallbackDate) {
  if (!input || typeof input !== 'object') {
    throw new Error('Archive item is not an object.');
  }
  const date = String(input.date ?? fallbackDate ?? '').trim();
  if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
    throw new Error(`Invalid or missing date: ${date}`);
  }

  const sourceLanguages = input.languages ?? input.content ?? {};
  const languages = {};

  for (const [rawKey, rawContent] of Object.entries(sourceLanguages)) {
    const key = normalizeLanguageKey(rawKey);
    const quotes = rawContent?.quoteOfTheDay ?? rawContent?.quotes ?? [];
    const facts = rawContent?.interestingKnowledge ?? rawContent?.facts ?? [];
    const people = rawContent?.whoWereThey ?? rawContent?.people ?? [];

    languages[key] = {
      quoteOfTheDay: quotes.map(normalizeQuote).filter(Boolean),
      interestingKnowledge: facts.map(normalizeFact).filter((fact) => fact.title && fact.text),
      whoWereThey: people.map(normalizePerson).filter((person) => person.name && person.bio),
    };
  }

  return { date, languages };
}

function normalizeArchive(raw, startDate) {
  const entries = Array.isArray(raw) ? raw : Object.values(raw);
  const archive = {};
  for (const [index, entry] of entries.entries()) {
    const bundle = normalizeBundle(entry, startDate ? addDays(startDate, index) : undefined);
    archive[bundle.date] = bundle;
  }
  return Object.fromEntries(Object.entries(archive).sort(([a], [b]) => a.localeCompare(b)));
}

function writeArchive(archive) {
  const target = path.resolve('services/contentArchive.ts');
  const body = JSON.stringify(archive, null, 2);
  const source = `import type { DailyQuoteBundle } from '../types';\n\nexport const CONTENT_ARCHIVE: Record<string, DailyQuoteBundle> = ${body};\n\nexport function getArchivedBundle(date: string): DailyQuoteBundle | null {\n  return CONTENT_ARCHIVE[date] ?? null;\n}\n\nexport function listArchiveDates(): string[] {\n  return Object.keys(CONTENT_ARCHIVE).sort((a, b) => b.localeCompare(a));\n}\n\nexport function listReachedArchiveDates(today: string): string[] {\n  return listArchiveDates().filter((date) => date <= today);\n}\n\nexport function hasArchivedContent(): boolean {\n  return Object.keys(CONTENT_ARCHIVE).length > 0;\n}\n`;
  fs.writeFileSync(target, source, 'utf8');
}

const [, , sourcePath, ...args] = process.argv;
if (!sourcePath) usage();
const startDateArg = args.find((arg) => arg.startsWith('--start-date='));
const startDate = startDateArg?.split('=')[1];
if (startDate && !/^\d{4}-\d{2}-\d{2}$/.test(startDate)) {
  throw new Error(`Invalid --start-date value: ${startDate}`);
}

const rawSource = fs.readFileSync(sourcePath, 'utf8');
const rawArchive = extractExpression(rawSource);
const archive = normalizeArchive(rawArchive, startDate);
writeArchive(archive);

console.log(`Imported ${Object.keys(archive).length} day(s) into services/contentArchive.ts`);
