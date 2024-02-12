import java.io.*;
import java.net.*; 
import java.util.*;

public class ClienteTCP {

	public static Scanner sc;
	public static int sinLeer;
	public static String nombreUsuario;
	public static boolean funcionando;
	public static boolean login;
	
	public static void main(String[] args) {
		
		logo();
		
		comprobarServidor();
		
		nombreUsuario = null;
		sc = new Scanner(System.in);
		
		funcionando = true;
		login = false;
		
		while (login == false) {
			System.out.println("Introduce tu nombre de usuario:");
			nombreUsuario = sc.nextLine();

			iniciarSesion();
		}
		
		//Creamos una tarea que cada X tiempo muestra el número de mensajes no leídos utilizando un hilo secundario
		TimerTask mostrarNoLeidos = new UnreadReminders();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(mostrarNoLeidos, 0, 5000);
		
		while (funcionando) {
			menu();
		}
		 
	}
	
	public static void logo() {
		System.out.println("-------------------");
		System.out.println("| Ey! Remember... |");
		System.out.println("-------------------\n");
	}
	
	/**
	 * Solicita un nombre de usuario y envía esta información al servidor.
	 * Si el servidor detecta que ya hay una sesión iniciada con ese usuario, 
	 * devuelve unn código de fallo.
	 */
	public static void iniciarSesion() {
		System.out.println("Iniciando sesion como usuario: " + nombreUsuario);
		String resultadoInicioSesion = send("Login="+nombreUsuario);
		
		switch (resultadoInicioSesion) {
		case "EXITO":
			System.out.println("Login correcto.");
			getRecordatorios();
			login = true;
			break;
		case "FALLO":
			System.out.println("Login incorrecto, usuario ya conectado.");
			break;
		}
		
	}
	
	/**
	 * Recuperamos el número de recordatorios sin leer del usuario
	 */
	public static void getRecordatoriosNum() {
		int nuevo = Integer.parseInt(send("GetN="+nombreUsuario));
		if (nuevo > 0) {
			// Si hay algún recordatorio sin leer, periódicamente informa al usuario
			System.out.printf("Tienes %d recordatorios sin leer.\n", nuevo);
		}
		if (nuevo != sinLeer) {
			sinLeer = nuevo;
		}
	}
	
	/**
	 * Recuperamos el mensaje de todos los recordatorios 
	 */
	public static void getRecordatorios() {
		String rawRecordatorios = send("Get="+nombreUsuario);
		for(String recordatorio : rawRecordatorios.split(";")) {
			System.out.println("Mensaje: " + recordatorio);
		}
	}
	
	/**
	 * Envía una cadena de texto al servidor y devuelve la respuesta
	 * @param mensaje
	 * @return
	 */
	public static String send(String mensaje) {
		
		//Aqui leeremos la respuesta del servidor
		byte[] pck = null;
		String respuesta = null;
		
		InetAddress direcc = null;

		try {
			
			 // WINDOWS: 
			  direcc = InetAddress.getByName("192.168.56.1");
			 // LINUX:
			 //direcc = InetAddress.getByName("127.0.1.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		int puerto = 1234;
		
		 Socket sckt = null;
		 DataInputStream dis = null;
		 DataOutputStream dos = null; 
		 try {
			 sckt = new Socket(direcc,puerto);
			 
			 dis = new DataInputStream(sckt.getInputStream());
			 dos = new DataOutputStream(sckt.getOutputStream());
			 
			 dos.write(mensaje.getBytes());
			 
			 pck = dis.readAllBytes();
			 respuesta = new String(pck);

			 dis.close();
			 dos.close(); 
		 } catch (ConnectException ce) {
			 //Si el servidor se cierra en algún momento, lo gestionamos aquí.
			 System.out.println("El servidor no esta operativo en este momento.");
			 System.exit(0);
		 }
		 catch(Exception e)
		 {
			 System.err.println("Se ha producido la excepción : " +e);
		 }
		 
		 try
		 {
			 if (sckt!=null) sckt.close();
		 }
		 catch(IOException ioe)
		 {
			 System.err.println("Error al cerrar el socket :" + ioe);
		 } 
		 
		return respuesta;
	 }
	
	/**
	 * Creamos un recordatorio nuevo
	 */
	public static void setRecordatorio() {
		System.out.println("Introduce el mensaje del recordatorio:");
		String mensaje = sc.nextLine();
		//Evitamos mensajes vacíos
		if(mensaje.length()<1) {
			System.out.println("El mensaje introducido no es válido.");
			return;
		}
		System.out.println("Introduce la cantidad de tiempo hasta el recordatorio en segundos:");
		String rawSegundos = sc.nextLine();
		if (rawSegundos.length()<1) {
			rawSegundos = "0";
		}
		String input = String.format("Set=%s;%s;%s", nombreUsuario,rawSegundos,mensaje);
		String respuesta = send(input);
		System.out.println(respuesta);
	}
	
	/**
	 * Permite al servidor actualizar su lista de usuarios conectados.
	 */
	public static void cerrarSesion() {
		System.out.println("Cerrando sesión.");
		String mensaje = "Exit="+nombreUsuario;
		System.out.println(mensaje);
		String respuesta = send("Exit="+nombreUsuario);
		System.out.println(respuesta);
	}
	
	/**
	 * Este método imprimer el menu, solo lleva lo visual
	 * Esto permite invocarlo desde el hilo secundario del timer sin afectar al scanner
	 */
	public static void printMenu() {
		System.out.println("Tienes "+sinLeer+" recordatorios no leídos");
		
		String[] opciones = {"1. Crear recordatorio", "2. Ver recordatorios sin leer", "0. Salir","","Recuerda: Al ver los recordatorios, estos serán eliminados."};
		
		System.out.println("");
		for (String opcion : opciones) {
			System.out.println(opcion);
		}
	}
	
	
	public static void menu() {
		printMenu();
		try {
		String input = sc.nextLine(); 
		int seleccion = Integer.parseInt(""+input.charAt(0));
		
		switch (seleccion) {
		case 1:
			setRecordatorio();
			break;
		case 2:
			getRecordatorios();
			break;
		case 0:
			cerrarSesion();
			funcionando = false;
			break;
		}
		
		} catch (Exception e) {
			System.out.println("Opción no reconocida.");
		}
	}
	
	/**
	 * Comprueba si el servidor está levantado mandando una petición simple.
	 */
	public static void comprobarServidor() {
		send("Up");
	}
	
}
