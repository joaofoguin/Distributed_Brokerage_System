import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        try {
            CorretoraRemote stub = null;

            while (stub == null) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 8080);
                    stub = (CorretoraRemote) registry.lookup("CorretoraService");
                    System.out.println("Conectado ao servidor!");
                } catch (Exception e) {
                    System.out.println("Servidor offline... tentando reconectar");
                    Thread.sleep(3000);
                }
            }

            // Cria callback
            ClienteCallbackImpl callback = new ClienteCallbackImpl();
            stub.registrarCliente(callback);

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n1 - Listar ações");
                System.out.println("2 - Consultar preço");
                System.out.println("3 - Atualizar preço");

                int op;

                if (sc.hasNextInt()) {
                    op = sc.nextInt();
                } else {
                    System.out.println("Opção inválida, digite um número.");
                    sc.next();
                    continue;
                }

                switch (op) {
                    case 1:
                        System.out.println(stub.listarAcoes());
                        break;

                    case 2: {
                        System.out.print("Ação: ");
                        String acao = sc.next();

                        try {
                            double preco = stub.consultarPreco(acao);
                            System.out.println("Preço: " + preco);
                        } catch (Exception e) {
                            System.out.println("Ação inexistente.");
                        }
                        break;
                    }

                    case 3: {
                        try {
                            System.out.print("Ação: ");
                            String acao = sc.next();

                            System.out.print("Novo preço: ");
                            double preco = sc.nextDouble();

                            stub.atualizarPreco(acao, preco);

                        } catch (Exception e) {
                            System.out.println("Ação não existe.");
                        }
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}