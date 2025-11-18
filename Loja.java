import java.util.*;

class Produto {
    private String nome;
    private double preco;
    private int estoque;

    public Produto(String nome, double preco, int estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }

    public void vender(int quantidade) {
        if (quantidade > estoque) throw new IllegalArgumentException("Estoque insuficiente.");
        estoque -= quantidade;
    }

    public String toString() {
        return nome + " - R$ " + preco + " (" + estoque + " em estoque)";
    }
}

class Caixa {
    private Map<Integer, Integer> notas = new TreeMap<>(Collections.reverseOrder()); 
    private int[] tiposNotas = {50, 20, 10};

    public Caixa() {
        notas.put(50, 5);
        notas.put(20, 5);
        notas.put(10, 5);
    }

    public double getSaldo() {
        double total = 0;
        for (int valor : notas.keySet()) {
            total += valor * notas.get(valor);
        }
        return total;
    }

    public void adicionarNotas(Map<Integer, Integer> recebidas) {
        for (int nota : recebidas.keySet()) {
            notas.put(nota, notas.getOrDefault(nota, 0) + recebidas.get(nota));
        }
    }

    public boolean darTroco(double troco) {
        Map<Integer, Integer> usado = new HashMap<>();
        double restante = troco;

        for (int nota : notas.keySet()) {
            int qtdDisponivel = notas.get(nota);
            int qtdNecessaria = (int) (restante / nota);

            int usar = Math.min(qtdNecessaria, qtdDisponivel);
            if (usar > 0) {
                usado.put(nota, usar);
                restante -= usar * nota;
            }
        }

        if (restante > 0.001) { 
            return false;
        } else {
            for (int nota : usado.keySet()) {
                notas.put(nota, notas.get(nota) - usado.get(nota));
            }
            return true;
        }
    }

    public void exibirCaixa() {
        System.out.println("\n💰 Saldo total: R$ " + getSaldo());
        for (int nota : notas.keySet()) {
            System.out.println("Notas de R$" + nota + ": " + notas.get(nota));
        }
    }
}

public class Loja {
    private static Scanner sc = new Scanner(System.in);
    private static List<Produto> produtos = new ArrayList<>();
    private static Caixa caixa = new Caixa();

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n===== MENU LOJA =====");
            System.out.println("1 - Cadastrar produto");
            System.out.println("2 - Listar produtos");
            System.out.println("3 - Realizar venda");
            System.out.println("4 - Exibir caixa");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
    case 1:
        cadastrarProduto();
        break;
    case 2:
        listarProdutos();
        break;
    case 3:
        realizarVenda();
        break;
    case 4:
        caixa.exibirCaixa();
        break;
    case 0:
        System.out.println("Encerrando o sistema...");
        break;
    default:
        System.out.println("Opção inválida!");
        break;

            }
        } while (opcao != 0);
    }

    private static void cadastrarProduto() {
        System.out.print("Nome do produto: ");
        String nome = sc.nextLine().trim();

        if (nome.isEmpty()) {
            System.out.println("Nome inválido.");
            return;
        }

        for (Produto p : produtos) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                System.out.println("Produto já cadastrado.");
                return;
            }
        }

        System.out.print("Preço: ");
        double preco = sc.nextDouble();
        System.out.print("Estoque inicial: ");
        int estoque = sc.nextInt();

        if (preco <= 0 || estoque < 0) {
            System.out.println("Dados inválidos.");
            return;
        }

        produtos.add(new Produto(nome, preco, estoque));
        System.out.println("Produto cadastrado com sucesso!");
    }

    private static void listarProdutos() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("\n🧾 Lista de produtos:");
        for (Produto p : produtos) {
            System.out.println("- " + p);
        }
    }

    private static void realizarVenda() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto disponível.");
            return;
        }

        listarProdutos();
        System.out.print("Digite o nome do produto: ");
        String nome = sc.nextLine();

        Produto produto = null;
        for (Produto p : produtos) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                produto = p;
                break;
            }
        }

        if (produto == null) {
            System.out.println("Produto não encontrado.");
            return;
        }

        System.out.print("Quantidade: ");
        int qtd = sc.nextInt();
        if (qtd <= 0 || qtd > produto.getEstoque()) {
            System.out.println("Quantidade inválida ou estoque insuficiente.");
            return;
        }

        double total = produto.getPreco() * qtd;
        System.out.println("Total da compra: R$ " + total);
        System.out.print("Valor pago pelo cliente: ");
        double pago = sc.nextDouble();

        if (pago < total) {
            System.out.println("Valor insuficiente para pagamento.");
            return;
        }

        double troco = pago - total;
        System.out.println("Troco necessário: R$ " + troco);

        if (troco > 0) {
            boolean conseguiu = caixa.darTroco(troco);
            if (!conseguiu) {
                System.out.println("Caixa não tem notas suficientes para o troco.");
                return;
            }
        }

        produto.vender(qtd);
        System.out.println("Venda concluída!");
    }
}
