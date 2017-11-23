/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brututz.util;


import org.jsoup.Jsoup;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 *
 * @author Egidius Mysliwietz
 */
public class Util {

    //Destroying OOP at its finest...
    private static Util u = new Util();

    public static class MediaInderfaces {
        public static class SoundInderface {

            static Clip clip;

            public static void playSound(String name) {
                try {
                    URL url = u.getClass().getResource("/assets/music/"+ name +".wav");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            public static void loopSound(String name) {
                try {
                    URL url = u.getClass().getResource("/assets/music/"+ name +".wav");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Util.MediaInderfaces.SoundInderface.clip = AudioSystem.getClip();
                    Util.MediaInderfaces.SoundInderface.clip.open(audioIn);
                    Util.MediaInderfaces.SoundInderface.clip.loop(-1);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            public static void loopSound(String name, int times) {
                try {
                    URL url = u.getClass().getResource("/assets/music/"+ name +".wav");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Util.MediaInderfaces.SoundInderface.clip = AudioSystem.getClip();
                    Util.MediaInderfaces.SoundInderface.clip.open(audioIn);
                    Util.MediaInderfaces.SoundInderface.clip.loop(times);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            public static void stopSound() {
                Util.MediaInderfaces.SoundInderface.clip.close();
            }
        }
        public static class FileInderface {

            public static void fileOpen(String document) throws IOException {
                Desktop dt = Desktop.getDesktop();
                dt.open(new File(document));
            }

            public static void fileWrite(String file, String[] toPrint) {
                try {
                    FileWriter fw = new FileWriter(file);
                    PrintWriter pw = new PrintWriter(fw);
                    for (String toPrint1 : toPrint) {
                        pw.println(toPrint1);
                    }
                    pw.close();
                }
                catch (IOException e) {
                    System.err.println("Write: Le Errör!");
                }
            }

            public static void fileWrite(String file, Object[] toPrint) {
                try {
                    FileWriter fw = new FileWriter(file);
                    PrintWriter pw = new PrintWriter(fw);
                    for (Object toPrint1 : toPrint) {
                        pw.println(toPrint1.toString());
                    }
                    pw.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Write: Le Errör!");
                }
            }

            public static String[] fileRead(String file) throws IOException {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    int lines = 0;
                    while (br.readLine() != null) {
                        lines++;
                    }
                    br.close();
                    fr = new FileReader(file);
                    br = new BufferedReader(fr);
                    String[] storeInto = new String[lines];
                    String current;
                    int i=0;
                    while ((current = br.readLine()) != null && i<storeInto.length) {
                        storeInto[i]=current;
                        i++;
                    }
                    return storeInto;
                }
                catch (IOException e) {
                    throw e;
                }
            }
            
            public static void appendToFile(String file, String[] toWrite) {
                try(FileWriter fw = new FileWriter(file, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw)) {
                    for (String s:toWrite) {
                        pw.println(s);
                    }
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }
            }

            public static boolean fileCheckFor(String file, String checkIfThisIsInFile) {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    int lines = 0;
                    while (br.readLine() != null) {
                        lines++;
                    }
                    br.close();
                    fr = new FileReader(file);
                    br = new BufferedReader(fr);
                    boolean returnvalue = false;
                    for (int i = 0; i < lines; i++) {
                        if (br.readLine().contains(checkIfThisIsInFile)) {
                            returnvalue = true;
                        }
                    }
                    return returnvalue;
                } catch (IOException e) {
                    System.err.println("checkFor: Le Errör in findings of file!");
                    return false;
                }
            }
        }
        public static class ConsoleInderface {
            static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            public static String consRead() {
                String input = "";
                try {
                    input = br.readLine();
                } catch (IOException e) {}
                return input;
            }

            public static class Foregrounds {
                public static int BLACK = 30;
                public static int RED = 31;
                public static int GREEN = 32;
                public static int YELLOW = 33;
                public static int BLUE = 34;
                public static int MAGENTA = 35;
                public static int CYAN = 36;
                public static int WHITE = 37;
            }
            public static class Backgrounds {
                public static int BLACK = 40;
                public static int RED = 41;
                public static int GREEN = 42;
                public static int YELLOW = 43;
                public static int BLUE = 44;
                public static int MAGENTA = 45;
                public static int CYAN = 46;
                public static int WHITE = 47;
                public static int TRANSPARENT = -1;
            }
            public static class Formats {
                public static int BOLD = 1;
                public static int STOP_BOLD = 21;
                public static int UNDERLINE = 4;
                public static int STOP_UNDERLINE = 24;
                public static int CLEAR_ALL = 0;
            }
            public static void colorPrint(String message, int foreground, int background, Integer... format) {
                for (int i : format) {
                    out.print((char)27 + "[ " + i + "m");
                }
                if (background == Backgrounds.TRANSPARENT) {
                    out.print((char)27 + "[" + foreground + "m" + message);
                } else {
                    out.print((char)27 + "[" + foreground + ";" + background + "m" + message);
                }
                out.print((char)27 + "[0m");
            }
            public static void colorPrintln(String message, int foreground, int background, Integer... format) {
                for (int i : format) {
                    out.print((char)27 + "[ " + i + "m");
                }
                if (background == Backgrounds.TRANSPARENT) {
                    out.print((char)27 + "[" + foreground + "m" + message);
                } else {
                    out.print((char)27 + "[" + foreground + ";" + background + "m" + message);
                }
                out.println((char)27 + "[0m");
            }
        }
        public static class WikipediaInderface {
            public static HashMap<String, String> getNewWikipediaPage(String language) {
                org.jsoup.nodes.Document doc = null;
                if (Objects.equals(language.toLowerCase().trim(), "de") || Objects.equals(language.toLowerCase().trim(), "deutsch")) {
                    try {
                        doc = Jsoup.connect("https://de.wikipedia.org/wiki/Spezial:Zuf%C3%A4llige_Seite").get();
                    } catch (IOException e) {
                        out.println("Blocked by firewall. Blame Heldpfoff! (This may also be caused by your website not existing, still it's best to just blame someone)");
                        return getNewWikipediaPage(language);
                    }
                } else if (Objects.equals(language.toLowerCase().trim(), "en") || Objects.equals(language.toLowerCase().trim(), "english")){
                    try {
                        doc = Jsoup.connect("https://en.wikipedia.org/wiki/Special:Random").get();
                    } catch (IOException e) {
                        out.println("Blocked by firewall. Blame Heldpfoff! (This may also be caused by your website not existing, still it's best to just blame someone)");
                        return getNewWikipediaPage(language);
                    }
                } else if (Objects.equals(language.toLowerCase().trim(), "nl") || Objects.equals(language.toLowerCase().trim(), "nederlands")){
                    try {
                        doc = Jsoup.connect("https://nl.wikipedia.org/wiki/Speciaal:Willekeurig").get();
                    } catch (IOException e) {
                        out.println("Blocked by firewall. Blame Heldpfoff! (This may also be caused by your website not existing, still it's best to just blame someone)");
                        return getNewWikipediaPage(language);
                    }
                } else if (Objects.equals(language.toLowerCase().trim(), "la") || Objects.equals(language.toLowerCase().trim(), "latina")){
                    try {
                        doc = Jsoup.connect("https://la.wikipedia.org/wiki/Specialis:Pagina_fortuita").get();
                    } catch (IOException e) {
                        out.println("Blocked by firewall. Blame Heldpfoff! (This may also be caused by your website not existing, still it's best to just blame someone)");
                        return getNewWikipediaPage(language);
                    }
                }
                HashMap<String, String> hm = new HashMap<>();
                hm.put("title", doc.title());
                hm.put("h1", doc.getElementsByTag("h1").get(0).text());
                hm.put("text", doc.getElementById("mw-content-text").text());

                return hm;
            }
        }
    }

    public static class WIPUtil {
        public static void wip() {
            JOptionPane.showMessageDialog(null, "Wörk in prögress", "Stüff nöt wörking yet", JOptionPane.ERROR_MESSAGE, new ImageIcon(u.getClass().getResource("/assets/img/util/wörk.png")));
        }
    }

    public static class MathUtil {

        public static String convertTimeFromMilSecsToReadable(Long time) {
            int minutes = (int) (time/60000);
            time -= minutes*60000;
            int seconds = (int) (time/1000);
            time -= seconds*1000;
            return minutes+":"+seconds+":"+time;
        }

        public static int randomBetween(int min, int max) {
            return min + (int)(Math.random() * ((max - min) + 1));
        }

        public static int ggT(int a, int b) {
            if (a==b) return(a);
            else
            {
                if (a>b) return(ggT(a-b,b));
                else return(ggT(b-a,a));
            }
        }

        //Use like this [arg1] is closer to [arg2] than [arg3]. e.g: 5 is closer to 4 than 9, so return=true
        public static boolean closerToThan(double firstCanidate, double whatIsItCloseTo, double secondCanidate) {
            return (Math.abs(firstCanidate - whatIsItCloseTo) < Math.abs(secondCanidate - whatIsItCloseTo));
        }
    }

    public static class GUIHelper {
        public static class JPaneHelper {

            public static void appendToPane(JTextPane tp, String msg, Color c, String font) {
                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

                aset = sc.addAttribute(aset, StyleConstants.FontFamily, font);
                aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

                int len = tp.getDocument().getLength();
                tp.setCaretPosition(len);
                tp.setCharacterAttributes(aset, false);
                tp.replaceSelection(msg);
            }

            public static void writeToPane(JTextPane tp, String msg, Color c, String font) {
                tp.setText("");
                appendToPane(tp, msg, c, font);
            }

            public static void centerPane(JTextPane tp) {
                StyledDocument doc = tp.getStyledDocument();
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
            }
        }
        public static class FrameHelper {
            public static ArrayList<Component> getAllComponents(Container c) {
                Component[] comps = c.getComponents();
                ArrayList<Component> compList = new ArrayList();
                for (Component comp : comps) {
                    compList.add(comp);
                    if (comp instanceof Container)
                        compList.addAll(getAllComponents((Container) comp));
                }
                return compList;
            }

            public static void scaleFrame(Container c) {
                //NOT WORKING!!! WHAT ABOUT ICON UPDATES???
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

                double a = d.width / c.getWidth();
                double b = d.height / c.getHeight();

                c.setBounds(0, 0, d.width, d.height);

                for (Component comp : getAllComponents(c)) {
                    int cx = comp.getX();
                    int cy = comp.getY();

                    int cw = comp.getWidth();
                    int ch = comp.getHeight();

                    comp.setBounds((int) (a * cx), (int) (b * cy), (int) (a * cw), (int) (b * ch));
                    if (comp instanceof JLabel) {
                        JLabel jl = (JLabel) comp;
                        if (((ImageIcon) jl.getIcon()) != null) {
                            jl.setIcon(new ImageIcon(((((ImageIcon) jl.getIcon()).getImage().getScaledInstance((int) (a * cw), (int) (b * ch),java.awt.Image.SCALE_SMOOTH)))));
                        }
                    }
                }
            }
        }
        public static class IconHelper {

            public static ImageIcon createIcon(Color main, int width, int height) {
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                graphics.setColor(main);
                graphics.fillRect(0, 0, width, height);
                graphics.setXORMode(Color.DARK_GRAY);
                graphics.drawRect(0, 0, width-1, height-1);
                image.flush();
                ImageIcon icon = new ImageIcon(image);
                return icon;
            }
        }
        public static class WindowSetter {

            public static void centerWindow(Window frame) {
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
                int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
                out.println(dimension.getSize());
                //       setLocationRelativeTo(null); ODER SO
                frame.setLocation(x, y);
            }

            public static void setWindow(Window frame, double xProz, double yProz) {
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int) ((dimension.getWidth() - frame.getWidth()) * (xProz / 100));
                int y = (int) ((dimension.getHeight() - frame.getHeight()) * (yProz /100));
                frame.setLocation(x, y);
            }
        }
    }

    public static class WebHelper {
        public static void openUrl(String url) {
            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException ex) {
                err.println("Le 'rrör //Danke Marcel...");
            }
        }
    }

}
