import type { WhoPerson } from './types';

export enum Language {
    ENGLISH = 'en',
    HEBREW = 'he',
    SPANISH = 'es',
    FRENCH = 'fr',
    GERMAN = 'de',
    TURKISH = 'tr',
}

export const SUPPORTED_LANGUAGES = [
    { code: Language.ENGLISH, name: 'English', dir: 'ltr' },
    { code: Language.HEBREW, name: 'עברית', dir: 'rtl' },
    { code: Language.SPANISH, name: 'Español', dir: 'ltr' },
    { code: Language.FRENCH, name: 'Français', dir: 'ltr' },
    { code: Language.GERMAN, name: 'Deutsch', dir: 'ltr' },
    { code: Language.TURKISH, name: 'Türkçe', dir: 'ltr' },
];

export const FALLBACK_WHO_WERE_THEY: { [key in Language]: WhoPerson[] } = {
    [Language.ENGLISH]: [
        { name: 'Marie Curie', bio: 'A pioneer in radioactivity research, the first woman to win a Nobel Prize, and the only person to win the Nobel Prize in two different scientific fields.' },
        { name: 'Leonardo da Vinci', bio: 'An Italian polymath of the High Renaissance who is widely considered one of the most diversely talented individuals ever to have lived, known for works like the Mona Lisa and The Last Supper.' },
        { name: 'Isaac Newton', bio: 'An English mathematician, physicist, astronomer, and author who is widely recognised as one of the most influential scientists of all time and as a key figure in the scientific revolution.'}
    ],
    [Language.HEBREW]: [
        { name: 'מארי קירי', bio: 'חלוצה בחקר רדיואקטיביות, האישה הראשונה שזכתה בפרס נובל, והאדם היחיד שזכה בפרס נובל בשני תחומים מדעיים שונים.' },
        { name: 'לאונרדו דה וינצ\'י', bio: 'איש אשכולות איטלקי בתקופת הרנסאנס, הנחשב לאחד האנשים המוכשרים ביותר שחיו אי פעם, וידוע בזכות יצירות כמו המונה ליזה והסעודה האחרונה.' },
        { name: 'אייזק ניוטון', bio: 'מתמטיקאי, פיזיקאי, אסטרונום וסופר אנגלי, המוכר כאחד המדענים המשפיעים ביותר בכל הזמנים ודמות מפתח במהפכה המדעית.' }
    ],
    [Language.SPANISH]: [
        { name: 'Marie Curie', bio: 'Pionera en la investigación de la radiactividad, la primera mujer en ganar un Premio Nobel y la única persona en ganar el Premio Nobel en dos campos científicos diferentes.' },
        { name: 'Leonardo da Vinci', bio: 'Un polímata italiano del Alto Renacimiento, considerado uno de los individuos con más talentos diversos que jamás haya existido, conocido por obras como la Mona Lisa y La Última Cena.' },
        { name: 'Isaac Newton', bio: 'Un matemático, físico, astrónomo y autor inglés ampliamente reconocido como uno de los científicos más influyentes de todos los tiempos y una figura clave en la revolución científica.' }
    ],
    [Language.FRENCH]: [
        { name: 'Marie Curie', bio: 'Pionnière dans la recherche sur la radioactivité, première femme à remporter un prix Nobel et seule personne à avoir remporté le prix Nobel dans deux domaines scientifiques différents.' },
        { name: 'Léonard de Vinci', bio: 'Un polymathe italien de la Haute Renaissance, largement considéré comme l\'un des individus les plus talentueux et diversifiés ayant jamais vécu, connu pour des œuvres comme La Joconde et La Cène.' },
        { name: 'Isaac Newton', bio: 'Un mathématicien, physicien, astronome et auteur anglais, largement reconnu comme l\'un des scientifiques les plus influents de tous les temps et une figure clé de la révolution scientifique.' }
    ],
    [Language.GERMAN]: [
        { name: 'Marie Curie', bio: 'Eine Pionierin in der Radioaktivitätsforschung, die erste Frau, die einen Nobelpreis gewann, und die einzige Person, die den Nobelpreis in zwei verschiedenen wissenschaftlichen Bereichen gewann.' },
        { name: 'Leonardo da Vinci', bio: 'Ein italienischer Universalgelehrter der Hochrenaissance, der weithin als einer der vielseitig talentiertesten Menschen gilt, die je gelebt haben, bekannt für Werke wie die Mona Lisa und Das letzte Abendmahl.' },
        { name: 'Isaac Newton', bio: 'Ein englischer Mathematiker, Physiker, Astronom und Autor, der weithin als einer der einflussreichsten Wissenschaftler aller Zeiten und als Schlüsselfigur der wissenschaftlichen Revolution anerkannt ist.' }
    ],
    [Language.TURKISH]: [
        { name: 'Marie Curie', bio: 'Radyoaktivite araştırmalarında öncü, Nobel Ödülü kazanan ilk kadın ve Nobel Ödülü\'nü iki farklı bilimsel alanda kazanan tek kişi.' },
        { name: 'Leonardo da Vinci', bio: 'Mona Lisa ve Son Akşam Yemeği gibi eserleriyle tanınan, şimdiye kadar yaşamış en çeşitli yeteneklere sahip bireylerden biri olarak kabul edilen Yüksek Rönesans\'ın İtalyan polimatı.' },
        { name: 'Isaac Newton', bio: 'Tüm zamanların en etkili bilim adamlarından biri ve bilimsel devrimde kilit bir figür olarak tanınan İngiliz matematikçi, fizikçi, gökbilimci ve yazar.' }
    ],
};