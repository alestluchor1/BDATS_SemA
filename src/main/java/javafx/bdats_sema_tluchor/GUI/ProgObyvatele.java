package javafx.bdats_sema_tluchor.GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.bdats_sema_tluchor.abstrdoublelist.AbstrDoubleList;
import javafx.bdats_sema_tluchor.abstrdoublelist.AbstrDoubleListException;
import javafx.bdats_sema_tluchor.generator.Generator;
import javafx.bdats_sema_tluchor.obyvatele.Obec;
import javafx.bdats_sema_tluchor.obyvatele.Obyvatele;
import javafx.bdats_sema_tluchor.obyvatele.enumKraj;
import javafx.bdats_sema_tluchor.obyvatele.enumPozice;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Random;

/**
 *
 * @author atluc
 */
public class ProgObyvatele extends Application {

    private final static int VYSKA_OKNO = 500;
    private final static int SIRKA_OKNO = 800;

    private ComboBox<enumKraj> krajComboBox;
    private Obyvatele obyvatele;
    private final Generator generator = new Generator();
    private AbstrDoubleList<Obec> abstrDoubleList;
    private final Random random = new Random();

    // Tlačítka
    private final Button zobrazObceNadPrumerButton = new Button("Zobraz obce nad prumer");
    private final Button importButton = new Button("Importovat data ze souboru");
    private final Button zobrazObceButton = new Button("Zobrazit obce");
    private final Button zrusButton = new Button("Zrušit obce");
    private final Button prumerButton = new Button("Zjistit průměr obyvatel");
    private final Button vlozObecButton = new Button("Vložit obec");
    private final Button odeberObecButton = new Button("Odebrat obec");
    private final Button pristupObecButton = new Button("Zpřístupnit obec");
    private final Button uloz = new Button("Uložit do CSV");
    private final Button generovatButton = new Button("Generovat obce");
    private final Button zobrazObcePodleKrajeButton = new Button("Zobrazit obce podle kraje");

    //Spinner
    private final Spinner<Integer> spPocetGenerovani = new Spinner<>(1, 100, 1);

    @Override
    public void start(Stage stage) {

        obyvatele = new Obyvatele();
        abstrDoubleList = new AbstrDoubleList<>();

        Label titleLabel = new Label("Správa obcí");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);

        // Nastavení ListView a ComboBoxů
        ListView<String> listView = new ListView<>();
        krajComboBox = new ComboBox<>();
        krajComboBox.getItems().add(null); // Přidání prázdné položky na začátek
        krajComboBox.getItems().addAll(enumKraj.values());
        ComboBox<enumPozice> poziceComboBox = new ComboBox<>();
        poziceComboBox.getItems().addAll(enumPozice.values());

        // Rozmístění pomocí GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // První sloupec - Akce s obcemi
        VBox leftVBox = new VBox(10, importButton, zobrazObceButton, zobrazObcePodleKrajeButton, zrusButton, prumerButton, uloz);
        leftVBox.setAlignment(Pos.TOP_LEFT);
        gridPane.add(leftVBox, 0, 0);

        // Druhý sloupec - Vkládání, přístup a odebírání obcí
        VBox rightVBox = new VBox(10, vlozObecButton, odeberObecButton, pristupObecButton, zobrazObceNadPrumerButton);
        rightVBox.setAlignment(Pos.TOP_LEFT);
        gridPane.add(rightVBox, 1, 0);

        // Třetí sloupec - Generování obcí a počet
        VBox generatorVBox = new VBox(10, new Label("Počet obcí k generování:"), spPocetGenerovani, generovatButton);
        generatorVBox.setAlignment(Pos.TOP_LEFT);
        gridPane.add(generatorVBox, 2, 0);

        // Čtvrtý sloupec - Výběr kraje a pozice
        VBox selectionVBox = new VBox(10, new Label("Vyberte kraj:"), krajComboBox, new Label("Vyberte pozici:"), poziceComboBox);
        selectionVBox.setAlignment(Pos.TOP_LEFT);
        gridPane.add(selectionVBox, 3, 0);

        // ListView pro zobrazení obcí
        VBox listVBox = new VBox(10, new Label("Seznam obcí:"), listView);
        listVBox.setAlignment(Pos.TOP_LEFT);
        listVBox.setPadding(new Insets(10));
        gridPane.add(listVBox, 0, 1, 4, 1); // Spojí do jednoho řádku s 4 sloupci

        //setOnAction
        zobrazObcePodleKrajeButton.setOnAction(e -> zobrazObcePodleKraje(krajComboBox.getValue(), listView));
        zobrazObceNadPrumerButton.setOnAction(e -> zobrazObceNadPrumer(krajComboBox.getValue(), listView));
        importButton.setOnAction(e -> importData(listView));
        zobrazObceButton.setOnAction(e -> zobrazObce(listView));
        zrusButton.setOnAction(e -> zrusObce(listView));
        prumerButton.setOnAction(e -> zjistiPrumer(krajComboBox.getValue()));
        vlozObecButton.setOnAction(e -> vlozObec(krajComboBox.getValue(), poziceComboBox.getValue(), listView));
        pristupObecButton.setOnAction(e -> pristupObec(krajComboBox.getValue(), poziceComboBox.getValue(), listView));
        odeberObecButton.setOnAction(e -> odeberObec(krajComboBox.getValue(), poziceComboBox.getValue(), listView));
        uloz.setOnAction(e -> ulozDoSouboru(listView));

        generovatButton.setOnAction(e -> {
            try {

                generator.generateData(listView, spPocetGenerovani, abstrDoubleList);

                listView.getItems().clear();

                for (Object obec : abstrDoubleList) {
                    if (obec instanceof Obec obec1) {

                        obyvatele.vlozObec(obec1, enumPozice.PRVNI, enumKraj.values()[random.nextInt(enumKraj.values().length)]);

                        // Přidání do listView pro zobrazení
                        listView.getItems().add(obec1.toString() + " (Kraj: " + enumKraj.values()[random.nextInt(enumKraj.values().length)] + ")");
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Obce byly úspěšně generovány a přidány.");
                alert.show();

            } catch (AbstrDoubleListException ex) {
                Logger.getLogger(ProgObyvatele.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při generování obcí: " + ex.getMessage());
                alert.show();
            } catch (Exception ex) { 
                Logger.getLogger(ProgObyvatele.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Nastala neočekávaná chyba: " + ex.getMessage());
                alert.show();
            }
        });

        // Scéna a její nastavení
        VBox mainVBox = new VBox(20, titleLabel, gridPane);
        mainVBox.setStyle("-fx-background-color: lightblue;");
        mainVBox.setPadding(new Insets(20));
        Scene scene = new Scene(mainVBox, SIRKA_OKNO, VYSKA_OKNO);
        stage.setScene(scene);
        stage.setTitle("Správa obcí (Tlučhoř)");
        stage.show();
    }

    private void ulozDoSouboru(ListView<String> listView) {

        if (listView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Žádné položky k uložení.");
            alert.show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Uložit CSV soubor");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV soubory", "*.csv"));

        // Otevře dialog pro uložení souboru
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            // podmínka, jestli má soubor příponu .csv
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (String item : listView.getItems()) {
                    writer.write(item + "\n");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Soubor byl úspěšně uložen na: " + file.getAbsolutePath());
                alert.show();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při ukládání souboru: " + e.getMessage());
                alert.show();
                Logger.getLogger(ProgObyvatele.class.getName()).log(Level.SEVERE, "Chyba při ukládání do CSV", e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Uložení souboru bylo zrušeno.");
            alert.show();
        }
    }

    private void odeberObec(enumKraj kraj, enumPozice pozice, ListView<String> listView) {
        if (kraj == null || pozice == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kraj a pozici.");
            alert.show();
            return;
        }

        try {
            Obec odebrano = obyvatele.odeberObec(pozice, kraj);
            if (odebrano != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Obec byla úspěšně odebrána: " + odebrano.toString());
                alert.show();
                zobrazObce(listView);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Žádná obec nebyla odebrána.");
                alert.show();
            }
        } catch (AbstrDoubleListException ex) {
            Logger.getLogger(ProgObyvatele.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při odebrání obce: " + ex.getMessage());
            alert.show();
        }
    }

    private void pristupObec(enumKraj kraj, enumPozice pozice, ListView<String> listView) {
        if (kraj == null || pozice == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kraj a pozici.");
            alert.show();
            return;
        }

        try {
            Obec obec = obyvatele.zpristupniObec(pozice, kraj);
            if (obec != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nalezená obec: " + obec.toString());
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Žádná obec nenalezena.");
                alert.show();
            }
        } catch (AbstrDoubleListException ex) {
            Logger.getLogger(ProgObyvatele.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při zpřístupnění obce: " + ex.getMessage());
            alert.show();
        }
    }

    private void importData(ListView<String> listView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vyberte CSV soubor");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV soubory", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            int n = obyvatele.importData(selectedFile.getAbsolutePath());

            listView.getItems().clear();

            // Získání seznamu obcí a jejich přidání do ListView
            for (enumKraj kraj : enumKraj.values()) {
                AbstrDoubleList<Obec> seznamObci = obyvatele.zobrazObce(kraj);
                if (seznamObci != null) {
                    for (Object obec : seznamObci) {
                        listView.getItems().add(obec.toString() + " (Kraj: " + kraj + ")");
                    }
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Načteno " + n + " obcí.");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nebyl vybrán žádný soubor.");
            alert.show();
        }
    }

    private void zobrazObce(ListView<String> listView) {
        listView.getItems().clear();

        for (enumKraj kraj : enumKraj.values()) {
            AbstrDoubleList<Obec> obceSeznam = obyvatele.zobrazObce(kraj);
            if (obceSeznam != null) {
                for (Object obec : obceSeznam) {
                    listView.getItems().add(obec.toString() + " (Kraj: " + kraj + ")");
                }
            }
        }

        // Pokud nebyly nalezeny žádné obce, zobraz upozornění
        if (listView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Žádné obce nebyly nalezeny.");
            alert.show();
        }
    }

    private void zrusObce(ListView<String> listView) {
        obyvatele.zrus(null);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Všechny obce byly zrušeny.");
        alert.show();
        listView.getItems().clear();
    }

    private void zjistiPrumer(enumKraj kraj) {

        if (kraj == null || kraj.ordinal() == 0) {
            kraj = null;
        }

        // Získání průměru
        float prumer = obyvatele.zjistiPrumer(kraj);

        if (prumer == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nebyly nalezeny žádné obce pro vybraný kraj.");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Průměr obyvatel: " + prumer);
            alert.show();
        }
    }

    private void vlozObec(enumKraj kraj, enumPozice pozice, ListView<String> listView) {
        if (kraj == null || pozice == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kraj a pozici.");
            alert.show();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vložení nové obce");
        dialog.setHeaderText("Zadejte informace o nové obci (PSC;název;počet mužů;počet žen):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(";");
            if (parts.length == 4) {
                try {
                    String PSC = parts[0].trim();
                    String nazev = parts[1].trim();
                    int pocetMuz = Integer.parseInt(parts[2].trim());
                    int pocetZen = Integer.parseInt(parts[3].trim());

                    Obec novaObec = new Obec(PSC, nazev, pocetMuz, pocetZen);

                    // Vložení nové obce do AbstrDoubleList
                    obyvatele.vlozObec(novaObec, pozice, kraj);

                    // Přidání nové obce do ListView
                    listView.getItems().add(novaObec.toString() + " (Kraj: " + kraj + ")");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Obec byla úspěšně vložena.");
                    alert.show();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při převodu čísel: " + e.getMessage());
                    alert.show();
                } catch (AbstrDoubleListException ex) {
                    Logger.getLogger(ProgObyvatele.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Špatný formát. Zadejte PSC;název;počet mužů;počet žen.");
                alert.show();
            }
        });
    }

    private void zobrazObceNadPrumer(enumKraj kraj, ListView<String> listView) {
        listView.getItems().clear(); // Vyčistit stávající položky

        if (kraj == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kraj.");
            alert.show();
            return;
        }

        // Získání obcí nad průměr pro vybraný kraj
        AbstrDoubleList<Obec> obceNadPrumer = obyvatele.zobrazObceNadPrumer(kraj);

        Iterator<Obec> iterator = obceNadPrumer.iterator();

        // Přidání názvů obcí do ListView
        while (iterator.hasNext()) {
            Obec obec = iterator.next();
            listView.getItems().add(obec.toString() + " (Kraj: " + kraj + ")");
        }

        // Pokud nebyly nalezeny žádné obce, zobrazíme upozornění
        if (listView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Žádné obce nebyly nalezeny nad průměrem.");
            alert.show();
        }
    }

    private void zobrazObcePodleKraje(enumKraj kraj, ListView<String> listView) {
        listView.getItems().clear();

        // Pokud není vybrán žádný kraj, zobrazit varování
        if (kraj == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kraj.");
            alert.show();
            return;
        }

        // Získání obcí pro vybraný kraj
        AbstrDoubleList<Obec> obceSeznam = obyvatele.zobrazObce(kraj);
        if (obceSeznam != null) {
            for (Object obec : obceSeznam) {
                listView.getItems().add(obec.toString() + " (Kraj: " + kraj + ")");
            }
        }

        // Pokud nebyly nalezeny žádné obce, zobraz upozornění
        if (listView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Žádné obce nebyly nalezeny pro vybraný kraj.");
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
