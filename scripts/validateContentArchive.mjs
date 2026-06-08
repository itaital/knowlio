import fs from 'node:fs';
import vm from 'node:vm';

const REQUIRED_LANGUAGES = ['en', 'he', 'es', 'fr', 'de', 'tr'];
const BAD_ENCODING = /Ã|Â|â€|â€“|�/;

function parseArgs() {
  const args = new Map();
  for (const arg of process.argv.slice(2)) {
    if (arg === '--allow-empty') args.set('allow-empty', true);
    const match = arg.match(/^--([^=]+)=(.+)$/);
    if (match) args.set(match[1], match[2]);
  }
  return args;
}

function eachDate(from, to) {
  const dates = [];
  const cursor = new Date(`${from}T00:00:00Z`);
  const end = new Date(`${to}T00:00:00Z`);
  while (cursor <= end) {
    dates.push(cursor.toISOString().slice(0, 10));
    cursor.setUTCDate(cursor.getUTCDate() + 1);
  }
  return dates;
}

function readArchive() {
  const source = fs
    .readFileSync('services/contentArchive.ts', 'utf8')
    .replace(/import\s+type\s+[^;]+;?/g, '')
    .replace(/export\s+const\s+CONTENT_ARCHIVE/g, 'const CONTENT_ARCHIVE')
    .replace(/:\s*Record<string,\s*DailyQuoteBundle>/g, '')
    .replace(/export\s+function\s+\w+[\s\S]*$/g, '');

  const context = {};
  vm.createContext(context);
  vm.runInContext(`${source}\nthis.archive = CONTENT_ARCHIVE;`, context, { timeout: 10_000 });
  return context.archive;
}

function quoteAuthor(quote) {
  const parts = String(quote).split(/\s[-–—]\s/);
  return parts.length > 1 ? parts.at(-1).trim().toLowerCase() : '';
}

function wordCount(text) {
  const matches = String(text).trim().match(/\S+/g);
  return matches ? matches.length : 0;
}

function validate() {
  const args = parseArgs();
  const archive = readArchive();
  const archiveDates = Object.keys(archive);
  const errors = [];

  if (archiveDates.length === 0) {
    if (args.get('allow-empty')) return errors;
    errors.push('content archive is empty');
    return errors;
  }

  const datesToCheck =
    args.has('from') && args.has('to') ? eachDate(args.get('from'), args.get('to')) : archiveDates;

  const seenQuotesByLang = Object.fromEntries(REQUIRED_LANGUAGES.map((lang) => [lang, new Set()]));
  const seenFactsByLang = Object.fromEntries(REQUIRED_LANGUAGES.map((lang) => [lang, new Set()]));

  for (const date of datesToCheck) {
    const bundle = archive[date];
    if (!bundle) {
      errors.push(`${date}: missing daily bundle`);
      continue;
    }
    if (bundle.date !== date) errors.push(`${date}: bundle date does not match key`);

    for (const lang of REQUIRED_LANGUAGES) {
      const content = bundle.languages?.[lang];
      if (!content) {
        errors.push(`${date}: missing language ${lang}`);
        continue;
      }

      if (content.quoteOfTheDay?.length !== 3) {
        errors.push(`${date}/${lang}: quoteOfTheDay must contain exactly 3 quotes`);
      }
      if (content.whoWereThey?.length !== 3) {
        errors.push(`${date}/${lang}: whoWereThey must contain exactly 3 people`);
      }
      if (content.interestingKnowledge?.length !== 5) {
        errors.push(`${date}/${lang}: interestingKnowledge must contain exactly 5 facts`);
      }

      const peopleNames = new Set((content.whoWereThey ?? []).map((person) => person.name.toLowerCase()));
      for (const quote of content.quoteOfTheDay ?? []) {
        if (BAD_ENCODING.test(quote)) errors.push(`${date}/${lang}: quote has encoding artifacts`);
        const normalizedQuote = quote.toLowerCase();
        if (seenQuotesByLang[lang].has(normalizedQuote)) {
          errors.push(`${date}/${lang}: repeated quote "${quote}"`);
        }
        seenQuotesByLang[lang].add(normalizedQuote);

        const author = quoteAuthor(quote);
        if (!author) errors.push(`${date}/${lang}: quote is missing " - author" suffix`);
        if (author && !peopleNames.has(author)) {
          errors.push(`${date}/${lang}: whoWereThey must include quote author "${author}"`);
        }
      }

      for (const fact of content.interestingKnowledge ?? []) {
        if (BAD_ENCODING.test(fact.title) || BAD_ENCODING.test(fact.text)) {
          errors.push(`${date}/${lang}: fact "${fact.title}" has encoding artifacts`);
        }
        if (wordCount(fact.text) < 25) {
          errors.push(`${date}/${lang}: fact "${fact.title}" is too short for a daily paragraph`);
        }
        const title = fact.title.toLowerCase();
        if (seenFactsByLang[lang].has(title)) {
          errors.push(`${date}/${lang}: repeated fact title "${fact.title}"`);
        }
        seenFactsByLang[lang].add(title);
      }

      if (lang === 'he') {
        const combined = JSON.stringify(content);
        if (!/[\u0590-\u05ff]/.test(combined)) {
          errors.push(`${date}/he: Hebrew content does not contain Hebrew characters`);
        }
      }
    }
  }

  return errors;
}

const errors = validate();
if (errors.length) {
  for (const error of errors) console.error(`Content archive validation failed: ${error}`);
  process.exit(1);
}
console.log('Content archive validation passed.');
