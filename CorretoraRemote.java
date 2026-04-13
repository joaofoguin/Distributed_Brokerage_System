import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CorretoraRemote extends Remote {

    double consultarPreco(String acao) throws RemoteException;

    List<String> listarAcoes() throws RemoteException;

    void atualizarPreco(String acao, double preco) throws RemoteException;

    int registrarCliente(ClienteCallback cliente) throws RemoteException;

    List<String> listarClientesOnline() throws RemoteException;

    void adicionarAcao(String nome, double preco) throws RemoteException;
}