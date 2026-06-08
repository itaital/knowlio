import fs from 'node:fs';
import vm from 'node:vm';

const REQUIRED_LANGUAGES = ['en', 'he', 'es', 'fr', 'de', 'tr'];

function parseArgs() {
  const args = new Map();
  for (const arg of process.argv.slice(2)) {
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

function summarizeContent(content) {
  return {
    quotes: content?.quoteOfTheDay?.length ?? 0,
    people: content?.whoWereThey?.length ?? 0,
    facts: content?.interestingKnowledge?.length ?? 0,
  };
}

const args = parseArgs();
const from = args.get('from') ?? '2025-11-28';
const to = args.get('to') ?? '2025-12-27';
const archive = readArchive();
const dates = eachDate(from, to);

let presentDays = 0;
let completeLanguageBlocks = 0;
const missing = [];
const incomplete = [];

for (const date of dates) {
  const bundle = archive[date];
  if (!bundle) {
    missing.push(`${date}: day missing`);
    continue;
  }
  presentDays += 1;

  for (const lang of REQUIRED_LANGUAGES) {
    const content = bundle.languages?.[lang];
    if (!content) {
      missing.push(`${date}/${lang}: language missing`);
      continue;
    }

    const counts = summarizeContent(content);
    if (counts.quotes === 3 && counts.people === 3 && counts.facts === 5) {
      completeLanguageBlocks += 1;
    } else {
      incomplete.push(
        `${date}/${lang}: quotes=${counts.quotes}, people=${counts.people}, facts=${counts.facts}`,
      );
    }
  }
}

const totalLanguageBlocks = dates.length * REQUIRED_LANGUAGES.length;

console.log(`Archive coverage for ${from} to ${to}`);
console.log(`Days present: ${presentDays}/${dates.length}`);
console.log(`Complete language blocks by count: ${completeLanguageBlocks}/${totalLanguageBlocks}`);

if (missing.length) {
  console.log('\nMissing:');
  for (const item of missing.slice(0, 80)) console.log(`- ${item}`);
  if (missing.length > 80) console.log(`- ...and ${missing.length - 80} more`);
}

if (incomplete.length) {
  console.log('\nIncomplete count blocks:');
  for (const item of incomplete.slice(0, 80)) console.log(`- ${item}`);
  if (incomplete.length > 80) console.log(`- ...and ${incomplete.length - 80} more`);
}
