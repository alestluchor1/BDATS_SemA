package javafx.bdats_sema_tluchor.generator;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import java.util.Random;
import javafx.bdats_sema_tluchor.abstrdoublelist.AbstrDoubleList;
import javafx.bdats_sema_tluchor.obyvatele.Obec;

public class Generator {

    private final Random random = new Random();

    private static final String[] KRAJE = {
        "Hlavni mesto Praha", // 1
        "Jihocesky", // 2
        "Jihomoravsky", // 3
        "Karlovarsky", // 4
        "Kraj Vysocina", // 5
        "Kralovehradecky", // 6
        "Liberecky", // 7
        "Moravskoslezsky", // 8
        "Olomoucky", // 9
        "Pardubicky", // 10
        "Plzensky", // 11
        "Stredocesky", // 12
        "Ustecky", // 13
        "Zlinsky" // 14
    };

    // Obce a jejich PSČ
    private static final String[] OBCE = {
        "Praha", "Ceske Budejovice", "Brno", "Karlovy Vary", "Jihlava", "Hradec Kralove",
        "Liberec", "Ostrava", "Olomouc", "Pardubice", "Plzen", "Melnik", "Usti nad Labem", "Zlin"
    };

    private static final String[] PSC = {
        "11538", "37421", "60200", "36001", "58601", "50003", "46001", "70200", "77900", "53002", "30100", "27601", "40001", "76001"
    };

    public void generateData(ListView<String> listView, Spinner<Integer> spPocetGenerovani, AbstrDoubleList<Obec> obceList) {
        if (obceList == null) {
            throw new IllegalArgumentException("Seznam obcí je null");
        }

        int pocetGenerovani = spPocetGenerovani.getValue();

        ObservableList<String> obceGenerator = listView.getItems();

        for (int i = 0; i < pocetGenerovani; i++) {
            int krajIndex = random.nextInt(KRAJE.length);
            String kraj = KRAJE[krajIndex];
            String obec = OBCE[krajIndex];
            String psc = PSC[krajIndex];

            int pocetMuzu = random.nextInt(10000) + 5000;
            int pocetZen = random.nextInt(10000) + 5000;

            Obec novaObec = new Obec(psc, obec, pocetMuzu, pocetZen);

            obceList.vlozPrvni(novaObec);

            obceGenerator.add(novaObec.toString() + " (Kraj: " + kraj + ")");
        }
    }
}
