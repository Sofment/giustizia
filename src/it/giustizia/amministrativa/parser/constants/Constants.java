package it.giustizia.amministrativa.parser.constants;

/**
 * Created by avsupport on 2/11/15.
 */
public class Constants {
    public static String CURRENT_TYPE = Type.TAR;

    public static class Url {
        public static final String TAR_MAIN_URL = "https://www.giustizia-amministrativa.it/cdsintra/cdsintra/Attivita/tarattivita/index.html";
        public static final String CS_MAIN_URL = "https://www.giustizia-amministrativa.it/cdsintra/cdsintra/index.html";
    }

    public static class Xpath {
        public static final String PROVVEDIMENTI = "//*[@id=\"menu\"]/li[1]/table/tbody/tr/td[2]/a";
        public static final String EDIT_TEXT_FROM = "//*[@id=\"id1::content\"]";
        public static final String EDIT_TEXT_TO = "//*[@id=\"id2::content\"]";
        public static final String BUTTON_CERCA = "//*[@id=\"cb1\"]";
        public static final String BUTTON_VISUALIZZA = "//*[@id=\"cb3\"]";
        public static final String T_BODY = "//*[@id=\"t1::db\"]/table/tbody";


        public static final String CONSIGLIO_DI_STATTO = "//*[@id=\"navigation_dx\"]/ul/li[1]/a/span";
        public static final String ATTIVITA_GIURISDIZIONALE = "//*[@id=\"content\"]/div[2]/p[2]/a";
        public static final String SCROLLER = "//*[@id=\"t1::scroller\"]";
    }

    public static class ClassName {
        public static final String ROW = "x10m";
        public static final String CELL = "x10j";
        public static final String SELECTED_ROW = "p_AFSelected";
    }

    public static class Type{
        public static final String TAR = "tar";
        public static final String CDS = "cds";
    }
}