

//ClienteTCP.java
import java.io.*;
import java.net.*; 

public class ClienteTCP {

	public static void main(String[] args) {
		
		/* 	1.- Solicitar nombre de usuario
		 * 
		 * En un thread paralelo de manera periódica se va preguntando por el número de remainders sin leer nuevos
		 * 
		 	2.- Menu con opciones:
		 			A)set remainder
		 			B)ver remainder (numero de remainder nuevos)
					C)salir
			
				A - 
				B -
				C - 
		*/
		
		
		
		
		
		
	/**
		Leemos el primer parámetro, donde debe ir la dirección
		IP del servidor
	*/
		 InetAddress direcc = null;
		 try
		 {
			 direcc = InetAddress.getByName(args[0]);
		 }
		 catch(UnknownHostException uhe)
		 {
			 System.err.println("Host no encontrado : " + uhe);
			 
		 } catch (ArrayIndexOutOfBoundsException ae) {
			 System.err.println("Resulting to Default ip: " + "192.168.56.1");
			 try {
				direcc = InetAddress.getByName("192.168.56.1");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		// Puerto que hemos usado para el servidor
		 int puerto = 1234;
		// Para cada uno de los argumentos...
		 
		 if (args.length<1) {
			 Socket sckt = null;
			 DataInputStream dis = null;
			 DataOutputStream dos = null; 
			 try {
				// Convertimos el texto en número
				 int numero = 2;
				 // Creamos el Socket
				 sckt = new Socket(direcc,puerto);
				 // Extraemos los streams de entrada y salida
				 dis = new
				DataInputStream(sckt.getInputStream());
				 dos = new
				DataOutputStream(sckt.getOutputStream());
				 // Lo escribimos
				 dos.writeInt(numero);
				 
				 
				 // Leemos el resultado final
				 long resultado = dis.readLong();
				 // Indicamos en pantalla
				 System.out.println( "Solicitud = " + numero +"\tResultado = " +resultado );
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
		 } else {
		 
		 
			 for (int n=1;n<args.length;n++)
			 { 
				 Socket sckt = null;
				 DataInputStream dis = null;
				 DataOutputStream dos = null; 
				 try {
					// Convertimos el texto en número
					 int numero = Integer.parseInt(args[n]);
					 // Creamos el Socket
					 sckt = new Socket(direcc,puerto);
					 // Extraemos los streams de entrada y salida
					 dis = new
					DataInputStream(sckt.getInputStream());
					 dos = new
					DataOutputStream(sckt.getOutputStream());
					 // Lo escribimos
					 dos.writeInt(numero);
					 // Leemos el resultado final
					 long resultado = dis.readLong();
					 // Indicamos en pantalla
					 System.out.println( "Solicitud = " + numero +"\tResultado = " +resultado );
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
			 }
		 }
	}

}
