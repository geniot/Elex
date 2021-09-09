package io.github.geniot.elex.tools.decompile.lsd;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Tools {
    public static Map<String, String> lang_map = createMap();

    public static Map<String, String> code_map = createCodeMap();

    private static Map<String, String> createCodeMap() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("English", "en");
        result.put("Russian", "ru");
        return result;
    }

    private static Map<String, String> createMap() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("1555", "Abazin");
        result.put("1556", "Abkhaz");
        result.put("1557", "Adyghe");
        result.put("1078", "Afrikaans");
        result.put("1559", "Agul");
        result.put("1052", "Albanian");
        result.put("1545", "Altaic");
        result.put("1025", "Arabic");// x5 tested support
//            result.put("1025,"ArabicSaudiArabia");
        result.put("5121", "ArabicAlgeria");
        result.put("15361", "ArabicBahrain");
        result.put("3073", "ArabicEgypt");
        result.put("2049", "ArabicIraq");
        result.put("11265", "ArabicJordan");
        result.put("13313", "ArabicKuwait");
        result.put("12289", "ArabicLebanon");
        result.put("4097", "ArabicLibya");
        result.put("6145", "ArabicMorocco");
        result.put("8193", "ArabicOman");
        result.put("16385", "ArabicQatar");
        result.put("10241", "ArabicSyria");
        result.put("7169", "ArabicTunisia");
        result.put("14337", "ArabicUAE");
        result.put("9217", "ArabicYemen");
        result.put("1067", "Armenian");// x5 tested support
//            result.put("1067,"ArmenianEastern");
        result.put("33835", "ArmenianGrabar");
        result.put("32811", "ArmenianWestern");
        result.put("1101", "Assamese");
        result.put("1558", "Awar");
        result.put("1560", "Aymara");
        result.put("2092", "AzeriCyrillic");
        result.put("1068", "AzeriLatin");
        result.put("1561", "Bashkir");
        result.put("1069", "Basque");
        result.put("1059", "Belarusian");
//        result.put("1059", "Byelorussian");
        result.put("1562", "Bemba");
        result.put("1093", "Bengali");
        result.put("1563", "Blackfoot");
        result.put("1536", "Breton");
        result.put("1564", "Bugotu");
        result.put("1026", "Bulgarian");
        result.put("1109", "Burmese");
        result.put("1565", "Buryat");
        result.put("1027", "Catalan");
        result.put("1566", "Chamorro");
        result.put("1544", "Chechen");
        result.put("1028", "Chinese");// x5 tested support
//            result.put("1028,"ChineseTaiwan");
        result.put("3076", "ChineseHongKong");
        result.put("5124", "ChineseMacau");
        result.put("2052", "ChinesePRC");
        result.put("4100", "ChineseSingapore");
        result.put("1567", "Chukcha");
        result.put("1568", "Chuvash");
        result.put("1569", "Corsican");
        result.put("1546", "CrimeanTatar");
        result.put("1050", "Croatian");
        result.put("1570", "Crow");
        result.put("1029", "Czech");
        result.put("1030", "Danish");
        result.put("1572", "Dungan");
        result.put("1043", "Dutch");// x5 tested
        result.put("2067", "DutchBelgian");// not supported
        result.put("1033", "English");// 1033,EnglishUnitedStates
        result.put("3081", "EnglishAustralian");
        result.put("10249", "EnglishBelize");
        result.put("4105", "EnglishCanadian");
        result.put("9225", "EnglishCaribbean");
        result.put("6153", "EnglishIreland");
        result.put("8201", "EnglishJamaica");
        result.put("35849", "EnglishLaw");
        result.put("33801", "EnglishMedical");
        result.put("5129", "EnglishNewZealand");
        result.put("13321", "EnglishPhilippines");
        result.put("34825", "EnglishProperNames");
        result.put("7177", "EnglishSouthAfrica");
        result.put("11273", "EnglishTrinidad");
        result.put("2057", "EnglishUnitedKingdom");
        result.put("12297", "EnglishZimbabwe");
        result.put("1573", "EskimoCyrillic");
        result.put("1537", "Esperanto");
        result.put("1061", "Estonian");
        result.put("1574", "Even");
        result.put("1575", "Evenki");
        result.put("1065", "Farsi");
        result.put("1538", "Fijian");
        result.put("1035", "Finnish");
        result.put("1036", "French");// x5 supported
        result.put("2060", "FrenchBelgian");
        result.put("3084", "FrenchCanadian");
        result.put("5132", "FrenchLuxembourg");
        result.put("6156", "FrenchMonaco");
        result.put("33804", "FrenchProperNames");
        result.put("4108", "FrenchSwiss");
        result.put("1122", "Frisian");
        result.put("1576", "Frisian_Legacy");  // x6
        result.put("1577", "Friulian");
        result.put("1084", "GaelicScottish");
        result.put("1578", "Gagauz");
        result.put("1110", "Galician");
        result.put("1579", "Galician_Legacy");  // x6
        result.put("1580", "Ganda");
        result.put("1079", "Georgian");
        result.put("1031", "German");
        result.put("3079", "GermanAustrian");
        result.put("34823", "GermanLaw");
        result.put("5127", "GermanLiechtenstein");
        result.put("4103", "GermanLuxembourg");
        result.put("36871", "GermanMedical");
        result.put("32775", "GermanNewSpelling");
        result.put("35847", "GermanNewSpellingLaw");
        result.put("37895", "GermanNewSpellingMedical");
        result.put("39943", "GermanNewSpellingProperNames");
        result.put("38919", "GermanProperNames");
        result.put("2055", "GermanSwiss");
        result.put("1032", "Greek");
        result.put("32776", "GreekKathareusa");
        result.put("1140", "Guarani");
        result.put("1582", "Guarani_Legacy");  // x6
        result.put("1095", "Gujarati");
        result.put("1583", "Hani");
        result.put("1128", "Hausa");  // x6
        result.put("1652", "Hausa_Legacy");
        result.put("1141", "Hawaiian");  // x6
        result.put("1539", "Hawaiian_Legacy");
        result.put("1037", "Hebrew");
        result.put("1081", "Hindi");
        result.put("1038", "Hungarian");
        result.put("1039", "Icelandic");
        result.put("1584", "Ido");
        result.put("1057", "Indonesian");
        result.put("1585", "Ingush");
        result.put("1586", "Interlingua");
        result.put("2108", "Irish");  // x6
        result.put("1552", "Irish_Legacy");  // x6
//        result.put("2108", "Gaelic");// x6
//        result.put("1552", "Gaelic_Legacy");   // x6
        result.put("1040", "Italian");// x5 tested
        result.put("33808", "ItalianProperNames");
        result.put("2064", "ItalianSwiss");
        result.put("1041", "Japanese");
        result.put("1548", "Kabardian");
        result.put("1587", "Kalmyk");
        result.put("1099", "Kannada");
        result.put("1589", "KarachayBalkar");
        result.put("1588", "Karakalpak");
        result.put("1120", "Kashmiri");
        result.put("2144", "KashmiriIndia");
        result.put("1590", "Kasub");
        result.put("1591", "Kawa");
        result.put("1087", "Kazakh");
        result.put("1592", "Khakas");
        result.put("1593", "Khanty");
        result.put("1107", "Khmer");
        result.put("1594", "Kikuyu");
        result.put("1595", "Kirgiz");
        result.put("1597", "KomiPermian");
        result.put("1596", "KomiZyryan");
        result.put("1598", "Kongo");
        result.put("1111", "Konkani");
        result.put("1042", "Korean");
        result.put("2066", "KoreanJohab");
        result.put("1599", "Koryak");
        result.put("1600", "Kpelle");
        result.put("1601", "Kumyk");
        result.put("1602", "Kurdish");
        result.put("1603", "KurdishCyrillic");
        result.put("1604", "Lak");
        result.put("1108", "Lao");
        result.put("1142", "Latin");  // x6
        result.put("1540", "Latin_Legacy");
        result.put("1062", "Latvian");
        result.put("1655", "LatvianGothic");
        result.put("1605", "Lezgin");
        result.put("1063", "Lithuanian");
        result.put("2087", "LithuanianClassic");
        result.put("1606", "Luba");
        result.put("1071", "Macedonian");
        result.put("1607", "Malagasy");
        result.put("1086", "Malay");
        result.put("2110", "MalayBruneiDarussalam");
        result.put("1100", "Malayalam");
        result.put("1608", "Malinke");
        result.put("1082", "Maltese");
        result.put("1112", "Manipuri");
        result.put("1609", "Mansi");
        result.put("1153", "Maori");  // x6
        result.put("1102", "Marathi");
        result.put("1610", "Mari");
        result.put("1611", "Maya");
        result.put("1612", "Miao");
        result.put("1613", "Minankabaw");
        result.put("1614", "Mohawk");
        result.put("1104", "Mongol");
        result.put("1615", "Mordvin");
        result.put("1616", "Nahuatl");
        result.put("1617", "Nanai");
        result.put("1618", "Nenets");
        result.put("1121", "Nepali");
        result.put("2145", "NepaliIndia");
        result.put("1619", "Nivkh");
        result.put("1620", "Nogay");
        result.put("1044", "Norwegian");
//        result.put("1044", "NorwegianBokmal");
        result.put("2068", "NorwegianNynorsk");
        result.put("1621", "Nyanja");
        result.put("1622", "Occidental");
        result.put("1623", "Ojibway");
        result.put("32777", "OldEnglish");
        result.put("32780", "OldFrench");
        result.put("33799", "OldGerman");
        result.put("32784", "OldItalian");
        result.put("1657", "OldSlavonic");  // x6
        result.put("32778", "OldSpanish");
        result.put("1096", "Oriya");
        result.put("1547", "Ossetic");
        result.put("1145", "Papiamento");  // x6
        result.put("1624", "Papiamento_Legacy");
        result.put("1625", "PidginEnglish");
        result.put("1654", "Pinyin");
        result.put("1045", "Polish");
        result.put("1046", "Portuguese");// not supported
//            result.put(" 1046,"PortugueseBrazilian");
        result.put("2070", "PortugueseStandard");// x5 supported
        result.put("1541", "Provencal");
        result.put("1094", "Punjabi");
        result.put("1131", "Quechua");  // x6
//        result.put(" 1131,"QuechuaBolivia");  // x6
        result.put("2155", "QuechuaEcuador");  // x6
        result.put("3179", "QuechuaPeru");  // x6
        result.put("1626", "Quechua_Legacy");
        result.put("1047", "RhaetoRomanic");
        result.put("1048", "Romanian");
        result.put("2072", "RomanianMoldavia");
        result.put("1627", "Romany");
        result.put("1628", "Ruanda");
        result.put("1629", "Rundi");
        result.put("1049", "Russian");
        result.put("2073", "RussianMoldavia");
        result.put("32793", "RussianOldSpelling");
        result.put("34841", "RussianOldOrtho");  // x6
        result.put("33817", "RussianProperNames");
        result.put("1083", "Saami");
        result.put("1542", "Samoan");
        result.put("1103", "Sanskrit");
        result.put("1630", "Selkup");
        result.put("3098", "SerbianCyrillic");
        result.put("2074", "SerbianLatin");
        result.put("1631", "Shona");
        result.put("1113", "Sindhi");
        result.put("1051", "Slovak");
        result.put("1060", "Slovenian");
        result.put("1143", "Somali");  // x6
        result.put("1633", "Somali_Legacy");
        result.put("1070", "Sorbian");// not supported
        result.put("1634", "Sotho");
//        result.put("1034,"Spanish");// not supported
        result.put("1034", "SpanishTraditionalSort");// x5 tested
        result.put("11274", "SpanishArgentina");
        result.put("16394", "SpanishBolivia");
        result.put("13322", "SpanishChile");
        result.put("9226", "SpanishColombia");
        result.put("5130", "SpanishCostaRica");
        result.put("7178", "SpanishDominicanRepublic");
        result.put("12298", "SpanishEcuador");
        result.put("17418", "SpanishElSalvador");
        result.put("4106", "SpanishGuatemala");
        result.put("18442", "SpanishHonduras");
        result.put("2058", "SpanishMexican");
        result.put("3082", "SpanishModernSort");
        result.put("19466", "SpanishNicaragua");
        result.put("6154", "SpanishPanama");
        result.put("15370", "SpanishParaguay");
        result.put("10250", "SpanishPeru");
        result.put("33802", "SpanishProperNames");
        result.put("20490", "SpanishPuertoRico");
        result.put("14346", "SpanishUruguay");
        result.put("8202", "SpanishVenezuela");
        result.put("1635", "Sunda");
        result.put("1072", "Sutu");
        result.put("1089", "Swahili");
        result.put("1636", "Swazi");
        result.put("1053", "Swedish");
        result.put("2077", "SwedishFinland");
        result.put("1637", "Tabassaran");
        result.put("1553", "Tagalog");
        result.put("1639", "Tahitian");
        result.put("1064", "Tajik");  // x6
        result.put("1638", "Tajik_Legacy");
        result.put("1097", "Tamil");
        result.put("1092", "Tatar");
        result.put("1098", "Telugu");
        result.put("1054", "Thai");
        result.put("1105", "Tibet");
        result.put("1641", "Tongan");
        result.put("1073", "Tsonga");
        result.put("1074", "Tswana");
//        result.put(" 1074", "Chuana");
        result.put("1642", "Tun");
        result.put("1055", "Turkish");
        result.put("1090", "Turkmen");  // x6
        result.put("1643", "Turkmen_Legacy");
        result.put("1644", "Tuvin");
        result.put("1645", "Udmurt");
//        result.put(" 1646,"Uighur");// not supported
        result.put("1646", "UighurCyrillic");// not supported
        result.put("1647", "UighurLatin");
        result.put("1058", "Ukrainian");
        result.put("1653", "Universal");
        result.put("2080", "UrduIndia");
        result.put("1056", "UrduPakistan");
        result.put("1554", "User");
        result.put("2115", "UzbekCyrillic");
        result.put("1091", "UzbekLatin");
        result.put("1075", "Venda");
        result.put("1066", "Vietnamese");
        result.put("1648", "Visayan");
        result.put("1106", "Welsh");  // x6
        result.put("1543", "Welsh_Legacy");
        result.put("1160", "Wolof");  //x6
        result.put("1649", "Wolof_Legacy");
        result.put("1076", "Xhosa");
        result.put("1157", "Yakut");  // x6
        result.put("1650", "Yakut_Legacy");
        result.put("1085", "Yiddish");
        result.put("1651", "Zapotec");
        result.put("1077", "Zulu");
        return Collections.unmodifiableMap(result);
    }


    public static String int2unichr(byte[] bbs) {
        return new String(bbs, StandardCharsets.UTF_16);
    }

    static int bit_length(int num) {
        int res = 1;
        num >>= 1;
        while (num != 0) {
            res += 1;
            num >>= 1;
        }
        return res;
    }

}

