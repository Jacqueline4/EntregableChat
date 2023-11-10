/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import mensajes.Mensaje;

/**
 * este controla la emision
 *
 * @author dev
 */
public class Client {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String txtoSms = "";
        Mensaje m;
        ObjectOutputStream oos = null;
        String nickName;
        ObjectInputStream ois = null;
        boolean libre = false;

        try (Socket s = new Socket("localhost", 6666)) {
            do {
                System.out.println("Introduce el nickname: ");
                nickName = sc.nextLine();

                //comprobacion en servidor del nickname
                m = new Mensaje("", nickName);// aqui me da igual quien manda el mesaje sino el mensaje en si

                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(m);
                ois = new ObjectInputStream(s.getInputStream());
                libre = (Boolean) ois.readObject();
                if (libre == false) {
                    System.out.println("Nickname ocupado");
                }
                //
            } while (libre == false);

            ReceptorClient cr = new ReceptorClient(s);
            cr.start();

            do {
                System.out.print("-> ");
                txtoSms = sc.nextLine();
                //para el.private se puede declarar String s= ".private pepe hola tu";
                // String []trozos= s.split(" ",3); // esto lo que hace es separarte en 3, serian 2 espacios

                String[] trozos = txtoSms.split(" ", 3);
                if (trozos[0].equalsIgnoreCase(".private")) {
                    String destino = trozos[1];
                    m = new Mensaje(nickName, txtoSms, destino);

                } else if (trozos[0].equalsIgnoreCase(".channel")) {
                    String destino = trozos[1];
                    m = new Mensaje(nickName, txtoSms, destino);
                    
                } else if (trozos[0].equalsIgnoreCase(".listUsers")) {
                   
                    m = new Mensaje("", txtoSms);
                    
                }else if (trozos[0].equalsIgnoreCase(".help")) {
                   
                    m = new Mensaje("", txtoSms);
                    
                }else {
                    m = new Mensaje(nickName, txtoSms);
                }
//                m.getName();
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(m);

            } while (!txtoSms.equals(".exit"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    System.out.println(" ");
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    System.out.println(" ");
                }
            }
        }
    }

}
