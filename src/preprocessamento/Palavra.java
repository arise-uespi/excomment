package preprocessamento;

public class Palavra {
	String palavra;
	int qtde;
	
	public Palavra(String palavra, int qtde) {
		this.palavra = palavra;
		this.qtde = qtde;
	}
	
	@Override
	public String toString() {
		return palavra + " -> " + qtde;
	}
}
