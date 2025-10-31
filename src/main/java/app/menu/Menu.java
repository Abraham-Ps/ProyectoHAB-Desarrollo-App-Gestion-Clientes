package app.menu;

import app.servicio.ClienteService;
import app.cliente.Cliente;
import app.util.JpaUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Menu {
    private final Scanner sc;
    private final ClienteService servicio;

    // codigos ANSI para colorear la consola. solo estetica
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";  // reset quita estilos y vuelve al color por defecto

    public Menu(Scanner sc, ClienteService servicio) {
        this.sc = sc;
        this.servicio = servicio;
    }

    public void iniciar() {
        int opcion = -1;

        while (opcion != 6) {
            mostrarMenu();
            opcion = leerEntero("Elige una opcion: ");

            //uso de switch con flechas para evitar breaks
            switch (opcion) {

                case 1 -> agregarCliente();
                case 2 -> listarClientes();
                case 3 -> actualizarCliente();
                case 4 -> eliminarCliente();
                case 5 -> buscarPorCiudad();
                case 6 -> System.out.println(GREEN + "Saliendo..." + RESET);
                default -> System.out.println(RED + "ERROR: Selecciona una opcion valida" + RESET);
            }

        }
        JpaUtil.close();
    }

    //imprime el titulo del menu con colores
    private void mostrarMenu() {
        System.out.println(GREEN + "\n----- Gestion de Clientes "
                + RED + "E" + YELLOW + "S" + RED + "P"
                + GREEN + " -----" + RESET);
        System.out.println("1. Agregar cliente");
        System.out.println("2. Listar cliente");
        System.out.println("3. Actualizar cliente");
        System.out.println("4. Eliminar cliente");
        System.out.println("5. Buscar por ciudad");
        System.out.println("6. Salir");
    }

    //flujo del alta. se leen los campos y se reintenta email si hay duplicado
    private void agregarCliente() {
        String nombre = leerTextoObligatorio("Nombre: ");
        String apellidos = leerTextoObligatorio("Apellidos: ");
        String sexo = leerSexo();
        String ciudad = leerTextoObligatorio("Ciudad: ");
        LocalDate fechaNacimiento = leerFecha("Fecha de nacimiento (DD-MM-YYYY) [opcional]: ");
        String telefono = leerTelefono();
        String email = leerEmail();

        while (true) {
            try {
                Cliente c = servicio.altaCliente(nombre, apellidos, sexo, ciudad, fechaNacimiento, telefono, email);
                System.out.println(GREEN + "Cliente creado con id " + c.getId() + RESET);
                break;
            } catch (IllegalArgumentException ex) {
                String msg = ex.getMessage() == null ? "" : ex.getMessage();
                if (msg.toUpperCase().contains("EMAIL DUPLICADO")) {
                    System.out.println(RED + "ERROR: " + ex.getMessage() + RESET);
                    email = leerEmail();
                    continue;
                }
                System.out.println(RED + "ERROR: " + ex.getMessage() + RESET);
                break;
            }
        }
    }

    //lista clientes o muestra aviso si no hay
    private void listarClientes() {
        List<Cliente> lista = servicio.listar();
        if (lista.isEmpty()) {
            System.out.println(RED + "No hay clientes" + RESET);
        } else {
            lista.forEach(System.out::println);
        }
    }

    //flujo de update, y si lo dejas en blanco mantiene el valor actual
    private void actualizarCliente() {
        Long id = leerLong("Id del cliente a actualizar: ");
        Optional<Cliente> opt = servicio.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println(RED + "No existe cliente con id " + id + RESET);
            return;
        }
        System.out.println("Dejar en blanco para mantener el valor actual.");
        Cliente actual = opt.get();

        String nombre = leerTextoOpcional("Nombre [" + actual.getNombre() + "]: ", actual.getNombre());
        String apellidos = leerTextoOpcional("Apellidos [" + actual.getApellidos() + "]: ", actual.getApellidos());
        String sexo = leerSexoOpcional("Sexo (M/F) [" + actual.getSexo() + "]: ", actual.getSexo());
        String ciudad = leerTextoOpcional("Ciudad [" + actual.getCiudad() + "]: ", actual.getCiudad());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        String fechaActualVis = (actual.getFechaNacimiento() == null) ? "" : actual.getFechaNacimiento().format(fmt);
        LocalDate fechaNacimiento = leerFechaOpcional("Fecha nacimiento (DD-MM-YYYY) [" + fechaActualVis + "]: ", actual.getFechaNacimiento());
        String telefono = leerTelefonoOpcional("Telefono [" + actual.getTelefono() + "]: ", actual.getTelefono());
        String email = leerEmailOpcional("Email [" + actual.getEmail() + "]: ", actual.getEmail());

        while (true) {
            try {
                Cliente actualizado = servicio.actualizar(id, nombre, apellidos, sexo, ciudad, fechaNacimiento, telefono, email);
                System.out.println(GREEN + "Cliente actualizado: " + actualizado + RESET);
                break;
            } catch (IllegalArgumentException ex) {
                String msg = ex.getMessage() == null ? "" : ex.getMessage();
                if (msg.toUpperCase().contains("EMAIL DUPLICADO")) {
                    System.out.println(RED + "ERROR: " + ex.getMessage() + RESET);
                    email = leerEmailOpcional("Email [" + actual.getEmail() + "]: ", actual.getEmail());
                    continue;
                }
                System.out.println(RED + "ERROR: " + ex.getMessage() + RESET);
                break;
            }
        }
    }

    //elimina un cliente por id
    private void eliminarCliente() {
        Long id = leerLong("Id del cliente a eliminar: ");
        try {
            servicio.eliminar(id);
            System.out.println(RED + "Cliente Eliminado" + RESET);
        } catch (IllegalArgumentException ex) {
            System.out.println(RED + "ERROR: " + ex.getMessage() + RESET);
        }
    }

    //busca clientes por ciudad
    private void buscarPorCiudad() {

        {
            String ciudad = leerTextoObligatorio("Ciudad: ");
            try {
                List<Cliente> lista = servicio.buscarPorCiudad(ciudad);
                if (lista.isEmpty()) {
                    System.out.println(RED + "No hay clientes en esa ciudad" + RESET);
                } else {
                    lista.forEach(System.out::println);
                }

            } catch (IllegalArgumentException ex) {
                System.out.println(RED + "ERROR: " + ex.getMessage() + RESET);
            }
        }
    }

    /// ********************** VALIDACIONES Y LECTURAS *************************///

    // lee un entero y reintenta si no es valido
    private int leerEntero(String msg) {
        int valor;
        while (true) {
            System.out.println(msg);
            String linea = sc.nextLine();
            try {
                valor = Integer.parseInt(linea.trim());
                return valor;
            } catch (NumberFormatException e){
                System.out.println(RED + "ERROR: Selecciona una opcion valida" + RESET);
            }

        }
    }

    // lee sexo M o F con reintentos
    private String leerSexo(){
        while (true){
            String s = leerTextoObligatorio("Sexo (M/F): ");
            if (s.equalsIgnoreCase("M") || s.equalsIgnoreCase("F")) {
                return s.toUpperCase();
            }
            System.out.println(RED + "ERROR: debe ser M o F. Intentalo de nuevo." + RESET);
        }
    }

    // lee sexo OPCIONAL y si dejas en blanco mantiene el valor
    private String leerSexoOpcional (String msg, String actual) {
        while (true) {
            System.out.println(msg);
            String s = sc.nextLine();
            if (s == null || s.isBlank()) return actual;
            if (s.equalsIgnoreCase("M") || s.equalsIgnoreCase("F")) {
                return s.toUpperCase();
            }
            System.out.println(RED + "ERROR: debe ser M o F. Intentalo de nuevo." + RESET);
        }
    }

    // uso una ragex que bloquea las letras y solo permite del 0-9, y espacios, y 9 digitos como en el prefijo +34
    private String leerTelefono(){
        while (true) {
            String t = leerTextoObligatorio("Telefono (9 digitos): ");
            // uso una ragex que bloquea las letras y solo permite del 0-9, y espacios, y 9 digitos como en el prefijo +34
            String tSinEspacios = t.replaceAll("\\s+", "");
            if (tSinEspacios.matches("^\\d{9}$")) {
                return tSinEspacios;
            }
            System.out.println(RED + "ERROR: Debe contener exactamente 9 dígitos (España). Intentalo de nuevo." + RESET);
        }
    }
    // lee telefono OPCIONAL y si dejas blanco mantiene el valor
    private String leerTelefonoOpcional (String msg, String actual) {
        while (true) {
            System.out.println(msg);
            String t = sc.nextLine();
            if (t == null || t.isBlank()) return actual;
            String tSinEspacios = t.replaceAll("\\s+", "");
            if (tSinEspacios.matches("^\\d{9}$")) {
                return tSinEspacios;
            }

            System.out.println(RED + "ERROR: Debe contener exactamente 9 dígitos (España). Intentalo de nuevo." + RESET);
        }
    }

    //lee un long y reintenta si no es valido
    private Long leerLong(String msg) {
        Long valor;
        while (true) {
            System.out.println(msg);
            String linea = sc.nextLine();
            try {
                valor = Long.parseLong(linea.trim());
                return valor;
            } catch (NumberFormatException e){
                System.out.println("Introduce un numero valido");
            }

        }
    }

    // lee un texto no vacio. reintenta hasta que tenga valor
    private String leerTextoObligatorio(String msg) {
        String valor;
        while (true) {
            System.out.print(msg);
            valor = sc.nextLine();
            if (valor != null && !valor.isBlank()) {
                return valor.trim();
            }
            System.out.println(RED + "Campo obligatorio" + RESET);
        }
    }

    //lee texto OPCIONAL y si dejas en blanco mantiene el valor actual
    private String leerTextoOpcional(String msg, String actual) {
        String valor;
        System.out.print(msg);
        valor = sc.nextLine();
        if (valor == null || valor.isBlank()) {
            return actual;
        }
        return valor.trim();
    }

    // lee email con regex y reintenta formato
    private String leerEmail() {
        while (true) {
            String e = leerTextoObligatorio("Email: ");
            String email = e.trim();
            if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }
            System.out.println(RED + "ERROR: EMAIL INVALIDO. Intentalo de nuevo." + RESET);
        }
    }

    //lee email OPCIONAL con regex y si lo dejas en blanco mantiene el actual
    private String leerEmailOpcional(String msg, String actual) {
        while (true) {
            System.out.print(msg);
            String e = sc.nextLine();
            if (e == null || e.isBlank()) return actual;
            String email = e.trim();
            if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }
            System.out.println(RED + "ERROR: EMAIL INVALIDO. Intentalo de nuevo." + RESET);
        }
    }


    // lee fecha con formato DD-MM-YYYY, no permite fechas futuras y si no pues te hace reintentar
    private LocalDate leerFecha(String msg) {
        while (true) {
            System.out.print(msg);
            String linea = sc.nextLine();
            if (linea == null || linea.isBlank()) {
                return null;
            }
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                LocalDate fecha = LocalDate.parse(linea.trim(), fmt);
                if (fecha.isAfter(LocalDate.now())) {
                    System.out.println(RED + "La fecha no puede ser futura. Intentalo de nuevo (DD-MM-YYYY)." + RESET);
                    continue;
                }
                return fecha;
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Formato invalido, intentalo de nuevo (DD-MM-YYYY)." + RESET);
            }
        }
    }

    // lee fecha OPCIONAL con formato DD-MM-YYYY no permite fechas futuras
    private LocalDate leerFechaOpcional(String msg, LocalDate actual) {
        while (true) {
            System.out.print(msg);
            String linea = sc.nextLine();
            if (linea == null || linea.isBlank()) {
                return actual;
            }
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                LocalDate fecha = LocalDate.parse(linea.trim(), fmt);
                if (fecha.isAfter(LocalDate.now())) {
                    System.out.println(RED + "La fecha no puede ser futura. Intentalo de nuevo (DD-MM-YYYY)." + RESET);
                    continue;
                }
                return fecha;
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Formato invalido, intentalo de nuevo (DD-MM-YYYY)." + RESET);
            }
        }
    }
}