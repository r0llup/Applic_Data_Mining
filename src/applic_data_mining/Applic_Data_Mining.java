/**
 * Applic_Data_Mining
 *
 * Copyright (C) 2012 Sh1fT
 *
 * This file is part of Applic_Data_Mining.
 *
 * Applic_Data_Mining is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * Applic_Data_Mining is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Applic_Data_Mining; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package applic_data_mining;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import utils.Md5;
import utils.PropertiesLauncher;

/**
 * Manage a {@link Applic_Data_Mining}
 * @author Sh1fT
 */
public class Applic_Data_Mining {
    private PropertiesLauncher propertiesLauncher;

    /**
     * Create a new {@link Applic_Data_Mining} instance
     */
    public Applic_Data_Mining() {
        this.setPropertiesLauncher(new PropertiesLauncher(
            System.getProperty("file.separator") + "properties" +
            System.getProperty("file.separator") + "Applic_Data_Mining.properties"));
    }

    /**
     * Execute a Command
     * @param args
     * @return 
     */
    public Object sendCmd(String[] args) {
        try {
            Socket socket = new Socket(this.getServerAddress(), this.getServerPort());
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String assembledCmd = "";
            for (String arg : args)
                assembledCmd += arg + ":";
            pw.println(assembledCmd);
            Object response = ois.readObject();
            ois.close();
            pw.close();
            socket.close();
            return response;
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
        return null;
    }

    public PropertiesLauncher getPropertiesLauncher() {
        return propertiesLauncher;
    }

    public void setPropertiesLauncher(PropertiesLauncher propertiesLauncher) {
        this.propertiesLauncher = propertiesLauncher;
    }

    public Properties getProperties() {
        return this.getPropertiesLauncher().getProperties();
    }

    public String getServerAddress() {
        return this.getProperties().getProperty("serverAddress");
    }

    public Integer getServerPort() {
        return Integer.parseInt(this.getProperties().getProperty("serverPort"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Applic_Data_Mining aa = new Applic_Data_Mining();
            String cmd = ".4S&v";
            String cmdTemp = "";
            Object response = null;
            String name = "";
            String password = "";
            String year = "";
            String activity = "";
            String graphType = "";
            Boolean logged = false;
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            do {
                if (cmd.equals(".4S&v")) {
                    System.out.println("Veuillez entrer une commande: ");
                    cmd = br.readLine();
                }
                if (!logged) {
                    cmdTemp = cmd;
                    cmd = "LOGIN";
                }
                switch (cmd) {
                    case "LOGIN":
                        System.out.println("~ Identification ~");
                        System.out.println("Name: ");
                        name = br.readLine();
                        System.out.println("Password: ");
                        password = Md5.encode(br.readLine());
                        response = aa.sendCmd(new String[] {cmd, name, password});
                        switch ((String) response) {
                            case "OK":
                                System.out.println("Login successfully executed :)");
                                logged = true;
                                if (!cmdTemp.equals(""))
                                    cmd = cmdTemp;
                                else
                                    cmd = ".4S&v";
                                break;
                            case "KO":
                                System.out.println("Login not successfully executed :(");
                                cmd = ".4S&v";
                                break;
                            default:
                                break;
                        }
                       break;
                    case "GET_STAT_DESCR_ACTIV":
                        System.out.println("~ Get Stat Descr Activ ~");
                        System.out.println("Year: ");
                        year = br.readLine();
                        System.out.println("Activity: ");
                        activity = br.readLine();
                        response = aa.sendCmd(new String[] {cmd, year, activity});
                        System.out.println("Registrations: " + response);
                        cmd = ".4S&v";
                        break;
                    case "GET_GR_ACTIV_COMP":
                        System.out.println("~ Get Gr Activ Comp ~");
                        System.out.println("Year: ");
                        year = br.readLine();
                        System.out.println("Graph type: ");
                        graphType = br.readLine();
                        response = aa.sendCmd(new String[] {cmd, year, graphType});
                        if (response instanceof ImageIcon) {
                            Image image = ((ImageIcon) response).getImage();
                            RenderedImage rendered = null;
                            if (image instanceof RenderedImage)
                                rendered = (RenderedImage)image;
                            else {
                                BufferedImage buffered = new BufferedImage(
                                    ((ImageIcon) response).getIconWidth(),
                                    ((ImageIcon) response).getIconHeight(),
                                    BufferedImage.TYPE_INT_RGB);
                                Graphics2D g = buffered.createGraphics();
                                g.drawImage(image, 0, 0, null);
                                g.dispose();
                                rendered = buffered;
                            }
                            ImageIO.write(rendered, "PNG", new File("img" +
                                new Random().nextInt(999) + ".png"));
                        }
                        cmd = ".4S&v";
                        break;
                    case "GET_GR_ACTIV_EVOL":
                        System.out.println("~ Get Gr Activ Evol ~");
                        System.out.println("Year: ");
                        year = br.readLine();
                        response = aa.sendCmd(new String[] {cmd, year});
                        if (response instanceof ImageIcon) {
                            Image image = ((ImageIcon) response).getImage();
                            RenderedImage rendered = null;
                            if (image instanceof RenderedImage)
                                rendered = (RenderedImage)image;
                            else {
                                BufferedImage buffered = new BufferedImage(
                                    ((ImageIcon) response).getIconWidth(),
                                    ((ImageIcon) response).getIconHeight(),
                                    BufferedImage.TYPE_INT_RGB);
                                Graphics2D g = buffered.createGraphics();
                                g.drawImage(image, 0, 0, null);
                                g.dispose();
                                rendered = buffered;
                            }
                            ImageIO.write(rendered, "PNG", new File("img" +
                                new Random().nextInt(999) + ".png"));
                        }
                        cmd = ".4S&v";
                }
            } while (cmd != "");
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}