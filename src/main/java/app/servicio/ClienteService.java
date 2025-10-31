package app.servicio;

import app.cliente.Cliente;
import app.data.ClienteData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// capa del servicio, valida los datos y llama a ClienteData
public class ClienteService {

    private final ClienteData data;

    public ClienteService(ClienteData data) {
        this.data = data;
    }

    // alta de cliente y validaciones
    public Cliente altaCliente(String nombre, String apellidos, String sexo, String ciudad, LocalDate fechaNacimiento, String telefono, String email){

        List<String> errores = new ArrayList<>();

        // validaciones de los campos obligatorios
        if (nombre == null || nombre.isBlank()) errores.add("NOMBRE OBLIGATORIO");
        if (apellidos == null || apellidos.isBlank()) errores.add("APELLIDOS OBLIGATORIO");
        if (sexo == null || !(sexo.equalsIgnoreCase("M") || (sexo.equalsIgnoreCase("F")))) errores.add("EL SEXO DEBE SER M O F");
        if (ciudad == null || ciudad.isBlank()) errores.add("CIUDAD OBLIGATORIO");

        // telefono espaÑa exactamente 9 digitos
        if (telefono == null || telefono.isBlank()) {
            errores.add("TELEFONO OBLIGATORIO");
        } else if (!telefono.matches("^\\d{9}$")) {
            errores.add("TELEFONO INVALIDO");
        }

        // email, formato @dominio.etc y duplicado
        if (email == null || email.isBlank()) {
            errores.add("EMAIL OBLIGATORIO");
        } else {
            String emailTrim = email.trim();
            // formato email basico con regex
            if (!emailTrim.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                errores.add("EMAIL INVALIDO");
            } else if (data.existeEmail(emailTrim)) {
                errores.add("EMAIL DUPLICADO");
            }
        }

        if (!errores.isEmpty()){
            throw new IllegalArgumentException("FALLO DE VALIDACION, " + String.join(", ", errores));
        }

        Cliente c = new Cliente(
                nombre.trim(),
                apellidos.trim(),
                sexo.toUpperCase().trim(),
                ciudad.trim(),
                fechaNacimiento,
                telefono.trim(),
                email.trim());

        return data.crear(c);
    }

    //LISTAR
    public List<Cliente> listar(){
        return data.listarTodos();
    }

    //Busqueda por ID
    public Optional<Cliente> buscarPorId (Long id){
        return data.buscarPorId(id);
    }

    // update con sus validaciones
    public Cliente actualizar(Long id, String nombre, String apellidos, String sexo, String ciudad, LocalDate fechaNacimiento, String telefono, String email){
        Optional<Cliente> opt = data.buscarPorId(id);
        if (opt.isEmpty()){
            throw new IllegalArgumentException("CLiente no encontrado con id " + id);
        }

        List<String> errores = new ArrayList<>();

        // updates de sus validaciones de campos obligatorios
        if (nombre == null || nombre.isBlank()) errores.add("NOMBRE OBLIGATORIO");
        if (apellidos == null || apellidos.isBlank()) errores.add("APELLIDOS OBLIGATORIO");
        if (sexo == null || !(sexo.equalsIgnoreCase("M") || (sexo.equalsIgnoreCase("F")))) errores.add("EL SEXO DEBE SER M O F");
        if (ciudad == null || ciudad.isBlank()) errores.add("CIUDAD OBLIGATORIO");

        // updates de telefono 9 digitos
        if (telefono == null || telefono.isBlank()) {
            errores.add("TELEFONO OBLIGATORIO");
        } else if (!telefono.matches("^\\d{9}$")) {
            errores.add("TELEFONO INVALIDO");
        }

        Cliente existente = opt.get();

        // update de email, valida formato y solo chequea duplicado si cambia respecto al actual
        if (email == null || email.isBlank()) {
            errores.add("EMAIL OBLIGATORIO");
        } else {
            String emailNuevo = email.trim();
            if (!emailNuevo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                errores.add("EMAIL INVALIDO");
            } else {
                String emailActual = existente.getEmail() == null ? null : existente.getEmail().trim();
                if (emailActual == null || !emailNuevo.equalsIgnoreCase(emailActual)) {
                    if (data.existeEmail(emailNuevo)) {
                        errores.add("EMAIL DUPLICADO");
                    }
                }
            }
        }

        if (!errores.isEmpty()){
            throw new IllegalArgumentException("FALLO DE VALIDACION, " + String.join(", ", errores));
        }

        existente.setNombre(nombre.trim());
        existente.setApellidos(apellidos.trim());
        existente.setSexo(sexo.toUpperCase().trim());
        existente.setCiudad(ciudad.trim());
        existente.setFechaNacimiento(fechaNacimiento);
        existente.setTelefono(telefono.trim());
        existente.setEmail(email.trim());

        return data.actualizar(existente);
    }

    //Eliminar por id
    public void eliminar(Long id){
        Optional<Cliente> opt = data.buscarPorId(id);
        if (opt.isEmpty()){
            throw new IllegalArgumentException("Cliente no encontrado con id " + id);
        }
        data.eliminar(id);
    }

    //Buscar por ciudad
    public List<Cliente> buscarPorCiudad(String ciudad){
        if (ciudad == null || ciudad.isBlank()){
            throw new IllegalArgumentException("CIUDAD OBLIGATORIA ");
        }
        return data.buscarPorCiudad(ciudad.trim());
    }
}