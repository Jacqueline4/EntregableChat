/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entregablechat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import mensajes.Mensaje;

/**
 *
 * @author dev
 */
public class ObjetoCompartido {

    HashMap<String, Socket> mapSockets = new HashMap<>();
    HashMap<String, ArrayList<String>> mapCanales = new HashMap<>();

  
    //si hay un cierre de usuario, quitar tanto del map de Sockets y del AL de usuarios de del mapCanales -->hecho falta comprobar

    
    //revisar si el deleteChannel es correcto
    public synchronized boolean createChannel(String channelName, String user) {
        if (this.mapCanales.containsKey(channelName)) {
            return false;
        } else {
            this.mapCanales.put(channelName, new ArrayList<>());
            //this.joinChannel(channelName, user);
            this.mapCanales.get(channelName).add(user);
            return true;
        }
    }

    public synchronized void joinChannel(String channelName, String user) {
        if (this.mapCanales.containsKey(channelName)) {
            ArrayList<String> uCanal = this.mapCanales.get(channelName);
            if (uCanal.contains(user)) {                                // no se una con join a un canal existente
                //uCanal.add(user); en la condicion-->!uCanal.contains(user)
//                return true;
            } else {
                uCanal.add(user);
            }

        }
//        return false;      
    }

    public synchronized void leaveChannel(String channelName, String user) {

        for (Map.Entry<String, ArrayList<String>> entry : mapCanales.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> listadoUsers = entry.getValue();
            if (key.equalsIgnoreCase(channelName)) {
                ///Forma 1 => añadir los otros users
                ArrayList<String> aux = new ArrayList<String>();
                for (String s : listadoUsers) {
                    if (s.equalsIgnoreCase(user)) {
                        //val.remove(s);  -> borrar usuario
                    } else {
                        aux.add(s);
                    }
                }
                listadoUsers = aux;
                mapCanales.put(key, listadoUsers);
            }
        }

    }

//    public synchronized void deleteChannel(String name) {
//        try {
//            this.mapCanales.get(name).clear();
//        } catch (Exception ex) {
//            System.out.println("Error cerrar/borrar canal");
//        }
//        this.mapCanales.remove(name);
//    }
    public synchronized String listChannels() {
        String resultado = "Listado de Canales:\n";
        for (String string : this.mapCanales.keySet()) {
            resultado = resultado + string + "\n";
        }
        return resultado;

    }

    public synchronized String listChannelUsers(String user) {
        String resultado = user + " está unido en los siguientes canales:\n";
        Set<String> nombresCanales = null;

        for (Map.Entry<String, ArrayList<String>> entry : mapCanales.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> val = entry.getValue();
            for (String userAL : val) {
                if (userAL.equalsIgnoreCase(user)) {
                    nombresCanales = mapCanales.keySet();
                    // nombresCanales= nombresCanales+"\n";
                    resultado = resultado + key + "\n";
                }
            }

        }

        return resultado;

    }

    public synchronized boolean addUser(String name, Socket cliente) {
        if (this.mapSockets.containsKey(name)) {
            return false;
        } else {
            this.mapSockets.put(name, cliente);
            return true;
        }
    }

    public synchronized boolean existUser(String name) {
        return this.mapSockets.containsKey(name);
    }

    public synchronized void deleteUser(String name) {//HAY QUE SACAR AL USER DE LOS CANALES-->lo hace cuando .exit pero no en cierre abrupto
         ArrayList<String> aux;
        try {
            this.mapSockets.get(name).close();
            for (Map.Entry<String, ArrayList<String>> entry : mapCanales.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> val = entry.getValue();
                aux= new ArrayList<>();
                for (String user : val) {
                    if(!user.equalsIgnoreCase(name)){
                        aux.add(user);
                    }
                }
                 mapCanales.put(key, aux);

            }
            
            for (Map.Entry<String, ArrayList<String>> entry : mapCanales.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> val = entry.getValue();
                System.out.println("canal: "+ key + "listado: ");
                for (String s : val) {
                    System.out.println(s);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error cerrar/borrar cliente");
        }
        this.mapSockets.remove(name);
    }

    public synchronized void enviarSms(Mensaje m, String oldName) {
        ArrayList<String> usuariosBorrar = new ArrayList<>();// este list es para guardar los usuarios a borrar ya que no podemos hacerlo directamente en el for del Map ya que se recorre ese mapa
        ObjectOutputStream oos;
        for (Map.Entry<String, Socket> entry : mapSockets.entrySet()) {
            String key = entry.getKey();
            Socket val = entry.getValue();
            if (!key.equals(oldName)) {
                try {
                    oos = new ObjectOutputStream(val.getOutputStream());
                    oos.writeObject(m);
                } catch (IOException ex) {
                    usuariosBorrar.add(key);//
                }
            }
        }
        for (String keyBorrar : usuariosBorrar) {//
            this.deleteUser(keyBorrar);
        }
    }

    public synchronized void enviarMensajePrivado(Mensaje m) {
        String sms;
        Socket SocketDestino = this.mapSockets.get(m.getDestinoName());
        ObjectOutputStream oos;
        if (SocketDestino != null) {
            try {
                oos = new ObjectOutputStream(SocketDestino.getOutputStream());
                sms = "Mensaje privado de " + m.getName() + " --> " + m.getSms();
                Mensaje mensjeInterno = new Mensaje(m.getName(), sms, m.getDestinoName());
                oos.writeObject(mensjeInterno);

            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }

    public synchronized void enviarMensajeCanal(String origen, Mensaje m, String nomreCanalDestino) {
        String sms;
        ArrayList<String> usuariosBorrar = new ArrayList<>();
        ObjectOutputStream oos;
        Socket SocketDestino;
        ArrayList<String> destinatarios = mapCanales.get(nomreCanalDestino);
        for (String destinatario : destinatarios) {
            SocketDestino = this.mapSockets.get(destinatario);

            if (SocketDestino != null) {
                try {
                    oos = new ObjectOutputStream(SocketDestino.getOutputStream());
                    sms = "Mensaje canal " + nomreCanalDestino + " de " + m.getName() + " --> " + m.getSms();
                    Mensaje mensjeInterno = new Mensaje(m.getName(), sms, destinatario);
                    oos.writeObject(mensjeInterno);
//                 oos.writeObject(m);
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }

        }
    }

    public synchronized String listUsers() {
        String resultado = "Listado de Usuarios:\n";
        for (String string : this.mapSockets.keySet()) {
            resultado = resultado + string + "\n";
        }
        return resultado;

    }

    public String help() {

        String resultado = "COMANDO \t\t\t Descripción \n";
        resultado = resultado + "--------------------------------------------------------------------------------\n";
        resultado = resultado + ".exit \t\t\t Salir \n";
        resultado = resultado + ".listUsers \t\t\t Listado de los usuarios conectados \n";
        resultado = resultado + ".private destinatario message \t\t\t enviar un mensaje privado \n";
        resultado = resultado + ".listChannels \t\t\t listado de los canales existentes \n";
        resultado = resultado + ".listMyChannels \t\t\t listado de los canales a los que se ha unido el cliente \n";
        resultado = resultado + ".join channelName \t\t\t para unirse a un canal \n";
        resultado = resultado + ".channel channelName message \t\t\t enviar un mensaje a un canal \n";  //(debería comprobar si estas unido a ese canal previamente) FALTA!!!
        resultado = resultado + ".leave channelName \t\t\t para abandonar un canal \n";
        resultado = resultado + ".createChannel channelName \t\t\t para crear un canal \n";
        return resultado;
    }
}
