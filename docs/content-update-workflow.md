# Knowlio Content Update Workflow

Knowlio is wired to read daily content from `services/contentArchive.ts` first. The UI stays unchanged; this file is the offline static database.

## Import Existing AI Output

```bash
node scripts/importContentArchive.mjs "C:\path\to\contentArchive_30days_complete.ts"
npm run validate:archive:target
npm run report:archive
npm run build
```

The importer accepts arrays, records keyed by date, `DAILY_RECORDS`, `CONTENT_ARCHIVE`, `dailyContent`, and quoted objects. It normalizes quote objects like `{ text, author }` into the app format.

## Validate The Required 30 Days

```bash
npm run validate:archive:target
```

The validator checks November 28, 2025 through December 27, 2025 for:

- 6 languages: English, Hebrew, Spanish, French, German, Turkish
- exactly 3 quotes
- exactly 3 matching `whoWereThey` biographies
- exactly 5 paragraph-length knowledge facts
- duplicate quotes/facts
- obvious text encoding artifacts

`npm run report:archive` gives a quick coverage summary so missing languages or incomplete daily blocks are easy to spot before running the stricter validator.

## Preview A Specific Day

The app normally uses the device's local date. For debugging, open:

```text
http://127.0.0.1:5173/?date=2025-11-28
```

This does not change production behavior; it only helps verify a specific archived date.
