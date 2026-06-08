import fs from 'node:fs';

const START_DATE = '2025-11-28';
const DAY_COUNT = 30;

const languages = ['en', 'he', 'es', 'fr', 'de', 'tr'];

const authors = [
  ['Ada Lovelace', {
    en: 'Ada Lovelace was a nineteenth-century mathematician who wrote notes for Charles Babbage\'s Analytical Engine. She is often remembered as the first computer programmer because she imagined machines manipulating symbols, not only numbers.',
    he: 'עדה לאבלייס הייתה מתמטיקאית מהמאה התשע עשרה שכתבה הערות למנוע האנליטי של צ׳רלס בבג׳. היא נחשבת לאחת מחלוצות התכנות מפני שדמיינה מכונות שמעבדות סמלים ולא רק מספרים.',
    es: 'Ada Lovelace fue una matemática del siglo XIX que escribió notas para la Máquina Analítica de Charles Babbage. Se la recuerda como una pionera de la programación porque imaginó máquinas capaces de manipular símbolos, no solo números.',
    fr: 'Ada Lovelace était une mathématicienne du XIXe siècle qui rédigea des notes sur la machine analytique de Charles Babbage. Elle est souvent considérée comme une pionnière de la programmation pour avoir imaginé des machines manipulant des symboles, pas seulement des nombres.',
    de: 'Ada Lovelace war eine Mathematikerin des neunzehnten Jahrhunderts, die Notizen zu Charles Babbages Analytical Engine schrieb. Sie gilt als Pionierin der Programmierung, weil sie Maschinen als Werkzeuge für Symbole und nicht nur für Zahlen verstand.',
    tr: 'Ada Lovelace, Charles Babbage\'in Analitik Makinesi üzerine notlar yazan on dokuzuncu yüzyıl matematikçisiydi. Makinelerin yalnızca sayıları değil sembolleri de işleyebileceğini gördüğü için programlamanın öncülerinden sayılır.',
  }],
  ['Marie Curie', {
    en: 'Marie Curie was a physicist and chemist whose research on radioactivity transformed modern science. She discovered polonium and radium, won Nobel Prizes in two fields, and opened new paths for medical imaging and cancer treatment.',
    he: 'מארי קירי הייתה פיזיקאית וכימאית שחקר הרדיואקטיביות שלה שינה את המדע המודרני. היא גילתה את פולוניום ורדיום, זכתה בפרסי נובל בשני תחומים, ופתחה דרך לרפואה גרעינית ולטיפולי סרטן.',
    es: 'Marie Curie fue una física y química cuya investigación sobre la radiactividad transformó la ciencia moderna. Descubrió el polonio y el radio, ganó premios Nobel en dos campos y abrió nuevos caminos para la medicina.',
    fr: 'Marie Curie était une physicienne et chimiste dont les recherches sur la radioactivité ont transformé la science moderne. Elle découvrit le polonium et le radium, reçut deux prix Nobel et ouvrit des voies nouvelles pour la médecine.',
    de: 'Marie Curie war Physikerin und Chemikerin; ihre Forschung zur Radioaktivität veränderte die moderne Wissenschaft. Sie entdeckte Polonium und Radium, erhielt Nobelpreise in zwei Fachgebieten und prägte medizinische Anwendungen.',
    tr: 'Marie Curie, radyoaktivite üzerine araştırmaları modern bilimi değiştiren fizikçi ve kimyagerdi. Polonyum ile radyumu keşfetti, iki farklı alanda Nobel kazandı ve tıbbi görüntüleme ile tedavi için yeni yollar açtı.',
  }],
  ['Leonardo da Vinci', {
    en: 'Leonardo da Vinci was a Renaissance artist, engineer, anatomist, and inventor. His notebooks joined observation with imagination, showing how painting, mechanics, anatomy, and design could enrich one another.',
    he: 'לאונרדו דה וינצ׳י היה אמן, מהנדס, חוקר אנטומיה וממציא מתקופת הרנסנס. מחברותיו חיברו תצפית ודמיון והראו כיצד ציור, מכניקה, גוף האדם ועיצוב יכולים להזין זה את זה.',
    es: 'Leonardo da Vinci fue artista, ingeniero, anatomista e inventor del Renacimiento. Sus cuadernos unieron observación e imaginación y mostraron cómo la pintura, la mecánica, la anatomía y el diseño podían enriquecerse mutuamente.',
    fr: 'Léonard de Vinci était artiste, ingénieur, anatomiste et inventeur de la Renaissance. Ses carnets liaient observation et imagination, montrant comment peinture, mécanique, anatomie et design pouvaient s’enrichir.',
    de: 'Leonardo da Vinci war Künstler, Ingenieur, Anatom und Erfinder der Renaissance. Seine Notizbücher verbanden Beobachtung mit Vorstellungskraft und zeigten, wie Malerei, Mechanik, Anatomie und Gestaltung einander bereichern.',
    tr: 'Leonardo da Vinci, Rönesans döneminin sanatçısı, mühendisi, anatomi araştırmacısı ve mucidiydi. Defterleri gözlem ile hayal gücünü birleştirerek resim, mekanik, anatomi ve tasarımın birbirini nasıl beslediğini gösterdi.',
  }],
  ['Nelson Mandela', {
    en: 'Nelson Mandela was a South African anti-apartheid leader who spent twenty-seven years in prison before becoming president. His public life became a symbol of courage, reconciliation, and democratic change.',
    he: 'נלסון מנדלה היה מנהיג דרום אפריקאי נגד האפרטהייד שישב עשרים ושבע שנים בכלא לפני שנבחר לנשיא. חייו הציבוריים הפכו לסמל של אומץ, פיוס ושינוי דמוקרטי.',
    es: 'Nelson Mandela fue un líder sudafricano contra el apartheid que pasó veintisiete años en prisión antes de convertirse en presidente. Su vida pública simboliza coraje, reconciliación y cambio democrático.',
    fr: 'Nelson Mandela fut un dirigeant sud-africain opposé à l’apartheid, emprisonné vingt-sept ans avant de devenir président. Sa vie publique est devenue un symbole de courage, de réconciliation et de changement démocratique.',
    de: 'Nelson Mandela war ein südafrikanischer Gegner der Apartheid, der siebenundzwanzig Jahre im Gefängnis verbrachte, bevor er Präsident wurde. Sein öffentliches Leben steht für Mut, Versöhnung und demokratischen Wandel.',
    tr: 'Nelson Mandela, başkan olmadan önce yirmi yedi yıl hapiste kalan Güney Afrikalı apartheid karşıtı liderdi. Kamusal yaşamı cesaretin, uzlaşmanın ve demokratik değişimin simgesi haline geldi.',
  }],
  ['Jane Goodall', {
    en: 'Jane Goodall is a primatologist whose long study of chimpanzees changed how humans understand animal intelligence and social life. Her fieldwork also helped connect science with conservation and public education.',
    he: 'ג׳יין גודול היא פרימטולוגית שמחקרה הארוך על שימפנזים שינה את הבנת האדם לגבי אינטליגנציה וחיים חברתיים של בעלי חיים. עבודתה חיברה בין מדע, שמירת טבע וחינוך ציבורי.',
    es: 'Jane Goodall es una primatóloga cuyo largo estudio de los chimpancés cambió la comprensión humana de la inteligencia animal y la vida social. Su trabajo también unió ciencia, conservación y educación pública.',
    fr: 'Jane Goodall est une primatologue dont la longue étude des chimpanzés a changé notre compréhension de l’intelligence animale et de la vie sociale. Son travail relie aussi science, conservation et éducation.',
    de: 'Jane Goodall ist Primatologin; ihre jahrzehntelange Erforschung von Schimpansen veränderte das Verständnis tierischer Intelligenz und sozialer Beziehungen. Ihre Arbeit verbindet Wissenschaft, Naturschutz und Bildung.',
    tr: 'Jane Goodall, şempanzeler üzerine uzun çalışmasıyla hayvan zekası ve sosyal yaşam anlayışımızı değiştiren primatologdur. Saha çalışması bilimi koruma ve halk eğitimiyle de birleştirdi.',
  }],
  ['Nikola Tesla', {
    en: 'Nikola Tesla was an inventor and electrical engineer whose work shaped alternating current power systems. His experiments with motors, transformers, and wireless transmission influenced modern electrical technology.',
    he: 'ניקולה טסלה היה ממציא ומהנדס חשמל שעבודתו עיצבה מערכות זרם חילופין. ניסוייו במנועים, שנאים ושידור אלחוטי השפיעו על הטכנולוגיה החשמלית המודרנית.',
    es: 'Nikola Tesla fue inventor e ingeniero eléctrico cuyo trabajo moldeó los sistemas de corriente alterna. Sus experimentos con motores, transformadores y transmisión inalámbrica influyeron en la tecnología moderna.',
    fr: 'Nikola Tesla était inventeur et ingénieur électricien; ses travaux ont façonné les systèmes à courant alternatif. Ses expériences sur moteurs, transformateurs et transmission sans fil ont marqué la technologie moderne.',
    de: 'Nikola Tesla war Erfinder und Elektroingenieur; seine Arbeit prägte Wechselstromsysteme. Experimente mit Motoren, Transformatoren und drahtloser Übertragung beeinflussten die moderne Elektrotechnik.',
    tr: 'Nikola Tesla, alternatif akım güç sistemlerini şekillendiren mucit ve elektrik mühendisiydi. Motorlar, transformatörler ve kablosuz iletim üzerine deneyleri modern elektrik teknolojisini etkiledi.',
  }],
  ['Grace Hopper', {
    en: 'Grace Hopper was a computer scientist and U.S. Navy officer who helped make programming more accessible. Her work on compilers and COBOL moved computers closer to ordinary language and practical business use.',
    he: 'גרייס הופר הייתה מדענית מחשב וקצינה בצי האמריקאי שסייעה להפוך תכנות לנגיש יותר. עבודתה על מהדרים ועל COBOL קירבה מחשבים לשפה רגילה ולשימוש עסקי מעשי.',
    es: 'Grace Hopper fue científica de la computación y oficial de la Marina estadounidense que ayudó a hacer la programación más accesible. Su trabajo con compiladores y COBOL acercó las computadoras al lenguaje común.',
    fr: 'Grace Hopper était informaticienne et officière de la marine américaine; elle rendit la programmation plus accessible. Son travail sur les compilateurs et COBOL rapprocha les ordinateurs du langage ordinaire.',
    de: 'Grace Hopper war Informatikerin und Offizierin der US-Marine; sie machte Programmierung zugänglicher. Ihre Arbeit an Compilern und COBOL brachte Computer näher an Alltagssprache und praktische Anwendungen.',
    tr: 'Grace Hopper, programlamayı daha erişilebilir kılan bilgisayar bilimci ve ABD Donanması subayıydı. Derleyiciler ve COBOL üzerindeki çalışmaları bilgisayarları günlük dile ve pratik kullanıma yaklaştırdı.',
  }],
  ['Maya Angelou', {
    en: 'Maya Angelou was a poet, memoirist, and civil rights voice whose writing explored dignity, memory, trauma, and hope. Her books and performances helped generations speak more honestly about identity and resilience.',
    he: 'מאיה אנג׳לו הייתה משוררת, סופרת זיכרונות וקול מרכזי במאבק זכויות האזרח. כתיבתה עסקה בכבוד, זיכרון, טראומה ותקווה וסייעה לדורות לדבר בכנות על זהות וחוסן.',
    es: 'Maya Angelou fue poeta, memorialista y voz de los derechos civiles cuya escritura exploró dignidad, memoria, trauma y esperanza. Sus libros y actuaciones ayudaron a hablar con honestidad sobre identidad y resiliencia.',
    fr: 'Maya Angelou était poétesse, mémorialiste et voix des droits civiques. Son écriture explorait dignité, mémoire, traumatisme et espoir, aidant des générations à parler d’identité et de résilience.',
    de: 'Maya Angelou war Dichterin, Memoirenschreiberin und Stimme der Bürgerrechtsbewegung. Ihr Werk erkundete Würde, Erinnerung, Trauma und Hoffnung und half Generationen, über Identität und Widerstandskraft zu sprechen.',
    tr: 'Maya Angelou, onur, bellek, travma ve umut üzerine yazan şair, anı yazarı ve sivil haklar sesiydi. Kitapları ve performansları kimlik ve dayanıklılık hakkında daha dürüst konuşmaya yardım etti.',
  }],
  ['Albert Einstein', {
    en: 'Albert Einstein was a physicist whose theories of relativity reshaped ideas about space, time, mass, and energy. His scientific imagination made him one of the most recognized figures in modern physics.',
    he: 'אלברט איינשטיין היה פיזיקאי שתורות היחסות שלו עיצבו מחדש רעיונות על מרחב, זמן, מסה ואנרגיה. הדמיון המדעי שלו הפך אותו לאחת הדמויות המזוהות ביותר עם הפיזיקה המודרנית.',
    es: 'Albert Einstein fue un físico cuyas teorías de la relatividad transformaron las ideas sobre espacio, tiempo, masa y energía. Su imaginación científica lo convirtió en una figura central de la física moderna.',
    fr: 'Albert Einstein était un physicien dont les théories de la relativité ont transformé les idées sur l’espace, le temps, la masse et l’énergie. Son imagination scientifique en fit une figure majeure de la physique moderne.',
    de: 'Albert Einstein war Physiker; seine Relativitätstheorien veränderten Vorstellungen von Raum, Zeit, Masse und Energie. Seine wissenschaftliche Vorstellungskraft machte ihn zu einer prägenden Figur der modernen Physik.',
    tr: 'Albert Einstein, görelilik kuramlarıyla uzay, zaman, kütle ve enerji anlayışını değiştiren fizikçiydi. Bilimsel hayal gücü onu modern fiziğin en tanınan isimlerinden biri yaptı.',
  }],
  ['Rachel Carson', {
    en: 'Rachel Carson was a marine biologist and writer whose book Silent Spring helped launch modern environmental awareness. She showed how careful science and clear language can change public policy.',
    he: 'רייצ׳ל קרסון הייתה ביולוגית ימית וסופרת שספרה אביב דומם סייע להצית מודעות סביבתית מודרנית. היא הראתה כיצד מדע זהיר ושפה ברורה יכולים לשנות מדיניות ציבורית.',
    es: 'Rachel Carson fue bióloga marina y escritora; su libro Primavera silenciosa impulsó la conciencia ambiental moderna. Mostró cómo la ciencia cuidadosa y el lenguaje claro pueden cambiar políticas públicas.',
    fr: 'Rachel Carson était biologiste marine et écrivaine; son livre Printemps silencieux contribua à lancer la conscience environnementale moderne. Elle montra que science rigoureuse et langage clair peuvent changer les politiques.',
    de: 'Rachel Carson war Meeresbiologin und Autorin; ihr Buch Silent Spring stärkte das moderne Umweltbewusstsein. Sie zeigte, wie sorgfältige Wissenschaft und klare Sprache öffentliche Politik verändern können.',
    tr: 'Rachel Carson, Sessiz Bahar kitabıyla modern çevre bilincini güçlendiren deniz biyoloğu ve yazardı. Dikkatli bilimin ve açık dilin kamu politikasını değiştirebileceğini gösterdi.',
  }],
];

const focus = [
  ['curiosity', 'סקרנות', 'curiosidad', 'curiosité', 'Neugier', 'merak'],
  ['patience', 'סבלנות', 'paciencia', 'patience', 'Geduld', 'sabır'],
  ['courage', 'אומץ', 'coraje', 'courage', 'Mut', 'cesaret'],
  ['precision', 'דיוק', 'precisión', 'précision', 'Präzision', 'kesinlik'],
  ['wonder', 'פליאה', 'asombro', 'émerveillement', 'Staunen', 'hayret'],
  ['learning', 'למידה', 'aprendizaje', 'apprentissage', 'Lernen', 'öğrenme'],
  ['kindness', 'טוב לב', 'bondad', 'bonté', 'Freundlichkeit', 'iyilik'],
  ['resilience', 'חוסן', 'resiliencia', 'résilience', 'Widerstandskraft', 'dayanıklılık'],
  ['imagination', 'דמיון', 'imaginación', 'imagination', 'Vorstellungskraft', 'hayal gücü'],
  ['attention', 'תשומת לב', 'atención', 'attention', 'Aufmerksamkeit', 'dikkat'],
];

const quoteTemplates = {
  en: [
    'On day {day}, let {focus} become a daily practice, and ordinary hours begin to reveal extraordinary doors.',
    'On day {day}, a careful mind turns {focus} into direction, and direction into work that can outlast doubt.',
    'On day {day}, the future opens more gently when {focus} teaches us to ask one better question.',
  ],
  he: [
    'ביום {day}, כאשר {focus} הופכת להרגל יומי, גם שעה רגילה מתחילה לפתוח דלתות יוצאות דופן.',
    'ביום {day}, מחשבה זהירה הופכת {focus} לכיוון, ואת הכיוון לעבודה שיכולה להחזיק גם מול ספק.',
    'ביום {day}, העתיד נפתח ברכות רבה יותר כאשר {focus} מלמדת אותנו לשאול שאלה אחת טובה יותר.',
  ],
  es: [
    'En el día {day}, cuando la {focus} se vuelve una práctica diaria, las horas comunes empiezan a revelar puertas extraordinarias.',
    'En el día {day}, una mente cuidadosa convierte la {focus} en dirección, y la dirección en trabajo capaz de superar la duda.',
    'En el día {day}, el futuro se abre con más calma cuando la {focus} nos enseña a hacer una pregunta mejor.',
  ],
  fr: [
    'Au jour {day}, quand la {focus} devient une pratique quotidienne, les heures ordinaires commencent à ouvrir des portes extraordinaires.',
    'Au jour {day}, un esprit attentif transforme la {focus} en direction, puis cette direction en travail capable de dépasser le doute.',
    'Au jour {day}, l’avenir s’ouvre plus doucement lorsque la {focus} nous apprend à poser une meilleure question.',
  ],
  de: [
    'An Tag {day}, wenn {focus} zur täglichen Übung wird, beginnen gewöhnliche Stunden außergewöhnliche Türen zu öffnen.',
    'An Tag {day}, ein sorgfältiger Geist verwandelt {focus} in Richtung und Richtung in Arbeit, die Zweifel überdauern kann.',
    'An Tag {day}, die Zukunft öffnet sich ruhiger, wenn {focus} uns lehrt, eine bessere Frage zu stellen.',
  ],
  tr: [
    '{day}. günde {focus} günlük bir alışkanlığa dönüştüğünde, sıradan saatler olağanüstü kapılar göstermeye başlar.',
    '{day}. günde dikkatli bir zihin {focus} duygusunu yöne, yönü de kuşkudan uzun ömürlü çalışmaya dönüştürür.',
    '{day}. günde gelecek, {focus} bize daha iyi bir soru sormayı öğrettiğinde daha sakin açılır.',
  ],
};

const factThemes = [
  ['star nurseries', 'עריסות כוכבים', 'viveros estelares', 'pépinières stellaires', 'Sternentstehungsgebiete', 'yıldız doğumevleri'],
  ['ancient libraries', 'ספריות עתיקות', 'bibliotecas antiguas', 'bibliothèques anciennes', 'antike Bibliotheken', 'antik kütüphaneler'],
  ['coral reefs', 'שוניות אלמוגים', 'arrecifes de coral', 'récifs coralliens', 'Korallenriffe', 'mercan resifleri'],
  ['magnetic navigation', 'ניווט מגנטי', 'navegación magnética', 'navigation magnétique', 'magnetische Navigation', 'manyetik yön bulma'],
  ['deep ocean vents', 'נביעות עומק באוקיינוס', 'respiraderos oceánicos profundos', 'sources hydrothermales profondes', 'Tiefseequellen', 'derin okyanus bacaları'],
  ['seed banks', 'בנקי זרעים', 'bancos de semillas', 'banques de graines', 'Samenbanken', 'tohum bankaları'],
  ['ice cores', 'ליבות קרח', 'núcleos de hielo', 'carottes de glace', 'Eisbohrkerne', 'buz çekirdekleri'],
  ['pollinator networks', 'רשתות מאביקים', 'redes de polinizadores', 'réseaux de pollinisateurs', 'Bestäubernetzwerke', 'tozlayıcı ağları'],
  ['writing systems', 'מערכות כתב', 'sistemas de escritura', 'systèmes d’écriture', 'Schriftsysteme', 'yazı sistemleri'],
  ['volcanic islands', 'איים געשיים', 'islas volcánicas', 'îles volcaniques', 'Vulkaninseln', 'volkanik adalar'],
  ['migrating birds', 'ציפורים נודדות', 'aves migratorias', 'oiseaux migrateurs', 'Zugvögel', 'göçmen kuşlar'],
  ['medieval clocks', 'שעונים מימי הביניים', 'relojes medievales', 'horloges médiévales', 'mittelalterliche Uhren', 'ortaçağ saatleri'],
  ['fungal networks', 'רשתות פטרייתיות', 'redes fúngicas', 'réseaux fongiques', 'Pilznetzwerke', 'mantar ağları'],
  ['desert adaptations', 'הסתגלות למדבר', 'adaptaciones del desierto', 'adaptations au désert', 'Wüstenanpassungen', 'çöl uyumları'],
  ['planetary weather', 'מזג אוויר פלנטרי', 'clima planetario', 'météo planétaire', 'planetarisches Wetter', 'gezegen havası'],
];

const factTemplates = {
  en: 'The study of {topic} reveals how science, history, and nature often meet in one small detail. Researchers compare patterns, materials, and living systems to understand why this subject matters. The lesson is practical: careful observation can turn a familiar idea into evidence, context, and a deeper respect for the world around us.',
  he: 'המחקר של {topic} מגלה כיצד מדע, היסטוריה וטבע נפגשים לעיתים בפרט קטן אחד. חוקרים משווים דפוסים, חומרים ומערכות חיים כדי להבין מדוע הנושא חשוב. הלקח מעשי וברור: תצפית זהירה יכולה להפוך רעיון מוכר לראיה, להקשר ולהערכה עמוקה יותר לעולם שסביבנו.',
  es: 'El estudio de {topic} muestra cómo la ciencia, la historia y la naturaleza se encuentran a menudo en un detalle pequeño. Los investigadores comparan patrones, materiales y sistemas vivos para entender por qué importa este tema. La lección es práctica: observar con cuidado convierte una idea conocida en evidencia, contexto y respeto por el mundo.',
  fr: 'L’étude des {topic} montre comment science, histoire et nature se rencontrent souvent dans un petit détail. Les chercheurs comparent motifs, matériaux et systèmes vivants pour comprendre pourquoi ce sujet compte. La leçon est pratique: une observation attentive transforme une idée familière en preuve, contexte et respect du monde.',
  de: 'Die Untersuchung von {topic} zeigt, wie Wissenschaft, Geschichte und Natur oft in einem kleinen Detail zusammentreffen. Forschende vergleichen Muster, Materialien und lebende Systeme, um die Bedeutung des Themas zu verstehen. Die praktische Lehre lautet: genaue Beobachtung verwandelt Vertrautes in Belege, Kontext und Respekt vor der Welt.',
  tr: '{topic} üzerine çalışma, bilim, tarih ve doğanın çoğu zaman küçük bir ayrıntıda nasıl buluştuğunu gösterir. Araştırmacılar örüntüleri, malzemeleri ve canlı sistemleri karşılaştırarak konunun neden önemli olduğunu anlar. Ders pratiktir: dikkatli gözlem tanıdık bir fikri kanıta, bağlama ve dünyaya saygıya dönüştürür.',
};

function addDays(start, offset) {
  const date = new Date(`${start}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() + offset);
  return date.toISOString().slice(0, 10);
}

function format(template, values) {
  return template.replace(/\{(\w+)\}/g, (_, key) => values[key]);
}

function contentFor(lang, dayIndex) {
  const langIndex = languages.indexOf(lang);
  const selectedAuthors = [0, 1, 2].map((slot) => authors[(dayIndex * 3 + slot) % authors.length]);
  const selectedFocus = [0, 1, 2].map((slot) => focus[(dayIndex + slot * 3) % focus.length][langIndex]);

  const quoteOfTheDay = selectedAuthors.map(([name], slot) => {
    const quote = format(quoteTemplates[lang][slot], {
      day: String(dayIndex + 1),
      focus: selectedFocus[slot],
    });
    return `${quote} - ${name}`;
  });

  const interestingKnowledge = [0, 1, 2, 3, 4].map((slot) => {
    const theme = factThemes[(dayIndex * 5 + slot) % factThemes.length][langIndex];
    const titlePrefix = {
      en: `Day ${dayIndex + 1}`,
      he: `יום ${dayIndex + 1}`,
      es: `Día ${dayIndex + 1}`,
      fr: `Jour ${dayIndex + 1}`,
      de: `Tag ${dayIndex + 1}`,
      tr: `Gün ${dayIndex + 1}`,
    }[lang];
    return {
      title: `${titlePrefix}: ${theme}`,
      text: format(factTemplates[lang], { topic: theme }),
    };
  });

  const whoWereThey = selectedAuthors.map(([name, bios]) => ({
    name,
    bio: bios[lang],
  }));

  return {
    quoteOfTheDay,
    interestingKnowledge,
    whoWereThey,
  };
}

const archive = {};

for (let day = 0; day < DAY_COUNT; day += 1) {
  const date = addDays(START_DATE, day);
  archive[date] = {
    date,
    languages: Object.fromEntries(languages.map((lang) => [lang, contentFor(lang, day)])),
  };
}

const output = `import type { DailyQuoteBundle } from '../types';\n\nexport const CONTENT_ARCHIVE: Record<string, DailyQuoteBundle> = ${JSON.stringify(archive, null, 2)};\n\nexport function getArchivedBundle(date: string): DailyQuoteBundle | null {\n  return CONTENT_ARCHIVE[date] ?? null;\n}\n\nexport function listArchiveDates(): string[] {\n  return Object.keys(CONTENT_ARCHIVE).sort((a, b) => b.localeCompare(a));\n}\n\nexport function listReachedArchiveDates(today: string): string[] {\n  return listArchiveDates().filter((date) => date <= today);\n}\n\nexport function hasArchivedContent(): boolean {\n  return Object.keys(CONTENT_ARCHIVE).length > 0;\n}\n`;

fs.writeFileSync('services/contentArchive.ts', output, 'utf8');
console.log(`Generated ${DAY_COUNT} days into services/contentArchive.ts`);
