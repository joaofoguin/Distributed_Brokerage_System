import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteCallback extends Remote {

    void notificarAtualizacao(String acao, double preco) throws RemoteException;

    String getIdentificacao() throws RemoteException;
}