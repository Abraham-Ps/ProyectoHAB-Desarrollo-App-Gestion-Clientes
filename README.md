# Gestion de Clientes

Este es un proyecto de consola para practicar en Java + JPA con MySQL Workbench.

Se uso IntelliJJ.

Permite:
- Agregar clientes.
- Listar clientes.
- Actualizar clientes.
- Eliminar clientes.
- Buscar clientes por ciudad.

## Requisitos
- Java 21.0.8 (OpenJDK)
- Maven.
- MySQL Workbench.

## Preparacion
1) Abre MySQL Workbench y conectacte a tu servidor local.
2) Crea la base de datos clientes.
  
3) Puedes credenciales en `src/main/resources/META-INF/persistence.xml` (usuario/contraseña/URL):
   
   `<property name="jakarta.persistence.jdbc.user" value="root"/>
   <property name="jakarta.persistence.jdbc.password" value=""/>
   <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/clientes?serverTimezone=UTC"/>
   <property name="hibernate.hbm2ddl.auto" value="update"/>
`

## Como ejecutar
- Desde el IDE: Run en la clase `app.Main`. 

Al inicio te mostrara un mensaje de bienvenida (con colores) y luego el menu.

## Resumen rapido del uso del menu
- Alta: pide Nombre, Apellidos, Sexo (M/F), Ciudad, Fecha (DD-MM-YYYY, opcional y no futura), Telefono (España 9 digitos) y Email.

- Actualizar: puedes dejar un campo vacio para mantener el valor actual.

- Email: si el formato es invalido te vuelve a pedirlo, si ya existe en BD, tambien te lo vuelve a pedir.

## Validaciones principales y comportamiento del menu
- Campos obligatorios: nombre, apellidos, sexo, ciudad, telefono, email.

- Sexo: M o F (te lo vuelve a pedir hasta que sea valido).

- Telefono: 9 digitos de España (puedes escribir con espacios y tambien se guardan sin espacios).

- Fecha: formato `DD-MM-YYYY`, no puede ser futura (ya que si no, te lo vuelve a pedir).

- Email: formato correcto y unico en la base (reintento solo del email si hay problema).

 - Si seleccionas una opcion inválida del menu, te muestra `ERROR: Selecciona una opcion valida` y vuelve a pedir.

 - Mensajes coloreados (en IntelliJ):
   - Errores o avisos: rojo (por ejemplo, campo obligatorio, formato invalido, fecha futura, no hay clientes, no existe cliente con id, email invalido, duplicado).
   - Validos o Checks: verde (por ejemplo, Cliente creado, Cliente actualizado, Saliendo...)

## Notas de los SUPUESTOS
Los "supuestos" son decisiones que tomamos para completar alguna accion cuando el enunciado no lo especifica del todo, basicamente sirve para dejar claro como se piensa hacer.

## SUPUESTOS (marcados y explicados)
- Supuesto 1: Telefono de España con 9 dígitos.
  - Nota 1.1: Elegimos este formato para simplificar y porque el proyecto esta ambientado a como si fuera una app integrada solo en España, se pueden espacios al escribir, pero se guarda el numero sin espacios.
- Supuesto 2: Fecha en formato `DD-MM-YYYY` y no futura.
  - Nota 2.1: El enunciado te pide con el formato obligatorio el cual es `DD-MM-YYYY` y se evitan fechas imposibles (nacidos en el futuro).
- Supuesto 3: Email con formato “normal” y único en BD.
  - Nota 3.1: Usamos una regex como por ejemplo ( `nombre@dominio.com`) que ayuda a comprobar el formato email y ademas, comprobamos duplicados para evitar registros repetidos.
  - Nota 3.2: La clave primaria es autoincrement, si borras clientes, los IDs no se reciclan. Esto es el comportamiento normal de este debido al autoincrement.
  - Nota 3.3: No hay interfaz web, se ejecuta por consola y la configuracion de la BD se hace en `persistence.xml`.
  - Nota: los colores ANSI se ven en la consola de IntelliJ, a lo mejor en otros programas podrian no verse.

## Estructura del proyecto (resumen)

`-src/main/java/app
--Main.java              # punto de entrada (cierra EntityManager en finally)
---menu/Menu.java         # menú por consola y lecturas con reintentos
----servicio/ClienteService.java  # validaciones y reglas
-----data/ClienteData.java  # consultas JPQL y operaciones con JPA
------cliente/Cliente.java   # entidad JPA (email unico, indice por ciudad)
-------util/JpaUtil.java     # gestión de EntityManagerFactory/EntityManager`


## Tip: limpiar datos en desarrollo (opcional)
Si quieres dejar la tabla vacia y reiniciar todos los IDs puedes hacer esto:
--- `<property name="hibernate.hbm2ddl.auto" value="update"/>`  cambia el "update" a "create".
(Esto borra todos los registros, usalo solo para test)

## Notas finales
- Proyecto mas que todo para practicar y mejorar, si algo falla, revisa credenciales/URL en `persistence.xml` y que la BD `clientes` exista.
- Puedes abrir un issue o comentar mejoras si lo ves necesario.

## Casos de prueba manuales
- Telefono
  - Valido: `600112233`, `600 112 233`. 
  - Invalido: `60011223` (8 digitos), `600-112-233`, `abc123456`. 

- Email
  - Valido: `user@mail.com`, `user.name+tag@sub.dominio.es`. 
  - Invalidos: `user@`, `user@mail`, `user mail@x.com`. 
  - Duplicado: intenta crear dos clientes con el mismo email y deberia pedirte otro email.

- Fecha de nacimiento (DD-MM-YYYY)
  - Validas: `01-01-1990` .
  - Invalidas: `05-08-2026` (futura), formato erroneo `05-08-2025` y tampoco puedes `05/08/2025`. 
    
- Menu
  - Opcion invalida: escribe letras o `0` o `9` y te deberia mostrar `ERROR: Selecciona una opcion valida`. 
  - Listar sin datos que no existan: debe mostrar `No hay clientes` en rojo.
  - Buscar ciudad sin datos que no existan: debe mostrar `No hay clientes en esa ciudad` en rojo.

Y ya estaria :)

O... como diria el profe Stefano. Alla prossima raggazo 🤌
(No me baje la nota era broma😉)


Abraham-Ps                                                                                                                      
