import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CorretoraImpl extends UnicastRemoteObject implements CorretoraRemote {

    private final Map<String, Double> acoes = new ConcurrentHashMap<>();
    private final Map<String, Queue<Thread>> filaAcoes = new ConcurrentHashMap<>();

    private final Map<Integer, ClienteCallback> clientes = new ConcurrentHashMap<>();
    private final Map<Integer, String> identificacoes = new ConcurrentHashMap<>();

    private int proximoId = 1;

    private CorretoraRemote cloud; // CASCATA

    protected CorretoraImpl(CorretoraRemote cloud) throws RemoteException {
        super();
        this.cloud = cloud;
    }

    // =========================
    // CALLBACK SERVIDOR
    // =========================
    public interface ServidorCallback {
        void onEvento(String msg);
    }

    private ServidorCallback callback;

    public void setServidorCallback(ServidorCallback callback) {
        this.callback = callback;
    }

    // =========================
    // AÇÕES
    // =========================
    @Override
    public synchronized void adicionarAcao(String nome, double preco) throws RemoteException {
        if (acoes.containsKey(nome)) {
            throw new RemoteException("Ação já existe");
        }
        acoes.put(nome, preco);
        System.out.println("Ação adicionada: " + nome + " = R$" + preco);
    }

    @Override
    public synchronized List<String> listarAcoes() throws RemoteException {
        Set<String> todas = new HashSet<>(acoes.keySet());

        if (cloud != null) {
            try {
                todas.addAll(cloud.listarAcoes());
            } catch (Exception e) {
                System.out.println("Cloud indisponível");
            }
        }

        return new ArrayList<>(todas);
    }

    @Override
    public synchronized double consultarPreco(String acao) throws RemoteException {

        if (acoes.containsKey(acao)) {
            return acoes.get(acao);
        }

        if (cloud != null) {
            try {
                double precoCloud = cloud.consultarPreco(acao);

                // 🔥 SE CLOUD RETORNAR 0.0, TRATAR COMO INEXISTENTE
                if (precoCloud != 0.0) {
                    return precoCloud;
                }

            } catch (Exception e) {
                System.out.println("Cloud indisponível");
            }
        }

        // 🔥 AGORA SIM: lança erro
        throw new RemoteException("Ação não existe");
    }

    // =========================
    // ATUALIZAÇÃO COM FILA
    // =========================
    @Override
    public void atualizarPreco(String acao, double preco) throws RemoteException {

        // =========================
        // 🔥 VALIDAÇÃO DA AÇÃO
        // =========================
        if (!acoes.containsKey(acao)) {

            if (cloud != null) {
                try {
                    double precoCloud = cloud.consultarPreco(acao);

                    if (precoCloud != 0.0) {
                        // traz do cloud para o local
                        acoes.put(acao, precoCloud);
                    } else {
                        throw new RemoteException("Ação não existe");
                    }

                } catch (Exception e) {
                    throw new RemoteException("Ação não existe");
                }
            } else {
                throw new RemoteException("Ação não existe");
            }
        }

        // =========================
        // 🔥 CONTROLE DE FILA (CONCORRÊNCIA)
        // =========================
        synchronized (this) {
            filaAcoes.putIfAbsent(acao, new LinkedList<>());
            Queue<Thread> fila = filaAcoes.get(acao);

            Thread atual = Thread.currentThread();
            fila.add(atual);

            while (fila.peek() != atual) {
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
        }

        // =========================
        // 🔥 EXECUÇÃO DA ATUALIZAÇÃO
        // =========================
        try {
            Thread.sleep(1000); // simula processamento

            synchronized (this) {
                acoes.put(acao, preco);

                // 🔥 CASCATA
                if (cloud != null) {
                    try {
                        cloud.atualizarPreco(acao, preco);
                    } catch (Exception e) {
                        System.out.println("Erro ao atualizar no cloud");
                    }
                }

                // 🔥 CALLBACK
                notificarClientes(acao, preco);

                if (callback != null) {
                    callback.onEvento("Atualizado: " + acao + " = R$" + preco);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            // =========================
            // 🔥 LIBERA FILA
            // =========================
            synchronized (this) {
                filaAcoes.get(acao).poll();
                notifyAll();
            }
        }
    }

    // =========================
    // CLIENTES
    // =========================
    @Override
    public synchronized int registrarCliente(ClienteCallback cliente) throws RemoteException {

        String ident;
        try {
            ident = cliente.getIdentificacao();
        } catch (Exception e) {
            ident = "Desconhecido";
        }

        int id = proximoId++;

        clientes.put(id, cliente);
        identificacoes.put(id, ident);

        if (callback != null) {
            callback.onEvento("Cliente conectado: ID " + id + " - " + ident);
        }

        return id;
    }

    @Override
    public synchronized List<String> listarClientesOnline() throws RemoteException {
        List<String> lista = new ArrayList<>();

        for (Integer id : clientes.keySet()) {
            lista.add("ID: " + id + " | " + identificacoes.get(id));
        }

        return lista;
    }

    private void notificarClientes(String acao, double preco) {
        Iterator<Map.Entry<Integer, ClienteCallback>> it = clientes.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, ClienteCallback> entry = it.next();

            try {
                entry.getValue().notificarAtualizacao(acao, preco);
            } catch (Exception e) {
                it.remove();
                identificacoes.remove(entry.getKey());
            }
        }
    }
}