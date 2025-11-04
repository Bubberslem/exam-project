package dat.config;


import dat.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


public class Populate {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


        try (EntityManager em = emf.createEntityManager())  {
            em.getTransaction().begin();



            em.getTransaction().commit();
        }
    }
}