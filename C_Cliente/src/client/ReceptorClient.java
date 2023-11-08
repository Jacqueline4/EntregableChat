/*
este controla la reception
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mensajes.Mensaje;

/**
 *
 * @author dev
 */
public class ReceptorClient extends Thread {

    Socket s;

    public ReceptorClient(Socket s) {
        this.s = s;
    }

    public void run() {
        ObjectInputStream ois = null;
        Mensaje m;
        try {
            do {
                ois = new ObjectInputStream(this.s.getInputStream());
                m = (Mensaje) ois.readObject();
                if (m.getName().equalsIgnoreCase("")) {
                    System.out.print(m.getSms() + "\n------>");

                } else {
                    System.out.print(m.getName() + ": " + m.getSms() + "\n");
                }
            } while (true);
        } catch (Exception ex) {
//            System.out.println("Error al crear el ois del cliente receptor");
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    System.out.println("Error cerrar ois cliente");
                }
            }
            try {
                this.s.close();
            } catch (IOException ex) {
                System.out.println("Error cerrar el socket del cliente");
            }
        }

    }
}
