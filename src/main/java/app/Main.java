package app;

import app.data.ClienteData;
import app.menu.Menu;
import app.servicio.ClienteService;
import app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EntityManager em = JpaUtil.getEntityManager();
        //aplico un cierre safe de entity manager
        try {
            ClienteService servicio = new ClienteService(new ClienteData(em));
            Menu menu = new Menu(sc, servicio);
            System.out.println("\n\u001B[33mBienvenido al menu de gestion de clientes (" 
                    + "\u001B[31mEs" + "\u001B[33mpa" + "\u001B[31mña" 
                    + "\u001B[33m)\u001B[0m"); // Esto es mas que todo para que haga la alucion como si fuera la bandera de Spain
            menu.iniciar();
        } finally {
            em.close();
            JpaUtil.close();
        }
    }
}
