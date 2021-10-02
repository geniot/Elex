package io.github.geniot.elex.ftindexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.ckb.SoraniAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.et.EstonianAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ga.IrishAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lt.LithuanianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LocaleAwareAnalyzer extends AnalyzerWrapper {
    private static Logger logger = LoggerFactory.getLogger(LocaleAwareAnalyzer.class);

    private Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> perLocaleAnalyzer = perLocaleAnalyzers();

    public LocaleAwareAnalyzer() {
        super(Analyzer.GLOBAL_REUSE_STRATEGY);
        this.defaultAnalyzer = perLocaleAnalyzer.get("en");
    }

    protected LocaleAwareAnalyzer(ReuseStrategy reuseStrategy) {
        super(reuseStrategy);
    }

    @Override
    public Analyzer getWrappedAnalyzer(final String languageCode) {
        if (languageCode == null) {
            return defaultAnalyzer;
        }
        Analyzer analyzer = perLocaleAnalyzer.get(languageCode);
        if (analyzer == null) {
            return defaultAnalyzer;
        }
        return analyzer;
    }

    private static Map<String, Analyzer> perLocaleAnalyzers() {
        final Map<String, Analyzer> m = new HashMap<>();
        m.put("ar", new ArabicAnalyzer());

        m.put("bg", new BulgarianAnalyzer());
        m.put("bn", new BengaliAnalyzer());
        m.put("br", new BrazilianAnalyzer());

        m.put("ca", new CatalanAnalyzer());
        m.put("cjk", new CJKAnalyzer());
        m.put("ckb", new SoraniAnalyzer());
        m.put("cz", new CzechAnalyzer());

        m.put("da", new DanishAnalyzer());
        m.put("de", new GermanAnalyzer());

        m.put("el", new GreekAnalyzer());
        m.put("en", new EnglishAnalyzer());
        m.put("es", new SpanishAnalyzer());
        m.put("et", new EstonianAnalyzer());
        m.put("eu", new BasqueAnalyzer());

        m.put("fa", new PersianAnalyzer());
        m.put("fi", new FinnishAnalyzer());
        m.put("fr", new FrenchAnalyzer());

        m.put("ga", new IrishAnalyzer());
        m.put("gl", new GalicianAnalyzer());

        m.put("hi", new HindiAnalyzer());
        m.put("hu", new HungarianAnalyzer());
        m.put("hy", new ArmenianAnalyzer());

        m.put("id", new IndonesianAnalyzer());
        m.put("it", new ItalianAnalyzer());

        m.put("lt", new LithuanianAnalyzer());
        m.put("lv", new LatvianAnalyzer());

        m.put("nl", new DutchAnalyzer());
        m.put("no", new NorwegianAnalyzer());

        m.put("pt", new PortugueseAnalyzer());

        m.put("ro", new RomanianAnalyzer());
        m.put("ru", new RussianAnalyzer());

        m.put("sv", new SwedishAnalyzer());

        m.put("th", new ThaiAnalyzer());
        m.put("tr", new TurkishAnalyzer());
        return m;
    }
}
