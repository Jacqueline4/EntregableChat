/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mensajes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author dev
 */
public class Mensaje implements Serializable{
    private String name;
    private String sms;
    private String destinoName;
    private ArrayList<String> tipo= new ArrayList<>();
    

    public Mensaje(String name, String sms) {
        this.name = name;
        this.sms = sms;
    }

    public Mensaje(String name, String sms, String destinoName) {
        this.name = name;
        this.sms = sms;
        this.destinoName = destinoName;
    }
    

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinoName() {
        return destinoName;
    }

    public ArrayList<String> getTipo() {
        return tipo;
    }

   
    
}
