import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.List;

public class Servidor {

    private static void menu() {
        System.out.println("\n1 - Listar ações");
        System.out.println("2 - Consultar preço");
        System.out.println("3 - Atualizar preço");
        System.out.println("4 - Adicionar ação");
        System.out.println("5 - Listar clientes online");
        System.out.print("Escolha: ");
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "192.168.18.56");

            // CONEXÃO COM CLOUD
            CorretoraRemote cloud = null;

            try {
                Registry registryCloud = LocateRegistry.getRegistry("56.125.188.182", 1099);
                cloud = (CorretoraRemote) registryCloud.lookup("CorretoraService");
                System.out.println("Conectado ao CLOUD");
            } catch (Exception e) {
                System.out.println("Cloud indisponível, rodando local");
            }

            CorretoraImpl obj = new CorretoraImpl(cloud);

            obj.adicionarAcao("BTC", 300000.0);
            obj.adicionarAcao("ETH", 15000.0);
            obj.adicionarAcao("SOL", 500.0);

            obj.setServidorCallback(msg -> {
                System.out.println("\n" + msg);
                menu();
            });

            Registry registry = LocateRegistry.createRegistry(8080);
            registry.rebind("CorretoraService", obj);

            System.out.println("Servidor pronto!");

            Scanner sc = new Scanner(System.in);

            while (true) {
                menu();

                int op;

                if (sc.hasNextInt()) {
                    op = sc.nextInt();
                    sc.nextLine(); // limpa buffer
                } else {
                    System.out.println("Opção inválida.");
                    sc.nextLine(); // limpa entrada errada
                    continue;
                }

                switch (op) {
                    case 1:
                        System.out.println(obj.listarAcoes());
                        break;

                    case 2:
                        System.out.print("Ação (Ex.: BTC): ");
                        String acaoBusca = sc.nextLine();

                        try {
                            double preco = obj.consultarPreco(acaoBusca);
                            System.out.println("Preço: R$" + preco);
                        } catch (Exception e) {
                            System.out.println("Ação inexistente, tente novamente.");
                        }
                        break;

                        case 3: {
                            try {
                                System.out.print("Ação: ");
                                String acao = sc.nextLine();

                                System.out.print("Preço: ");

                                if (!sc.hasNextDouble()) {
                                    System.out.println("Preço inválido.");
                                    sc.nextLine();
                                    break;
                                }

                                double preco = sc.nextDouble();
                                sc.nextLine();

                                obj.atualizarPreco(acao, preco);

                            } catch (Exception e) {
                                System.out.println("Erro ao atualizar.");
                            }
                            break;
                        }
                    case 4:
                        System.out.print("Nome: ");
                        String nome = sc.nextLine();
                        System.out.print("Preço: ");
                        double preco;

                        if (sc.hasNextDouble()) {
                            preco = sc.nextDouble();
                            sc.nextLine();
                        } else {
                            System.out.println("Preço inválido.");
                            sc.nextLine();
                            break;
                        }
                        obj.adicionarAcao(nome, preco);
                        break;

                    case 5:
                        try {
                            List<String> clientes = obj.listarClientesOnline();

                            if (clientes.isEmpty()) {
                                System.out.println("Nenhum cliente conectado.");
                            } else {
                                System.out.println("\nClientes conectados:");
                                for (String c : clientes) {
                                    System.out.println(c);
                                }
                            }

                        } catch (Exception e) {
                            System.out.println("Erro ao listar clientes.");
                        }
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}