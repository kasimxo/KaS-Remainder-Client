

//ClienteTCP.java
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
		
		
		nombreUsuario = null;
		sc = new Scanner(System.in);
		
		funcionando = true;
		login = false;
		
		while (login == false) {
			System.out.println("Introduce tu nombre de usuario:");
			nombreUsuario = sc.nextLine();

			iniciarSesion();
		}
		
		
		
		
		
		//Creamos una tarea que cada X tiempo lista a los usuarios conectados 
		TimerTask listarUsuarios = new UnreadReminders();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(listarUsuarios, 0, 5000);
		
		while (funcionando) {
			menu();
		}
		
		
		/* 	1.- Solicitar nombre de usuario
		 * 
		 * En un thread paralelo de manera periódica se va preguntando por el número de remainders sin leer nuevos
		 * 
		 	2.- Menu con opciones:
		 			A)set remainder
		 			B)ver remainder (numero de remainder nuevos)
					C)salir
			
				A - Se le piden ciertos datos al cliente: 
						El mensaje (cadena de texto)
						El tiempo hasta que se envíe
						[Si es periódico o solo una vez] Opcional.
				B - Se muestra una lista de los remainder. 
						Los antiguos se almacenan de manera local y se muestran de otra manera
						Los que están sin leer se han recibido y se muestran con colores
				C - 
				
El servidor tiene una lista de usuarios conectados
Cuando un usuario establece la primera conexión, 
comprueba si está en esa lista y si es así, envía un mensaje de error
Cuando el cliente cierra, manda un mensaje de finde conexion
Si el servidor no escucha nada del cliente en un minuto, 
(job periódico) da la sesión por terminada (y guarda los datos de ese usuario en un archivo?)
El cliente le pide el nombre de usuario y pregunta al servidor, 
hasta que no recibe una confirmación no entra
El cliente tiene un hilo secundario que va a estar haciendo peticiones periódicas al servidor para mostrar los recordatorios sin leer

		*/
		
		

		 
	}
	
	public static void logo() {
		System.out.println("-------------------");
		System.out.println("| Ey! Remember... |");
		System.out.println("-------------------\n");
	}
	
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
		 
		 
		// Puerto que hemos usado para el servidor
		 int puerto = 1234;
		// Para cada uno de los argumentos...
		
			 Socket sckt = null;
			 DataInputStream dis = null;
			 DataOutputStream dos = null; 
			 try {
				
				 // Creamos el Socket
				 sckt = new Socket(direcc,puerto);
				 // Extraemos los streams de entrada y salida
				 dis = new DataInputStream(sckt.getInputStream());
				 dos = new DataOutputStream(sckt.getOutputStream());
				 
				 
				 dos.write(mensaje.getBytes());
				 
				 pck = dis.readAllBytes();
				 respuesta = new String(pck);
				 
				 // y cerramos los streams y el socket
				 dis.close();
				 dos.close(); 
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
	
	public static void setRecordatorio() {
		System.out.println("Introduce el mensaje del recordatorio:");
		String mensaje = sc.nextLine();
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
		
		//getRecordatoriosNum();
		
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
	

}
