/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entregablechat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import mensajes.Mensaje;

/**
 * METODO OK/KO
 *
 * @author dev
 */
public class HiloServer extends Thread {

    ObjetoCompartido c;
    Socket cliente;
    String name;

    public HiloServer(ObjetoCompartido c, Socket cliente, String name) {
        this.c = c;
        this.cliente = cliente;
        this.name = name;
    }

    public void run() {//lee y cierra        

        Mensaje m;
        String mensaje = "";
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        boolean libre = false;
        String nicknameCandidato;
        String mensajeList;
        Mensaje privateMsg = null;
        System.out.println("Cliente conectado: " + this.name + " Socket ---->" + this.cliente);
        try {
            //conversación para dar de alta
            do {
                ois = new ObjectInputStream(this.cliente.getInputStream());
                m = (Mensaje) ois.readObject();
                nicknameCandidato = m.getSms();
                System.out.println("Cliente envia nickname candidato " + nicknameCandidato);
                libre = c.addUser(nicknameCandidato, cliente);
                System.out.println("Esta libre?: " + libre);
                oos = new ObjectOutputStream(this.cliente.getOutputStream());
                oos.writeObject(libre);

            } while (libre == false);
            this.name = nicknameCandidato;

            //Flujo normal hasta el .exit            
            do {
                try {
                    if (cliente != null) {
//                        System.out.println(name+" isBound: "+cliente.isBound());
//                        System.out.println(name+" isClosed: "+cliente.isClosed());
//                        System.out.println(name+" isConnected: "+cliente.isConnected());
//                        System.out.println(name+" isInputShutdown: "+cliente.isInputShutdown());
//                        System.out.println(name+" hashCode: "+cliente.hashCode());
//                        System.out.println(name+" getPort: "+cliente.getPort());
//                        System.out.println(name+" getReceiveBufferSize: "+cliente.getReceiveBufferSize());
//                        System.out.println(name+" getLocalPort: "+cliente.getLocalPort());
                        ois = new ObjectInputStream(cliente.getInputStream());
                    } else {
                        System.out.println("cliente es null");
                    }

                } catch (IOException e) {
//                    System.err.println(e);
//                    System.err.println(name + "--  " + Arrays.toString(e.getStackTrace()) + " " + e.getMessage());
                }
                m = (Mensaje) ois.readObject();

                System.out.println("Mensaje recibido " + m.getName() + "----> " + m.getSms());
                mensaje = m.getSms();
                if (mensaje.equalsIgnoreCase(".listUsers")) {
                    mensajeList = c.listUsers();
                    oos = new ObjectOutputStream(this.cliente.getOutputStream());
                    m = new Mensaje(mensajeList, name);////en sms es name porque devuelve el listado de usuarios conectados
                    oos.writeObject(m);
                } else if (mensaje.startsWith(".private")) {
                    String[] parts = mensaje.split(" ", 3);
                    if (parts.length >= 3) {
                        String destino = parts[1];
                        String privateMessage = parts[2];
                        privateMsg = new Mensaje(name, privateMessage, destino);
//                        privateMsg.setName(destino);
                        c.enviarMensajePrivado(privateMsg);
                    }
                } else if (mensaje.startsWith(".createChannel")) {
                    String[] parts = mensaje.split(" ", 2);

                    String channelName = parts[1];
                    c.createChannel(channelName, name);
                } else if (mensaje.startsWith(".join")) {
                    String[] parts = mensaje.split(" ", 2);

                    String channelName = parts[1];
                    c.joinChannel(channelName, name);
                } else if (mensaje.startsWith(".channel")) { 
                    String[] parts = mensaje.split(" ", 3);
                    if (parts.length >= 3) {
                     String canalDestino = parts[1];
                        String privateMessage = parts[2];
                        privateMsg = new Mensaje(name, privateMessage, canalDestino);
                        c.enviarMensajeCanal(name, privateMsg, canalDestino);
                    }
                } else if (mensaje.startsWith(".leave")) {
                    String[] parts = mensaje.split(" ", 2);

                    String channelName = parts[1];
                    c.leaveChannel(channelName, name);

                } else if (mensaje.startsWith(".listChannels")) {
                    mensajeList = c.listChannels();
                    oos = new ObjectOutputStream(this.cliente.getOutputStream());
                    m = new Mensaje(mensajeList, "");////revisar que salida tiene aqui name
                    oos.writeObject(m);
                } else if (mensaje.startsWith(".listMyChannels")) { //da error si aun no esta en ninguna canal-- cierre abrupto
                    mensajeList = c.listChannelUsers(name);
                    oos = new ObjectOutputStream(this.cliente.getOutputStream());
                    m = new Mensaje(mensajeList, "");////revisar que salida tiene aqui name
                    oos.writeObject(m);
                } else if (mensaje.equalsIgnoreCase(".help")) {
                    mensajeList = c.help();
                    oos = new ObjectOutputStream(this.cliente.getOutputStream());
                    m = new Mensaje(mensajeList, "");////en sms es name porque devuelve el listado de usuarios conectados
                    oos.writeObject(m);

                } else if (!mensaje.equals(".exit")) {
//                    m.setName(this.name);// lo borraremos cuando ya sepamos su name
                    c.enviarSms(m, this.name);
                }
            } while (!mensaje.equals(".exit"));

            System.out.println("Cierre controlado del cliente: " + name);
        } catch (Exception ex) {
            System.out.println(name + "-- Cierre abrupto ");
        } finally {
            c.deleteUser(this.name);
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    System.out.println("Error cierre ois");
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    System.out.println("Error cierre ois");
                }
            }
        }

    }
}
