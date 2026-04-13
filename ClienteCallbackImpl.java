import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClienteCallbackImpl extends UnicastRemoteObject implements ClienteCallback {

    private String identificacao;

    protected ClienteCallbackImpl() throws RemoteException {
        super();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            identificacao = addr.getHostName() + " (" + addr.getHostAddress() + ")";
        } catch (Exception e) {
            identificacao = "Máquina desconhecida";
        }
    }

    @Override
    public void notificarAtualizacao(String acao, double preco) throws RemoteException {
        System.out.println("Atualização: " + acao + " = R$" + preco);
    }

    @Override
    public String getIdentificacao() throws RemoteException {
        return identificacao;
    }
}