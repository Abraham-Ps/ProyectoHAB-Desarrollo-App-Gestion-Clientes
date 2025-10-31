package app.data;

import app.cliente.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ClienteData {
    //Aqui empleare las variables con su constructor

    private final EntityManager em;

    public ClienteData(EntityManager em) {
        this.em = em;
    }

    //CRUD
    public Cliente crear(Cliente cliente) {
        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();
        return cliente;
    }

    // esto lo que hace es que va a leer el cliente por id, y devuelve en optional que puede tener el cliente o estar vacio
    public Optional<Cliente> buscarPorId(Long id) {
        Cliente c = em.find(Cliente.class, id);
        return Optional.ofNullable(c);
    }

    //esta es la forma de actualizar un cliente existente y tiene merge lo que es sincronizar los cambios con la bd
    public Cliente actualizar(Cliente cliente) {
        em.getTransaction().begin();
        Cliente updatecliente = em.merge(cliente);
        em.getTransaction().commit();
        return updatecliente;
    }

    //basicamente esto lo que hace es eliminar un cliente por id si es que existe, primero lo busca y luego lo borra
    public void eliminar(long id) {
        em.getTransaction().begin();
        Cliente c = em.find(Cliente.class, id);
        if (c != null) {
            em.remove(c);
        }
        em.getTransaction().commit();
    }

    //Esto es el listado de todos los clientes ordenado por id usando JPQL
    public List<Cliente> listarTodos() {
        TypedQuery<Cliente> q = em.createQuery(
                "SELECT c FROM Cliente c ORDER BY c.id", Cliente.class); //Implemento JPQL en la entidad Cliente
        return q.getResultList();
    }

    // busca a los clientes por ciudad usando el parametro :ciudad
    public List<Cliente> buscarPorCiudad(String ciudad) {
        TypedQuery<Cliente> q = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.ciudad = :ciudad ORDER BY c.id", Cliente.class);
        q.setParameter("ciudad", ciudad);
        return q.getResultList();
    }

    // esta es la comprobacion de si existe un email duplicado. te devuelve un true si ya esta en la BD
    public boolean existeEmail(String email){
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE c.email = :email", Long.class);
        q.setParameter("email", email);
        Long total = q.getSingleResult(); // te devuelve el resultado
        return total != null && total > 0; // es true si el total te da 0
    }
}

