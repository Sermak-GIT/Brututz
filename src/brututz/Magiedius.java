package brututz;

import brututz.util.Util;

import java.io.IOException;
import java.util.*;

import static brututz.util.Util.MathUtil.closerToThan;
import static brututz.util.Util.MathUtil.ggT;
import static brututz.util.Util.MediaInderfaces.FileInderface.appendToFile;
import static brututz.util.Util.MediaInderfaces.WikipediaInderface.getNewWikipediaPage;
import static java.lang.System.err;
import static java.lang.System.out;
import static brututz.util.Util.MediaInderfaces.ConsoleInderface.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Magiedius {
    
    public static void aToConsFile(String s) {
        appendToFile(System.getProperty("user.home") + "/console" + langCode + ".txt", new String[] {s});
    }
    
    static double tweaker;
    int rightGuessesInARow = 0;
    int falseGuessesInARow = 0;
    private void learn(int maxTextLength) {
        initSigs();

        while (true) {
            HashMap<String, String> hm = getNewWikipediaPage(langCode);
            if ((hm.get("text").length() - maxTextLength) > 0) {
                colorPrintln("Reading: " + hm.get("h1") + " ["+ hm.get("text").length() + " chars. Skipping " + (hm.get("text").length() - maxTextLength) + "]", Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                aToConsFile("Reading: " + hm.get("h1") + " ["+ hm.get("text").length() + " chars. Skipping " + (hm.get("text").length() - maxTextLength) + "]");
            } else {
                colorPrintln("Reading: " + hm.get("h1") + " ["+ hm.get("text").length() + " chars. Skipping 0]", Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                aToConsFile("Reading: " + hm.get("h1") + " ["+ hm.get("text").length() + " chars. Skipping 0]");
            }

            String randomPassword = "";
            int passwordLength = Util.MathUtil.randomBetween(1, 10);
            for (int j = 0; j < passwordLength; j++) {
                randomPassword += charSet[Util.MathUtil.randomBetween(0, charSet.length - 1)];
            }
            String crypt;
            if ((hm.get("text").length() - maxTextLength) > 0) {
                crypt = vigenereHin(charseterize(hm.get("text").substring(0, maxTextLength)), randomPassword);
            } else {
                crypt = vigenereHin(charseterize(hm.get("text")), randomPassword);
            }
            int f = friedmanTest(crypt);
            if (f == passwordLength) {
                colorPrintln("Guessed correctly. Yay!", Foregrounds.GREEN, Backgrounds.TRANSPARENT);
                aToConsFile("Guessed correctly. Yay!");
                rightGuessesInARow++;
                falseGuessesInARow = 0;
                if (rightGuessesInARow > 10) {
                    tweaker /= 10;
                    colorPrintln("Tweaker getting divided by ten.", Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                    aToConsFile("Tweaker getting divided by ten.");
                    rightGuessesInARow = 0;
                }
            } else {
                if (f < passwordLength) {
                    colorPrintln("Guessed wrong :( [accuracy: " + Math.abs((f + 0.0) / passwordLength) + "% | guessed: " + f + ", correct: " + passwordLength + "]", Foregrounds.RED, Backgrounds.TRANSPARENT);
                    aToConsFile("Guessed wrong :( [accuracy: " + Math.abs((f + 0.0) / passwordLength) + "% | guessed: " + f + ", correct: " + passwordLength + "]");
                } else {
                    colorPrintln("Guessed wrong :( [accuracy: " + Math.abs((passwordLength + 0.0) / f) + "% | guessed: " + f + ", correct: " + passwordLength + "]", Foregrounds.RED, Backgrounds.TRANSPARENT);
                    aToConsFile("Guessed wrong :( [accuracy: " + Math.abs((passwordLength + 0.0) / f) + "% | guessed: " + f + ", correct: " + passwordLength + "]");
                }

                rightGuessesInARow = 0;
                falseGuessesInARow++;
                if (falseGuessesInARow > 10) {
                    tweaker *= 10;
                    colorPrintln("Tweaker getting multiplyed by ten.", Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                    aToConsFile("Tweaker getting multiplyed by ten.");
                    falseGuessesInARow = 0;
                }

                if (hmMax > passwordLength) {
                    if (hm2Max > passwordLength) {
                        int fr = f/passwordLength;
                        durchschnitt -=  tweaker;
                        if (closerToThan(fr, 1, (friedmanTest(crypt) / passwordLength))) {
                            durchschnitt += tweaker;
                        } else {
                            colorPrintln("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt, Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                            aToConsFile("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt);
                        }
                    } else {
                        if ((f/passwordLength) > 1) {
                            if (passwordLength % f == 0) {
                                double fr = f + 0.0 /passwordLength;
                                minSignificantPercentageOfGoodHits -=  tweaker;
                                double frr = (friedmanTest(crypt) + 0.0 / passwordLength);
                                if (closerToThan(fr, 1, frr)) {
                                    colorPrintln("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%", Foregrounds.MAGENTA, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%");
                                    minSignificantPercentageOfGoodHits += tweaker;
                                } else {
                                    colorPrintln("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt, Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt);
                                }
                            } else {
                                double fr = f + 0.0 /passwordLength;
                                minSignificantPercentageOfBadHits -=  tweaker;
                                double frr = (friedmanTest(crypt) + 0.0 / passwordLength);
                                if (closerToThan(fr, 1, frr)) {
                                    colorPrintln("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%", Foregrounds.MAGENTA, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%");
                                    minSignificantPercentageOfBadHits += tweaker;
                                } else {
                                    colorPrintln("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt, Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt);
                                }
                            }
                        } else {
                            if (passwordLength % f == 0) {
                                double fr = f + 0.0 /passwordLength;
                                minSignificantPercentageOfGoodHits +=  tweaker;
                                double frr = (friedmanTest(crypt) + 0.0 / passwordLength);
                                if (closerToThan(fr, 1, frr)) {
                                    colorPrintln("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%", Foregrounds.MAGENTA, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%");
                                    minSignificantPercentageOfGoodHits -= tweaker;
                                } else {
                                    colorPrintln("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt, Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt);
                                }
                            } else {
                                double fr = f + 0.0 /passwordLength;
                                minSignificantPercentageOfBadHits +=  tweaker;
                                double frr = (friedmanTest(crypt) + 0.0 / passwordLength);
                                if (closerToThan(fr, 1, frr)) {
                                    colorPrintln("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%", Foregrounds.MAGENTA, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaking makes stuff worse: Tweaked: " + frr + "%, Untweaked: " + fr + "%");
                                    minSignificantPercentageOfBadHits -= tweaker;
                                } else {
                                    colorPrintln("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt, Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                                    aToConsFile("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt);
                                }
                            }
                        }
                    }
                } else {
                    int fr = f/passwordLength;
                    durchschnitt +=  tweaker;
                    if (closerToThan(fr, 1, (friedmanTest(crypt) / passwordLength))) {
                        durchschnitt -= tweaker;
                    } else {
                        colorPrintln("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt, Foregrounds.YELLOW, Backgrounds.TRANSPARENT);
                        aToConsFile("Tweaked percentages: " + minSignificantPercentageOfGoodHits + "|" + minSignificantPercentageOfBadHits + "|" + durchschnitt);
                    }
                }
            }
        }
    }

    //e.g: keyLength = 16 found hits indicating keyLength = 8 ("good" ggT(8, 16) = 8)
    static double minSignificantPercentageOfGoodHits;
    //e.g: keyLength = 16 found hits indicating keyLength = 7 ("bad" ggT(7, 16) = 1)
    static double minSignificantPercentageOfBadHits;

    double defaultGood = 0.05;
    double defaultBad = 0.05;
    double defaultAverage = 1;
    double defaultTweaker = 0.01;
    private void initSigs() {
        String[] s;
        try {
            s = Util.MediaInderfaces.FileInderface.fileRead(System.getProperty("user.home") + "/sigs" + langCode + ".txt");
            colorPrintln("Loaded values from: " + System.getProperty("user.home") + "/sigs" + langCode + ".txt", Foregrounds.CYAN, Backgrounds.TRANSPARENT);
            aToConsFile("Loaded values from: " + System.getProperty("user.home") + "/sigs" + langCode + ".txt");
        } catch (Exception e) {
            Util.MediaInderfaces.FileInderface.fileWrite(System.getProperty("user.home") + "/sigs" + langCode + ".txt", new String[]{defaultGood + "", defaultBad + "", defaultAverage + "", defaultTweaker + ""});
            colorPrintln("Generated new document at: " + System.getProperty("user.home") + "/sigs" + langCode + ".txt", Foregrounds.RED, Backgrounds.TRANSPARENT);
            aToConsFile("Generated new document at: " + System.getProperty("user.home") + "/sigs" + langCode + ".txt");
            try {
                s = Util.MediaInderfaces.FileInderface.fileRead(System.getProperty("user.home") + "/sigs" + langCode + ".txt");
            } catch (IOException e1) {
                s = null;
            }
        }

        assert s != null : "Keine Rechte ein Dokument zu erstellen oder sonstiger Fehler";
        minSignificantPercentageOfGoodHits = Double.parseDouble(s[0]);
        minSignificantPercentageOfBadHits = Double.parseDouble(s[1]);
        durchschnitt = Double.parseDouble(s[2]);
        tweaker = Double.parseDouble(s[3]);
    }

    private static void onShutdown() {
        Util.MediaInderfaces.FileInderface.fileWrite(System.getProperty("user.home") + "/sigs" + langCode + ".txt", new String[]{minSignificantPercentageOfGoodHits + "", minSignificantPercentageOfBadHits + "", durchschnitt + "", tweaker + ""});
        colorPrintln("Don't forget to get your results from: " + System.getProperty("user.home") + "/sigs" + langCode + ".txt", Foregrounds.MAGENTA, Backgrounds.CYAN, Formats.BOLD, Formats.UNDERLINE);
        aToConsFile("Don't forget to get your results from: " + System.getProperty("user.home") + "/sigs" + langCode + ".txt");
        /*
        try {
            fileOpen(System.getProperty("user.home") + "/sigs" + langCode + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //consRead();
    }

    private String charseterize(String toC) {
        char[] c = toC.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (getInt(c[i]) < 0) {
                c[i] = '\u0000';
            }
        }

        String ret = "";
        for (char d:c) {
            if (d != '\u0000') {
                ret += d;
            }
        }

        return ret;
    }

    private HashMap<Character, Integer> häufigkeitsanalyse(String s) {
        char[] c = s.toCharArray();
        HashMap<Character, Integer> hm = new HashMap<>();
        for (char d:charSet) {
            int i = 0;
            for (char e:c) {
                if (e == d) {
                    i++;
                }
            }
            if (i != 0) {
                hm.put(d, i);
            }
        }
        return hm;
    }

    private Double kappa(String tt1, String tt2) {
        char[] t1 = tt1.toCharArray();
        char[] t2 = tt2.toCharArray();
        Double j = 0.0;
        for (int i = 0; i < t1.length; i++) {
            if (t1[i] == t2[i]) {
                j++;
            }
        }
        return j/t1.length;
    }

    private ArrayList<Double> kappaShift(String s) {
        ArrayList<Double> integerArrayList = new ArrayList<>();
        for (int i = 0; i < s.length() - 1; i++) {
            integerArrayList.add(kappa(s, s.substring(s.length() - (i + 1)) + s.substring(0, s.length() - (i + 1))));
        }
        return integerArrayList;
    }

    static double durchschnitt;
    double signifikanterWert = 0;
    int hmMax;
    int hm2Max;

    private Integer friedmanTest(String s) {
        hmMax = 0;
        hm2Max = 0;
        ArrayList<Double> al = kappaShift(s);
        for (double d : al) {
            signifikanterWert += d;
        }
        signifikanterWert /= al.size();
        signifikanterWert += 0.5 * signifikanterWert;
        signifikanterWert *= durchschnitt;
        HashMap<Integer, Integer> hm = new HashMap<>();
        int linesSinceLastStatisticalSignificantValue = 2; // Das muss so
        for (int i = 0; i < al.size(); i++) {
            ArrayList<Integer> alleAbständeVonDiesemWert = new ArrayList<>();
            if (al.get(i) > signifikanterWert) {
                linesSinceLastStatisticalSignificantValue = 2;
                for (int j = i + 1; j < al.size(); j++) {
                    if ((al.get(j) > signifikanterWert) && !alleAbständeVonDiesemWert.contains(linesSinceLastStatisticalSignificantValue)) {
                        alleAbständeVonDiesemWert.add(linesSinceLastStatisticalSignificantValue);
                        linesSinceLastStatisticalSignificantValue = 2;
                        j = i + 1;
                    } else {
                        linesSinceLastStatisticalSignificantValue++;
                    }
                }
            }
            for (int aABDWi : alleAbständeVonDiesemWert) {
                try {
                    hm.put(aABDWi, hm.get(aABDWi) + 1);
                } catch (Exception e) {
                    hm.put(aABDWi, 1);
                }
            }
        }
/*
        for (int m: hm.keySet()) {
            out.println(m + ": " + hm.get(m));
        }
*/
        Integer keyLength = -1;

        while (!hm.isEmpty()) {
            Map.Entry<Integer, Integer> me = hm.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get();
            if (hmMax == 0) {
                hmMax = me.getKey();
            } else if (hm2Max == 0) {
                hmMax = me.getKey();
            }
            boolean goodHit = false;
            if (keyLength != -1) {
                if (me.getKey() % keyLength == 0) {
                    hm.remove(me.getKey());
                    continue;
                } else if (keyLength % me.getKey() == 0) {
                    goodHit = true;
                }
            }
            if ((me.getValue() > (s.length() * minSignificantPercentageOfGoodHits) && goodHit) || (me.getValue() > (s.length() * minSignificantPercentageOfBadHits) && !goodHit)) {
                if (keyLength == -1) {
                    keyLength = me.getKey();
                    hm.remove(me.getKey());
                } else {
                    //keyLength = ggT(me.getKey(), keyLength); not the case, because keyLength could be due to noise
                    keyLength = me.getKey() < keyLength ? me.getKey() : keyLength;
                    hm.remove(me.getKey());
                }
            }
        }

        return keyLength;
    }

    private String vigenereHin(String toV, String key) {
        char[] v = toV.toCharArray();
        char[] k = key.toCharArray();
        for (int i = 0; i < v.length; i++) {
            v[i] = caesar(getInt(k[(i % (k.length))]), v[i] + "").toCharArray()[0];
        }
        String ret = "";
        for (char d : v) {
            ret += d;
        }
        return ret;
    }


    private String vigenereRück(String toV, String key) {
        char[] v = toV.toCharArray();
        char[] k = key.toCharArray();
        for (int i = 0; i < v.length; i++) {
            v[i] = brutus(getInt(k[(i % (k.length ))]), v[i] + "").toCharArray()[0];
        }

        String ret = "";
        for (char d:v) {
            ret += d;
        }
        return ret;
    }

    private char[] charSet = "abcdefghijklmnopqrstuvwxyzäöüßABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ1234567890 ;:,.-_!?\"'#+*~<>&()^".toCharArray(); //Better charset for a better terrible encryption
    //  private static char[] charSet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private Integer getInt(char c){
        for(int i = 0; i < charSet.length; i++){
            if(charSet[i] == c){
                return i;
            }
        }
        return -1;
    }

    private String caesar(int rot, String toC) {
        rot = rot % charSet.length;
        char[] c = toC.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (getInt(c[i]) < 0) {
                err.println("Illegal character entered. Starting to build more walls to keep 'em out. \nMake ⛄ great again! " + c[i]);
                return "⛄";
            }
            if (getInt(c[i]) + rot < 0) {
                c[i] = (charSet[charSet.length - (Math.abs((getInt(c[i]) + rot) % charSet.length))]);
            } else {
                c[i] = (charSet[(getInt(c[i]) + rot) % charSet.length]);
            }

        }
        toC = "";
        for (char d : c) {
            toC += d;
        }
        return toC;
    }

    private String brutus(int rot, String toC) {
        return caesar(-rot, toC);
    }

    private void manual() {
        out.print("Caesar/Brutus/Vigenere/De-Vigenere/Häufigkeitsanalyse/Kappa-Shift/Friedman-Test/Exit? [C/b/v/d/h/k/f/e]: ");
        switch (consRead().toLowerCase().trim()) {
            case "b":
                out.print("What to brutus? ");
                String s = consRead();
                out.print("'a' gets shifted to ");
                Integer i = getInt(consRead().toCharArray()[0]);
                assert i > 0 : "Illegal snowman exception ⛄";
                out.println("Brutused " + s + " to " + brutus(i, s));
                out.println("");
                manual();
                break;
            case "e":
                err.print("Process won't finish with exit code 0");
                System.exit(1337);
            case "v":
                out.print("What to vigenere? ");
                String sss = consRead();
                out.print("key: ");
                String iii = consRead();
                assert iii != null && !Objects.equals(iii, "") : "Illegal snowman exception ⛄";
                out.println("Vigenered " + sss + " to " + vigenereHin(sss, iii));
                out.println("");
                manual();
                break;
            case "d":
                out.print("What to de-vigenere? ");
                String ssss = consRead();
                out.print("key: ");
                String iiii = consRead();
                assert iiii != null && !Objects.equals(iiii, "") : "Illegal snowman exception ⛄";
                out.println("De-Vigenered " + ssss + " to " + vigenereRück(ssss, iiii));
                out.println("");
                manual();
                break;
            case "h":
                out.print("What to analyze? ");
                String sssss = consRead();
                out.println("Analyzed " + sssss + " : ");
                HashMap<Character, Integer> hm = häufigkeitsanalyse(sssss);
                for (Character c : hm.keySet()) {
                    out.println(c + ": " + hm.get(c));
                }
                out.println("");
                manual();
                break;
            case "k":
                out.print("What to shift? ");
                String ssssss = consRead();
                out.println("Shifted " + ssssss + " : ");
                ArrayList<Double> arrayList = kappaShift(ssssss);
                for (Double d : arrayList) {
                    out.println(d * 100 + "%");
                }
                out.println("");
                manual();
                break;
            case "f":
                out.print("What to friedman-test? ");
                String sssssss = consRead();
                out.println("friedman-tested: " + sssssss + ". Probable keylength: " + friedmanTest(sssssss));
                out.println("");
                manual();
                break;
            case "c":
            default:
                out.print("What to caesar? ");
                String ss = consRead();
                out.print("'a' is the result of a shift from ");
                Integer ii = getInt(consRead().toCharArray()[0]);
                assert ii > 0 : "Illegal snowman exception ⛄";
                out.println("Caesared " + ss + " to " + caesar(ii, ss));
                out.println("");
                manual();
                break;
        }
    }

    private static String langCode;
    public Magiedius(String langCode) {
        Magiedius.langCode = langCode;
    }

    public Magiedius() {
    }

    public static void main(String[] args){
        if (System.console() == null) {
            Runtime rt = Runtime.getRuntime();
            switch ((String) JOptionPane.showInputDialog(null, "Select mode:", "Mode select", JOptionPane.PLAIN_MESSAGE, null, new String[] {"learn DE", "learn EN", "learn NL", "learn LA", "manual", "cancel"}, "manual")) {
                case "learn DE": {try{rt.exec("cmd.exe /c cd \"" + Magiedius.class.getPackage() + "\" & start cmd.exe /k \"java -jar " + new File(Magiedius.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + " -learn 9001 de");}catch (Exception ex) {}}; break;
                case "learn EN": {try{rt.exec("cmd.exe /c cd \"" + Magiedius.class.getPackage() + "\" & start cmd.exe /k \"java -jar " + new File(Magiedius.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + " -learn 9001 en");}catch (Exception ex) {}}; break;
                case "learn NL": {try{rt.exec("cmd.exe /c cd \"" + Magiedius.class.getPackage() + "\" & start cmd.exe /k \"java -jar " + new File(Magiedius.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + " -learn 9001 nl");}catch (Exception ex) {}}; break;
                case "learn LA": {try{rt.exec("cmd.exe /c cd \"" + Magiedius.class.getPackage() + "\" & start cmd.exe /k \"java -jar " + new File(Magiedius.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + " -learn 9001 la");}catch (Exception ex) {}}; break;
                case "manual": {try{rt.exec("cmd.exe /c cd \"" + Magiedius.class.getPackage() + "\" & start cmd.exe /k \"java -jar " + new File(Magiedius.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + " -manual");}catch (Exception ex) {}}; break;
                default: System.exit(1337);
            }
           
        } else {
            for (String arg : args) {
                if (Objects.equals(arg, "-learn")) {
                    for (String ss : args) {
                        try {
                            int i = Integer.parseInt(ss);
                            for (String sss : args) {
                                if (Objects.equals(sss.toLowerCase().trim(), "de")) {
                                    Runtime.getRuntime().addShutdownHook(new Thread(Magiedius::onShutdown, "Shutdown-thread"));
                                    new Magiedius("DE").learn(i);
                                    break;
                                } else if (Objects.equals(sss.toLowerCase().trim(), "en")) {
                                    Runtime.getRuntime().addShutdownHook(new Thread(Magiedius::onShutdown, "Shutdown-thread"));
                                    new Magiedius("EN").learn(i);
                                    break;
                                } else if (Objects.equals(sss.toLowerCase().trim(), "nl")) {
                                    Runtime.getRuntime().addShutdownHook(new Thread(Magiedius::onShutdown, "Shutdown-thread"));
                                    new Magiedius("NL").learn(i);
                                    break;
                                } else if (Objects.equals(sss.toLowerCase().trim(), "la")) {
                                    Runtime.getRuntime().addShutdownHook(new Thread(Magiedius::onShutdown, "Shutdown-thread"));
                                    new Magiedius("LA").learn(i);
                                    break;
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                } else if (Objects.equals(arg, "-manual")) {
                    new Magiedius().manual();
                    break;
                }
            }
            colorPrintln("Wrong Syntax! Add\n-learn [maxTextLength] [languageCode (de/en/nl/la)] to automatically improve the algorithm using wikipedia pages of specified max length or\n-manual to start the manual mode of program", Foregrounds.RED, Backgrounds.TRANSPARENT, Formats.BOLD);
        }
}
}
