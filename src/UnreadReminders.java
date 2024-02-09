import java.util.TimerTask;

/**
 * Recupera de manera periódica el número de recordatorios sin leer del servidor
 * @author andres
 *
 */
public class UnreadReminders extends TimerTask{

	@Override
	public void run() {
		ClienteTCP.getRecordatoriosNum();
		//ClienteTCP.printMenu();
	}
}
