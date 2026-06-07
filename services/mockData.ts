import type { DailyQuoteBundle } from '../types';

const today = new Date();
const y = today.getFullYear();
const m = String(today.getMonth() + 1).padStart(2, '0');
const d = String(today.getDate()).padStart(2, '0');
const todayStr = `${y}-${m}-${d}`;

const yesterday = new Date(today);
yesterday.setDate(today.getDate() - 1);
const y_y = yesterday.getFullYear();
const y_m = String(yesterday.getMonth() + 1).padStart(2, '0');
const y_d = String(yesterday.getDate()).padStart(2, '0');
const yesterdayStr = `${y_y}-${y_m}-${y_d}`;

const dayBeforeYesterday = new Date(today);
dayBeforeYesterday.setDate(today.getDate() - 2);
const dby_y = dayBeforeYesterday.getFullYear();
const dby_m = String(dayBeforeYesterday.getMonth() + 1).padStart(2, '0');
const dby_d = String(dayBeforeYesterday.getDate()).padStart(2, '0');
const dayBeforeYesterdayStr = `${dby_y}-${dby_m}-${dby_d}`;


export const MOCK_DATA: Record<string, DailyQuoteBundle> = {
  [todayStr]: {
    date: todayStr,
    languages: {
      "en": {
        "quoteOfTheDay": [
          "The only way to do great work is to love what you do. – Steve Jobs",
          "The greatest glory in living lies not in never falling, but in rising every time we fall. – Nelson Mandela",
          "Nothing in life is to be feared, it is only to be understood. Now is the time to understand more, so that we may fear less. – Marie Curie"
        ],
        "interestingKnowledge": [
          { "title": "The Eiffel Tower Can 'Grow' in the Summer", "text": "Constructed from wrought iron, the Eiffel Tower is a remarkable example of 19th-century engineering, and its very material gives it a surprising characteristic: it changes height with the seasons. This phenomenon, known as thermal expansion, causes the iron to expand when heated and contract when cooled. On a hot summer day in Paris, this expansion can result in the tower's peak rising by as much as 15 centimeters (about 6 inches) compared to its height in winter. Gustave Eiffel ingeniously accounted for this, incorporating expansion joints that allow the structure to flex without sustaining damage, ensuring its stability through extreme temperatures." },
          { "title": "Octopuses Have Three Hearts and Blue Blood", "text": "The octopus possesses a fascinating and highly efficient circulatory system unlike that of most vertebrates. Two of its three hearts are dedicated to pumping blood through the gills, where it picks up oxygen, while the third, larger heart circulates this oxygenated blood to the rest of the body. Their blood is blue due to the presence of hemocyanin, a copper-based protein, which is more efficient at transporting oxygen in cold, low-oxygen environments compared to the iron-based hemoglobin found in human blood. This unique biology is a key reason for their survival and adaptability in diverse marine ecosystems." },
          { "title": "A Single Bolt of Lightning Contains Enough Energy to Toast 100,000 Slices of Bread", "text": "A bolt of lightning is a spectacular display of nature's power, releasing an immense amount of energy in a fraction of a second. It can reach temperatures hotter than the surface of the sun and carry up to one billion volts of electricity. While the total energy—around 5 billion joules—is theoretically enough for mundane tasks like toasting bread on a massive scale, harnessing it is practically impossible. The energy is delivered in an incredibly short, unpredictable burst, making its capture and storage beyond the reach of current technology. This fact highlights the sheer scale of energy present in natural atmospheric phenomena." },
          { "title": "The Shortest War in History Lasted 38 Minutes", "text": "The Anglo-Zanzibar War holds the record for the shortest war in recorded history, fought on August 27, 1896. The conflict erupted after the pro-British Sultan Hamad bin Thuwaini died and his cousin, Khalid bin Barghash, seized power without British approval. The British Empire issued an ultimatum for him to abdicate, which was ignored. In response, the Royal Navy commenced a bombardment of the Sultan's palace at 9:02 AM. By 9:40 AM, the palace was destroyed and the Sultan's flag was lowered, marking a swift and decisive victory for the British and ending the conflict in under 40 minutes." },
          { "title": "Honey Never Spoils", "text": "The remarkable longevity of honey is a result of its unique chemical properties working in harmony. Its extremely low water content—typically around 17%—creates an environment too arid for bacteria and microorganisms to survive. Furthermore, honey is highly acidic, with a pH between 3 and 4.5, which is inhospitable to most microbes. But its most fascinating defense is an enzyme called glucose oxidase, added by bees. When honey is exposed to moisture, this enzyme produces small amounts of hydrogen peroxide, a powerful antimicrobial agent. This natural preservative system is so effective that archaeologists have discovered edible honey in ancient Egyptian tombs over 3,000 years old." }
        ],
        "whoWereThey": [
          { "name": "Steve Jobs", "bio": "Co-founder, CEO, and chairman of Apple Inc., a visionary entrepreneur who revolutionized the personal computer, animated film, music, and mobile phone industries with products like the Macintosh, iPhone, and Pixar." },
          { "name": "Nelson Mandela", "bio": "A South African anti-apartheid revolutionary and political leader who served as the first president of South Africa from 1994 to 1999. He is a global icon of peace and reconciliation." },
          { "name": "Marie Curie", "bio": "A Polish and naturalized-French physicist and chemist who conducted pioneering research on radioactivity. She was the first woman to win a Nobel Prize and the only person to win in two different scientific fields." }
        ]
      },
      "he": {
        "quoteOfTheDay": [
          "הדרך היחידה לעשות עבודה נהדרת היא לאהוב את מה שאתה עושה. – סטיב ג'ובס",
          "התהילה הגדולה ביותר בחיים אינה טמונה בכך שלעולם לא ניפול, אלא בכך שנקום בכל פעם שאנו נופלים. – נלסון מנדלה",
          "שום דבר בחיים אינו מפחיד, יש רק להבין אותו. עכשיו הזמן להבין יותר, כדי שנפחד פחות. – מארי קירי"
        ],
        "interestingKnowledge": [
          { "title": "מגדל אייפל יכול 'לצמוח' בקיץ", "text": "מגדל אייפל, שנבנה מברזל מחושל, מהווה דוגמה יוצאת דופן להנדסה של המאה ה-19, והחומר ממנו הוא עשוי מעניק לו תכונה מפתיעה: גובהו משתנה עם עונות השנה. תופעה זו, המכונה התפשטות תרמית, גורמת לברזל להתרחב בחום ולהתכווץ בקור. ביום קיץ חם בפריז, התרחבות זו יכולה לגרום לפסגת המגדל לעלות בכ-15 סנטימטרים (כ-6 אינץ') בהשוואה לגובהו בחורף. גוסטב אייפל התחשב בכך בצורה גאונית, ושילב מחברי התפשטות המאפשרים למבנה להתגמש מבלי להינזק, ובכך הבטיח את יציבותו בטמפרטורות קיצוניות." },
          { "title": "לתמנונים יש שלושה לבבות ודם כחול", "text": "לתמנון מערכת דם מרתקת ויעילה ביותר, השונה מזו של רוב בעלי החוליות. שניים משלושת לבבותיו מוקדשים להזרמת דם דרך הזימים, שם הוא קולט חמצן, בעוד הלב השלישי והגדול יותר מזרים את הדם המחומצן לשאר הגוף. דמם כחול בשל נוכחותו של המוציאנין, חלבון מבוסס נחושת, שיעיל יותר בהובלת חמצן בסביבות קרות ודלות חמצן בהשוואה להמוגלובין מבוסס הברזל המצוי בדם אנושי. ביולוגיה ייחודית זו היא סיבה מרכזית להישרדותם ויכולת הסתגלותם במערכות אקולוגיות ימיות מגוונות." },
          { "title": "ברק בודד מכיל מספיק אנרגיה כדי לקלות 100,000 פרוסות לחם", "text": "מכת ברק היא תצוגה מרהיבה של כוח הטבע, המשחררת כמות עצומה של אנרגיה בשבריר שנייה. היא יכולה להגיע לטמפרטורות גבוהות יותר מפני השמש ולשאת עד מיליארד וולט של חשמל. בעוד שהאנרגיה הכוללת - כ-5 מיליארד ג'אול - מספיקה תיאורטית למשימות כמו קליית לחם בקנה מידה עצום, ניצולה כמעט בלתי אפשרי. האנרגיה מועברת בפרץ קצר ובלתי צפוי, מה שהופך את לכידתה ואחסונה למעבר ליכולות הטכנולוגיה הנוכחית. עובדה זו מדגישה את סדר הגודל העצום של האנרגיה בתופעות אטמוספריות טבעיות." },
          { "title": "המלחמה הקצרה בהיסטוריה נמשכה 38 דקות", "text": "מלחמת אנגליה-זנזיבר מחזיקה בשיא המלחמה הקצרה ביותר בהיסטוריה המתועדת, והיא התרחשה ב-27 באוגוסט 1896. הסכסוך פרץ לאחר שהסולטן הפרו-בריטי חמד בן ת'וואיני מת ובן דודו, ח'אלד בן ברע'ש, תפס את השלטון ללא אישור בריטי. האימפריה הבריטית הציבה לו אולטימטום לפרוש, אך הוא התעלם ממנו. בתגובה, הצי המלכותי החל בהפצצת ארמון הסולטן בשעה 9:02 בבוקר. עד 9:40, הארמון נהרס ודגל הסולטן הורד, מה שסימן ניצחון מהיר והחלטי לבריטים וסיים את הסכסוך בפחות מ-40 דקות." },
          { "title": "דבש לעולם אינו מתקלקל", "text": "אורך החיים המדהים של הדבש נובע מהתכונות הכימיות הייחודיות שלו. תכולת המים הנמוכה ביותר שלו - בדרך כלל סביב 17% - יוצרת סביבה יבשה מדי עבור חיידקים ומיקרואורגניזמים. בנוסף, הדבש חומצי מאוד, עם pH בין 3 ל-4.5, מה שמקשה על רוב החיידקים לשרוד. אך ההגנה המרתקת ביותר שלו היא אנזים בשם גלוקוז אוקסידאז, שנוסף על ידי הדבורים. כאשר הדבש נחשף ללחות, אנזים זה מייצר כמויות קטנות של מי חמצן, חומר אנטי-מיקרוביאלי חזק. מערכת שימור טבעית זו כה יעילה, עד שארכיאולוגים גילו דבש אכיל בכדי חרס בקברים מצריים עתיקים בני למעלה מ-3,000 שנה." }
        ],
        "whoWereThey": [
          { "name": "סטיב ג'ובס", "bio": "מייסד שותף, מנכ\"ל ויו\"ר של אפל, יזם בעל חזון שחולל מהפכה בתעשיות המחשב האישי, סרטי האנימציה, המוזיקה והטלפונים הניידים עם מוצרים כמו מקינטוש, אייפון ופיקסאר." },
          { "name": "נלסון מנדלה", "bio": "מהפכן ופעיל נגד האפרטהייד בדרום אפריקה, שכיהן כנשיא הראשון של דרום אפריקה בין השנים 1994 ל-1999. הוא סמל עולמי לשלום ופיוס." },
          { "name": "מארי קירי", "bio": "פיזיקאית וכימאית פולנייה-צרפתייה שערכה מחקר חלוצי על רדיואקטיביות. היא הייתה האישה הראשונה שזכתה בפרס נובל והאדם היחיד שזכה בשני תחומים מדעיים שונים." }
        ]
      },
      "es": { 
        "quoteOfTheDay": [
          "La única forma de hacer un gran trabajo es amar lo que haces. – Steve Jobs",
          "La mayor gloria de vivir no reside en no caer nunca, sino в levantarnos cada vez que caemos. – Nelson Mandela",
          "No hay que temer nada en la vida, solo hay que entenderlo. Ahora es el momento de comprender más, para que temamos menos. – Marie Curie"
        ],
        "interestingKnowledge": [
          { "title": "La Torre Eiffel puede 'crecer' en verano", "text": "Construida con hierro forjado, la Torre Eiffel es un notable ejemplo de la ingeniería del siglo XIX, y su propio material le confiere una característica sorprendente: cambia de altura con las estaciones. Este fenómeno, conocido como expansión térmica, hace que el hierro se expanda al calentarse y se contraiga al enfriarse. En un caluroso día de verano en París, esta expansión puede hacer que la cima de la torre se eleve hasta 15 centímetros en comparación con su altura en invierno. Gustave Eiffel lo tuvo en cuenta ingeniosamente, incorporando juntas de expansión que permiten que la estructura se flexione sin sufrir daños, garantizando su estabilidad." },
          { "title": "Los pulpos tienen tres corazones y sangre azul", "text": "El pulpo posee un sistema circulatorio fascinante y muy eficiente. Dos de sus tres corazones se dedican a bombear sangre a través de las branquias, donde recoge oxígeno, mientras que el tercer corazón, más grande, la distribuye al resto del cuerpo. Su sangre es azul debido a la presencia de hemocianina, una proteína a base de cobre, que es más eficiente para transportar oxígeno en ambientes fríos y con poco oxígeno en comparación con la hemoglobina a base de hierro de la sangre humana. Esta biología única es clave para su supervivencia en diversos ecosistemas marinos." },
          { "title": "Un solo rayo contiene energía suficiente para tostar 100.000 rebanadas de pan", "text": "Un rayo es una espectacular demostración del poder de la naturaleza, que libera una inmensa cantidad de energía en una fracción de segundo. Puede alcanzar temperaturas más altas que la superficie del sol. Aunque la energía total —alrededor de 5 mil millones de julios— es teóricamente suficiente para tareas mundanas a gran escala, aprovecharla es prácticamente imposible. La energía se libera en una ráfaga increíblemente corta e impredecible, lo que hace que su captura y almacenamiento estén fuera del alcance de la tecnología actual." },
          { "title": "La guerra más corta de la historia duró 38 minutos", "text": "La Guerra Anglo-Zanzibariana ostenta el récord de la guerra más corta de la historia, librada el 27 de agosto de 1896. El conflicto estalló después de que el sultán pro-británico muriera y su primo, Khalid bin Barghash, tomara el poder sin la aprobación británica. El Imperio Británico le dio un ultimátum para que abdicara, que fue ignorado. En respuesta, la Royal Navy comenzó un bombardeo del palacio del Sultán a las 9:02 AM. A las 9:40 AM, el palacio fue destruido, marcando una victoria rápida y decisiva para los británicos." },
          { "title": "La miel nunca se estropea", "text": "La notable longevidad de la miel es resultado de sus propiedades químicas únicas. Su contenido de agua extremadamente bajo —alrededor del 17%— crea un ambiente demasiado árido para que las bacterias sobrevivan. Además, la miel es muy ácida, con un pH entre 3 y 4.5, que es inhóspito para la mayoría de los microbios. Pero su defensa más fascinante es una enzima llamada glucosa oxidasa, añadida por las abejas. Cuando la miel se expone a la humedad, esta enzima produce pequeñas cantidades de peróxido de hidrógeno, un potente agente antimicrobiano. Este sistema es tan eficaz que se ha encontrado miel comestible en tumbas egipcias de más de 3.000 años." }
        ],
        "whoWereThey": [
          { "name": "Steve Jobs", "bio": "Cofundador y presidente ejecutivo de Apple Inc., un empresario visionario que revolucionó las industrias de la computadora personal, el cine de animación, la música y la telefonía móvil con productos como el Macintosh, el iPhone y Pixar." },
          { "name": "Nelson Mandela", "bio": "Revolucionario anti-apartheid y líder político sudafricano que fue el primer presidente de Sudáfrica de 1994 a 1999. Es un ícono mundial de la paz y la reconciliación." },
          { "name": "Marie Curie", "bio": "Física y química polaca, naturalizada francesa, que realizó investigaciones pioneras sobre la radiactividad. Fue la primera mujer en ganar un Premio Nobel y la única persona en ganarlo en dos campos científicos diferentes." }
        ]
      },
      "fr": {
        "quoteOfTheDay": [
          "La seule façon de faire du bon travail est d'aimer ce que vous faites. – Steve Jobs",
          "La plus grande gloire n'est pas de ne jamais tomber, mais de se relever à chaque chute. – Nelson Mandela",
          "Dans la vie, rien n'est à craindre, tout est à comprendre. Il est maintenant temps de comprendre davantage, afin de craindre moins. – Marie Curie"
        ],
        "interestingKnowledge": [
          { "title": "La Tour Eiffel peut 'grandir' en été", "text": "Construite en fer puddlé, la Tour Eiffel est un exemple remarquable de l'ingénierie du XIXe siècle, et son matériau même lui confère une caractéristique surprenante : elle change de hauteur avec les saisons. Ce phénomène, connu sous le nom de dilatation thermique, fait que le fer se dilate lorsqu'il est chauffé. Lors d'une chaude journée d'été à Paris, cette dilatation peut entraîner une élévation du sommet de la tour jusqu'à 15 centimètres par rapport à sa hauteur en hiver. Gustave Eiffel a ingénieusement prévu cela, en incorporant des joints de dilatation qui permettent à la structure de fléchir sans subir de dommages." },
          { "title": "Les pieuvres ont trois cœurs et du sang bleu", "text": "La pieuvre possède un système circulatoire fascinant et très efficace. Deux de ses trois cœurs sont dédiés au pompage du sang à travers les branchies, où il capte l'oxygène, tandis que le troisième cœur, plus grand, le distribue au reste du corps. Leur sang est bleu en raison de la présence d'hémocyanine, une protéine à base de cuivre, plus efficace pour transporter l'oxygène dans des environnements froids et peu oxygénés que l'hémoglobine à base de fer. Cette biologie unique est une clé de leur survie dans divers écosystèmes marins." },
          { "title": "Un seul éclair contient assez d'énergie pour griller 100 000 tranches de pain", "text": "Un éclair est une démonstration spectaculaire de la puissance de la nature, libérant une immense quantité d'énergie en une fraction de seconde. Il peut atteindre des températures plus élevées que la surface du soleil. Bien que l'énergie totale — environ 5 milliards de joules — soit théoriquement suffisante pour des tâches banales à grande échelle, son exploitation est pratiquement impossible. L'énergie est libérée dans une rafale incroyablement courte et imprévisible, ce qui rend sa capture et son stockage hors de portée de la technologie actuelle." },
          { "title": "La guerre la plus courte de l'histoire a duré 38 minutes", "text": "La guerre anglo-zanzibarienne détient le record de la guerre la plus courte de l'histoire, menée le 27 août 1896. Le conflit a éclaté après la mort du sultan pro-britannique et la prise de pouvoir par son cousin, Khalid bin Barghash, sans l'approbation britannique. L'Empire britannique lui a adressé un ultimatum pour qu'il abdique, qui a été ignoré. En réponse, la Royal Navy a bombardé le palais du sultan à 9h02. À 9h40, le palais était détruit, marquant une victoire rapide et décisive pour les Britanniques." },
          { "title": "Le miel ne se gâte jamais", "text": "La longévité remarquable du miel résulte de ses propriétés chimiques uniques. Sa très faible teneur en eau — environ 17 % — crée un environnement trop aride pour que les bactéries survivent. De plus, le miel est très acide, avec un pH compris entre 3 et 4,5, inhospitalier pour la plupart des microbes. Mais sa défense la plus fascinante est une enzyme appelée glucose oxydase, ajoutée par les abeilles. Lorsque le miel est exposé à l'humidité, cette enzyme produit de petites quantités de peroxyde d'hydrogène, un puissant agent antimicrobien. Ce système est si efficace que l'on a retrouvé du miel comestible dans des tombes égyptiennes vieilles de plus de 3 000 ans." }
        ],
        "whoWereThey": [
          { "name": "Steve Jobs", "bio": "Cofondateur et PDG d'Apple Inc., un entrepreneur visionnaire qui a révolutionné les industries de l'ordinateur personnel, du film d'animation, de la musique et de la téléphonie mobile avec des produits comme le Macintosh, l'iPhone et Pixar." },
          { "name": "Nelson Mandela", "bio": "Révolutionnaire anti-apartheid et dirigeant politique sud-africain qui fut le premier président de l'Afrique du Sud de 1994 à 1999. Il est une icône mondiale de la paix et de la réconciliation." },
          { "name": "Marie Curie", "bio": "Physicienne et chimiste polonaise, naturalisée française, qui a mené des recherches pionnières sur la radioactivité. Elle fut la première femme à recevoir un prix Nobel et la seule personne à en remporter un dans deux domaines scientifiques différents." }
        ]
      },
      "de": {
        "quoteOfTheDay": [
          "Der einzige Weg, großartige Arbeit zu leisten, ist, zu lieben, was man tut. – Steve Jobs",
          "Der größte Ruhm im Leben liegt nicht darin, niemals zu fallen, sondern jedes Mal wieder aufzustehen, wenn wir fallen. – Nelson Mandela",
          "Nichts im Leben ist zu fürchten, es ist nur zu verstehen. Jetzt ist die Zeit, mehr zu verstehen, damit wir weniger fürchten. – Marie Curie"
        ],
        "interestingKnowledge": [
          { "title": "Der Eiffelturm kann im Sommer 'wachsen'", "text": "Der aus Schmiedeeisen erbaute Eiffelturm ist ein bemerkenswertes Beispiel für die Ingenieurskunst des 19. Jahrhunderts, und sein Material verleiht ihm eine überraschende Eigenschaft: Er verändert seine Höhe mit den Jahreszeiten. Dieses als Wärmeausdehnung bekannte Phänomen bewirkt, dass sich das Eisen bei Erwärmung ausdehnt. An einem heißen Sommertag in Paris kann diese Ausdehnung dazu führen, dass die Spitze des Turms im Vergleich zu seiner Höhe im Winter um bis zu 15 Zentimeter ansteigt. Gustave Eiffel hat dies genial berücksichtigt und Dehnungsfugen eingebaut, die es der Struktur ermöglichen, sich zu biegen, ohne Schaden zu nehmen." },
          { "title": "Oktopusse haben drei Herzen und blaues Blut", "text": "Der Oktopus besitzt ein faszinierendes und hocheffizientes Kreislaufsystem. Zwei seiner drei Herzen pumpen Blut durch die Kiemen, wo es Sauerstoff aufnimmt, während das dritte, größere Herz es im Rest des Körpers verteilt. Ihr Blut ist aufgrund des Vorhandenseins von Hämocyanin, einem kupferbasierten Protein, blau, das Sauerstoff in kalten, sauerstoffarmen Umgebungen effizienter transportiert als das eisenbasierte Hämoglobin im menschlichen Blut. Diese einzigartige Biologie ist ein Schlüssel für ihr Überleben in verschiedenen marinen Ökosystemen." },
          { "title": "Ein einzelner Blitz enthält genug Energie, um 100.000 Scheiben Brot zu toasten", "text": "Ein Blitz ist eine spektakuläre Demonstration der Kraft der Natur, die in einem Bruchteil einer Sekunde eine immense Energiemenge freisetzt. Er kann Temperaturen erreichen, die heißer sind als die Oberfläche der Sonne. Obwohl die Gesamtenergie – etwa 5 Milliarden Joule – theoretisch für alltägliche Aufgaben im großen Stil ausreicht, ist ihre Nutzung praktisch unmöglich. Die Energie wird in einem unglaublich kurzen, unvorhersehbaren Stoß abgegeben, was ihre Erfassung und Speicherung für die aktuelle Technologie unerreichbar macht." },
          { "title": "Der kürzeste Krieg der Geschichte dauerte 38 Minuten", "text": "Der Anglo-Sansibar-Krieg hält den Rekord für den kürzesten Krieg der Geschichte und wurde am 27. August 1896 geführt. Der Konflikt brach aus, nachdem der pro-britische Sultan gestorben war und sein Cousin, Khalid bin Barghash, ohne britische Zustimmung die Macht übernommen hatte. Das Britische Empire stellte ihm ein Ultimatum zur Abdankung, das ignoriert wurde. Daraufhin begann die Royal Navy um 9:02 Uhr mit dem Bombardement des Sultanspalastes. Um 9:40 Uhr war der Palast zerstört, was einen schnellen und entscheidenden Sieg für die Briten bedeutete." },
          { "title": "Honig verdirbt nie", "text": "Die bemerkenswerte Langlebigkeit von Honig ist das Ergebnis seiner einzigartigen chemischen Eigenschaften. Sein extrem niedriger Wassergehalt – etwa 17 % – schafft eine zu trockene Umgebung für das Überleben von Bakterien. Zudem ist Honig stark sauer, mit einem pH-Wert zwischen 3 und 4,5, was für die meisten Mikroben unwirtlich ist. Seine faszinierendste Abwehr ist jedoch ein Enzym namens Glukoseoxidase, das von Bienen hinzugefügt wird. Wenn Honig Feuchtigkeit ausgesetzt wird, produziert dieses Enzym geringe Mengen Wasserstoffperoxid, ein starkes antimikrobielles Mittel. Dieses System ist so wirksam, dass essbarer Honig in über 3.000 Jahre alten ägyptischen Gräbern gefunden wurde." }
        ],
        "whoWereThey": [
          { "name": "Steve Jobs", "bio": "Mitbegründer, CEO und Vorsitzender von Apple Inc., ein visionärer Unternehmer, der die Branchen Personal Computer, Animationsfilm, Musik und Mobiltelefone mit Produkten wie dem Macintosh, iPhone und Pixar revolutionierte." },
          { "name": "Nelson Mandela", "bio": "Ein südafrikanischer Anti-Apartheid-Revolutionär und politischer Führer, der von 1994 bis 1999 als erster Präsident Südafrikas diente. Er ist eine globale Ikone des Friedens und der Versöhnung." },
          { "name": "Marie Curie", "bio": "Eine polnische und eingebürgerte französische Physikerin und Chemikerin, die wegweisende Forschungen zur Radioaktivität durchführte. Sie war die erste Frau, die einen Nobelpreis gewann, und die einzige Person, die ihn in zwei verschiedenen wissenschaftlichen Bereichen gewann." }
        ]
      },
      "tr": {
        "quoteOfTheDay": [
          "Harika bir iş yapmanın tek yolu, yaptığınız işi sevmektir. – Steve Jobs",
          "Yaşamdaki en büyük zafer asla düşmemek değil, her düştüğümüzde yeniden ayağa kalkmaktır. – Nelson Mandela",
          "Hayatta hiçbir şeyden korkulmamalı, sadece anlaşılmalıdır. Şimdi daha çok anlama zamanı, böylece daha az korkabiliriz. – Marie Curie"
        ],
        "interestingKnowledge": [
          { "title": "Eyfel Kulesi Yazın 'Büyüyebilir'", "text": "Dövme demirden inşa edilen Eyfel Kulesi, 19. yüzyıl mühendisliğinin dikkat çekici bir örneğidir ve malzemesi ona şaşırtıcı bir özellik kazandırır: mevsimlere göre yüksekliği değişir. Termal genleşme olarak bilinen bu olgu, demirin ısındığında genleşmesine neden olur. Paris'te sıcak bir yaz gününde, bu genleşme kulenin zirvesinin kışa göre 15 santimetreye kadar yükselmesine neden olabilir. Gustave Eiffel bunu ustaca hesaba katarak, yapının hasar görmeden esnemesine olanak tanıyan genleşme derzleri eklemiş ve aşırı sıcaklıklarda stabilitesini sağlamıştır." },
          { "title": "Ahtapotların Üç Kalbi ve Mavi Kanı Vardır", "text": "Ahtapot, büyüleyici ve oldukça verimli bir dolaşım sistemine sahiptir. Üç kalbinden ikisi, oksijen aldığı solungaçlara kan pompalamaya adanmıştır, üçüncüsü ve daha büyük olanı ise vücudun geri kalanına dağıtır. Kanları, insan kanındaki demir bazlı hemoglobine kıyasla soğuk, düşük oksijenli ortamlarda oksijeni daha verimli taşıyan bakır bazlı bir protein olan hemosiyanin varlığı nedeniyle mavidir. Bu eşsiz biyoloji, çeşitli deniz ekosistemlerinde hayatta kalmalarının anahtarıdır." },
          { "title": "Tek bir şimşek 100.000 dilim ekmeği kızartacak kadar enerji içerir", "text": "Bir şimşek, doğanın gücünün muhteşem bir göstergesidir ve saniyenin bir kesrinde muazzam miktarda enerji açığa çıkarır. Güneş yüzeyinden daha yüksek sıcaklıklara ulaşabilir. Toplam enerji – yaklaşık 5 milyar joule – teorik olarak büyük ölçekli sıradan işler için yeterli olsa da, bundan faydalanmak pratik olarak imkansızdır. Enerji, inanılmaz derecede kısa ve öngörülemez bir patlama ile salınır, bu da yakalanmasını ve depolanmasını mevcut teknolojinin ötesine taşır." },
          { "title": "Tarihteki en kısa savaş 38 dakika sürdü", "text": "Anglo-Zanzibar Savaşı, 27 Ağustos 1896'da yapılan tarihin en kısa savaşı rekorunu elinde tutuyor. Çatışma, İngiliz yanlısı Sultan'ın ölümü ve kuzeni Halid bin Bargaş'ın İngiliz onayı olmadan iktidarı ele geçirmesinden sonra patlak verdi. Britanya İmparatorluğu ona tahttan çekilmesi için bir ültimatom verdi, ancak bu göz ardı edildi. Buna karşılık, Kraliyet Donanması saat 9:02'de Sultan'ın sarayını bombalamaya başladı. Saat 9:40'ta saray yıkıldı ve bu, İngilizler için hızlı ve kesin bir zafer anlamına geliyordu." },
          { "title": "Bal Asla Bozulmaz", "text": "Balın dikkat çekici uzun ömürlülüğü, benzersiz kimyasal özelliklerinin bir sonucudur. Aşırı düşük su içeriği – yaklaşık %17 – bakterilerin hayatta kalması için çok kurak bir ortam yaratır. Ayrıca, bal 3 ile 4,5 arasında bir pH değeriyle oldukça asidiktir, bu da çoğu mikrop için elverişsizdir. Ancak en büyüleyici savunması, arılar tarafından eklenen glukoz oksidaz adlı bir enzimdir. Bal neme maruz kaldığında, bu enzim küçük miktarlarda güçlü bir antimikrobiyal ajan olan hidrojen peroksit üretir. Bu sistem o kadar etkilidir ki, 3000 yıldan eski Mısır mezarlarında yenilebilir bal bulunmuştur." }
        ],
        "whoWereThey": [
          { "name": "Steve Jobs", "bio": "Apple Inc.'in kurucu ortağı, CEO'su ve başkanı, Macintosh, iPhone ve Pixar gibi ürünlerle kişisel bilgisayar, animasyon filmi, müzik ve cep telefonu endüstrilerinde devrim yaratan vizyoner bir girişimci." },
          { "name": "Nelson Mandela", "bio": "1994'ten 1999'a kadar Güney Afrika'nın ilk cumhurbaşkanı olarak görev yapan Güney Afrikalı bir anti-apartheid devrimcisi ve siyasi lider. O, küresel bir barış ve uzlaşma ikonudur." },
          { "name": "Marie Curie", "bio": "Radyoaktivite üzerine öncü araştırmalar yürüten Polonyalı ve Fransız vatandaşı fizikçi ve kimyager. Nobel Ödülü kazanan ilk kadın ve iki farklı bilimsel alanda kazanan tek kişidir." }
        ]
      }
    }
  },
  [yesterdayStr]: {
    date: yesterdayStr,
     languages: {
       "en": {
         "quoteOfTheDay": [
           "The future belongs to those who believe in the beauty of their dreams. – Eleanor Roosevelt",
           "If you can't explain it simply, you don't understand it well enough. – Albert Einstein",
           "Try to be a rainbow in someone's cloud. – Maya Angelou"
         ],
         "interestingKnowledge": [
           { "title": "A Day on Venus is Longer than its Year", "text": "Venus presents a fascinating paradox in our solar system: its rotational period is longer than its orbital period. It takes a sluggish 243 Earth days for Venus to complete a single rotation on its axis, but only 225 Earth days to orbit the Sun. This means a Venusian day is longer than a Venusian year. This peculiar characteristic is attributed to its retrograde (backward) rotation and its incredibly thick, fast-moving atmosphere, which may create a tidal drag on the planet's surface, slowing its spin over billions of years. It's a unique planetary dynamic that challenges our typical understanding of days and years." },
           { "title": "There Are More Trees on Earth Than Stars in the Milky Way", "text": "While the night sky appears filled with countless stars, the number of trees on our own planet is estimated to be vastly greater. A study published in the journal 'Nature' estimated there are over 3 trillion trees on Earth. In contrast, astronomers estimate that the Milky Way galaxy contains between 100 to 400 billion stars. This surprising fact underscores the immense scale of Earth's biosphere and the critical role forests play in sustaining life. It serves as a powerful reminder of the precious and vast natural resources our planet holds, often hidden in plain sight." },
           { "title": "The 'Sound' of a Black Hole", "text": "Although space is a vacuum where sound as we know it cannot travel, black holes are not silent. In 2003, NASA's Chandra X-ray Observatory detected pressure waves rippling through the hot gas in the Perseus galaxy cluster, generated by a supermassive black hole at its center. These waves are essentially sound waves with a very low frequency. Scientists were able to translate these ripples into a note, albeit one that is 57 octaves below middle C, making it far too deep for the human ear to perceive. This discovery revealed that on a cosmic scale, even the most mysterious objects can create a form of 'music'." },
           { "title": "Wombats Have Cube-Shaped Poop", "text": "The wombat, a marsupial native to Australia, is unique in the animal kingdom for producing cube-shaped feces. This bizarre trait is a result of the varying elasticity of its intestinal walls. As digested food moves through the final sections of the intestine, the alternating soft and rigid regions sculpt the waste into distinct cubes. This shape is not just a quirk; it serves a practical purpose. Wombats use their droppings to mark territory, and the flat sides of the cubes prevent them from rolling away from prominent locations like logs or rocks, ensuring their scent markers stay put." },
           { "title": "Sharks Are Older Than Trees", "text": "Sharks are true survivors of the ancient world, having navigated Earth's oceans for approximately 450 million years. This makes them one of the oldest animal groups still in existence, predating even the dinosaurs. In contrast, the earliest known trees, such as the fern-like Archaeopteris, only appeared around 350 million years ago. This means that sharks had already been ruling the seas for a staggering 100 million years before the first forests began to spread across the land. Their long evolutionary history is a testament to their remarkable adaptability and resilience through multiple mass extinction events." }
         ],
         "whoWereThey": [
           { "name": "Eleanor Roosevelt", "bio": "An American political figure, diplomat, and activist. She served as the First Lady of the United States from 1933 to 1945 and was a tireless advocate for human rights, later chairing the UN's Human Rights Commission." },
           { "name": "Albert Einstein", "bio": "A German-born theoretical physicist who developed the theory of relativity, one of the two pillars of modern physics. His work is also known for its influence on the philosophy of science. E = mc² is his most famous equation." },
           { "name": "Maya Angelou", "bio": "An American poet, memoirist, and civil rights activist. She published seven autobiographies, three books of essays, several books of poetry, and is credited with a list of plays, movies, and television shows spanning over 50 years." }
         ]
       },
       "he": {
         "quoteOfTheDay": [
           "העתיד שייך לאלה המאמינים ביופיים של חלומותיהם. – אלינור רוזוולט",
           "אם אינך יכול להסביר זאת בפשטות, אינך מבין זאת מספיק טוב. – אלברט איינשטיין",
           "נסה להיות קשת בענן של מישהו. – מאיה אנג'לו"
         ],
         "interestingKnowledge": [
           { "title": "יום על נוגה ארוך יותר משנה על נוגה", "text": "נוגה מציג פרדוקס מרתק במערכת השמש שלנו: תקופת הסיבוב העצמי שלו ארוכה יותר מתקופת ההקפה שלו. לנוגה לוקח 243 ימי ארץ איטיים להשלים סיבוב אחד על צירו, אך רק 225 ימי ארץ להקיף את השמש. משמעות הדבר היא שיממה על נוגה ארוכה יותר משנה על נוגה. מאפיין מוזר זה מיוחס לסיבוב הרטרוגרדי (הפוך) שלו ולאטמוספירה העבה והמהירה שלו, שעשויה ליצור גרירת גאות על פני הכוכב, ולהאט את סיבובו במשך מיליארדי שנים. זוהי דינמיקה פלנטרית ייחודית המאתגרת את הבנתנו הטיפוסית של ימים ושנים." },
           { "title": "יש יותר עצים על כדור הארץ מאשר כוכבים בשביל החלב", "text": "בעוד ששמי הלילה נראים מלאים בכוכבים אינספור, מספר העצים על כדור הארץ מוערך כגדול בהרבה. מחקר שפורסם בכתב העת 'Nature' העריך כי ישנם למעלה מ-3 טריליון עצים על פני כדור הארץ. לשם השוואה, אסטרונומים מעריכים כי גלקסיית שביל החלב מכילה בין 100 ל-400 מיליארד כוכבים. עובדה מפתיעה זו מדגישה את קנה המידה העצום של הביוספרה של כדור הארץ ואת התפקיד הקריטי שיערות ממלאים בקיום החיים. היא משמשת תזכורת רבת עוצמה למשאבי הטבע היקרים והעצומים שכוכב הלכת שלנו מחזיק, שלעתים קרובות חבויים לעין כל." },
           { "title": "ה'צליל' של חור שחור", "text": "אף שהחלל הוא ואקום שבו קול כפי שאנו מכירים אותו אינו יכול להתפשט, חורים שחורים אינם שקטים. בשנת 2003, מצפה הכוכבים Chandra של נאס\"א זיהה גלי לחץ המתפשטים בגז החם בצביר הגלקסיות פרסאוס, הנוצרים על ידי חור שחור על-מסיבי במרכזו. גלים אלה הם למעשה גלי קול בתדירות נמוכה מאוד. מדענים הצליחו לתרגם את האדוות הללו לתו, אם כי הוא נמוך ב-57 אוקטבות מהדו האמצעי, מה שהופך אותו לעמוק מדי מכדי שאוזן אנושית תוכל לקלוט. תגלית זו חשפה כי בקנה מידה קוסמי, אפילו האובייקטים המסתוריים ביותר יכולים ליצור סוג של 'מוזיקה'." },
           { "title": "לוומבטים יש צואה בצורת קובייה", "text": "הוומבט, חיית כיס ילידת אוסטרליה, הוא ייחודי בממלכת החי בכך שהוא מייצר צואה בצורת קובייה. תכונה מוזרה זו היא תוצאה של הגמישות המשתנה של דפנות המעיים שלו. כאשר מזון מעוכל נע דרך החלקים הסופיים של המעי, האזורים הרכים והקשיחים המתחלפים מעצבים את הפסולת לקוביות מובחנות. לצורה זו יש מטרה מעשית; וומבטים משתמשים בגללים שלהם לסימון טריטוריה, והצדדים השטוחים של הקוביות מונעים מהם להתגלגל ממקומות בולטים כמו בולי עץ או סלעים, ובכך מבטיחים שסימני הריח שלהם יישארו במקומם." },
           { "title": "כרישים עתיקים יותר מעצים", "text": "כרישים הם שורדים אמיתיים של העולם העתיק, לאחר שניווטו באוקיינוסים של כדור הארץ במשך כ-450 מיליון שנה. זה הופך אותם לאחת מקבוצות בעלי החיים העתיקות ביותר שעדיין קיימות, והם קדמו אפילו לדינוזאורים. לשם השוואה, העצים המוקדמים ביותר הידועים, כמו הארכאופטריס דמוי השרך, הופיעו רק לפני כ-350 מיליון שנה. משמעות הדבר היא שכרישים כבר שלטו בימים במשך 100 מיליון שנים מדהימות לפני שהיערות הראשונים החלו להתפשט על פני היבשה. ההיסטוריה האבולוציונית הארוכה שלהם היא עדות ליכולת ההסתגלות והחוסן המדהימים שלהם דרך אירועי הכחדה המוניים מרובים." }
         ],
         "whoWereThey": [
           { "name": "אלינור רוזוולט", "bio": "דמות פוליטית, דיפלומטית ופעילה אמריקאית. היא כיהנה כגברת הראשונה של ארצות הברית בין השנים 1933 ל-1945 והייתה תומכת בלתי נלאית בזכויות אדם, ובהמשך עמדה בראש ועדת זכויות האדם של האו\"ם." },
           { "name": "אלברט איינשטיין", "bio": "פיזיקאי תיאורטי יליד גרמניה שפיתח את תורת היחסות, אחד משני עמודי התווך של הפיזיקה המודרנית. עבודתו ידועה גם בהשפעתה על הפילוסופיה של המדע. E = mc² היא המשוואה המפורסמת ביותר שלו." },
           { "name": "מאיה אנג'לו", "bio": "משוררת, סופרת זיכרונות ופעילת זכויות אזרח אמריקאית. היא פרסמה שבע אוטוביוגרפיות, שלושה ספרי מאמרים, מספר ספרי שירה, וזוקפים לזכותה רשימה של מחזות, סרטים ותוכניות טלוויזיה המשתרעים על פני 50 שנה." }
         ]
       }
     }
  },
  [dayBeforeYesterdayStr]: {
    date: dayBeforeYesterdayStr,
    languages: {
        "en": {
            "quoteOfTheDay": [
              "Simplicity is the ultimate sophistication. – Leonardo da Vinci",
              "If I have seen further it is by standing on the shoulders of Giants. – Isaac Newton",
              "There is no charm equal to tenderness of heart. – Jane Austen"
            ],
            "interestingKnowledge": [
              { "title": "The Hawaiian Alphabet Has Only 12 Letters", "text": "The traditional Hawaiian alphabet, or 'ka pīʻāpā Hawaiʻi,' is one of the shortest alphabets in the world, consisting of just five vowels (A, E, I, O, U) and seven consonants (H, K, L, M, N, P, W). Every word in Hawaiian ends in a vowel, and syllables are simple, which gives the language its melodic and flowing sound. This streamlined system makes the language phonetically consistent and relatively easy to pronounce once the basic rules are understood, reflecting a culture that values clarity and harmony in communication." },
              { "title": "A Group of Flamingos is Called a 'Flamboyance'", "text": "This colorful and fitting term perfectly captures the essence of these vibrant, social birds, often seen wading together in large, dazzling groups. The English language is rich with such poetic collective nouns for animals, each often reflecting a unique characteristic of the species. For instance, you might encounter a 'crash' of rhinoceroses, a 'parliament' of wise-looking owls, a 'murder' of ominous crows, or an 'ostentation' of peacocks. These terms add a layer of creativity and history to the way we describe the natural world." },
              { "title": "The World's Largest Desert is Antarctica", "text": "While we typically associate deserts with scorching heat and vast sand dunes, the technical definition of a desert is a region that receives very little precipitation. By this measure, the Antarctic Polar Desert is the largest desert on Earth, covering an area of around 14 million square kilometers. It is also the coldest, driest, and highest desert in the world. The frigid air is unable to hold much moisture, resulting in annual precipitation of less than 200mm, which falls almost exclusively as snow, challenging our conventional image of a desert landscape." },
              { "title": "Cleopatra Was Not Egyptian", "text": "Despite being one of the most famous rulers of Egypt, Cleopatra VII was not of Egyptian descent. She was the last active ruler of the Ptolemaic Kingdom, a dynasty founded by Ptolemy I Soter, a Macedonian Greek general who was a companion of Alexander the Great. While her family lineage was Greek, Cleopatra fully embraced Egyptian culture and was a rare exception in her dynasty for being the first to learn the Egyptian language. This cultural immersion, combined with her political acumen, made her a formidable and beloved leader among her people, distinguishing her from her predecessors." },
              { "title": "The Canary Islands Are Named After Dogs, Not Birds", "text": "The name of this popular Spanish archipelago has a surprising origin that has nothing to do with the cheerful yellow songbird. The islands' name is derived from the Latin phrase 'Canariae Insulae,' which translates to 'Islands of the Dogs.' This name was reportedly given by early explorers who encountered a species of large, fierce dogs ('canes' in Latin) on the islands. It was only later that the small finch, native to the region, was named the 'canary bird' after its island home, leading to the common misconception." }
            ],
            "whoWereThey": [
              { "name": "Leonardo da Vinci", "bio": "An Italian polymath of the High Renaissance, active as a painter, draughtsman, engineer, scientist, theorist, sculptor, and architect. His genius epitomized the Renaissance humanist ideal." },
              { "name": "Isaac Newton", "bio": "An English mathematician, physicist, and astronomer, widely recognized as one of the most influential scientists of all time. His book 'Principia Mathematica' formulated the laws of motion and universal gravitation." },
              { "name": "Jane Austen", "bio": "An English novelist known primarily for her six major novels, which interpret, critique, and comment upon the British landed gentry at the end of the 18th century. 'Pride and Prejudice' is her most famous work." }
            ]
        },
        "he": {
            "quoteOfTheDay": [
              "פשטות היא התחכום האולטימטיבי. – לאונרדו דה וינצ'י",
              "אם ראיתי רחוק יותר, זה מפני שעמדתי על כתפיהם של ענקים. – אייזק ניוטון",
              "אין קסם השווה לעדינות הלב. – ג'יין אוסטן"
            ],
            "interestingKnowledge": [
              { "title": "באלפבית ההוואי יש רק 12 אותיות", "text": "האלפבית ההוואי המסורתי, 'ka pīʻāpā Hawaiʻi', הוא אחד האלפביתים הקצרים בעולם, ומורכב מחמש תנועות (A, E, I, O, U) ושבעה עיצורים (H, K, L, M, N, P, W) בלבד. כל מילה בהוואית מסתיימת בתנועה, וההברות פשוטות, מה שמעניק לשפה את צלילה המלודי והזורם. מערכת יעילה זו הופכת את השפה לעקבית מבחינה פונטית וקלה יחסית להגייה לאחר לימוד הכללים הבסיסיים, ומשקפת תרבות המעריכה בהירות והרמוניה בתקשורת." },
              { "title": "קבוצת פלמינגו נקראת 'Flamboyance'", "text": "מונח צבעוני והולם זה לוכד בצורה מושלמת את מהותן של ציפורים תוססות וחברתיות אלו, שלעתים קרובות נראות צועדות יחד בקבוצות גדולות ומסנוורות. השפה האנגלית עשירה בשמות קיבוציים פואטיים כאלה לבעלי חיים, כאשר כל אחד מהם משקף לעתים קרובות מאפיין ייחודי של המין. לדוגמה, ניתן להיתקל ב'התרסקות' של קרנפים, 'פרלמנט' של ינשופים בעלי מראה חכם, 'רצח' של עורבים מבשרי רעות, או 'התפארות' של טווסים. מונחים אלה מוסיפים רובד של יצירתיות והיסטוריה לאופן שבו אנו מתארים את עולם הטבע." },
              { "title": "המדבר הגדול בעולם הוא אנטארקטיקה", "text": "בעוד שאנו נוטים לקשר מדבריות לחום לוהט ודיונות חול עצומות, ההגדרה הטכנית של מדבר היא אזור שמקבל מעט מאוד משקעים. על פי מדד זה, המדבר הקוטבי האנטארקטי הוא המדבר הגדול ביותר על פני כדור הארץ, המשתרע על שטח של כ-14 מיליון קילומטרים רבועים. הוא גם המדבר הקר, היבש והגבוה ביותר בעולם. האוויר הקפוא אינו מסוגל להחזיק לחות רבה, וכתוצאה מכך כמות המשקעים השנתית נמוכה מ-200 מ\"מ, היורדת כמעט כולה כשלג, ובכך מאתגרת את הדימוי המקובל שלנו של נוף מדברי." },
              { "title": "קליאופטרה לא הייתה מצרית", "text": "למרות היותה אחת השליטות המפורסמות ביותר של מצרים, קליאופטרה השביעית לא הייתה ממוצא מצרי. היא הייתה השליטה הפעילה האחרונה של הממלכה התלמיית, שושלת שנוסדה על ידי תלמי הראשון סוטר, גנרל יווני מקדוני שהיה בן לווייתו של אלכסנדר הגדול. בעוד ששושלת משפחתה הייתה יוונית, קליאופטרה אימצה לחלוטין את התרבות המצרית והייתה חריגה נדירה בשושלתה בכך שהייתה הראשונה שלמדה את השפה המצרית. היטמעות תרבותית זו, בשילוב עם תבונתה הפוליטית, הפכה אותה למנהיגה אדירה ואהובה בקרב עמה, והבדילה אותה מקודמיה." },
              { "title": "האיים הקנריים נקראים על שם כלבים, לא ציפורים", "text": "שמו של ארכיפלג ספרדי פופולרי זה מקורו במפתיע ואין לו כל קשר לציפור השיר הצהובה והעליזה. שם האיים נגזר מהביטוי הלטיני 'Canariae Insulae', שפירושו 'איי הכלבים'. על פי הדיווחים, שם זה ניתן על ידי חוקרים מוקדמים שנתקלו בזן של כלבים גדולים ועזים ('canes' בלטינית) באיים. רק מאוחר יותר נקראה הפרוש הקטן, יליד האזור, 'ציפור הקנרית' על שם אי מולדתו, מה שהוביל לתפיסה השגויה הנפוצה." }
            ],
            "whoWereThey": [
              { "name": "לאונרדו דה וינצ'י", "bio": "איש אשכולות איטלקי מתקופת הרנסאンス, פעיל כצייר, רשם, מהנדס, מדען, תיאורטיקן, פסל ואדריכל. גאונותו גילמה את האידיאל ההומניסטי של הרנסאנס." },
              { "name": "אייזק ניוטון", "bio": "מתמטיקאי, פיזיקאי ואסטרונום אנגלי, המוכר כאחד המדענים המשפיעים ביותר בכל הזמנים. ספרו 'פרינקיפיה מתמטיקה' ניסח את חוקי התנועה והכבידה האוניברסלית." },
              { "name": "ג'יין אוסטן", "bio": "סופרת אנגלייה הידועה בעיקר בזכות ששת הרומנים העיקריים שלה, המפרשים, מבקרים ומעירים על האצולה הקרקעית הבריטית בסוף המאה ה-18. 'גאווה ודעה קדומה' היא יצירתה המפורסמת ביותר." }
            ]
        }
    }
  }
};