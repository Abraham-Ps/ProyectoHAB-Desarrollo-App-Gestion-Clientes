package app.util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

    public class JpaUtil {

        // ponemos la variable
        private static EntityManagerFactory emf;

        // indicamos el metodo
        public static EntityManager getEntityManager() {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("cliente");
            }
            return emf.createEntityManager();
        }

        public static void close() {
            if (emf != null) {
                emf.close();
            }
        }
    }
