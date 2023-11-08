/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entregablechat;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import mensajes.Mensaje;

/**
 *
 * @author dev
 */
public class Server {

    public static void main(String[] args) {
        ObjetoCompartido c = new ObjetoCompartido();
        Socket cliente;
        int id = 0;
        try(ServerSocket ss= new ServerSocket(6666)){
            do {
                cliente=ss.accept();
                id++;   
                HiloServer hs= new HiloServer(c, cliente, Integer.toString(id));
                hs.start();                                
            } while (true);
        }catch(Exception e)
        {
//            System.out.println(e.getMessage());
                    
        }
    }

}
